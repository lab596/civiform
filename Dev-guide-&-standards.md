
- [Running a local server](#running-a-local-server)
  * [Dev database](#dev-database)
  * [Dev integration with IDCS and AD](#dev-integration-with-idcs-and-ad)
  * [Run tests](#run-tests)

# Running a local server

To run the application, run `bin/run-dev`, which uses `docker-compose` (see `docker-compose.yaml`).
This enables java and javascript hot-reloading - when you modify most files, the server will recompile and restart.  This is pretty time-consuming on first page load, but after that, it's not so bad.
After this, you can access the server at localhost:9000.

## Dev database

Dev database container is not linked to any external storage so if you delete the container, you will lose all data previously persisted.
Note if you restart a paused container, you could still see the change(s) applied from previous session(s).

If you wish to create new table(s) or change schema, please add the SQL(s) under `conf/evolutions/default`.
You will need to create or update corresponding EBean model(s) under `app/models`, and potentially one or more repositories under `app/repository` for how you'd like to interact with the table(s).

In dev mode, Play automatically applies the evolution scripts to set up the schema, including making destructive changes if the database is out-of-sync.  You'll be notified if it needs manual resolution.
If the database is in an inconsistent state in dev mode, it is usually easier to trash the problem database and start a new one.

If we want to undo a schema change, we create new evolution scripts that modify the schema.

### Dev integration with IDCS and AD

Integration with identity providers requires some secrets which we can't disseminate to everyone.  If you work for google.org and are able to access AWS, you can run `bin/refresh-secrets` after authenticating to AWS using one of the methods on the AWS splash page.  From there, all subsequent runs of `bin/run-dev` will integrate cleanly with AD and IDCS.  If that's not possible, you should use the OIDC provider that we use for unit tests.  See `test/app/SecurityBrowserTest.java` for examples of how to interact with that.
