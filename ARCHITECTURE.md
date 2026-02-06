# Project Architecture Documentation

This document provides a detailed overview of the core architecture of the project. While it was originally developed as a BedWars plugin, the underlying framework is designed to be a robust foundation for any arena-based Minecraft minigame.

---

## 1. Project Structure (Modules)

The project is organized into several modules to ensure a clean separation of concerns and to support multiple Minecraft versions:

- **`bedwars-api`**: Contains all interfaces and common data structures. This is the entry point for addons and other plugins. It has no dependencies on NMS or the plugin's internal implementation.
- **`bedwars-plugin`**: The main implementation module. It contains the core logic for arena management, game flow, and BedWars-specific mechanics.
- **`versionsupport_*`**: These modules provide NMS (Native Minecraft Server) abstractions for different Minecraft versions (e.g., 1.8.8, 1.12.2, 1.20.x). They implement the `VersionSupport` interface defined in the API.
- **`versionsupport_common`**: Contains code that is shared across multiple version support modules.
- **`resetadapter_*`**: Modules that handle world restoration using different backends (e.g., SlimeWorldManager, internal ZIP restore).
- **`buildSrc`**: Contains Gradle build logic and convention plugins used across all modules.

---

## 2. NMS Abstraction Layer (`VersionSupport`)

To support multiple Minecraft versions, the project uses a robust abstraction layer.

- **`VersionSupport` (Abstract Class)**: Defines methods for actions that vary between Minecraft versions, such as:
    - Sending titles and action bars.
    - Handling NBT tags on items.
    - Spawning custom entities or NPCs.
    - Managing player collisions.
    - Version-specific material and sound mapping.
- **Implementation**: Each `versionsupport_*` module provides a concrete implementation for a specific NMS version.
- **Dynamic Loading**: On startup, the plugin detects the server version and reflects the appropriate `VersionSupport` implementation.

---

## 3. Arena Lifecycle and Game States

The project uses a state-machine approach to manage game flow within each arena.

### Game States (`GameState` Enum)
1. **`waiting`**: The arena is ready for players to join.
2. **`starting`**: The minimum number of players has been reached, and a countdown is in progress.
3. **`playing`**: The game is active.
4. **`restarting`**: The game has ended, players are being sent back to the lobby, and the world is being restored.

### Arena Management
- **`IArena`**: The core interface for an arena, providing methods to manage players, spectators, teams, and the game state.
- **`Arena`**: The main implementation of `IArena`. It handles the transition between states and coordinates tasks.
- **`ArenaManager`**: (Often implemented via static methods or a utility class) Handles the registry of all loaded arenas.

---

## 4. Task System

The game logic is driven by various tasks (BukkitRunnables):

- **State-Specific Tasks**:
    - `StartingTask`: Manages the countdown before the game starts.
    - `PlayingTask`: Manages the game duration, periodic events, and win conditions.
    - `RestartingTask`: Handles the cleanup and world restore after a game ends.
- **Utility Tasks**:
    - `HologramTask`: Updates holograms periodically.
    - `RefreshTask`: General periodic refreshes (e.g., scoreboard updates).

---

## 5. Configuration and Localization

### Configuration System
- **`ConfigManager`**: A wrapper around Bukkit's `YamlConfiguration` that provides easier access to nested paths, automatic saving, and default value handling.
- **Centralized Paths**: `ConfigPath` in the API module contains all the string constants for configuration keys, ensuring type safety and easy refactoring.

### Localization (`Language`)
- **Per-Player Language**: Players can choose their preferred language via `/bw lang`.
- **Dynamic Placeholders**: Messages support internal placeholders (e.g., `%bw_player%`) and integration with PlaceholderAPI.
- **Message Bundles**: YAML files in the `languages/` directory store all translatable strings.

---

## 6. World Restoration System (`RestoreAdapter`)

Since minigames often involve players modifying the world, a reliable restore system is crucial.

- **`RestoreAdapter`**: An interface for world restoration.
- **Internal Adapter**: Uses ZIP files to backup the arena world and extracts them when the arena restarts.
- **External Adapters**: Hooks into plugins like SlimeWorldManager (SWM) for faster, asynchronous world loading and restoring.

---

## 7. Player and Spectator Management

- **Player Tracking**: The plugin tracks players per arena, ensuring they are removed from the global player pool for certain events.
- **Spectator Mode**: A sophisticated spectator system that handles invisibility, flight, and specialized spectator items.
- **Re-Join System**: Allows players who disconnected to rejoin their active session within a certain timeframe.

---

## 8. API and Extensibility

- **`BedWars` Interface**: The main API entry point. It provides access to all managers (Arena, Shop, Stats, etc.).
- **Addon System**: A built-in system to load JAR files from an `addons/` folder, allowing developers to extend the plugin without modifying the core source code.
- **Custom Events**: A wide range of Bukkit events (e.g., `PlayerJoinArenaEvent`, `GameStateChangeEvent`) are fired for external plugins to hook into.

---

## 9. Data Persistence

- **`IDatabase` Interface**: Abstraction for player statistics and data.
- **Implementations**: Support for **MySQL**, **SQLite**, and **H2**.
- **Redis Support**: Used for synchronizing arena states across multiple servers in a BungeeCord/Velocity network (Scalable Bungee mode).

---

## 10. Adapting for Other Minigames

To use this project as a base for a different minigame, you can follow these steps:

1. **Retain the Core**: Keep the API, NMS support, Arena lifecycle, and Config/Language systems.
2. **Remove BedWars Specifics**:
    - Strip BedWars-specific methods from `IArena` (e.g., bed-related methods).
    - Remove or replace the `shop` and `upgrades` packages.
    - Modify `BedWarsTeam` to fit your game's requirements.
3. **Implement Your Logic**:
    - Create new tasks for the `playing` state that implement your game's unique mechanics.
    - Define new win conditions in your `PlayingTask` or `Arena` implementation.
    - Register new custom items and handlers.

This architecture provides the "plumbing" (arenas, world reset, multi-version support, localization) so you can focus on your game's unique "mechanics".
