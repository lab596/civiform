# Dependencies

This page will describe the ways in which Civiform maintainers manage, track, and update dependencies.

## Dependency Plugin for sbt

To view all dependencies, open an sbt shell using `bin/sbt`, then run the `dependencyBrowseTree command`. This will open up an HTML page displaying a searchable version of the complete Java dependency graph (including transitive dependencies). 