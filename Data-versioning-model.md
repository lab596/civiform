Work in Progress

User created data (Questions, Programs, Applications) are all versioned and this doc discusses the versioning mechanism and the data life cycle that drives it.

For ease of phrasing, QP will mean "Question/Program" in this page as many of the concepts are the same for each.

# Questions and Programs

## Conceptual Overview

As a concept a QP is a specific named instance of a Question or Program such as a Home Address Question, or the Utility Discount Program that is seen and worked with in the user interfaces.  A QP can change over time.

### Versions

Versioning is done at the system level with all modified data advancing to a new version system-wide together.  It is most correct, and less confusing, to talk about a QP as being *associated* with a version(s); rather than having a version themselves.

### Lifecycle

A Question or Program can conceptually be in one of three states: ACTIVE, DRAFT, DELETED.  A Draft question may also have a previous active version or not for a new one.

As a QP changes states the representative data will get copied forward, and the *[stage](https://github.com/seattle-uat/civiform/blob/45631099ef4245f60a98d5ab8cb90178aab7cfb2/universal-application-tool-0.0.1/app/models/LifecycleStage.java#L12)* of the relevant data-copies are updated which included OBSOLETE also. (more on this later)

Resident users will see the ACTIVE version of a QP, while admins will instead see any DRAFT versions that exist for a QP.

## Data Model

### QP modeling

As a QP is first created or modified-after-publish a new row in the respective table is created and associated with the system DRAFT version; that version is created if it doesn't exist.  Subsequent additional modifications to the DRAFT associated row overwrites the row data; it doesn't create new rows.

Once Publish All happens the current DRAFT rows are "locked in" as the ACTIVE version and their definitions are immutable. (Their state may change though, per the below version tracking system).  In this way any ACTIVE QP is available in the database historically.

The effective Key for a QP is the `name` field in its table.  All updates to a QP will have the same name in the new rows which represent its change history.

### Version tracking

There are a few tables that manage the versions and associates them with QPs

* versions: The source for system version IDs
* programs_versions: Associates Programs with a version ID
* questions_versions: Associates Questions with a version ID

QPs have a many-to-many relationship with versions.

#### Versions table

Versions is the master list of all system wide versions.

The key points are that there is always 1 ACTIVE version in the system and at most 1 DRAFT version for the entire system.

There should only be a DRAFT version if a QP has been added/modified but not published to ACTIVE.  When a user "publishes" all drafts the DRAFT version ID state is simply updated to be the ACTIVE one; and the ACTIVE one is set to OBSOLETE.

#### QP versions tables

As a QP changes, its new data is written as a new row in the respective tables and that QP id is associated with the current Draft version in the respective versions table.  In this manner a QPs changes don't change the current ACTIVE data and are specifically associated with a new system version.

## Life of a QP

To start with we'll have an ACTIVE version.

Bold represents new data changes, and for readability only the relevant parts of each table will be shown and each table will start at a different ID number.

### Add a Question

An admin adds a Question named "Home Address".

The Question is added:

questions
| id | name | description |
| - | - | - |
| 20 | Home Address | address |

A DRAFT version is added because one does not exist.
versions
| ID | Stage |
| - | - |
| **1**  | **ACTIVE** | 
| **2**  | **DRAFT** | 

The Question is associated with the DRAFT version
versions_questions
| questions_id | versions_id | 
| - | - |
| 20 | 2 |

### The question is updated

Because the Question is associated with the DRAFT version the data is updated in place, with no version changes.

questions
| id | name | description |
| - | - | - |
| 20 | Home Address | **The applicants home address** |

### Publish all

The Admin clicks the "publish all" button

The ACTIVE version becomes OBSOLETE and the DRAFT version is changed to ACTIVE. The Home Address question is now considered live to end users. 

versions
| ID | Stage |
| - | - |
| 1  | **OBSOLETE** | 
| 2  | **ACTIVE** | 


No Program uses it though, so let's change that

### Add a Program.

A new Program is added for UDP (Utility Discount Program) that used the Home Address Question ID 20.  we'll use short hand for the block_definiton.

programs
| ID | name | block_definition | 
| - | - | - |
| **40** | **UDP** | **QID 20** |


A DRAFT version is added because one does not exist.

versions
| ID | Stage |
| - | - |
| 1  | OBSOLETE | 
| 2  | ACTIVE | 
| **3** | **DRAFT** | 

The program is associated with the DRAFT version

versions_programs
| programs_id | versions_id | 
| - | - |
| **40** | **3** |