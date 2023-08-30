## Background

Some [configuration variables](Server-configuration-variables) hold values that should not be checked into public repositories. Examples include client ids and client secrets for integration with authentication providers.

The CiviForm deployment system supports such variables by storing the values in the [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html) and retrieving the values upon server startup.

This page describes the process of adding a secret to support a new configuration variable.

