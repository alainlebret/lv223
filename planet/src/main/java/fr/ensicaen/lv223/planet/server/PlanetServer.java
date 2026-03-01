/*
 * LV-223 (Colonization) multi-agent simulation
 *
 * Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
 *
 * SPDX-License-Identifier: MIT
 */

package fr.ensicaen.lv223.planet.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import fr.ensicaen.lv223.planet.Cell;
import fr.ensicaen.lv223.planet.Planet;
import fr.ensicaen.lv223.planet.pojo.ActionRequest;
import fr.ensicaen.lv223.planet.utils.RobotScenarioManager;
import fr.ensicaen.lv223.planet.utils.RobotScenarioType;
import fr.ensicaen.lv223.planet.utils.RobotInfo;
import fr.ensicaen.lv223.planet.utils.RobotType;
import fr.ensicaen.lv223.planet.utils.Config;

/**
 * Server for remote access and control of the planet.
 * <p>
 * This server allows clients to connect and send requests to query the planet state or
 * perform actions. It manages client connections and processes incoming requests using
 * encapsulated request handlers. It also supports both standard operation and demonstration scenarios.
 * </p>
 *
 * @version 1.4
 */
public class PlanetServer {
    private static final Logger logger = LogManager.getLogger(PlanetServer.class);
    static final long TURN_TIMEOUT_MS = 2000; 

    Random random = new Random();
    final Planet planet;
    private final ServerSocket serverSocket;
    private final List<Socket> connectedClients = Collections.synchronizedList(new ArrayList<>());
    private final ExecutorService clientHandlerExecutor = Executors.newCachedThreadPool();
    private Map<RequestType, RequestHandler> requestHandlers;
    private ClientType clientType;

    private final ReentrantLock simulationLock = new ReentrantLock();
    private final Condition robotsCompletedCondition = simulationLock.newCondition();
    private final Condition guiAcknowledgedCondition = simulationLock.newCondition();
    private AtomicBoolean running = new AtomicBoolean(true);
    private AtomicBoolean guiClientReady = new AtomicBoolean();
    private AtomicBoolean colonyClientReady = new AtomicBoolean();
    private boolean robotsCompletedTurn = false;
    private boolean guiAcknowledgedTurn = false;

    private Thread simulationThread;
    private final Map<String, RobotInfo> robots;
    /** The robot manager. Intended for future use */
    private final RobotManager robotManager;
    private volatile Socket guiSocket;
    private volatile Socket colonySocket;

    private final Config config;

    ///////////////////// FOR DEMO PURPOSES ONLY //////////////////////////////
    private RobotScenarioManager demoFactory;
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new PlanetServer associated with the given planet and configuration.
     * <p>
     * In demonstration mode, simulated robots are initialized.
     * </p>
     *
     * @param planet the associated planet
     * @param config the configuration for the server and simulation
     * @throws IOException if an I/O error occurs during server initialization
     */
    public PlanetServer(Planet planet, Config config) throws IOException {
        this.planet = planet;
        this.config = config;
        this.clientType = ClientType.GUI_CLIENT;
        // Create the server socket and enable address reuse (not working well on Windows)
        this.serverSocket = new ServerSocket(this.config.getPort());
        this.serverSocket.setReuseAddress(true);
        this.robotManager = new RobotManager();

        setupShutdownHook();
        initializeRequestHandlers();

        robots = new ConcurrentHashMap<>();
        ///////////////////// FOR DEMO PURPOSES ONLY //////////////////////////
        if (config.getScenario() != RobotScenarioType.NONE) {
            demoFactory = new RobotScenarioManager(this);
        }
        ///////////////////////////////////////////////////////////////////////
    }

    /**
     * Initializes request handlers for the planet server.
     * <p>
     * Request handlers are stored in an {@code EnumMap}, with the request
     * type as the key and the corresponding handler as the value.
     * </p>
     * <p>
     * Supported request types include:
     * <ul>
     * <li>{@code SCAN}</li>
     * <li>{@code MOVE}</li>
     * <li>{@code CULTIVATE}</li>
     * <li>{@code HARVEST}</li>
     * <li>{@code PIPE}</li>
     * <li>{@code PUMP}</li>
     * <li>{@code MINE}</li>
     * </ul>
     * </p>
     */
    private void initializeRequestHandlers() {
        requestHandlers = new EnumMap<>(RequestType.class);
        requestHandlers.put(RequestType.SCAN, new ScanRequestHandler());
        requestHandlers.put(RequestType.MOVE, new MoveRequestHandler());
        requestHandlers.put(RequestType.CULTIVATE, new CultivateRequestHandler());
        requestHandlers.put(RequestType.HARVEST, new HarvestRequestHandler());
        requestHandlers.put(RequestType.PIPE, new PipeRequestHandler());
        requestHandlers.put(RequestType.PUMP, new PumpRequestHandler());
        requestHandlers.put(RequestType.MINE, new MineRequestHandler());
    }

    /**
     * Sets up a shutdown hook for the server.
     * When a shutdown signal is received, the server will be stopped.
     */
    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received.");
            stopServer();
        }));
    }

    // ------------------------ SERVER CONTROL METHODS ------------------------

    /**
     * Starts the server and listens for client connections.
     * <p>
     * This method runs a simulation loop and accepts incoming client connections.
     * For each client connection, a new thread is created to handle the client.
     */
    public void startServer() {
        logger.info("Planet server starts, listening for connections...");
        runSimulationLoop();
        try {
            while (running.get()) {
                logger.debug("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                connectedClients.add(clientSocket);
                clientHandlerExecutor.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            logger.error("Error accepting client connection: {}", e.getMessage());
            running.set(false);
        }
    }

    /**
     * Stops the server by halting new client connections and closing all open sockets.
     */
    public void stopServer() {
        logger.info("Stopping server...");
        running.set(false);
    
        if (colonySocket != null && !colonySocket.isClosed()) {
            try {
                PrintWriter out = new PrintWriter(colonySocket.getOutputStream(), true);
                out.println("{\"status\":\"success\",\"action\":\"quit\",\"message\":\"Server shutting down.\"}");
                logger.info("Quit message sent to colony client.");
            } catch (IOException e) {
                logger.error("Error sending quit message to colony client: {}", e.getMessage());
            }
        }
    
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }
    
        clientHandlerExecutor.shutdownNow();
        try {
            if (!clientHandlerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while awaiting executor termination.", e);
        } finally {
            closeAllClientSockets();
            closeServerSocket();
        }
    }
     
    /**
     * Closes all client sockets that are currently connected to the server.
     * This method iterates over the list of connected clients and closes their
     * sockets.
     * If an error occurs while closing a socket, an error message is logged.
     * After closing all sockets, the list of connected clients is cleared.
     */
    private void closeAllClientSockets() {
        for (Socket socket : connectedClients) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Error closing client socket: {}", e.getMessage());
            }
        }
        connectedClients.clear();
    }

    /**
     * Closes the server socket if it is open.
     */
    private void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("Error closing server socket: {}", e.getMessage());
            }
        }
    }

    // ------------------------ CLIENT HANDLING METHODS -----------------------

    /**
     * Handles a client connection, determining its type and processing 
     * accordingly.
     *
     * @param clientSocket The socket representing the connected client.
     */
    private void handleClient(Socket clientSocket) {
        connectedClients.removeIf(socket -> clientSocket.isClosed());

        try {
            InputStream in = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String firstLine = reader.readLine();
            if (firstLine == null) {
                logger.warn("Client at {} disconnected before sending its type.",
                        clientSocket.getInetAddress().getHostAddress());
                clientSocket.close();
                return;
            }
            ClientType type;
            try {
                type = ClientType.valueOf(firstLine.trim());
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown client identifier '{}' from {}; closing socket.",
                        firstLine, clientSocket.getInetAddress().getHostAddress());
                clientSocket.close();
                return;
            }

            switch (type) {
                case GUI_CLIENT:
                    guiSocket = clientSocket;
                    logger.info("GUI client connected: {}", clientSocket.getInetAddress().getHostAddress());
                    guiClientReady.set(true);
                    synchronized (this) {
                        notifyAll();
                    }
                    handleGuiClient(clientSocket);
                    break;
                case COLONY_CLIENT:
                    colonySocket = clientSocket;
                    logger.info("Colony client connected: {}", clientSocket.getInetAddress().getHostAddress());
                    colonyClientReady.set(true);
                    synchronized (this) {
                        notifyAll();
                    }
                    handleColonyClient(clientSocket, reader);
                    break;
                default:
                    logger.warn("Unhandled client type {} from {}; closing socket.",
                            type, clientSocket.getInetAddress().getHostAddress());
                    clientSocket.close();
                    break;
            }
        } catch (IOException e) {
            logger.error("Error handling client: {}", e.getMessage());
        }
    }

    /**
     * Handles communication with a GUI client.
     *
     * @param clientSocket The socket representing the GUI client connection.
     */
    private void handleGuiClient(Socket clientSocket) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                while (running.get() && !clientSocket.isClosed()) {
                    String message = reader.readLine();
                    if (message != null && "ACK".equals(message.trim())) {
                        handleGuiClientAcknowledge();
                    }
                }
            } catch (IOException e) {
                logger.error("Error listening for GUI acknowledgments: {}", e.getMessage());
            }
        }).start();
    }

    /**
     * Notifies any waiting threads that the GUI has acknowledged to.
     */
    public void handleGuiClientAcknowledge() {
        simulationLock.lock();
        try {
            guiAcknowledgedTurn = true;
            guiAcknowledgedCondition.signalAll();
        } finally {
            simulationLock.unlock();
        }
    }

    /**
     * Notifies any waiting threads that the colony has completed its turn
     * (i.e. : the simulation loop).
     */
    public void handleColonyClientEndOfTurn() {
        simulationLock.lock();
        try {
            robotsCompletedTurn = true;
            robotsCompletedCondition.signalAll();
        } finally {
            simulationLock.unlock();
        }
    }

    /**
     * Handles communication with a colony client.
     * Reads requests from the client and sends responses back.
     *
     * @param clientSocket The socket connected to the colony client.
     * @param reader       The BufferedReader used to read requests from the client.
     */
    public void handleColonyClient(Socket clientSocket, BufferedReader reader) {
        try {
            String requestLine;
            while ((requestLine = reader.readLine()) != null) {
                logger.debug("Colony sent: {}", requestLine);
                String response = answerToRequest(requestLine);
                logger.debug("Server answered: {}", response);
                sendResponseToClient(clientSocket, response);
            }
        } catch (IOException e) {
            logger.error("Error communicating with colony client: {}", e.getMessage());
        }
    }

    // --------------------- CLIENT COMMUNICATION METHODS ---------------------

    /**
     * Sends the current planet state to the GUI client, then disables the
     * {@code hasSignificantChanges} flag. Called synchronously from the
     * simulation loop so that the state snapshot is consistent with the turn
     * that just completed and no concurrent writes can corrupt the socket stream.
     */
    private void sendUpdatesToClients() {
        String dataJson = convertPlanetDataToJson();
        try {
            if (guiSocket != null && !guiSocket.isClosed()) {
                logger.debug("Sending updates to GUI for turn: {}", planet.getCurrentTurn());
                sendPlanetData(guiSocket, dataJson);
            }
        } catch (IOException e) {
            logger.error("Error sending updates to GUI: {}", e.getMessage());
        }
        planet.disableHasSignificantChanges();
    }

    /**
     * Sends the planet data to the client socket.
     *
     * @param clientSocket The client socket to send the data to
     * @param data         The planet data to send
     * @throws IOException if an I/O error occurs while sending the data
     */
    private void sendPlanetData(Socket clientSocket, String data) throws IOException {
        OutputStream out = clientSocket.getOutputStream();
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4);

        buffer.putInt(dataBytes.length);
        out.write(buffer.array());
        out.write(dataBytes);
        out.flush();
    }

    // Methods to send a response back to the client

    /**
     * Sends a response to the client over the provided socket.
     *
     * @param clientSocket The socket connected to the client.
     * @param response     The response to send to the client.
     * @throws IOException If an I/O error occurs while sending the response.
     */
    private void sendResponseToClient(Socket clientSocket, String response) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(response); // Sends the response followed by a newline character
    }

    // ---------------------- REQUEST PROCESSING METHODS ----------------------

   /**
     * Processes a JSON request and returns the response.
     * <p>
     * Handles quit and end-of-turn commands before parsing other requests.
     * </p>
     * <p>A request may have the following format:</p>
     * <pre>
     * {
     *   "action": "move"|"scan"|"pump"|"mine"|"harvest"|"cultivate"|"pipe"|"endOfTurn",
     *   "robotId": the id/name of the robot,
     *   "robotType": "Cartographer"|"Miner"|"Harvester"|"Farmer"|"Pipeliner",
     *   "parameters": 
     *   {
     *     "x": the x-coordinate of the robot in the grid,
     *     "y": the y-coordinate of the robot in the grid,
     *     "newX": the future x-coordinate of the robot in the grid (optional),
     *     "newY": the future y-coordinate of the robot in the grid (optional),
     *     "units": the units to mine/pump/harvest on the current position (optional),     * 
     *   }
     * }
     * </pre>
     * <p>
     * Response to the colony client may be:
     * <pre>
     * {
     *   "status": "success"|"error",
     *   "action": "move"|"scan"|"pump"|"mine"|"harvest"|"cultivate"|"pipe"|"endOfTurn" or empty if error,
     *   "message": What you need to say...,
     *   "affectedRobots": [
     *     {
     *       "id": the id/name of the robot,
     *       "type": "Cartographer"|"Miner"|"Harvester"|"Farmer"|"Pipeliner",
     *       "injury": 0|1
     *     },
     *     ...
     *   ],
     *   "detectedCells": [
     *     {
     *       "x": the x-coordinate of the detected cell in the grid,
     *       "y": the y-coordinate of the detected cell in the grid,
     *       "type": "unknown"|"base"|"stone"|"forest"|"desert"|"water"
     *               |"mineral"|"dry_prairie"|"prairie"|"wet_prairie"
     *               |"impenetrable"|"fruits_and_vegetables"
     *     },
     *     ...
     *   ]
     * } 
     * </pre>
     *
     * @param jsonRequest the JSON request.
     * @return the response as a JSON string.
     * 
     */
    public String answerToRequest(String jsonRequest) {
        ObjectMapper mapper = new ObjectMapper();
        // Check for quit command
        if (jsonRequest.trim().equals("{\"action\": \"quit\"}")) {
            logger.info("Quit command received from client.");
            stopServer();
            return "{\"status\":\"success\",\"action\":\"quit\",\"message\":\"Server shutting down.\"}";
        }
        try {
            JsonNode root = mapper.readTree(jsonRequest);
            String action = root.path("action").asText("");
            if ("endOfTurn".equals(action)) {
                handleColonyClientEndOfTurn();
                return "{\"status\":\"success\",\"action\":\"endOfTurn\"}";
            }
            // Parse the request
            ActionRequest request = mapper.readValue(jsonRequest, ActionRequest.class);
            RequestType requestType;
            try {
                requestType = RequestType.valueOf(request.getAction().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Unknown request action: {}. Request: {}", request.getAction(), jsonRequest, e);
                return generateErrorResponse("Invalid request type");
            }
            
            RequestHandler handler = requestHandlers.get(requestType);
            if (handler == null) {
                logger.error("No handler found for request type: {}. Request: {}", requestType, jsonRequest);
                return generateErrorResponse("Invalid request type");
            }
            
            String response = handler.handleRequest(new RequestContext(request, this));
            logger.debug("Request processed successfully. Request: {} Response: {}", jsonRequest, response);
            return response;
        } catch (JsonProcessingException e) {
            logger.error("JSON processing error for request: {}. Error: {}", jsonRequest, e.getMessage(), e);
            return generateErrorResponse("Invalid JSON format");
        } catch (Exception e) {
            logger.error("Unexpected error processing request: {}. Error: {}", jsonRequest, e.getMessage(), e);
            return generateErrorResponse("Server error");
        }
    }

    // --------------------------- UTILITY METHODS ----------------------------

    /**
     * Checks if the robot type is valid.
     *
     * @param robotType the robot type to check
     * @return true if valid, false otherwise
     */
    boolean isValidRobotType(RobotType robotType) {
        return (robotType == RobotType.CARTOGRAPHER || robotType == RobotType.MINER ||
                robotType == RobotType.FARMER || robotType == RobotType.HARVESTER || robotType == RobotType.PIPELINER);
    }

    /**
     * Checks if the given coordinates represent an invalid cell in the grid.
     *
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @return true if the cell is invalid, false otherwise.
     */
    boolean isInvalidCell(int x, int y) {
        Cell[][] grid = planet.getGrid();
        return x < 0 || x >= grid[0].length || y < 0 || y >= grid.length;
    }

    // ----------------- SIMULATION LOOP AND RELATED METHODS ------------------

    /**
     * Runs the simulation loop in a separate thread.
     * <p>
     * Advances the planet turn-by-turn until the total simulation years are reached.
     * In each turn, updates are sent to clients and the server waits for client acknowledgments.
     * </p>
     */
    private void runSimulationLoop() {
        int totalNumberOfYears = config.getTotalNumberOfYears();
        int delayMilliseconds = config.getTurnDelayMilliseconds();
    
        simulationThread = new Thread(() -> {
            try {
                waitUntilClientsAreReady();
                while (running.get() && planet.getCurrentYear() < totalNumberOfYears) {
                    for (int day = 0; day < Planet.DAYS_PER_YEAR && running.get(); day++) {
                        logger.info("Year: {}, Day: {}", planet.getCurrentYear() + 1, day + 1);
                        sendUpdatesToClients();
    
                        // Wait for colony client turn completion with timeout
                        if (config.getScenario() == RobotScenarioType.NONE) {
                            simulationLock.lock();
                            try {
                                long startWait = System.currentTimeMillis();
                                while (!robotsCompletedTurn && running.get()) {
                                    long elapsed = System.currentTimeMillis() - startWait;
                                    long remaining = TURN_TIMEOUT_MS - elapsed;
                                    if (remaining <= 0) {
                                        logger.warn("Timeout waiting for colony turn completion.");
                                        break;
                                    }
                                    robotsCompletedCondition.await(remaining, TimeUnit.MILLISECONDS);
                                }
                            } finally {
                                simulationLock.unlock();
                            }
                        } else {
                            robotsCompletedTurn = true;
                        }
    
                        // In demo mode, wait for the GUI to acknowledge with timeout
                        if (config.getScenario() != RobotScenarioType.NONE) {
                            simulationLock.lock();
                            try {
                                long startWait = System.currentTimeMillis();
                                while (!guiAcknowledgedTurn && running.get()) {
                                    long elapsed = System.currentTimeMillis() - startWait;
                                    long remaining = TURN_TIMEOUT_MS - elapsed;
                                    if (remaining <= 0) {
                                        logger.warn("Timeout waiting for GUI acknowledgment.");
                                        break;
                                    }
                                    guiAcknowledgedCondition.await(remaining, TimeUnit.MILLISECONDS);
                                }
                            } finally {
                                simulationLock.unlock();
                            }
                        }
    
                        if (!running.get()) break;
    
                        logger.debug("End-of-turn received, proceeding to next turn");
                        planet.nextTurn();
    
                        // Reset flags
                        simulationLock.lock();
                        try {
                            guiAcknowledgedTurn = false;
                            robotsCompletedTurn = false;
                        } finally {
                            simulationLock.unlock();
                        }
    
                        TimeUnit.MILLISECONDS.sleep(delayMilliseconds);
                    }
                    // End-of-year update
                    sendUpdatesToClients();
                }
                stopServer();
                running.set(false);
            } catch (InterruptedException e) {
                logger.error("Simulation loop interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        });
        simulationThread.start();
    }

    private void waitForClientReadiness() throws InterruptedException {
        synchronized (this) {
            waitUntilClientsAreReady();
        }
    }

    private void waitUntilClientsAreReady() throws InterruptedException {
        synchronized (this) {
            // In demo mode, only the GUI client is required to be ready
            if (config.getScenario() == RobotScenarioType.NONE) {
                while (!colonyClientReady.get()) {
                    wait();
                }
            } else {
                while (!guiClientReady.get()) {
                    wait();
                }
            }
        }
    }
    
    // ------------------- JSON AND DATA CONVERSION METHODS -------------------

    /**
     * Converts the planet data to JSON format and returns it as a string.
     *
     * @return the planet data in JSON format as a string
     */
    private String convertPlanetDataToJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.put("version", "1.4");
        rootNode.put("timestamp", System.currentTimeMillis());

        rootNode.set("terrain", addGridStateToJson());
        rootNode.set("robots", addRobotPositionsToJson());
        rootNode.put("health", planet.getHealthStateHandler().getCurrentHealthDescription());
        rootNode.put("season", planet.getSeasonHandler().getCurrentSeason().toString().toLowerCase());
        rootNode.put("turns", planet.getCurrentTurn());

        boolean isSimulationEnded = checkEndOfSimulation();
        rootNode.put("end_simulation", isSimulationEnded);

        try {
            return mapper.writeValueAsString(rootNode);
        } catch (IOException e) {
            logger.error("Error converting planet data to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * Adds the grid state to a JSON object.
     *
     * @return JSON array node containing the grid state
     */
    private ArrayNode addGridStateToJson() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode terrainArray = mapper.createArrayNode();

        Cell[][] grid = planet.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                ObjectNode cellNode = terrainArray.addObject();
                cellNode.put("x", x);
                cellNode.put("y", y);
                cellNode.put("type", grid[y][x].getType().toString());
                cellNode.put("quantity", grid[y][x].getUnits());
                cellNode.put("visited", String.valueOf(grid[y][x].isVisited()));
                cellNode.put("modified", String.valueOf(grid[y][x].isModified()));
                cellNode.put("has_pipeline", String.valueOf(grid[y][x].hasAlienConstructionOnIt()));
            }
        }
        return terrainArray;
    }

    /**
     * Adds the positions of the robots to a JSON array.
     * If the server is in demo mode, the positions of the simulated robots are
     * added.
     * Otherwise, the positions of the robots on the planet are retrieved and added.
     */
    private ArrayNode addRobotPositionsToJson() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode robotsArray = mapper.createArrayNode();
        RobotScenarioType demoType = config.getScenario();

        if (demoType != RobotScenarioType.NONE) {
            this.demoFactory.runScenario(demoType, planet);
        }

        for (Map.Entry<String, RobotInfo> entry : robots.entrySet()) {
            RobotInfo robot = entry.getValue();
            ObjectNode robotNode = robotsArray.addObject();
            robotNode.put("id", robot.getId());
            String typeWithSpaces = camelCaseToSpaced(robot.getType().toFormattedString());
            robotNode.put("type", typeWithSpaces);
            ObjectNode positionNode = robotNode.putObject("position");
            positionNode.put("x", robot.getX());
            positionNode.put("y", robot.getY());
        }
        return robotsArray;
    }

    // ----------------------- MISCELLANEOUS METHODS --------------------------

    /**
     * Converts a camelCase string to a spaced string.
     * 
     * @param camelCase the camelCase string to convert
     * @return the spaced string
     */
    public static String camelCaseToSpaced(String camelCase) {
        // Insert a space before each uppercase letter, except the first one
        String spaced = camelCase.replaceAll("([A-Z])", " $1").trim();

        // Capitalize the first letter of each word
        return Arrays.stream(spaced.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

   /**
     * Checks if the turn timeout has been reached.
     *
     * @param startTime the start time of the turn.
     */
    private void checkForTurnTimeout(long startTime) {
        if (System.currentTimeMillis() - startTime > TURN_TIMEOUT_MS) {
            logger.warn("No answer from colony... Turn timeout");
            robotsCompletedTurn = true;
        }
    }

    /**
     * Determines whether the simulation has ended.
     *
     * @return true if the simulation is complete, false otherwise
     */
    private boolean checkEndOfSimulation() {
        return planet.getCurrentTurn() >= config.getTotalNumberOfYears() * Planet.DAYS_PER_YEAR;
    }

    /**
     * Generates a JSON error response with the specified message.
     *
     * @param message the error message
     * @return the JSON error response string
     */
    private String generateErrorResponse(String message) {
        return String.format("{\"status\":\"error\",\"message\":\"%s\"}", message);
    }

    // ------------------------ GETTERS AND SETTERS ---------------------------

    /**
     * Returns the map of colony robots.
     *
     * @return the map of robots
     */
    public Map<String, RobotInfo> getColonyRobots() {
        return robots;
    }

}
