# Task Manager App

Task Manager App is a mobile application built to help users manage their tasks effectively. It offers features like task creation, editing, deletion, and a dashboard to visualize tasks. The app is developed using MVVM architecture with Clean Architecture principles to ensure separation of concerns and maintainability. Dark mode and notification functionalities are also integrated to enhance user experience.

## Features

- **Task Management:** Add, edit, delete tasks with due dates, priority, and location.
- **Dashboard:** Overview of tasks with sorting and filtering options.
- **Dark Mode:** Toggle between light and dark themes for comfortable viewing.
- **Notifications:** Reminders based on task due dates and proximity.

## Architecture

The app follows the **MVVM with Clean Architecture principles**  and for cloud database used **Firebase**:

- **Presentation Layer:** Composed of UI components built with Jetpack Compose.
- **Domain Layer:** Contains business logic and use cases independent of the framework.
- **Data Layer:** Manages local data storage with Room Database and remote data with Firebase Firestore.

## APP LINK

[Download the app](https://github.com/sudhanshuGt/TaskManager/blob/main/screen_shots/app-debug.apk)

## VIDEO LINK

[Watch the video](https://drive.google.com/file/d/1f3pvRlUPUueRS_AubHfbLtNxNouXSnxj/view?usp=sharing)

## Screenshots

Include screenshots of different screens here to showcase the app's UI.

<div style="display: flex; flex-wrap: wrap;">
  <img src="https://github.com/sudhanshuGt/TaskManager/blob/main/screen_shots/Login.png" alt="Login Screen" style="height: 520px; width: 324px; margin: 5px;">
  <img src="https://github.com/sudhanshuGt/TaskManager/blob/main/screen_shots/home.png" alt="Home Screen" style="height: 520px; width: 324px; margin: 5px;">
  <img src="https://github.com/sudhanshuGt/TaskManager/blob/main/screen_shots/dashboard.png" alt="Dashboard Screen" style="height: 520px; width: 324px; margin: 5px;">
  <img src="https://github.com/sudhanshuGt/TaskManager/blob/main/screen_shots/setting.png" alt="Settings Screen" style="height: 520px; width: 324px; margin: 5px;">
  <img src="https://github.com/sudhanshuGt/TaskManager/blob/main/screen_shots/edit_option.png" alt="Edit Option Screen" style="height: 520px; width: 324px; margin: 5px;">
</div>
