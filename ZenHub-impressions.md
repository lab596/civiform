ZenHub does a pretty good job of two-way sync. Some quirks:

* Milestones in ZenHub map 1:1 to milestones in GitHub. ZenHub has an additional field of "start date" for milestones that aren't saved in GitHub issues, though.
* Epics in ZenHub are represented in GitHub as plain old issues with an "Epic" label.
* GitHub projects and ZenHub projects seem to be unsync-able -- neither shows up in the other's UI.
* In ZenHub, you can assign issues to Epics, but that doesn't seem to be reflected anywhere in the corresponding GitHub issue.
* In ZenHub, you can place issues in columns (In Progress, Backlog, Done, etc), but that data isn't reflected in GitHub anywhere.

Basically, most things from GitHub are represented in ZenHub, but not vice-versa. ZenHub has advanced features that aren't stored anywhere in their corresponding GitHub artifacts.