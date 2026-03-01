<!--
LV-223 (Colonization) multi-agent simulation

Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)

SPDX-License-Identifier: MIT
-->

# Planet server – Simulation of an evolving planetary ecosystem

The planet server is a Java-based application that simulates a dynamic
planetary ecosystem. It acts as the backend of the simulation, managing
environmental changes, seasonal cycles, and various planetary events. It
serves two main clients:

- The Python-based GUI (`planet-gui`) for real-time visualization.
- The colony client (`colony`), which simulates robotic agents interacting
  with the planet.

## Prerequisites

Before running the planet server, ensure you have the following installed:

- Java JDK 11 (or higher)
- Apache Maven 3.6.0 (or higher)
- Python 3

*Note:* If you are using IntelliJ IDEA, please refer to the guide "[Setting up in IntelliJ IDEA](doc/intellij-setup-en.md)"
(English version) or "[Configuration d'IntelliJ IDEA](doc/intellij-setup-fr.md)" (French version).

## Project Structure

```plaintext
planet/
│
├── lib/                # External libraries (e.g., JFuzzyLogic)
│   └── JFuzzyLogic.jar # Fuzzy Logic Library
│
├── src/                # Java source files
│   ├── main/           # Main application source code
│   └── test/           # Unit tests
│
├── target/             # Compiled output directory
├── pom.xml             # Maven configuration file
├── config.json         # Simulation configuration file
└── README.md           # This file
```

Each folder is designed to isolate responsibilities and simplify maintenance.
The "lib/" folder contains external dependencies that are manually installed
if needed.

## Setup and installation

1. Clone the repository
   Clone the repository and navigate to the planet directory:

   ```sh
   git clone https://github.com/alainlebret/lv223.git
   cd lv223/planet
   ```

2. Build the project
   For cross-platform compatibility and to avoid Maven dependency issues, use
   the provided Python script:

   ```sh
   python3 ../scripts/compile.py planet
   ```

   This script automatically:

   - Cleans previous builds.
   - Compiles the planet server.
   - Installs necessary dependencies (including JFuzzyLogic).

   No need to run Maven manually!

## Running the server

### Running in standalone mode

To start the planet server in demonstration (standalone) mode, use the
following command:

```sh
python3 ../scripts/run_alone.py --scenario=<scenario_name>
```

Replace <scenario_name> with one of the predefined scenarios:

| Scenario name |                   Description                         |
| ------------- | ----------------------------------------------------- |
| `see`         | Simulates a `Cartographer` robot scanning the planet  |
| `move`        | Demonstrates basic robot movement                     |
| `scan`        | Robots scan the environment while moving              |
| `cultivate`   | Simulates `Farmer` robots cultivating crops           |
| `harvest`     | Simulates `Harvester` robots gathering resources      |
| `pipe`        | Simulates `Pipeliner` robots constructing pipelines   |
| `mine`        | Simulates `Miner` robots extracting minerals          |
| `pump`        | Simulates water extraction by `Farmer` robots         |
| `var`         | Mixed scenario combining multiple actions             |

Example:

```sh
python3 ../scripts/run_alone.py --scenario=mine
```

### Running with the colony client

To run the planet server together with the colony client, use:

```sh
python3 ../scripts/run_with_colony.py
```

This script launches:

- The planet server (backend simulation)
- The Python GUI (real-time visualization)
- The colony client (robotic agent interactions)

## Setting up in IntelliJ IDEA

If you are using IntelliJ IDEA, follow these steps:

1. Open the project:
   - Launch IntelliJ IDEA.
   - Select "Open" and choose the "`planet/`" folder.
   - IntelliJ will detect it as a Maven project.
2. Install dependencies (if needed):
   If the build fails due to missing JFuzzyLogic, install it manually using:

   ```sh
   mvn install:install-file -Dfile=lib/jFuzzyLogic.jar -DgroupId=net.sf.jfuzzylogic -DartifactId=jfuzzylogic -Dversion=2.1 -Dpackaging=jar
   ```

3. Run the `Main` class:
   - Navigate to `fr.ensicaen.lv223.planet.Main` within IntelliJ.
   - Right-click and select "Run" to start the application.

If issues persist, prefer using the provided Python scripts (`compile.py`, `run_alone.py`, `run_with_colony.py`).

## Key Features

- Dynamic ecosystem simulation:
  The server simulates a planetary ecosystem with evolving terrain, seasonal
  changes, and resource management.
- Real-time communication:
  Communicates with both the GUI and the colony client using JSON-based requests
  over sockets.
- Robust protocol:
  Ensures data consistency and handles errors through strict validation and
  comprehensive error messages.

## Communication protocol

The planet server exchanges data with clients using JSON messages. This section
briefly describes the expected formats.

### Request format

Each client request must include:

- `"action"`: The operation to perform (e.g., `"move"`, `"scan"`, `"mine"`, `"endOfTurn"`).
- `"robotId"`: The unique identifier for the robot.
- `"robotType"`: The type of the robot (e.g., `"Miner"`, `"Cartographer"`, `"Harvester"`).
- `"parameters"`: An object containing action-specific details (coordinates, resource
  amounts, etc.).

Example request – `"move"`:

```json
{
  "action": "move",
  "robotId": "r2d2",
  "robotType": "Miner",
  "parameters": {
    "x": 3,
    "y": 5,
    "newX": 4,
    "newY": 5
  }
}
```

### Response format

The server responds with a JSON message containing:

- `"status"`: `"success"` or `"error"`.
- `"action"`: the action performed.
- `"message"`: a description of the outcome (especially on errors).
- `"affectedRobots`": a list of robots impacted by the action.
- `"detectedCells"`: a list of scanned cells for `"scan"` actions.

Example successful response – `"move"`:

```json
{
  "status": "success",
  "action": "move",
  "message": "Move completed successfully.",
  "affectedRobots": [
    {
      "id": "r2d2",
      "type": "Miner",
      "injury": 0
    }
  ]
}
```

### Error handling

In case of an error (e.g., invalid coordinates), the response might be:

```json
{
  "status": "error",
  "action": "move",
  "message": "Invalid destination: Robot cannot move to water",
  "affectedRobots": [
    {
      "id": "r2d2",
      "type": "Miner",
      "injury": 0
    }
  ]
}
```

### Adding new request types

To extend the functionality of the server:

1. Add a new constant in the `RequestType` enum (in package `fr.ensicaen.lv223.planet.server`).
2. Implement a new `RequestHandler` that processes the specific logic.
3. Register the handler in the `initializeRequestHandlers()` method of `PlanetServer`.

Refer to the existing handlers (e.g., `MoveRequestHandler`, `MineRequestHandler`) as examples.

## Configuration

The simulation settings are stored in "`src/main/resources/config.json`". Modify
this file to adjust parameters such as:

- planet dimensions ;
- initial robot positions ;
- environmental settings (e.g., resource levels, seasonal effects).

## Testing

To run unit tests for the planet server, execute:

```sh
mvn test
```

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
