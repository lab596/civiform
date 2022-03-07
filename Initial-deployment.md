This guide is for SREs or DevOps folks.  It explains how to do an initial deployment of CiviForm into your production cloud environment.

At the time of first writing (March 2022) CiviForm has these constraints:
* Can be deployed directly into AWS, via direct AWS APIs and tools such as [cloudformation](https://github.com/seattle-uat/civiform/tree/main/infra).
* Can be deployed into Azure using [Terraform](https://github.com/seattle-uat/civiform/tree/main/cloud/azure).  _In the future, AWS deployment will also be ported to Terraform, and GCP support will likely be added._

# Deploying into AWS

Our production infrastructure is managed declaratively by [cloudformation](https://github.com/seattle-uat/civiform/tree/main/infra). To deploy, `run bin/deploy-prod`. You will need the AWS CLI - `brew install awscli`.

Production can also be deployed by kicking off the workflow for this prober [here](https://github.com/seattle-uat/civiform/actions/workflows/cron.yaml). We have turned off scheduled probe and deploys for now.


# Deploying into Azure

## Forking the Deployment Repository

## Setting up LoginRadius

## Setting up Azure Active Directory

## Running the Terraform Deployment Scripts
### Filling in all the config variables

## Kicking the Tires
### Testing authentication
### Testing form-construction
### Testing form submission & file uploads
### Testing program admin views
### Testing outgoing email
