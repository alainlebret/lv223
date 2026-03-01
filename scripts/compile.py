#!/usr/bin/env python3
# LV-223 (Colonization) multi-agent simulation
#
# Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
#
# SPDX-License-Identifier: MIT

import os
import subprocess
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(SCRIPT_DIR, ".."))
MAVEN_REPO_OVERRIDE = os.environ.get("LV223_M2_REPO")

def run_command(command, cwd=None):
    """Execute a shell command and handle errors."""
    try:
        subprocess.run(command, shell=True, check=True, cwd=cwd)
    except subprocess.CalledProcessError as e:
        print(f"Error: {e}")
        sys.exit(1)

def mvn(command, cwd):
    """Run Maven, optionally with an explicit local repository."""
    repo_option = ""
    if MAVEN_REPO_OVERRIDE:
        os.makedirs(MAVEN_REPO_OVERRIDE, exist_ok=True)
        repo_option = f'-Dmaven.repo.local="{MAVEN_REPO_OVERRIDE}" '
    run_command(f"mvn {repo_option}{command}", cwd=cwd)

def artifact_exists(group_id, artifact_id, version):
    """Return True if artifact jar is already present in the selected Maven repo."""
    repo_root = MAVEN_REPO_OVERRIDE or os.path.expanduser("~/.m2/repository")
    rel_path = os.path.join(
        group_id.replace(".", "/"),
        artifact_id,
        version,
        f"{artifact_id}-{version}.jar",
    )
    return os.path.isfile(os.path.join(repo_root, rel_path))

def compile_planet():
    print("Compiling the planet server...")
    planet_dir = os.path.join(PROJECT_ROOT, "planet")

    mvn("clean", cwd=planet_dir)
    if not artifact_exists("net.sf.jfuzzylogic", "jfuzzylogic", "2.1"):
        mvn(
            "install:install-file -Dfile=lib/jFuzzyLogic.jar "
            "-DgroupId=net.sf.jfuzzylogic -DartifactId=jfuzzylogic "
            "-Dversion=2.1 -Dpackaging=jar",
            cwd=planet_dir,
        )
    if not artifact_exists("com.beust", "jcommander", "1.82"):
        mvn(
            "install:install-file -Dfile=lib/jcommander-1.82.jar "
            "-DgroupId=com.beust -DartifactId=jcommander "
            "-Dversion=1.82 -Dpackaging=jar",
            cwd=planet_dir,
        )
    mvn("-DskipTests package", cwd=planet_dir)

def compile_colony():
    print("Compiling the colony client...")
    colony_dir = os.path.join(PROJECT_ROOT, "colony")

    mvn("clean -DskipTests package", cwd=colony_dir)

if __name__ == "__main__":
    if len(sys.argv) == 1:
        print("Compiling both planet server and colony client.")
        compile_planet()
        compile_colony()
    elif sys.argv[1] == "planet":
        compile_planet()
    elif sys.argv[1] == "colony":
        compile_colony()
    else:
        print("Invalid argument. Use 'planet', 'colony', or no argument to compile both.")
        sys.exit(1)
