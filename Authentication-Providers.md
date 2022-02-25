# Supported Authentication Providers

This page will go over the implementation and configuration steps for currently supported authentication providers. A Civiform deployment should have exactly one admin authentication provider and one applicant authentication provider configured.


## Admin Authentication

### ADFS (OIDC)

### Azure AD (OIDC)

## Applicant Authentication

### Oracle IDCS (OIDC)

### LoginRadius (SAML)
SAML authentication involves an exchange between an Identity Provider or IdP(LoginRadius), and a Service Provider or SP (Civiform). In our application, we use SP-initiated SAML authentication, which means our application signs and sends a SAML request to LoginRadius to begin the auth process.

Follow the steps below to configure LoginRadius SAML auth on the SP side for a local dev instance: 
- First create a keystore using the Java keytool using the following command. Take note of the keystore password and private key password used, and set the `LOGIN_RADIUS_PRIVATE_KEY_PASS` and `LOGIN_RADIUS_KEYSTORE_PASS` environment variables.
```bash
keytool -genkeypair -alias civiform-saml -keypass <private-key-password>  -keystore civiformSamlKeystore.jks -storepass <keystore-password>  -keyalg RSA -keysize 2048 -validity 3650
```

- Next, navigate to the [LoginRadius Dashboard](https://dashboard.loginradius.com/getting-started). Click on "Get Your API Key and Secret", copy the API key, and set the `LOGIN_RADIUS_API_KEY` environment variable to the copied value. 

- Finally set the `LOGIN_RADIUS_METADATA_URI` environment variable to the link `https://<login-radius-site-url>/service/saml/idp/metadata` (e.g. `https://civiform-staging.hub.loginradius.com/service/saml/idp/metadata`).

To configure SAML on the IDP side for LoginRadius, navigate to the "Integration" section on the left sidebar, add a SAML outbound SSO integration and follow the instructions [linked here](https://www.loginradius.com/docs/single-sign-on/tutorial/federated-sso/saml/sp-initiated/).  


- When configuring the integration, make note of the `SAML App Name` field, and set the `LOGIN_RADIUS_SAML_APP_NAME` environment variable.
> <img width="558" alt="image" src="https://user-images.githubusercontent.com/19631367/155665020-88d4e1ca-9dc6-41ee-a852-1ae19b080e22.png">

- For the service provider certificate field, export the locally generated public certificate, and then copy the contents of the exported file:
```bash
keytool -exportcert -alias civiform-saml  -keystore civiformSamlKeystore.jks -rfc -file test.cert
pbcopy < test.cert
``` 

- Set the following attributes. each attribute should have the format set to `urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified`
> <img width="1000" alt="image" src="https://user-images.githubusercontent.com/19631367/155667111-6ca9ac96-48cc-4f15-948b-26a615d4fa50.png">

- For local testing/development, the Service Provider Details section should have the following values.
> <img width="700" alt="image" src="https://user-images.githubusercontent.com/19631367/155667445-223de285-906f-4624-bbd4-ea88612fcc14.png">


