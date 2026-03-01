#!/usr/bin/env python3
# LV-223 (Colonization) multi-agent simulation
#
# Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
#
# SPDX-License-Identifier: MIT

import os
import sys
import subprocess
import time
import platform
import argparse

# Constants
CP_SEPARATOR = ";" if platform.system() == "Windows" else ":"
PLANET_MAIN_CLASS = "fr.ensicaen.lv223.planet.Main"
PLANET_GUI_PATH = os.path.join("planet-gui", "src", "planet_gui.py")
TEMP_FILE_PATH = os.path.join(".tmp", "colony_client_ready")
DEFAULT_SLEEP_TIME = 2    # seconds to wait for server initialization
GUI_WAIT_TIMEOUT = 30     # timeout in seconds for GUI readiness

# Ensure the .tmp directory exists
os.makedirs(".tmp", exist_ok=True)

def run_process(command, cwd):
    """Start a process with the specified command in the given working directory."""
    try:
        return subprocess.Popen(command, cwd=cwd)
    except Exception as e:
        print(f"Failed to start process in {cwd}: {e}")
        sys.exit(1)

def shutdown_process(process, name, timeout=5):
    """Terminate a subprocess and force kill it if it does not exit in time."""
    if process is None:
        return
    if process.poll() is not None:
        return
    print(f"Stopping {name}...")
    process.terminate()
    try:
        process.wait(timeout=timeout)
    except subprocess.TimeoutExpired:
        print(f"{name} did not exit after {timeout}s. Killing it...")
        process.kill()
        process.wait(timeout=timeout)

def wait_for_file(filepath, timeout):
    """Wait until the specified file exists or timeout is reached."""
    start_time = time.time()
    while not os.path.exists(filepath):
        if time.time() - start_time > timeout:
            print(f"Timeout waiting for file {filepath}.")
            sys.exit(1)
        time.sleep(1)

def build_classpath(module_path):
    """Prefer packaged dependencies from target/lib, fallback to legacy lib directory."""
    target_lib = os.path.join(module_path, "target", "lib")
    if os.path.isdir(target_lib):
        return f"target/classes{CP_SEPARATOR}target/lib/*"
    return f"target/classes{CP_SEPARATOR}lib/*"

def start_planet_server(years, delay, scenario):
    """Start the Planet Server with the given options."""
    print("Starting the planet server...")
    planet_dir = os.path.join(os.getcwd(), "planet")
    planet_cmd = [
        "java",
        "-cp",
        build_classpath(planet_dir),
        PLANET_MAIN_CLASS,
        f"--years={years}",
        f"--delay={delay}",
        f"--scenario={scenario}"
    ]
    return run_process(planet_cmd, cwd=planet_dir)

def start_gui():
    """Start the Planet GUI."""
    print("Launching the planet GUI...")
    gui_cmd = [sys.executable, PLANET_GUI_PATH]
    return run_process(gui_cmd, cwd=os.getcwd())

def main():
    parser = argparse.ArgumentParser(
        description="Run the Planet Server and GUI in demonstration mode (without Colony Client)."
    )
    parser.add_argument("--years", type=int, default=1, help="Number of simulation years (default: 1)")
    parser.add_argument("--delay", type=int, default=1000, help="Delay between turns in ms (default: 1000)")
    parser.add_argument("--scenario", type=str, default="see", help="Demonstration scenario (default: see)")
    args = parser.parse_args()

    planet_process = None
    gui_process = None
    try:
        # Start the Planet Server
        planet_process = start_planet_server(args.years, args.delay, args.scenario)
        print("Waiting for the planet server to initialize...")
        time.sleep(DEFAULT_SLEEP_TIME)
        
        # Start the GUI
        gui_process = start_gui()
        
        # Wait for the GUI readiness signal (temporary file)
        print("Waiting for the GUI to be ready...")
        wait_for_file(TEMP_FILE_PATH, GUI_WAIT_TIMEOUT)
        
        # In demonstration mode, the colony client is not started.
        
        # Wait for the GUI process to finish
        gui_process.wait()
        print("Shutdown complete.")
    finally:
        if os.path.exists(TEMP_FILE_PATH):
            os.remove(TEMP_FILE_PATH)
        shutdown_process(gui_process, "Planet GUI")
        shutdown_process(planet_process, "Planet server")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\nInterrupted! Exiting...")
        sys.exit(1)
