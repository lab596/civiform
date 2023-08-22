# Getting started

This guide helps you get started running and interacting with a local development server and tests. If you are a Full Time SWE onboarding, first check out the links/tutorials/account-settup detailed in [New Full Time SWE Onboarding Material](new-full-time-swe-onboarding-materials.md).

## Setting up your environment

Note: these instructions assume a unix environment, scroll down for Windows.

Start here! This step is a prerequisite for everything that follows, even if you only want to interact with a local server without pushing changes. If you're working in Windows, check out [Getting started with Windows](getting-started-with-windows.md).

1. [Join GitHub](https://github.com/join).
2. [Install git](https://github.com/git-guides/install-git) on your machine.
3. [Configure an SSH key with your GitHub account](https://docs.github.com/en/authentication/connecting-to-github-with-ssh). We recommend not including the "UseKeychain yes" line or setting a password.
4. Install Docker
   * On Mac
     * Download [Docker Desktop](https://www.docker.com/get-started). 
     * Run Docker Desktop and go to Preferences > Resources to increase max RAM for containers to at least 4GB (ideally 6GB), otherwise sbt compiles can get killed before completing and produce strange runtime behavior.
   * On Linux
     * Install [Docker Engine](https://docs.docker.com/engine/install/#server) for your Linux distribution.
     * Configure Docker to run in [Rootless Mode](https://docs.docker.com/engine/security/rootless). This will create generated files as the correct user, otherwise file permission issues occur.
     * Install Docker compose v2 by following [these instructions](https://docs.docker.com/compose/cli-command/#install-on-linux).
5. Clone the CiviForm repo. This will create a copy of the codebase on your machine:
   1. Open a terminal and navigate to the directory you'd like the copy of the CiviForm codebase to live.
   2. In that directory, run the following (and/or refer to [this guide](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository)):

       ```
       git clone git@github.com:civiform/civiform.git
       ```

### A note on IDEs

#### Running SBT

You may use whichever IDE you prefer, though _DO NOT_ use the IDE's built-in sbt (Scala Build Tool) shell if it has one. Instead, run `bin/sbt` to bring up an sbt shell inside the Docker container. The sbt shell allows you to compile, run tests, and run any other sbt commands you may need

#### Configuring IntelliJ

The easiest way to get IntelliJ to index the project correctly is to install the Scala plugin and open the project by specifying the [build.sbt](https://github.com/civiform/civiform/blob/main/server/build.sbt) file in IntelliJ's open existing project dialog. If this isn't done correctly IntelliJ probably won't load library code correctly and will complain that it can't find symbols imported from libraries.

This setup will only include the files under (.../server), therefore, if you would like to edit other files under .../civiform, you need to add additional modules manually:

* Go to File/Project Structure, 
* Select modules 
* Ensure `civiform-server` and `sources` are selected in second and third column,
* Click on + on the right handside to add all folders under civiform except "server" to the content roots 

If you still have trouble getting some symbols to show (such as `routes` packages), try the following, in order:

1. Go to Project Structure and add `target` to your Sources in the `civiform-server` Module.
2. Go to the sbt shell **within IntelliJ** and run `compile`.
3. Go to Preferences, then "Languages and Frameworks", then "Scala". Switch Error Highlighting from "Built-in" to "Compiler".

#### Configuring VSCode

**Setup**

1. Install the following extensions:
    * [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
    * [Scala Syntax (official)](https://marketplace.visualstudio.com/items?itemName=scala-lang.scala)
    * [Scala (Metals)](https://marketplace.visualstudio.com/items?itemName=scalameta.metals)
2. Run `bin/vscode-setup` to generate a pom.xml file that allows VSCode to resolve dependencies
3. Open the workspace file `civiform.code-workspace` in VSCode
4. Metals automatically detects the project. Click `Import Build` at the prompt. 
<img width="676" alt="Screen Shot 2022-05-06 at 9 28 17 AM" src="https://user-images.githubusercontent.com/1870301/167177672-45594f16-cfea-4f8c-845d-8c266443acc3.png">
5. Choose `sbt` at the prompt for multiple build definitions. 
<img width="665" alt="Screen Shot 2022-05-06 at 9 51 24 AM" src="https://user-images.githubusercontent.com/1870301/167177704-cc3c290f-5cda-4776-88f5-a88573af3662.png">


**Troubleshooting**

* If source code isn't being indexed / symbols aren't found, you may need to clean the Java workspace. Trigger the command pallet and select `Java: Clean Java Language Server Workspace`
* If a new dependency is added, the `pom.xml` file may be out of date. You'll need to either add the dependancies manually, or re-run the bin/vscode-setup script to regenerate the file.
  * Here's a [guide to pom.xml setup](https://yongjie.codes/guides/java-play-sbt-on-vscode/) with the play framework

**Important files**

VSCode uses the following files

* server/pom.xml (maven dependancies for symbols)
* server/.settings/\*. (generated by `sbt eclipse`)
* civiform.code-workspace (opens the top level directory, and server)

## Running a local server

**Note:** the project makes heavy use of shell scripts for development tasks. Run `bin/help` to see a list of them.

1.  To run the application, navigate to the `civiform` directory you created when you cloned the repo and run the following:

    ```
    bin/run-dev
    ```

    To run the application using Azure instead of AWS, run:

    ```
    bin/run-dev –-azure
    ```

This will start up a dev instance of the application that uses Azurite, the Azure emulator, instead of local stack, the AWS emulator. This script sets an environment variable, `STORAGE_SERVICE_NAME` telling the application to run using the Azure emulator instead of AWS.

**Tip**: Once you have the local server working, you may want to set `USE_LOCAL_CIVIFORM=1` by default in your environment.  This will speed up reruns by not always redownloading the latest docker images (1-2GB each) which is not usually necessary.

**Warning**: There's a [known issue](https://github.com/civiform/civiform/issues/2230) where you may encounter a compile loop, the most reliable way to address that is to do `bin/sbt clean` before the above.

2. Once you see "Server started" in your terminal (it will take some time for the server to start up), you can access the app in a browser at http://localhost:9000. Be patient on the initial page load since it will take some time for all the sources to compile.

If you want to use the Log In flow see [those instructions](authentication-providers.md#testing) which include a one-time setup too.

The `bin/run-dev` script uses `docker-compose` (see [`docker-compose.yaml`](https://github.com/civiform/civiform/blob/main/docker-compose.yml)). It enables Java and Javascript hot-reloading: when you modify most files, the server will recompile and restart. This is pretty time-consuming on first page load, but after that, it's not so bad.

### Setting up routing for local testing

For login and file upload to work with your local server, you need to edit your `/etc/hosts` file to include the following:

```
127.0.0.1 dev-oidc
127.0.0.1 azurite
127.0.0.1 localhost.localstack.cloud
# Required for test AWS S3 bucket
127.0.0.1 civiform-local-s3.localhost.localstack.cloud
```
This provides a local IP route for the 'dev-oidc', 'azurite', and 'localstack' hostnames.

### Seeding the development database

Creating questions and programs in CiviForm is a bit time consuming to do manually with the UI, so in dev mode there is a controller action that generates several for you. You can access this feature at `localhost:<port number>/dev/seed`.

### Help! It's not working!

We know setting up a development environment can have some snags in the road. If something isn't working, check out our [Troubleshooting ](troubleshooting.md)guide or reach out on [Slack](https://join.slack.com/t/civiform/shared\_invite/zt-niap7ys1-RAICICUpDJfjpizjyjBr7Q).

## Running tests

This section will help you run CiviForm unit and browser tests in a basic way. For more information on _writing_ and _debugging_ these tests, check out the [Testing ](testing.md)guide.

### Running java unit tests

To run the java unit tests (includes all tests under [test/](https://github.com/civiform/civiform/tree/main/server/test)), run the following:

```
bin/run-test
```

If you'd like to run a specific test or set of tests, and/or save sbt startup time each time you run the test(s), use the following steps. This is recommended for developer velocity.

1.  Bring up an sbt shell inside the Docker container by running:

    ```
    bin/sbt-test
    ```
2.  Run any sbt commands! For example:

    ```
    testOnly services.question.types.QuestionDefinitionTest
    
    testOnly services.question.types.QuestionDefinitionTest -- -z getQuestion*

    test
    ```
    
### Running typescript unit tests

To run the unit tests in [app/assets/javascripts](https://github.com/civiform/civiform/tree/main/server/app/assets/javascripts), run the following:

```
bin/run-ts-tests
```

If you'd like to run a specific test or set of tests, run the following:

```
bin/run-ts-tests file1.test.ts file2.test.ts
```

### Running browser tests

To run the browser tests (includes all the [Playwright](https://playwright.dev) tests in [`civiform/browser-test/src/`](https://github.com/civiform/civiform/tree/main/browser-test/src), there are two steps:

1.  Bring up the local test environment using the AWS emulator. Leave this running:

    ```
    bin/run-browser-test-env
    ```

2.  Once you see "Server started" in the terminal from the above step, in a separate terminal run the tests in a docker container:

    ```
    bin/run-browser-tests
    ```

    Or, to run a test in a specific file, you can pass the file path relative to the `browser-test/src` directory:

    ```
    bin/run-browser-tests landing_page.test.ts
    ```

## Tips

Browser tests are heavy handed and can take a while to run.  You can focus the run to only execute a single `it` test or `describe` suite per file by prefixing it with `f`.  EG: `fit` and `fdescribe`.

## Creating fake data

To create Questions and Programs that use them, you need to log in as a "Program and CiviForm Admin" through http://localhost:9000/loginForm .

You can return to that screen to switch to a Guest user and back again to an Admin as needed.

## Debug logging

You can change the logging levels by editing [conf/logback.xml](https://github.com/civiform/civiform/blob/main/server/conf/logback.xml). This can help get a deeper understanding of what the server is doing for development.

## Running Coverage

To generate coverage report, run the following:
   
  ```
    bin/run-test-coverage
  ```

Navigate to server/code-coverage/report/html/index.html and see the detailed report of the code coverage data and also dig deep to see how much your implemented classes are covered.

## Running the formatter

To format your code, run:

```
bin/fmt
```

By default, this will format any files that have diffs compared to `origin/main`. Diffing against `origin/main`, however, can have a lot of noise if you haven't synced in a while. You can run this against a different diffbase with:

```
bin/fmt -d <diffbase>
```

For example:
```
# Diff against your local main branch:
bin/fmt -d main

# Diff against a specific commit:
bin/fmt -d <commit ID>

# Diff against the current commit:
bin/fmt -d HEAD

# Diff against the previous commit:
bin/fmt -d HEAD^
```

## What's next?

To learn more about how to make code contributions, head to [Technical contribution guide.](technical-contribution-guide.md)

To learn about the CiviForm tech stack and standards, head to [Technology overview ](technology-overview.md)and [Development standards.](development-standards.md)


# Getting started with Windows

There are special considerations involved with getting an environment set up in Windows, notably with:

1. Setting up Docker with a Windows Subsystem for Linux (WSL 2) backend
2. Setting up an IDE

## Docker (with WSL 2 backend)

We recommend running Docker in Windows with WSL 2.

### Windows Subsystem for Linux 2 (WSL 2)

Follow the [**Windows WSL Installation Guide**](https://docs.microsoft.com/en-us/windows/wsl/install-win10) to install WSL 2. There are two ways to install WSL 2:

1. Join the Windows Insider Program (not recommended)
2. Manually install WSL 2

Windows Insider Program may not be available on corporate devices.

#### System Requirements

You’ll need to have virtualization enabled on your machine. To check if you have virtualization enabled: open the Task Manager, go to the Performance tab, and under CPU verify `Virtualization: Enabled`.

![Windows task manager virtualization check](https://drive.google.com/uc?id=1jknfSqD\_qUEU8ulsFko52PG31eYs6QSm)

**Note: If virtualization is disabled, you’ll need to boot up into the BIOS and enable virtualization. If your machine does not support virtualization you won't be able to proceed.**

### Is it working?

Follow [**Windows guide to running WSL**](https://docs.microsoft.com/en-us/windows/wsl/wsl-config#ways-to-run-wsl) to run your virtual machine with WSL 2.

### Problems

#### "WSL is not recognized as the name of a cmdlet"

PowerShell does not recognize `wsl` command.

**Solution**: Reinstall WSL, making sure you run everything with administrator privileges.

#### "The service cannot be started"

Running `wsl` commands with PowerShell generates the following error:

![WSL error: service cannot be started](https://drive.google.com/uc?id=1pwt7Lp-\_sjS99lCv08EriPjM01K5hYtN)

**Solution**: [**From WSL GitHub issue 3386**](https://github.com/Microsoft/WSL/issues/3386)**: in the command prompt (not PowerShell)** with administrator privileges, execute `sc config LxssManager start=auto`

#### Unable to access the Microsoft Store

You won't be able to install a Linux distribution from the Microsoft Store if you don't have privileges to access the store.

**Solution**: You'll have to follow [**Microsoft's guide to manually install a distribution**](https://docs.microsoft.com/en-us/windows/wsl/install-manual#downloading-distributions).

#### WSL is not connected to the internet

In your WSL distribution, you can `ping` your favorite website to check for internet connectivity.

**Multi-part solution**: First, make sure your Linux distribution is connected to the right DNS server. The DNS server used by Windows can be identified with `IPCONFIG /ALL | FIND /I "DNS Server"` in the command prompt. The DNS server used by Linux is found in the `/etc/resolv.conf` file. They should be the same.

**If it's still broken**, follow the [**stackoverflow solution for no internet connection on WSL Ubuntu**](https://stackoverflow.com/questions/62314789/no-internet-connection-on-wsl-ubuntu-windows-subsystem-for-linux).

**If resetting your network says "Access is denied"**, try [**this solution**](https://davidvielmetter.com/tricks/netsh-int-ip-reset-says-access-denied/).

### Installing and configuring Docker

**Note: If you already had Docker installed before setting up WSL 2, you may want to uninstall and reinstall Docker so it installs with the required components for WSL 2.**

As you install Docker, make sure you install the required Windows components for WSL 2.

![Docker install with WSL components](https://drive.google.com/uc?id=1dT9X\_myIs5oDWdgJxMtSJy7vft94qRWh)

Once installed, check `Settings > General` to make sure WSL is used.

![Docker is using WSL](https://drive.google.com/uc?id=1KsFOCAgWTQ7evWJSD324Z1Biwjc47fvi)

Also check `Settings > Resources > WSL Integration` to select your Linux distribution.

![Docker is using specified Linux distribution](https://drive.google.com/uc?id=1qyuU2k\_fCiwCWglWms\_kv3EHnhbF2XJ0)

#### Problems

**No WSL 2 distribution found**

Docker may not be able to find any WSL 2 distributions. This means your distributions are configured with WSL 1.

![Docker error: no WSL 2 distributions found](https://drive.google.com/uc?id=1R7JMIZZuL4qZZ5Q4iALhJ7IWRa6dBTTx)

**Solution**: You can try to upgrade the distribution with `wsl --set-version <distro-name> 2` (e.g. `wsl --set-version Ubuntu-20.04 2`). If this doesn't work, you can uninstall and reinstall it.

**Linux distribution is not running**

For some reason, the distribution may not be running. You can check by running `wsl -l -v`. You'll want everything to be running with version 2:

![WSL all instances running with version 2](https://drive.google.com/uc?id=1JXndL2-z2Gqvhs5P-JClaIjlTxIAXVty)

**Solution**: Follow the [**Windows guide to running WSL**](https://docs.microsoft.com/en-us/windows/wsl/wsl-config#ways-to-run-wsl) to run your virtual machine with WSL 2.

**Docker fails to start**

**Solution**: In Docker Desktop, click the bug icon in the top right corner. Try `Restart Docker Desktop` and restart your computer. If that isn't strong enough, try `Reset to factory defaults` and restart your computer. If that still doesn't work, uninstall Docker and reinstall it.

### Is it working?

If things are working, you'll be able to run CiviForm in WSL with Docker using the following steps:

1. In WSL, clone [**the CiviForm repository**](https://github.com/civiform/civiform).
2. Run [**`/bin/run-dev`**](https://github.com/civiform/civiform/blob/main/bin/run-dev) to start the application.
3. In a browser, go to `localhost:9000`, and then wait for the application to load.

## Getting an IDE

We recommend Intellij. Here are [**other IDEs supported by Play**](https://www.playframework.com/documentation/2.8.x/IDE).

There are two options for configuring an IDE:

1. **(faster to set up, slower development time)**: store and edit files directly in Windows (e.g. C:) and use WSL to run the `bin` commands through `/mnt/c`.
2. **(more setup, faster development time)**: Store, edit, and run everything from within WSL 2.

### Developing with files stored in Windows

**Note: It's slow to run start and test the application when the files are stored in Windows because Docker runs in WSL, and will be accessing files through the `/mnt/c` mount.**

1. From WSL, `git clone` the CiviForm repository somewhere in your Windows file system (`/mnt/c/...`).
2. Install jdk 11 for Windows: [**http://jdk.java.net/java-se-ri/11**](http://jdk.java.net/java-se-ri/11)
3. Install sbt 1.3.13 for Windows: [**https://www.scala-sbt.org/download.html**](https://www.scala-sbt.org/download.html)
4. Install and configure IntelliJ in windows from [**https://www.jetbrains.com/idea/**](https://www.jetbrains.com/idea/)
5. Open IntelliJ, download the Scala plugin, and restart IntelliJ.
6. Open IntelliJ and open the [**`server/build.sbt`**](https://github.com/civiform/civiform/blob/main/server/build.sbt) file as a project.
7. Wait for a while for IntelliJ to index the project.

**Note: While waiting for step 6, you may need to restart IntelliJ. This step takes a long time, but it takes a long time the first time.****

The files are stored in Windows, and WSL is used to run `bin/run-dev` and other `bin` executables.

**Note: You may need to configure the IDE to use Linux line endings.**

### Developing with files stored in WSL

You'll need a UI to access an IDE in WSL. A couple options:

1. [https://medium.com/javarevisited/using-wsl-2-with-x-server-linux-on-windows-a372263533c3](https://medium.com/javarevisited/using-wsl-2-with-x-server-linux-on-windows-a372263533c3)
2. [https://www.nextofwindows.com/how-to-enable-wsl2-ubuntu-gui-and-use-rdp-to-remote](https://www.nextofwindows.com/how-to-enable-wsl2-ubuntu-gui-and-use-rdp-to-remote)

From within WSL:

1. `git clone` the CiviForm repository in WSL

**Note: Do not clone into `/mnt/c`**

1. Install zip using `sudo apt install zip unzip`
2. Install [sdkman](https://sdkman.io/install), and use it to install `sdk install java 11.0.10-open`, `sdk install sbt 1.3.13`, and `sdk install scala 2.13.1`.
3. Install IntelliJ.
4. Open IntelliJ, download the Scala plugin, and restart IntelliJ.
5. Open IntelliJ and open the [**`server/build.sbt`**](https://github.com/civiform/civiform/blob/main/server/build.sbt) file as a project.
6. Wait for a while for IntelliJ to index the project.

**Note: While waiting for step 6, you may need to restart IntelliJ. This step takes a long time, but it takes a long time the first time.**
