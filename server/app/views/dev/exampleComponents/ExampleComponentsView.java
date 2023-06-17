package views.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.*;

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
  // Dropdown menus
  // Radio buttons

  public Content render() {

    ATag buttonLink =
        new LinkElement()
            .setStyles("mb-4", "underline")
            .setText("Button Component")
            .setHref("/dev/components/buttons")
            .asAnchorText();

    ATag modalLink =
        new LinkElement()
            .setStyles("mb-4", "underline")
            .setText("Modal Component")
            .setHref("/dev/components/modals")
            .asAnchorText();

    ATag accordionLink =
        new LinkElement()
            .setStyles("mb-4", "underline")
            .setText("Accordion Component")
            .setHref("/dev/components/accordions")
            .asAnchorText();

    DivTag content =
        div()
            .with(
                h1("Example Components").withClasses("mx-6", "my-8"),
                hr().withClasses("border", "border-seattle-blue", "border-double"),
                div()
                    .with(buttonLink, modalLink, accordionLink)
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
}
