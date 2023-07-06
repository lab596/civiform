package views.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.div;

import com.google.inject.Inject;
import j2html.tags.specialized.DivTag;
import java.util.ArrayList;
import play.twirl.api.Content;
import views.BaseHtmlLayout;
import views.BaseHtmlView;
import views.HtmlBundle;
import views.JsBundle;
import views.components.Accordion;

/** Renders a page with examples of all accordion components used in CiviForm */
public class AccordionView extends BaseHtmlView {

  private final BaseHtmlLayout layout;

  @Inject
  public AccordionView(BaseHtmlLayout layout) {
    this.layout = checkNotNull(layout);
  }

  public Content render() {

    ArrayList<String> accordionContent = new ArrayList<String>();
    accordionContent.add("Accordion Item 1");
    accordionContent.add("Accordion Item 2");
    accordionContent.add("Accordion Item 3");

    DivTag content =
        div()
            .with(
                ExampleComponentsView.buildHeader("Accordion"),
                div()
                    .withClass("m-12")
                    .with(buildAccordion("Accordion Element Title", accordionContent)));

    HtmlBundle bundle =
        layout
            .getBundle()
            .setTitle("Accordion")
            .addMainContent(content)
            .setJsBundle(JsBundle.ADMIN);
    return layout.render(bundle);
  }

  private static DivTag buildAccordion(String title, ArrayList<String> accordionContent) {
    Accordion accordion = new Accordion().setTitle(title);
    accordionContent.forEach(item -> accordion.addContent(div(item)));
    return accordion.getContainer();
  }
}
