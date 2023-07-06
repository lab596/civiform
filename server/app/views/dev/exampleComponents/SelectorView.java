package views.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.legend;

import com.google.inject.Inject;
import forms.QuestionForm;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.LabelTag;
import play.twirl.api.Content;
import views.BaseHtmlLayout;
import views.BaseHtmlView;
import views.HtmlBundle;
import views.JsBundle;
import views.components.FieldWithLabel;
import views.style.BaseStyles;

/**
 * Renders a page with examples of all selector components, namely radio buttons, dropdowns,
 * checkboxes and toggle switches, used in CiviForm
 */
public class SelectorView extends BaseHtmlView {

  private final BaseHtmlLayout layout;

  @Inject
  public SelectorView(BaseHtmlLayout layout) {
    this.layout = checkNotNull(layout);
  }

  public Content render() {

    String selected = "";

    DivTag content =
        div()
            .with(
                ExampleComponentsView.buildHeader("Selectors"),
                div()
                    .withClass("m-12")
                    .with(
                        h2("Radio Buttons"),
                        div()
                            .withClass("m-12")
                            .with(
                                legend("What is your favorite thing about Civiform?")
                                    .withClass(BaseStyles.INPUT_LABEL),
                                buildRadioOption(
                                    "One place",
                                    "Applying to multiple programs in one place",
                                    selected),
                                buildRadioOption(
                                    "Eligibility",
                                    "Seeing what programs I'm eligible for",
                                    selected),
                                buildRadioOption("Color", "The color scheme", selected))));

    HtmlBundle bundle =
        layout
            .getBundle()
            .setTitle("Selectors")
            .addMainContent(content)
            .setJsBundle(JsBundle.ADMIN);
    return layout.render(bundle);
  }

  /**
   * Mimics the radio buttons here: {@link
   * views.admin.questions.QuestionEditView#buildDemographicFields(QuestionForm, boolean)}}.
   */
  private LabelTag buildRadioOption(String value, String optionLabel, String selected) {
    return FieldWithLabel.radio()
        .setDisabled(false)
        .setAriaRequired(true)
        .setFieldName("favoriteCiviFormFeature")
        .setLabelText(optionLabel)
        .setValue(value)
        .setChecked(value.equals(selected))
        .getRadioTag();
  }

  //  private DivTag buildRadioOption(String optionText, int optionValue, boolean checked,
  //                                  boolean hasErrors, boolean isOptional) {
  //    LabelTag labelTag =
  //      label()
  //        .withFor("radio")
  //        .with(span(optionText).withClasses(ReferenceClasses.MULTI_OPTION_VALUE));
  //    InputTag inputTag =
  //      input()
  //        .withId("radio")
  //        .withType("radio")
  //        .withValue(String.valueOf(optionValue))
  //        .withCondChecked(checked)
  //        .condAttr(hasErrors, "aria-invalid", "true")
  //        .condAttr(!isOptional, "aria-required", "true")
  //        .withClasses(StyleUtils.joinStyles(ReferenceClasses.RADIO_INPUT, BaseStyles.RADIO));
  //
  //    return div()
  //      .withClasses(
  //        "my-2",
  //        "relative",
  //        ReferenceClasses.MULTI_OPTION_QUESTION_OPTION,
  //        ReferenceClasses.RADIO_OPTION,
  //        BaseStyles.RADIO_LABEL,
  //        checked ? BaseStyles.BORDER_SEATTLE_BLUE : "")
  //      .with(inputTag)
  //      .with(labelTag);
  //  }

}
