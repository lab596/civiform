package views.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.hr;

import com.google.inject.Inject;
import j2html.tags.specialized.ATag;
import j2html.tags.specialized.DivTag;
import play.twirl.api.Content;
import views.BaseHtmlLayout;
import views.BaseHtmlView;
import views.HtmlBundle;
import views.JsBundle;
import views.components.LinkElement;

/** Renders a page of all style HTML components used in CiviForm */
public final class ExampleComponentsView extends BaseHtmlView {
  private final BaseHtmlLayout layout;

  @Inject
  public ExampleComponentsView(BaseHtmlLayout layout) {
    this.layout = checkNotNull(layout);
  }
  // FieldWithLabel
  // ButtonStyles - DONE
  // Accordion - DONE
  // Icons - link to http://localhost:9000/dev/icons
  // LinkElement
  // Modal - DONE
  // PathTag?
  // ProgramCardFactory
  // SelectWithLabel
  // SvgTag
  // TextFormatter ?
  // ToastMessage
  // Forms
  // Some things in ViewUtils.java
  // ApplicantLayout
  // AdminLayout
  // Also how work in styles files like BaseStyles
  // Checkboxes/ Multi-select
  // Dropdown menus (see admin program list, under kabob icon)
  // Radio buttons (see admin question settings)
  // Toggle switches (see admin program edit, under "questions", the "Optional" toggle)

  public Content render() {

    ATag buttonLink =
        new LinkElement()
            .setStyles("mb-4", "underline")
            .setText("Button Components ")
            .setHref("/dev/components/buttons")
            .asAnchorText();

    ATag modalLink =
        new LinkElement()
            .setStyles("mb-4", "underline")
            .setText("Modal Components")
            .setHref("/dev/components/modals")
            .asAnchorText();

    ATag accordionLink =
        new LinkElement()
            .setStyles("mb-4", "underline")
            .setText("Accordion Components")
            .setHref("/dev/components/accordions")
            .asAnchorText();

    ATag selectorLink =
        new LinkElement()
            .setStyles("mb-4", "underline")
            .setText("Selector Components")
            .setHref("/dev/components/selectors")
            .asAnchorText();

    DivTag content =
        div()
            .with(
                h1("Example Components").withClasses("mx-6", "my-8"),
                hr().withClasses("border", "border-seattle-blue", "border-double"),
                div()
                    .with(buttonLink, modalLink, accordionLink, selectorLink)
                    .withClasses("flex", "flex-col", "m-12"));

    // CreateQuestionButton.renderCreateQuestionButton(
    // "www.google.com", /* isPrimaryButton= */ false, false);

    HtmlBundle bundle =
        layout
            .getBundle()
            .setTitle("Example Components")
            .addMainContent(content)
            .setJsBundle(JsBundle.ADMIN);
    return layout.render(bundle);
  }

  // Utility method for making the header used on all example component views
  static DivTag buildHeader(String title) {
    return div()
        .with(
            h1(title).withClasses("mx-6", "my-8"),
            hr().withClasses("border", "border-seattle-blue", "border-double"));
  }
}
