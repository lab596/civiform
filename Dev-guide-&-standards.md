- [Technology overview](#technology-overview)
  * [Views](#views)
- [Running a local server](#running-a-local-server)
  * [Dev database](#dev-database)
  * [Dev integration with IDCS and AD](#dev-integration-with-idcs-and-ad)
  * [Run tests](#run-tests)
- [Development standards](#development-standards)
  * [Client-server concerns](#client-server-concerns)
  * [Java code](#java-code)
    + [Async request handling](#async-request-handling)
    + [Separation of concerns](#separation-of-concerns)
  * [Routing and controller methods](#routing-and-controller-methods)
    + [HTML routing convention](#html-routing-convention)
    + [API routing convention](#api-routing-convention)
  * [Testing](#testing)
    + [Functional browser tests](#functional-browser-tests)
    + [Controller tests](#controller-tests)
    + [View tests](#view-tests)

# Technology overview

CiviForm is built on [Play Framework](https://www.playframework.com/) in Java, and backed by a [PostgreSQL](https://www.postgresql.org/) database.

## Views

Instead of the default templating language for Play (Twirl), CiviForm uses the [J2Html](https://j2html.com/) Java library to render HTML.

All view classes should extend [`BaseHtmlView`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/BaseHtmlView.java), which has some helpful common tag helper methods. Its `makeCsrfTokenInputTag` must be added to all CiviForm forms.

[`ViewUtils`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/ViewUtils.java) is a utility class for accessing stateful view dependencies.

See [`J2HtmlDemoController`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/controllers/J2HtmlDemoController.java
) and [`J2HtmlDemo`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/J2HtmlDemo.java) for a working example.


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

## Run tests

To run the tests, run `bin/run-test`. This include all tests under `test/`.

# Development standards

## Client-server concerns

The client should be as simple as is practical to implement the desired user experience.

* Pages should only include the JavaScript needed to implement their own behavior.
* Client-side JavaScript should have no need to manage browser history or derive state from the URL.
* Client-side JavaScript should avoid API-driven interactions, and instead access JSON embedded in the initial page load and submit data using HTML forms.

For example, enable/disable logic in forms can be specified server-side with HTML [data attributes](https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes) then implemented with generic client-side JS that responds to DOM events relevant to the attribute-specified elements. [Here's a simple example](https://jsfiddle.net/c8g6y0ru/1/).

## Java code

Java code should conform to the Google Java [styleguide](https://google.github.io/styleguide/javaguide.html). The project makes use of a linter and autoformatter for Java to help with this, just run `bin/fmt` and your code should be automatically formatted.

Prefer using immutable collection types provided by [Guava](https://github.com/google/guava) ([API docs](https://guava.dev/releases/snapshot/api/docs/)) over the Java standard library's mutable collections unless impractical. Include a comment justifying the use of a mutable collection if you use one.

### Async request handling

__Summary: Controllers handling requests from applicants or trusted intermediaries should be implemented asynchronously. All other controllers should be implemented synchronously.__

[Async IO](https://en.wikipedia.org/wiki/Asynchronous_I/O) is helpful for reducing per-request resource consumption and sometimes per-request latency. Play allows controllers to implement request handling methods either synchronously, by returning `Result`, or asynchronously by returning `CompletionStage<Result>`. The tradeoff is that writing asynchronous code tends to result in more complex production and test code and a slower development velocity.

We anticipate relatively low [QPS](https://en.wikipedia.org/wiki/Queries_per_second) for deployments of CiviForm. However, if a large jurisdiction uses CiviForm, QPS from applicants could get high enough to present scaling concerns. To balance the needs of development velocity and future scalability, we opt to optimize the applicant and intermediary code paths for scale while leaving the code paths that are unlikely to ever see significantly high QPS implemented synchronously.

Exception handling in asynchronous execution deserves a special mention as `CompletionException` is unchecked and can be easily missed. We should always explicitly catch `CompletionException` when joining a future in a synchronous context. When returning `CompletionStage<Result>` to users, we should provide exception handling through `CompletableFuture#exceptionally` API.

### Separation of concerns

See [wikipedia definition](https://en.wikipedia.org/wiki/Separation_of_concerns).

In lieu of a microservice architecture, this project necessitates special care in ensuring separation of concerns. While Play provides some structure in this regard, it should be viewed as a starting point.

Code in **Play controllers** should be limited to brokering interaction between the server's business logic systems and HTTP. Code in controllers should never directly implement business logic concerns and should instead delegate to classes specific to those purposes. One way to help think about this when writing controller code: if you're writing an HTTP handler that responds with HTML, factor out business logic classes so that implementing another handler that performs the same logic but responds with JSON benefits from high code re-use.

Code in **ebean models** should be limited to brokering interaction between the server's business logic and the database. Code in models should never directly implement business logic concerns.

## Routing and controller methods

APIs should follow [REST](https://en.wikipedia.org/wiki/Representational_state_transfer) when possible with the appropriate HTTP verb. Routing should be [resource-oriented](https://www.oreilly.com/library/view/restful-web-services/9780596529260/ch04.html) ([relevant AIP](https://google.aip.dev/121)). Path names should be [kebab-case](https://en.wikipedia.org/wiki/Letter_case#Special_case_styles).

### HTML routing convention

For a resource called "programs" that implements the standard actions via HTML requests the routes would be:

|HTTP verb|URL path             |Controller#method         |Use                                                                                                                                                      |
|---------|---------------------|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
|GET      |/programs            |ProgramsController#index  |Get a list of all programs                                                                                                                               |
|GET      |/programs/new        |ProgramsController#newOne |Get an HTML form for creating a new program (Note: the associated controller method is named `newOne` since `new` is disallowed as a method name in Java)|
|POST     |/programs            |ProgramsController#create |Create a new program, probably redirect to the #show method to view it                                                                                   |
|GET      |/programs/:id        |ProgramsController#show   |Get the details of a specific program                                                                                                                    |
|GET      |/programs/:id/edit   |ProgramsController#edit   |Get an HTML form for editing an existing program                                                                                                         |
|POST     |/programs/:id        |ProgramsController#update |Update an existing program                                                                                                                               |
|POST     |/programs/:id/delete |ProgramsController#destroy|Delete an existing program, probably redirect to the #index method                                                                                       |

### API routing convention

For the same resource accessed via JSON API the routes should be under the "/api" namespace and naturally do not require form-serving endpoints:

|HTTP verb|URL path              |Controller#method            |Use                                                                    |
|---------|----------------------|-----------------------------|-----------------------------------------------------------------------|
|GET      |/api/programs         |ProgramsApiController#index  |Get a list of all programs                                             |
|POST     |/api/programs         |ProgramsApiController#create |Create a new program                                                   |
|GET      |/api/programs/:id     |ProgramsApiController#show   |Get the details of a specific program                                  |
|PATCH/PUT|/api/programs/:id     |ProgramsApiController#update |Update an existing program                                             |
|DELETE   |/api/programs/:id     |ProgramsApiController#destroy|Delete an existing program                                             |

## Testing

We aim for complete unit test coverage of all execution paths in the system. If you submit code that is infeasible or impractical to get full test coverage for, consider refactoring. If you would like to make an exception, include a clear explanation for why in your PR description.

For Java, classes generally have their own unit tests. The unit test file should mirror the implementation file - for example, `/app/package/path/MyClass.java` should have a unit test `/test/package/path/MyClassTest.java`.

All major user-facing features should be covered by a functional browser test.

Tests that require a play application should either use `extends play.test.WithApplication` or `extends repository.WithPostgresContainer`, opting for the latter if a database is required. By default, using `extends play.test.WithApplication` will produce an application with a binding to an in-memory postgres database that is incompatible with everything and is pretty much useless.

### Functional browser tests

Functional browser tests use the [Playwright](https://playwright.dev) browser automation TypeScript library with the [Jest](https://jestjs.io/) test runner. The code for those tests lives in the [browser-test](https://github.com/seattle-uat/civiform/tree/main/browser-test) subdirectory.

Browser tests run against an application stack that is very similar to the local development stack. The test stack has its own application server, postgres database, and fake IDCS server that all run in Docker, separate from the test code. The test stack is intended to stay up between test runs to reduce the iteration time for running the tests while developing. To run the tests:

- `./bin/build-browser-tests` - build the Docker image for running the playwright tests. This only needs to be done once.
- `./bin/run-browser-test-env` - bring up the local test environment. Leave this running while you are working for faster browser test runs.
- `./bin/run-browser-tests` - run the playwright tests in a docker container.

To run a test in a specific file, you can pass the file path relative to the `browser-test/src` directory e.g. `./bin/run-browser-tests landing_page.test.ts`.

Debugging tips:

You can take screenshots of the browser during test runs and save them to `browser-test/tmp`. (that directory [is mounted as a volume](https://github.com/seattle-uat/civiform/blob/main/bin/run-browser-tests) in the Docker test container). For example, to take a full-page screenshot and save it in a file called `screenshot.png`: `await page.screenshot({ path: 'tmp/screenshot.png', fullPage: true })`. **Note that you must prefix the filename with `tmp/`**. [More info on taking screenshots with Playwright here](https://playwright.dev/docs/screenshots).

#### Guidelines for functional browser tests

In contrast to unit tests, browser tests do not and should attempt to exhaustively test all code paths and states possible for the system under test.

### Controller tests

Controller tests should test the integration of business logic behind each HTTP endpoint. Most controller tests should likely extend `WithPostgresContainer` which provides a real database. Controllers should contain very little if any conditional logic and delegate business logic and network interactions (database, auth service, file services, etc) to service classes.

* Assertions should be on the method's `Result` rather than the rendered HTML
* Assertions may also be on the database state after the controller method has completed
* Should not rely heavily on mocking

See [AdminProgramControllerTest.java ](https://github.com/seattle-uat/civiform/pull/167/files#diff-643f94cff692c6554cd33c8e4c542b9f2bc65b4756bf027a623ce8f203d28677) for a good example of a controller test. See the [Play documentation](https://www.playframework.com/documentation/2.8.x/JavaTest#Unit-testing-controllers) for information on framework-provided testing tools.

### View tests

[`BaseHtmlView`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/BaseHtmlView.java) provides a number of HTML tag producing methods, for example [`Tag submitButton(String textContents)`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/BaseHtmlView.java#L33). These methods tend to be fairly simple, with unit tests that are brittle to small, inconsequential changes. Whether or not to test these types of methods is at the discretion of the implementer and code reviewer(s).

View classes that render a complete page should not be unit tested, but instead should have corresponding browser test(s) that asserts the key interactions for a user on that page.

Question type rendering and client-side logic deserves a special mention since they can have complex interaction logic. These should be unit tested in isolation, in browser test(s).