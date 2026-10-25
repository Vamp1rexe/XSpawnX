# XSpawnX

Schlankes Spawn-Plugin für **Paper 26.2** mit eigenem Spawn, der den Vanilla-Weltspawn komplett übergeht.

[![build](https://github.com/Vamp1rexe/XSpawnX/actions/workflows/build.yml/badge.svg)](https://github.com/Vamp1rexe/XSpawnX/actions/workflows/build.yml)
[Download der neuesten Version](https://github.com/Vamp1rexe/XSpawnX/releases/latest)

## Funktionen

| Funktion | Verhalten |
|---|---|
| `/spawn` | Teleportiert zum Spawn. Nur für Ops und Spieler mit dem Recht `xspawnx.spawn`. |
| `/setspawn` | Setzt den Spawn an deine Position, inklusive Blickrichtung. Recht `xspawnx.setspawn`. |
| `/spawn reload` | Lädt die `config.yml` neu. Recht `xspawnx.reload`. |
| `/spawn resetcooldown <Spieler>` | Setzt die Abklingzeit eines Spielers zurück. Recht `xspawnx.resetcooldown`. |
| Erster Join | Neue Spieler starten am Spawn. |
| Tod | Ohne Bett und ohne Respawn-Anker respawnt der Spieler am Spawn. Mit Bett oder Anker bleibt alles wie bei Vanilla. |

Der Spawn liegt in `plugins/XSpawnX/spawn.yml` und ist unabhängig von `/setworldspawn`. Solange kein Spawn gesetzt ist, tut das Plugin nichts, die Server-Log weist darauf hin.

## Installation

1. `XSpawnX-<Version>.jar` von der [Release-Seite](https://github.com/Vamp1rexe/XSpawnX/releases/latest) in den Ordner `plugins` legen.
2. Server starten. Die `config.yml` wird automatisch erstellt.
3. An die gewünschte Stelle gehen und den Spawn setzen:

```
/setspawn
```

## Berechtigungen

Alle Rechte sind standardmäßig nur für Ops. Für andere Spieler oder Gruppen über LuckPerms:

| Recht | Wirkung |
|---|---|
| `xspawnx.spawn` | `/spawn` benutzen |
| `xspawnx.setspawn` | `/setspawn` benutzen |
| `xspawnx.reload` | `/spawn reload` benutzen |
| `xspawnx.resetcooldown` | `/spawn resetcooldown` benutzen |
| `xspawnx.bypass` | Wartezeit und Abklingzeit von `/spawn` ignorieren |
| `xspawnx.cooldown.<Minuten>` | Abklingzeit von `/spawn` pro Spieler oder Gruppe, z.B. `xspawnx.cooldown.50` = 50 Minuten. Überschreibt `teleport.cooldown-seconds`, bei mehreren Rechten gilt das kleinste, `xspawnx.cooldown.0` = keine. |

```
/lp user <Name> permission set xspawnx.spawn true
/lp group <Gruppe> permission set xspawnx.spawn true
/lp group <Gruppe> permission set xspawnx.cooldown.50 true
```

## Konfiguration

Das Plugin erstellt `plugins/XSpawnX/config.yml` beim ersten Start selbst. Änderungen werden mit `/spawn reload` übernommen.

| Einstellung | Standard | Bedeutung |
|---|---|---|
| `first-join.enabled` | `true` | Neue Spieler starten am Spawn |
| `respawn.enabled` | `true` | Respawn am Spawn nach dem Tod |
| `respawn.respect-bed-and-anchor` | `true` | Bett und Respawn-Anker haben Vorrang. Bei `false` respawnen alle am Spawn. |
| `teleport.delay-seconds` | `0` | Wartezeit vor `/spawn`, 0 = sofort |
| `teleport.cancel-on-move` | `true` | Teleport bei Bewegung während der Wartezeit abbrechen |
| `teleport.cooldown-seconds` | `0` | Abklingzeit zwischen zwei Teleports |
| `messages.*` | siehe Datei | Alle Nachrichten im MiniMessage-Format |

## Bauen

Jeder Push startet den Build über GitHub Actions (Tab *Actions*). Ein Tag wie `v1.0.0` erzeugt zusätzlich ein Release mit angehängtem Jar.
Lokal: JDK 25 und Gradle 9.5.1, dann `gradle build --no-daemon`, das Jar liegt in `build/libs/`.
