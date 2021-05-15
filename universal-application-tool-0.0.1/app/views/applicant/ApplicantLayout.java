package views.applicant;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.nav;
import static j2html.TagCreator.span;

import auth.ProfileUtils;
import auth.Roles;
import auth.UatProfile;
import com.typesafe.config.Config;
import controllers.ti.routes;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import java.util.Optional;
import javax.inject.Inject;
import play.i18n.Messages;
import play.mvc.Http;
import play.twirl.api.Content;
import services.MessageKey;
import views.BaseHtmlLayout;
import views.HtmlBundle;
import views.ViewUtils;
import views.style.ApplicantStyles;
import views.style.BaseStyles;
import views.style.StyleUtils;
import views.style.Styles;

public class ApplicantLayout extends BaseHtmlLayout {
  private static final String CIVIFORM_TITLE = "CiviForm";

  private final ProfileUtils profileUtils;

  @Inject
  public ApplicantLayout(ViewUtils viewUtils, Config configuration, ProfileUtils profileUtils) {
    super(viewUtils, configuration);
    this.profileUtils = checkNotNull(profileUtils);
  }

  @Override
  public Content render(HtmlBundle bundle) {
    bundle.addBodyStyles(ApplicantStyles.BODY);
    bundle.addMainStyles(ApplicantStyles.MAIN);
    String currentTitle = bundle.getTitle();
    if (currentTitle != null && !currentTitle.isEmpty()) {
      bundle.setTitle(String.format("%s — %s", currentTitle, CIVIFORM_TITLE));
    } else {
      bundle.setTitle(CIVIFORM_TITLE);
    }
    return super.render(bundle);
  }

  public Content renderWithNav(Http.Request request, Messages messages, HtmlBundle bundle) {
    bundle.addHeaderContent(renderNavBar(request, messages));
    return render(bundle);
  }

  private ContainerTag renderNavBar(Http.Request request, Messages messages) {
    Optional<UatProfile> profile = profileUtils.currentUserProfile(request);
    return renderNavBar(profile, messages);
  }

  private ContainerTag renderNavBar(Optional<UatProfile> profile, Messages messages) {
    return nav()
        .withClasses(Styles.PT_8, Styles.PB_4, Styles.MB_12, Styles.FLEX, Styles.ALIGN_MIDDLE)
        .with(branding(), status(messages), maybeRenderTiButton(profile), logoutButton(messages));
  }

  private ContainerTag maybeRenderTiButton(Optional<UatProfile> profile) {
    if (profile.isPresent() && profile.get().getRoles().contains(Roles.ROLE_TI.toString())) {
      String tiDashboardText = "Trusted intermediary dashboard";
      String tiDashboardLink = routes.TrustedIntermediaryController.dashboard().url();
      return a(tiDashboardText)
          .withHref(tiDashboardLink)
          .withClasses(
              Styles.PX_3, Styles.TEXT_SM, Styles.OPACITY_75, StyleUtils.hover(Styles.OPACITY_100));
    }
    return div();
  }

  private ContainerTag branding() {
    return div()
        .withId("brand-id")
        .withClasses(Styles.W_1_2, ApplicantStyles.LOGO_STYLE)
        .withText("CiviForm");
  }

  private ContainerTag status(Messages messages) {
    return div()
        .withId("application-status")
        .withClasses(Styles.W_1_4, Styles.TEXT_RIGHT, Styles.TEXT_SM, Styles.UNDERLINE)
        .with(span(messages.at(MessageKey.LINK_VIEW_APPLICATIONS.getKeyName())));
  }

  private ContainerTag logoutButton(Messages messages) {
    String logoutLink = org.pac4j.play.routes.LogoutController.logout().url();
    return a(messages.at(MessageKey.BUTTON_LOGOUT.getKeyName()))
        .withHref(logoutLink)
        .withClasses(
            Styles.PX_3, Styles.TEXT_SM, Styles.OPACITY_75, StyleUtils.hover(Styles.OPACITY_100));
  }

  /**
   * Use this one when the application is already complete, to show a complete progress indicator.
   */
  protected ContainerTag renderProgramApplicationTitleAndProgressIndicator(String programTitle) {
    return renderProgramApplicationTitleAndProgressIndicator(programTitle, 0, 0);
  }

  protected ContainerTag renderProgramApplicationTitleAndProgressIndicator(
      String programTitle, int blockIndex, int totalBlockCount) {
    int percentComplete = getPercentComplete(blockIndex, totalBlockCount);

    ContainerTag progressInner =
        div()
            .withClasses(
                BaseStyles.BG_SEATTLE_BLUE,
                Styles.TRANSITION_ALL,
                Styles.DURATION_300,
                Styles.H_FULL,
                Styles.BLOCK,
                Styles.ABSOLUTE,
                Styles.LEFT_0,
                Styles.TOP_0,
                Styles.W_1,
                Styles.ROUNDED_FULL)
            .withStyle("width:" + percentComplete + "%");
    ContainerTag progressIndicator =
        div(progressInner)
            .withId("progress-indicator")
            .withClasses(
                Styles.BORDER,
                Styles.ROUNDED_FULL,
                Styles.FONT_SEMIBOLD,
                Styles.BG_WHITE,
                Styles.RELATIVE,
                Styles.H_4,
                Styles.MT_4);

    ContainerTag blockNumberTag = div();
    if (percentComplete != 100) {
      blockNumberTag
          .withText(String.format("%d of %d", blockIndex + 1, totalBlockCount))
          .withClasses(Styles.TEXT_GRAY_500, Styles.TEXT_RIGHT);
    }

    Tag programTitleDiv =
        div()
            .with(h2(programTitle).withClasses(ApplicantStyles.PROGRAM_TITLE_HEADING))
            .with(blockNumberTag)
            .withClasses(Styles.GRID, Styles.GRID_COLS_2);

    return div().with(programTitleDiv).with(progressIndicator);
  }

  /** Returns whole number out of 100 representing the completion percent of this program. */
  private int getPercentComplete(int blockIndex, int totalBlockCount) {
    if (totalBlockCount == 0) return 100;
    if (blockIndex == -1) return 0;

    // Add one to blockIndex for 1-based indexing, so that when applicant is on first block, we show
    // some amount of progress.
    // Add one to totalBlockCount so that when applicant is on the last block, we show that they're
    // still in progress. Save showing "100% completion" for the application review page.
    return (int) (((blockIndex + 1.0) / (totalBlockCount + 1.0)) * 100.0);
  }
}
