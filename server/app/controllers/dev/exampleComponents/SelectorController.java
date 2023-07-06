package controllers.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import services.DeploymentType;
import views.dev.exampleComponents.SelectorView;

/** Controller for rendering the Selector View. */
public class SelectorController extends Controller {

  private final SelectorView selectorView;
  private final boolean isDevOrStaging;

  @Inject
  public SelectorController(SelectorView selectorView, DeploymentType deploymentType) {
    this.selectorView = checkNotNull(selectorView);
    this.isDevOrStaging = deploymentType.isDevOrStaging();
  }

  public Result index() {
    if (!isDevOrStaging) {
      return notFound();
    }
    return ok(selectorView.render());
  }
}
