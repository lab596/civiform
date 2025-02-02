package controllers.dev.seeding;

import static org.assertj.core.api.Assertions.assertThat;
import static play.api.test.CSRFTokenHelper.addCSRFToken;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;

import java.util.Optional;
import org.junit.After;
import org.junit.Test;
import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.Helpers;

public class DevDatabaseSeedControllerTest {

  private Optional<Application> maybeApp = Optional.empty();
  private DevDatabaseSeedController controller;

  @After
  public void stopApplication() {
    if (maybeApp.isPresent()) {
      Helpers.stop(maybeApp.get());
      maybeApp = Optional.empty();
    }
  }

  @Test
  public void seedAndClearDatabase_displaysCorrectInformation() {
    setupControllerInMode(Mode.DEV);
    // Navigate to index before seeding - should not have the fake program.
    Result result = controller.index(addCSRFToken(fakeRequest()).build());
    assertThat(result.status()).isEqualTo(OK);
    assertThat(contentAsString(result)).doesNotContain("comprehensive-sample-program");

    // Seed the fake data.
    result = controller.seedPrograms();
    assertThat(result.redirectLocation()).hasValue(routes.DevDatabaseSeedController.index().url());
    assertThat(result.flash().get("success")).hasValue("The database has been seeded");
    result = controller.index(addCSRFToken(fakeRequest()).build());
    assertThat(result.status()).isEqualTo(OK);
    assertThat(contentAsString(result)).contains("comprehensive-sample-program");

    // Clear the data.
    result = controller.clear();
    assertThat(result.redirectLocation()).hasValue(routes.DevDatabaseSeedController.index().url());
    assertThat(result.flash().get("success")).hasValue("The database has been cleared");
    result = controller.index(addCSRFToken(fakeRequest()).build());
    assertThat(result.status()).isEqualTo(OK);
    assertThat(contentAsString(result)).doesNotContain("comprehensive-sample-program");
  }

  @Test
  public void index_inNonDevMode_returnsNotFound() {
    setupControllerInMode(Mode.TEST);
    Result result = controller.index(fakeRequest().build());

    assertThat(result.status()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void seedPrograms_inNonDevMode_returnsNotFound() {
    setupControllerInMode(Mode.TEST);
    Result result = controller.seedPrograms();

    assertThat(result.status()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void clear_inNonDevMode_returnsNotFound() {
    setupControllerInMode(Mode.TEST);
    Result result = controller.clear();

    assertThat(result.status()).isEqualTo(NOT_FOUND);
  }

  private void setupControllerInMode(Mode mode) {
    maybeApp = Optional.of(new GuiceApplicationBuilder().in(mode).build());
    controller = maybeApp.get().injector().instanceOf(DevDatabaseSeedController.class);
  }
}
