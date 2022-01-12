Work in Progress

User created data (Questions, Programs, Applications) are all versioned and this doc discusses the versioning mechanism and the data life cycle that drives it.

For ease of phrasing, QP will mean "Question/Program" in this page as many of the concepts are the same for each.

# Questions and Programs

## Conceptual Overview

As a concept a QP is a specific named instance of a Question or Program such as a Home Address Question, or the Utility Discount Program that is seen and worked with in the user interfaces.

### Versions

Versioning is done at the system level with all modified data advancing to a new version system-wide together.  It is most correct, and less confusing, to talk about a QP as being *associated* with a version(s); rather than having a version themselves.

### Lifecycle

A Question or Program can conceptually be in one of three states: ACTIVE, DRAFT, DELETED.  A Draft question may also have a previous active version or not for a new one.

As a QP changes states the representative data will get copied forward, and the *[stage](https://github.com/seattle-uat/civiform/blob/45631099ef4245f60a98d5ab8cb90178aab7cfb2/universal-application-tool-0.0.1/app/models/LifecycleStage.java#L12)* of the relevant data-copies are updated which included OBSOLETE also. (more on this later)

## Data Model

### Version tracking

There are a few tables that manage the versions and associates them with PQs

* versions: The source for version IDs
* programs_versions: Associates Programs with a version ID
* questions_versions: Associates Questions with a version ID

QPs have a many-to-many relationship with versions.

#### Versions table

Versions is the master list of all system wide versions.

The key points are that there is always 1 ACTIVE version in the system and at most 1 DRAFT version for the entire system.

There should only be a DRAFT version if a QP has been added/modified but not published to ACTIVE.  When a user "publishes" all drafts the DRAFT version ID state is simply updated to be the ACTIVE one; and the ACTIVE one is set to OBSOLETE.

#### QP versions tables

As a QP changes, its new data is written as a new row in the respective tables and that QP id is associated with the current Draft version in the respective versions table.  In this manner a QPs changes, don't change the current ACTIVE data and are specifically associated with a new system version