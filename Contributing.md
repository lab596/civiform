**First, please read our [code of conduct](https://github.com/seattle-uat/civiform/blob/main/code_of_conduct.md).**

* [Getting started](#getting-started)
  * [A note on IDEs](#a-note-on-ides)
* [Issue tracking](#issue-tracking)
* [Pull requests](#pull-requests)
  * [Approval and merging](#approval-and-merging)

## Getting started

1. Download [Docker Desktop](https://www.docker.com/get-started) if you don't already have it
1. [Clone](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) this repository
1. In your terminal, navigate to the `civiform` directory you just created by cloning
1. Run `bin/run-dev` to confirm you can bring up the server (this also enables hot reloading)
  1. For Windows machines, use `docker build -t uat .` to build, then `docker-compose up` to run
1. Navigate to http://localhost:9000 to view the running app

### A note on IDEs

You may use whichever IDE you prefer, though DO NOT use the IDE's built-in sbt (Scala Build Tool) shell if it has one. Instead, run `bin/sbt` to bring up an sbt shell inside the Docker container. The sbt shell allows you to compile, run tests, and run any other sbt commands you may need

## Issue tracking

Development tasks are managed in the [GitHub issues](https://github.com/seattle-uat/civiform/issues) for this repository. When you begin working on an issue, please self-assign or comment on it indicating you're beginning work to avoid duplicate effort.

If you're just getting started, check out issues labeled with [Good First Issue](https://github.com/seattle-uat/civiform/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22). Also check out issues in the [next milestone](https://github.com/seattle-uat/civiform/milestones?direction=asc&sort=due_date&state=open) so you can work on the highest-priority tasks.

## Pull requests

When you're ready to submit your code, open a pull request with "Closes #X" to link the relevant issue.

It's easy for the intention of code review comments to be unclear or get misinterpreted. To help with communication, reviewers are encouraged to use [conventional comments](https://conventionalcomments.org/) and explicitly indicate that comments are `(blocking)`, where the discussion must be resolved for PR to be merged, or `(non-blocking)` where resolving the discussion is optional for the implementer.

### Approval and merging

Reviewers should grant approval if they do not feel additional review is necessary before merging. This does not necessarily mean no more changes are required before merging, but that any further changes are expected to be minor enough to not require review.

If the pull request does not require additional changes, the reviewer should merge it immediately after approving. Otherwise, once they have addressed all comments marked `(blocking)` or `nit`, the pull request author should either merge if able or re-request review and merging from a maintainer if not. Authors are encouraged to at least reply to `(non-blocking)` and `(if-minor)` comments if they do not address them with code changes.