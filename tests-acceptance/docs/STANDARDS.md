# Standards

## Geneva Crud Test Structure and Standards 

The following are standards and guidelines for writing new, or updating existing tests in the geneva crud test suite. 
Different standards may apply to the geneva reports test suite.

You can use the Google Doc referenced below as an example. The file documents an investigation of the inventory
creation workflow, applications and sites feature. It includes a summary of the business logic and a list of the endpoints
that comprise the workflow. The assessment of existing tests can be ignored. Correlating tests have been implemented in
the inventorycreation -> applicationsandsites directory.
 
[Google Doc: Spike for Applications and Sites Creation Feature, Inventory Creation Workflow](https://docs.google.com/document/d/1QipoUiRQOhh5xdTU90w2jtjj0OUZcVaitbcSdmP_3vI)

### Test Organization and Structure

The decision has been made to organize tests according to workflows and features. The Google sheet referenced below 
lists the workflows and features that constitute the application. Consult with product for any questions and update the 
Google Sheet if there are any changes.

[Google Sheet: SSP Yahoo Workflow and Feature Summary](https://docs.google.com/spreadsheets/d/1SvZB7W-bc_prdtfOrmuIg6Pz29xU9_wEgEZvB7Zaxuw)
 
##### Directory Structure

workflow -> feature -> feature file

Example: 

   inventorycreation -> applicationsandsites -> feature file(s)
                                                            
##### Test Structure

* Each feature file should only include tests for one particular endpoint within the workflow and feature. This will
prevent the creation of large files that are hard to maintain.
* Use descriptive file names for feature files.
* Be descriptive when writing the Features, Scenarios and Steps. Also, remember that feature files are meant to be
high-level and are read by a wide, potentially non-technical audience.

### Minimal Testing Standards

##### Account and Role Authorization

Some functionality within the geneva-server codebase is restricted to users with specific account/role combinations
(e.g. Nexage Admin). Account/role authorization is typically enforced in service classes (at both the class 
and function level) using Spring Security in the geneva-server codebase. Include a happy path test for authorized users 
and tests to ensure that account/role combinations without authorization can not access such a resource. You can use the 
`sellers_sites_role_and_company_pid_authorization.feature` file in the geneva crud test suite as a reference.

Note that an endpoint may call multiple service classes, each with different authorized accounts and roles.

##### Company Level Authorization

Company level authorization ensures that the user's company pid equals the company pid of the requested resource (specified
in the URL path or path parameters). It is typically implemented in service classes (at both the class and function level)
using Spring Security in the geneva-server codebase. If company level authorization is enforced, include a test to 
ensure that a user for a particular company cannot access a resource for another company.

##### Arguments

Please follow standard testing practices. Include happy path tests, invalid and nonsensical values
for arguments when applicable. For example, for arguments that are object types test a valid value for happy path
tests as well as a null value and an invalid value (e.g. another publisher's id for arguments of type Long).
Only include tests for arguments that are relevant to the workflow. For example, if a particular workflow and feature
update a publisher's email, do not test that other attributes of a publisher are updated in the tests for that particular
workflow and feature.

### Implementation Details

* Be careful when using the annotation to restore the state of the DB. Do not use it if it's not necessary as it adds
overhead (time to run the tests).
* Use Scenario Outlines if the same test cases need to be iterated over using different values for arguments. You can use the
 _sellers_sites_role_and_company_pid_authorization.feature_ file in the geneva crud test suite as a reference.

### Syntax

TBD

### Considerations

The organization and structure as described was chosen because of a priority in testing BRXD related features. Depending
on the factors considered, this may not be the most favorable design. Particularly, feature files sometimes require some
setup to be in a particular state for testing. If the full functionality of one endpoint is divided up into 
multiple files, since the organization of tests is according to workflow and feature, that means the same set up is
required across all of the files testing that endpoint. This will result in relatively more overhead, most notably in
the time required to run the tests. Also, the organization and structure will be less intuitive to navigate and less 
conducive to determining overall test coverage. One will have to be familiar with the workflows and features, and a 
significant change in the application may constitute substantial refactoring. Organizing and structuring tests according
to controller classes in the geneva-server codebase (where endpoints are defined) would mitigate these considerations.
