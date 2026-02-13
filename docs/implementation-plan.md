# Implementation Plan: StudyPlan

> Auto-generated checklist from project-context.json
>
> **Purpose:** Track homework and exam dates
>
> **Backend:** google_sheets

---

## 📋 How to Use This Plan

**This plan is a COMPLETENESS CHECKLIST**, not a specification document.

1. **For detailed specifications** (entity fields, feature descriptions, UI colors, etc.):
   → Read `project-context.json`

2. **For development sequence** (what to build and when):
   → Follow this implementation plan phase-by-phase

3. **For implementation patterns** (how to write the code):
   → Skill patterns are loaded into the system prompt automatically per phase

4. **Mark tasks as complete** (as you work):
   → Change `- [ ]` to `- [x]` after completing each task
   → This provides real-time progress tracking

**Goal:** Ensure ZERO empty folders, classes, or functions. Every checkbox must be complete!

---

## Prerequisites - MUST READ FIRST

**Before starting any phase, review these pattern guidelines:**

1. **Platform-Specific Code:** Use Interface + platformModule injection pattern (NOT expect object)
2. **External Data:** Support multiple input formats, handle missing data gracefully
3. **State Management:** Check existing state before showing input screens

See `CLAUDE.md` (Critical Code Rules section) for detailed patterns.

**For Google Sheets backend:** Follow `gsheet-skill` for multi-tab GID discovery and CSV validation patterns.

---

## Overview

This implementation plan outlines the development phases for StudyPlan.
Each phase lists its relevant skills — patterns are available in the system prompt.

**Total Phases:** 9
**Entities:** 4
**Screens:** 10

---

## Phase 1: Theme

**Skills:** `Skill(skill="theme-skill")`

**Description:** Update existing theme files with ui_design colors

**Tasks:**
**Update existing theme files (already in template):**
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/presentation/theme/AppColors.kt` - Update color values from ui_design
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/presentation/theme/AppTheme.kt` - Update MaterialTheme with new colors
- [ ] `composeApp/src/androidMain/kotlin/{{package}}/presentation/theme/AppTheme.android.kt` - Platform-specific (already exists)
- [ ] `composeApp/src/iosMain/kotlin/{{package}}/presentation/theme/AppTheme.ios.kt` - Platform-specific (already exists)
- [ ] Apply colors from project-context.json ui_design section
- [ ] Ensure dark color scheme is properly configured

---

## Phase 2: Domain Models

**Skills:** `Skill(skill="data-skill")`

**Description:** Create domain model data classes (specs from project-context.json)

**Tasks:**
**📖 Read entity specifications from `project-context.json → data_models`**

**Create Domain Models in `composeApp/src/commonMain/kotlin/{{package}}/domain/model/`:**
- [ ] `Assignment.kt` (6 fields - see context.json)
- [ ] `Exam.kt` (5 fields - see context.json)
- [ ] `Project.kt` (7 fields - see context.json)
- [ ] `AppConfig.kt` (3 fields - see context.json)

**Implementation Rules:**
  - Use `kotlin.time.Instant` for timestamps (NOT kotlinx.datetime)
  - Make all models `data class` with sensible defaults
  - Each model must have ALL fields from project-context.json

**✅ Completeness Check:**
  - Every entity in context.json has a .kt file
  - Each model has ALL required fields (no missing properties)
  - No empty data classes or TODOs

---

## Phase 3: Data Layer

**Skills:** `Skill(skill="data-skill")`, `Skill(skill="gsheet-skill")`, `Skill(skill="koin-di-skill")`, `Skill(skill="database-skill")`

**Description:** Create services, database, and repositories with state management

**Tasks:**
**Core Infrastructure:**
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/core/network/ApiResult.kt` - Sealed class with Success<T>/Error variants (used by all repositories)

**Google Sheets Infrastructure (gsheet-skill patterns):**

*Configuration (read `backend_config.config` for mode):*
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/core/config/SheetsApiConfig.kt` - script_url, spreadsheet_url from backend_config
  - If `config_type == "hardcoded"`: Use `object` with `const val`
  - If `config_type == "configurable"`: Use `class` with AppSettings injection

*Service & Models:*
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/data/remote/model/SheetsApiModels.kt` - SheetsRequest, SheetsResponse, InsertResponse, SchemaResponse
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/data/remote/service/SheetsApiService.kt` - 7 actions: PING, GET, GET_BY_ID, INSERT, UPDATE, DELETE, GET_SCHEMA
  - Support both edit URLs and published URLs (pubhtml)
  - Implement multi-format date parsing (yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy)
  - Add CSV response validation (check for HTML error pages)
  - Handle missing tabs gracefully (return empty list, not crash)
**Create Room Database (database-skill patterns):**

*Database Constructor (REQUIRED for Room KMP):*
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/core/database/AppDatabaseConstructor.kt` - `expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>` with `@Suppress("NO_ACTUAL_FOR_EXPECT")`

*Database Class:*
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/core/database/AppDatabase.kt` - Room @Database with `@ConstructedBy(AppDatabaseConstructor::class)`

*Database Module (expect/actual for platform-specific Room builder):*
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/di/DatabaseModule.kt` - `expect fun appDatabaseModule(): Module`
- [ ] `composeApp/src/androidMain/kotlin/{{package}}/di/DatabaseModule.android.kt` - Room.databaseBuilder with `setDriver(BundledSQLiteDriver())` + `setQueryCoroutineContext(Dispatchers.IO)`
- [ ] `composeApp/src/iosMain/kotlin/{{package}}/di/DatabaseModule.ios.kt` - Room.databaseBuilder with NSDocumentDirectory path + `setDriver(BundledSQLiteDriver())` + `setQueryCoroutineContext(Dispatchers.IO)`

*Entities and DAOs:*
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/local/entity/AssignmentEntity.kt` - Room @Entity
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/local/dao/AssignmentDao.kt` - Room @Dao with CRUD
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/local/entity/ExamEntity.kt` - Room @Entity
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/local/dao/ExamDao.kt` - Room @Dao with CRUD
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/local/entity/ProjectEntity.kt` - Room @Entity
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/local/dao/ProjectDao.kt` - Room @Dao with CRUD
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/local/entity/AppConfigEntity.kt` - Room @Entity
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/local/dao/AppConfigDao.kt` - Room @Dao with CRUD
  - Store timestamps as Long (not Instant) in entities
  - Add entity mappers: `{Entity}Entity.toDomain()` and `{Entity}.toEntity()`
**Create Repositories (with STATE MANAGEMENT):**
Both interface and implementation go in the SAME folder: `data/repositories/{feature}/`

**CRITICAL: Interface vs Implementation are SEPARATE files/classes:**
  - `{Name}Repository.kt` contains ONLY the `interface {Name}Repository { ... }`
  - `{Name}RepositoryImpl.kt` contains ONLY the `class {Name}RepositoryImpl(...) : {Name}Repository { ... }`
  - NEVER put the implementation class inside the interface file
  - NEVER define the same class in both files (causes Redeclaration error)

- [ ] `composeApp/src/commonMain/kotlin/{package}/data/repositories/appconfig/AppConfigRepository.kt` (interface ONLY)
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/repositories/appconfig/AppConfigRepositoryImpl.kt` (implementation ONLY)
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/repositories/assignment/AssignmentRepository.kt` (interface ONLY)
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/repositories/assignment/AssignmentRepositoryImpl.kt` (implementation ONLY)
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/repositories/exam/ExamRepository.kt` (interface ONLY)
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/repositories/exam/ExamRepositoryImpl.kt` (implementation ONLY)
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/repositories/project/ProjectRepository.kt` (interface ONLY)
- [ ] `composeApp/src/commonMain/kotlin/{package}/data/repositories/project/ProjectRepositoryImpl.kt` (implementation ONLY)

**Google Sheets Repository Pattern (NO Room/DAO methods):**
  - Constructor: `class XxxRepositoryImpl(private val sheetsApi: SheetsApiService) : XxxRepository`
  - Fetch: `sheetsApi.getAll("SheetName")` returns `Result<List<Map<String, String>>>`
  - Parse: define `Map<String, String>.toXxx(): Xxx` mapper in each RepositoryImpl
  - Do NOT use `observeAll()`, `getAll()`, `replaceAll()` — those are Room DAO methods
  - Do NOT reference `GoogleSheetsRepository` — use `SheetsApiService` directly
  - StateFlow pattern: same as data-skill (MutableStateFlow in Impl, StateFlow in interface)
  - Each repository holds `MutableStateFlow` for its data
  - Exposes `StateFlow` to ViewModels (read-only)
  - Implements offline-first: cache on fetch, serve from cache when offline
  - Do NOT create a separate `domain/repository/` folder

---

## Phase 4: Use Cases

**Skills:** `Skill(skill="data-skill")`

**Description:** Create use case classes for business logic

**Tasks:**
**Create Use Cases in `composeApp/src/commonMain/kotlin/{{package}}/domain/usecase/`:**
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/GetAssignmentListUseCase.kt` (feature: Assignment Tracking)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/MarkAssignmentCompleteUseCase.kt` (feature: Assignment Tracking)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/ViewAssignmentDetailsUseCase.kt` (feature: Assignment Tracking)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/GetExamListUseCase.kt` (feature: Exam Tracking)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/ViewExamDetailsUseCase.kt` (feature: Exam Tracking)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/GetProjectListUseCase.kt` (feature: Project Tracking)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/ViewProjectDetailsUseCase.kt` (feature: Project Tracking)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/GetDashboardOverviewUseCase.kt` (feature: Dashboard Overview)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/GetUpcomingItemsUseCase.kt` (feature: Dashboard Overview)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/UpdateGoogleSheetUrlUseCase.kt` (feature: Google Sheets Configuration)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/ValidateSheetUrlUseCase.kt` (feature: Google Sheets Configuration)
- [ ] `composeApp/src/commonMain/kotlin/{package}/domain/usecase/GetCurrentSheetConfigUseCase.kt` (feature: Google Sheets Configuration)

**Implementation Rules:**
  - Each UseCase: single `operator fun invoke()` or `suspend operator fun invoke()`
  - UseCase calls Repository, applies business logic, returns result
  - Keep UseCases focused - one responsibility each

**CRITICAL: Import paths must match repository locations:**
  - `import {package}.data.repositories.{feature}.{Name}Repository`
  - NOT `import {package}.repository.{Name}Repository` (this path does not exist)
  - NOT `import {package}.domain.repository.{Name}Repository` (this path does not exist)

**UseCase Sample:**
```kotlin
import {package}.data.repositories.assignment.AssignmentRepository

class GetAssignmentListUseCase(
    private val repository: AssignmentRepository  // constructor injection
) {
    operator fun invoke(): StateFlow<List<Assignment>> = repository.assignments
}
```

---

## Phase 5: ViewModels

**Skills:** `Skill(skill="feature-orchestration-skill")`, `Skill(skill="coroutine-flow-skill")`

**Description:** Create THIN ViewModels that observe repository state

**Tasks:**
**Create ViewModels (feature-based organization):**
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/assignmentdetail/AssignmentDetailViewModel.kt` with `AssignmentDetailUiState` sealed interface
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/assignmentlist/AssignmentListViewModel.kt` with `AssignmentListUiState` sealed interface
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/dashboard/DashboardViewModel.kt` with `DashboardUiState` sealed interface
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/examdetail/ExamDetailViewModel.kt` with `ExamDetailUiState` sealed interface
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/examlist/ExamListViewModel.kt` with `ExamListUiState` sealed interface
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/projectdetail/ProjectDetailViewModel.kt` with `ProjectDetailUiState` sealed interface
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/projectlist/ProjectListViewModel.kt` with `ProjectListUiState` sealed interface
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/settings/SettingsViewModel.kt` with `SettingsUiState` sealed interface
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/sheeturlconfig/SheetUrlConfigViewModel.kt` with `SheetUrlConfigUiState` sealed interface

**THIN ViewModel Sample (FOLLOW THIS PATTERN):**
```kotlin
sealed interface EventsUiState {
    data object Loading : EventsUiState
    data class Success(val items: List<Event>) : EventsUiState
    data class Error(val message: String) : EventsUiState
}

class EventsViewModel(
    private val repository: EventRepository
) : ViewModel() {

    val uiState: StateFlow<EventsUiState> = combine(
        repository.events,
        repository.isLoading,
        repository.error
    ) { events, isLoading, error ->
        when {
            isLoading -> EventsUiState.Loading
            error != null -> EventsUiState.Error(error)
            else -> EventsUiState.Success(events)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EventsUiState.Loading)

    fun loadEvents() { viewModelScope.launch { repository.loadEvents() } }
    fun deleteEvent(id: String) { viewModelScope.launch { repository.deleteEvent(id) } }
}
```

**Key Rules:**
  - OBSERVE repository StateFlow with `combine()` → transform to UiState
  - Do NOT use `private val _uiState = MutableStateFlow(...)` — that copies state
  - Use `combine(repo.data, repo.isLoading, repo.error).stateIn()` instead
  - ViewModel actions just delegate to repository (one-liners)
  - Action methods MUST be `fun` (public) — NOT `private fun` (screens call these)
  - UiState sealed interface defined in SAME file as ViewModel
  - Use `koinViewModel()` default parameter in Screen composables
  - Import repositories from `data.repositories.{feature}` package

---

## Phase 6: Screens

**Skills:** `Skill(skill="ui-skill")`

**Description:** Create screen composables with proper state handling

**Tasks:**
**Create Shared UI Components FIRST (ui-skill patterns):**
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/presentation/components/LoadingIndicator.kt` — Centered `CircularProgressIndicator`
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/presentation/components/ErrorMessage.kt` — Error text + Retry button
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/presentation/components/EmptyState.kt` — Icon + message + optional action button for empty lists

**Create Screens (feature-based organization):**
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/assignmentdetail/AssignmentDetailScreen.kt` using `AssignmentDetailViewModel`
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/assignmentlist/AssignmentListScreen.kt` using `AssignmentListViewModel`
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/dashboard/DashboardScreen.kt` using `DashboardViewModel`
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/examdetail/ExamDetailScreen.kt` using `ExamDetailViewModel`
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/examlist/ExamListScreen.kt` using `ExamListViewModel`
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/projectdetail/ProjectDetailScreen.kt` using `ProjectDetailViewModel`
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/projectlist/ProjectListScreen.kt` using `ProjectListViewModel`
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/settings/SettingsScreen.kt` using `SettingsViewModel`
- [ ] `composeApp/src/commonMain/kotlin/{package}/presentation/sheeturlconfig/SheetUrlConfigScreen.kt` using `SheetUrlConfigViewModel`

**IMPORTANT: Do NOT create separate UiState files — UiState is defined in the ViewModel file.**

**Screen Pattern:**
  - `@Composable fun XxxScreen(viewModel: XxxViewModel = koinViewModel())`
  - Collect state: `val uiState by viewModel.uiState.collectAsState()`
  - Handle Loading/Success/Error states with `when (val state = uiState)` (smart cast)
  - Use `koinViewModel()` as DEFAULT parameter (never pass null)
  - Use `LoadingIndicator()` and `ErrorMessage()` from shared components

**CRITICAL: Screen callback parameter names must MATCH NavigationHost:**
  - If NavigationHost passes `onAssignmentClick = { ... }`, Screen must accept `onAssignmentClick: (String) -> Unit`
  - Use consistent naming: `on{Feature}Click` for list item clicks, `onNavigateBack` for back navigation
  - Do NOT use different names (e.g., `onNavigateToDetail` in NavigationHost vs `onItemClick` in Screen)
**Bottom Navigation Items:** Dashboard, Assignments, Exams, Projects

**Create Splash Screen:**
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/presentation/splash/SplashScreen.kt` - Full-screen branding display
  - Branding text: "MyTask"
  - Show app logo/icon centered with branding text below
  - Use primary color as background
  - Auto-navigate to start screen after 1.5-2 seconds
  - Pattern:
    ```kotlin
    @Composable
    fun SplashScreen(onSplashComplete: () -> Unit) {
        LaunchedEffect(Unit) {
            delay(1500)
            onSplashComplete()
        }
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) {
            Column(horizontalAlignment = CenterHorizontally, modifier = Modifier.align(Center)) {
                // App icon or logo
                Text(text = "MyTask", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
    ```

---

## Phase 7: Navigation

**Skills:** `Skill(skill="ui-skill")`

**Description:** Set up type-safe navigation with @Serializable routes

**Tasks:**
**Create Navigation in `composeApp/src/commonMain/kotlin/{{package}}/navigation/`:**
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/navigation/NavRoutes.kt` - @Serializable route classes (NOT string routes)
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/navigation/NavigationHost.kt` - NavHost with composable<Route> entries
  - Start destination: `Splash`

**CRITICAL: Create ONLY these 2 navigation files. Do NOT create:**
  - `presentation/navigation/NavigationGraph.kt` (causes route Redeclaration)
  - Any other file that defines @Serializable route objects
  - All routes go in NavRoutes.kt, all composable<> entries go in NavigationHost.kt

**Required imports for NavigationHost.kt:**
  - `import androidx.compose.runtime.Composable`
  - `import androidx.navigation.compose.NavHost`
  - `import androidx.navigation.compose.composable`
  - `import androidx.navigation.compose.rememberNavController`
  - `import androidx.navigation.toRoute`  (for parameterized routes)

**Required imports for NavRoutes.kt:**
  - `import kotlinx.serialization.Serializable`

**NavigationHost must be @Composable:**
  - `@Composable fun NavigationHost() { ... }`
  - The function MUST have the @Composable annotation

**Splash Screen Navigation:**
- [ ] Add `@Serializable object Splash` route
- [ ] Splash navigates to `ConfigScreen` after delay
- [ ] Use `navController.navigate(route) {{ popUpTo(Splash) {{ inclusive = true }} }}` to remove splash from backstack
**Navigation Flows:**
  - ConfigScreen -> DashboardScreen (after successful configuration)
  - DashboardScreen -> AssignmentListScreen (tap assignments section)
  - DashboardScreen -> ExamListScreen (tap exams section)
  - DashboardScreen -> ProjectListScreen (tap projects section)
  - AssignmentListScreen -> AssignmentDetailScreen (tap assignment item)
  - ... (+5 more flows)
**Bottom Navigation:** Dashboard, Assignments, Exams, Projects
Create bottom nav bar with proper selected state
**Type-Safe Navigation:**
  - `@Serializable object Home` for simple routes
  - `@Serializable data class Detail(val id: String)` for parameterized routes
  - Navigate: `navController.navigate(Detail(id = "123"))`
For setup screens: check existing state in ViewModel init (skip if data exists)

---

## Phase 8: Dependency Injection

**Skills:** `Skill(skill="koin-di-skill")`

**Description:** Register all classes in Koin (now that they all exist)

**Tasks:**
**Create Koin Modules in `composeApp/src/commonMain/kotlin/{{package}}/di/`:**
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/di/AppModule.kt` - main module with all registrations
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/di/PlatformModule.kt` - expect fun platformModule(): Module
- [ ] `composeApp/src/androidMain/kotlin/{{package}}/di/PlatformModule.android.kt` - actual fun platformModule(): Module
- [ ] `composeApp/src/iosMain/kotlin/{{package}}/di/PlatformModule.ios.kt` - actual fun platformModule(): Module

**AppSettings (key-value storage for user preferences):**
- [ ] `composeApp/src/commonMain/kotlin/{{package}}/core/settings/AppSettings.kt` - Interface with getString/putString/getBoolean/putBoolean/remove
- [ ] `composeApp/src/androidMain/kotlin/{{package}}/core/settings/AndroidAppSettings.kt` - SharedPreferences implementation
- [ ] `composeApp/src/iosMain/kotlin/{{package}}/core/settings/IosAppSettings.kt` - NSUserDefaults implementation
  - Register in platformModule: `single<AppSettings> { AndroidAppSettings(get()) }` / `single<AppSettings> { IosAppSettings() }`

**Register ALL classes created in previous phases:**

**Google Sheets Service:**
  - `single { SheetsApiService(get(), get()) }`

**Room Database & DAOs:**
  - Include `appDatabaseModule()` in Koin startup: `modules(listOf(appModule, appDatabaseModule(), platformModule()))`
  - `single { get<AppDatabase>().xxxDao() }` for each DAO
  - DAOs MUST be registered before Repositories that depend on them

*Repositories (use `singleOf` with `bind` for interface binding):*
  - `singleOf(::AppConfigRepositoryImpl) { bind<AppConfigRepository>() }`
  - `singleOf(::AssignmentRepositoryImpl) { bind<AssignmentRepository>() }`
  - `singleOf(::ExamRepositoryImpl) { bind<ExamRepository>() }`
  - `singleOf(::ProjectRepositoryImpl) { bind<ProjectRepository>() }`

**CRITICAL: Import repositories from correct package:**
  - `import {{package}}.data.repositories.{feature}.{Name}Repository`
  - `import {{package}}.data.repositories.{feature}.{Name}RepositoryImpl`
  - NOT from `{{package}}.repository` or `{{package}}.domain.repository`

*UseCases:*
  - `factoryOf(::GetAssignmentListUseCase)`
  - `factoryOf(::MarkAssignmentCompleteUseCase)`
  - `factoryOf(::ViewAssignmentDetailsUseCase)`
  - `factoryOf(::GetExamListUseCase)`
  - `factoryOf(::ViewExamDetailsUseCase)`
  - `factoryOf(::GetProjectListUseCase)`
  - `factoryOf(::ViewProjectDetailsUseCase)`
  - `factoryOf(::GetDashboardOverviewUseCase)`
  - `factoryOf(::GetUpcomingItemsUseCase)`
  - `factoryOf(::UpdateGoogleSheetUrlUseCase)`
  - `factoryOf(::ValidateSheetUrlUseCase)`
  - `factoryOf(::GetCurrentSheetConfigUseCase)`

*ViewModels:*
  - `viewModelOf(::AssignmentDetailViewModel)`
  - `viewModelOf(::AssignmentListViewModel)`
  - `viewModelOf(::DashboardViewModel)`
  - `viewModelOf(::ExamDetailViewModel)`
  - `viewModelOf(::ExamListViewModel)`
  - `viewModelOf(::ProjectDetailViewModel)`
  - `viewModelOf(::ProjectListViewModel)`
  - `viewModelOf(::SettingsViewModel)`
  - `viewModelOf(::SheetUrlConfigViewModel)`

**Platform-Specific Dependencies:**
  - Use Interface + platformModule injection (NOT expect object)
  - Example: `interface DateFormatter` → `AndroidDateFormatter` / `IosDateFormatter`

**CRITICAL: Update App.kt to wire everything together:**
- [ ] Update `composeApp/src/commonMain/kotlin/{{package}}/App.kt`:
  - Replace `modules(listOf())` with `modules(listOf(appModule, platformModule()))`
  - Replace `// TBD: set content here` with `NavigationHost()` call
  - Add imports for `appModule`, `platformModule`, and `NavigationHost`

---

## Phase 9: Review & Fix

**Skills:** `Skill(skill="validation-skill")`

**Description:** Review all phases and fix potential issues before GitHub Actions build

**Tasks:**
**Review Each Phase for Common Issues:**

*Theme:*
  - Colors match ui_design specifications
  - Dark mode properly toggles if enabled

*Domain Models:*
  - Using `kotlin.time.Instant` (NOT kotlinx.datetime)
  - All fields have sensible defaults

*Data Layer:*
  - Repositories hold StateFlow (state management)
  - Timestamps stored as Long in Room entities
  - Error handling returns empty list, not crash

*ViewModels:*
  - THIN pattern - observe repository, no business logic
  - UiState sealed interface with Loading/Success/Error

*Screens:*
  - `koinViewModel()` as default parameter
  - Handles all UiState branches

*Navigation:*
  - @Serializable route classes (not strings)
  - Icons use `Icons.AutoMirrored.Filled` for arrows/lists

*DI:*
  - ALL ViewModels registered with `viewModelOf()`
  - ALL Repositories registered with `singleOf()`

**Final Tasks:**
  - Update README.md with app name, description, features from project-context.json
  - Remove any placeholder comments
  - Ensure no empty folders remain

---

## Implementation Notes

### Skills Reference
Skill patterns are loaded into the system prompt automatically. Each phase lists its relevant skills.

### Key Patterns

- **THIN ViewModels**: ViewModels should only observe repository state, not hold business logic
- **Repository State Management**: Shared state lives in repositories, not ViewModels
- **Type-Safe Navigation**: Use sealed classes for navigation routes
- **Flow Patterns**: Use StateFlow for state, Flow for one-shot operations

### Files Reference

- `docs/project-context.json` - Complete project specifications
- `docs/implementation-plan.md` - This file

---

## 🎯 Completeness Validation (NO EMPTY IMPLEMENTATIONS!)

Before marking the implementation as complete, verify:

### ✅ File-Level Completeness
- [ ] **NO empty folders** - Every folder has at least one file
- [ ] **NO empty files** - Every .kt file has implementation code
- [ ] **NO placeholder comments** - Remove all `// TODO`, `// Add implementation here`

### ✅ Class-Level Completeness
- [ ] Every `data class` has ALL fields from project-context.json (no missing properties)
- [ ] Every repository has ALL CRUD methods fully implemented (not just stubs)
- [ ] Every ViewModel has:
  - UiState sealed interface with Loading, Success, Error states
  - StateFlow exposure
  - Intent/action handlers fully implemented
- [ ] Every UseCase has complete `invoke()` implementation (not just `TODO()`)

### ✅ Function-Level Completeness
- [ ] NO functions with just `TODO()` or empty body
- [ ] All repository methods return actual data (not `emptyList()` stubs)
- [ ] All navigation functions properly navigate (not placeholder code)
- [ ] All error handlers have proper error messages

### ✅ Screen-Level Completeness
- [ ] Every screen from project-context.json has a corresponding .kt file
- [ ] Each screen has a complete Compose UI (not just `Text("TODO")`)
- [ ] Each screen properly observes ViewModel state
- [ ] Each screen has proper error/loading states

### ✅ Integration Completeness
- [ ] All ViewModels registered in Koin DI
- [ ] All Repositories registered in Koin DI
- [ ] All navigation routes defined
- [ ] All bottom nav items (if specified) present

**CRITICAL**: The goal is a COMPLETE, BUILD-PASSING application with ZERO empty implementations!

---

*Generated by Mismaiti Backend*
