package controllers.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import services.DeploymentType;
import views.dev.exampleComponents.ButtonView;

/** Controller for rendering the Button View. */
public class ButtonController extends Controller {

  private final ButtonView buttonView;
  private final boolean isDevOrStaging;

  @Inject
  public ButtonController(ButtonView buttonView, DeploymentType deploymentType) {
    this.buttonView = checkNotNull(buttonView);
    this.isDevOrStaging = deploymentType.isDevOrStaging();
  }

  public Result index() {
    if (!isDevOrStaging) {
      return notFound();
    }
    return ok(buttonView.render());
  }
}
