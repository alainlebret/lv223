<!--
LV-223 (Colonization) multi-agent simulation

Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)

SPDX-License-Identifier: MIT
-->

# Setting up IntelliJ IDEA

This document provides step-by-step instructions to correctly configure IntelliJ IDEA for working on the planet simulation project. It addresses common issues you may encounter, especially regarding JFuzzyLogic, multi-module Maven projects, and execution configurations.

## 1. Configuring JFuzzyLogic in IntelliJ IDEA

### Method 1: add JFuzzyLogic with Maven (recommended)

Step 1: Ensure the project is a Maven project

- Open IntelliJ IDEA and navigate to "pom.xml" (at the project root).
- Verify that it contains `<dependencies>` and `<groupId>`.
- If it's not recognized as a Maven project, add it manually:
- Go to File > Project Structure > Modules > Add > Maven.

Step 2: Add the JFuzzyLogic dependency to "pom.xml"

Insert the following inside `<dependencies>`:

```xml
<dependency>
    <groupId>net.sourceforge.jfuzzylogic</groupId>
    <artifactId>jfuzzylogic</artifactId>
    <version>2.1</version>
</dependency>
```

Step 3: Force IntelliJ to download dependencies

- Right-click "pom.xml" and select "Maven" > "Reload project".
- Or use the terminal:

  ```sh
  mvn clean install
  ```

### Method 2: Add JFuzzyLogic manually (if Maven fails)

Step 1: Download JFuzzyLogic.jar

- Download the JFuzzyLogic library.
- Place it inside the "lib/" folder of your project.

Step 2: Add the JAR to IntelliJ

- Go to File > Project Structure > Modules > Dependencies.
- Click "+" > "JARs or directories".
- Select lib/jFuzzyLogic.jar.

Step 3: Add the JAR to the classpath

- Go to Run > Edit Configurations.
- Under "Use classpath of module", select your main module.

1. Opening a multi-module project in IntelliJ IDEA
   The project consists of multiple modules (`planet` and `colony`). You must properly configure IntelliJ to manage them.

   Step 1: Check project structure
     - Go to File > Project Structure > Modules.
     - Verify that `planet` and `colony` are listed.
     - If missing, click "+" > "Import module" and select `planet` and `colony`.

   Step 2: Set up a parent maven project (if needed)

     - In "pom.xml" (root project), ensure it includes:

       ```xml
       <modules>
         <module>planet</module>
         <module>colony</module>
       </modules>
       ```

2. Running the project in IntelliJ
   You must start the planet server and colony client separately in IntelliJ.

   Step 1: Create a configuration for the planet server

     1. Go to Run > Edit Configurations > Add New Configuration.
     2. Choose "Application" and configure:
        - Name: Planet
        - Main Class: `fr.ensicaen.lv223.planet.Main`
        - Use classpath of module: Select `planet`
        - VM Options:

          ```default
          -cp target/classes:lib/*
          ```

        - Program arguments (example):

          ```default
          --years=<number_of_years> --delay=<turn_delay_ms> --scenario=<optional_scenario>
          ```

   Step 2: Create a configuration for the colony client
     3. Go to Run > Edit Configurations > Add New Configuration.
     4. Choose "Application" and configure:
        - Name: Colony
        - Main Class: `fr.ensicaen.lv223.colony.Main`
        - Use classpath of module: Select `colony`
        - VM Options:
          ```default
          -cp target/classes:lib/*
          ```

   Step 3: Start the applications
     - First, run planet.
     - Then, run colony to connect the robots.

## Alternative: Using Python scripts for execution

If IntelliJ setup becomes problematic, use the provided Python scripts:

Compile the project:

```sh
python3 scripts/compile.py planet
```

Run a predefined scenario:

```sh
python3 scripts/run_alone.py --scenario=move
```

Run the planet server with the colony client:

```sh
python3 scripts/run_with_colony.py
```
