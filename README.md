Aufgabe 9

Installationsanleitung
Um die App auf deinem lokalen Entwicklungsumgebung auszuführen, folge diesen Schritten:

1.	Clone das Repository:
git clone <repository-url>
2.	Öffne das Projekt in Android Studio: Öffne Android Studio und lade das Projekt, das du gerade geklont hast.
3.	Projektabhängigkeiten synchronisieren: Stelle sicher, dass alle Abhängigkeiten korrekt synchronisiert sind. In Android Studio sollte dies automatisch passieren. Falls nicht, gehe zu File > Sync Project with Gradle Files.
4.	Run die App: Verbinde dein Android-Gerät oder starte einen Emulator, um die App auszuführen.
   
Funktionsbeschreibung
Die To-Do-App hat folgende Funktionen:
•	Offene Aufgaben: Benutzer können offene Aufgaben hinzufügen, bearbeiten, löschen und den Status auf erledigt setzen.
•	Erledigte Aufgaben: Benutzer können zwischen offenen und erledigten Aufgaben wechseln und erledigte Aufgaben anzeigen.
•	Bearbeiten und Löschen von Aufgaben: Aufgaben können bearbeitet oder gelöscht werden, wenn der Benutzer daraufklickt.
•	Eingabe von Aufgabeninformationen: Beim Hinzufügen oder Bearbeiten einer Aufgabe können Titel, Beschreibung, Priorität und Deadline eingegeben werden.

Verwendete Technologien
Die App verwendet die folgenden Technologien:
•	Jetpack Compose: Eine moderne Android UI-Toolkit, das deklarative UI ermöglicht.
•	Kotlin: Die Programmiersprache für die Entwicklung der Android-App.
•	ViewModel und Repository: Architekturkomponenten zur Trennung von UI und Logik.
•	Room Database: Für die persistente Speicherung von To-Do-Daten.


