This guide helps you get started running and interacting with a local development server and tests.

## Prerequisites

1. A 64-bit OS and computer

1. At least 4 GB of RAM

## Recommendations

* The preferred IDE is [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) because it’s designed around Java-based projects

    * Recommended plugins: Docker, Database Navigator

* The preferred platform is Unix-based since there is less overhead for running Docker Engine due to resource isolation features of Linux kernels which does not require the need of virtual machines 

## Setting up your environment

Start here! This step is a prerequisite for everything that follows, even if you only want to interact with a local server without pushing changes.

1. [Join GitHub](https://github.com/join).

1. [Install git](https://github.com/git-guides/install-git) on your machine.

1. Clone the CiviForm repo. This will create a copy of the codebase on your machine:

    1. Open a terminal and navigate to the directory you'd like the copy of the CiviForm codebase to live.
    1. In that directory, run the following (and/or refer to
       [this guide](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository)):

           git clone git@github.com:seattle-uat/civiform.git

1. Next we will set up Docker which process differs per platform (see below)


### For Windows

Note that this only works for Windows 10 and a machine that supports Hyper-V (must be enabled in the BIOS).

1. [Install Windows Subsystem for Linux 2](https://docs.microsoft.com/en-us/windows/wsl/install-win10) (WSL 2) with any Linux distribution (Ubuntu is tested to work with this project).

    * If Windows Subsystem for Linux 1 (WSL 1) is already installed, skip to steps 4 and 5 in the linked guide to update to WSL 2.

1. [Install Docker Desktop on Windows](https://docs.docker.com/docker-for-windows/install/).

1. Startup Docker Desktop to enable the use of `docker` and `docker-compose` in WSL 2.

1. Open up PowerShell or Command Prompt and type `wsl -l -v` to make sure that you installed Docker Desktop and WSL 2 correctly.

    * You should have your Linux distribution(s) listed followed by `docker-desktop-data` and `docker-desktop`.

    * In addition, all distributions should be running at version 2.

1. Type `wsl` to go into the Bash shell.

1. Navigate to where you saved the CiviForm repo and type `bin/run-dev` to get the latest Docker images of the project.


### For macOS

Note that VirtualBox prior to version 4.3.30 is not compatible with Docker Desktop and must be uninstalled or updated to the latest version.

1. [Install Docker Desktop on Mac](https://docs.docker.com/docker-for-mac/install/).

1. Startup Docker Desktop to enable the use of `docker` and `docker-compose` in Zsh.

1. Launch Zsh and type `docker -v` and `docker-compose -v` to confirm that Docker Desktop was installed correctly.

1. Navigate to where you saved the CiviForm repo and type `bin/run-dev` to get the latest Docker images of the project.


### For Linux/Unix

Docker provides official support for the following distributions: Ubuntu, Debian, Fedora, CentOS, and Raspbian.

1. [Install Docker Engine](https://docs.docker.com/engine/install/).

1. [Install Docker Compose](https://docs.docker.com/compose/install/).

1. Launch a new terminal instance and type `docker -v` and `docker-compose -v` to confirm that Docker Engine and Docker Compose were installed correctly.

1. Navigate to where you saved the CiviForm repo and type `bin/run-dev` to get the latest Docker images of the project.


### A note on IDEs

You may use whichever IDE you prefer, though _DO NOT_ use the IDE's built-in sbt (Scala Build Tool) shell if it has one. Instead, run `bin/sbt` to bring up an sbt shell inside the Docker container. The sbt shell allows you to compile, run tests, and run any other sbt commands you may need.


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

To run the browser tests (includes all the [Playwright](https://playwright.dev/) tests in [`civiform/browser-test/src/`](https://github.com/seattle-uat/civiform/tree/main/browser-test/src), there are three steps:

1. Build the Docker image for running the playwright tests. This only needs to be done once:

       bin/build-browser-tests

1. Bring up the local test environment. This step can be done in a separate terminal window while the above step is still building.
   Leave this running while you are working for faster browser test runs:

       bin/run-browser-test-env

1. Once you see "Server started" in the terminal from the above step, in a separate terminal run the
   tests in a docker container:

       bin/run-browser-tests

   Or, to run a test in a specific file, you can pass the file path relative to the `browser-test/src` directory:

       bin/run-browser-tests landing_page.test.ts


## What's next?

To learn more about how to make code contributions, head to [Technical contribution guide](https://github.com/seattle-uat/civiform/wiki/Technical-contributions).

To learn about the CiviForm tech stack and standards, head to [Technology overview](https://github.com/seattle-uat/civiform/wiki/Dev-guide-&-standards) and [Development standards](https://github.com/seattle-uat/civiform/wiki/Development-standards).