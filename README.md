
# KFChess — Client/Server Turn-Based Chess-Like Engine

**KFChess** is a modular Java project implementing a turn-based, animated chess-like game with a client, server, and shared (`common`) logic. It includes a WebSocket-based networking layer, a Swing-based GUI with animated piece states, a pluggable state machine for piece behavior, an event-publisher system, and a comprehensive test suite.

---

## Table of contents

- [Features](#features)  
- [Architecture](#architecture)  
- [Repository layout](#repository-layout)  
- [Requirements & dependencies](#requirements--dependencies)  
- [Installation](#installation)  
- [Build & run](#build--run)  
  - [Build (all modules)](#build-all-modules)  
  - [Run server (dev)](#run-server-dev)  
  - [Run client (dev)](#run-client-dev)  
  - [Run packaged JAR (example)](#run-packaged-jar-example)  
- [Testing](#testing)  
- [Configuration & resources](#configuration--resources)  
- [Key implementation notes](#key-implementation-notes)  
- [Contributing](#contributing)  
- [Troubleshooting](#troubleshooting)  
- [License & contact](#license--contact)

---

## Features

- Modular Maven multi-module project: `client`, `common`, and `server`.  
- WebSocket-based server / client endpoint for multiplayer.  
- Swing GUI with animated piece states and a `StateMachine` driving physics/graphics.  
- Event-driven architecture via `EventPublisher` and `IEventListener` hooks.  
- Pluggable piece movement and states loaded from resource files (CSV / JSON).  
- Extensive JUnit tests across modules (unit + some GUI smoke tests).

---

## Architecture

High-level components:

- **Client** (`client/`) — Swing UI, `GameController`, WebSocket client endpoint (`ChessClientEndpoint`), rendering pipeline, and audio hooks.  
- **Common** (`common/`) — Core game model (`Game`, `Board`, `Piece`), `StateMachine`, DTOs, resource-driven piece/state definitions, and `EventPublisher`.  
- **Server** (`server/`) — WebSocket server launcher, `GameHandler`, message routing and lifecycle management.

Separation keeps game logic in `common`, UI in `client`, and networking in `server`.

---

## Repository layout (selected)

```
pom.xml
client/
  pom.xml
  src/main/java/...
  src/test/java/...
common/
  pom.xml
  src/main/java/...
  src/main/resources/pieces/...
  src/test/java/...
server/
  pom.xml
  src/main/java/...
  src/test/java/...
repomix-output.xml
```

---

## Requirements & dependencies

**Minimum system requirements**

- Java JDK 17 or later (JDK 21 recommended).  
- Apache Maven 3.6+ (or compatible).  
- Desktop OS with GUI support for running the client and GUI tests (or use Xvfb for headless CI).  
- ~1GB free disk for build artifacts (varies by environment).

**Key build/runtime dependencies** (declared in module pom.xml files)

- `javax.websocket` / `jakarta.websocket` (WebSocket API) for server/client endpoints.  
- `com.fasterxml.jackson.core` / `jackson-databind` for JSON serialization/deserialization.  
- `junit-jupiter` (JUnit 5) for unit tests.  
- Mockito for unit test mocking.  
- Standard Swing / AWT (part of the JDK) for GUI.

Check each module's `pom.xml` for exact artifact IDs and versions.

---

## Installation

1. Install Java (JDK 17+). Verify:

```bash
java -version
# example output: openjdk version "21.0.x"
```

2. Install Maven. Verify:

```bash
mvn -v
```

3. (Optional) Configure IDE: import as a Maven project (IntelliJ IDEA, Eclipse, or VS Code with Java extensions). Ensure each module (`client`, `common`, `server`) is recognized as a Maven module.

4. (Optional) If running GUI tests on CI, provide a virtual display (e.g., `Xvfb`) or ensure tests skip headless GUI checks.

---

## Build & run

### Build (all modules)

From repository root:

```bash
# Clean and build everything (runs tests)
mvn clean install
```

To skip tests during a quick build:

```bash
mvn clean install -DskipTests
```

### Run server (development)

Run the server main class via Maven Exec plugin (from repo root):

```bash
mvn -pl server exec:java -Dexec.mainClass="endpoint.launch.WebSocketServer"
```

Or run the packaged server JAR if available:

```bash
java -cp server/target/*:server/target/dependency/* endpoint.launch.WebSocketServer
```

> The server entrypoint is `endpoint.launch.WebSocketServer`. Adjust classpath if your build packages artifacts differently.

### Run client (development)

From repo root, run the client main:

```bash
mvn -pl client exec:java -Dexec.mainClass="endpoint.launch.KFChessClientApp"
```

Alternative local launcher:

```bash
mvn -pl client exec:java -Dexec.mainClass="local.launch.Main"
```

`KFChessClientApp` starts the Swing GUI and the `ChessClientEndpoint` WebSocket client.

### Run packaged JAR (example)

If modules are packaged as executable JARs:

```bash
# server
java -jar server/target/server-<version>.jar

# client
java -jar client/target/client-<version>.jar
```

Replace `<version>` with the built artifact version.

---

## Testing

Run tests for all modules:

```bash
mvn test
```

Run tests for a single module (example: `common`):

```bash
mvn -pl common test
```

Notes:
- GUI tests use `GraphicsEnvironment.isHeadless()` to skip on headless CI. Use a virtual display (Xvfb) or run on an environment with GUI support to execute them.
- Tests include concurrency and resource-loading checks (`EventPublisherConcurrencyTest`, `MovesTest`, `StateMachineTest`, etc.).

---

## Configuration & resources

Important resource directories:

- `common/src/main/resources/pieces/` — per-piece `moves*.txt` and `states/<state>/config.json`.  
- `common/src/main/resources/board/board.csv` — initial board layout.  
- `common/src/main/resources/state/` — transition tables for state machine.  
- `common/src/main/resources/config.properties` — runtime properties.  
- `common/src/main/resources/messages.properties` — user-facing messages.

To modify piece behavior or animations, update JSON/CSV resources and re-run tests.

---

## Key implementation notes & TODOs

- Some event listener classes (e.g., `CapturedLogger`, `JumpsLogger`, `GameEndLogger`) have empty handlers — consider implementing logging or analytics hooks.  
- When adding new piece types, ensure `moves*.txt` and `states/*/config.json` files are included. `Moves.createMovesList()` throws `IOException` if resources are missing.  
- `GameController` and other classes create background threads; follow proper thread lifecycle and interruption handling when modifying.  
- GUI tests are intentionally lightweight and guarded for headless environments.

---

## Contributing

1. Fork the repository.  
2. Create a branch: `git checkout -b feat/<description>`.  
3. Implement changes and add tests in the relevant module (`common`, `server`, `client`).  
4. Run `mvn clean install` and ensure tests pass.  
5. Open a pull request with description and test coverage.

Guidelines:
- Keep game logic in `common`.  
- Keep networking in `server`.  
- Keep UI in `client`.  
- Add unit tests for new logic; guard UI tests for headless environments.

---

## Troubleshooting

- **WebSocket connection refused**: confirm server is running and reachable at configured host/port in `config.properties` / `ServerConfig`.  
- **Missing resources at runtime**: ensure resources are packaged (check `target/classes` in each module).  
- **GUI tests fail on CI**: run them inside Xvfb or skip GUI tests on headless agents.  
- **Port conflicts**: change server port in configuration or ensure no other service occupies the port.

---

## License & contact

No license file is included by default. Consider adding `LICENSE` (e.g., MIT or Apache-2.0) if you intend to open-source the project.

For questions, issues, or code-review requests, open an issue in the repo or contact the maintainer.

---

_End of README._
