This guide offers best practices for writing unit and browser tests for CiviForm, as well as debugging tips and practices.

## What to test

In general, all execution paths in the system should be covered by [unit tests](#unit-tests). If you submit code that is infeasible or impractical to get full test coverage for, consider refactoring. If you would like to make an exception, include a clear explanation for why in your PR description.

In contrast, [Functional browser tests](#functional-browser-tests should cover all major user-facing features to make sure the system generally works from a user's point of view, rather than exhaustively test all execution paths.

## Unit tests

For Java, classes generally have their own unit tests. The unit test file should mirror the implementation file. For example, `/app/package/path/MyClass.java` should have a unit test `/test/package/path/MyClassTest.java`.

Tests that require a Play application should either use `extends play.test.WithApplication`, or `extends repository.WithPostgresContainer` if a database is required. By default, using `extends play.test.WithApplication` will produce an application with a binding to an in-memory postgres database that is incompatible with everything and is pretty much useless.

To run the unit tests (includes all tests under [`test/`](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/test)), run the following:

```
bin/run-test
```

If you'd like to run a specific test or set of tests, and/or save sbt startup time each time you run the test(s), use these steps:

1. Bring up an sbt shell inside the Docker container by running:

       bin/sbt

1. Run any sbt commands! For example:

       testOnly services.question.QuestionDefinitionTest

### Controller tests

Controller tests should test the integration of business logic behind each HTTP endpoint. Most controller tests should likely extend `WithPostgresContainer` which provides a real database. Controllers should contain very little if any conditional logic and delegate business logic and network interactions (database, auth service, file services, etc.) to service classes.

* Assertions should be on the method's `Result` rather than the rendered HTML.
* Assertions may also be on the database state after the controller method has completed.
* Controller tests should not rely heavily on mocking.

See [AdminProgramControllerTest.java ](https://github.com/seattle-uat/civiform/pull/167/files#diff-643f94cff692c6554cd33c8e4c542b9f2bc65b4756bf027a623ce8f203d28677) for a good example of a controller test. See the [Play documentation](https://www.playframework.com/documentation/2.8.x/JavaTest#Unit-testing-controllers) for information on framework-provided testing tools.

### View tests

[`BaseHtmlView`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/BaseHtmlView.java) provides a number of HTML tag-producing methods, for example [`Tag submitButton(String textContents)`](https://github.com/seattle-uat/civiform/blob/main/universal-application-tool-0.0.1/app/views/BaseHtmlView.java#L53). These methods tend to be fairly simple, with unit tests that are brittle to small, inconsequential changes. Whether or not to test these types of methods is at the discretion of the implementer and code reviewer(s).

View classes that render a complete page should not be unit tested, but instead should have corresponding [browser test(s)](#functional-browser-tests) that assert the key interactions for a user on that page.

Question type rendering and client-side logic deserves a special mention since they can have complex interaction logic. These should be unit tested in isolation, in browser test(s).

## Functional browser tests

Functional browser tests use the [Playwright](https://playwright.dev) browser automation TypeScript library with the [Jest](https://jestjs.io/) test runner. The code for those tests lives in the [browser-test/](https://github.com/seattle-uat/civiform/tree/main/browser-test) subdirectory.

Browser tests run against an application stack that is very similar to the local development stack. The test stack has its own application server, postgres database, and fake IDCS server that all run in Docker, separate from the test code. The test stack is intended to stay up between test runs to reduce the iteration time for running the tests while developing.

To run the tests:

1. Build the Docker image for running the playwright tests. This only needs to be done once:

       bin/build-browser-tests

1. Bring up the local test environment. This step can be done in a separate terminal window while the
   Docker image is still building.
   Leave this running while you are working for faster browser test runs:

       bin/run-browser-test-env

1. Once you see "Server started" in the terminal from the above step, in a separate terminal run the
   Playwright tests in a docker container:

       bin/run-browser-tests

   Or, to run a test in a specific file, pass the file path relative to the `browser-test/src` directory. For example:

       bin/run-browser-tests landing_page.test.ts

### Debugging browser tests

Please see the [Playwright debug docs](https://playwright.dev/docs/debug) for a lot more info on this topic.

#### Debug mode

You can step through a test run line-by-line with the browser by running the tests locally (i.e. not in Docker) with debug mode turned on.

This requires:
1. Installing node.js.
1. Installing [yarn](https://yarnpkg.com/).
1. Running `yarn install` in the `browers-test` directory.

To run the tests locally, use:

    bin/run-browser-tests-local

To run them in debug mode with the open browser add the `PWDEBUG` environment variable:

    PWDEBUG=1 /bin/run-browser-tests-local

#### Screenshots

With both `bin/run-browser-tests` and `bin/run-browser-tests-local` you can take screenshots of the browser during test runs and save them to `browser-test/tmp`. (that directory [is mounted as a volume](https://github.com/seattle-uat/civiform/blob/main/bin/run-browser-tests) in the Docker test container). For example, to take a full-page screenshot and save it in a file called `screenshot.png`:

```typescript
await page.screenshot({ path: 'tmp/screenshot.png', fullPage: true })
```

**Note**: You must prefix the filename with `tmp/`. [More info on taking screenshots with Playwright here](https://playwright.dev/docs/screenshots).

### Guidelines for functional browser tests

In contrast to unit tests, browser tests do not and should attempt to exhaustively test all code paths and states possible for the system under test. Browser tests should:

- be fewer and larger, covering major features of the application
- only create state in the database by interacting with the UI (e.g. when testing the applicant experience for answering of a certain type, first login as an admin, create a question and a program with that question)
- encapsulate UI interaction details into [page object classes](https://playwright.dev/docs/pom/)
- as much as is practical navigate using the UI and not by directly referencing URL paths