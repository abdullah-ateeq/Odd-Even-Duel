# 🎲 Odd Even Duel

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-purple.svg)](https://m3.material.io/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-37-red.svg)](https://developer.android.com)

**Odd Even Duel** is a fast-paced, strategic number parity puzzle game built for Android using modern **Jetpack Compose** and **Material 3**. Players go head-to-head against a smart CPU opponent on a 6x6 grid of numbers, racing to claim cells and score points by correctly matching active parity challenges (**ODD** or **EVEN**).

---

## 🌟 Key Features

* **🎮 Strategic Parity Gameplay**:
  * 6x6 grid filled with dynamic numbers (1 to 99).
  * Dynamic challenge generator that alternates and swaps target parities (**ODD** / **EVEN**).
  * Score system rewarding higher cell values (`10 + value / 10`) alongside total claimed territory.

* **🤖 Smart CPU AI**:
  * **3 Difficulty Levels**: Easy, Normal, and Hard.
  * AI evaluates board tile values, picks strategic targets, and exhibits realistic human-like mistake rates based on chosen difficulty.

* **⏱️ Configurable Turn Timers**:
  * Choose between **Off**, **5 Seconds**, **10 Seconds**, or **15 Seconds** per turn for quick-fire decisions.

* **📊 Detailed Statistics**:
  * Tracks lifetime games played, total wins, win streaks, accuracy percentages, highest scores, and maximum claimed cells.

* **🎨 Material 3 & Customization**:
  * Support for **Dynamic Color** (Material You) on Android 12+.
  * Light, Dark, and System Default themes.
  * Interactive coordinate labels (A1..F6).
  * Smooth tile animations (scale, claim popups, and shake effects on invalid picks).

* **🔊 Audio & Tactile Feedback**:
  * Integrated sound effects and haptic vibration feedback for claims, misses, and turn switches.
  * Educational math feedback explanations (e.g., `17 IS ODD`).

* **📱 Adaptive UI**:
  * Fully responsive layouts optimized for both **Portrait** and **Landscape** orientations.

---

## 🕹️ How to Play

1. **Observe the Challenge**: Look at the current parity challenge (e.g., **"CLAIM AN ODD NUMBER"** or **"CLAIM AN EVEN NUMBER"**).
2. **Select a Tile**: Tap any active number on the 6x6 grid that matches the current challenge.
   * **Correct Match**: You claim the cell, score points based on tile value, and pass the turn to the CPU.
   * **Incorrect Match**: The tile shakes, feedback is displayed, and the turn passes to the CPU.
3. **Outsmart the CPU**: Adapt your strategy to secure high-value tiles and prevent the CPU from taking the lead.
4. **Win the Duel**: The game ends when all 36 tiles are claimed. The player with the highest total score wins!

---

## 🛠️ Tech Stack & Architecture

Odd Even Duel is built following modern Android development best practices and Clean Architecture principles:

* **UI Layer**: 100% [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 components and declarative Navigation.
* **Architecture**: MVVM (Model-View-ViewModel) + Clean Domain Logic using Kotlin `StateFlow` and `Coroutines`.
* **Persistence**: Asynchronous preferences storage for game settings and lifetime statistics.
* **Domain Logic**: Decoupled game engine (`OddEvenGameEngine`), board generator (`OddEvenBoardGenerator`), CPU AI strategy (`OddEvenCpuStrategy`), and parity rules (`ParityEngine`).
* **Unit Testing**: 100% unit test coverage for domain models, game rules, scoring logic, and CPU decision-making (`JUnit4`).

---

## 📁 Project Structure

```
app/src/main/java/com/example/oddevenduel/
├── data/
│   └── PreferencesManager.kt          # Settings & statistics persistence
├── domain/
│   ├── logic/
│   │   ├── OddEvenBoardGenerator.kt   # Grid generation logic
│   │   ├── OddEvenChallengeGenerator.kt # Challenge rotation engine
│   │   ├── OddEvenCpuStrategy.kt      # CPU AI decision logic
│   │   ├── OddEvenGameEngine.kt       # Turn processing & game rules
│   │   └── ParityEngine.kt           # Number parity & scoring logic
│   └── model/                         # State & data models
├── ui/
│   ├── components/                    # Reusable Compose components
│   ├── navigation/                    # Navigation flow & graph
│   ├── screens/                       # Home, Game, Settings, Result, About, Splash
│   ├── theme/                         # Material 3 colors, typography & theme setup
│   └── OddEvenViewModel.kt            # Central UI state management
└── util/
    ├── HapticsManager.kt             # Haptic feedback system
    └── SoundManager.kt               # Sound effects manager
```

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio**: Ladybug (2024.2.1) or newer recommended.
* **JDK**: Version 11 or higher.
* **Android SDK**: Min SDK 24 (Android 7.0), Compile SDK 37.

### Building & Running

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/Odd-Even-Duel.git
   cd Odd-Even-Duel
   ```

2. **Open in Android Studio**:
   Open Android Studio and select **Open**, then navigate to the cloned project folder.

3. **Build the Project**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

5. **Deploy to Device/Emulator**:
   Select an emulator or connected physical device and click **Run** (`Shift + F10`).

---

## 🧪 Testing

The core domain logic is backed by a comprehensive unit test suite:

```bash
./gradlew testDebugUnitTest
```

Key test suites:
* `OddEvenGameEngineTest`: Validates full game lifecycle, turns, correct/incorrect selections, timeouts, and end conditions.
* `OddEvenCpuStrategyTest`: Verifies AI move selection across Easy, Normal, and Hard difficulties.
* `OddEvenBoardGeneratorTest`: Tests 6x6 grid generation and parity balance.
* `ParityEngineTest`: Ensures accurate scoring calculations and odd/even matching.
* `DuelStatisticsAndScoringTest`: Verifies lifetime stats aggregation.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) - see the file for details.
