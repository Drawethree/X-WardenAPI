# X-WardenAPI

Official API for the [X-Warden](https://builtbybit.com/resources/x-warden-anti-cheat-anti-exploit.125374/)
plugin — anti-abuse for any server with an economy.

Two things live here, and nothing else:

- **The contract** any plugin can compile against to report a violation into X-Warden's pipeline,
  read back what it already knows, punish, quarantine, and take part in duplicate-item detection.
- **The addon contract** — drop a jar into `plugins/X-Warden/addons/` and X-Warden loads it, hands it
  a data folder, a logger and the API, and unloads it cleanly on shutdown.

**Links:**
- [Wiki (Documentation)](https://github.com/Drawethree/X-Warden/wiki)
- [Developer API](https://github.com/Drawethree/X-Warden/wiki/Developer-API)
- [Writing an addon](https://github.com/Drawethree/X-Warden/wiki/Addons)
- [Javadocs](https://javadocs.drawethree.dev/x-warden/)

## Requirements

| | |
|---|---|
| X-Warden | 1.1.0 or newer on the server |
| Server | Paper or Spigot 1.21.8 |
| Java | 17 to compile against this artifact; the server itself runs Java 21 |

## Dependency

Artifacts are published to [repo.drawethree.dev](https://repo.drawethree.dev). Every build,
including per-commit snapshots, is listed at
[ci.drawethree.dev/x-warden](https://ci.drawethree.dev/x-warden/).

### Maven

```xml
<repository>
    <id>drawethree</id>
    <url>https://repo.drawethree.dev/releases</url>
</repository>

<dependency>
    <groupId>dev.drawethree.xwarden</groupId>
    <artifactId>X-WardenAPI</artifactId>
    <version>1.1.0</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

```groovy
repositories {
    maven { url 'https://repo.drawethree.dev/releases' }
}

dependencies {
    compileOnly 'dev.drawethree.xwarden:X-WardenAPI:1.1.0'
}
```

Pin a real version. Maven 3 dropped `LATEST` and `RELEASE` for dependency resolution, so a build
that asks for one resolves differently depending on who runs it, or not at all. The development
head is published to `https://repo.drawethree.dev/snapshots` as `1.1.1-SNAPSHOT` if you want it.

Sources and javadoc jars are published alongside each release, so your IDE will show the contract
and its comments rather than decompiled bytecode.

Always `provided` / `compileOnly`. X-Warden supplies these classes at runtime; shading them into your
own jar puts a second copy on the server and the two will not be the same class.

## Getting the instance

X-Warden may not be installed, so guard the lookup:

```java
try {
    XWardenAPI warden = XWardenAPI.getInstance();
    warden.registerCheck("economy", "mygame-impossible-win");
    warden.report(uuid, "economy", "mygame-impossible-win", 80, Map.of("Wagered", "1000"));
} catch (IllegalStateException | NoClassDefFoundError wardenAbsent) {
    // carry on without it
}
```

`XWardenAPI.find()` returns an `Optional` if you would rather not catch, and the instance is also
registered with Bukkit's `ServicesManager` under `XWardenAPI.class`. Do not type a field in your
plugin as an X-Warden class unless you have already confirmed it is installed: a field of that type
makes the JVM resolve the class when *your* class loads, which is before you get a chance to check.

X-Warden publishes its API in `onEnable`, so read it from your own `onEnable` (with `softdepend`)
or from a `PluginEnableEvent`, never from a static initialiser.

Confidence is **how sure you are, not how bad it is**. What happens next — a log line, an alert, a
freeze — is the server owner's setting, never yours: a plugin cannot ship a check that punishes.

## The ten area APIs

Everything added since 1.0.0 lives on these. `XWardenAPI`'s own methods are the original
simplified surface and stay as they are.

| Getter | What it is for |
|---|---|
| `getFlagsApi()` | the findings X-Warden has recorded, paging and grouping them, marking them handled |
| `getLedgerApi()` | where the money went: transactions, mining income by hour and by minute, lifetime totals |
| `getEconomyApi()` | which economy is being watched and what it can observe; planning and applying a rollback |
| `getIntegrityApi()` | identities, sightings, the live index, quarantine, and registering a container source |
| `getNetworkApi()` | accounts that look like the same hands — read-only, there is no action path |
| `getAutomationApi()` | the mining automation score and its signals, challenges, calibration |
| `getModulesApi()` | the five modules, their checks, and whether each check can actually reach its action |
| `getClientApi()` | the client each player joined with and what X-Warden concluded (1.1.0) |
| `getStaffApi()` | freeze, watch, punish, notes, case export, server status, vulnerability matches |
| `getConfigApi()` | reading and changing the configuration one line at a time, presets, reload |

## Threads

Every method on an area API carries a `@ThreadSafety` annotation saying which thread it may be
called from, and X-Warden enforces it — a call from the wrong thread throws
`IllegalStateException` at once rather than working nine times out of ten.

| Requirement | Meaning |
|---|---|
| `OFF_PRIMARY` | reads the database and blocks; never call it on the server thread |
| `PRIMARY` | touches the server; call it on the server thread |
| `ANY` | reads memory; call it from anywhere |

An addon doing work on a thread of its own gets to the server thread with
`XWardenAddonContext.call(...)` / `sync(...)` and leaves it with `async(...)`.

## Events

| Event | Fires | Cancellable |
|---|---|---|
| `WardenViolationEvent` | before a finding is recorded; usually off the server thread | yes — discards it entirely |
| `WardenViolationRecordedEvent` | after a finding is stored, with its id, level and action | no |
| `WardenFlagResolvedEvent` | after a finding is marked handled; may be off the server thread | no |
| `WardenPunishmentApplyEvent` | before a punishment is recorded or applied (1.1.0) | yes |
| `WardenPunishmentEvent` | after a punishment is applied or lifted (1.1.0) | no |
| `WardenQuarantineEvent` | after an item is taken out of play or handed back (1.1.0) | no |
| `WardenClientVerdictEvent` | when the join sequence concludes what client a player uses (1.1.0) | no |

## API version

`apiVersion()` grows by one whenever a method is added and nothing is ever removed, so a plugin
built against a newer X-Warden can refuse politely on an older one instead of dying at the call
site:

| Version | X-Warden | Added |
|---|---|---|
| 1 | 1.0.0 | the area APIs, `apiVersion()`, the addon contract, the addon registry methods |
| 2 | 1.0.0 | `unloadAddon(String)` |
| 3 | 1.1.0 | `addonFileNames()` |
| 4 | 1.1.0 | `getClientApi()`, punishments, quarantine, live sightings, `WardenAction.PUNISH`, the four 1.1.0 events |

Every type and method also carries `@since` in its Javadoc.

## Writing an addon

```java
public final class MyAddon implements XWardenAddon {

    @Override
    public void onEnable(XWardenAddonContext context) {
        XWardenAPI warden = context.getAPI();
        context.registerEvents(new MyListener());
        context.async(() -> {
            int open = warden.getFlagsApi().countOpen();
            context.sync(() -> context.getLogger().info(open + " open findings"));
        });
    }

    @Override
    public void onDisable() {
        // listeners and tasks that went through the context are released for you;
        // stop anything else you started here
    }
}
```

Declare it in your jar's manifest. Only the class is required; the rest is what `/xwarden addons`
shows:

```
X-Warden-Addon-Class: com.example.MyAddon
X-Warden-Addon-Name: MyAddon
X-Warden-Addon-Version: 1.0.0
X-Warden-Addon-Author: You
X-Warden-Addon-Description: One line on what it does
X-Warden-Min-Version: 1.1.0
```

`X-Warden-Priority` and `X-Warden-Depends` order the load; see `XWardenAddon`'s Javadoc. The
[Addons](https://github.com/Drawethree/X-Warden/wiki/Addons) page covers installing, replacing a
jar without a restart, and the X-Warden dashboard, which is built on exactly this contract.

## A note on Spigot

This module compiles against **spigot-api**, not paper-api, deliberately. X-Warden is sold to servers
running both, and a Paper-only method compiles and links exactly like any other — it fails only on
the server that lacks it. Building the contract against Spigot makes that a compile error here
instead of a `NoSuchMethodError` on somebody's server.

## Licence

MIT. See `LICENSE.txt`. Link against it from anything, open or closed.
