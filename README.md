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