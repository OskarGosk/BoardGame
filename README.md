Prosta aplikacja do zbierania wyników rozgrywek gier planszowych. W aplikacji tworzymy listę graczy i gier planszowych oraz tworzymy historyczne rogrywki w których podajem, w jaką gre graliśmy, kto grał i wygrał, kiedy graliśmy, krótki opis rozgrywki.
Mamy informację który gracz ile rozegrał rozgrywek i ile z nich wygrał, widzimy liczbe rozgrywem poszczególnych gier. Możemy sprawdzić kto grał i kto wygrał daną rozgrywkę z historii. 

<br/><br/>
Aplikacja pozwa przechowywać listę gier razem z podstawowymi informacjami, listę graczy z liczbą rozgrywek i win ratio, oraz wyniki rozgrywek.

<img width="360" height="400" alt="Login Screen" src="https://github.com/user-attachments/assets/ec199c14-cf29-4986-95bc-f2b606ea2b4e" /><br/>
<img width="360" height="400" alt="HomeScreen Light" src="https://github.com/user-attachments/assets/fe78352c-7b77-4c18-b372-ced587280414" /><br/>
<img width="360" height="400" alt="HomeScreen Dark" src="https://github.com/user-attachments/assets/1d002034-f8d5-419f-866f-e7d9936d1b68" /><br/>

<br/><br/>
**Player List:**<br/>
Przy każdym graczu widniej jego liczba dotychczasowych zarejestrowanych rozgrywek oraz licba wygranych.
Jest możliwośc wyszukania gracza po niku/imieniu, oraz proste sortowanie po nazwie lub liczbie rozegranych gier co oczywiście można łączyć.


<img width="720" height="400" alt="Player List Dark" src="https://github.com/user-attachments/assets/60300a2c-fa9b-4464-8007-1595190bd0dd" /><br/>
<img width="900" height="400" alt="Player list Light" src="https://github.com/user-attachments/assets/43fbce15-4194-4750-b62f-8ecefcf14fd1" /><br/>

<br/><br/>
**Board Game List:**<br/>
Lista gier, zawiera podstawowe informację o nich, takie jak min/max graczy. Liczba dotychczasowych rozgrywek.
Aplikacja pozwala na dodwanie, usuwanie i edycję gier.
Użytkownik posiada możlwiość wyszukiwania po nazwie gry w celu zawęzenia wyników, oraz podstawowe sortowanie, co tak samo jak w przypadku graczy można łączyć.

<img width="720" height="400" alt="Game List Dark" src="https://github.com/user-attachments/assets/18a039a2-9683-4f99-906e-8dd18aae1448" /><br/>
<img width="720" height="400" alt="Game List Light" src="https://github.com/user-attachments/assets/ebed0dd1-3293-47e4-84ae-16af88ff4324" /><br/>
<br/><br/>
Dodawanie gry jest możlwie poprzez wyszukanie jej z bazy danych udostytępnionej poprzez BGG lub podanie podatwowych danych manualnie.

<img width="540" height="400" alt="AddGame manualy" src="https://github.com/user-attachments/assets/bac62d8f-19af-435e-8c83-2f2d840cacd0" /><br/>
<img width="720" height="400" alt="AddGame from BGG" src="https://github.com/user-attachments/assets/85d89a33-7afc-4e40-9ce9-66dce5bab4d9" /><br/>

<br/><br/>
**Add gameplay:**<br/>

Po kliknięciu "+" na ekranie z listą gier, przechodzimy do dodania rozgrywki.
Możemy wybrać czy gramy tylko w podstawe, czy razem z jakimiś dodatkami. 
Wybieramy listę graczy, datę gry oraz kto wygrał. Mamy możliwość zmiany modelu rozgrywki, pomiędzy normalnym PLvsPL lub CO-OP.
Jest możliwość dodania opisu rozgrywki. Np. różnicę punktów, ogólnie wrażenie z rogrywki, czy cookolwiek aby zostało w pamięci. 

<img width="900" height="400" alt="GamePLay Dark" src="https://github.com/user-attachments/assets/75799099-a571-43c8-8074-c698a3af60b3" /><br/>
<img width="540" height="400" alt="GamePlay Light" src="https://github.com/user-attachments/assets/2c6e549b-e734-42aa-a0fe-ece1fbba19e2" /><br/>


<br/><br/>
**History game list:**<br/>
We also have Game History where we can search for the game result by game or player name. <br/>
On this screen we see the name of the game, the date of the game, the players who played and who won.

Mamy równeiż Historię Gier, gdzie możemy wyszukiwać wynik rozgrywki po nazwie gry lub gracza.
Widzimy tutaj, nazwe gry, datę rozgrywki, oraz gracz który wygrał.

<img width="720" height="400" alt="Games History" src="https://github.com/user-attachments/assets/a9f88d83-f56b-4fa3-97c8-18ae7eb80f2f" /><br/>

<br/><br/>
**Reports:**<br/>
Currently, we have one chart showing the number of gameplays depending on the selection.<br/>
The chart can show the number of gameplays in each year, in a selected month, or in a selected period.

Aktualnie posaiadamy jedne wykres przedstawiający ilośc rozgrywke w zależności od wyboru.<br/>
Wykres może przedstawioać ilośc rogrywke w każdym roku, w wybranym roku podzielone na miesiąće, w wybranym miesciu czy w wybranym okresie. 

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
