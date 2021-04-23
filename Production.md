Our production infrastructure is managed declaratively by [cloudformation](https://github.com/seattle-uat/civiform/tree/main/infra). To deploy, `run bin/deploy-prod`. You will need the AWS CLI - `brew install awscli`.

Production automatically updates after a prober run at 8 pm and 6 am, pacific time, every weekday.  You can view the most recent runs of this prober [here](https://github.com/seattle-uat/civiform/actions/workflows/cron.yaml).

## Resources not managed by CloudFormation

### DNS records
The records for staging.seattle.civiform.com and seattle.civiform.com are created in hosted zones managed via Route 53.  The delegation of seattle.civiform.com is done in a GoDaddy console - maintained by @gcapiel.  The City of Seattle maintains the DNS record for civiform.seattle.gov.

### SSL Certificates
The SSL certs for staging.seattle.civiform.com and seattle.civiform.com are managed in AWS Certificate Manager.  The certificate for civiform.seattle.gov is managed by the City of Seattle and is stored in Certificate Manager.  Certificate rotation will be enabled for *.civiform.com, but not for civiform.seattle.gov, since it is impossible to rotate manually managed certificates.

### Notification endpoints
The messages for tickets or production failures are sent to SNS queues which are maintained by CloudFormation.  The distribution lists of those queues are managed in the console.

### Log processing
Our logs are processed by [this Lambda function](https://us-west-2.console.aws.amazon.com/lambda/home?region=us-west-2#/functions/prod-log-processor?tab=code), which is also managed by the console.  You'll need to be signed in to the Civiform AWS account, which you can reach [here](https://seattle-commercial.awsapps.com/start#/).

The logging input is configured in [this panel](https://us-west-2.console.aws.amazon.com/lambda/home?region=us-west-2#/functions/prod-log-processor?tab=configure), and you can read more about how to work with Lambda and CloudWatch logs [here](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html) and [here](https://docs.aws.amazon.com/lambda/latest/dg/services-cloudwatchlogs.html).