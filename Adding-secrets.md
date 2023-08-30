## Background

Some [configuration variables](Server-configuration-variables) hold values that should not be checked into public repositories. Examples include client ids and client secrets for integration with authentication providers.

The CiviForm deployment system supports such variables by storing the values in the [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html) and retrieving the values upon server startup.

This page describes the process of adding a secret to support a new configuration variable.

## Steps

1. Define the new secret(s) in [`secrets.tf`](https://github.com/civiform/cloud-deploy-infra/blob/main/cloud/aws/templates/aws_oidc/secrets.tf). For each secret, you will need to define the [secret](https://docs.aws.amazon.com/secretsmanager/latest/userguide/getting-started.html#term_secret) itself as well as a [version](https://docs.aws.amazon.com/secretsmanager/latest/userguide/getting-started.html#term_version).

## Sample PR

Here is an example PR that demonstrates the steps described above: [Add new AWS secrets for admin OIDC](https://github.com/civiform/cloud-deploy-infra/pull/242).
