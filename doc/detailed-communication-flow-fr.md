<!--
LV-223 (Colonization) multi-agent simulation

Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)

SPDX-License-Identifier: MIT
-->

# Flux de communication dans le module `colony`

Ce document détaille le processus complet par lequel un robot de la colonie
envoie une requête de balayage ("scan") ou de déplacement ("move") au serveur,
depuis la construction et l'envoi de la requête jusqu'au traitement de la
réponse et sa transmission vers le robot.
L'objectif est d'identifier les classes et méthodes impliquées côté colonie,
ainsi que le rôle de la façade de communication.

**Remarque :** Le traitement côté planète (par exemple, la classe `RequestHandler`
et ses classes dérivées) n'est pas couvert ici. Ce document se concentre uniquement
sur le côté client/colonie.

---

## 1. Envoi d'une requête de balayage ("scan")

### 1.1 Vue d'ensemble du flux

1. **Action du robot**  
   Le robot déclenche un balayage en appelant sa méthode `scan()` (définie dans la classe abstraite `Robot`).
2. **Délégation à la façade**  
   La méthode `scan()` appelle `environmentFacade.scanEnvironment(this)`, en transmettant l'instance du robot.
3. **Construction de la requête**  
   Dans la méthode `scanEnvironment()` de `RobotEnvironmentFacade` :
   - La position locale du robot est d'abord convertie en coordonnées globales à l'aide de la méthode `translateToGlobal()`.
   - La requête est alors construite grâce à la méthode `createScanRequest(robotName, robotType, currentGlobalLocation)`.
   - La requête est envoyée au serveur par l'intermédiaire de la methode `PlanetServerConnection.sendRequest(request)`.
4. **Traitement de la réponse**  
   Lorsque le serveur retourne une réponse, la méthode `processScanResponse(response)` est appelée :
   - La réponse JSON est traitée à l'aide de `parseScanResponse(response)`.
   - Les résultats du balayage sont alors extraits avec la méthode auxiliaire `extractScanResults(rootNode)`.
   - Puis un objet `EnvironmentFeedback` (de type `SCAN_RESULT`) est créé.
   - La méthode `notifyObservers(feedback)` est ensuite appelée pour diffuser cette information à tous les observateurs (robots).
5. **Mise à jour du robot**  
   Le robot, en tant qu'`EnvironmentObserver`, voit sa méthode `update(EnvironmentFeedback feedback)` appelée. Si le retour est de type `SCAN_RESULT`, le robot met à jour sa carte locale en appelant `updateLocalMap(scanResults)`.

### 1.2 Diagramme de séquence pour le balayage

La figure suivante présente le diagramme de séquence détaillé d'un balayage ("scan") :

![Diagramme de séquence détaillé pour un scan.](figures/detailed-scan-sequence-diagram.svg)

---

## 2. Envoi d'une requête de déplacement ("move")

### 2.1 Vue d'ensemble du flux

1. **Action du robot**
   Lorsqu'un robot (par exemple, un `Cartographer`) souhaite se déplacer, il appelle la méthode `navigate(Direction direction)`.
2. **Délégation à la façade**
   La méthode `navigate()` invoque `environmentFacade.moveRobot(this, direction)`. Après l'envoi de la requête, la méthode `navigate()` met à jour la position du robot par un appel à `updateLocation(direction)` et déclenche un balayage à l'aide de `scan()` afin d'actualiser sa carte locale.
3. **Construction de la requête**
   Dans `RobotEnvironmentFacade.moveRobot()` :
   - Les nouvelles coordonnées globales sont calculées à partir de la position actuelle et de la direction gràce à la méthode `calculateNewGlobalCoordinates()` qui appelle `translateToGlobalDirection()`.
   - La requête de déplacement est alors construite en appelant la méthode auxiliaire `createMoveRequest(robotName, robotType, currentCoord, newCoord)`.
   - La requête est alors envoyée au serveur en appelant `PlanetServerConnection.sendRequest(request)`.
4. **Traitement de la réponse**
   À réception de la réponse, la méthode `handleMoveResponse(robot, response)` est appelée pour traiter le résultat. Dans le squelette proposé côté colonie, la mise à jour réelle de l'état du robot est encore à implémenter, mais elle consistera typiquement à mettre à jour la carte locale et l'état interne du robot en fonction de la réponse.
5. **Mise à jour post-déplacement**
   Enfin, la méthode `updateLocation(Direction)` est appelée pour mettre à jour les coordonnées internes du robot, suivie d'un appel à `scan()` afin de rafraîchir la perception qu'a le robot de l'environnement.

### 2.2 Diagramme de séquence pour le déplacement

La figure suivante présente le diagramme de séquence détaillé d'un déplacement ("move") :

![Diagramme de séquence détaillé pour un déplacement.](figures/detailed-move-sequence-diagram.svg)

## 3. Récapitulatif des classes et méthodes impactées

### Pour une requête de balayage ("scan")

- **`Robot`**
  - `scan()` : lance le balayage en appelant `environmentFacade.scanEnvironment(this)`.
  - `update(EnvironmentFeedback feedback)` : lorsqu'un *feedback* est reçu (type `SCAN_RESULT`), cette méthode met à jour la carte locale à l'aide de `updateLocalMap(scanResults)`.
- **`RobotEnvironmentFacade`**
  - `scanEnvironment(Robot robot)`: méthode appelée par le robot pour initier un balayage.
  - `createScanRequest(String robotName, String robotType, Coordinate location)` : construit la requête de balayage.
  - `processScanResponse(String response)` : traite la réponse JSON et la convertit en objet `EnvironmentFeedback`.
  - `parseScanResponse(String response)` : analyse la réponse JSON (utilisation de Jackson).
  - `extractScanResults(JsonNode rootNode)` : méthode auxiliaire pour extraire les résultats du balayage.
  - `notifyObservers(EnvironmentFeedback feedback)` : notifie tous les observateurs (robots) du retour.
- **`PlanetServerConnection`**
  - `sendRequest(String request)` : envoie la requête au serveur et retourne la réponse.
  - `receiveResponse()` : lit et retourne la réponse du serveur.
- **`EnvironmentFeedback`**  est utilisée pour encapsuler les résultats d'un balayage (type `SCAN_RESULT`).

### Pour une requête de déplacement ("move")

- **`Robot`**
  - `navigate(Direction direction)` : initie le déplacement en appelant `environmentFacade.moveRobot(this, direction)`, met à jour la position avec `updateLocation(direction)` et déclenche un balayage.
  - `updateLocation(Direction direction)` : met à jour les coordonnées internes du robot selon la direction.
- **`RobotEnvironmentFacade`**
  - `moveRobot(Robot robot, Direction direction)` : lance la requête de déplacement.
  - `createMoveRequest(String robotName, String robotType, Coordinate currentCoord, Coordinate newCoord)` : construit la requête pour le déplacement.
  - `handleMoveResponse(Robot robot, String response)` : traite la réponse du serveur et met à jour l'état du robot (à implémenter).
- **`PlanetServerConnection`**
  - `sendRequest(String request)` : envoie la requête de déplacement au serveur et retourne la réponse.
- **Mécanismes communs**
  - La méthode `update(EnvironmentFeedback feedback)` de `Robot` traite le retour (souvent avec un balayage déclenché après un déplacement).
  - La méthode `notifyObservers()` de `RobotEnvironmentFacade` propage le *feedback* aux robots abonnés.

## 4. Conclusion

Ce document présente en détail le flux de communication pour les requêtes de scan et de déplacement dans le module `colony`. Grâce aux diagrammes de séquence détaillés et au récapitulatif des classes et méthodes impliquées, vous disposez d'une vue complète sur :

- L'initiation d’un balayage ou d'un déplacement par un robot.
- La construction et l'envoi de la requête JSON à l'aide de la façade (`RobotEnvironmentFacade`) et la connexion (`PlanetServerConnection`).
- Le traitement de la réponse du serveur et sa propagation aux robots au travers du patron observateur.
- Les interactions spécifiques entre les différentes classes côté colonie.
