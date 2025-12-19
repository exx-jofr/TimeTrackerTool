# Time Tracker Tool

A modern desktop time tracking application built with **Kotlin Multiplatform** and **Compose Desktop**, designed for efficient task time management and work logging with Excel export capabilities.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Technology Stack](#technology-stack)
4. [Project Structure](#project-structure)
5. [Prerequisites](#prerequisites)
6. [Installation & Setup](#installation--setup)
7. [Build & Run](#build--run)
8. [Building for Distribution](#building-for-distribution)
9. [Configuration & Settings](#configuration--settings)
10. [Key Components](#key-components)
11. [Architecture](#architecture)
12. [Development Guide](#development-guide)
13. [Contributing](#contributing)
14. [License](#license)

---

## Overview

**Time Tracker** is a desktop application for managing work time entries, tracking tasks, and logging work activities. It provides an intuitive interface for time tracking, supports Jira integration, and enables data export to Excel format. This is a student project created to learn Kotlin Multiplatform development.

---

## Features

- **Time Tracking**: Record and manage time entries for different tasks
- **Work Logs**: Create and edit work log entries with detailed descriptions
- **Excel Export**: Export tracked time data to Excel (XLSX format)
- **Jira Integration**: Support for Jira connectivity (API integration ready)
- **Task Management**: Create, edit, and organize tasks
- **Settings & Configuration**: Persistent user settings via DataStore
- **System Tray Integration**: Minimize to system tray for background tracking
- **Date Picker**: Convenient date selection for time entries
- **Dark/Light Theme Support**: Material Design 3 theme system
- **Help Documentation**: Built-in help system and documentation

---

## Technology Stack

### Core Framework
- **Kotlin**: 2.2.20 - Modern programming language for JVM
- **Kotlin Multiplatform**: Cross-platform code sharing
- **Compose Multiplatform**: 1.9.1 - Declarative UI framework
- **Compose Material 3**: Latest Material Design implementation

### JVM & Desktop
- **Compose Desktop**: Native desktop UI rendering
- **Kotlinx Coroutines**: 1.10.2 - Asynchronous programming with Swing support

### Data Handling & Export
- **Apache POI**: 5.5.0 - Excel file generation (OOXML format)
- **DataStore Preferences**: 1.2.0 - Encrypted local data storage
- **Log4j**: 2.25.2 - Logging framework (API + Core)

### Build System
- **Gradle**: 8.x with Kotlin DSL (build.gradle.kts)
- **Compose Hot Reload**: 1.0.0-rc02 - Development time reload
- **Lifecycle ViewModel & Runtime Compose**: 2.9.5 - State management

---

## Project Structure

```
TimeTracker/
├── composeApp/                          # Main application module
│   ├── src/
│   │   ├── jvmMain/
│   │   │   ├── kotlin/org/exxjofr/timetracker/
│   │   │   │   ├── main.kt              # Application entry point
│   │   │   │   ├── App.kt               # Main UI composable
│   │   │   │   ├── Platform.kt          # Platform-specific code
│   │   │   │   ├── Task.kt              # Task data model
│   │   │   │   ├── TimeTable.kt         # Time table data structure
│   │   │   │   ├── BuisnessDay.kt       # Business day logic
│   │   │   │   ├── WorkLogEntry.kt      # Work log entry model
│   │   │   │   ├── ExcelWriter.kt       # Excel export functionality
│   │   │   │   ├── Jira.kt              # Jira API integration
│   │   │   │   ├── SettingsRepository.kt # Settings persistence
│   │   │   │   │
│   │   │   │   ├── components/          # UI Components
│   │   │   │   │   ├── App.kt           # Main app structure
│   │   │   │   │   ├── Body.kt          # Body/content area
│   │   │   │   │   ├── TableScreen.kt   # Time table display
│   │   │   │   │   ├── Settings.kt      # Settings UI
│   │   │   │   │   ├── Help.kt          # Help screen
│   │   │   │   │   ├── Navigationsbar.kt # Navigation bar
│   │   │   │   │   ├── SideMenu.kt      # Side menu
│   │   │   │   │   ├── Tray.kt          # System tray integration
│   │   │   │   │   ├── DatePickerDocked.kt # Date picker component
│   │   │   │   │   ├── SnackBarManager.kt  # Notification system
│   │   │   │   │   └── icon.png         # Application icon
│   │   │   │   │
│   │   │   │   └── ViewModel/           # MVVM ViewModel layer
│   │   │   │       └── SettingsModel.kt # Settings view model
│   │   │   │
│   │   │   └── resources/               # JVM-specific resources
│   │   │
│   │   └── jvmTest/                     # JVM unit tests
│   │
│   └── build.gradle.kts                 # Module-specific build config
│
├── gradle/
│   ├── libs.versions.toml              # Centralized dependency versions
│   └── wrapper/                        # Gradle wrapper files
│
├── build.gradle.kts                    # Root project build config
├── settings.gradle.kts                 # Project structure definition
├── gradle.properties                   # Gradle configuration
├── gradlew & gradlew.bat               # Gradle wrapper scripts
│
├── README.md                           # This file
├── BUILD_GUIDE.md                      # Build instructions
└── .gitignore                          # Git ignore rules
```

### Key Directories

- **[/composeApp](./composeApp/src)** - Main application code
  - **jvmMain** - Desktop (JVM) specific Kotlin code and UI components
  - **jvmTest** - Unit tests for JVM target
  - **resources** - Application resources (images, data files)
  
---

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd TimeTracker
```

### 2. Verify Java Installation
```bash
java -version
```
Ensure JDK 17+ is installed.

### 3. Build the Project
```bash
# On Windows
.\gradlew.bat build

# On macOS/Linux
./gradlew build
```

### 4. IDE Setup
- Open the project in IntelliJ IDEA
- Let the IDE index the project
- Wait for Gradle sync to complete
- Configure JDK: File → Project Structure → SDK → Select JDK 17+

---

## Build & Run

### Run Development Application

**Windows:**
```bash
.\gradlew.bat :composeApp:run
```

**macOS/Linux:**
```bash
./gradlew :composeApp:run
```

### Using IDE Run Configuration
1. Look for the run widget in the IDE toolbar
2. Select "composeApp [run]" configuration
3. Click the green "Run" button

### Build Without Running
```bash
# Windows
.\gradlew.bat :composeApp:build

# macOS/Linux
./gradlew :composeApp:build
```

---

## Building for Distribution

### Create Installable Package

The project is configured to generate native installers for Windows:

```bash
# Windows - Create MSI and EXE installers
.\gradlew.bat :composeApp:createDistributable
```

### Installer Configuration
The build system is configured to create:
- **MSI (Windows Installer)** - For system-wide installation
- **EXE (Portable)** - Standalone executable

**Key Settings** (in `composeApp/build.gradle.kts`):
- Package Name: `TimeTracker`
- Package Version: `1.0.0`
- Menu Group: `TimeTracker`
- Installation: Per-user (not system-wide)
- Directory Chooser: Enabled

---

## Configuration & Settings

### Gradle Properties
Located in `gradle.properties`:
- Kotlin code style: `official`
- JVM arguments: 3GB memory allocation
- Configuration cache: Enabled
- Build caching: Enabled

### Dependency Versions
All library versions are managed in `gradle/libs.versions.toml`:
- Kotlin: 2.2.20
- Compose Multiplatform: 1.9.1
- AndroidX Lifecycle: 2.9.5
- Kotlinx Coroutines: 1.10.2
- Apache POI: 5.5.0
- Log4j: 2.25.2

### User Settings
Settings are persisted using DataStore in encrypted format at:
- **Windows**: `%APPDATA%/TimeTracker/settings.properties`
- **macOS**: `~/Library/Application Support/TimeTracker/settings.properties`
- **Linux**: `~/.local/share/TimeTracker/settings.properties`

---

## Key Components

### Data Models

#### Task
- Represents a work task/project
- Contains task ID, name, description
- Used for categorizing time entries

#### WorkLogEntry
- Individual time entry record
- Stores start/end time, duration, task reference
- Can include work description/notes

#### TimeTable
- Collection of work log entries for a specific date
- Manages daily time tracking data
- Supports filtering and aggregation

#### BuisnessDay
- Represents a working day
- Contains business hours configuration
- Handles holiday and weekend logic

### Services

#### ExcelWriter
- Exports time tracking data to Excel (OOXML)
- Supports formatted tables with styling
- Generates time reports with summaries

#### Jira
- Jira API integration module
- Handles task synchronization
- Work log submission to Jira (when configured)

#### SettingsRepository
- Manages application settings persistence
- Uses encrypted DataStore
- Handles user preferences and configuration

### UI Components (in `components/` folder)

| Component | Purpose |
|-----------|---------|
| **App.kt** | Main application structure and layout |
| **Body.kt** | Primary content/body area |
| **TableScreen.kt** | Time table display and interaction |
| **Settings.kt** | User settings and configuration UI |
| **Help.kt** | Help documentation and user guide |
| **Navigationsbar.kt** | Top navigation bar |
| **SideMenu.kt** | Side navigation menu |
| **DatePickerDocked.kt** | Date selection widget |
| **Tray.kt** | System tray integration |
| **SnackBarManager.kt** | Toast/notification system |

### ViewModel Layer

#### SettingsModel
- MVVM pattern implementation
- Manages settings state
- Reactive state updates for UI

---

## Architecture

### Pattern: MVVM (Model-View-ViewModel)

```
┌─────────────────┐
│      UI Layer   │  (Compose Components)
│   (View)        │
└────────┬────────┘
         │ Observes
         │
┌────────▼────────┐
│   ViewModel     │  (SettingsModel, etc.)
│   Layer         │
└────────┬────────┘
         │ Uses
         │
┌────────▼────────┐
│   Data Layer    │  (Models, Repositories)
│   (Model)       │
└─────────────────┘
```

### Data Flow

1. **User Interaction** → UI Component triggers action
2. **ViewModel Updates** → State changes propagate
3. **Repository Operations** → Data persistence/retrieval
4. **UI Recomposition** → Compose re-renders affected UI

### Platform-Specific Code

Platform-specific implementation is organized in:
- `jvmMain/kotlin` - Desktop/JVM specific code
- Uses `Platform.kt` for platform detection and abstraction
- Enables future multiplatform expansion (iOS, Android, Web)

---

## Development Guide

### Code Organization Best Practices

1. **Models** (`Task.kt`, `WorkLogEntry.kt`, etc.)
   - Place in root of package
   - Keep data classes simple and immutable

2. **UI Components** (`components/` folder)
   - One component per file (generally)
   - Use `@Composable` annotation
   - Props should be immutable

3. **Services/Repositories** (`*Repository.kt`, `*Service.kt`)
   - Handle business logic and data access
   - Support dependency injection
   - Return coroutine Flow for reactive data

4. **ViewModels** (`ViewModel/` folder)
   - Extend `ViewModel` from lifecycle library
   - Use StateFlow for state management
   - Implement proper scope management

---

## Dependencies Overview

### Compose & UI
- `compose.runtime` - Composition and state management
- `compose.foundation` - Layout and basic components
- `compose.material3` - Material Design 3 components
- `compose.ui` - Core UI primitives
- `compose.components.resources` - Resource handling
- `compose.desktop.currentOs` - OS-specific Compose features

### State Management
- `androidx.lifecycle.viewmodelCompose` - ViewModel integration
- `androidx.lifecycle.runtimeCompose` - Lifecycle awareness
- `kotlinx.coroutines.swing` - Coroutine support for Swing/Desktop

### Data & Storage
- `androidx.datastore.preferences` - Encrypted local storage
- `org.apache.poi:poi-ooxml` - Excel file generation

### Logging
- `org.apache.logging.log4j:log4j-api` - Logging API
- `org.apache.logging.log4j:log4j-core` - Logging implementation

### Material Icons
- `org.jetbrains.compose.material:material-icons-extended` - Extended icon set

---

## Contributing

### Development Workflow

1. **Create a Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   - Follow Kotlin coding standards
   - Write tests for new features
   - Keep commits atomic and well-documented

3. **Run Quality Checks**
   ```bash
   .\gradlew.bat build test
   ```

4. **Submit Pull Request**
   - Provide clear description of changes
   - Reference related issues
   - Ensure CI/CD passes

---

## Common Issues & Troubleshooting

### Gradle Sync Issues
```bash
# Clean Gradle cache
./gradlew clean

# Force re-sync
./gradlew --refresh-dependencies
```

---


## License

This project is a student project for learning purposes. 

---

**Last Updated**: December 2025

