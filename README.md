# Sports Club Membership Management System

### Java Programming — Case Study 47

A desktop-based application developed in Java to simplify the management of sports club members, sports, coaches, training schedules, memberships, and payments.

---



## 1. Project Overview

Managing a sports club involves more than maintaining a list of members. The club also needs to keep track of membership plans, sports categories, coaches, training schedules, enrollment records, and payment history. When these activities are handled manually, information can become difficult to organize, update, and retrieve.

The **Sports Club Management System** is a Java-based desktop application designed to bring these activities together in one place. It provides a graphical user interface through which club administrators can manage records, search for information, organize training schedules, track memberships, and maintain payment history.

The project was developed as part of the Java Programming course using object-oriented programming, Java Collections Framework, exception handling, file handling, and Java Swing.

The goal was to apply classroom concepts to a practical application while keeping the interface straightforward and the code organized into separate model, view, and controller components.

## 2. Case Study Details

**Official Case Study:** Case Study 47 — Sports Club Membership Management System

### Problem Statement

A sports club needs an organized way to manage its members, available sports, coaches, training schedules, membership plans, and payment records. The system should allow administrators to register members, maintain sports and coach information, manage training activities, track membership validity, record payments, and retrieve records efficiently.

The application should provide a user-friendly desktop interface and use appropriate Java data structures to store and process information. It should also validate user input, handle invalid operations gracefully, support searching and sorting, and maintain relevant records between application sessions.

### Objectives

- Create and maintain member records.
- Manage sports categories and coach information.
- Organize training schedules and enrollment details.
- Maintain membership information and expiry dates.
- Record payments and preserve payment history.
- Search for members, sports, and coaches.
- Sort member records by name and membership expiry.
- Use appropriate data structures for different types of information.
- Apply object-oriented programming principles and exception handling.
- Provide an interactive interface using Java Swing.
- Store application data locally so that records can be available across sessions.

### Proposed Solution

The application combines the major activities of a sports club into a single desktop interface. It uses separate classes for the main entities, collection types for managing records, controller logic for operations, and Swing components for user interaction.

The implementation also includes validation for membership plans, renewal dates, training inputs, and payment-related data.

## 3. Key Features

### 3.1 Dashboard

The dashboard provides a central place to access the main sections of the application. It helps users navigate between member management, sports, coaches, memberships, training, payments, and reports.



<img width="2940" height="1912" alt="image" src="https://github.com/user-attachments/assets/7582b27f-0f51-47fe-b54f-4be2ef444cdf" />


### 3.2 Member Management

The member management module supports the creation, viewing, updating, and deletion of member records.

It also provides member search functionality and supports sorting records by name and membership expiry date. Member information is organized using appropriate collections to make lookup and record management easier.



<img width="2940" height="1904" alt="33474B06-BA67-457C-BB73-1C3202D0653F" src="https://github.com/user-attachments/assets/67337691-3de4-40f8-ac8a-2932933c0a0f" />


### 3.3 Sports Management

The sports management module maintains information about the sports offered by the club. Users can add new sports, view existing records, update information, delete records, and search for a particular sport.

This helps keep the available sports categories organized and accessible.

<img width="2940" height="1912" alt="image" src="https://github.com/user-attachments/assets/452dea23-2562-4b75-866f-e3ac3928771b" />



### 3.4 Coach Management

The coach management module maintains coach records and supports standard record-management operations. Search functionality helps users find coach information without manually browsing every entry.

<img width="2940" height="1912" alt="image" src="https://github.com/user-attachments/assets/bfcfd183-be8f-4e72-baf9-769fde848237" />


### 3.5 Membership Management

The membership module manages membership information, including membership plans and expiry dates.

The application validates supported membership plans and renewal dates to reduce invalid entries. Membership expiry information can also be organized for sorting and reporting.

<img width="2916" height="1912" alt="257877EA-282F-4198-8F56-DE1AB80A60F1" src="https://github.com/user-attachments/assets/9aacccc6-8994-4ed5-9691-24647b5a7601" />


### 3.6 Training and Enrollment

The training module supports the management of training schedules and related information. Enrollment functionality connects members with relevant sports and training activities.

Training inputs are validated so that unsupported or invalid values can be rejected instead of being accepted as normal records.

**Screenshot placeholder:**

<img width="2940" height="1912" alt="image" src="https://github.com/user-attachments/assets/18d4bfe3-9830-443d-a9f1-dd82b04e1f6d" />


### 3.7 Payment Management

The payment module records payment-related information and supports the maintenance of payment history.

Using a dedicated payment model and a linked collection for history helps keep payment records separate from other member and training information.


<img width="2940" height="1912" alt="image" src="https://github.com/user-attachments/assets/690b2496-bf62-4073-ab07-4fd711b64fbb" />

### 3.8 Training History

The training history section maintains relevant training records so that previous activities can be reviewed rather than treated as temporary information.


<img width="2940" height="1912" alt="image" src="https://github.com/user-attachments/assets/a723993b-0c99-4459-ae60-c8fdb02587bc" />

### 3.9 Search and Sorting

The application provides search functionality for members, sports, and coaches. Member records can also be sorted by name and membership expiry date.

These features make it easier to retrieve specific information and review records in an organized order.

<img width="2940" height="1912" alt="image" src="https://github.com/user-attachments/assets/9936378d-289b-4fc6-9e1d-dbe8b834b590" />


### 3.10 Reports

The reporting section provides access to organized information from the application to support reviewing club records.

*<img width="2940" height="1912" alt="image" src="https://github.com/user-attachments/assets/6ada557b-751a-4f4f-a7b1-b3cc03a69153" />


### 3.11 Input Validation and Exception Handling

Validation is used to reject invalid membership plans, renewal dates, training inputs, and payment-related data.

Custom exceptions, including `InvalidMemberException` and `InvalidTrainingException`, are used for relevant error conditions. These mechanisms help prevent invalid information from being processed as a valid operation.

### 3.12 Local Data Persistence

The application includes local data storage through serialized application data. This supports retaining relevant records between sessions.

The local data file is kept outside the GitHub repository to avoid uploading potentially private application records.

## 4. Technologies and Tools Used

| Technology / Tool | Purpose |
|---|---|
| **Java** | Main programming language |
| **Java Swing** | Desktop graphical user interface |
| **Object-Oriented Programming** | Organizing entities, behavior, and application logic |
| **Java Collections Framework** | Storing, retrieving, and organizing records |
| **Array** | Managing fixed collections such as training slots and sport categories |
| **ArrayList** | Managing member, sport, and coach collections |
| **LinkedList** | Maintaining training and payment history |
| **HashMap** | Looking up members using their IDs |
| **TreeMap** | Organizing members by membership expiry date |
| **Exception Handling** | Handling invalid operations and input |
| **File Handling / Serialization** | Saving application data locally |
| **JTable and Swing Dialogs** | Presenting tabular records and interactive messages |
| **Shell Script (`compile.sh`, `run.sh`)** | Compiling and launching the application on supported Unix-like environments |
| **Windows Batch (`run.bat`)** | Providing a Windows launch script |

*The table describes the technologies and structures used in the implementation. The Windows batch script has not yet been verified on Windows.*

## 5. Java Concepts Demonstrated

### 5.1 Classes and Objects

The application represents real-world entities through Java classes such as `Member`, `Sport`, `Coach`, `Training`, `Membership`, and `Payment`.

Each class groups the information and operations relevant to its entity.

### 5.2 Encapsulation

The model classes organize related data and behavior. Keeping entity logic separate from the graphical interface helps make the application easier to maintain.

### 5.3 Inheritance and Polymorphism

The application includes a `Person` base class and related person-oriented models. This demonstrates how common characteristics can be shared through inheritance, while polymorphism allows related objects to be treated through a common type where appropriate.

### 5.4 Constructors

Constructors are used to initialize objects when members, sports, coaches, training records, and other entities are created.

### 5.5 Arrays and Collections

Different data structures are used according to the type of information being managed:

- Arrays are suitable for fixed-size groups of values.
- `ArrayList` supports collections that can grow or shrink.
- `LinkedList` is used for training and payment histories.
- `HashMap` provides member lookup by ID.
- `TreeMap` organizes membership expiry information in sorted-key order.

Choosing different structures demonstrates that one collection type is not necessarily ideal for every requirement.

### 5.6 Exception Handling

Exception handling allows the application to respond to invalid operations without treating incorrect input as valid data. Custom exceptions provide more specific handling for relevant member and training errors.

### 5.7 File Handling

Local persistence allows application records to be retained beyond a single execution. The project also separates local saved data from the source files intended for version control.

### 5.8 GUI Programming

Java Swing is used to build the desktop interface. Panels, tables, menus, and dialogs help users interact with application features without needing to enter commands in a terminal.

## 6. Application Architecture

The project separates responsibilities into model, view, and controller components.

### Model

The `model` package contains the application's data entities and related exceptions.

Examples:

- `Person.java`
- `Member.java`
- `Sport.java`
- `Coach.java`
- `Training.java`
- `Membership.java`
- `Payment.java`
- `ClubData.java`
- `InvalidMemberException.java`
- `InvalidTrainingException.java`

### View

The `view` package contains the graphical interface and its individual panels.

Examples:

- `MainFrame.java`
- `DashboardPanel.java`
- `MemberPanel.java`
- `SportsPanel.java`
- `CoachPanel.java`
- `MembershipPanel.java`
- `TrainingPanel.java`
- `EnrollmentPanel.java`
- `PaymentPanel.java`
- `TrainingHistoryPanel.java`
- `SearchSortPanel.java`
- `ReportPanel.java`
- `Theme.java`

### Controller

The `controller` package contains `ClubController.java`, which manages application operations between the interface and the underlying data.

### Application Entry Point

`Main.java` provides the application entry point.

This separation follows the general Model–View–Controller (MVC) approach: models represent data, views present the interface, and the controller handles application operations.

## 7. Project Folder Structure

The main project structure is organized as follows:

```text
FINAL JAVA PROJECT/
│
├── src/
│   ├── Main.java
│   ├── TestRunner.java
│   │
│   ├── controller/
│   │   └── ClubController.java
│   │
│   ├── model/
│   │   ├── Person.java
│   │   ├── Member.java
│   │   ├── Sport.java
│   │   ├── Coach.java
│   │   ├── Training.java
│   │   ├── Membership.java
│   │   ├── Payment.java
│   │   ├── ClubData.java
│   │   ├── InvalidMemberException.java
│   │   └── InvalidTrainingException.java
│   │
│   └── view/
│       ├── MainFrame.java
│       ├── DashboardPanel.java
│       ├── MemberPanel.java
│       ├── SportsPanel.java
│       ├── CoachPanel.java
│       ├── MembershipPanel.java
│       ├── TrainingPanel.java
│       ├── EnrollmentPanel.java
│       ├── PaymentPanel.java
│       ├── TrainingHistoryPanel.java
│       ├── SearchSortPanel.java
│       ├── ReportPanel.java
│       └── Theme.java
│
├── compile.sh
├── run.sh
├── run.bat
├── .gitignore
└── README.md
```

The `bin/` directory contains generated compiled classes and is excluded from version control. Local saved data is also excluded from the repository.

## 8. How to Run the Project

### Prerequisites

- Java Development Kit (JDK) installed.
- A terminal or command prompt.
- Git is optional for running the application.

Check whether Java is installed:

```bash
java -version
javac -version
```

### Option 1: macOS or Linux

Open a terminal in the project directory and run:

```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

The scripts should compile and launch the application according to their configured commands.

### Option 2: Windows

Open Command Prompt or PowerShell in the project directory and run:

```bat
run.bat
```

The Windows script is included, but its behavior should be verified on a Windows machine before claiming Windows compatibility has been tested.

## 9. Testing and Verification

The project has undergone compilation and automated controller/persistence testing.

| Test Area | Status |
|---|---|
| Array usage | Passed |
| ArrayList usage |

## Student Details

| Field | Details |
|---|---|
| **Student Name** | Bhuvisha Gohil |
| **College / University** | ITM Skills University |
| **Programme** | B.Tech in Computer Science and Engineering |
| **Academic Year** | 2025–2029 |
| **Semester** | III |
| **Subject** | Java Programming |
| **Case Study Number** | 47 |
| **Case Study Name** | Sports Club Membership Management System |
| **Project Title** | Sports Club Management System |



---
