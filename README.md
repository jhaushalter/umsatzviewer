# Umsatzviewer

Liest Bankumsätze aus einer DB2-Datenbank.

Das Projekt ist als Maven-Webanwendung aufgebaut.

## Voraussetzungen

- Java 8 oder neuer
- Maven 3
- Zugriff auf die DB2-Datenbank

## Datenbank konfigurieren

Die Zugangsdaten werden nicht im Quelltext gespeichert. Konfigurieren Sie diese
Werte als Umgebungsvariablen oder JVM-Systemproperties:

```text
DB_URL=jdbc:db2://host:25010/banking
DB_USER=banking
DB_PASSWORD=<Passwort>
```

## Build

```powershell
mvn clean package
```

Das erzeugte WAR liegt unter `target/umsatzviewer.war`.
