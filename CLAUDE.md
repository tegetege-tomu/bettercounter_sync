# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BetterCounter is an Android habit/counter tracking app (package `org.kde.bettercounter`, min SDK 21, target SDK 36). It records timestamped events for each counter and displays statistics and charts. Available on F-Droid and Play Store.

## Build & Test Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run all unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "org.kde.bettercounter.extensions.CalendarExtensionTest"

# Run a single test method (use the method name with backtick syntax stripped)
./gradlew test --tests "org.kde.bettercounter.persistence.IntervalTest.someMethodName"

# Lint
./gradlew lint
```

Tests are JVM unit tests (no Android instrumentation tests exist). JDK 21 (Temurin) is required, configured in `gradle.properties`.

## Architecture

**Persistence layer** (`persistence/`):
- `Entry` — Room entity: a single counter event with `name` (String) and `date` (Date). The database (`AppDatabase`, version 3) has only this one table.
- `EntryDao` — Room DAO for querying/inserting entries.
- `Repository` — The data access facade. Counter *metadata* (interval, color, goal) lives in `SharedPreferences`; counter *events* live in Room. Counter ordering is stored as a JSON list in prefs under key `"counters"`.
- `CounterSummary` — A computed snapshot of a counter: current-interval count, total count, most/least recent date, color, goal. This is what the UI observes.
- `Exporter` — CSV-like format: one line per counter, `name,timestamp1,timestamp2,...`. Supports auto-export on each save.
- `Interval` enum — HOUR, DAY, WEEK, MONTH, YEAR, LIFETIME. LIFETIME is the default; it has no ChronoUnit equivalent and charts display it year-by-year.

**ViewModel** (`ui/main/MainActivityViewModel`):
- Not a `androidx.lifecycle.ViewModel` — it's a plain class instantiated lazily in `MainActivity`. Holds a `HashMap<String, MutableStateFlow<CounterSummary>>` for live counter data.
- All DB operations run on `Dispatchers.IO`. Summaries are refreshed automatically every hour and after each mutation.
- Counter mutations (increment, decrement, reset, delete) update the flow, refresh app widgets, and trigger auto-export.
- Import logic lives here as a companion `parseImportLine` (supports counter names containing commas via heuristic: timestamps are ≥ 12 digits).

**UI** (`ui/`):
- `MainActivity` — single-activity app. A `RecyclerView` lists counters (`EntryListViewAdapter`). Tapping a counter expands a bottom sheet with a chart pager (`ChartsAdapter`).
- `ChartsAdapter` / `ChartHolder` — horizontal pager of bar charts (one page per time period). Uses MPAndroidChart.
- `ChartDataAggregation` — pure logic for bucketing entries into chart bars.
- `CounterSettingsDialogBuilder` — dialog for creating/editing a counter (name, interval, color, goal).
- `WidgetProvider` / `WidgetViewModel` — home-screen widget support; receives broadcast `ACTION_REFRESH_COUNTER` from `MainActivity`.
- `SettingsActivity` / `SettingsViewModel` — app-level settings (average mode, auto-export).

**Extensions** (`extensions/`): Calendar/Date/ChronoUnit utilities used throughout. The `truncated(interval)` and `plusInterval()` functions on `Calendar` are central to time-bucketing logic. Tests in `app/src/test/` cover these extensions and chart aggregation.

## Key Conventions

- Counter identity is its **name string** (mutable via rename). All prefs keys and DB rows reference the name directly — rename updates both.
- `CounterSummary.lastIntervalCount` reflects counts in the *current* interval window only (not all time). `totalCount` is the lifetime total.
- The export/import format is plain text CSV; the importer reads everything into memory before touching the DB to avoid partial writes.
- ViewBinding is used throughout (no findViewById). Layout files follow `activity_*` / `fragment_*` / `item_*` naming.
- Room schema migrations are in `app/schemas/`.
