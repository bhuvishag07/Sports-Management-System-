# Sports Club Membership Management System

A Java Swing desktop application for managing sports club members, sports, coaches, training sessions, memberships, and payments. The project is implemented with the standard Java library and stores application data locally using Java object serialization; it does not require a database, cloud service, or external API.

## Features

- Member registration and CRUD, including member search and name/expiry sorting
- Sports and coach CRUD with search
- Member enrollment in sports and fixed-slot training schedules
- Membership plan renewal, expiry alerts, and payment history
- Training history and a club report with export
- Dashboard, navigation menus, Swing tables, dialogs, and confirmation prompts
- Local save/load and reset-to-sample-data actions

## Java Concepts

| Requirement | Implementation |
| --- | --- |
| Classes and objects | `Member`, `Sport`, `Coach`, `Training`, `Membership`, and `Payment` models |
| Constructors | Parameterized and default constructors initialize the core model objects |
| Array | Fixed training slots in `Training` and sport categories in `Sport` |
| ArrayList | Members, sports, and coaches |
| LinkedList | Training schedule/history and payment history |
| HashMap | Member ID lookup index |
| TreeMap | Members grouped by membership expiry date |
| HashSet | Unique sport enrollment for each member |
| Swing | Desktop interface with forms, tables, menus, and dialogs |
| Persistence | Java serialization to `data/sports_club_data.dat` |

## Project Structure

```text
src/
  Main.java                  Application entry point
  TestRunner.java            Controller and persistence verification suite
  controller/                ClubController business logic
  model/                     Member, sport, coach, training, membership, and payment data
  view/                      Swing frame, panels, tables, and theme
compile.sh                   Compile the application and test runner
run.sh                       Compile and launch on macOS/Linux
run.bat                      Compile and launch on Windows
```

The project follows a small MVC structure: model classes hold club data, `ClubController` implements validation and operations, and the Swing classes in `view` present the interface and handle user actions.

## Requirements and Run Instructions

- JDK 8 or newer
- No third-party dependencies
- Run commands from the project directory

### macOS / Linux

```sh
./compile.sh
./run.sh
```

`run.sh` recompiles the current sources before launching the GUI. To launch an already compiled build manually:

```sh
java -cp bin Main
```

### Windows

Run `run.bat` from the project directory. It compiles the current sources and then launches the Swing application.

## Verification Suite

Compile and run the controller/persistence tests with:

```sh
./compile.sh
java -cp bin TestRunner
```

The test runner uses temporary files and does not reset or overwrite `data/sports_club_data.dat`.

## Local Data and Reset

On first launch, the application creates `data/sports_club_data.dat` with sample club records. Subsequent launches load the saved records. Data-changing actions auto-save; the File menu also provides explicit save and load actions.

The **Reset to Sample Data** menu action replaces the current application data with the bundled sample records after a confirmation prompt. Back up any local club data before confirming a reset. Runtime data files are excluded from version control by `.gitignore`.
