<<<<<<< HEAD
<!--
LV-223 (Colonization) multi-agent simulation

Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)

SPDX-License-Identifier: MIT
-->

# LV-223 – Planet simulation and robot colony project

The planet simulation project is an educational framework designed to explore simple artificial intelligence strategies within a dynamic planetary ecosystem. It consists of three modules:

- `planet`: a Java-based simulation server for the planetary environment.
- `colony`: a prototype robot colony interacting with the planet.
- `planet-gui`: a Python GUI for real-time visualization.

This project provides a simulation where robots interact within a planetary system, performing mapping, resource extraction, farming, and infrastructure development.

## Prerequisites

Before cloning this project, ensure you have the following installed:

- Java JDK 11 or higher.
- Apache Maven 3.6.0 or higher.
- Python 3.

Note: If you are using IntelliJ IDEA, see the document "[Setting up in IntelliJ IDEA](doc/intellij-setup-en.md)" (English version) or "[Configuration d'IntelliJ IDEA](doc/intellij-setup-fr.md)" (French version).

## Quick start

1. Clone the repository

   ```sh
   git clone https://github.com/alainlebret/lv223.git
   cd lv223
   ```

2. Compile the server and colony applications

   Just run the following command:

   ```sh
   scripts/compile.py
   ```

   This compiles both the planet server (`planet` module) and the colony client (`colony` module).

   - To compile only the planet server:

     ```sh
     scripts/compile.py planet
     ```

   - To compile only the colony client:

     ```sh
     scripts/compile.py colony
     ```

   This improves cross-platform compatibility (Ms-Windows, Mac OS X, Linux) and eliminates Maven dependency issues.

3. Running scenarios

   The project includes predefined scenarios demonstrating different planetary interactions.

   To run a scenario, use:

   ```sh
   scripts/run_alone.py --scenario=<scenario_name>
   ```

   Available scenarios:

   | Scenario name   | Description                               |
   | --------------- | ----------------------------------------- |
   | `see`           | Cartographer robot scans the planet       |
   | `move`          | Basic robot movement demonstration        |
   | `scan`          | Robots scan while moving                  |
   | `cultivate`     | Farmer robots cultivate crops             |
   | `harvest`       | Harvester robots gather resources         |
   | `pipe`          | Pipeliner robots construct infrastructure |
   | `mine`          | Miner robots extract minerals             |
   | `pump`          | Water pumping by farmer robots            |
   | `var`           | Mixed scenario combining multiple actions |

   Example:

   ```sh
   scripts/run_alone.py --scenario=mine
   ```

4. Running with the colony client

   To start the planet server and the colony client together:

   ```sh
   scripts/run_with_colony.py
   ```

   This launches:

   1. The planet server (backend simulation).
   2. The Python GUI (visualizing the planet in real time).
   3. The colony client (interacting with the planet).

## Project structure

```default
lv223/
│
├── colony/               # Robot colony simulation (Java)
├── doc/                  # Documentation and educational assets
├── planet/               # Planet simulation server (Java)
├── planet-gui/           # GUI for visualizing the planet (Python)
├── scripts/              # Python scripts replacing Bash scripts
│   ├── compile.py        # Compile the project
│   ├── run_alone.py      # Run Planet Server & GUI alone
│   ├── run_with_colony.py # Run Planet Server, GUI & Colony
│
├── LICENSE.md            # Main software license
├── LICENSE-EDUCATION.md  # Educational content license
├── README.md             # Project overview & instructions
├── pom.xml               # Maven configuration
```

## Detailed instructions

For in-depth technical details, check the architecture guide:
[English version](doc/guide-en.md) or [French version](doc/guide-fr.md).

You can also find additional setup and debugging steps inside each subdirectory’s
`README.md`.

## License

This project uses a **dual licensing scheme**:

- **MIT License** (see `LICENSE.md`)  
  Applies to the software code, including the reference code and the code produced
  by students.  
  Students are explicitly allowed to publish their work (e.g. GitHub, portfolio,
  CV demonstrations).

- **Educational License** (see `LICENSE-EDUCATION.md`)  
  Applies to the official sample project statement, figures, and pedagogical materials.
  These materials may be used and adapted for non-commercial educational purposes.
  Commercial use or redistribution implying institutional endorsement requires
  prior permission from the author.

This project also depends on third-party libraries (e.g. **JFuzzyLogic**, LGPL),
which remain under their respective licenses.

## License scope by path

To avoid ambiguity, license scope is defined by repository path:

- `colony/**`, `planet/**`, `planet-gui/**`, `scripts/**`, and build/config files:
  licensed under `LICENSE.md` (MIT).
- `doc/sample-pedagogical-project/**` and related pedagogical statement content:
  licensed under `LICENSE-EDUCATION.md`.
- `doc/sample-pedagogical-project/figures/**` (and any third-party material under `doc/`):
  each file must be declared in `doc/THIRD_PARTY.md` with source, author, license,
  modification status, and import date.

No file with unknown or incompatible rights should be committed under `doc/`.

## Author

Alain Lebret, ENSICAEN, 2019–2026

## Contributors

The following individuals have contributed significantly to this project:

- **Alain Lebret** – Major contributor, responsible for the overall architecture,
  pedagogical design, and development of the planet simulation.
- **Florian Richard**, **Loick Le Prevost**, **Julien Monteil** and
  **Antoine Lucerna-Grives** – Contributions to the implementation of the planet
  and robot colony.
- **Alexis Leray** – Key bug fixes and improvements in the communication protocol.
- **Erwann Taupin** and **Tom Hill** – Key bug fixes and improvements in the 
  architecture of the colony side.
- **Clément Daubeuf** – Fixed the jFuzzyLogic reccurrent problem by adding the
  lib-local repertory.
- **Lothaire Guée**, **Esteban Cochepain** and **Emmanuel Nicolle** – Code reviews
  and documentation improvements.

We also thank other contributors for their valuable bug fixes and suggestions.
=======
# lv223



## Getting started

To make it easy for you to get started with GitLab, here's a list of recommended next steps.

Already a pro? Just edit this README.md and make it your own. Want to make it easy? [Use the template at the bottom](#editing-this-readme)!

## Add your files

* [Create](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#create-a-file) or [upload](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#upload-a-file) files
* [Add files using the command line](https://docs.gitlab.com/topics/git/add_files/#add-files-to-a-git-repository) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin https://gitlab.ecole.ensicaen.fr/alebret/lv223.git
git branch -M master
git push -uf origin master
```

## Integrate with your tools

* [Set up project integrations](https://gitlab.ecole.ensicaen.fr/alebret/lv223/-/settings/integrations)

## Collaborate with your team

* [Invite team members and collaborators](https://docs.gitlab.com/ee/user/project/members/)
* [Create a new merge request](https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html)
* [Automatically close issues from merge requests](https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically)
* [Enable merge request approvals](https://docs.gitlab.com/ee/user/project/merge_requests/approvals/)
* [Set auto-merge](https://docs.gitlab.com/user/project/merge_requests/auto_merge/)

## Test and Deploy

Use the built-in continuous integration in GitLab.

* [Get started with GitLab CI/CD](https://docs.gitlab.com/ee/ci/quick_start/)
* [Analyze your code for known vulnerabilities with Static Application Security Testing (SAST)](https://docs.gitlab.com/ee/user/application_security/sast/)
* [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://docs.gitlab.com/ee/topics/autodevops/requirements.html)
* [Use pull-based deployments for improved Kubernetes management](https://docs.gitlab.com/ee/user/clusters/agent/)
* [Set up protected environments](https://docs.gitlab.com/ee/ci/environments/protected_environments.html)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!). Thanks to [makeareadme.com](https://www.makeareadme.com/) for this template.

## Suggestions for a good README

Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
>>>>>>> 9b113e0 (Initial commit)
