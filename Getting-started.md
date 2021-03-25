This guide helps you get started running and interacting with a local development server, as well as tests.

# Getting started with git, Docker, and the CiviForm repo

Start here! This step is a prerequisite for everything that follows, even if you only want to interact with a local development server without pushing changes.

1. Join GitHub and install git:

   a. If you haven't already, [join GitHub](https://github.com/join).

   b. Follow [this guide](https://github.com/git-guides/install-git) for installing git on your machine.

1. Install Docker:

   a. TODO

1. Clone the CiviForm repo. This will create a copy of the codebase on your machine:
  
   a. Open a terminal and navigate to the directory you'd like the copy of the CiviForm codebase to live.

   c. In that directory, run the following (and/or refer to
      [this guide](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository)):

          git clone git@github.com:seattle-uat/civiform.git


# Running a local server

To check out the current state of the CiviForm app, follow these steps.

1. To run the application, navigate to the top-level `civiform` directory and run the following:

       bin/run-dev

2. Once you see "Server started" in your terminal (it will take some time for the server to start up),
   you can access the server in a browser at `localhost:9000`.
   Be patient on the initial page load since it will take some time for all the sources to compile.

The `bin/run-dev` script uses `docker-compose` (see [`docker-compose.yaml`](https://github.com/seattle-uat/civiform/blob/main/docker-compose.yml)). It enables Java and Javascript hot-reloading: when you modify most files, the server will recompile and restart. This is pretty time-consuming on first page load, but after that, it's not so bad.

# Running tests

This section will help you run CiviForm unit and browser tests in a basic way. For more information on _writing_ and _debugging_ these tests, check out this [Testing](https://github.com/seattle-uat/civiform/wiki/Dev-guide-&-standards#testing) guide.

## Running unit tests

To run the unit tests (includes all tests under [`test/`](https://github.com/seattle-uat/civiform/tree/main/universal-application-tool-0.0.1/test)), run the following:

```
bin/run-test
```

## Running browser tests

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


# What's next?

Check out our [Development guide and standards](https://github.com/seattle-uat/civiform/wiki/Dev-guide-&-standards) to get started contributing.