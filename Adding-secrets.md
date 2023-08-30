## Background

Some [configuration variables](Server-configuration-variables) hold values that should not be checked into public repositories. Examples include client ids and client secrets for integration with authentication providers.

The CiviForm deployment system supports such variables by storing the values in the [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html) and retrieving the values upon server startup.

This page describes the process of adding a secret to support a new configuration variable.

## Steps

The secrets are provisioned by the CiviForm deployment system, rather than by using the AWS Secrets Manager UI.

1. Define the new secret(s) in [`secrets.tf`](https://github.com/civiform/cloud-deploy-infra/blob/main/cloud/aws/templates/aws_oidc/secrets.tf). For each secret, you will need to define the [secret](https://docs.aws.amazon.com/secretsmanager/latest/userguide/getting-started.html#term_secret) itself as well as a [version](https://docs.aws.amazon.com/secretsmanager/latest/userguide/getting-started.html#term_version).

1. Set new config variable(s) to refer to the new secret(s) in [`resources.py`](https://github.com/civiform/cloud-deploy-infra/blob/b2e14ae8d049df7ffebbec7d1aaa4bca5e8d541a/cloud/aws/templates/aws_oidc/bin/resources.py#L11-L21).

1. Add the new config variables to [`app.tf`](https://github.com/civiform/cloud-deploy-infra/blob/main/cloud/aws/templates/aws_oidc/app.tf), in the [`secrets` list](https://github.com/civiform/cloud-deploy-infra/blob/b2e14ae8d049df7ffebbec7d1aaa4bca5e8d541a/cloud/aws/templates/aws_oidc/app.tf#L41) and the [`Resource` list](https://github.com/civiform/cloud-deploy-infra/blob/b2e14ae8d049df7ffebbec7d1aaa4bca5e8d541a/cloud/aws/templates/aws_oidc/app.tf#L254).

1. Add the new config variables to the [`Resources` dict](https://github.com/civiform/cloud-deploy-infra/blob/b2e14ae8d049df7ffebbec7d1aaa4bca5e8d541a/cloud/aws/templates/aws_oidc/bin/setup.py#L15) in `setup.py`.

These steps will result in prompts to set the value(s) of the secret(s) when running the deployment script. This will provision the secrets in AWS, where they can then be viewed or changed if necessary.

## Sample PR

Here is an example PR that demonstrates the steps described above: [Add new AWS secrets for admin OIDC](https://github.com/civiform/cloud-deploy-infra/pull/242).
