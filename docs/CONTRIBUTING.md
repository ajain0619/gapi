# Contributing

Thanks for contributing!

This guide explains how to:

* Maximize the chance of your changes being accepted.
* Work on the project code base.
* Get help if you face any issue.

## Table Of Contents

[Code of Conduct](#code-of-conduct)

[Where to start?](#where-to-start)

[Style Guide](#style-guide)

[Getting Help](#getting-help)

## Code of Conduct

When contributing to this project, you certify that you you understand the information listed here. 

## Where to start?

Read the information that can be found into [README.md](../README.md) file and navigate through all its documents.

## Style Guide

### Code Style

* Be sure you follow all details defined into [CODESTYLE.md](../docs/CODESTYLE.md) document.
* Set up your IDE accordingly.

### Git Commit Messages

* Recommendation: Use the present tense 
  * "Add feature" instead of "Added feature".
* Recommendation: Use the imperative mood 
  * "Fix whatever with..." instead of "Fixes whatever with...".
* Limit the first line to 72 characters or less.
* Reference issues and pull requests liberally after the first line.
* Use meaningful information. 
* Avoid repetition. Each commit is unique, shows the uniqueness in there.

### Github Pull Request

* Add Ticket Id to your Pull Request title, followed by a colon and the Pull Request Information. 
  * Ex: `MX-12345: fix whatever`.
* Assign `labels` to show Pull Request status.
* Assign a `milestone` to show Pull Request feature priority. If it does not exist, please let us know.

#### PR pre-review checklist
In order to have your pull request reviewed quickly and efficiently, please make sure to have the following items ready before requesting a review:
* Build pipeline passes without errors. Make sure that code is formatted correctly and all unit, integration and acceptance tests pass.
* No merge conflicts are reported by Github.
* All Sonar reported issues are resolved, if possible. For legacy code, it might not be possible to resolve all issues, but no new issues should be introduced.
In addition, Sonar can flag false positives as issues. These can be resolved manually in Sonar UI. To foresee and resolve all possible Sonar lint issues during
the coding phase (before PR is even created) it's also handy to [setup SonarLint plugin](./how-to/HOWTO-SONARQUBE.md) for your IDE.
* You have at least one team approval.

**Note:** It is a good idea to initially put PR in draft mode, to make sure there are no build or Sonar issues. After that, the PR can be put in review mode.

## Getting Help

If you run into any trouble, please reach out to us on the issue you are working on.
