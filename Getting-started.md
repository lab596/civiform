This guide helps you get started running and interacting with a local development server and tests.

## Setting up your environment

Start here! This step is a prerequisite for everything that follows, even if you only want to interact with a local server without pushing changes. If you're working in Windows, check out [Getting started with Windows](https://github.com/seattle-uat/civiform/wiki/Getting-started-with-Windows).

1. [Join GitHub](https://github.com/join).

1. [Install git](https://github.com/git-guides/install-git) on your machine.

1. Download [Docker Desktop](https://www.docker.com/get-started). On Mac, run Docker Desktop and go to Preferences > Resources to increase max RAM for containers to at least 4GB, otherwise sbt compiles can get killed before completing and produce strange runtime behavior.

1. Clone the CiviForm repo. This will create a copy of the codebase on your machine:

    1. Open a terminal and navigate to the directory you'd like the copy of the CiviForm codebase to live.

    1. In that directory, run the following (and/or refer to
       [this guide](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository)):

           git clone git@github.com:seattle-uat/civiform.git

### A note on IDEs

You may use whichever IDE you prefer, though _DO NOT_ use the IDE's built-in sbt (Scala Build Tool) shell if it has one. Instead, run `bin/sbt` to bring up an sbt shell inside the Docker container. The sbt shell allows you to compile, run tests, and run any other sbt commands you may need


## Running a local server

1. To run the application, navigate to the `civiform` directory you created when you cloned the repo and run the following:

       bin/run-dev

2. Once you see "Server started" in your terminal (it will take some time for the server to start up),
   you can access the app in a browser at http://localhost:9000.
   Be patient on the initial page load since it will take some time for all the sources to compile.

The `bin/run-dev` script uses `docker-compose` (see [`docker-compose.yaml`](https://github.com/seattle-uat/civiform/blob/main/docker-compose.yml)). It enables Java and Javascript hot-reloading: when you modify most files, the server will recompile and restart. This is pretty time-consuming on first page load, but after that, it's not so bad.

### Help! It's not working!

We know setting up a development environment can have some snags in the road. If something isn't working, check out our [Troubleshooting](https://github.com/seattle-uat/civiform/wiki/Dev-troubleshooting) guide or reach out on [Slack](https://join.slack.com/t/civiform/shared_invite/zt-niap7ys1-RAICICUpDJfjpizjyjBr7Q).

## Running tests

This section will help you run CiviForm unit and browser tests in a basic way. For more information on _writing_ and _debugging_ these tests, check out the [Testing](https://github.com/seattle-uat/civiform/wiki/Testing) guide.

### Running unit tests

To run the unit tests (includes all tests under [`test/`](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/test)), run the following:

```
bin/run-test
```

If you'd like to run a specific test or set of tests, and/or save sbt startup time each time you run the test(s), use these steps:

1. Bring up an sbt shell inside the Docker container by running:

       bin/sbt

1. Run any sbt commands! For example:

       testOnly services.question.QuestionDefinitionTest

### Running browser tests

To run the browser tests (includes all the [Playwright](https://playwright.dev/) tests in
[`civiform/browser-test/src/`](https://github.com/seattle-uat/civiform/tree/main/browser-test/src),
there are three steps:

1. Build the Docker image for running the playwright tests. This only needs to be done once:

       bin/build-browser-tests

1. Bring up the local test environment. This step can be done in a separate terminal window while the
   above step is still building.
   Leave this running while you are working for faster browser test runs:

       bin/run-browser-test-env

1. Once you see "Server started" in the terminal from the above step, in a separate terminal run the
   tests in a docker container:

       bin/run-browser-tests

   Or, to run a test in a specific file, you can pass the file path relative to the `browser-test/src` directory:

       bin/run-browser-tests landing_page.test.ts


## Creating fake data

To create Questions and Programs that use them, you need to log in as a "Program and Civiform Admin" through http://localhost:9000/loginForm .

You can return to that screen to switch to a Guest user and back again to an Admin as needed.

## What's next?

To learn more about how to make code contributions, head to [Technical contribution guide](https://github.com/seattle-uat/civiform/wiki/Technical-contribution-guide).

To learn about the CiviForm tech stack and standards, head to [Technology overview](https://github.com/seattle-uat/civiform/wiki/Technology-overview) and [Development standards](https://github.com/seattle-uat/civiform/wiki/Development-standards).