package controllers.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import services.DeploymentType;
import views.dev.exampleComponents.AccordionView;

/** Controller for rendering the Accordion View. */
public class AccordionController extends Controller {

  private final AccordionView accordionView;
  private final boolean isDevOrStaging;

  @Inject
  public AccordionController(AccordionView accordionView, DeploymentType deploymentType) {
    this.accordionView = checkNotNull(accordionView);
    this.isDevOrStaging = deploymentType.isDevOrStaging();
  }

  public Result index() {
    if (!isDevOrStaging) {
      return notFound();
    }
    return ok(accordionView.render());
  }
}
