# Employee Directory - Android (Kotlin)

A modern, production-grade Android application built in Kotlin demonstrating **MVVM Architecture**, **Kotlin Coroutines & StateFlow**, **Retrofit & OkHttp**, and **Material 3 Design**.

Developed for a technical hiring assignment.

---

## 📱 Features

- **Asynchronous Data Fetching**: Fetches employee records from `https://aamras.com/dummy/EmployeeDetails.json` using Retrofit and OkHttp with custom timeout and connection failure handling.
- **Dual-Schema JSON Resilience**: Handles both naked root JSON arrays (`[...]`) and enclosed root JSON objects (`{"employees": [...]}` or `{"data": [...]}`) seamlessly.
- **Smooth RecyclerView List**: Displays employees inside elevated MaterialCardViews with ripple effects and dynamic colored initials avatar badges.
- **Detail Navigation**: Tapping any employee navigates to `EmployeeDetailActivity` passing the `@Parcelize` model via Intent (using Android 13+ type-safe parcelable retrieval with backward-compatible fallback).
- **Lifecycle-Aware State Handling**: Emits states through `StateFlow` collected via `repeatOnLifecycle(Lifecycle.State.STARTED)` preventing background resource leaks.
- **Comprehensive UX States**: Includes full support for:
  - Loading states (centered progress bar)
  - Pull-to-refresh (`SwipeRefreshLayout`)
  - Error states with actionable retry button (`MaterialButton`)
  - Empty states
  - Dark Mode (Material 3 DayNight support)

---

## 🛠️ Architecture & Tech Stack

- **Language**: Kotlin 1.9.22
- **Architecture**: MVVM (Model - View - ViewModel) + Repository Pattern
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0 + Logging Interceptor
- **Asynchronous**: Kotlin Coroutines & `StateFlow`
- **Serialization**: Gson + `@Parcelize` Parcelable
- **UI & Layouts**:
  - `MaterialCardView`
  - `ConstraintLayout` & `CoordinatorLayout`
  - `RecyclerView` with `ListAdapter` & `DiffUtil`
  - `SwipeRefreshLayout`
  - ViewBinding
- **Target SDK**: 34 (Android 14) | **Min SDK**: 24 (Android 7.0)

---

## 📂 Project Structure

```
com.example.employeedetails/
├── model/
│   └── Employee.kt                     # Parcelable data model & response wrapper
├── network/
│   ├── ApiService.kt                   # Retrofit API interface
│   ├── RetrofitClient.kt               # OkHttp configuration & timeouts
│   └── NetworkUtils.kt                 # Connectivity & resilient JSON parsing
├── repository/
│   └── EmployeeRepository.kt           # Suspend repository wrapping Kotlin Result
├── viewmodel/
│   ├── UiState.kt                      # Sealed interface (Loading, Success, Error, Empty)
│   └── EmployeeViewModel.kt            # Coroutines viewModelScope & StateFlow
├── adapter/
│   └── EmployeeAdapter.kt              # ListAdapter + DiffUtil + dynamic avatar badges
└── ui/
    ├── MainActivity.kt                 # StateFlow collection, swipe refresh, navigation
    └── EmployeeDetailActivity.kt       # API 33+ backward-compatible Parcelable unpacking
```

---

## 🚀 Building & Running

### Using Gradle Wrapper:
```bash
# Build the debug APK
./gradlew assembleDebug

# Windows Command Prompt / PowerShell:
gradlew.bat assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

The debug APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🧪 Unit Testing

Unit tests are included under `app/src/test/` verifying:
- Root-array vs wrapped-object JSON deserialization resilience
- Empty/malformed payload fallbacks
- Model property transformations (annual package calculation, initial extraction)
