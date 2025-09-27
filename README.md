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

<img width="360" height="400" alt="Login Screen" src="https://github.com/user-attachments/assets/ec199c14-cf29-4986-95bc-f2b606ea2b4e" /><br/>
<img width="360" height="400" alt="HomeScreen Light" src="https://github.com/user-attachments/assets/fe78352c-7b77-4c18-b372-ced587280414" /><br/>
<img width="360" height="400" alt="HomeScreen Dark" src="https://github.com/user-attachments/assets/1d002034-f8d5-419f-866f-e7d9936d1b68" /><br/>

<br/><br/>
## 👤 Player List<br/>

Each player shows the total number of games played and wins.<br/>
You can search for players by nickname and sort by name or number of games played.<br/><br/>


<img width="720" height="400" alt="Player List Dark" src="https://github.com/user-attachments/assets/60300a2c-fa9b-4464-8007-1595190bd0dd" /><br/>
<img width="900" height="400" alt="Player list Light" src="https://github.com/user-attachments/assets/43fbce15-4194-4750-b62f-8ecefcf14fd1" /><br/>

<br/><br/>
## 🎲 Board Game List<br/>

Shows basic information about each game, including min/max players and total sessions.<br/>
Games can be added, edited, or deleted.<br/>
Search and sorting options are available to filter results.<br/><br/>

<img width="720" height="400" alt="Game List Dark" src="https://github.com/user-attachments/assets/18a039a2-9683-4f99-906e-8dd18aae1448" /><br/>
<img width="720" height="400" alt="Game List Light" src="https://github.com/user-attachments/assets/ebed0dd1-3293-47e4-84ae-16af88ff4324" /><br/>
<br/><br/>
Games can be added manually or imported from the **BGG database**.<br/><br/>

<img width="540" height="400" alt="AddGame manualy" src="https://github.com/user-attachments/assets/bac62d8f-19af-435e-8c83-2f2d840cacd0" /><br/>
<img width="720" height="400" alt="AddGame from BGG" src="https://github.com/user-attachments/assets/85d89a33-7afc-4e40-9ce9-66dce5bab4d9" /><br/>

<br/><br/>
## ➕ Add Gameplay<br/>

Clicking "+" on the game list allows you to add a new gameplay session.<br/>
Select if you played only the base game or with expansions.<br/>
Choose participating players, date, winner, and optionally add a description of the session.<br/>
You can switch the game mode between normal (Player vs Player) or Co-Op.<br/><br/>

<img width="900" height="400" alt="GamePLay Dark" src="https://github.com/user-attachments/assets/75799099-a571-43c8-8074-c698a3af60b3" /><br/>
<img width="540" height="400" alt="GamePlay Light" src="https://github.com/user-attachments/assets/2c6e549b-e734-42aa-a0fe-ece1fbba19e2" /><br/>


## 📜 Game History<br/>

View past game sessions, search by game or player name.<br/>
Displays game name, date, players involved, and the winner.<br/><br/>

<img width="720" height="400" alt="Games History" src="https://github.com/user-attachments/assets/a9f88d83-f56b-4fa3-97c8-18ae7eb80f2f" /><br/>

<br/><br/>
## 📊 Reports<br/>

Currently, one chart is available to show the number of gameplays based on selection.<br/>
It can display sessions per year, month, or custom period.<br/>

<img width="540" height="400" alt="Games Reports" src="https://github.com/user-attachments/assets/fd9c80ac-c98c-419f-bc9a-665552aeb1c2" /><br/>

<br/><br/><br/>
📄 Licenses
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
