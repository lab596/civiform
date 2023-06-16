package views.dev;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.div;

import com.google.inject.Inject;
import j2html.tags.specialized.ButtonTag;
import j2html.tags.specialized.DivTag;
import java.util.ArrayList;
import play.twirl.api.Content;
import views.BaseHtmlLayout;
import views.BaseHtmlView;
import views.HtmlBundle;
import views.JsBundle;
import views.components.Accordion;
import views.components.ButtonStyles;
import views.components.Icons;

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
    // Also how work in styles files like BaseStyles

  public Content render() {

    ArrayList<String> accordionContent = new ArrayList<String>();
    accordionContent.add("Accordion Item 1");
    accordionContent.add("Accordion Item 2");
    accordionContent.add("Accordion Item 3");

    DivTag buttons = buildButtons();


    DivTag content =
        div()
            .with(buildAccordion("Accordion Element Title", accordionContent))
            .with(buttons);

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

  private static DivTag buildAccordion(String title, ArrayList<String> accordionContent) {
    Accordion accordion = new Accordion().setTitle(title);
    accordionContent.forEach(item -> accordion.addContent(div(item)));
    return accordion.getContainer();
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
        .with(solidWhiteBtn)
        .with(solidBlueBtn)
        .with(solidBlueWithIconBtn)
        .with(solidBlueTextSmBtn)
        .with(solidBlueTextXlBtn)
        .with(outlinedTransparentBtn)
        .with(outlinedWhiteWithIconBtn)
        .with(clearWithIconBtn)
        .with(clearWithIconForDropdownBtn);
  }

}
