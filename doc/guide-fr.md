<!--
LV-223 (Colonization) multi-agent simulation

Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)

SPDX-License-Identifier: MIT
-->

# Architecture du projet LV-223

Ce document décrit l'organisation et l'utilisation du projet LV-223, en mettant
particulièrement l'accent sur la partie serveur qui simule une planète dynamique.
L'objectif principal est d'offrir une base solide pour développer ses propres
stratégies d'intelligence artificielle dans le cadre d'une simulation multi-agents.

---

Plan

1. [Introduction](#introduction)
2. [Architecture globale](#architecture-globale)
3. [Protocole de communication](#protocole-de-communication)
4. [Principaux patrons et choix techniques](#principaux-patrons-et-choix-techniques)
5. [Instructions](#instructions)
6. [Références](#references)

---

## <a name="introduction"></a> 1. Introduction

Le projet LV-223 a pour vocation de simuler l'installation d'une colonie de robots sur
une planète évolutive. Cette planète "vivante" subit des changements de saisons et des
métamorphoses induites par l'exploitation de ses ressources. L'enjeu principal est de
développer des stratégies d'intelligence artificielle afin de permettre aux robots de
s'adapter en temps réel à un environnement incertain et en constante évolution.

Objectifs pédagogiques :

- Concevoir et implémenter des stratégies d'apprentissage par renforcement, de recherche
  de chemin et de collaboration entre agents.
- Optimiser la navigation et l'exploitation de ressources dans un environnement dynamique.
- Expérimenter la gestion de comportements adaptatifs à partir d'informations partielles
  issues de capteurs virtuels.
- Se concentrer sur le développement de comportements IA tout en utilisant une
  infrastructure de communication préexistante.

---

## <a name="architecture-globale"></a> 2. Architecture globale

### 2.1 Organisation générale du projet

Le projet LV-223 est conçu de manière modulaire pour favoriser l'extensibilité et la
maintenabilité, tout en mettant l'accent sur le développement de stratégies IA. Le
système se compose de trois ensembles principaux :

- Module `planet` :
  Ce module simule la planète. Il gère une grille représentant divers types de terrain,
  des ressources et l'évolution de l'environnement comme les changements de saisons,
  les métamorphoses ou encore l'état de santé. Le serveur, implémenté par la classe
  `PlanetServer`, expose ces données par l'intermédiaire d'une interface réseau et
  traite les requêtes émises par les clients.
  Note : ce module est fourni dans sa quasi-totalité ; les étudiants se concentreront
  principalement sur le développement côté colonie.
- Module `colony` :
  Ce module contient la logique côté client pour la gestion de la colonie de robots. Il
  offre une façade de communication (classe `RobotEnvironmentFacade`) qui permet de
  construire, envoyer et traiter des requêtes vers le serveur. Les différents types de
  robots (ex. `Cartographer`, `Miner`, `Harvester`, `Farmer`, `Pipeliner`, et `Centralizer`)
  y sont implémentés en étendant la classe abstraite `Robot`.
  Au départ, les robots ne disposent que de méthodes vides ou d'exemples minimalistes.
  Les étudiants sont invités à y implémenter des stratégies IA avancées (apprentissage par
  renforcement, recherche de chemin, collaboration).
- Interface graphique :
  Développée en Python avec *Tkinter*, l'interface graphique offre une visualisation en
  temps réel de l'état de la planète et de la colonie. Elle affiche la grille, les ressources,
  l'état de santé et le nombre de tours écoulés, grâce à des mises à jour régulières basées
  sur les messages reçus du serveur.

### 2.2 Diagrammes et description des composants

#### Diagramme de composants

Le diagramme de composants ci-dessous illustre l’architecture globale du projet et met en
évidence les interactions entre :

- **La planète et son serveur** :
  - La classe `Planet` modélise la grille de terrains, les saisons, l'état de
    santé et les métamorphoses.
  - La classe `PlanetServer` reçoit et traite les requêtes des clients (colonie
    et IHM) et orchestre la simulation.
  - Des sous-modules dédiés gèrent respectivement les saisons (`SeasonHandler`),
    l'état de santé (`HealthStateHandler`) et les métamorphoses (`MetamorphosisHandler`).
- **La colonie** :
  - La classe `ColonyManager` gère la connexion au serveur et l'ordonnancement des robots.
  - La classe `RobotEnvironmentFacade` constitue l'interface unique entre la colonie et
    le serveur, isolant ainsi la logique IA des détails de communication.
  - Les différentes classes de robots implémentent des stratégies spécifiques et
    communiquent au travers de cette façade.
- **L'interface graphique** :
  Se connecte au serveur pour recevoir des mises à jour régulières, permettant ainsi
  la visualisation en temps réel de l'évolution de la simulation.

![Diagramme de composants](figures/lv223-component-diagram.svg)

#### Diagramme de déploiement

Le diagramme de déploiement représente la répartition des différents composants sur
les machines ou processus :

- Le serveur peut s'exécuter sur une machine dédiée et écouter sur un port (par
  défaut 12345).
- Le client colonie et l'interface graphique se connectent au serveur sur le réseau
  en utilisant des *sockets*, échangeant des messages JSON pour synchroniser l'état
  de la simulation.

![Diagramme de déploiement](figures/lv223-deployment-diagram.svg)

### 2.3 Flux de données

Les échanges de données s'effectuent en mettant en oeuvre un protocole standardisé
qui utilise le format JSON. Les principaux flux sont :

- **Requêtes émises par la colonie** :
  Les robots, par le biais de la façade (`RobotEnvironmentFacade`), envoient des
  requêtes telles que `"move"`, `"scan"`, `"mine"`, etc. Ces requêtes, construites
  à partir des actions des robots, sont transmises au serveur par l'intermédiaire de
  la classe `PlanetServerConnection`.
- **Réponses du serveur** :
  Le serveur traite chaque requête (en utilisant des gestionnaires spécifiques comme
  `MoveRequestHandler`, `ScanRequestHandler`, etc.) et renvoie une réponse JSON
  qui indique le résultat de l'opération, l'état mis à jour de la simulation
  (grille, ressources, robots affectés), et le cas échéant, un message d'erreur.
- **Mise à jour de l'interface graphique** :
  L'interface graphique se connecte également au serveur pour recevoir des mises à
  jour globales de la simulation en temps réel.

Une description détaillée des flux de communication côté colonie est fournie dans
le document [detailed-communication-flow-fr.md](detailed-communication-flow-fr.md)
en ce qui concerne les mécanismes du balayage ("scan") et du déplacement ("move").

### 2.4 Cycle de vie du serveur

Le cycle de vie du serveur, implémenté dans le module `planet`, synchronise la
simulation de la planète avec les clients. Il se déroule en trois phases :

1. **Démarrage** :
   - La classe `Main` instancie un objet `Planet` à partir d'un fichier de configuration
     JSON qui définit la grille, les types de terrain et les paramètres environnementaux.
   - Un objet `PlanetServer` est créé avec l'objet `Planet` et la configuration (`Config`).
     Le serveur ouvre alors une *socket* d'écoute (par défaut sur le port 12345) et
     initialise l'ensemble des gestionnaires de requêtes (implémentés par les classes
     dérivées de `RequestHandler`).
   - En mode démonstration, un gestionnaire de scénarios (`RobotScenarioManager`) peut
     être activé pour simuler des comportements préprogrammés.
2. **Boucle de simulation** :
   - Un *thread* dédié exécute la boucle de simulation où chaque tour représente un jour simulé.
   - À chaque tour, le serveur appelle la méthode `nextTurn()` de l'objet `Planet`, qui gère :
     - Le changement de saison à l'aide du gestionnaire `SeasonHandler`.
     - L'application des métamorphoses par l'intermédiaire du gestionnaire `MetamorphosisHandler`.
     - La mise à jour de l'état de santé par le gestionnaire `HealthStateHandler`.
     - La réactualisation des ressources (par exemple, le renouvellement de l'eau).
   - Après ces mises à jour, le serveur convertit l'état de la planète en message JSON et le
     diffuse aux clients à l'aide de la méthode `sendUpdatesToClients()`.
   - La synchronisation est assurée par l'attente d’un message `"endOfTurn"` (ou d'un
     accusé de réception de l'IHM en mode démo) avant de passer au tour suivant.
3. **Fin de simulation** :
   Une fois le nombre total de tours atteint (défini par le nombre d'années simulées multiplié
   par le nombre de jours par an), le serveur envoie une dernière mise à jour indiquant la fin
   de la simulation, arrête la boucle et ferme toutes les connexions réseau.

#### Diagramme de séquence simplifié pour une requête de déplacement ("move")

Le diagramme de séquence ci-dessous illustre, de manière simplifiée, l'interaction lors
d'une requête de déplacement :

![Diagramme de séquence pour une requête `"move"`.](figures/move-sequence-diagram-simplified.svg)

Consulter le document [detailed-communication-flow-fr.md](detailed-communication-flow-fr.md)
pour plus d'informations.

#### Diagramme de classes simplifié (module `planet)

Ce diagramme de classes offre une vue simplifiée des principales classes du module `planet` :

![Diagramme de classes du module `planet`.](figures/planet-class-diagram.svg)

---

## <a name="protocole-de-communication"></a> 3. Protocole de communication

La communication entre la colonie (et l'interface graphique) et la planète s'effectue
en utilisant des messages au format JSON transmis à l'aide de *sockets*. Cette séparation
garantit que la logique métier (stratégies des robots et simulation environnementale)
 est isolée de la couche de communication.

### Format des messages

Chaque message, qu'il s'agisse d'une requête ou d'une réponse, respecte une structure
JSON standardisée permettant de décrire :

- `"action`" : l'opération demandée (ex. `"move"`, `"scan"`, `"mine"`, `"harvest"`, `"cultivate"`, `"pipe"`, `"pump"`, `"endOfTurn"`) ;
- `"robotId"` : l'identifiant unique du robot effectuant l'action ;
- `"robotType"` : le type du robot (ex. `"Cartographer"`, `"Miner"`, `"Harvester"`, `"Farmer"`, `"Pipeliner"`) ;
- `"parameters"` : un objet regroupant les paramètres spécifiques à l'action (coordonnées, unités, etc.).

#### Exemple de requête de déplacement ("move")

```json
{
  "action": "move",
  "robotId": "R1",
  "robotType": "Cartographer",
  "parameters": {
    "x": 10,
    "y": 10,
    "newX": 10,
    "newY": 11
  }
}
```

#### Exemple de requête de balayage ("scan")

```json
{
  "action": "scan",
  "robotId": "R2",
  "robotType": "Cartographer",
  "parameters": {
    "x": 5,
    "y": 5
  }
}
```

#### Exemple de requête "endOfTurn"

```json
{
  "action": "endOfTurn"
}
```

#### Structure générale d'une réponse

Les réponses du serveur comportent les champs suivants :

- `"status"` : `"success"` ou `"error"`.
- `"action"` : l'action concernée (ex. `"move"`, `"scan"`). En cas d'erreur, ce champ peut être vide.
- `"message"` : une description du résultat (surtout en cas d'erreur).
- `"affectedRobots"` : une liste d'objets décrivant les robots affectés par l'action (id, type, niveau d'impact).
- `"detectedCells"` : (pour `"scan"`) une liste d'objets décrivant l'état des cellules détectées (coordonnées, type, unités).

Exemple de réponse pour un déplacement ("move")

```json
{
  "status": "success",
  "action": "move",
  "message": "Move completed successfully.",
  "affectedRobots": [
    {
      "id": "R1",
      "type": "Cartographer",
      "injury": 0
    }
  ],
  "detectedCells": []
}
```

Exemple de réponse pour un balayage ("scan")

```json
{
  "status": "success",
  "action": "scan",
  "message": "",
  "affectedRobots": [
    {
      "id": "R2",
      "type": "Cartographer",
      "injury": 0
    }
  ],
  "detectedCells": [
    { "x": 4, "y": 4, "type": "forest", "units": 100 },
    { "x": 4, "y": 5, "type": "prairie", "units": 200 }
    // ... les autres cellules du voisinage
  ]
}
```

### Rôle et fonctionnement de la façade de communication

La classe `RobotEnvironmentFacade` (module `colony`) est le coeur du protocole côté
client. Elle se charge de :

- **Construire les requêtes**
  Convertit les actions des robots en messages JSON, par exemple en appelant `moveRobot()`
  qui construit une requête avec les coordonnées actuelles et de destination.
- **Envoyer les messages**
  Utilise `PlanetServerConnection` pour transmettre les requêtes au travers de la *socket*.
  La méthode `sendRequest()` assure la validité de la connexion.
- **Traiter les réponses**
  Convertit les réponses JSON en objets Java (ex. `ActionResponse`) et notifie les robots
  des résultats, permettant ainsi de gérer les erreurs et d'adapter les stratégies.
- **Notifier les observateurs**
  Grâce au patron observateur, en cas de changement (par exemple, après un `"scan"`), tous
  les robots inscrits sont informés pour actualiser leur comportement.

### Gestion des erreurs et des cas limites

Pour garantir la robustesse du système, le protocole intègre les fonctionnalités suivantes :

- Validation des entrées
  Chaque requête est vérifiée (coordonnées, unités, etc.) afin de limiter les erreurs lors
  du traitement.
- Messages d'erreur
  En cas de problème, le serveur renvoie une réponse `"status": "error"` et un message
  explicatif (ex. `"Invalid cell coordinates"`).
- Gestion des exceptions
  La façade et `PlanetServerConnection` capturent les exceptions afin de faciliter le débogage.
- Dépassement de délai et synchronisation
  Des mécanismes de synchronisation tentent de garantir que le serveur attend les réponses des
  clients avant de passer au tour suivant. En cas de délai excessif, un avertissement est
  signalé et le serveur passe au tour suivant.

---

## <a name="principaux-patrons-et-choix-techniques"></a> 4. Principaux patrons et choix techniques

Le projet adopte une architecture modulaire et repose sur plusieurs patrons de conception
afin de garantir flexibilité, extensibilité et robustesse. Voici les principaux :

### 4.1 Patrons de conception

#### 4.1.1 Observateur

Utilisé pour informer les robots des changements dans l'environnement (après un `"scan"` par exemple).

**Implémentation** :

- La classe `RobotEnvironmentFacade` agit en tant que sujet.
- Les robots s'inscrivent à l'aide de la méthode `subscribe()`.
- Lorsqu'une réponse est reçue, un objet `EnvironmentFeedback` est créé et
  diffusé à tous les observateurs.

#### 4.1.2 Façade

Isoler la logique de communication réseau de la logique métier des robots.

**Implémentation** :

- La classe `RobotEnvironmentFacade` est l'interface unique entre la colonie et le serveur.
- Elle construit les messages JSON, envoie les requêtes par l'intermédiaire de `PlanetServerConnection` et traite les réponses.

#### 4.1.3 Stratégie

Permet de gérer des comportements dynamiques en fonction du contexte (notamment
pour la métamorphose de la planète).

**Implémentation** :

- Dans le module `planet`, `MetamorphosisHandler` utilise l'interface `MetamorphosisStrategy`.
- `StandardMetamorphosisStrategy` en est une implémentation concrète qui ajuste les terrains
  en fonction des saisons et de l'intensité des extractions.

### 4.2 Choix techniques

- Langages et technologies :
  - Java (JDK 8+ ou 11+) pour les modules `planet` et `colony`.
  - Python (Tkinter) pour l'interface graphique.
  - Maven pour la gestion du projet.
- Communication réseau :
  - Utilisation de *sockets* et du format JSON pour l'échange de messages, assurant
    ainsi une séparation nette entre simulation et communication.
- Gestion de la configuration :
  - Les paramètres de simulation (dimensions, ressources, etc.) sont chargés à partir
    d'un fichier JSON, ce qui facilite la modification sans altérer le code source.

---

## <a name="instructions"></a> 5. Instructions

Cette section fournit des indications pratiques pour installer, compiler, exécuter et étendre le projet.

### 5.1 Installation et compilation

### Prérequis

- Une version récente du JDK (8, 11 ou supérieure) installée.
- Maven (version 3 ou ultérieure) pour la gestion des dépendances.
- Pour l'interface graphique, Python 3 et Tkinter doivent être installés.

#### Compilation

Depuis la racine du projet (où se trouve le fichier "pom.xml" principal), utilisez le
script Python suivant pour compiler l'ensemble des modules :

```sh
python3 scripts/compile.py
```

### 5.2 Exécution du projet

Deux scripts Python sont disponibles dans le dossier scripts :

- `run_alone.py` : Lance le serveur (module `planet`) en mode autonome.
- `run_with_colonie.py` : Lance simultanément le serveur et le client colonie.

#### Lancement du serveur (module `planet`)

Pour démarrer le serveur en mode démonstration, exécutez :

```sh
python3 scripts/run_alone.py --years=1 --delay=1000 --scenario=move --port=12345 --config=target/classes/json/planet2.json
```

Les options disponibles sont :

- `--years=<N>` : nombre d'années à simuler (par défaut : 8).
- `--delay=<ms>` : délai entre deux tours (par défaut : 1000 ms).
- `--scenario=<type>` : mode de démonstration (ex. `"move"`, `"mine"`, `"scan"`, etc. ou `"none"` pour le mode normal).
- `--port=<port>` : port d'écoute du serveur (par défaut : 12345).
- `--config=<chemin>` : chemin vers le fichier de configuration JSON.

#### Lancement de la colonie

Pour exécuter le client colonie, déplacez-vous dans le dossier `colony` et lancez :

```sh
python3 ../scripts/run_with_colonie.py
```

Cela lance le serveur et démarre l'ordonnancement des robots.

#### Lancement de l'interface graphique

Pour lancer l'interface, exécutez depuis le dossier `planet-gui` :

```sh
python3 src/planet_gui.py
```

Remarque : Les paramètres de connexion (hôte et port) sont codés dans le script et
peuvent être modifiés si nécessaire.

### 5.3 Arborescence du projet

Voici une vue d'ensemble de l'organisation du projet :

```default
LV-223/
 ├─ pom.xml                   # POM principal (liste des modules)
 ├─ planet/                   # Module serveur (simulation de la planète)
 │   └─ src/main/java/fr/ensicaen/lv223/planet/ ...
 ├─ colony/                   # Module client (colonie de robots)
 │   └─ src/main/java/fr/ensicaen/lv223/colony/ ...
 ├─ planet-gui/               # Interface graphique
 │   └─ src/planet_gui.py
 └─ scripts/                  # Scripts Python
     ├─ compile.py
     ├─ run_alone.py
     └─ run_with_colonie.py
```

Chaque module possède son propre fichier "pom.xml" pour permettre une compilation
et une gestion des dépendances indépendantes.

---

## <a name="references"></a> 6. Références

- P. Cingolani, J. Alcalá-Fdez. "jFuzzyLogic: a Java Library to Design Fuzzy Logic Controllers According to the Standard for Fuzzy Control Programming", International Journal of Computational Intelligence Systems, 61-75, 2013.
- [Documentation Maven](https://maven.apache.org/guides/index.html)
- [Documentation Tkinter](https://docs.python.org/fr/3.13/library/tkinter.html)
