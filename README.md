![Android CI](https://github.com/OskarGosk/BoardGame/actions/workflows/android-ci.yml/badge.svg)

# 🎲 BoardGame Tracker

> 🚧 **Work in progress:** the project has just been through a major UI redesign and refactor (new UI designed with Google Stitch, implemented with Claude Code).
> A few screens and cleanup items are still open – see [Remaining work](#remaining-work).

The application is designed to **record and analyze board game sessions**.<br/><br/>

With this app you can:<br/>
- **Create a player list** – each player has their total number of games played, win rate and rank.<br/>
- **Create a board game collection** – games can be added manually (name, number of players, category, year, co-op, cover photo) or imported from the **BoardGameGeek (BGG)** database.<br/>
- **Log gameplay sessions** – game, date, duration, participating players, the winner and optional notes.<br/>
- **Browse game history** – sessions grouped by time (this week / earlier) with search, plus a details screen for every session.<br/>
- **Track your own stats** – pick "who you are" and see total games, most played game and win ratio on the home screen.<br/><br/>

The application works in two modes:<br/>
- **Guest** – data is stored locally.<br/>
- **Logged-in user** – data is synchronized with the **Firebase Database**, allowing saving and retrieving across devices.<br/>
<br/>

A demo account is available on request, or you can try the app right away in **Guest** mode.<br/><br/>

| Before | After |
|:---:|:---:|
| <img src="screenshots/login_screen.png" alt="Before" width="240" /> | <img src="screenshots/redesign/01_login.jpg" alt="After" width="150" /> |

<br/>

## ✨ New look (UI redesign)

> 🤖 **The new look of the app was designed and implemented in collaboration with AI.**
> The visual design (screens, layout and style) was created in [Google Stitch](https://stitch.withgoogle.com), and the implementation in Jetpack Compose – components and the screen-by-screen refactor – was done together with [Claude Code](https://claude.com/claude-code). All changes were reviewed, tested and shipped by the author.
> All screenshots below were taken from a real emulator running the current build (test account).

**Before → after.** The old app was a menu of text buttons that opened separate lists. The redesign turns it into a dashboard with bottom navigation:

| Before | After |
|:---:|:---:|
| <img src="screenshots/home_screen_light.png" alt="Old home" width="240" /> | <img src="screenshots/redesign/02_home_light.jpg" alt="New home" width="150" /> |

**What changed:**
- 🧭 **New navigation** – bottom bar (*Home · Collection · Add Session · Players*) instead of a button menu; a profile avatar in the top bar opens the profile screen.
- 🏠 **Dashboard home screen** – greeting, "Who are you?" picker, total games / most played / win-ratio tiles, quick actions and recent sessions.
- 🎨 **Design system** – new warm Material 3 colour palette, typography, cards, chips and shared components in a dedicated `components` / `theme` package.
- 🌗 **Light and dark theme** across every screen.
- 🖼 **Cover art everywhere** – collection grid, history and recent sessions use game covers.
- 📝 **Redesigned forms** – log session, add game (BGG search / manual entry), add player.
- 🔔 **One feedback mechanism** – themed snackbars instead of toasts (see the notes below).
- ✅ **More tests** – new ViewModel and use-case tests for the redesigned screens (Home, Add Gameplay, Profile and others).

**Still in progress:** the *Quick Report* tile on the home screen is not wired up yet, and the Reports screen has not been redesigned.

<br/>

## 🏠 Home

Dashboard with your stats, quick actions and the three most recent sessions. Choose which player represents you on first launch.<br/><br/>

| Before | After |
|:---:|:---:|
| <img src="screenshots/home_screen_light.png" alt="Before" width="240" /><br/><img src="screenshots/home_screen_dark.png" alt="Before" width="240" /> | <img src="screenshots/redesign/02_home_light.jpg" alt="After" width="150" /> <img src="screenshots/redesign/03_home_recent_sessions.jpg" alt="After" width="150" /> <img src="screenshots/redesign/15_home_dark.jpg" alt="After" width="150" /> |

"Who are you?" picker (new):<br/>

| New |
|:---:|
| <img src="screenshots/redesign/02a_home_who_are_you.jpg" alt="After" width="150" /> |

<br/>

## 🎲 Collection

Games shown as a cover grid with a play counter. Filter by *All / Base / Expansions*, search by name and sort (default, name, number of plays). Tap a card to flip it – you get min/max players, total plays and quick actions: **log a session**, **edit** or **delete**.<br/><br/>

| Before | After |
|:---:|:---:|
| <img src="screenshots/game_list_light.png" alt="Before" width="240" /><br/><img src="screenshots/game_list_dark.png" alt="Before" width="240" /> | <img src="screenshots/redesign/04_collection_grid.jpg" alt="After" width="150" /> <img src="screenshots/redesign/06_collection_card_flip.jpg" alt="After" width="150" /> <img src="screenshots/redesign/05_collection_sort_menu.jpg" alt="After" width="150" /> <img src="screenshots/redesign/16_collection_dark.jpg" alt="After" width="150" /> |

<br/>

## ➕ Add Game

Search the **BGG database** or add a game **manually** (cover from camera/gallery, players range, category, year, co-op switch).
Games imported from BGG have a details screen with rating, weight, play time, categories, mechanics and designers.<br/><br/>

| Before | After |
|:---:|:---:|
| <img src="screenshots/add_game_from_bgg.png" alt="Before" width="240" /><br/><img src="screenshots/add_game_manually.png" alt="Before" width="240" /> | <img src="screenshots/redesign/07_add_game_bgg_search.jpg" alt="After" width="150" /> <img src="screenshots/redesign/08_game_details_bgg.jpg" alt="After" width="150" /> <img src="screenshots/redesign/09_add_game_manual.jpg" alt="After" width="150" /> |

<br/>

## 📝 Log a Gameplay Session

Pick the game, date, duration and participants, choose the winner (or play co-op) and add notes. New players can be added on the fly.<br/><br/>

| Before | After |
|:---:|:---:|
| <img src="screenshots/gameplay_light.png" alt="Before" width="240" /><br/><img src="screenshots/gameplay_dark.png" alt="Before" width="240" /> | <img src="screenshots/redesign/10_add_session.jpg" alt="After" width="150" /> <img src="screenshots/redesign/11_add_session_bottom.jpg" alt="After" width="150" /> <img src="screenshots/redesign/18_add_session_dark.jpg" alt="After" width="150" /> |

<br/>

## 📜 Gaming History

Sessions grouped into *This week* / *Earlier*, searchable, with covers, participants and winners. Open a session to see the points of each player, notes, and to **edit** or **delete** it.<br/><br/>

| Before | After |
|:---:|:---:|
| <img src="screenshots/games_history.png" alt="Before" width="240" /> | <img src="screenshots/redesign/13_history.jpg" alt="After" width="150" /> |

Session details (new):<br/>

| New |
|:---:|
| <img src="screenshots/redesign/14_session_details.jpg" alt="After" width="150" /> |

<br/>

## 👤 Players

Players directory with search, win rate and ranking. Each player shows the total number of games played.<br/><br/>

| Before | After |
|:---:|:---:|
| <img src="screenshots/player_list_light.png" alt="Before" width="240" /><br/><img src="screenshots/player_list_dark.png" alt="Before" width="240" /> | <img src="screenshots/redesign/12_players_directory.jpg" alt="After" width="150" /> <img src="screenshots/redesign/17_players_dark.jpg" alt="After" width="150" /> |

<br/>

## ⚙️ Profile

Your games logged and win rate, account settings and sign-out.<br/><br/>

| New |
|:---:|
| <img src="screenshots/redesign/19_profile.jpg" alt="After" width="150" /> |


<br/><br/>
## Remaining work

The redesign itself is done; this is what I still plan to finish (roughly in priority order).

**Features**
- [ ] **Reports screen** – the *Quick Report* tile on Home is not wired up yet and the charts screen has not been redesigned (`GameReportsViewModel` is waiting for it).
- [ ] **Settings** – the settings rows on the Profile screen (account information, notifications, privacy, appearance) are not functional yet.
- [ ] **Games with several modes** – a game should offer multiple play modes, and you should pick per session whether it is co-op or player vs player (today it is a single co-op flag on the game).
- [ ] **Forgot password** – the login screen link does nothing yet.
- [ ] **Create an account** – instead of a sign-up form it should show a message asking to contact me for an account.
- [ ] **History filters** – the filter chips (*Wins Only*, *Strategy*, *Co-op*) are in the state but do not filter the list yet.
- [ ] **Game details from the collection** – only available for games imported from BGG; manually added games have no details screen.
- [ ] **Richer data model (Room migrations)** – game category / year / rating, per-player scores, session duration, player avatar and level, "active this week" and achievements are placeholders or hidden in the UI for now.

**Code cleanup**
- [ ] Remove **legacy ViewModels** that no screen uses any more (`AddEditGameViewModel`, `GamePlayViewModel`, `GameDetailsBGGViewModel`, `GameSearchViewModel`) together with their Koin registrations and tests.
- [ ] Delete **commented-out code** (`AddPlayerNewScreen`, `Typography`, `CooperatePlayers`) and finish the font `TODO` in `Typography.kt`.
- [ ] Remove **unused resources** reported by lint (≈ 86: old icons, colors, strings).
- [ ] Move the remaining **hard-coded UI strings** (≈ 45, mostly the login screen, dialogs and navigation labels) to `strings.xml`.
- [ ] Rename the temporary `*New*` classes (`HistoryNewScreen`, `ProfileNewViewModel`, `NewBottomBarElements`, …) now that the old UI is gone.
- [ ] Follow the component conventions everywhere: `modifier` as the first parameter (3 lint warnings), one component per file, 4 dp spacing grid.
- [ ] Update outdated Gradle dependencies flagged by lint.

**UI polish**
- [ ] **Edge-to-edge** support – in dark theme the system status / navigation bars currently stay light.
- [ ] Visual fixes on the **My Collection** screen.
- [ ] Visual fixes on the **session** screens (log session / session details).
- [ ] (Optional) Redesign the splash screen.

**Tests**
- [ ] More unit tests for the new ViewModels (sign-out, saving a game / session, mappings). The suite currently passes (187 tests) and runs in CI on every pull request.

<br/><br/>
## 🏗 Architecture & engineering notes

**Stack:** Kotlin · Jetpack Compose · MVVM · Coroutines + Flow · Voyager (navigation) · Koin (DI) · Room · Retrofit (BGG XML API) · Firebase · Coil · JUnit + MockK + Turbine.

**Structure:** `data` (Room DAOs, Retrofit/BGG, Firebase, repositories that wrap results in a `RequestResult` sealed type) → `ui` (feature-based Compose screens, one `ViewModel` per screen exposing an immutable `State` via `StateFlow`).

**Recent refactor — user feedback & data flow:**
- **One-off events, not boolean state** – success/error are emitted as `Channel` events and shown through a single themed snackbar host (`LocalSnackbarHost`), instead of `success*/errorVisible` flags lingering in UI state.
- **Consistent feedback** – every action (add / edit / delete / login) surfaces a typed snackbar (success / error / info); legacy `Toast`s were removed for a consistent, Compose-native UX.
- **Data before render** – screens load once on entry and show a loader until the data is ready, avoiding flashes of empty/placeholder state.
- **Errors handled at the boundary** – repository calls share a single `safeDbCall` helper (`runCatching` → `RequestResult`), so exceptions don't leak into the UI layer.

**Testing:** unit tests (JUnit + MockK + Turbine) cover ViewModels (state **and** emitted events), repositories/DAOs and use cases; CI runs them on every push (see the badge at the top).

<br/>

<br/><br/><br/>
📄 Licenses

This project's own source code is licensed under the [MIT License](LICENSE).

This project uses several open-source libraries. Key libraries and their licenses include:

[Voyager](https://github.com/adrielcafe/voyager) – MIT License

[Koin](https://insert-koin.io/) – Apache License 2.0

[Retrofit](https://square.github.io/retrofit/) – Apache License 2.0

[Gson](https://github.com/google/gson) – Apache License 2.0

[Coil](https://github.com/coil-kt/coil) – Apache License 2.0

[Timber](https://github.com/JakeWharton/timber) – Apache License 2.0

[Simple XML](http://simple.sourceforge.net/) – Apache License 2.0

[Compose Charts](https://github.com/ehsannarmani/ComposeCharts) – Apache License 2.0

[Sheets Compose Dialogs](https://github.com/maxkeppeler/sheets-compose-dialogs) – MIT License

[Firebase – Google Play Services Terms](https://firebase.google.com/)

You can find full license texts in the third_party_licenses.txt file.
