package services.question.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import services.CiviFormError;
import services.LocalizedStrings;
import services.TranslationNotFoundException;
import services.question.QuestionOption;
import services.question.exceptions.UnsupportedQuestionTypeException;
import services.question.types.AddressQuestionDefinition.AddressValidationPredicates;
import services.question.types.MultiOptionQuestionDefinition.MultiOptionQuestionType;
import services.question.types.MultiOptionQuestionDefinition.MultiOptionValidationPredicates;
import services.question.types.TextQuestionDefinition.TextValidationPredicates;

@RunWith(JUnitParamsRunner.class)
public class QuestionDefinitionTest {
  private QuestionDefinitionBuilder builder;

  @Before
  public void setup() {
    builder =
        new QuestionDefinitionBuilder()
            .setName("name")
            .setDescription("description")
            .setQuestionType(QuestionType.TEXT)
            .setQuestionText(LocalizedStrings.of(Locale.US, "question?"))
            .setQuestionHelpText(LocalizedStrings.of(Locale.US, "help text"))
            .setEntityType(LocalizedStrings.empty())
            .setValidationPredicates(TextValidationPredicates.builder().setMaxLength(128).build());
  }

  @Test
  public void testEquality_true() throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();
    assertThat(question.equals(question)).isTrue();
    QuestionDefinition sameQuestion = new QuestionDefinitionBuilder(question).build();
    assertThat(question.equals(sameQuestion)).isTrue();
  }

  @Test
  public void testEquality_nullReturnsFalse() throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();
    assertThat(question.equals(null)).isFalse();
  }

  @Test
  public void testEquality_differentPredicatesReturnsFalse()
      throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();

    QuestionDefinition differentQuestionPredicates =
        new QuestionDefinitionBuilder(question)
            .setValidationPredicates(TextValidationPredicates.builder().setMaxLength(1).build())
            .build();
    assertThat(question.equals(differentQuestionPredicates)).isFalse();
  }

  @Test
  public void testEquality_differentHelpTextReturnsFalse() throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();

    QuestionDefinition differentQuestionHelpText =
        new QuestionDefinitionBuilder(question)
            .setQuestionHelpText(LocalizedStrings.of(Locale.US, "different help text"))
            .build();
    assertThat(question.equals(differentQuestionHelpText)).isFalse();
  }

  @Test
  public void testEquality_differentTextReturnsFalse() throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();

    QuestionDefinition differentQuestionText =
        new QuestionDefinitionBuilder(question)
            .setQuestionText(LocalizedStrings.of(Locale.US, "question text?"))
            .build();
    assertThat(question.equals(differentQuestionText)).isFalse();
  }

  @Test
  public void testEquality_differentQuestionTypeReturnsFalse()
      throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();

    QuestionDefinition differentQuestionType =
        new QuestionDefinitionBuilder(question)
            .setQuestionType(QuestionType.ADDRESS)
            .setValidationPredicates(AddressValidationPredicates.create())
            .build();
    assertThat(question.equals(differentQuestionType)).isFalse();
  }

  @Test
  public void testEquality_differentIdReturnsFalse() throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();

    QuestionDefinition unpersistedQuestion = builder.clearId().build();
    assertThat(question.equals(unpersistedQuestion)).isFalse();

    QuestionDefinition differentId = builder.setId(456L).build();
    assertThat(question.equals(differentId)).isFalse();
  }

  @Test
  public void testEquality_differentNameReturnsFalse() throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();
    QuestionDefinition differentName = builder.setName("Different name").build();
    assertThat(question.equals(differentName)).isFalse();
  }

  @Test
  public void testEquality_differentDescriptionReturnsFalse()
      throws UnsupportedQuestionTypeException {
    QuestionDefinition question = builder.setId(123L).build();
    QuestionDefinition differentDescription =
        builder.setDescription("Different description").build();
    assertThat(question.equals(differentDescription)).isFalse();
  }

  @Test
  public void isEnumerator_false() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    assertThat(question.isEnumerator()).isFalse();
  }

  @Test
  public void isEnumerator_true() {
    QuestionDefinition question =
        new EnumeratorQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .build(),
            LocalizedStrings.empty());
    assertThat(question.isEnumerator()).isTrue();
  }

  @Test
  public void isRepeated_false() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    assertThat(question.isRepeated()).isFalse();
  }

  @Test
  public void isRepeated_true() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .setEnumeratorId(Optional.of(123L))
                .build());
    assertThat(question.isRepeated()).isTrue();
  }

  @Test
  public void newQuestionHasCorrectFields() throws Exception {
    QuestionDefinition question =
        new QuestionDefinitionBuilder()
            .setQuestionType(QuestionType.TEXT)
            .setId(123L)
            .setName("name")
            .setDescription("description")
            .setQuestionText(LocalizedStrings.of(Locale.US, "question?"))
            .setQuestionHelpText(LocalizedStrings.of(Locale.US, "help text"))
            .setValidationPredicates(TextValidationPredicates.builder().setMinLength(0).build())
            .build();

    assertThat(question.getId()).isEqualTo(123L);
    assertThat(question.getName()).isEqualTo("name");
    assertThat(question.getDescription()).isEqualTo("description");
    assertThat(question.getQuestionText().get(Locale.US)).isEqualTo("question?");
    assertThat(question.getQuestionHelpText().get(Locale.US)).isEqualTo("help text");
    assertThat(question.getValidationPredicates())
        .isEqualTo(TextValidationPredicates.builder().setMinLength(0).build());
  }

  @Test
  public void getQuestionTextForUnknownLocale_throwsException() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("text")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of(Locale.US, "not french"))
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    Throwable thrown = catchThrowable(() -> question.getQuestionText().get(Locale.FRANCE));

    assertThat(thrown).isInstanceOf(TranslationNotFoundException.class);
    assertThat(thrown).hasMessage("No translation was found for locale fr_FR");
  }

  @Test
  public void getQuestionHelpTextForUnknownLocale_throwsException() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("text")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.of(Locale.US, "help text"))
                .build());

    Throwable thrown = catchThrowable(() -> question.getQuestionHelpText().get(Locale.FRANCE));

    assertThat(thrown).isInstanceOf(TranslationNotFoundException.class);
    assertThat(thrown).hasMessage("No translation was found for locale fr_FR");
  }

  @Test
  public void getEmptyHelpTextForUnknownLocale_succeeds() throws TranslationNotFoundException {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("text")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());
    assertThat(question.getQuestionHelpText().get(Locale.FRANCE)).isEqualTo("");
  }

  @Test
  public void getQuestionTextOrDefault_returnsDefaultIfNotFound() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("text")
                .setDescription("")
                .setQuestionText(LocalizedStrings.withDefaultValue("default"))
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    assertThat(question.getQuestionText().getOrDefault(Locale.forLanguageTag("und")))
        .isEqualTo("default");
  }

  @Test
  public void getQuestionHelpTextOrDefault_returnsDefaultIfNotFound() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("text")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.withDefaultValue("default"))
                .build());

    assertThat(question.getQuestionHelpText().getOrDefault(Locale.forLanguageTag("und")))
        .isEqualTo("default");
  }

  @Test
  public void maybeGetQuestionText_returnsOptionalWithText() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of(Locale.US, "hello"))
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    assertThat(question.getQuestionText().maybeGet(Locale.US)).hasValue("hello");
  }

  @Test
  public void maybeGetQuestionText_returnsEmptyIfLocaleNotFound() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    assertThat(question.getQuestionText().maybeGet(Locale.forLanguageTag("und"))).isEmpty();
  }

  @Test
  public void maybeGetQuestionHelpText_returnsOptionalWithText() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.of(Locale.US, "world"))
                .build());

    assertThat(question.getQuestionHelpText().maybeGet(Locale.US)).hasValue("world");
  }

  @Test
  public void maybeGetQuestionHelpText_returnsEmptyIfLocaleNotFound() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    assertThat(question.getQuestionHelpText().maybeGet(Locale.forLanguageTag("und"))).isEmpty();
  }

  @Test
  public void newQuestionHasTypeText() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("text")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    assertThat(question.getQuestionType()).isEqualTo(QuestionType.TEXT);
  }

  @Test
  public void validateWellFormedQuestion_returnsNoErrors() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("text")
                .setDescription("description")
                .setQuestionText(LocalizedStrings.of(Locale.US, "question?"))
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());
    assertThat(question.validate()).isEmpty();
  }

  @Test
  public void validateBadQuestion_returnsErrors() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("")
                .setDescription("")
                .setQuestionText(LocalizedStrings.of())
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());
    assertThat(question.validate())
        .containsOnly(
            CiviFormError.of("Administrative identifier cannot be blank"),
            CiviFormError.of("Question text cannot be blank"));
  }

  @Test
  public void validate_withEnumerator_withEmptyEntityString_returnsErrors() throws Exception {
    QuestionDefinition question =
        new QuestionDefinitionBuilder()
            .setName("name")
            .setDescription("description")
            .setQuestionText(LocalizedStrings.withDefaultValue("text"))
            .setQuestionHelpText(LocalizedStrings.withDefaultValue("help text"))
            .setEntityType(LocalizedStrings.withDefaultValue(""))
            .setQuestionType(QuestionType.ENUMERATOR)
            .build();

    assertThat(question.validate())
        .containsOnly(CiviFormError.of("Enumerator question must have specified entity type"));
  }

  @Test
  public void validate_withRepeatedQuestion_missingEntityNameFormatString_returnsErrors()
      throws Exception {
    QuestionDefinition question =
        new QuestionDefinitionBuilder()
            .setName("name")
            .setDescription("description")
            .setQuestionText(LocalizedStrings.withDefaultValue("text"))
            .setQuestionHelpText(LocalizedStrings.withDefaultValue("help text"))
            .setEnumeratorId(Optional.of(1L))
            .setQuestionType(QuestionType.TEXT)
            .build();

    assertThat(question.validate())
        .containsOnly(
            CiviFormError.of(
                "Repeated questions must reference '$this' in the text and help text (if"
                    + " present)"));
  }

  @Test
  public void
      validate_withRepeatedQuestion_oneTranslationMissingEntityNameFormatString_returnsErrors()
          throws Exception {
    QuestionDefinition question =
        new QuestionDefinitionBuilder()
            .setName("name")
            .setDescription("description")
            .setQuestionText(
                LocalizedStrings.of(
                    Locale.US, "$this is present", Locale.FRANCE, "$this is also present"))
            .setQuestionHelpText(
                LocalizedStrings.of(
                    Locale.US, "$this is present", Locale.FRANCE, "this is not present"))
            .setEnumeratorId(Optional.of(1L))
            .setQuestionType(QuestionType.TEXT)
            .build();

    assertThat(question.validate())
        .containsOnly(
            CiviFormError.of(
                "Repeated questions must reference '$this' in the text and help text (if"
                    + " present)"));
  }

  @Test
  public void validate_withRepeatedQuestion_withNoHelpText_returnsNoErrors() throws Exception {
    QuestionDefinition question =
        new QuestionDefinitionBuilder()
            .setName("name")
            .setDescription("description")
            .setQuestionText(LocalizedStrings.withDefaultValue("something with $this"))
            .setEnumeratorId(Optional.of(1L))
            .setQuestionType(QuestionType.TEXT)
            .build();

    assertThat(question.validate()).isEmpty();
  }

  @Test
  public void validate_localeHasBlankText_returnsError() {
    QuestionDefinition question =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("test")
                .setDescription("test")
                .setQuestionText(LocalizedStrings.of(Locale.US, ""))
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());
    assertThat(question.validate()).containsOnly(CiviFormError.of("Question text cannot be blank"));
  }

  @Test
  public void validate_multiOptionQuestion_withoutOptions_returnsError() {
    QuestionDefinitionConfig config =
        QuestionDefinitionConfig.builder()
            .setName("test")
            .setDescription("test")
            .setQuestionText(LocalizedStrings.withDefaultValue("test"))
            .setQuestionHelpText(LocalizedStrings.empty())
            .build();
    QuestionDefinition question =
        new MultiOptionQuestionDefinition(
            config, /* questionOptions */ ImmutableList.of(), MultiOptionQuestionType.CHECKBOX);
    assertThat(question.validate())
        .containsOnly(CiviFormError.of("Multi-option questions must have at least one option"));
  }

  @Test
  public void validate_multiOptionQuestion_withBlankOption_returnsError() {
    QuestionDefinitionConfig config =
        QuestionDefinitionConfig.builder()
            .setName("test")
            .setDescription("test")
            .setQuestionText(LocalizedStrings.withDefaultValue("test"))
            .setQuestionHelpText(LocalizedStrings.empty())
            .build();
    QuestionDefinition question =
        new MultiOptionQuestionDefinition(
            config,
            ImmutableList.of(QuestionOption.create(1L, LocalizedStrings.withDefaultValue(""))),
            MultiOptionQuestionType.CHECKBOX);
    assertThat(question.validate())
        .containsOnly(CiviFormError.of("Multi-option questions cannot have blank options"));
  }

  @Test
  public void validate_multiOptionQuestion_withDuplicateOptions_returnsError() {
    QuestionDefinitionConfig config =
        QuestionDefinitionConfig.builder()
            .setName("test")
            .setDescription("test")
            .setQuestionText(LocalizedStrings.withDefaultValue("test"))
            .setQuestionHelpText(LocalizedStrings.empty())
            .build();
    ImmutableList<QuestionOption> questionOptions =
        ImmutableList.of(
            QuestionOption.create(1L, LocalizedStrings.withDefaultValue("a")),
            QuestionOption.create(2L, LocalizedStrings.withDefaultValue("a")));
    QuestionDefinition question =
        new MultiOptionQuestionDefinition(
            config, questionOptions, MultiOptionQuestionType.CHECKBOX);
    assertThat(question.validate())
        .containsOnly(CiviFormError.of("Multi-option question options must be unique"));
  }

  private static ImmutableList<Object[]> getMultiOptionQuestionValidationTestData() {
    return ImmutableList.of(
        // Valid cases.
        new Object[] {OptionalInt.empty(), OptionalInt.empty(), Optional.<String>empty()},
        new Object[] {OptionalInt.of(1), OptionalInt.empty(), Optional.<String>empty()},
        new Object[] {OptionalInt.empty(), OptionalInt.of(1), Optional.<String>empty()},
        new Object[] {OptionalInt.of(1), OptionalInt.of(2), Optional.<String>empty()},
        new Object[] {OptionalInt.of(1), OptionalInt.of(1), Optional.<String>empty()},

        // Edge cases.
        new Object[] {
          OptionalInt.of(-1),
          OptionalInt.empty(),
          Optional.of("Minimum number of choices required cannot be negative")
        },
        new Object[] {
          OptionalInt.empty(),
          OptionalInt.of(-1),
          Optional.of("Maximum number of choices allowed cannot be negative")
        },
        new Object[] {
          OptionalInt.of(2),
          OptionalInt.of(1),
          Optional.of(
              "Minimum number of choices required must be less than or equal to the maximum"
                  + " choices allowed")
        },
        new Object[] {
          OptionalInt.of(0), OptionalInt.of(0), Optional.of("Cannot require exactly 0 choices")
        },
        // Note: In the test code, we configure two options.
        new Object[] {
          OptionalInt.empty(),
          OptionalInt.of(3),
          Optional.of("Maximum number of choices allowed cannot exceed the number of options")
        },
        new Object[] {
          OptionalInt.of(3),
          OptionalInt.empty(),
          Optional.of("Minimum number of choices required cannot exceed the number of options")
        });
  }

  @Test
  @Parameters(method = "getMultiOptionQuestionValidationTestData")
  public void validate_multiOptionQuestion_validationConstraints(
      OptionalInt minChoicesRequired,
      OptionalInt maxChoicesAllowed,
      Optional<String> wantErrorMessage) {
    QuestionDefinitionConfig config =
        QuestionDefinitionConfig.builder()
            .setName("test")
            .setDescription("test")
            .setQuestionText(LocalizedStrings.withDefaultValue("test"))
            .setQuestionHelpText(LocalizedStrings.empty())
            .setValidationPredicates(
                MultiOptionValidationPredicates.builder()
                    .setMinChoicesRequired(minChoicesRequired)
                    .setMaxChoicesAllowed(maxChoicesAllowed)
                    .build())
            .build();
    ImmutableList<QuestionOption> questionOptions =
        ImmutableList.of(
            QuestionOption.create(1L, LocalizedStrings.withDefaultValue("a")),
            QuestionOption.create(2L, LocalizedStrings.withDefaultValue("b")));

    QuestionDefinition question =
        new MultiOptionQuestionDefinition(
            config, questionOptions, MultiOptionQuestionType.CHECKBOX);

    ImmutableSet<CiviFormError> errors = question.validate();
    if (wantErrorMessage.isEmpty()) {
      assertThat(errors).isEmpty();
    } else {
      assertThat(question.validate()).containsOnly(CiviFormError.of(wantErrorMessage.get()));
    }
  }

  @Test
  public void getSupportedLocales_onlyReturnsFullySupportedLocales() {
    QuestionDefinition definition =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("test")
                .setDescription("test")
                .setQuestionText(
                    LocalizedStrings.of(
                        Locale.US,
                        "question?",
                        Locale.forLanguageTag("es-US"),
                        "pregunta",
                        Locale.FRANCE,
                        "question"))
                .setQuestionHelpText(
                    LocalizedStrings.of(
                        Locale.US,
                        "help",
                        Locale.forLanguageTag("es-US"),
                        "ayuda",
                        Locale.GERMAN,
                        "Hilfe"))
                .build());

    assertThat(definition.getSupportedLocales())
        .containsExactly(Locale.US, Locale.forLanguageTag("es-US"));
  }

  @Test
  public void getSupportedLocales_emptyHelpText_returnsLocalesForQuestionText() {
    QuestionDefinition definition =
        new TextQuestionDefinition(
            QuestionDefinitionConfig.builder()
                .setName("test")
                .setDescription("test")
                .setQuestionText(
                    LocalizedStrings.of(
                        Locale.US,
                        "question?",
                        Locale.forLanguageTag("es-US"),
                        "pregunta",
                        Locale.FRANCE,
                        "question"))
                .setQuestionHelpText(LocalizedStrings.empty())
                .build());

    assertThat(definition.getSupportedLocales())
        .containsExactly(Locale.US, Locale.forLanguageTag("es-US"), Locale.FRANCE);
  }
}
