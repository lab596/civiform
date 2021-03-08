# Development guide

## Technology overview

The UAT is built on [Play Framework](https://www.playframework.com/) in Java, and backed by a [PostgreSQL](https://www.postgresql.org/) database.

### Views

Instead of the default templating language for Play (Twirl), UAT uses the [J2Html](https://j2html.com/) Java library to render HTML.

All view classes should extend [`BaseHtmlView`](https://github.com/seattle-uat/universal-application-tool/blob/main/universal-application-tool-0.0.1/app/views/BaseHtmlView.java), which has some helpful common tag helper methods. Its `makeCsrfTokenInputTag` must be added to all UAT forms.

[`ViewUtils`](https://github.com/seattle-uat/universal-application-tool/blob/main/universal-application-tool-0.0.1/app/views/ViewUtils.java) is a utility class for accessing stateful view dependencies.

See [`J2HtmlDemoController`](https://github.com/seattle-uat/universal-application-tool/blob/main/universal-application-tool-0.0.1/app/controllers/J2HtmlDemoController.java
) and [`J2HtmlDemo`](https://github.com/seattle-uat/universal-application-tool/blob/main/universal-application-tool-0.0.1/app/views/J2HtmlDemo.java) for a working example.


## Running a local server

To build the container that runs the app, type `docker build -t uat .`  Running this takes ~3 minutes, but it bakes in most of the dependencies you will need to download, so if you make a significant change to the dependencies you may want to re-build.

To run the application, run `bin/run-dev`, which uses `docker-compose` (see `docker-compose.yaml`).
This enables java and javascript hot-reloading - when you modify most files, the server will recompile and restart.  This is pretty time-consuming on first page load, but after that, it's not so bad.
After this, you can access the server at localhost:9000.

### Dev database

Whenever a new database container is created, it is empty, i.e. it is not linked to any external storage.
Note if you restart a paused container, you could see the change(s) applied from last session.
You can obtain a fresh container by removing the existing one on docker dashboard.

As the database is configured today, it does not persist data after it terminates, but this can be changed.

If you wish to create new table(s) or change schema, please add the SQL(s) under `conf/evolutions/default`.
You will need to create corresponding EBean model(s) under `app/models`, and potentially a repository under `app/repository` for how you'd like to interact with the table(s).

In dev mode, whenever you first start the app, Play confirms with you if the database is up-to-date and whether you want to apply the evolution scrtips to set up schema or if the database is out-of-sync and needs manual resolution.
If the database is in an inconsistent state, it is usually easier to trash the problem database and start a new one in dev mode.

If we want to undo a schema change, we can create new evolution scripts that change the schema or simply remove existing scripts that create the schema we don't want. If we choose the latter, we likely need to manually reconcile existing database state or we have to start a new one.

## Run tests

To run the tests, run `bin/run-test`. This include all tests under `test/`.