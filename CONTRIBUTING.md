# <ins>Contributing to Eclipse Tractus-X</ins>

Thanks for your interest in this project.

# Table of Contents
1. [Project description](#project_description)
2. [Developer resources](#developer_ressources)
3. [Problem Eclipse Development Process](#eclipse_commitment)
4. [Eclipse Contributor Agreement](#eclipse_agreement)
5. [General contribution to the project](#general)
6. [Contributing as a Consultant](#consultant)
7. [Contributing as a Developer](#developer)
8. [Contact](#contact)


## <ins>Project description</ins><a name="project_description"></a>

The companies involved want to increase the automotive industry's
competitiveness, improve efficiency through industry-specific cooperation and
accelerate company processes through standardization and access to information
and data. A special focus is also on SMEs, whose active participation is of
central importance for the network's success. That is why Catena-X has been
conceived from the outset as an open network with solutions ready for SMEs,
where these companies will be able to participate quickly and with little IT
infrastructure investment. Tractus-X is meant to be the PoC project of the
Catena-X alliance focusing on parts traceability.

- https://projects.eclipse.org/projects/automotive.tractusx
- https://github.com/eclipse-tractusx/traceability-foss

## <ins>Developer resources</ins><a name="developer_ressources"></a>

Information regarding source code management, builds, coding standards, and
more.

- https://projects.eclipse.org/projects/automotive.tractusx/developer

The project maintains the source code repositories in the following GitHub organization:

- https://github.com/eclipse-tractusx/

## <ins>Eclipse Development Process</ins><a name="eclipse_commitment"></a>

This Eclipse Foundation open project is governed by the Eclipse Foundation
Development Process and operates under the terms of the Eclipse IP Policy.

- https://eclipse.org/projects/dev_process
- https://www.eclipse.org/org/documents/Eclipse_IP_Policy.pdf

## <ins>Eclipse Contributor Agreement</ins><a name="eclipse_agreement"></a>

In order to be able to contribute to Eclipse Foundation projects you must
electronically sign the Eclipse Contributor Agreement (ECA).

- http://www.eclipse.org/legal/ECA.php

The ECA provides the Eclipse Foundation with a permanent record that you agree
that each of your contributions will comply with the commitments documented in
the Developer Certificate of Origin (DCO). Having an ECA on file associated with
the email address matching the "Author" field of your contribution's Git commits
fulfills the DCO's requirement that you sign-off on your contributions.

For more information, please see the Eclipse Committer Handbook:
https://www.eclipse.org/projects/handbook/#resources-commit

## <ins>General contribution to the project</ins> <a name="general"></a>

### Maintaining [CHANGELOG.md](CHANGELOG.md) 
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html). 

_**For better traceability add the corresponding GitHub issue number in each changelog entry, please.**_

### git-hooks
Use git-hooks to ensure commit message consistency. 
Detailed pattern can be found here: [commit-msg](https://github.com/eclipse-tractusx/traceability-foss/blob/457cb3523e981ef6aed98355a7faf0ff29867c33/dev/commit-msg#L4)

#### How to use

````
cp dev/commit-msg .git/hooks/commit-msg && chmod 500 .git/hooks/commit-msg
````
For further information, please see https://github.com/hazcod/semantic-commit-hook

** Good practices**

The commit messages have to match a pattern in the form of:

````
< type >(optional scope):[<Ticket_ID>] < description >
````

````
fix(api):[TRACEFOSS-123] Fix summary what is fixed.
chore(repos):[TRACEFOSS-123] Configuration change of ci cd pipeline for new repository. 
docs(arc42):[TRACEFOSS-123] Added level 1 description for runtime view.
 
chore(helm): TRACEFOSS-1131- Moving the values under the global key - increasing the version  
````

### Dash IP
Prerequisites:
1) Create access token
   https://gitlab.eclipse.org/-/profile/personal_access_tokens

### Branching system and release workflow

We are using the [GitHub Flow](https://docs.github.com/en/get-started/quickstart/github-flow) for our branching system.

The general idea behind this approach is that you keep the main code in a constant deployable state.
You start off with the main branch, then a developer creates a feature branch directly from main.
After the feature is developed the code is reviewed and tested on the branch.
Only after the code is stable it can be merged to main.

<img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/images/github-flow-branching-model.jpeg" height="60%" width="60%"/>

### Commit messages
- The commit messages have to match a pattern in the form of:
  `<type>(optional scope): <Ticket_ID> <description>`
- Allowed types are `chore`, `fix` and `feature`.

Examples:
- `feature(users): #DDD description`
- `fix: #322 make X work again`

The detailed pattern can be found here: [commit-msg](https://github.com/eclipse-tractusx/traceability-foss/blob/main/dev/commit-msg)

#### How to use
```shell
cp dev/commit-msg .git/hooks/commit-msg && chmod 500 .git/hooks/commit-msg
```

## <ins>Contributing as a Consultant</ins><a name="consultant"></a>

### Conceptual work and specification guidelines
1. The prerequisite for a concept is always a github issue that defines the business value and the acceptance criteria that are to be implemented with the concept
2. Copy and rename directory /docs/#000-concept-name-template /docs/#<DDD>-<target-name>
3. Copy file /docs/Concept_TEMPLATE.md into new directory  /docs/#<DDD>-<target-name>

## <ins>Contributing as a Developer (Developer Hints)</ins><a name="developer"></a>

### Coding styles

To maintain coding styles we utilize [EditorConfig](https://editorconfig.org/) tool, see [configuration](.editorconfig)
file for the details.

### Static Code Analysis with SonarCloud

#### Overview
This project utilizes SonarCloud for static code analysis, which provides feedback on issues such as code smells, bugs, and security vulnerabilities.

#### How to Use (IntelliJ Example)
To connect your remote SonarCloud instance with your IntelliJ IDE, follow these steps:

1. Install the SonarLint plugin for IntelliJ:
    * Open IntelliJ and go to File > Settings (or IntelliJ IDEA > Preferences on macOS)
    * Select Plugins and search for "SonarLint"
    * Click on the "Install" button and follow the prompts to complete the installation

2. Configure SonarLint with your SonarCloud account:
    * Go to File > Settings (or IntelliJ IDEA > Preferences on macOS)
    * Select Other Settings > SonarLint
    * Click on the "+ Add" button and select "SonarCloud"
    * Add your project key
    * Click on "Create Token" and log in
    * Copy and paste the token
    * Click on the "Test Connection" button to verify the connection

3. Analyze your code with SonarLint:
    * Open your project in IntelliJ IDEA
    * Right-click on your project folder in the Project Explorer and select "SonarLint > Analyze <project_name>"

By following these steps, you can connect your remote SonarCloud instance with your IntelliJ IDE and analyze your code with SonarLint.

### Frontend coding guidelines
These guidelines are defined to maintain homogeneous code quality and style. It can be adapted as the need arises.

New and old developers should regularly review this [guide](https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/GUIDELINES.md) to update it as new points emerge and to sync themselves with the latest changes.

#### Angular Template Attribute Convention

Attributes in Angular template should be properly ordered by groups:

1. `*` - Structural Directives
2. `[]` - Attribute Directives or Input parameters
3. `()` - Event listeners
4. All other attributes

### IDE plugins

* IntelliJ IDEA ships with built-in support for .editorconfig files
* [Eclipse plugin](https://github.com/ncjones/editorconfig-eclipse#readme)
* [Visual studio code plugin](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)

#### Backend
##### Generate Dependencies

`mvn package org.eclipse.dash:license-tool-plugin:license-check -DskipTests=true -Ddash.summary=DEPENDENCIES_BACKEND -Ddash.fail=true`

##### Request Review

`mvn org.eclipse.dash:license-tool-plugin:license-check -Ddash.iplab.token=<token> -Ddash.projectId=automotive.tractusx`

#### Frontend
##### Generate Dependencies
`cd frontend`
`yarn install`
`yarn run dependencies:generate`

##### Request Review

`java -jar scripts/download/org.eclipse.dash.licenses-0.0.1-SNAPSHOT.jar yarn.lock -review -token <token> -project automotive.tractusx`

## Contact

Contact the Eclipse Tractus-X developers via the developer mailing list.

* https://accounts.eclipse.org/mailing-list/tractusx-dev

Contact the project developers via eclipse matrix chat.

* Eclipse Matrix Chat https://chat.eclipse.org/#/room/#tractusx-trace-x:matrix.eclipse.org
