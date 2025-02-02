package controllers.applicant;

import static org.assertj.core.api.Assertions.assertThat;
import static play.api.test.CSRFTokenHelper.addCSRFToken;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.stubMessagesApi;

import com.google.common.collect.ImmutableMap;
import controllers.WithMockedProfiles;
import java.util.Locale;
import models.Applicant;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import repository.UserRepository;

public class ApplicantInformationControllerTest extends WithMockedProfiles {

  private Applicant currentApplicant;
  private UserRepository userRepository;
  private ApplicantInformationController controller;

  @Before
  public void setup() {
    resetDatabase();
    controller = instanceOf(ApplicantInformationController.class);
    userRepository = instanceOf(UserRepository.class);
    currentApplicant = createApplicantWithMockedProfile();
  }

  @Test
  public void setLangFromBrowser_differentApplicant_returnsUnauthorizedResult() {
    Result result =
        controller
            .setLangFromBrowser(fakeRequest().build(), currentApplicant.id + 1)
            .toCompletableFuture()
            .join();
    assertThat(result.status()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  public void setLangFromBrowser_updatesLanguageCode_usingRequestHeaders() {
    Http.Request request =
        addCSRFToken(
                fakeRequest(
                        routes.ApplicantInformationController.setLangFromBrowser(
                            currentApplicant.id))
                    .header("Accept-Language", "es-US"))
            .build();

    Result result =
        controller.setLangFromBrowser(request, currentApplicant.id).toCompletableFuture().join();

    currentApplicant =
        userRepository.lookupApplicant(currentApplicant.id).toCompletableFuture().join().get();
    assertThat(currentApplicant.getApplicantData().preferredLocale())
        .isEqualTo(Locale.forLanguageTag("es-US"));
    assertThat(result.status()).isEqualTo(SEE_OTHER);
    assertThat(result.cookie("PLAY_LANG").get().value()).isEqualTo("es-US");
  }

  @Test
  public void setLangFromSwitcher_differentApplicant_returnsUnauthorizedResult() {
    Result result =
        controller
            .setLangFromSwitcher(fakeRequest().build(), currentApplicant.id + 1)
            .toCompletableFuture()
            .join();
    assertThat(result.status()).isEqualTo(UNAUTHORIZED);
  }

  @Test
  public void setLangFromSwitcher_redirectsToProgramIndex_withNonEnglishLocale() {
    Http.Request request =
        addCSRFToken(
                fakeRequest(
                        routes.ApplicantInformationController.setLangFromSwitcher(
                            currentApplicant.id))
                    .bodyForm(ImmutableMap.of("locale", "es-US")))
            .build();

    Result result =
        controller.setLangFromSwitcher(request, currentApplicant.id).toCompletableFuture().join();

    currentApplicant =
        userRepository.lookupApplicant(currentApplicant.id).toCompletableFuture().join().get();
    assertThat(currentApplicant.getApplicantData().preferredLocale())
        .isEqualTo(Locale.forLanguageTag("es-US"));
    assertThat(result.status()).isEqualTo(SEE_OTHER);
    assertThat(result.cookie("PLAY_LANG").get().value()).isEqualTo("es-US");
  }

  @Test
  public void setLangFromSwitcher_ignoresExistingLangCookie() {
    Http.Request request =
        addCSRFToken(
                fakeRequest(
                        routes.ApplicantInformationController.setLangFromSwitcher(
                            currentApplicant.id))
                    .bodyForm(ImmutableMap.of("locale", "es-US")))
            .langCookie(Locale.US, stubMessagesApi())
            .build();

    Result result =
        controller.setLangFromSwitcher(request, currentApplicant.id).toCompletableFuture().join();

    currentApplicant =
        userRepository.lookupApplicant(currentApplicant.id).toCompletableFuture().join().get();
    assertThat(currentApplicant.getApplicantData().preferredLocale())
        .isEqualTo(Locale.forLanguageTag("es-US"));
    assertThat(result.status()).isEqualTo(SEE_OTHER);
    assertThat(result.cookie("PLAY_LANG").get().value()).isEqualTo("es-US");
  }
}
