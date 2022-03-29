Work in Progress

User created data (Questions, Programs, Applications) are all versioned and this doc discusses the versioning mechanism and the data life cycle that drives it.

# Glossary

| Term | Definition |
| ---  | --- |
| QP | Short hand for "Question or Program". Many concepts here apply to both. |
| Revision | A specific iteration/edit of a QP |
| Publish | The act of making all DRAFT QPs ACTIVE |


# Conceptual Overview

As applicants interact with CiviForm, they are seeing the system's Questions and Programs at a specific version of the entire system.  Admins also experience CiviForm this way but with the ability to edit the QPs and affect the version CiviForm is at.

## System versioning

Versioning is done monolithically across all QPs. Each time the Publish action is done, the system state is captured as a specific Version number.  The system version can be rolled back to a previous one, which then affects all QPs.

There is 1 single ACTIVE version and at most 1 DRAFT version at any given time. (more on these terms below)

## Questions and Programs

As a concept a QP is a specific named instance of a Question or Program such as a "Home Address" Question, or "Utility Discount Program" program that is seen and worked with in the user interfaces.  QPs can be updated as a new revision and published in a new system version.

# Data modeling

## Versions

There are a few tables that manage the versions and associates them with specific revisions of QPs

* versions: The source for system version IDs
* programs_versions: Associates Programs with a version ID
* questions_versions: Associates Questions with a version ID

QPs have a many-to-many relationship with versions, as a specific QP revision may be in many system versions.  As such it is most correct to say a QP is "associated" with a version(s) rather than it "having" a version.

Versions have a [lifecycle_stage](https://github.com/seattle-uat/civiform/blob/45631099ef4245f60a98d5ab8cb90178aab7cfb2/universal-application-tool-0.0.1/app/models/LifecycleStage.java#L12) of:

* ACTIVE (what applicants currently see)
* DRAFT (unpublished changes made by admins).
* OBSOLETE

There can only ever be 1 ACTIVE version and at most 1 DRAFT version.

As QPs are edited and the system published, the DRAFT version (and all associated QPs) will become ACTIVE and a new DRAFT version will be created. In this way the system versions are immutable once published.

## QPs

QPs have similar modeling in their respective tables.

A key detail of QPs is that a conceptual QP is uniquely identified by its name: name (for Questions) and adminName (for Programs).

As a QP is edited and published it will have many rows/revisions in its respective table, but all with the same name.

Through the lifecycle of the system the individual revisions will become associated with specific system versions.


# Lifecycle

We'll now tie everything together by talking about the lifecycle of the above and how that manifests in the data.

Each time the system is published the following steps happen

* Any ACTIVE QP not also associated with the DRAFT version is associated with it.
* The ACTIVE version's stage is set to OBSOLETE
* The DRAFT versions's stage is set to ACTIVE

Now all previously ACTIVE QPs are ACTIVE still along with the pending DRAFT changes for the edited QPS.

NOTE: Before the Publish, QPs with a DRAFT had 1 revision (A) associated with ACTIVE and another (B) with DRAFT. After the Publish, A is now part of the "historical record", and B is the current revision.  For all other ACTIVE QPs, they are associated with the previous ACTIVE revision and the new one.  An ACTIVE QP that never changes will end up being associated will all versions past its creation.

## QP

When a QP is first edited after being published, the current ACTIVE revision is copied forward into a new row in the respective table, and that row is associated with the current system DRAFT version through the version_question/versions_program table. If a DRAFT version currently doesn't exist it is created. If a previous revision doesn't, the QP is seeded as the first revision.

Subsequent additional modifications to a DRAFT associated QP overwrites the revisions row data; it doesn't create new rows.

Many Questions and Programs can be edited in this way, all of them associated with the same DRAFT version, and then ACTIVE when published.

## QP Interdependency

So far things have been simple.... ;)

The power of CiviForm is re-use, which means Programs are built from the same set of Questions, and Questions can refer to others for things like enumerators. So when a Question is edited and a new DRAFT associated revision is created, those dependency references must also be updated to the new revision of the Question to have any user visible effect.

This is done in a fairly straight forward manner.

When a Question is edited and a new DRAFT revision is created, ALL QPs that refer to the ACTIVE revision are found, and themselves edited to refer to the new Question revision.  Identical to the above lifecycle flow: if a DRAFT already exists, it is updated. If not it is created.

Note: If 1 question is referenced by ALL other QPs, then editing it will result in all ACTIVE QPs having a DRAFT revision.

# Life of a QP

To start with we'll have an ACTIVE version.

highlighted blocks around text represents new data changes, and for readability only the relevant parts of each table will be shown and each table will start at a different ID number.

## Add a Question

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
| `1`  | `ACTIVE` | 
| `2`  | `DRAFT` | 

The Question is associated with the DRAFT version

versions_questions
| questions_id | versions_id | 
| - | - |
| 20 | 2 |

## The question is updated

Because the Question is associated with the DRAFT version the data is updated in place, with no version changes.

questions
| id | name | description |
| - | - | - |
| 20 | Home Address | `The applicants home address` |

## Publish all

The Admin clicks the "publish all" button

The ACTIVE version becomes OBSOLETE and the DRAFT version is changed to ACTIVE. The Home Address question is now considered live to end users. 

versions
| ID | Stage |
| - | - |
| 1  | `OBSOLETE` | 
| 2  | `ACTIVE` | 

## Update the ACTIVE QUESTION

The Address Question is updated

Because the Question is associated with the ACTVIE version the data is copied forward into a new row

questions
| id | name | description |
| - | - | - |
| 20 | Home Address | The applicants home address |
| `21` | `Home Address` | `Where the applicants primarily resides` |


A DRAFT version is added because one does not exist.

versions
| ID | Stage |
| - | - |
| 1  | OBSOLETE | 
| 2  | ACTIVE | 
| `3` | `DRAFT` | 

The Question is associated with the DRAFT version

versions_questions
| questions_id | versions_id | 
| - | - |
| 20 | 2 |
| `21` | `3` |


We've updated our question but no Program uses it, so let's change that

## Add a Program.

A new Program is added for UDP (Utility Discount Program) that uses the latest Home Address Question ID 21.  we'll use short hand for the block_definiton.

programs
| ID | name | block_definition | 
| - | - | - |
| `40` | `UDP` | `QID 21` |


The program is associated with the existing DRAFT version

versions_programs
| programs_id | versions_id | 
| - | - |
| `40` | `3` |

## Publish all

The Admin clicks the "publish all" button

The ACTIVE version becomes OBSOLETE and the DRAFT version is changed to ACTIVE. The updated Home Address question and the new UDP program are now considered live to end users. 

versions
| ID | Stage |
| - | - |
| 1  | OBSOLETE | 
| 2  | OBSOLETE | 
| 3 | `ACTIVE` | 