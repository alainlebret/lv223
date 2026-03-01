<!--
LV-223 (Colonization) multi-agent simulation

Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)

SPDX-License-Identifier: MIT
-->

# Colony module

## Overview

The `colony` module simulates a colony of autonomous robots that interact with a
dynamic planetary environment. This module provides a framework for experimenting
with various artificial intelligence strategies such as reinforcement learning,
pathfinding, and collaborative behaviors. Initially, the robots are provided with
minimal functionality (basic methods and a simple example for the `Cartographer`),
and it is your task to extend and enhance these behaviors.

## Prerequisites

- Java JDK 11 or higher
- Apache Maven 3.6.0 or higher

## Project structure

The main packages and classes in the `colony` module include:

- **`fr.ensicaen.lv223.colony`**  
  Contains the main application entry point (`Main`) and the core management class
  (`ColonyManager`), which handles the connection to the planet server and coordinates
  the robot agents.

- **`fr.ensicaen.lv223.colony.communication`**  
  Implements the communication facade (`RobotEnvironmentFacade`) that abstracts the
  network details from the robot logic. This class is responsible for building JSON
  requests, sending them using the class `PlanetServerConnection`, and processing the
  responses.

- **`fr.ensicaen.lv223.colony.robot`**  
  Contains the various robot types (e.g., `Cartographer`, `Miner`, `Harvester`,
  `Farmer`, `Pipeliner`, and `Centralizer`). All these classes extend an abstract
  class `Robot` that provides common attributes and methods for movement and action
  execution.

- **`fr.ensicaen.lv223.colony.decision`**  
  Provides support classes for decision-making, such as `LocalMap`, which represents
  a robot’s perception of the environment, used for pathfinding and navigation.

## Diagrams

Below is a simplified class diagram illustrating the key components of the colony
module:

<!-- The following diagram is generated from PlantUML and then converted to SVG -->
![Colony Module Class Diagram](colonyDiagCla.svg)

## Getting Started

### Compilation

To compile the `colony` module, you can use the provided Python script for 
cross-platform compatibility:

```sh
python3 scripts/compile.py colony
```

### Running the colony client

To run the colony application (client), navigate to the colony directory and
execute:

```sh
mvn exec:java -Dexec.mainClass="fr.ensicaen.lv223.colony.Main"
```

This will start the colony, which will attempt to connect to the planet server
and begin coordinating the robot activities.

### Extending the colony

The `colony` module is intentionally provided as a skeleton framework to encourage
you to develop your own AI strategies. Consider the following extension points:

- Implement advanced decision-making algorithms:
  Enhance the methods in your robot classes (e.g., `move()`, `performAction()`) to
  include reinforcement learning, pathfinding algorithms (like A*), or other AI
  techniques.
- Improve the communication facade:
  Modify or extend the `RobotEnvironmentFacade` to experiment with alternative
  communication strategies or to support additional actions.
- Enhance perception and navigation:
  Develop the `LocalMap` class further to provide more detailed environmental data
  and improved navigation capabilities for the robots.
- Create new robot types:
  Extend the abstract `Robot` class to create additional specialized agents (e.g.,
  `Builder`, etc.). Implement the abstract `performAction()` method with custom logic.

## Additional resources

For further technical details, refer to the main project architecture guide
(available in both English and French in the "doc" folder). This guide includes
detailed diagrams, descriptions of the communication protocol, and an overview
of the server lifecycle.

## License

This project uses a **dual licensing scheme**:

- **MIT License** (see `LICENSE`)  
  Applies to the software code, including the reference code and the code produced
  by students.  
  Students are explicitly allowed to publish their work (e.g. GitHub, portfolio,
  CV demonstrations).

- **Educational License** (see `LICENSE-EDUCATION.md`)  
  Applies to the official project statement, figures, and pedagogical materials.
  These materials may be used and adapted for non-commercial educational purposes.
  Commercial use or redistribution implying institutional endorsement requires
  prior permission from the author.

This project also depends on third-party libraries (e.g. **JFuzzyLogic**, LGPL),
which remain under their respective licenses.
