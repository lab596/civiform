package services.program;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import controllers.BadRequestException;
import forms.BlockForm;
import io.ebean.DB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import models.Account;
import models.DisplayMode;
import models.Program;
import models.Question;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import repository.ResetPostgres;
import services.CiviFormError;
import services.ErrorAnd;
import services.LocalizedStrings;
import services.applicant.question.Scalar;
import services.program.predicate.AndNode;
import services.program.predicate.LeafOperationExpressionNode;
import services.program.predicate.Operator;
import services.program.predicate.OrNode;
import services.program.predicate.PredicateAction;
import services.program.predicate.PredicateDefinition;
import services.program.predicate.PredicateExpressionNode;
import services.program.predicate.PredicateValue;
import services.question.exceptions.QuestionNotFoundException;
import services.question.types.AddressQuestionDefinition;
import services.question.types.NameQuestionDefinition;
import services.question.types.QuestionDefinition;
import services.question.types.TextQuestionDefinition;
import support.ProgramBuilder;

@RunWith(JUnitParamsRunner.class)
public class ProgramServiceImplTest extends ResetPostgres {

  private QuestionDefinition addressQuestion;
  private QuestionDefinition secondaryAddressQuestion;
  private QuestionDefinition colorQuestion;
  private QuestionDefinition nameQuestion;
  private ProgramServiceImpl ps;

  @Before
  public void setProgramServiceImpl() {
    ps = instanceOf(ProgramServiceImpl.class);
  }

  @Before
  public void setUp() {
    addressQuestion = testQuestionBank.applicantAddress().getQuestionDefinition();
    secondaryAddressQuestion = testQuestionBank.applicantSecondaryAddress().getQuestionDefinition();
    colorQuestion = testQuestionBank.applicantFavoriteColor().getQuestionDefinition();
    nameQuestion = testQuestionBank.applicantName().getQuestionDefinition();
  }

  @Test
  public void syncQuestions_constructsAllQuestionDefinitions() {
    QuestionDefinition questionOne = nameQuestion;
    QuestionDefinition questionTwo = addressQuestion;
    QuestionDefinition questionThree = colorQuestion;

    ProgramBuilder.newDraftProgram("program1")
        .withBlock()
        .withRequiredQuestionDefinition(questionOne)
        .withRequiredQuestionDefinition(questionTwo)
        .withBlock()
        .withRequiredQuestionDefinition(questionThree)
        .buildDefinition();
    ProgramBuilder.newDraftProgram("program2")
        .withBlock()
        .withRequiredQuestionDefinition(questionTwo)
        .withBlock()
        .withRequiredQuestionDefinition(questionOne)
        .buildDefinition();

    ImmutableList<ProgramDefinition> programDefinitions =
        ps.getActiveAndDraftPrograms().getDraftPrograms();

    QuestionDefinition found = programDefinitions.get(0).getQuestionDefinition(0, 0);
    assertThat(found).isInstanceOf(NameQuestionDefinition.class);
    found = programDefinitions.get(0).getQuestionDefinition(0, 1);
    assertThat(found).isInstanceOf(AddressQuestionDefinition.class);
    found = programDefinitions.get(0).getQuestionDefinition(1, 0);
    assertThat(found).isInstanceOf(TextQuestionDefinition.class);
    found = programDefinitions.get(1).getQuestionDefinition(0, 0);
    assertThat(found).isInstanceOf(AddressQuestionDefinition.class);
    found = programDefinitions.get(1).getQuestionDefinition(1, 0);
    assertThat(found).isInstanceOf(NameQuestionDefinition.class);
  }

  @Test
  public void getActiveAndDraftProgramsWithoutQuestionLoad_hasBasicProgramInfo() {
    QuestionDefinition questionOne = nameQuestion;
    QuestionDefinition questionTwo = addressQuestion;
    QuestionDefinition questionThree = colorQuestion;

    ProgramBuilder.newDraftProgram("program1")
        .withBlock()
        .withRequiredQuestionDefinition(questionOne)
        .withRequiredQuestionDefinition(questionTwo)
        .withBlock()
        .withRequiredQuestionDefinition(questionThree)
        .buildDefinition();
    ProgramBuilder.newActiveProgram("program2")
        .withBlock()
        .withRequiredQuestionDefinition(questionTwo)
        .withBlock()
        .withRequiredQuestionDefinition(questionOne)
        .buildDefinition();

    ImmutableList<ProgramDefinition> draftPrograms =
        ps.getActiveAndDraftProgramsWithoutQuestionLoad().getDraftPrograms();
    ImmutableList<ProgramDefinition> activePrograms =
        ps.getActiveAndDraftProgramsWithoutQuestionLoad().getActivePrograms();

    ProgramDefinition draftProgramDef = draftPrograms.get(0);
    assertThat(draftProgramDef.getBlockCount()).isEqualTo(2);
    assertThat(draftProgramDef.getQuestionCount()).isEqualTo(3);
    ProgramDefinition activeProgramDef = activePrograms.get(0);
    assertThat(activeProgramDef.getBlockCount()).isEqualTo(2);
    assertThat(activeProgramDef.getQuestionCount()).isEqualTo(2);
  }

  @Test
  public void createProgram_setsId() {
    assertThat(ps.getActiveAndDraftPrograms().getActivePrograms()).isEmpty();
    assertThat(ps.getActiveAndDraftPrograms().getDraftPrograms()).isEmpty();

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "test-program",
            "description",
            "name",
            "description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.getResult().id()).isNotNull();
  }

  @Test
  public void createProgram_hasEmptyBlock() {
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "test-program",
            "description",
            "name",
            "description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.getResult().blockDefinitions()).hasSize(1);
    BlockDefinition firstBlock = result.getResult().getBlockDefinitionByIndex(0).get();
    assertThat(firstBlock.id()).isEqualTo(1L);
    assertThat(firstBlock.name()).isEqualTo("Screen 1");
    assertThat(firstBlock.description()).isEqualTo("Screen 1 description");
  }

  @Test
  public void createProgram_returnsErrors() {
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "",
            "",
            "",
            "",
            "",
            "",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            false,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isFalse();
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors())
        .containsExactlyInAnyOrder(
            CiviFormError.of("A public display name for the program is required"),
            CiviFormError.of("A public description for the program is required"),
            CiviFormError.of("A program URL is required"),
            CiviFormError.of("A program note is required"));
  }

  @Test
  public void createProgramWithoutDisplayMode_returnsError() {
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "test-program",
            "description",
            "name",
            "description",
            "confirm",
            "https://usa.gov",
            "",
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ false,
            /* tiGroup */ ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isFalse();
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors())
        .containsExactly(CiviFormError.of("A program visibility option must be selected"));
  }

  @Test
  public void createProgram_protectsAgainstProgramNameCollisions() {
    ps.createProgramDefinition(
        "name",
        "description",
        "display name",
        "display description",
        "",
        "https://usa.gov",
        DisplayMode.PUBLIC.getValue(),
        ProgramType.DEFAULT,
        /* isIntakeFormFeatureEnabled= */ false,
        ImmutableList.copyOf(new ArrayList<>()));

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "name",
            "description",
            "display name",
            "display description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isFalse();
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors())
        .containsExactly(CiviFormError.of("A program URL of name already exists"));
  }

  @Test
  @Parameters({"name with spaces", "DiFfErEnT-cAsEs", "special-characters-$#@"})
  public void createProgram_requiresSlug(String adminName) {
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            adminName,
            "description",
            "display name",
            "display description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isFalse();
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors())
        .containsExactly(
            CiviFormError.of(
                "A program URL may only contain lowercase letters, numbers, and dashes"));
  }

  @Test
  public void createProgram_protectsAgainstProgramSlugCollisions() {
    // Two programs with names that are different but slugify to same value.
    // To simulate this state, we first create a program with a slugified name, then manually
    // update the Program entity in order to add a name value that slugifies to the same value.
    ProgramDefinition originalProgramDefinition =
        ps.createProgramDefinition(
                "name-one",
                "description",
                "display name",
                "display description",
                "",
                "https://usa.gov",
                DisplayMode.PUBLIC.getValue(),
                ProgramType.DEFAULT,
                /* isIntakeFormFeatureEnabled= */ false,
                ImmutableList.copyOf(new ArrayList<>()))
            .getResult();
    // Program name here is missing the extra space
    // so that the names are different but the resulting
    // slug is the same.
    Program updatedProgram =
        originalProgramDefinition.toBuilder().setAdminName("name    one").build().toProgram();
    updatedProgram.update();
    assertThat(updatedProgram.getProgramDefinition().adminName()).isEqualTo("name    one");

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "name-one",
            "description",
            "display name",
            "display description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));
    assertThat(result.hasResult()).isFalse();
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors())
        .containsExactly(CiviFormError.of("A program URL of name-one already exists"));
  }

  @Test
  public void createProgram_intakeFormDisabled() {
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "name-one",
            "description",
            "display name",
            "display description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.COMMON_INTAKE_FORM,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));
    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().programType()).isEqualTo(ProgramType.DEFAULT);
  }

  @Test
  public void createProgram_allowsSettingDefaultProgram() {
    ps.createProgramDefinition(
        "name-one",
        "description",
        "display name",
        "display description",
        "",
        "https://usa.gov",
        DisplayMode.PUBLIC.getValue(),
        ProgramType.COMMON_INTAKE_FORM,
        /* isIntakeFormFeatureEnabled= */ true,
        ImmutableList.copyOf(new ArrayList<>()));
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "name-two",
            "description",
            "display name",
            "display description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().programType()).isEqualTo(ProgramType.DEFAULT);
  }

  @Test
  public void createProgram_clearsExistingCommonIntakeForm() {
    ps.createProgramDefinition(
        "name-one",
        "description",
        "display name",
        "display description",
        "",
        "https://usa.gov",
        DisplayMode.PUBLIC.getValue(),
        ProgramType.COMMON_INTAKE_FORM,
        /* isIntakeFormFeatureEnabled= */ true,
        ImmutableList.copyOf(new ArrayList<>()));

    Optional<ProgramDefinition> commonIntakeForm = ps.getCommonIntakeForm();
    assertThat(commonIntakeForm).isPresent();
    assertThat(commonIntakeForm.get().adminName()).isEqualTo("name-one");

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.createProgramDefinition(
            "name-two",
            "description",
            "display name",
            "display description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.COMMON_INTAKE_FORM,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));
    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().programType()).isEqualTo(ProgramType.COMMON_INTAKE_FORM);

    commonIntakeForm = ps.getCommonIntakeForm();
    assertThat(commonIntakeForm).isPresent();
    assertThat(commonIntakeForm.get().adminName()).isEqualTo("name-two");
    Optional<ProgramDefinition> oldCommonIntake =
        ps.getActiveAndDraftPrograms().getDraftProgramDefinition("name-one");
    assertThat(oldCommonIntake).isPresent();
    assertThat(oldCommonIntake.get().programType()).isEqualTo(ProgramType.DEFAULT);
  }

  @Test
  public void validateProgramDataForCreate_returnsErrors() {
    ImmutableSet<CiviFormError> result =
        ps.validateProgramDataForCreate(
            "",
            "",
            "",
            "",
            "",
            DisplayMode.PUBLIC.getValue(),
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result)
        .containsExactlyInAnyOrder(
            CiviFormError.of("A public display name for the program is required"),
            CiviFormError.of("A public description for the program is required"),
            CiviFormError.of("A program URL is required"),
            CiviFormError.of("A program note is required"));
  }

  @Test
  @Parameters({"name with spaces", "DiFfErEnT-cAsEs", "special-characters-$#@"})
  public void validateProgramDataForCreate_requiresSlug(String adminName) {
    ImmutableSet<CiviFormError> result =
        ps.validateProgramDataForCreate(
            adminName,
            "description",
            "display name",
            "display desc",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result)
        .containsExactly(
            CiviFormError.of(
                "A program URL may only contain lowercase letters, numbers, and dashes"));
  }

  @Test
  public void validateProgramDataForCreate_requiresTIListInSelectTiMode() {
    ImmutableSet<CiviFormError> result =
        ps.validateProgramDataForCreate(
            "program",
            "description",
            "display name",
            "display desc",
            "https://usa.gov",
            DisplayMode.SELECT_TI.getValue(),
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result)
        .containsExactly(
            CiviFormError.of("One or more TI Org must be selected for program visibility"));
  }

  @Test
  public void validateProgramDataForUpdate_requiresTIListInSELECT_TIMode() {
    ImmutableSet<CiviFormError> result =
        ps.validateProgramDataForUpdate(
            "program",
            "description",
            "display name",
            "https://usa.gov",
            DisplayMode.SELECT_TI.getValue(),
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result)
        .containsExactly(
            CiviFormError.of("One or more TI Org must be selected for program visibility"));
  }

  @Test
  public void validateProgramDataForCreate_protectsAgainstProgramSlugCollisions() {
    // Two programs with names that are different but slugify to same value.
    // To simulate this state, we first create a program with a slugified name, then manually
    // update the Program entity in order to add a name value that slugifies to the same value.
    ProgramDefinition originalProgramDefinition =
        ps.createProgramDefinition(
                "name-one",
                "description",
                "display name",
                "display description",
                "",
                "https://usa.gov",
                DisplayMode.PUBLIC.getValue(),
                ProgramType.DEFAULT,
                /* isIntakeFormFeatureEnabled= */ false,
                ImmutableList.copyOf(new ArrayList<>()))
            .getResult();
    // Program name here is missing the extra space
    // so that the names are different but the resulting
    // slug is the same.
    Program updatedProgram =
        originalProgramDefinition.toBuilder().setAdminName("name    one").build().toProgram();
    updatedProgram.update();
    assertThat(updatedProgram.getProgramDefinition().adminName()).isEqualTo("name    one");

    ImmutableSet<CiviFormError> result =
        ps.validateProgramDataForCreate(
            "name-one",
            "description",
            "display name",
            "display desc",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ImmutableList.copyOf(new ArrayList<>()));
    assertThat(result)
        .containsExactly(CiviFormError.of("A program URL of name-one already exists"));
  }

  @Test
  public void validateProgramDataForCreate_returnsNoErrorsForValidData() {
    ImmutableSet<CiviFormError> result =
        ps.validateProgramDataForCreate(
            "name-two",
            "description",
            "display name",
            "display description",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result).isEmpty();
  }

  @Test
  public void validateProgramDataForCreate_returnsNoErrorsInSELECT_TIMode() {
    List<Long> tiGroups = new ArrayList<>();
    tiGroups.add(1L);
    tiGroups.add(3L);
    ImmutableSet<CiviFormError> result =
        ps.validateProgramDataForCreate(
            "name-two",
            "description",
            "display name",
            "display description",
            "https://usa.gov",
            DisplayMode.SELECT_TI.getValue(),
            ImmutableList.copyOf(tiGroups));

    assertThat(result).isEmpty();
  }

  @Test
  public void updateProgram_withNoProgram_throwsProgramNotFoundException() {
    assertThatThrownBy(
            () ->
                ps.updateProgramDefinition(
                    1L,
                    Locale.US,
                    "new description",
                    "name",
                    "description",
                    "",
                    "https://usa.gov",
                    DisplayMode.PUBLIC.getValue(),
                    ProgramType.DEFAULT,
                    /* isIntakeFormFeatureEnabled= */ false,
                    ImmutableList.copyOf(new ArrayList<>())))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessage("Program not found for ID: 1");
  }

  @Test
  public void updateProgram_updatesProgram() throws Exception {
    ProgramDefinition originalProgram =
        ProgramBuilder.newDraftProgram("original", "original description").buildDefinition();
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateProgramDefinition(
            originalProgram.id(),
            Locale.US,
            "new description",
            "name",
            "description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    ProgramDefinition updatedProgram = result.getResult();

    ProgramDefinition found = ps.getProgramDefinition(updatedProgram.id());

    assertThat(ps.getActiveAndDraftPrograms().getDraftPrograms()).hasSize(1);
    assertThat(ps.getActiveAndDraftProgramsWithoutQuestionLoad().getDraftPrograms()).hasSize(1);
    assertThat(found.adminName()).isEqualTo(updatedProgram.adminName());
    assertThat(found.lastModifiedTime().isPresent()).isTrue();
    assertThat(originalProgram.lastModifiedTime().isPresent()).isTrue();
    assertThat(found.lastModifiedTime().get().isAfter(originalProgram.lastModifiedTime().get()))
        .isTrue();
  }

  @Test
  public void updateProgram_constructsQuestionDefinitions() throws Exception {
    QuestionDefinition question = nameQuestion;
    ProgramDefinition program = ProgramBuilder.newDraftProgram().buildDefinition();
    ps.addQuestionsToBlock(program.id(), 1L, ImmutableList.of(question.getId()));

    ProgramDefinition found =
        ps.updateProgramDefinition(
                program.id(),
                Locale.US,
                "new description",
                "name",
                "description",
                "",
                "https://usa.gov",
                DisplayMode.PUBLIC.getValue(),
                ProgramType.DEFAULT,
                /* isIntakeFormFeatureEnabled= */ false,
                ImmutableList.copyOf(new ArrayList<>()))
            .getResult();

    QuestionDefinition foundQuestion =
        found.blockDefinitions().get(0).programQuestionDefinitions().get(0).getQuestionDefinition();
    assertThat(foundQuestion).isInstanceOf(NameQuestionDefinition.class);
  }

  @Test
  public void updateProgram_returnsErrors() throws Exception {
    ProgramDefinition program = ProgramBuilder.newDraftProgram().buildDefinition();

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateProgramDefinition(
            program.id(),
            Locale.US,
            "",
            "",
            "",
            "",
            "",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isFalse();
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors())
        .containsOnly(
            CiviFormError.of("A public display name for the program is required"),
            CiviFormError.of("A public description for the program is required"),
            CiviFormError.of("A program note is required"));
  }

  @Test
  public void updateProgram_clearsOldConfirmationScreenTranslations() throws Exception {
    ProgramDefinition originalProgram =
        ProgramBuilder.newDraftProgram("original", "original description").buildDefinition();
    ErrorAnd<ProgramDefinition, CiviFormError> resultOne =
        ps.updateProgramDefinition(
            originalProgram.id(),
            Locale.US,
            "new description",
            "name",
            "description",
            "custom confirmation screen message",
            "",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            false,
            ImmutableList.copyOf(new ArrayList<>()));

    // check that the confirmation screen message saved
    LocalizedStrings expectedUsString =
        LocalizedStrings.create(ImmutableMap.of(Locale.US, "custom confirmation screen message"));
    ProgramDefinition firstProgramUpdate = resultOne.getResult();
    assertThat(firstProgramUpdate.localizedConfirmationMessage()).isEqualTo(expectedUsString);

    // update the confirmation screen with an translation and check that it saves correctly
    ErrorAnd<ProgramDefinition, CiviFormError> resultTwo =
        ps.updateProgramDefinition(
            firstProgramUpdate.id(),
            Locale.FRANCE,
            "new description",
            "name",
            "description",
            "french custom confirmation screen message",
            "",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            false,
            ImmutableList.copyOf(new ArrayList<>()));
    ProgramDefinition secondProgramUpdate = resultTwo.getResult();
    assertThat(secondProgramUpdate.localizedConfirmationMessage())
        .isEqualTo(
            expectedUsString.updateTranslation(
                Locale.FRANCE, "french custom confirmation screen message"));

    // delete the english confirmation screen and check that all translations were cleared as well
    ErrorAnd<ProgramDefinition, CiviFormError> resultThree =
        ps.updateProgramDefinition(
            firstProgramUpdate.id(),
            Locale.US,
            "new description",
            "name",
            "description",
            "",
            "",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            false,
            ImmutableList.copyOf(new ArrayList<>()));
    ProgramDefinition thirdProgramUpdate = resultThree.getResult();
    assertThat(thirdProgramUpdate.localizedConfirmationMessage())
        .isEqualTo(LocalizedStrings.create(ImmutableMap.of(Locale.US, "")));
  }

  @Test
  public void getProgramDefinition() throws Exception {
    ProgramDefinition programDefinition = ProgramBuilder.newDraftProgram().buildDefinition();
    ProgramDefinition found = ps.getProgramDefinition(programDefinition.id());

    assertThat(found.adminName()).isEqualTo(programDefinition.adminName());
  }

  @Test
  public void updateProgram_intakeFormDisabled() throws Exception {
    ProgramDefinition program = ProgramBuilder.newDraftProgram().buildDefinition();

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateProgramDefinition(
            program.id(),
            Locale.US,
            "a",
            "a",
            "a",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.COMMON_INTAKE_FORM,
            /* isIntakeFormFeatureEnabled= */ false,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().programType()).isEqualTo(ProgramType.DEFAULT);
  }

  @Test
  public void updateProgram_clearsExistingCommonIntakeForm() throws Exception {
    ps.createProgramDefinition(
        "name-one",
        "description",
        "display name",
        "display description",
        "",
        "https://usa.gov",
        DisplayMode.PUBLIC.getValue(),
        ProgramType.COMMON_INTAKE_FORM,
        /* isIntakeFormFeatureEnabled= */ true,
        ImmutableList.copyOf(new ArrayList<>()));

    Optional<ProgramDefinition> commonIntakeForm = ps.getCommonIntakeForm();
    assertThat(commonIntakeForm).isPresent();
    assertThat(commonIntakeForm.get().adminName()).isEqualTo("name-one");

    ProgramDefinition program = ProgramBuilder.newDraftProgram().buildDefinition();
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateProgramDefinition(
            program.id(),
            Locale.US,
            "a",
            "a",
            "a",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.COMMON_INTAKE_FORM,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().programType()).isEqualTo(ProgramType.COMMON_INTAKE_FORM);
    commonIntakeForm = ps.getCommonIntakeForm();
    assertThat(commonIntakeForm).isPresent();
    assertThat(commonIntakeForm.get().adminName()).isEqualTo(program.adminName());
    Optional<ProgramDefinition> oldCommonIntake =
        ps.getActiveAndDraftPrograms().getDraftProgramDefinition("name-one");
    assertThat(oldCommonIntake).isPresent();
    assertThat(oldCommonIntake.get().programType()).isEqualTo(ProgramType.DEFAULT);
  }

  @Test
  public void updateProgram_allowsUpdatingCommonIntakeForm() throws Exception {
    ErrorAnd<ProgramDefinition, CiviFormError> commonIntakeForm =
        ps.createProgramDefinition(
            "name-one",
            "description",
            "display name",
            "display description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.COMMON_INTAKE_FORM,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateProgramDefinition(
            commonIntakeForm.getResult().id(),
            Locale.US,
            "a",
            "a",
            "a",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.COMMON_INTAKE_FORM,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
  }

  @Test
  public void updateProgram_allowsUpdatingCommonIntakeFormToDefaultProgram() throws Exception {
    ErrorAnd<ProgramDefinition, CiviFormError> commonIntakeForm =
        ps.createProgramDefinition(
            "name-one",
            "description",
            "display name",
            "display description",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.COMMON_INTAKE_FORM,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateProgramDefinition(
            commonIntakeForm.getResult().id(),
            Locale.US,
            "a",
            "a",
            "a",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().programType()).isEqualTo(ProgramType.DEFAULT);
  }

  @Test
  public void updateProgram_clearsEligibilityConditionsWhenSettingCommonIntakeForm()
      throws Exception {
    QuestionDefinition question = nameQuestion;
    EligibilityDefinition eligibility =
        EligibilityDefinition.builder()
            .setPredicate(
                PredicateDefinition.create(
                    PredicateExpressionNode.create(
                        LeafOperationExpressionNode.create(
                            question.getId(),
                            Scalar.FIRST_NAME,
                            Operator.EQUAL_TO,
                            PredicateValue.of(""))),
                    PredicateAction.HIDE_BLOCK))
            .build();
    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withEligibilityDefinition(eligibility)
            .withBlock()
            .withEligibilityDefinition(eligibility)
            .buildDefinition();

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateProgramDefinition(
            program.id(),
            Locale.US,
            "a",
            "a",
            "a",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.COMMON_INTAKE_FORM,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().programType()).isEqualTo(ProgramType.COMMON_INTAKE_FORM);
    assertThat(result.getResult().getBlockCount()).isEqualTo(2);
    assertThat(result.getResult().getBlockDefinitionByIndex(0).get().eligibilityDefinition())
        .isNotPresent();
    assertThat(result.getResult().getBlockDefinitionByIndex(1).get().eligibilityDefinition())
        .isNotPresent();
  }

  @Test
  public void updateProgram_doesNotClearEligibilityConditionsForDefaultProgram() throws Exception {
    QuestionDefinition question = nameQuestion;
    EligibilityDefinition eligibility =
        EligibilityDefinition.builder()
            .setPredicate(
                PredicateDefinition.create(
                    PredicateExpressionNode.create(
                        LeafOperationExpressionNode.create(
                            question.getId(),
                            Scalar.FIRST_NAME,
                            Operator.EQUAL_TO,
                            PredicateValue.of(""))),
                    PredicateAction.HIDE_BLOCK))
            .build();
    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withEligibilityDefinition(eligibility)
            .buildDefinition();

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateProgramDefinition(
            program.id(),
            Locale.US,
            "a",
            "a",
            "a",
            "",
            "https://usa.gov",
            DisplayMode.PUBLIC.getValue(),
            ProgramType.DEFAULT,
            /* isIntakeFormFeatureEnabled= */ true,
            ImmutableList.copyOf(new ArrayList<>()));

    assertThat(result.hasResult()).isTrue();
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().programType()).isEqualTo(ProgramType.DEFAULT);
    assertThat(result.getResult().getBlockCount()).isEqualTo(1);
    assertThat(result.getResult().getLastBlockDefinition().eligibilityDefinition()).isPresent();
  }

  @Test
  public void getProgramDefinition_throwsWhenProgramNotFound() {
    assertThatThrownBy(() -> ps.getProgramDefinition(1L))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessageContaining("Program not found for ID");
  }

  @Test
  public void getProgramDefinition_constructsQuestionDefinitions() throws Exception {
    QuestionDefinition question = nameQuestion;
    ProgramDefinition program = ProgramBuilder.newDraftProgram().buildDefinition();
    ps.addQuestionsToBlock(program.id(), 1L, ImmutableList.of(question.getId()));

    ProgramDefinition found = ps.getProgramDefinition(program.id());

    QuestionDefinition foundQuestion =
        found.blockDefinitions().get(0).programQuestionDefinitions().get(0).getQuestionDefinition();
    assertThat(foundQuestion).isInstanceOf(NameQuestionDefinition.class);
  }

  @Test
  public void getProgramDefinitionAsync_getsDraftProgram() {
    ProgramDefinition programDefinition = ProgramBuilder.newDraftProgram().buildDefinition();

    CompletionStage<ProgramDefinition> found = ps.getProgramDefinitionAsync(programDefinition.id());

    assertThat(found.toCompletableFuture().join().adminName())
        .isEqualTo(programDefinition.adminName());
    assertThat(found.toCompletableFuture().join().id()).isEqualTo(programDefinition.id());
  }

  @Test
  public void getProgramDefinitionAsync_getsActiveProgram() {
    ProgramDefinition programDefinition = ProgramBuilder.newActiveProgram().buildDefinition();

    CompletionStage<ProgramDefinition> found = ps.getProgramDefinitionAsync(programDefinition.id());

    assertThat(found.toCompletableFuture().join().id()).isEqualTo(programDefinition.id());
  }

  @Test
  public void getProgramDefinitionAsync_cannotFindRequestedProgram_throwsException() {
    ProgramDefinition programDefinition = ProgramBuilder.newActiveProgram().buildDefinition();

    CompletionStage<ProgramDefinition> found =
        ps.getProgramDefinitionAsync(programDefinition.id() + 1);

    assertThatThrownBy(() -> found.toCompletableFuture().join())
        .isInstanceOf(CompletionException.class)
        .hasRootCauseInstanceOf(ProgramNotFoundException.class)
        .hasMessageContaining("Program not found for ID");
  }

  @Test
  public void getProgramDefinitionAsync_constructsQuestionDefinitions() throws Exception {
    QuestionDefinition question = nameQuestion;
    ProgramDefinition program =
        ProgramBuilder.newActiveProgram()
            .withBlock()
            .withRequiredQuestionDefinition(question)
            .buildDefinition();

    ProgramDefinition found =
        ps.getProgramDefinitionAsync(program.id()).toCompletableFuture().join();

    QuestionDefinition foundQuestion =
        found.blockDefinitions().get(0).programQuestionDefinitions().get(0).getQuestionDefinition();
    assertThat(foundQuestion).isInstanceOf(NameQuestionDefinition.class);
  }

  @Test
  public void getActiveProgramDefinitionAsync_getsActiveProgram() {
    ProgramDefinition programDefinition =
        ProgramBuilder.newActiveProgram("Test Program").buildDefinition();

    CompletionStage<ProgramDefinition> found =
        ps.getActiveProgramDefinitionAsync(programDefinition.slug());

    assertThat(found.toCompletableFuture().join().id()).isEqualTo(programDefinition.id());
  }

  @Test
  public void getActiveProgramDefinitionAsync_cannotFindRequestedProgram_throwsException() {
    ProgramBuilder.newActiveProgram("Test Program").buildDefinition();

    CompletionStage<ProgramDefinition> found =
        ps.getActiveProgramDefinitionAsync("non-existent-program");

    assertThatThrownBy(() -> found.toCompletableFuture().join())
        .isInstanceOf(CompletionException.class)
        .hasRootCauseInstanceOf(ProgramNotFoundException.class)
        .hasMessageContaining("Program not found for slug: non-existent-program");
  }

  @Test
  public void getDraftProgramDefinitionAsync_getsDraftProgram() throws Exception {
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram("Test Program").buildDefinition();

    ProgramDefinition found = ps.getDraftProgramDefinition(programDefinition.slug());

    assertThat(found.id()).isEqualTo(programDefinition.id());
  }

  @Test
  public void getDraftProgramDefinitionAsync_cannotFindRequestedProgram_throwsException() {
    ProgramBuilder.newDraftProgram("Test Program").buildDefinition();

    assertThatThrownBy(() -> ps.getDraftProgramDefinition("non-existent-program"))
        .isInstanceOf(ProgramDraftNotFoundException.class)
        .hasMessageContaining("Program draft not found for slug: non-existent-program");
  }

  @Test
  public void addBlockToProgram_noProgram_throwsProgramNotFoundException() {
    assertThatThrownBy(() -> ps.addBlockToProgram(1L))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessage("Program not found for ID: 1");
  }

  @Test
  public void addBlockToProgram_emptyBlock_returnsProgramDefinitionWithBlock() throws Exception {
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram().withBlock("Screen 1").buildDefinition();
    ErrorAnd<ProgramBlockAdditionResult, CiviFormError> result =
        ps.addBlockToProgram(programDefinition.id());

    assertThat(result.isError()).isFalse();
    assertThat(result.hasResult()).isTrue();
    ProgramDefinition updatedProgramDefinition = result.getResult().program();
    assertThat(result.getResult().maybeAddedBlock()).isNotEmpty();
    BlockDefinition addedBlock = result.getResult().maybeAddedBlock().get();

    ProgramDefinition found = ps.getProgramDefinition(programDefinition.id());

    assertThat(found.blockDefinitions()).hasSize(2);
    assertThat(found.blockDefinitions())
        .containsExactlyElementsOf(updatedProgramDefinition.blockDefinitions());

    BlockDefinition emptyBlock = found.blockDefinitions().get(0);
    assertThat(emptyBlock.name()).isEqualTo("Screen 1");
    assertThat(emptyBlock.description()).isEqualTo("");
    assertThat(emptyBlock.programQuestionDefinitions()).hasSize(0);

    BlockDefinition newBlock = found.blockDefinitions().get(1);
    assertThat(newBlock.id()).isEqualTo(addedBlock.id());
    assertThat(newBlock.name()).isEqualTo("Screen 2");
    assertThat(newBlock.description()).isEqualTo("Screen 2 description");
    assertThat(newBlock.programQuestionDefinitions()).hasSize(0);
  }

  @Test
  public void addBlockToProgram_returnsProgramDefinitionWithBlock() throws Exception {
    ProgramDefinition programDefinition = ProgramBuilder.newDraftProgram().buildDefinition();
    long programId = programDefinition.id();

    ErrorAnd<ProgramBlockAdditionResult, CiviFormError> result =
        ps.addBlockToProgram(programDefinition.id());

    assertThat(result.isError()).isFalse();
    assertThat(result.hasResult()).isTrue();
    ProgramDefinition updatedProgramDefinition = result.getResult().program();
    assertThat(result.getResult().maybeAddedBlock()).isNotEmpty();
    BlockDefinition addedBlock = result.getResult().maybeAddedBlock().get();

    ProgramDefinition found = ps.getProgramDefinition(programId);

    assertThat(found.blockDefinitions()).hasSize(2);
    assertThat(found.blockDefinitions())
        .containsExactlyElementsOf(updatedProgramDefinition.blockDefinitions());
    assertThat(found.blockDefinitions().get(1).id()).isEqualTo(addedBlock.id());
  }

  @Test
  public void addRepeatedBlockToProgram() throws Exception {
    Program program =
        ProgramBuilder.newActiveProgram()
            .withBlock()
            .withRequiredQuestion(testQuestionBank.applicantHouseholdMembers())
            .withRepeatedBlock()
            .withRequiredQuestion(testQuestionBank.applicantHouseholdMemberJobs())
            .withBlock()
            .withRequiredQuestion(testQuestionBank.applicantFavoriteColor())
            .build();

    ErrorAnd<ProgramBlockAdditionResult, CiviFormError> result =
        ps.addRepeatedBlockToProgram(program.id, 1L);

    assertThat(result.isError()).isFalse();
    assertThat(result.hasResult()).isTrue();
    ProgramDefinition updatedProgramDefinition = result.getResult().program();
    assertThat(result.getResult().maybeAddedBlock()).isNotEmpty();
    BlockDefinition addedBlock = result.getResult().maybeAddedBlock().get();

    ProgramDefinition found = ps.getProgramDefinition(program.id);

    assertThat(found.blockDefinitions()).hasSize(4);
    assertThat(found.getBlockDefinitionByIndex(0).get().isEnumerator()).isTrue();
    assertThat(found.getBlockDefinitionByIndex(0).get().isRepeated()).isFalse();
    assertThat(found.getBlockDefinitionByIndex(0).get().getQuestionDefinition(0))
        .isEqualTo(testQuestionBank.applicantHouseholdMembers().getQuestionDefinition());

    assertThat(found.getBlockDefinitionByIndex(1).get().isEnumerator()).isTrue();
    assertThat(found.getBlockDefinitionByIndex(1).get().isRepeated()).isTrue();
    assertThat(found.getBlockDefinitionByIndex(1).get().enumeratorId()).contains(1L);
    assertThat(found.getBlockDefinitionByIndex(1).get().getQuestionDefinition(0))
        .isEqualTo(testQuestionBank.applicantHouseholdMemberJobs().getQuestionDefinition());

    // The newly added block.
    assertThat(found.getBlockDefinitionByIndex(2).get().isRepeated()).isTrue();
    assertThat(found.getBlockDefinitionByIndex(2).get().enumeratorId()).contains(1L);
    assertThat(found.getBlockDefinitionByIndex(2).get().getQuestionCount()).isEqualTo(0);
    assertThat(found.getBlockDefinitionByIndex(2).get().id()).isEqualTo(addedBlock.id());

    assertThat(found.getBlockDefinitionByIndex(3).get().isRepeated()).isFalse();
    assertThat(found.getBlockDefinitionByIndex(3).get().getQuestionDefinition(0))
        .isEqualTo(testQuestionBank.applicantFavoriteColor().getQuestionDefinition());

    assertThat(found.blockDefinitions())
        .containsExactlyElementsOf(updatedProgramDefinition.blockDefinitions());
  }

  @Test
  public void addRepeatedBlockToProgram_toEndOfBlockList() throws Exception {
    Program program =
        ProgramBuilder.newActiveProgram()
            .withBlock()
            .withRequiredQuestion(testQuestionBank.applicantFavoriteColor())
            .withBlock()
            .withRequiredQuestion(testQuestionBank.applicantHouseholdMembers())
            .withRepeatedBlock()
            .withRequiredQuestion(testQuestionBank.applicantHouseholdMemberJobs())
            .build();

    ErrorAnd<ProgramBlockAdditionResult, CiviFormError> result =
        ps.addRepeatedBlockToProgram(program.id, 2L);

    assertThat(result.isError()).isFalse();
    assertThat(result.hasResult()).isTrue();
    ProgramDefinition updatedProgramDefinition = result.getResult().program();
    assertThat(result.getResult().maybeAddedBlock()).isNotEmpty();
    BlockDefinition addedBlock = result.getResult().maybeAddedBlock().get();

    ProgramDefinition found = ps.getProgramDefinition(program.id);

    assertThat(found.blockDefinitions()).hasSize(4);
    assertThat(found.getBlockDefinitionByIndex(0).get().isEnumerator()).isFalse();
    assertThat(found.getBlockDefinitionByIndex(0).get().isRepeated()).isFalse();
    assertThat(found.getBlockDefinitionByIndex(0).get().getQuestionDefinition(0))
        .isEqualTo(testQuestionBank.applicantFavoriteColor().getQuestionDefinition());

    assertThat(found.getBlockDefinitionByIndex(1).get().isEnumerator()).isTrue();
    assertThat(found.getBlockDefinitionByIndex(1).get().isRepeated()).isFalse();
    assertThat(found.getBlockDefinitionByIndex(1).get().getQuestionDefinition(0))
        .isEqualTo(testQuestionBank.applicantHouseholdMembers().getQuestionDefinition());

    assertThat(found.getBlockDefinitionByIndex(2).get().isEnumerator()).isTrue();
    assertThat(found.getBlockDefinitionByIndex(2).get().isRepeated()).isTrue();
    assertThat(found.getBlockDefinitionByIndex(2).get().enumeratorId()).contains(2L);
    assertThat(found.getBlockDefinitionByIndex(2).get().getQuestionDefinition(0))
        .isEqualTo(testQuestionBank.applicantHouseholdMemberJobs().getQuestionDefinition());

    // The newly added block.
    assertThat(found.getBlockDefinitionByIndex(3).get().isRepeated()).isTrue();
    assertThat(found.getBlockDefinitionByIndex(3).get().enumeratorId()).contains(2L);
    assertThat(found.getBlockDefinitionByIndex(3).get().getQuestionCount()).isEqualTo(0);
    assertThat(found.blockDefinitions())
        .containsExactlyElementsOf(updatedProgramDefinition.blockDefinitions());
    assertThat(found.getBlockDefinitionByIndex(3).get().id()).isEqualTo(addedBlock.id());
  }

  @Test
  public void addRepeatedBlockToProgram_invalidProgramId_throwsProgramNotFoundException() {
    assertThatThrownBy(() -> ps.addRepeatedBlockToProgram(1L, 1L))
        .isInstanceOf(ProgramNotFoundException.class);
  }

  @Test
  public void
      addRepeatedBlockToProgram_invalidEnumeratorId_throwsProgramBlockDefinitionNotFoundException() {
    Program program = ProgramBuilder.newActiveProgram().build();

    assertThatThrownBy(() -> ps.addRepeatedBlockToProgram(program.id, 5L))
        .isInstanceOf(ProgramBlockDefinitionNotFoundException.class);
  }

  @Test
  public void updateBlock_noProgram_throwsProgramNotFoundException() {
    assertThatThrownBy(() -> ps.updateBlock(1L, 1L, new BlockForm("block", "description")))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessage("Program not found for ID: 1");
  }

  @Test
  public void updateBlock_invalidBlock_returnsErrors() throws Exception {
    ProgramDefinition program = ProgramBuilder.newDraftProgram().buildDefinition();
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateBlock(program.id(), 1L, new BlockForm());

    // Returns the unmodified program definition.
    assertThat(result.hasResult()).isTrue();
    assertThat(result.getResult().adminName()).isEqualTo(program.adminName());
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors())
        .containsOnly(
            CiviFormError.of("screen name cannot be blank"),
            CiviFormError.of("screen description cannot be blank"));
  }

  @Test
  public void updateBlock() throws Exception {
    ProgramDefinition program = ProgramBuilder.newDraftProgram().buildDefinition();
    BlockForm blockForm = new BlockForm();
    blockForm.setName("new screen name");
    blockForm.setDescription("new description");

    ErrorAnd<ProgramDefinition, CiviFormError> result = ps.updateBlock(program.id(), 1L, blockForm);
    assertThat(result.isError()).isFalse();
    assertThat(result.hasResult()).isTrue();

    ProgramDefinition found = ps.getProgramDefinition(program.id());

    assertThat(found.blockDefinitions()).hasSize(1);
    assertThat(found.getBlockDefinition(1L).name()).isEqualTo("new screen name");
    assertThat(found.getBlockDefinition(1L).description()).isEqualTo("new description");
  }

  @Test
  public void setBlockQuestions_updatesBlock() throws Exception {
    QuestionDefinition question = nameQuestion;
    ProgramDefinition programDefinition = ProgramBuilder.newDraftProgram().buildDefinition();
    Long programId = programDefinition.id();

    ps.setBlockQuestions(
        programId,
        1L,
        ImmutableList.of(ProgramQuestionDefinition.create(question, Optional.of(programId))));
    ProgramDefinition found = ps.getProgramDefinition(programId);

    assertThat(found.blockDefinitions()).hasSize(1);

    BlockDefinition foundBlock = found.blockDefinitions().get(0);
    assertThat(foundBlock.programQuestionDefinitions()).hasSize(1);

    ProgramQuestionDefinition foundPqd =
        found.blockDefinitions().get(0).programQuestionDefinitions().get(0);
    assertThat(foundPqd.id()).isEqualTo(question.getId());
    assertThat(foundPqd.getQuestionDefinition()).isInstanceOf(NameQuestionDefinition.class);
    assertThat(foundPqd.getQuestionDefinition().getName()).isEqualTo("applicant name");
  }

  @Test
  public void setBlockQuestions_withBogusBlockId_throwsProgramBlockDefinitionNotFoundException() {
    ProgramDefinition p =
        ps.createProgramDefinition(
                "name",
                "description",
                "name",
                "description",
                "",
                "https://usa.gov",
                DisplayMode.PUBLIC.getValue(),
                ProgramType.DEFAULT,
                false,
                ImmutableList.copyOf(new ArrayList<>()))
            .getResult();
    assertThatThrownBy(() -> ps.setBlockQuestions(p.id(), 100L, ImmutableList.of()))
        .isInstanceOf(ProgramBlockDefinitionNotFoundException.class)
        .hasMessage(
            String.format(
                "Block not found in Program (ID %d) for block definition ID 100", p.id()));
  }

  @Test
  public void setBlockQuestions_constructsQuestionDefinitions() throws Exception {
    QuestionDefinition question = nameQuestion;
    ProgramDefinition programDefinition = ProgramBuilder.newDraftProgram().buildDefinition();
    Long programId = programDefinition.id();

    ProgramDefinition found =
        ps.setBlockQuestions(
            programId,
            1L,
            ImmutableList.of(ProgramQuestionDefinition.create(question, Optional.of(programId))));
    QuestionDefinition foundQuestion =
        found.blockDefinitions().get(0).programQuestionDefinitions().get(0).getQuestionDefinition();
    assertThat(foundQuestion).isInstanceOf(NameQuestionDefinition.class);
  }

  @Test
  public void setBlockQuestions_overwritesPredicateQuestion_throwsException() {
    QuestionDefinition question = nameQuestion;
    PredicateDefinition predicate =
        PredicateDefinition.create(
            PredicateExpressionNode.create(
                LeafOperationExpressionNode.create(
                    question.getId(), Scalar.FIRST_NAME, Operator.EQUAL_TO, PredicateValue.of(""))),
            PredicateAction.HIDE_BLOCK);
    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(question)
            .withBlock()
            .withVisibilityPredicate(predicate)
            .buildDefinition();

    // Overwriting the question in the first block invalidates the predicate in the second block.
    assertThatExceptionOfType(IllegalPredicateOrderingException.class)
        .isThrownBy(
            () ->
                ps.setBlockQuestions(
                    program.id(),
                    1L,
                    ImmutableList.of(
                        ProgramQuestionDefinition.create(
                            addressQuestion, Optional.of(program.id())))))
        .withMessage("This action would invalidate a block condition");
  }

  @Test
  public void addQuestionsToBlock_withDuplicatedQuestions_throwsCantAddQuestionToBlockException() {
    QuestionDefinition questionA = nameQuestion;

    Program program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(questionA)
            .build();

    assertThatThrownBy(
            () -> ps.addQuestionsToBlock(program.id, 1L, ImmutableList.of(questionA.getId())))
        .isInstanceOf(CantAddQuestionToBlockException.class)
        .hasMessage(
            String.format(
                "Can't add question to the block. Error: DUPLICATE. Program ID %d, block ID %d,"
                    + " question ID %d",
                program.id, 1L, questionA.getId()));
  }

  @Test
  public void addQuestionsToBlock_addsQuestionsToTheBlock() throws Exception {
    QuestionDefinition questionA = nameQuestion;
    QuestionDefinition questionB = addressQuestion;

    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(questionA)
            .buildDefinition();

    program = ps.addQuestionsToBlock(program.id(), 1L, ImmutableList.of(questionB.getId()));

    assertThat(program.hasQuestion(questionA)).isTrue();
    assertThat(program.hasQuestion(questionB)).isTrue();
  }

  @Test
  public void removeQuestionsFromBlock_withoutQuestion_throwsQuestionNotFoundException()
      throws Exception {
    QuestionDefinition questionA = nameQuestion;
    Program program = ProgramBuilder.newDraftProgram().withBlock().build();

    assertThatThrownBy(
            () -> ps.removeQuestionsFromBlock(program.id, 1L, ImmutableList.of(questionA.getId())))
        .isInstanceOf(QuestionNotFoundException.class)
        .hasMessage(
            String.format(
                "Question (ID %d) not found in Program (ID %d)", questionA.getId(), program.id));
  }

  @Test
  public void removeQuestionsFromBlock_removesQuestionsFromTheBlock() throws Exception {
    QuestionDefinition questionA = nameQuestion;
    QuestionDefinition questionB = addressQuestion;

    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(questionA)
            .withRequiredQuestionDefinition(questionB)
            .buildDefinition();

    program = ps.removeQuestionsFromBlock(program.id(), 1L, ImmutableList.of(questionB.getId()));

    assertThat(program.hasQuestion(questionA)).isTrue();
    assertThat(program.hasQuestion(questionB)).isFalse();
  }

  @Test
  public void removeQuestionsFromBlock_invalidatesPredicate_throwsException() {
    QuestionDefinition question = nameQuestion;
    PredicateDefinition predicate =
        PredicateDefinition.create(
            PredicateExpressionNode.create(
                LeafOperationExpressionNode.create(
                    question.getId(), Scalar.FIRST_NAME, Operator.EQUAL_TO, PredicateValue.of(""))),
            PredicateAction.HIDE_BLOCK);
    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(question)
            .withBlock()
            .withVisibilityPredicate(predicate)
            .buildDefinition();

    assertThatExceptionOfType(IllegalPredicateOrderingException.class)
        .isThrownBy(
            () -> ps.removeQuestionsFromBlock(program.id(), 1L, ImmutableList.of(question.getId())))
        .withMessage("This action would invalidate a block condition");
  }

  @Test
  public void setBlockPredicate_updatesBlock() throws Exception {
    Question question = testQuestionBank.applicantAddress();
    Program program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestion(question)
            .withBlock()
            .build();

    var cityPredicate =
        PredicateExpressionNode.create(
            LeafOperationExpressionNode.create(
                question.id, Scalar.CITY, Operator.EQUAL_TO, PredicateValue.of("")));
    var statePredicate =
        PredicateExpressionNode.create(
            LeafOperationExpressionNode.create(
                question.id, Scalar.STATE, Operator.EQUAL_TO, PredicateValue.of("")));
    var zipPredicate =
        PredicateExpressionNode.create(
            LeafOperationExpressionNode.create(
                question.id, Scalar.ZIP, Operator.EQUAL_TO, PredicateValue.of("")));
    // Exercise all node types.
    PredicateDefinition predicate =
        PredicateDefinition.create(
            PredicateExpressionNode.create(
                OrNode.create(
                    ImmutableList.of(
                        cityPredicate,
                        PredicateExpressionNode.create(
                            AndNode.create(ImmutableList.of(statePredicate, zipPredicate)))))),
            PredicateAction.HIDE_BLOCK);

    ps.setBlockVisibilityPredicate(program.id, 2L, Optional.of(predicate));

    ProgramDefinition found = ps.getProgramDefinition(program.id);

    // Verify serialization and deserialization.
    assertThat(found.blockDefinitions().get(1).visibilityPredicate()).hasValue(predicate);
  }

  @Test
  public void setBlockPredicate_withBogusBlockId_throwsProgramBlockDefinitionNotFoundException() {
    ProgramDefinition p = ProgramBuilder.newDraftProgram().buildDefinition();
    assertThatThrownBy(
            () ->
                ps.setBlockVisibilityPredicate(
                    p.id(),
                    100L,
                    Optional.of(
                        PredicateDefinition.create(
                            PredicateExpressionNode.create(
                                LeafOperationExpressionNode.create(
                                    1L, Scalar.CITY, Operator.EQUAL_TO, PredicateValue.of(""))),
                            PredicateAction.HIDE_BLOCK))))
        .isInstanceOf(ProgramBlockDefinitionNotFoundException.class)
        .hasMessage(
            String.format(
                "Block not found in Program (ID %d) for block definition ID 100", p.id()));
  }

  @Test
  public void setBlockPredicate_constructsQuestionDefinitions() throws Exception {
    QuestionDefinition question = nameQuestion;
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(question)
            .withBlock()
            .buildDefinition();
    Long programId = programDefinition.id();

    ProgramDefinition found =
        ps.setBlockVisibilityPredicate(
            programId,
            2L,
            Optional.of(
                PredicateDefinition.create(
                    PredicateExpressionNode.create(
                        LeafOperationExpressionNode.create(
                            question.getId(),
                            Scalar.CITY,
                            Operator.EQUAL_TO,
                            PredicateValue.of(""))),
                    PredicateAction.HIDE_BLOCK)));

    QuestionDefinition foundQuestion =
        found.blockDefinitions().get(0).programQuestionDefinitions().get(0).getQuestionDefinition();
    assertThat(foundQuestion).isInstanceOf(NameQuestionDefinition.class);
  }

  @Test
  public void setBlockPredicate_illegalPredicate_throwsException() {
    QuestionDefinition question = nameQuestion;
    PredicateDefinition predicate =
        PredicateDefinition.create(
            PredicateExpressionNode.create(
                LeafOperationExpressionNode.create(
                    question.getId(), Scalar.FIRST_NAME, Operator.EQUAL_TO, PredicateValue.of(""))),
            PredicateAction.HIDE_BLOCK);
    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(addressQuestion)
            .withBlock()
            .buildDefinition();

    // This predicate depends on a question that doesn't exist in a prior block.
    assertThatExceptionOfType(IllegalPredicateOrderingException.class)
        .isThrownBy(() -> ps.setBlockVisibilityPredicate(program.id(), 2L, Optional.of(predicate)))
        .withMessage("This action would invalidate a block condition");
  }

  @Test
  public void setBlockEligibilityDefinition_updatesBlock() throws Exception {
    Question question = testQuestionBank.applicantAddress();
    Program program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestion(question)
            .withBlock()
            .build();

    var cityPredicate =
        PredicateExpressionNode.create(
            LeafOperationExpressionNode.create(
                question.id, Scalar.CITY, Operator.EQUAL_TO, PredicateValue.of("")));
    var statePredicate =
        PredicateExpressionNode.create(
            LeafOperationExpressionNode.create(
                question.id, Scalar.STATE, Operator.EQUAL_TO, PredicateValue.of("")));
    var zipPredicate =
        PredicateExpressionNode.create(
            LeafOperationExpressionNode.create(
                question.id, Scalar.ZIP, Operator.EQUAL_TO, PredicateValue.of("")));
    // Exercise all node types.
    PredicateDefinition predicate =
        PredicateDefinition.create(
            PredicateExpressionNode.create(
                OrNode.create(
                    ImmutableList.of(
                        cityPredicate,
                        PredicateExpressionNode.create(
                            AndNode.create(ImmutableList.of(statePredicate, zipPredicate)))))),
            PredicateAction.HIDE_BLOCK);

    ps.setBlockEligibilityDefinition(
        program.id,
        2L,
        Optional.of(EligibilityDefinition.builder().setPredicate(predicate).build()));

    ProgramDefinition found = ps.getProgramDefinition(program.id);

    // Verify serialization and deserialization.
    assertThat(found.blockDefinitions().get(1).eligibilityDefinition()).isPresent();
    assertThat(found.blockDefinitions().get(1).eligibilityDefinition().get().predicate())
        .isEqualTo(predicate);
  }

  @Test
  public void setBlockEligibilityDefinition_throwsEligibilityNotValidForProgramTypeException()
      throws Exception {
    QuestionDefinition question = nameQuestion;
    EligibilityDefinition eligibility =
        EligibilityDefinition.builder()
            .setPredicate(
                PredicateDefinition.create(
                    PredicateExpressionNode.create(
                        LeafOperationExpressionNode.create(
                            question.getId(),
                            Scalar.FIRST_NAME,
                            Operator.EQUAL_TO,
                            PredicateValue.of(""))),
                    PredicateAction.HIDE_BLOCK))
            .build();
    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withProgramType(ProgramType.COMMON_INTAKE_FORM)
            .withBlock()
            .withRequiredQuestionDefinition(question)
            .buildDefinition();

    assertThatExceptionOfType(EligibilityNotValidForProgramTypeException.class)
        .isThrownBy(
            () -> ps.setBlockEligibilityDefinition(program.id(), 1L, Optional.of(eligibility)))
        .withMessage("Eligibility conditions cannot be set for ProgramType COMMON_INTAKE_FORM");
  }

  @Test
  public void removeBlockPredicate() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(addressQuestion)
            .withBlock()
            .build();

    // First set the predicate and assert its presence.
    PredicateDefinition predicate =
        PredicateDefinition.create(
            PredicateExpressionNode.create(
                LeafOperationExpressionNode.create(
                    addressQuestion.getId(),
                    Scalar.CITY,
                    Operator.EQUAL_TO,
                    PredicateValue.of(""))),
            PredicateAction.HIDE_BLOCK);
    ps.setBlockVisibilityPredicate(program.id, 2L, Optional.of(predicate));

    ProgramDefinition foundWithPredicate = ps.getProgramDefinition(program.id);
    assertThat(foundWithPredicate.blockDefinitions().get(1).visibilityPredicate())
        .hasValue(predicate);

    // Then remove that predicate and assert its absence.
    ps.removeBlockPredicate(program.id, 2L);

    ProgramDefinition foundWithoutPredicate = ps.getProgramDefinition(program.id);
    assertThat(foundWithoutPredicate.blockDefinitions().get(1).visibilityPredicate()).isEmpty();
  }

  @Test
  public void setProgramQuestionDefinitionOptionality() throws Exception {
    QuestionDefinition question = nameQuestion;
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(question)
            .buildDefinition();
    Long programId = programDefinition.id();

    assertThat(
            programDefinition
                .getBlockDefinitionByIndex(0)
                .get()
                .programQuestionDefinitions()
                .get(0)
                .optional())
        .isFalse();

    programDefinition =
        ps.setProgramQuestionDefinitionOptionality(programId, 1L, nameQuestion.getId(), true);
    assertThat(
            programDefinition
                .getBlockDefinitionByIndex(0)
                .get()
                .programQuestionDefinitions()
                .get(0)
                .optional())
        .isTrue();

    programDefinition =
        ps.setProgramQuestionDefinitionOptionality(programId, 1L, nameQuestion.getId(), false);
    assertThat(
            programDefinition
                .getBlockDefinitionByIndex(0)
                .get()
                .programQuestionDefinitions()
                .get(0)
                .optional())
        .isFalse();

    // Checking that there's no problem
    assertThatThrownBy(
            () ->
                ps.setProgramQuestionDefinitionOptionality(
                    programId, 1L, nameQuestion.getId() + 1, false))
        .isInstanceOf(ProgramQuestionDefinitionNotFoundException.class);
  }

  @Test
  public void setProgramQuestionDefinitionAddressCorrectionEnabled() throws Exception {
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(addressQuestion)
            .withBlock()
            .withVisibilityPredicate(
                PredicateDefinition.create(
                    PredicateExpressionNode.create(
                        LeafOperationExpressionNode.builder()
                            .setQuestionId(addressQuestion.getId())
                            .setScalar(Scalar.SERVICE_AREA)
                            .setOperator(Operator.IN_SERVICE_AREA)
                            .setComparedValue(PredicateValue.serviceArea("seattle"))
                            .build()),
                    PredicateAction.HIDE_BLOCK))
            .buildDefinition();

    assertThat(
            ps.setProgramQuestionDefinitionAddressCorrectionEnabled(
                    programDefinition.id(), 1L, addressQuestion.getId(), true)
                .getBlockDefinitionByIndex(0)
                .get()
                .programQuestionDefinitions()
                .get(0)
                .addressCorrectionEnabled())
        .isTrue();

    assertThatThrownBy(
            () ->
                ps.setProgramQuestionDefinitionAddressCorrectionEnabled(
                    programDefinition.id(), 1L, addressQuestion.getId(), false))
        .isInstanceOf(BadRequestException.class);
  }

  private void assertQuestionsOrder(ProgramDefinition program, QuestionDefinition... expectedOrder)
      throws Exception {
    var expectedQuestionNames = Arrays.stream(expectedOrder).map(QuestionDefinition::getName);
    var actualQuestionNames =
        program.getLastBlockDefinition().programQuestionDefinitions().stream()
            .map(q -> q.getQuestionDefinition().getName());
    assertThat(actualQuestionNames)
        .containsExactlyElementsOf(expectedQuestionNames.collect(Collectors.toList()));
  }

  @Test
  public void setProgramQuestionDefinitionPosition() throws Exception {
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(nameQuestion)
            .withRequiredQuestionDefinition(addressQuestion)
            .withRequiredQuestionDefinition(colorQuestion)
            .buildDefinition();
    BlockDefinition block = programDefinition.getLastBlockDefinition();

    // move address to the beginning
    programDefinition =
        ps.setProgramQuestionDefinitionPosition(
            programDefinition.id(), block.id(), addressQuestion.getId(), 0);
    assertQuestionsOrder(programDefinition, addressQuestion, nameQuestion, colorQuestion);

    // move address to the end
    programDefinition =
        ps.setProgramQuestionDefinitionPosition(
            programDefinition.id(), block.id(), addressQuestion.getId(), 2);
    assertQuestionsOrder(programDefinition, nameQuestion, colorQuestion, addressQuestion);

    // move name to itself (shouldn't change position)
    programDefinition =
        ps.setProgramQuestionDefinitionPosition(
            programDefinition.id(), block.id(), nameQuestion.getId(), 0);
    assertQuestionsOrder(programDefinition, nameQuestion, colorQuestion, addressQuestion);
  }

  @Test
  public void setProgramQuestionDefinitionPosition_invalidPosition() throws Exception {
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(nameQuestion)
            .withRequiredQuestionDefinition(addressQuestion)
            .withRequiredQuestionDefinition(colorQuestion)
            .buildDefinition();
    BlockDefinition block = programDefinition.getLastBlockDefinition();

    assertThatThrownBy(
            () ->
                ps.setProgramQuestionDefinitionPosition(
                    programDefinition.id(), block.id(), addressQuestion.getId(), -1))
        .isInstanceOf(InvalidQuestionPositionException.class);
    assertThatThrownBy(
            () ->
                ps.setProgramQuestionDefinitionPosition(
                    programDefinition.id(), block.id(), addressQuestion.getId(), 3))
        .isInstanceOf(InvalidQuestionPositionException.class);
  }

  @Test
  public void deleteBlock_invalidProgram_throwsProgramNotfoundException() {
    assertThatThrownBy(() -> ps.deleteBlock(1L, 2L))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessage("Program not found for ID: 1");
  }

  @Test
  public void deleteBlock_lastBlock_throwsProgramNeedsABlockException() throws Exception {
    Program program = ProgramBuilder.newDraftProgram().build();

    assertThatThrownBy(() -> ps.deleteBlock(program.id, 1L))
        .isInstanceOf(ProgramNeedsABlockException.class);
  }

  @Test
  public void deleteBlock_removesEligibilityPredicateQuestion_throwsException() {
    QuestionDefinition question = nameQuestion;
    EligibilityDefinition eligibility =
        EligibilityDefinition.builder()
            .setPredicate(
                PredicateDefinition.create(
                    PredicateExpressionNode.create(
                        LeafOperationExpressionNode.create(
                            question.getId(),
                            Scalar.FIRST_NAME,
                            Operator.EQUAL_TO,
                            PredicateValue.of(""))),
                    PredicateAction.HIDE_BLOCK))
            .build();
    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(addressQuestion)
            .withBlock()
            .withEligibilityDefinition(eligibility)
            .buildDefinition();

    // This predicate depends on a question that doesn't exist in a prior block.
    assertThatExceptionOfType(IllegalPredicateOrderingException.class)
        .isThrownBy(() -> ps.deleteBlock(program.id(), 1L))
        .withMessage("This action would invalidate a block condition");
  }

  @Test
  public void deleteBlock_removesVisibilityPredicateQuestion_throwsException() {
    QuestionDefinition question = nameQuestion;
    PredicateDefinition predicate =
        PredicateDefinition.create(
            PredicateExpressionNode.create(
                LeafOperationExpressionNode.create(
                    question.getId(), Scalar.FIRST_NAME, Operator.EQUAL_TO, PredicateValue.of(""))),
            PredicateAction.HIDE_BLOCK);
    ProgramDefinition program =
        ProgramBuilder.newDraftProgram()
            .withBlock()
            .withRequiredQuestionDefinition(addressQuestion)
            .withBlock()
            .withVisibilityPredicate(predicate)
            .buildDefinition();

    // This predicate depends on a question that doesn't exist in a prior block.
    assertThatExceptionOfType(IllegalPredicateOrderingException.class)
        .isThrownBy(() -> ps.deleteBlock(program.id(), 1L))
        .withMessage("This action would invalidate a block condition");
  }

  @Test
  public void deleteBlock_constructsQuestionDefinitions() throws Exception {
    QuestionDefinition question = nameQuestion;
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram()
            .withBlock("screen one")
            .withRequiredQuestionDefinition(question)
            .withBlock()
            .buildDefinition();
    Long programId = programDefinition.id();

    ProgramDefinition result = ps.deleteBlock(programId, 2L);

    assertThat(result.blockDefinitions()).hasSize(1);

    BlockDefinition blockResult = result.blockDefinitions().get(0);
    assertThat(blockResult.name()).isEqualTo("screen one");
    assertThat(blockResult.programQuestionDefinitions()).hasSize(1);

    QuestionDefinition questionResult =
        blockResult.programQuestionDefinitions().get(0).getQuestionDefinition();
    assertThat(questionResult).isInstanceOf(NameQuestionDefinition.class);
  }

  @Test
  public void newProgramFromExisting() throws Exception {
    Program program = ProgramBuilder.newActiveProgram().build();
    program.save();

    ProgramDefinition newDraft = ps.newDraftOf(program.id);
    assertThat(newDraft.adminName()).isEqualTo(program.getProgramDefinition().adminName());
    assertThat(newDraft.blockDefinitions())
        .isEqualTo(program.getProgramDefinition().blockDefinitions());
    assertThat(newDraft.localizedDescription())
        .isEqualTo(program.getProgramDefinition().localizedDescription());
    assertThat(newDraft.id()).isNotEqualTo(program.getProgramDefinition().id());

    ProgramDefinition secondNewDraft = ps.newDraftOf(program.id);
    assertThat(secondNewDraft.id()).isEqualTo(newDraft.id());
  }

  private static final String STATUS_WITH_EMAIL_ENGLISH_NAME = "status-with-email";
  private static final String STATUS_WITH_EMAIL_ENGLISH_EMAIL = "some email";
  private static final String STATUS_WITH_EMAIL_FRENCH_NAME = "status-with-email-french";
  private static final String STATUS_WITH_EMAIL_FRENCH_EMAIL = "some email in French";

  private static final StatusDefinitions.Status STATUS_WITH_EMAIL =
      StatusDefinitions.Status.builder()
          .setStatusText(STATUS_WITH_EMAIL_ENGLISH_NAME)
          .setLocalizedStatusText(
              LocalizedStrings.withDefaultValue(STATUS_WITH_EMAIL_ENGLISH_NAME)
                  .updateTranslation(Locale.FRENCH, STATUS_WITH_EMAIL_FRENCH_NAME))
          .setLocalizedEmailBodyText(
              Optional.of(
                  LocalizedStrings.withDefaultValue(STATUS_WITH_EMAIL_ENGLISH_EMAIL)
                      .updateTranslation(Locale.FRENCH, STATUS_WITH_EMAIL_FRENCH_EMAIL)))
          .build();

  private static final String STATUS_WITH_NO_EMAIL_ENGLISH_NAME = "status-with-no-email";
  private static final String STATUS_WITH_NO_EMAIL_FRENCH_NAME = "status-with-no-email-french";

  private static final StatusDefinitions.Status STATUS_WITH_NO_EMAIL =
      StatusDefinitions.Status.builder()
          .setStatusText(STATUS_WITH_NO_EMAIL_ENGLISH_NAME)
          .setLocalizedStatusText(
              LocalizedStrings.withDefaultValue(STATUS_WITH_NO_EMAIL_ENGLISH_NAME)
                  .updateTranslation(Locale.FRENCH, STATUS_WITH_NO_EMAIL_FRENCH_NAME))
          .build();

  @Test
  public void updateLocalizations_addsNewLocale() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(
                new StatusDefinitions(ImmutableList.of(STATUS_WITH_EMAIL, STATUS_WITH_NO_EMAIL)))
            .build();

    LocalizationUpdate updateData =
        LocalizationUpdate.builder()
            .setLocalizedDisplayName("German Name")
            .setLocalizedDisplayDescription("German Description")
            .setLocalizedConfirmationMessage("")
            .setStatuses(
                ImmutableList.of(
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(Optional.of("german-status-with-email"))
                        .setLocalizedEmailBody(Optional.of("german email body"))
                        .build(),
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_NO_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(Optional.of("german-status-with-no-email"))
                        .build()))
            .build();
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateLocalization(program.id, Locale.GERMAN, updateData);

    assertThat(result.isError()).isFalse();
    ProgramDefinition definition = result.getResult();
    assertThat(definition.localizedName().get(Locale.GERMAN)).isEqualTo("German Name");
    assertThat(definition.localizedDescription().get(Locale.GERMAN))
        .isEqualTo("German Description");
    assertThat(definition.statusDefinitions().getStatuses())
        .isEqualTo(
            ImmutableList.of(
                StatusDefinitions.Status.builder()
                    .setStatusText(STATUS_WITH_EMAIL.statusText())
                    .setLocalizedStatusText(
                        STATUS_WITH_EMAIL
                            .localizedStatusText()
                            .updateTranslation(Locale.GERMAN, "german-status-with-email"))
                    .setLocalizedEmailBodyText(
                        Optional.of(
                            STATUS_WITH_EMAIL
                                .localizedEmailBodyText()
                                .get()
                                .updateTranslation(Locale.GERMAN, "german email body")))
                    .build(),
                StatusDefinitions.Status.builder()
                    .setStatusText(STATUS_WITH_NO_EMAIL.statusText())
                    .setLocalizedStatusText(
                        STATUS_WITH_NO_EMAIL
                            .localizedStatusText()
                            .updateTranslation(Locale.GERMAN, "german-status-with-no-email"))
                    .build()));
  }

  @Test
  public void updateLocalizations_updatesExistingLocale() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram("English name", "English description")
            .withLocalizedName(Locale.FRENCH, "existing French name")
            .withLocalizedDescription(Locale.FRENCH, "existing French description")
            .withLocalizedConfirmationMessage(Locale.FRENCH, "")
            .withStatusDefinitions(
                new StatusDefinitions(ImmutableList.of(STATUS_WITH_EMAIL, STATUS_WITH_NO_EMAIL)))
            .build();

    LocalizationUpdate updateData =
        LocalizationUpdate.builder()
            .setLocalizedDisplayName("new French name")
            .setLocalizedDisplayDescription("new French description")
            .setLocalizedConfirmationMessage("")
            .setStatuses(
                ImmutableList.of(
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(
                            Optional.of(STATUS_WITH_EMAIL_FRENCH_NAME + "-updated"))
                        .setLocalizedEmailBody(
                            Optional.of(STATUS_WITH_EMAIL_FRENCH_EMAIL + "-updated"))
                        .build(),
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_NO_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(
                            Optional.of(STATUS_WITH_NO_EMAIL_FRENCH_NAME + "-updated"))
                        .build()))
            .build();
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateLocalization(program.id, Locale.FRENCH, updateData);

    assertThat(result.isError()).isFalse();
    ProgramDefinition definition = result.getResult();
    assertThat(definition.localizedName().get(Locale.FRENCH)).isEqualTo("new French name");
    assertThat(definition.localizedDescription().get(Locale.FRENCH))
        .isEqualTo("new French description");
    assertThat(definition.statusDefinitions().getStatuses())
        .isEqualTo(
            ImmutableList.of(
                StatusDefinitions.Status.builder()
                    .setStatusText(STATUS_WITH_EMAIL.statusText())
                    .setLocalizedStatusText(
                        STATUS_WITH_EMAIL
                            .localizedStatusText()
                            .updateTranslation(
                                Locale.FRENCH, STATUS_WITH_EMAIL_FRENCH_NAME + "-updated"))
                    .setLocalizedEmailBodyText(
                        Optional.of(
                            STATUS_WITH_EMAIL
                                .localizedEmailBodyText()
                                .get()
                                .updateTranslation(
                                    Locale.FRENCH, STATUS_WITH_EMAIL_FRENCH_EMAIL + "-updated")))
                    .build(),
                StatusDefinitions.Status.builder()
                    .setStatusText(STATUS_WITH_NO_EMAIL.statusText())
                    .setLocalizedStatusText(
                        STATUS_WITH_NO_EMAIL
                            .localizedStatusText()
                            .updateTranslation(
                                Locale.FRENCH, STATUS_WITH_NO_EMAIL_FRENCH_NAME + "-updated"))
                    .build()));
  }

  @Test
  public void updateLocalizations_returnsErrorMessages() throws Exception {
    Program program = ProgramBuilder.newDraftProgram().build();

    LocalizationUpdate updateData =
        LocalizationUpdate.builder()
            .setLocalizedDisplayName("")
            .setLocalizedDisplayDescription("")
            .setLocalizedConfirmationMessage("")
            .setStatuses(ImmutableList.of())
            .build();
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateLocalization(program.id, Locale.FRENCH, updateData);

    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors())
        .containsExactly(
            CiviFormError.of("program display name cannot be blank"),
            CiviFormError.of("program display description cannot be blank"));
  }

  @Test
  public void updateLocalizations_programNotFound_throws() {
    LocalizationUpdate updateData =
        LocalizationUpdate.builder()
            .setLocalizedDisplayName("a name")
            .setLocalizedDisplayDescription("a description")
            .setLocalizedConfirmationMessage("")
            .setStatuses(ImmutableList.of())
            .build();
    assertThatThrownBy(() -> ps.updateLocalization(1000L, Locale.FRENCH, updateData))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessageContaining("Program not found for ID: 1000");
  }

  @Test
  public void updateLocalizations_allowsClearingStatusFields() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram("English name", "English description")
            .withLocalizedName(Locale.FRENCH, "existing French name")
            .withLocalizedDescription(Locale.FRENCH, "existing French description")
            .withLocalizedConfirmationMessage(Locale.FRENCH, "")
            .withStatusDefinitions(
                new StatusDefinitions(ImmutableList.of(STATUS_WITH_EMAIL, STATUS_WITH_NO_EMAIL)))
            .build();

    LocalizationUpdate updateData =
        LocalizationUpdate.builder()
            .setLocalizedDisplayName("new French name")
            .setLocalizedDisplayDescription("new French description")
            .setLocalizedConfirmationMessage("")
            .setStatuses(
                ImmutableList.of(
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_EMAIL_ENGLISH_NAME)
                        .build(),
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_NO_EMAIL_ENGLISH_NAME)
                        .build()))
            .build();
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.updateLocalization(program.id, Locale.FRENCH, updateData);

    assertThat(result.isError()).isFalse();
    ProgramDefinition definition = result.getResult();
    assertThat(definition.localizedName().get(Locale.FRENCH)).isEqualTo("new French name");
    assertThat(definition.localizedDescription().get(Locale.FRENCH))
        .isEqualTo("new French description");
    assertThat(definition.statusDefinitions().getStatuses())
        .isEqualTo(
            ImmutableList.of(
                StatusDefinitions.Status.builder()
                    .setStatusText(STATUS_WITH_EMAIL.statusText())
                    .setLocalizedStatusText(
                        STATUS_WITH_EMAIL
                            .localizedStatusText()
                            .updateTranslation(Locale.FRENCH, Optional.empty()))
                    .setLocalizedEmailBodyText(
                        Optional.of(
                            STATUS_WITH_EMAIL
                                .localizedEmailBodyText()
                                .get()
                                .updateTranslation(Locale.FRENCH, Optional.empty())))
                    .build(),
                StatusDefinitions.Status.builder()
                    .setStatusText(STATUS_WITH_NO_EMAIL.statusText())
                    .setLocalizedStatusText(
                        STATUS_WITH_NO_EMAIL
                            .localizedStatusText()
                            .updateTranslation(Locale.FRENCH, Optional.empty()))
                    .build()));
  }

  @Test
  public void updateLocalizations_providesUnrecognizedStatuses_throws() {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(
                new StatusDefinitions(ImmutableList.of(STATUS_WITH_EMAIL, STATUS_WITH_NO_EMAIL)))
            .build();

    LocalizationUpdate updateData =
        LocalizationUpdate.builder()
            .setLocalizedDisplayName("German Name")
            .setLocalizedDisplayDescription("German Description")
            .setLocalizedConfirmationMessage("")
            .setStatuses(
                ImmutableList.of(
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate("unrecognized-status")
                        .setLocalizedStatusText(Optional.of("unrecognized-status"))
                        .setLocalizedEmailBody(Optional.of("unrecognized-status-email-body"))
                        .build(),
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(Optional.of("german-status-with-email"))
                        .setLocalizedEmailBody(Optional.of("german email body"))
                        .build(),
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_NO_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(Optional.of("german-status-with-no-email"))
                        .build()))
            .build();

    assertThatThrownBy(() -> ps.updateLocalization(program.id, Locale.FRENCH, updateData))
        .isInstanceOf(OutOfDateStatusesException.class);
  }

  @Test
  public void updateLocalizations_doesNotProvideStatus_throws() {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(
                new StatusDefinitions(ImmutableList.of(STATUS_WITH_EMAIL, STATUS_WITH_NO_EMAIL)))
            .build();

    LocalizationUpdate updateData =
        LocalizationUpdate.builder()
            .setLocalizedDisplayName("German Name")
            .setLocalizedDisplayDescription("German Description")
            .setLocalizedConfirmationMessage("")
            .setStatuses(
                ImmutableList.of(
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(Optional.of("german-status-with-email"))
                        .setLocalizedEmailBody(Optional.of("german email body"))
                        .build()))
            .build();

    assertThatThrownBy(() -> ps.updateLocalization(program.id, Locale.FRENCH, updateData))
        .isInstanceOf(OutOfDateStatusesException.class);
  }

  @Test
  public void updateLocalizations_emailProvidedInUpdateWithNoEmailInConfigure_throws() {
    Program program =
        ProgramBuilder.newDraftProgram("English name", "English description")
            .withLocalizedName(Locale.FRENCH, "existing French name")
            .withLocalizedDescription(Locale.FRENCH, "existing French description")
            .withLocalizedConfirmationMessage(Locale.FRENCH, "")
            .withStatusDefinitions(
                new StatusDefinitions(ImmutableList.of(STATUS_WITH_EMAIL, STATUS_WITH_NO_EMAIL)))
            .build();

    LocalizationUpdate updateData =
        LocalizationUpdate.builder()
            .setLocalizedDisplayName("new French name")
            .setLocalizedDisplayDescription("new French description")
            .setLocalizedConfirmationMessage("")
            .setStatuses(
                ImmutableList.of(
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(
                            Optional.of(STATUS_WITH_EMAIL_FRENCH_NAME + "-updated"))
                        .setLocalizedEmailBody(
                            Optional.of(STATUS_WITH_EMAIL_FRENCH_EMAIL + "-updated"))
                        .build(),
                    LocalizationUpdate.StatusUpdate.builder()
                        .setStatusKeyToUpdate(STATUS_WITH_NO_EMAIL_ENGLISH_NAME)
                        .setLocalizedStatusText(
                            Optional.of(STATUS_WITH_NO_EMAIL_FRENCH_NAME + "-updated"))
                        .setLocalizedEmailBody(Optional.of("a localized email"))
                        .build()))
            .build();

    assertThatThrownBy(() -> ps.updateLocalization(program.id, Locale.FRENCH, updateData))
        .isInstanceOf(OutOfDateStatusesException.class);
  }

  @Test
  public void getNotificationEmailAddresses() {
    String programName = "administered program";
    Program program = resourceCreator.insertActiveProgram(programName);
    program.save();

    // If there are no admins (uncommon), return empty.
    assertThat(ps.getNotificationEmailAddresses(programName)).isEmpty();

    String globalAdminEmail = "global@admin";
    Account globalAdmin = new Account();
    globalAdmin.setEmailAddress(globalAdminEmail);
    globalAdmin.setGlobalAdmin(true);
    globalAdmin.save();

    // If there are no program admins, return global admins.
    assertThat(ps.getNotificationEmailAddresses(programName)).containsExactly(globalAdminEmail);

    String programAdminEmail = "program@admin";
    Account programAdmin = new Account();
    programAdmin.setEmailAddress(programAdminEmail);
    programAdmin.addAdministeredProgram(program.getProgramDefinition());
    programAdmin.save();

    // Return program admins when there are.
    assertThat(ps.getNotificationEmailAddresses(programName)).containsExactly(programAdminEmail);
  }

  @Test
  public void getProgramDefinitionAsync_reordersBlocksOnRead() throws Exception {
    long programId = ProgramBuilder.newActiveProgram().build().id;
    ImmutableList<BlockDefinition> unorderedBlockDefinitions =
        ImmutableList.<BlockDefinition>builder()
            .add(
                BlockDefinition.builder()
                    .setId(1L)
                    .setName("enumerator")
                    .setDescription("description")
                    .addQuestion(
                        ProgramQuestionDefinition.create(
                            testQuestionBank.applicantHouseholdMembers().getQuestionDefinition(),
                            Optional.of(programId)))
                    .build())
            .add(
                BlockDefinition.builder()
                    .setId(2L)
                    .setName("top level")
                    .setDescription("description")
                    .addQuestion(
                        ProgramQuestionDefinition.create(
                            testQuestionBank.applicantEmail().getQuestionDefinition(),
                            Optional.of(programId)))
                    .build())
            .add(
                BlockDefinition.builder()
                    .setId(3L)
                    .setName("nested enumerator")
                    .setDescription("description")
                    .setEnumeratorId(Optional.of(1L))
                    .addQuestion(
                        ProgramQuestionDefinition.create(
                            testQuestionBank.applicantHouseholdMemberJobs().getQuestionDefinition(),
                            Optional.of(programId)))
                    .build())
            .add(
                BlockDefinition.builder()
                    .setId(4L)
                    .setName("repeated")
                    .setDescription("description")
                    .setEnumeratorId(Optional.of(1L))
                    .addQuestion(
                        ProgramQuestionDefinition.create(
                            testQuestionBank.applicantHouseholdMemberName().getQuestionDefinition(),
                            Optional.of(programId)))
                    .build())
            .add(
                BlockDefinition.builder()
                    .setId(5L)
                    .setName("nested repeated")
                    .setDescription("description")
                    .setEnumeratorId(Optional.of(3L))
                    .addQuestion(
                        ProgramQuestionDefinition.create(
                            testQuestionBank
                                .applicantHouseholdMemberDaysWorked()
                                .getQuestionDefinition(),
                            Optional.of(programId)))
                    .build())
            .add(
                BlockDefinition.builder()
                    .setId(6L)
                    .setName("top level 2")
                    .setDescription("description")
                    .addQuestion(
                        ProgramQuestionDefinition.create(
                            testQuestionBank.applicantName().getQuestionDefinition(),
                            Optional.of(programId)))
                    .build())
            .build();
    ObjectMapper mapper =
        new ObjectMapper().registerModule(new GuavaModule()).registerModule(new Jdk8Module());

    // Directly update the table with DB.sqlUpdate and execute. We can't save it through
    // the ebean model because the preupdate method will correct block ordering, and we
    // want to test that legacy block order is corrected on read.
    String updateString =
        String.format(
            "UPDATE programs SET block_definitions='%s' WHERE id=%d;",
            mapper.writeValueAsString(unorderedBlockDefinitions), programId);
    DB.sqlUpdate(updateString).execute();

    ProgramDefinition found = ps.getProgramDefinitionAsync(programId).toCompletableFuture().get();

    assertThat(found.hasOrderedBlockDefinitions()).isTrue();
  }

  private static final StatusDefinitions.Status APPROVED_STATUS =
      StatusDefinitions.Status.builder()
          .setStatusText("Approved")
          .setLocalizedStatusText(LocalizedStrings.of(Locale.US, "Approved"))
          .setLocalizedEmailBodyText(Optional.of(LocalizedStrings.of(Locale.US, "I'm a US email!")))
          .setDefaultStatus(Optional.of(false))
          .build();

  private static final StatusDefinitions.Status APPROVED_DEFAULT_STATUS =
      StatusDefinitions.Status.builder()
          .setStatusText("Approved")
          .setLocalizedStatusText(LocalizedStrings.of(Locale.US, "Approved"))
          .setLocalizedEmailBodyText(Optional.of(LocalizedStrings.of(Locale.US, "I'm a US email!")))
          .setDefaultStatus(Optional.of(true))
          .build();

  private static final StatusDefinitions.Status REJECTED_STATUS =
      StatusDefinitions.Status.builder()
          .setStatusText("Rejected")
          .setLocalizedStatusText(LocalizedStrings.of(Locale.US, "Rejected"))
          .setLocalizedEmailBodyText(
              Optional.of(LocalizedStrings.of(Locale.US, "I'm a US rejection email!")))
          .setDefaultStatus(Optional.of(false))
          .build();

  private static final StatusDefinitions.Status REJECTED_DEFAULT_STATUS =
      StatusDefinitions.Status.builder()
          .setStatusText("Rejected")
          .setLocalizedStatusText(LocalizedStrings.of(Locale.US, "Rejected"))
          .setLocalizedEmailBodyText(
              Optional.of(LocalizedStrings.of(Locale.US, "I'm a US rejection email!")))
          .setDefaultStatus(Optional.of(true))
          .build();

  @Test
  public void appendStatus() throws Exception {
    // Also tests unsetDefaultStatus

    Program program = ProgramBuilder.newDraftProgram().build();
    assertThat(program.getStatusDefinitions().getStatuses()).isEmpty();

    final ErrorAnd<ProgramDefinition, CiviFormError> firstResult =
        ps.appendStatus(program.id, APPROVED_DEFAULT_STATUS);

    assertThat(firstResult.isError()).isFalse();
    assertThat(firstResult.getResult().statusDefinitions().getStatuses())
        .containsExactly(APPROVED_DEFAULT_STATUS);

    // Ensure that appending to a non-empty list actually appends.
    ErrorAnd<ProgramDefinition, CiviFormError> secondResult =
        ps.appendStatus(program.id, REJECTED_DEFAULT_STATUS);

    assertThat(secondResult.isError()).isFalse();
    assertThat(secondResult.getResult().statusDefinitions().getStatuses())
        .containsExactly(APPROVED_STATUS, REJECTED_DEFAULT_STATUS);
  }

  @Test
  public void appendStatus_programNotFound_throws() throws Exception {
    assertThatThrownBy(() -> ps.appendStatus(Long.MAX_VALUE, APPROVED_STATUS))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessageContaining("Program not found for ID:");
  }

  @Test
  public void appendStatus_duplicateStatus_throws() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(new StatusDefinitions(ImmutableList.of(APPROVED_STATUS)))
            .build();

    var newApprovedStatus =
        StatusDefinitions.Status.builder()
            .setStatusText(APPROVED_STATUS.statusText())
            .setLocalizedStatusText(LocalizedStrings.withDefaultValue(APPROVED_STATUS.statusText()))
            .setLocalizedEmailBodyText(
                Optional.of(LocalizedStrings.withDefaultValue("A new US email")))
            .build();

    DuplicateStatusException exc =
        catchThrowableOfType(
            () -> ps.appendStatus(program.id, newApprovedStatus), DuplicateStatusException.class);
    assertThat(exc.userFacingMessage()).contains("A status with name Approved already exists");
  }

  @Test
  public void editStatus() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(new StatusDefinitions(ImmutableList.of(APPROVED_STATUS)))
            .build();

    var editedStatus =
        StatusDefinitions.Status.builder()
            .setStatusText("New status text")
            .setLocalizedStatusText(LocalizedStrings.withDefaultValue("New status text"))
            .setLocalizedEmailBodyText(
                Optional.of(LocalizedStrings.withDefaultValue("A new US email")))
            .build();

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.editStatus(
            program.id,
            APPROVED_STATUS.statusText(),
            (existing) -> {
              assertThat(existing).isEqualTo(APPROVED_STATUS);
              return editedStatus;
            });
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().statusDefinitions().getStatuses()).containsExactly(editedStatus);
  }

  @Test
  public void editStatus_programNotFound_throws() throws Exception {
    assertThatThrownBy(
            () ->
                ps.editStatus(
                    Long.MAX_VALUE,
                    APPROVED_STATUS.statusText(),
                    (existing) -> {
                      fail("unexpected edit entry found");
                      throw new RuntimeException("unexpected edit entry found");
                    }))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessageContaining("Program not found for ID:");
  }

  @Test
  public void editStatus_updatedStatusIsDuplicate_throws() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(
                new StatusDefinitions(ImmutableList.of(APPROVED_STATUS, REJECTED_STATUS)))
            .build();

    // We update the "rejected" status entry so that it's text is the same as the
    // "approved" status entry.
    DuplicateStatusException exc =
        catchThrowableOfType(
            () ->
                ps.editStatus(
                    program.id,
                    REJECTED_STATUS.statusText(),
                    (existingStatus) -> {
                      return StatusDefinitions.Status.builder()
                          .setStatusText(APPROVED_STATUS.statusText())
                          .setLocalizedStatusText(
                              LocalizedStrings.withDefaultValue("New status text"))
                          .setLocalizedEmailBodyText(
                              Optional.of(LocalizedStrings.withDefaultValue("A new US email")))
                          .build();
                    }),
            DuplicateStatusException.class);
    assertThat(exc.userFacingMessage()).contains("A status with name Approved already exists");
  }

  @Test
  public void editStatus_missingStatus_returnsError() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(new StatusDefinitions(ImmutableList.of(APPROVED_STATUS)))
            .build();

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.editStatus(
            program.id,
            REJECTED_STATUS.statusText(),
            (existingStatus) -> {
              fail("unexpected edit entry found");
              throw new RuntimeException("unexpected edit entry found");
            });
    assertThat(result.hasResult()).isFalse();
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors()).hasSize(1);
    assertThat(result.getErrors())
        .containsExactly(
            CiviFormError.of(
                "The status being edited no longer exists and may have been modified in a"
                    + " separate window."));
  }

  @Test
  public void deleteStatus() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(
                new StatusDefinitions(ImmutableList.of(APPROVED_STATUS, REJECTED_STATUS)))
            .build();

    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.deleteStatus(program.id, APPROVED_STATUS.statusText());
    assertThat(result.isError()).isFalse();
    assertThat(result.getResult().statusDefinitions().getStatuses())
        .isEqualTo(ImmutableList.of(REJECTED_STATUS));
    assertThat(ps.getProgramDefinition(program.id).statusDefinitions().getStatuses())
        .isEqualTo(ImmutableList.of(REJECTED_STATUS));
  }

  @Test
  public void deleteStatus_programNotFound_throws() throws Exception {
    assertThatThrownBy(() -> ps.deleteStatus(Long.MAX_VALUE, APPROVED_STATUS.statusText()))
        .isInstanceOf(ProgramNotFoundException.class)
        .hasMessageContaining("Program not found for ID:");
  }

  @Test
  public void deleteStatus_missingStatus_returnsError() throws Exception {
    Program program =
        ProgramBuilder.newDraftProgram()
            .withStatusDefinitions(new StatusDefinitions(ImmutableList.of(APPROVED_STATUS)))
            .build();
    ErrorAnd<ProgramDefinition, CiviFormError> result =
        ps.deleteStatus(program.id, REJECTED_STATUS.statusText());
    assertThat(result.hasResult()).isFalse();
    assertThat(result.isError()).isTrue();
    assertThat(result.getErrors()).hasSize(1);
    assertThat(result.getErrors())
        .containsExactly(
            CiviFormError.of(
                "The status being deleted no longer exists and may have been deleted in a"
                    + " separate window."));
    assertThat(ps.getProgramDefinition(program.id).statusDefinitions().getStatuses())
        .isEqualTo(ImmutableList.of(APPROVED_STATUS));
  }

  @Test
  public void setProgramQuestionDefinitionAddressCorrectionEnabled_alreadyEnabled_throws()
      throws Exception {
    ProgramDefinition programDefinition =
        ProgramBuilder.newDraftProgram()
            .withBlock("screen one")
            .withQuestionDefinition(addressQuestion, false)
            .withQuestionDefinition(secondaryAddressQuestion, false)
            .buildDefinition();

    Long programId = programDefinition.id();
    Long blockDefinitionId = programDefinition.getLastBlockDefinition().id();
    ps.setProgramQuestionDefinitionAddressCorrectionEnabled(
        programId, blockDefinitionId, addressQuestion.getId(), true);
    assertThatExceptionOfType(ProgramQuestionDefinitionInvalidException.class)
        .isThrownBy(
            () ->
                ps.setProgramQuestionDefinitionAddressCorrectionEnabled(
                    programId, blockDefinitionId, secondaryAddressQuestion.getId(), true));
  }

  @Test
  public void setEligibilityIsGating() throws Exception {
    ProgramDefinition programDefinition = ProgramBuilder.newDraftProgram().buildDefinition();
    ProgramDefinition result =
        ps.setEligibilityIsGating(programDefinition.id(), /* gating= */ false);
    assertThat(result.eligibilityIsGating()).isFalse();
  }

  @Test
  public void getCommonIntakeForm_ignoresObsoletePrograms() {
    // No common intake form in the most recent version of any program, although some programs
    // were previously marked as common intake.
    ProgramBuilder.newObsoleteProgram("one")
        .withProgramType(ProgramType.COMMON_INTAKE_FORM)
        .build();
    ProgramBuilder.newActiveProgram("two").withProgramType(ProgramType.COMMON_INTAKE_FORM).build();
    ProgramBuilder.newDraftProgram("two").withProgramType(ProgramType.DEFAULT).build();

    assertThat(ps.getCommonIntakeForm()).isNotPresent();
  }

  @Test
  public void getCommonIntakeForm_activeCommonIntake() {
    ProgramBuilder.newObsoleteProgram("one")
        .withProgramType(ProgramType.COMMON_INTAKE_FORM)
        .build();
    ProgramBuilder.newActiveProgram("two").withProgramType(ProgramType.COMMON_INTAKE_FORM).build();

    assertThat(ps.getCommonIntakeForm()).isPresent();
    assertThat(ps.getCommonIntakeForm().get().adminName()).isEqualTo("two");
  }

  @Test
  public void getCommonIntakeForm_draftCommonIntake() {
    ProgramBuilder.newObsoleteProgram("one")
        .withProgramType(ProgramType.COMMON_INTAKE_FORM)
        .build();
    ProgramBuilder.newActiveProgram("two").withProgramType(ProgramType.DEFAULT).build();
    ProgramBuilder.newDraftProgram("two").withProgramType(ProgramType.COMMON_INTAKE_FORM).build();

    assertThat(ps.getCommonIntakeForm()).isPresent();
    assertThat(ps.getCommonIntakeForm().get().adminName()).isEqualTo("two");
  }
}
