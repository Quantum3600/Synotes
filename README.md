
<div align="center">

# Synotes

A lightweight, synced notepad for low-spec setups.

<!-- Badges -->
![Status](https://img.shields.io/badge/status-WIP-orange)
![Platform](https://img.shields.io/badge/platform-Android%20%7C%20Desktop-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-KMP-7F52FF?logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white)
![License](https://img.shields.io/badge/license-TBD-lightgrey)

</div>

## ✨ What is Synotes?

I’ve always used Notepad for fast scribbles and quick code snippets—but over time it stopped feeling truly lightweight. On a low-spec PC, keeping multiple heavy apps open (Android Studio + a browser, for example) isn’t really an option, and the “read docs on mobile, type on desktop” routine is slow and awkward. Even tools like Phone Link can add more overhead than they’re worth.

**Synotes** is my attempt at a **small, fast notes app** that **syncs between Desktop and Android**, so you can keep a simple workflow without running a stack of heavyweight programs.

> **Status:** Work in progress. Google Sign-In is still unimpelmented for Desktop—Firebase support for KMP is currently a limitation.

---

## 🧩 Tech Stack

- **Kotlin Multiplatform (KMP)**
- **Compose Multiplatform** (UI)
- Targets: **Android** + **Desktop**
- **Gradle** build

---

## 📸 Screenshots

> Screenshots live in: `.github/assets`

### Android
<p float="left">
  <img src=".github/Screenshot_2026-03-01-20-20-56-771_com.trishit.synotes.jpg" width="240" alt="Android screenshot 1" />
  <img src=".github/Screenshot_2026-03-01-21-09-03-064_com.trishit.synotes.jpg" width="240" alt="Android screenshot 2" />
</p>

### Desktop
<p float="left">
  <img src=".github/Screenshot 2026-03-01 202225.png" width="520" alt="Desktop screenshot 1" />
  <img src=".github/Screenshot 2026-03-01 211234.png" width="520" alt="Desktop screenshot 2" />
</p>

---

## ✅ Features

- Lightweight note-taking for quick thoughts and code snippets
- Desktop ↔ Android workflow
- Google Auth for Desktop (WIP)

---

## 🚀 Running Locally

### Prerequisites

- **JDK 17**
- **Android Studio** (recommended for KMP + Android)
- Use the Gradle wrapper: `./gradlew`

### This is a Kotlin Multiplatform project targeting Android, Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
---

## 🤝 Contributing

Contributors are welcome.

- Open an issue for bugs / feature requests
- PRs are appreciated (small, focused PRs are easiest to review)

---

## 🗺️ Roadmap / Notes

- Google Auth for Desktop is under active development
- Current limitation: **Firebase doesn’t fully support KMP** for the intended approach

---

## 📄 License

**TBD** — add a `LICENSE` file and update the badge above.

