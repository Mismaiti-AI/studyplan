# StudyPlan

MyTask is a Compose Multiplatform mobile app designed to track homework and exam dates. The app connects to Google Sheets as a backend, allowing users to manage their academic tasks efficiently.

## Features

- **Assignment Tracking**: View and mark homework assignments as completed
- **Exam Tracking**: View upcoming exam dates and details
- **Project Tracking**: Track school project deadlines and progress
- **Dashboard Overview**: Central overview showing upcoming assignments, exams, and projects with deadlines
- **Google Sheets Configuration**: Allow users to update the Google Sheets URL to connect to different grade/teacher spreadsheets

## Tech Stack

- Compose Multiplatform (Android & iOS)
- Kotlin Coroutines & Flow
- Koin for Dependency Injection
- Room Database for local caching
- Google Sheets API integration
- Material Design 3

## Architecture

The app follows MVVM architecture with clean separation of concerns:

- **Presentation Layer**: Compose UI components with ViewModels
- **Domain Layer**: Business logic with Use Cases
- **Data Layer**: Repository pattern with local Room database and Google Sheets API

## Installation

### Prerequisites

- Android Studio (for Android development)
- Xcode (for iOS development)
- Kotlin 1.9+
- Compose Multiplatform plugin

### Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run the app on your preferred target (Android emulator/device or iOS simulator/device)

## Usage

1. On first launch, configure your Google Sheets URL
2. View your dashboard for an overview of upcoming tasks
3. Navigate to individual sections to manage assignments, exams, and projects
4. Mark items as completed to track your progress

## Data Models

- **Assignment**: Title, description, due date, subject, completion status, priority
- **Exam**: Title, subject, exam date, description, preparation status
- **Project**: Title, description, start/due dates, subject, progress percentage, completion status
- **AppConfig**: Google Sheets URL, timestamps

## Backend Integration

The app integrates with Google Sheets through a custom Apps Script API, providing real-time synchronization of academic data. The Google Sheets backend allows teachers or administrators to easily manage the data that students see in the app.

## License

This project is licensed under the MIT License - see the LICENSE file for details.