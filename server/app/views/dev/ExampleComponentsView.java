package views.dev;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import j2html.tags.specialized.DivTag;
import play.twirl.api.Content;
import views.BaseHtmlLayout;
import views.BaseHtmlView;
import views.HtmlBundle;
import views.JsBundle;
import views.components.CreateQuestionButton;

/** Renders a page of all style HTML components used in CiviForm */
public final class ExampleComponentsView extends BaseHtmlView {
  private final BaseHtmlLayout layout;

  @Inject
  public ExampleComponentsView(BaseHtmlLayout layout) {
    this.layout = checkNotNull(layout);
  }

  public Content render() {
    // FieldWithLabel
    // ButtonStyles
    // Accordion
    // Icons
    // LinkElement
    // Modal
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

    DivTag content =
        CreateQuestionButton.renderCreateQuestionButton(
            "www.google.com", /* isPrimaryButton= */ false, false);
    HtmlBundle bundle =
        layout
            .getBundle()
            .setTitle("Example Components")
            .addMainContent(content)
            .setJsBundle(JsBundle.ADMIN);
    return layout.render(bundle);
  }
}
