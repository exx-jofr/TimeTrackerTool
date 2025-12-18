package org.exxjofr.timetracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

@Composable
fun Help() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Titel
        Text(
            text = "Guide & Hilfe",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Abschnitt 1: Einrichtung
        HelpSection(
            title = "1. Einrichtung",
            content = """
Willkommen! Folge diesen Schritten, um deine TimeTracker-Anwendung einzurichten:

• Öffne die Anwendung und navigiere zu den Einstellungen
• Lege einen Pfad fest, wo deine Zeitdaten gespeichert werden sollen. Die Datei wird automatisch erstellt.
• Als nächstes füge den Pfad der Exceltabelle (Vorlage) hinzu, die du für Zeiterfassung verwendest.
• Gib deinen Jira Benutzername ein. Dieser ist eine Mailadresse.
• Gib dein Jira API Token ein. Wo du dieses findest, steht bei "3. Häufig gestellte Fragen (Q&A)".
• SPEICHERN nicht vergessen!


Nun ist es möglcih, die Zeiterfassung zu starten.
            """.trimIndent()
        )

        // Abschnitt 2: Do Not's
        HelpSection(
            title = "2. Do Not's - Häufige Fehler",
            content = """
Vermeide diese häufigen Fehler bei der Verwendung der Anwendung:

❌ Zeiteinträge werden erst hinzugefügt, nachdem man auf "Hinzufügen" geklickt hat
❌ Verboten sind Semikolon (;) in der Vorgangs-ID oder in der Vorgangsbeschreibung
❌ Überprüfe, ob die Vorgangs-ID in Jira existiert, bevor etwas hinzugefügt wird.
❌ Keine Änderungen in der CSV-Datei manuell vornehmen, während die Anwendung läuft
❌ 
❌ Nicht die Systemuhr, während einer aktiven Zeitmessung ändern

Denke daran: Ein kurzer Moment zur Überprüfung erspart dir später Probleme!
            """.trimIndent()
        )

        // Abschnitt 3: Fragen und Antworten
        HelpSection(
            title = "3. Häufig gestellte Fragen (Q&A)",
            content = """
F: Wie ist die CSV-Datei aufgebaut?
A: Die CSV-Datei hat folgende Spalten:
   UUID;Date;Vorgangs-ID;Startzeit;Endzeit;Dauer;Beschreibung;InJira

F: Warum wird die UUID nicht überprüft nach Duplikaten?
A: Die Wahrscheinlichkeit, dass zwei identische UUIDs generiert werden, ist extrem gering. 
   Es ist etwa 7 Milliarden Mal wahrscheinlicher, den Lotto-Jackpot in Deutschland zu gewinnen, als dass bei einer Milliarde generierter UUIDs eine zufällige Kollision auftritt.

F: Wird überprüft ob Jira Vorgänge sich überlappen?
A: Nein, man ist dafür verantwortlich, dass die Zeiten sich nicht überschneiden.

F: Kann ich mehrere Projekte verwalten?
A: Absolut! Erstelle verschiedene Kategorien und nutze Tags zur besseren Organisation 
   deiner Projekte.

F: Gibt es eine mobile Version?
A: Aktuell ist nur die Desktop-Version verfügbar. Eine mobile Version ist geplant.
            """.trimIndent()
        )

        // Abschnitt 4: Coding Hinweise
        HelpSection(
            title = "4. Coding Hinweise für Entwickler",
            content = """
Falls du die Anwendung erweitern oder anpassen möchtest, beachte folgende Punkte:

Architecture:
• Das Projekt folgt der MVVM-Architektur (Model-View-ViewModel)
• Verwende Compose für die UI-Schicht
• Nutze Coroutines für asynchrone Operationen

Best Practices:
• Schreibe wiederverwendbare Composable-Funktionen
• Nutze State Management für reaktive Updates
• Implementiere Fehlerbehandlung bei Datenbankoperationen
• Schreibe Tests für kritische Funktionen

Performance:
• Vermeide unnötige Recompositions
• Nutze remember{} für State-Speicherung
• Optimiere Listenrendering mit LazyColumn

Wichtig: Dokumentiere deine Code-Änderungen und erstelle einen Pull Request 
für Code-Reviews!
            """.trimIndent()
        )

        // Abstand am Ende
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun HelpSection(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
            .padding(bottom = 16.dp)
    ) {
        // Abschnitts-Titel
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Trennlinie
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp), thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outline
        )

        // Inhalt
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 1.6.em
        )
        Spacer(Modifier.height(4.dp))
    }
}