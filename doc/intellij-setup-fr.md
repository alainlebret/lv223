<!--
LV-223 (Colonization) multi-agent simulation

Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)

SPDX-License-Identifier: MIT
-->

# Configuration d'IntelliJ IDEA

Ce document fournit des instructions détaillées pour configurer IntelliJ IDEA afin de travailler efficacement sur le projet de simulation planétaire LV-223. Il aborde notamment :

- L'ajout de la dépendance *JFuzzyLogic* (version 2.1 recommandée) dans un projet Maven.
- La gestion des projets multi-modules (modules `planet` et `colony`).
- La création de configurations d'exécution adaptées.
- Des conseils supplémentaires en cas de problème.

## 1. Configuration de JFuzzyLogic dans IntelliJ IDEA

### Méthode 1 : Ajouter JFuzzyLogic à l'aide de Maven (recommandé)

1. Vérifier que le projet est bien détecté comme un projet Maven :
   - Ouvrez IntelliJ IDEA.
   - Sélectionnez File > Open et choisissez le fichier "pom.xml" à la racine du projet.
   - Assurez-vous que le panneau Maven est visible (le bouton "Reimport" peut être utilisé pour actualiser les dépendances).
2. Ajouter la dépendance JFuzzyLogic :
   Dans le fichier "pom.xml" du module concerné (par exemple, dans le module `planet`), ajoutez le bloc suivant à l'intérieur de la balise <dependencies> :

   ```xml
   <dependency>
      <groupId>net.sourceforge.jfuzzylogic</groupId>
      <artifactId>jfuzzylogic</artifactId>
      <version>2.1</version>
   </dependency>
   ```

3. Forcer le téléchargement des dépendances :
   Ouvrez le terminal intégré d'IntelliJ (ou utilisez le panneau Maven) et lancez la commande :

   ```sh
   mvn clean install
   ```

   Cela permettra à IntelliJ de télécharger et d'intégrer automatiquement la bibliothèque
   JFuzzyLogic dans votre projet.

### Méthode 2 : Ajouter JFuzzyLogic manuellement (en cas d'échec Maven)

1. Ajouter le JAR à IntelliJ :
   - Allez dans "File > Project Structure > Modules > Dependencies".
   - Cliquez sur le bouton "+" puis sélectionnez JARs or directories.
   - Sélectionnez le fichier "lib/jFuzzyLogic.jar" et validez.
   - Vérifiez que le JAR est bien ajouté au classpath de votre module.
2. Configurer le classpath dans les configurations d'exécution (si nécessaire) :
   - Allez dans "Run > Edit Configurations..."
   - Dans la configuration d'exécution (voir ci-dessous), assurez-vous que le classpath inclut le dossier "target/classes" ainsi que "lib/*".
3. Gestion d'un projet multi-modules dans IntelliJ IDEA
   Le projet LV-223 est organisé en plusieurs modules dont : `planet` et `colony`.

   Vérification et importation des modules
   1. Ouvrir le projet multi-modules :
      - Sélectionnez "File > Open" et choisissez le fichier "pom.xml" principal à la racine du projet.
      - IntelliJ IDEA devrait détecter automatiquement les sous-modules.
      - Si un module (par exemple, `planet` ou `colony`) n'apparaît pas dans "File > Project Structure > Modules", cliquez sur le bouton "+" et choisissez "Import Module" pour l'ajouter manuellement.
   2. Vérifier la structure du POM parent :
      Assurez-vous que le fichier pom.xml à la racine contient bien :

      ```xml
      <modules>
        <module>planet</module>
        <module>colony</module>
      </modules>
      ```

   3. Utiliser le bouton "Reimport" :
      Dans le panneau Maven, cliquez sur "Reimport" pour synchroniser les dépendances de tous les modules.

## 2. Création de configurations d’exécution dans IntelliJ IDEA

Pour exécuter séparément le serveur `planet` et le client `colony`, vous devez créer des configurations d'application.

### 2.1 Configuration pour le serveur `planet`

1. Créer une nouvelle configuration d'application :
   - Allez dans "Run > Edit Configurations..."
   - Cliquez sur le bouton "+" et sélectionnez "Application".
2. Configurer la nouvelle configuration :
   - Name : Planet server
   - Main class : fr.ensicaen.lv223.planet.Main
   - Module : Sélectionnez le module planet.
   - VM options :
     Vous pouvez utiliser :

     ```sh
     -cp target/classes:lib/*
     ```

     (ou utilisez ";" comme séparateur sur Windows)
   - Program arguments (exemple) :

     ```sh
     --years=1 --delay=1000 --scenario=move --port=12345
     ```

### 2.2 Configuration pour le client `colony`

1. Créer une nouvelle configuration d'application :
   - Dans "Run > Edit Configurations...", ajoutez une configuration de type "Application".
2. Configurer la nouvelle configuration :
   - Name : Colony client
   - Main class : fr.ensicaen.lv223.colony.Main
   - Module : Sélectionnez le module colony.
   - VM options :
     Par exemple :

     ```sh
     -cp target/classes:lib/*
     ```

     (avec ";" comme séparateur sur Windows)
   - Program arguments : Laissez vide ou spécifiez des arguments si nécessaire.

### 3.3 Conseils supplémentaires

- Re-importation des dépendances :
  Utilisez le bouton "Reimport" dans le panneau Maven si vous modifiez "pom.xml".
- Capture d'écran et documentation :
  Pensez à consulter la documentation d'IntelliJ IDEA (accessible avec "Help > Help Topics") pour plus de détails sur la gestion des projets Maven et des configurations d'exécution.
- Alternative avec les scripts Python :
  Si vous rencontrez des difficultés avec IntelliJ IDEA, vous pouvez toujours utiliser les scripts Python fournis (`compile.py`, `run_alone.py`, `run_with_colonie.py`) pour compiler et exécuter le projet.
