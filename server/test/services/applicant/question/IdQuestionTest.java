package services.applicant.question;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalLong;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import models.Applicant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.i18n.Lang;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import repository.ResetPostgres;
import services.LocalizedStrings;
import services.Path;
import services.applicant.ApplicantData;
import services.applicant.ValidationErrorMessage;
import services.question.QuestionAnswerer;
import services.question.types.IdQuestionDefinition;
import services.question.types.QuestionDefinitionConfig;

@RunWith(JUnitParamsRunner.class)
public class IdQuestionTest extends ResetPostgres {
  private static final IdQuestionDefinition idQuestionDefinition =
      new IdQuestionDefinition(
          QuestionDefinitionConfig.builder()
              .setName("question name")
              .setDescription("description")
              .setQuestionText(LocalizedStrings.of(Locale.US, "question?"))
              .setQuestionHelpText(LocalizedStrings.of(Locale.US, "help text"))
              .setId(OptionalLong.of(1))
              .setLastModifiedTime(Optional.empty())
              .build());

  private static final IdQuestionDefinition minAndMaxLengthIdQuestionDefinition =
      new IdQuestionDefinition(
          QuestionDefinitionConfig.builder()
              .setName("question name")
              .setDescription("description")
              .setQuestionText(LocalizedStrings.of(Locale.US, "question?"))
              .setQuestionHelpText(LocalizedStrings.of(Locale.US, "help text"))
              .setValidationPredicates(IdQuestionDefinition.IdValidationPredicates.create(3, 4))
              .setId(OptionalLong.of(1))
              .setLastModifiedTime(Optional.empty())
              .build());
  ;

  private Applicant applicant;
  private ApplicantData applicantData;
  private Messages messages;

  @Before
  public void setUp() {
    applicant = new Applicant();
    applicantData = applicant.getApplicantData();
    messages = instanceOf(MessagesApi.class).preferred(ImmutableList.of(Lang.defaultLang()));
  }

  @Test
  public void withEmptyApplicantData() {
    ApplicantQuestion applicantQuestion =
        new ApplicantQuestion(idQuestionDefinition, applicantData, Optional.empty());

    IdQuestion idQuestion = new IdQuestion(applicantQuestion);

    assertThat(idQuestion.getValidationErrors().isEmpty()).isTrue();
  }

  @Test
  public void withApplicantData_passesValidation() {
    ApplicantQuestion applicantQuestion =
        new ApplicantQuestion(idQuestionDefinition, applicantData, Optional.empty());
    QuestionAnswerer.answerIdQuestion(
        applicantData, applicantQuestion.getContextualizedPath(), "12345");

    IdQuestion idQuestion = new IdQuestion(applicantQuestion);

    assertThat(idQuestion.getIdValue().get()).isEqualTo("12345");
    assertThat(idQuestion.getValidationErrors().isEmpty()).isTrue();
  }

  @Test
  @Parameters({"012", "0123"})
  public void withMinAndMaxLength_withValidApplicantData_passesValidation(String value) {
    ApplicantQuestion applicantQuestion =
        new ApplicantQuestion(minAndMaxLengthIdQuestionDefinition, applicantData, Optional.empty());
    QuestionAnswerer.answerIdQuestion(
        applicantData, applicantQuestion.getContextualizedPath(), value);

    IdQuestion idQuestion = new IdQuestion(applicantQuestion);

    assertThat(idQuestion.getIdValue().get()).isEqualTo(value);
    assertThat(idQuestion.getValidationErrors().isEmpty()).isTrue();
  }

  @Test
  @Parameters({
    ",Must contain at least 3 characters.",
    "1,Must contain at least 3 characters.",
    "12334,Must contain at most 4 characters.",
    "abc,Must contain only numbers."
  })
  public void withMinAndMaxLength_withInvalidApplicantData_failsValidation(
      String value, String expectedErrorMessage) {
    ApplicantQuestion applicantQuestion =
        new ApplicantQuestion(minAndMaxLengthIdQuestionDefinition, applicantData, Optional.empty());
    QuestionAnswerer.answerIdQuestion(
        applicantData, applicantQuestion.getContextualizedPath(), value);

    IdQuestion idQuestion = new IdQuestion(applicantQuestion);

    if (idQuestion.getIdValue().isPresent()) {
      assertThat(idQuestion.getIdValue().get()).isEqualTo(value);
    }
    ImmutableMap<Path, ImmutableSet<ValidationErrorMessage>> validationErrors =
        idQuestion.getValidationErrors();
    assertThat(validationErrors.size()).isEqualTo(1);
    ImmutableSet<ValidationErrorMessage> idErrors =
        validationErrors.getOrDefault(idQuestion.getIdPath(), ImmutableSet.of());
    assertThat(idErrors.size()).isEqualTo(1);
    String errorMessage = idErrors.iterator().next().getMessage(messages);
    assertThat(errorMessage).isEqualTo(expectedErrorMessage);
  }
}
