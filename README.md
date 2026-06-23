![Android CI](https://github.com/OskarGosk/BoardGame/actions/workflows/android-ci.yml/badge.svg)


# 🎲 BoardGame

The application is designed to **record and analyze board game sessions**.<br/><br/>

With this app you can:<br/>
- **Create a player list** – each player has their total number of games played and wins stored.<br/>
- **Create a board game list** – games can be added manually (basic data: name, number of players, type, expansion/base game) or automatically imported from the **BoardGameGeek (BGG)** database.<br/>
- **Add gameplays** – each session contains information about the game played (base/expansions), participating players, the winner, the date of the session, and an optional description.<br/>
- **Browse game history** – see a list of past sessions with the date, game name, participants, and the winner.<br/>
- **Analyze statistics** – e.g., how many players participated in sessions, which games are played most often, how many wins each player has, and reports in chart form.<br/><br/>

The application works in two modes:<br/>
- **Guest** – data is stored locally.<br/>
- **Logged-in user** – data is synchronized with the **Firebase Database**, allowing saving and retrieving across devices.<br/>
<br/>
A demo account is available on request, or you can try the app right away in **Guest** mode.<br/><br/>

<img width="360" height="400" alt="Login Screen" src="screenshots/login_screen.png" /><br/>
<img width="360" height="400" alt="HomeScreen Light" src="screenshots/home_screen_light.png" /><br/>
<img width="360" height="400" alt="HomeScreen Dark" src="screenshots/home_screen_dark.png" /><br/>

<br/><br/>
## 👤 Player List<br/>

Each player shows the total number of games played and wins.<br/>
You can search for players by nickname and sort by name or number of games played.<br/><br/>


<img width="720" height="400" alt="Player List Dark" src="screenshots/player_list_dark.png" /><br/>
<img width="900" height="400" alt="Player list Light" src="screenshots/player_list_light.png" /><br/>

<br/><br/>
## 🎲 Board Game List<br/>

Shows basic information about each game, including min/max players and total sessions.<br/>
Games can be added, edited, or deleted.<br/>
Search and sorting options are available to filter results.<br/><br/>

<img width="720" height="400" alt="Game List Dark" src="screenshots/game_list_dark.png" /><br/>
<img width="720" height="400" alt="Game List Light" src="screenshots/game_list_light.png" /><br/>
<br/><br/>
Games can be added manually or imported from the **BGG database**.<br/><br/>

<img width="540" height="400" alt="AddGame manually" src="screenshots/add_game_manually.png" /><br/>
<img width="720" height="400" alt="AddGame from BGG" src="screenshots/add_game_from_bgg.png" /><br/>

<br/><br/>
## ➕ Add Gameplay<br/>

Clicking "+" on the game list allows you to add a new gameplay session.<br/>
Select if you played only the base game or with expansions.<br/>
Choose participating players, date, winner, and optionally add a description of the session.<br/>
You can switch the game mode between normal (Player vs Player) or Co-Op.<br/><br/>

<img width="900" height="400" alt="GamePlay Dark" src="screenshots/gameplay_dark.png" /><br/>
<img width="540" height="400" alt="GamePlay Light" src="screenshots/gameplay_light.png" /><br/>


## 📜 Game History<br/>

View past game sessions, search by game or player name.<br/>
Displays game name, date, players involved, and the winner.<br/><br/>

<img width="720" height="400" alt="Games History" src="screenshots/games_history.png" /><br/>

<br/><br/>
## 📊 Reports<br/>

Currently, one chart is available to show the number of gameplays based on selection.<br/>
It can display sessions per year, month, or custom period.<br/>

<img width="540" height="400" alt="Games Reports" src="screenshots/games_reports.png" /><br/>

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
