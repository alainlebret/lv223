<!--
LV-223 (Colonization) multi-agent simulation

Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)

SPDX-License-Identifier: MIT
-->

# Planet GUI

## Overview

The Planet GUI is a Python-based graphical user interface designed to
visualize and interact with the planet simulation. It displays a dynamic grid
representing the planet's surface, where each cell corresponds to a specific
type of terrain or object. The GUI updates in real-time to reflect changes in
the planet's ecosystem and other events.

## Prerequisites

- Python 3.x installed on your system.
- All required Python libraries are listed in the `requirements.txt` file.

## Setup

To set up the planet GUI on your local machine, follow these steps:

1. Clone the repository:

   ```sh
   git clone https://github.com/alainlebret/lv223-2024
   cd lv223-2024/planet-gui
   ```

2. Install dependencies:
   Make sure you have Python 3.x installed, then install the required libraries using:

   ```sh
   pip install -r requirements.txt
   ```

## Running the application

Before launching the GUI, ensure that the planet server (part of the Maven project)
is running. Refer to the README in the `planet` module for detailed instructions on
starting the server.

To launch the GUI, run:

```sh
python src/planet_gui.py
```

If your main GUI script is located elsewhere, adjust the path accordingly.

## Features

- Real-time visualization:
  Displays an up-to-date grid view of the planet's surface, including terrain
  types and resource levels.
- Dynamic updates:
  Continuously reflects changes in the planet's ecosystem such as seasonal
  shifts, resource regeneration, and environmental events.
- User interaction:
  Allows users to monitor simulation progress.

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
