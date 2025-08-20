# KFChessClientServerProject — **Kung-Fu Chess (real-time variant, adapted)**

**Real-time chess variant implemented in Java** — a Maven multi-module project (client / server / common) that adapts Kung-Fu Chess (_no-turns_) into a server-authoritative engine with smooth client animations, explicit `JUMP` support, per-piece cooldowns and deterministic resolution.

---

## Quick links (jump to code)

- **Server bootstrap:** `server/src/main/java/endpoint/launch/WebSocketServer.java`  
- **Server game/session logic:** `server/src/main/java/server/GameHandler.java`  
- **Game loop & command queue:** `common/src/main/java/game/Game.java`  
- **Board & rules:** `common/src/main/java/board/Board.java`  
- **Move command:** `common/src/main/java/command/MoveCommand.java`  
- **Jump command:** `common/src/main/java/command/JumpCommand.java`  
- **Piece & state machine:** `common/src/main/java/state/State.java`  
- **Physics & animations:** `common/src/main/java/state/PhysicsData.java`, `common/src/main/java/state/GraphicsData.java`  
- **Pieces loader:** `common/src/main/java/pieces/PiecesFactory.java`  
- **DTOs & network messages:** `common/src/main/java/dto/`  
- **Client entry:** `client/src/main/java/endpoint/launch/KFChessClientApp.java`  
- **Client UI:** `client/src/main/java/viewUtils/GamePanel.java`, `client/src/main/java/endpoint/view/BoardPanel.java`

---

# Overview

This repository implements a **server-authoritative, real-time** chess variant (Kung-Fu Chess) using:

- `common/` — shared game engine: pieces, board, commands, state machine, events, DTOs.  
- `server/` — WebSocket server: session management, input collection, action validation and deterministic execution.  
- `client/` — Swing UI: renders board, piece animations, sends player action requests to server and plays sounds.

The system uses `ICommand` objects (e.g., `MoveCommand`, `JumpCommand`) enqueued into `Game` for deterministic execution; `State` + `PhysicsData` model per-piece action durations and cooldowns, which is exactly what Kung-Fu Chess requires for timing-sensitive behavior.

---

# What is Kung-Fu Chess (rules & references)

**Kung-Fu Chess** is a family of real-time chess variants where timing replaces turns: pieces move in continuous time, moves take measurable time (animation + cooldown), and captures depend on who reaches/occupies a square first. Classical check/checkmate mechanics are not applicable — the game ends when a king/leader is captured or a player resigns.

Useful public references:

- Wikipedia — *Kung-Fu Chess* (real-time variant overview). https://en.wikipedia.org/wiki/Kung-Fu_Chess  
- Chess.com article / community writeups (discussion and examples). https://www.chess.com/blog/Jasmin_Ocelli/kung-fu-chess  
- Community implementations and historical pages (for inspiration): https://www.kfchess.com, https://kungfuchess.org

---

# How the Kung-Fu rules map to this codebase

This project already contains the right primitives; below is a direct mapping and recommended server-side flow.

## 1) No turns — server authoritative, asynchronous input

Clients send action requests (selection → intended action) at any time using DTOs (e.g., `PlayerSelectedDTO`). The server (`GameHandler`) validates requests and **enqueues** `ICommand` objects rather than applying them immediately. `Game` processes the queue deterministically and broadcasts snapshots (`GameDTO`) to clients. This enforces a single source of truth and resolves simultaneous inputs consistently.

## 2) One moving piece per request; cooldowns via `State`

Each piece has a `State` (e.g., `idle`, `move`, `jump`, `short_rest`, `long_rest`) with `PhysicsData` controlling action duration and `isActionFinished(now)` semantics. When a command executes, the piece transitions into `move`/`jump` and then to a rest state; server rejects further actions for that piece until it returns to `idle`. See `State`, `PhysicsData`, `GraphicsData`.

## 3) JUMP action — exact mapping

The project already includes `JumpCommand` and `Board.jump`. Recommended server flow for a jump:

1. Client sends action request (action `"JUMP"`, `pieceId`, `target`).  
2. Server validates: ownership, `Board.isInBounds(target)`, `Board.isJumpLegal(piece)` and `piece.getCurrentState().isActionFinished(now)` (cooldown check).  
3. If valid, create and enqueue `JumpCommand(board, pieceId, from, target)` into `Game`.  
4. `Game` executes the command: `JumpCommand.execute()` calls `board.jump(...)`, updates logical grid positions atomically, sets piece state to `JUMP` (calls state.reset(...)), triggers `PIECE_JUMP` / `PIECE_CAPTURED` events, and `Game` broadcasts `GameDTO` so clients animate the jump.  
5. After `JUMP` finishes (via `State` transition), the piece enters `short_rest`/`long_rest` and cannot move until `idle`.

## 4) Animation & timing consistency

Per-state JSON and `GraphicsData`/`PhysicsData` guarantee consistent durations across clients — use server timestamps in `GameDTO` so clients sync animations deterministically.

---

# Build & run

## Requirements

- Java 21 (JDK 21)  
- Maven 3.8+  
- Recommended: IntelliJ IDEA (Maven integration)

## Build

From repository root:

```bash
mvn clean install
```

## Run server

From repository root:

```bash
mvn -pl server exec:java -Dexec.mainClass="endpoint.launch.WebSocketServer"
```

Or run `endpoint.launch.WebSocketServer` from your IDE. Server uses `common/src/main/resources/config.properties` and defaults in `ServerConfig` for host/port/path.

## Run client(s)

From repository root (each client in its own JVM):

```bash
mvn -pl client exec:java -Dexec.mainClass="endpoint.launch.KFChessClientApp"
```

Or run `KFChessClientApp` from your IDE. Client prompts for username and connects to server endpoint.

## Local dev mode (no server)

Run:

```bash
client/src/main/java/local/launch/Main.java
```

This starts the UI with an in-process `Game` for rapid iteration and UI debugging.

---

# Configuration & resources

- `common/src/main/resources/config.properties` — runtime overrides (server.host, server.port, server.ws.path, log.file).  
- `common/src/main/resources/pieces/*` — per-piece `moves*.txt`, state JSON files and `transitions.csv` (control moves & animation). Edit these to adjust movement sets and per-state durations.  
- `common/src/main/java/constants/ServerConfig.java` — default server settings used by client & server.

**Tip:** When running from an IDE ensure `common` resources are on the classpath so images and move/state files are loaded.

---

# Message shapes (recommended)

**Client → Server (action request)**

```json
{
  "type": "PLAYER_SELECTED",
  "playerId": 1,
  "position": { "row": 4, "col": 3 },
  "clientTimestamp": 169xxxxxxx
}
```

Include server monotonic timestamps (and optionally `actionStartedAt`) to help clients synchronize animations.

---

# Tests (what to add / expand)

Add or extend these unit/integration tests to cover Kung-Fu behavior (especially jumps and cooldowns):

1. **`JumpCommandTest`** — assert logical position update, captured flag, `JUMP` → rest transitions, and event publication.  
2. **`CooldownEnforcerTest`** — ensure server rejects action requests when a piece is in a non-idle state.  
3. **`SimultaneousActionsIntegrationTest`** — simulate two clients issuing near-simultaneous actions and verify deterministic outcome.  
4. **`Player.handleSelection` tests** — returns correct `ICommand` type (`MoveCommand` vs `JumpCommand`) based on target and piece state.

Run tests:

```bash
mvn test
```

(GUI tests are guarded for headless CI.)

---

# References & further reading

- Kung-Fu Chess — Wikipedia: https://en.wikipedia.org/wiki/Kung-Fu_Chess  
- Kung-Fu Chess (community / wiki pages summarizing behavior): https://chess.fandom.com/wiki/Kung_Fu_Chess  
- Chess.com article (overview & discussion): https://www.chess.com/blog/Jasmin_Ocelli/kung-fu-chess  
- Community implementations (examples): https://www.kfchess.com, https://kungfuchess.org