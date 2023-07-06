package views.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.div;
import static j2html.TagCreator.p;

import com.google.inject.Inject;
import j2html.tags.specialized.ButtonTag;
import j2html.tags.specialized.DivTag;
import play.twirl.api.Content;
import views.BaseHtmlLayout;
import views.BaseHtmlView;
import views.HtmlBundle;
import views.components.ButtonStyles;
import views.components.Modal;

/** Renders a page with examples of modals used in CiviForm */
public final class ModalView extends BaseHtmlView {

  private final BaseHtmlLayout layout;

  @Inject
  public ModalView(BaseHtmlLayout layout) {
    this.layout = checkNotNull(layout);
  }

  public Content render() {
    HtmlBundle bundle =
        layout.getBundle().setTitle("Modal Component").addMainContent(contentDiv).addModals(modal);
    return layout.render(bundle);
  }

  Modal modal = makeModal();

  DivTag contentDiv =
      div()
          .with(
              ExampleComponentsView.buildHeader("Modals"),
              div()
                  .withClasses("m-12")
                  .with(
                      modal
                          .getButton()
                          .withClasses(ButtonStyles.OUTLINED_WHITE_WITH_ICON, "my-2", "p-12")));

  private Modal makeModal() {
    DivTag content = div().with(p("CiviForm is fun!"));
    ButtonTag triggerButton = button("Open Modal").withClasses(ButtonStyles.SOLID_BLUE);

    return Modal.builder()
        .setModalId(Modal.randomModalId())
        .setContent(content)
        .setModalTitle("Example Modal")
        .setTriggerButtonContent(triggerButton)
        .build();
  }
}
