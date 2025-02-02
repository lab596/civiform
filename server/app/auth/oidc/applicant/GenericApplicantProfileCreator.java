package auth.oidc.applicant;

import auth.ProfileFactory;
import com.google.common.collect.ImmutableList;
import javax.inject.Provider;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import repository.UserRepository;

/**
 * This class takes an existing CiviForm profile and augments it with the information from an AD
 * profile. Right now this is only extracting the email address, since that is all that AD provides
 * right now.
 */
public class GenericApplicantProfileCreator extends ApplicantProfileCreator {
  public GenericApplicantProfileCreator(
      OidcConfiguration configuration,
      OidcClient client,
      ProfileFactory profileFactory,
      Provider<UserRepository> accountRepositoryProvider,
      String emailAttributeName,
      String localeAttributeName,
      ImmutableList<String> nameAttributeNames) {
    super(
        configuration,
        client,
        profileFactory,
        accountRepositoryProvider,
        emailAttributeName,
        localeAttributeName,
        nameAttributeNames);
  }
}
