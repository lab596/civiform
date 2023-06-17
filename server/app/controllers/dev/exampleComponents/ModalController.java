package controllers.dev.exampleComponents;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import services.DeploymentType;
import views.dev.exampleComponents.ModalView;

/** Controller for rendering the Modal View. */
public class ModalController extends Controller {

  private final ModalView modalView;
  private final boolean isDevOrStaging;

  @Inject
  public ModalController(ModalView modalView, DeploymentType deploymentType) {
    this.modalView = checkNotNull(modalView);
    this.isDevOrStaging = deploymentType.isDevOrStaging();
  }

  public Result index() {
    if (!isDevOrStaging) {
      return notFound();
    }
    return ok(modalView.render());
  }
}
