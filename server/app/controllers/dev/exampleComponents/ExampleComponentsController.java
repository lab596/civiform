package controllers.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import services.DeploymentType;
import views.dev.exampleComponents.ExampleComponentsView;

/** Controller for rendering the Example Components View. */
public class ExampleComponentsController extends Controller {

  private final ExampleComponentsView exampleComponentsView;
  private final boolean isDevOrStaging;

  @Inject
  public ExampleComponentsController(
      ExampleComponentsView exampleComponentsView, DeploymentType deploymentType) {
    this.exampleComponentsView = checkNotNull(exampleComponentsView);
    this.isDevOrStaging = deploymentType.isDevOrStaging();
  }

  public Result index() {
    if (!isDevOrStaging) {
      return notFound();
    }
    return ok(exampleComponentsView.render());
  }
}
