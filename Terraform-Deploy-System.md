## Setup

1. Fork the [civiform-deploy](https://github.com/civiform/civiform-deploy) repo to your organization via the UI
2. Pull the repo onto the machine you are deploying from (ideally this would be a shared instance multiple people can log onto)
3. Run the bin/doctor and install the dependencies
4. Copy the civiform_config.example.sh into civiform_config.sh and fill out the missing values
5. Go to [docker-hub](https://hub.docker.com/r/civiform/civiform/tags) and get the desired snapshot tag
6. Run bin/setup --tag=<snapshot_tag>

### Setting up auth providers
Follow documentation for setting up the [Authentication Providers](https://github.com/seattle-uat/civiform/wiki/Authentication-Providers). 

### Setting up Azure AD
The setup scrip prompts you to set up Azure AD. There are a few additional steps. 

* You will need to setup the Redirect URI to be what the app expects: https://<custom_hostname>/callback/AdClient. 
* You will also need an admin group which creates civiform admins
* To allow for civiform admins you need to have the Azure Ad return the groups claim. Do this in the token configuration section of the Azure portal and add the security groups claim (you can verify the groups claim is being returned by decoding the base64 token from the token you get back from Azure AD on the website-- if you preserve the log in the Chrome Dev Tool window it should be from https://<custom_hostname>/callback/AdClient)

## Deploy

1. Go to [docker-hub](https://hub.docker.com/r/civiform/civiform/tags) and get the desired snapshot tag
2. Run bin/deploy --tag=<snapshot_tag>

## Access the database
1. cd checkout
2. cloud/azure/bin/db-connection -g sgdev -d civiform-artistic-halibut -v sgdev-civiform-kv

## Restore data to the database from a dump file
1. If on WSL figure out what the location of the dump file (possibly /mnt/c/..)
2. cd checkout
2. cloud/azure/bin/pg-restore -g sgdev -d civiform-artistic-halibut -v sgdev-civiform-kv -f /mnt/c/pg_dump.dump -b testData.dump

## Troubleshooting
### Terraform fails
The deploy command is idempotent so if it fails try running it again. The setup command can also be re-reun but it does re-set a lot of variables which are kind of a pain to continually set up.

If changes were made upstream, you can change the code in the checkout env, but will need to commit PRs to fix in the main repo

