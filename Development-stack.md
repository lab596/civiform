## Dev database

Dev database container is not linked to any external storage so if you delete the container, you will lose all data previously persisted.
Note if you restart a paused container, you could still see the change(s) applied from previous session(s).

If you wish to create new table(s) or change schema, please add the SQL(s) under [`conf/evolutions/default/`](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/conf/evolutions/default).
You will need to create or update corresponding EBean model(s) under [`app/models/`](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/app/models), and potentially one or more repositories under [`app/repository/`](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/app/repository) for how you'd like to interact with the table(s).

In dev mode, Play automatically applies the evolution scripts to set up the schema, including making destructive changes if the database is out-of-sync.  You'll be notified if it needs manual resolution.
If the database is in an inconsistent state in dev mode, it is usually easier to trash the problem database and start a new one.

If we want to undo a schema change, we create new evolution scripts that modify the schema.

## Dev integration with IDCS and AD

Integration with identity providers requires some secrets which we can't disseminate to everyone.  If you work for google.org and are able to access AWS, you can run `bin/refresh-secrets` after authenticating to AWS using one of the methods on the AWS splash page.  From there, all subsequent runs of `bin/run-dev` will integrate cleanly with AD and IDCS.  If that's not possible, you should use the OIDC provider that we use for unit tests.  See `test/app/SecurityBrowserTest.java` for examples of how to interact with that.
