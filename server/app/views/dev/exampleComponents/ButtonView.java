package views.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.div;

import com.google.inject.Inject;
import j2html.tags.specialized.ButtonTag;
import j2html.tags.specialized.DivTag;
import play.twirl.api.Content;
import views.BaseHtmlLayout;
import views.BaseHtmlView;
import views.HtmlBundle;
import views.JsBundle;
import views.components.ButtonStyles;
import views.components.Icons;

/** Renders a page with examples of all buttons used in CiviForm */
public final class ButtonView extends BaseHtmlView {
  private final BaseHtmlLayout layout;

  @Inject
  public ButtonView(BaseHtmlLayout layout) {
    this.layout = checkNotNull(layout);
  }

  public Content render() {

    DivTag buttons = buildButtons();

    DivTag content = div().with(ExampleComponentsView.buildHeader("Buttons"), buttons);

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

  private static DivTag buildButtons() {
    ButtonTag solidWhiteBtn = button("SOLID WHITE").withClasses(ButtonStyles.SOLID_WHITE);
    ButtonTag solidBlueBtn = button("SOLID BLUE").withClasses(ButtonStyles.SOLID_BLUE);
    ButtonTag solidBlueWithIconBtn =
        makeSvgTextButton("SOLID BLUE WITH ICON", Icons.DELETE)
            .withClasses(ButtonStyles.SOLID_BLUE_WITH_ICON);
    ButtonTag solidBlueTextSmBtn =
        button("SOLID BLUE TEXT SM").withClasses(ButtonStyles.SOLID_BLUE_TEXT_SM);
    ButtonTag solidBlueTextXlBtn =
        button("SOLID BLUE TEXT XL").withClasses(ButtonStyles.SOLID_BLUE_TEXT_XL);
    ButtonTag outlinedTransparentBtn =
        button("OUTLINED TRANSPARENT").withClasses(ButtonStyles.OUTLINED_TRANSPARENT);
    ButtonTag outlinedWhiteWithIconBtn =
        makeSvgTextButton("OUTLINED WHITE WITH ICON", Icons.DELETE)
            .withClasses(ButtonStyles.OUTLINED_WHITE_WITH_ICON);
    ButtonTag clearWithIconBtn =
        makeSvgTextButton("CLEAR WITH ICON", Icons.DELETE)
            .withClasses(ButtonStyles.CLEAR_WITH_ICON);
    ButtonTag clearWithIconForDropdownBtn =
        makeSvgTextButton("CLEAR WITH ICON FOR DROPDOWN", Icons.DELETE)
            .withClasses(ButtonStyles.CLEAR_WITH_ICON_FOR_DROPDOWN);
    return div()
        .withClasses("space-y-5", "m-12")
        .with(
            solidWhiteBtn,
            solidBlueBtn,
            solidBlueWithIconBtn,
            solidBlueTextSmBtn,
            solidBlueTextXlBtn,
            outlinedTransparentBtn,
            outlinedWhiteWithIconBtn,
            clearWithIconBtn,
            clearWithIconForDropdownBtn);
  }
}
