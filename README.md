# X-WARDEN API

Official API for the [X-Warden](https://builtbybit.com/resources/x-warden-anti-cheat-anti-exploit.125374/)
plugin — anti-abuse for any server with an economy.

Two things live here, and nothing else:

- **The contract** any plugin can compile against to report a violation into X-Warden's pipeline,
  read back what it already knows, and take part in duplicate-item detection.
- **The addon contract** — drop a jar into `plugins/X-Warden/addons/` and X-Warden loads it, hands it
  a data folder, a logger and the API, and unloads it cleanly on shutdown.

**Links:**
- [Wiki (Documentation)](https://github.com/Drawethree/X-Warden/wiki)
- [Developer API](https://github.com/Drawethree/X-Warden/wiki/Developer-API)
- [Javadocs](https://javadocs.drawethree.dev/x-warden/)

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
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

```groovy
repositories {
    maven { url 'https://repo.drawethree.dev/releases' }
}

dependencies {
    compileOnly 'dev.drawethree.xwarden:X-WardenAPI:1.0.0'
}
```

Pin a real version. Maven 3 dropped `LATEST` and `RELEASE` for dependency resolution, so a build
that asks for one resolves differently depending on who runs it, or not at all. The development
head is published to `https://repo.drawethree.dev/snapshots` as `1.0.1-SNAPSHOT` if you want it.

Sources and javadoc jars are published alongside each release, so your IDE will show the contract
and its comments rather than decompiled bytecode.

Always `provided` / `compileOnly`. X-Warden supplies these classes at runtime; shading them into your
own jar puts a second copy on the server and the two will not be the same class.

## Reporting something you detected

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

`XWardenAPI.find()` returns an `Optional` if you would rather not catch.

Confidence is **how sure you are, not how bad it is**. What happens next — a log line, an alert, a
freeze — is the server owner's setting, never yours: an addon cannot ship a check that punishes.

## Writing an addon

```java
public final class MyAddon implements XWardenAddon {

    @Override
    public void onEnable(XWardenAddonContext context) {
        XWardenAPI warden = context.getAPI();
        context.getLogger().info("running from " + context.getDataFolder());
    }

    @Override
    public void onDisable() {
    }
}
```

Declare it in your jar's manifest:

```
X-Warden-Addon-Class: com.example.MyAddon
X-Warden-Addon-Name: MyAddon
X-Warden-Addon-Version: 1.0.0
X-Warden-Addon-Author: You
```

## A note on Spigot

This module compiles against **spigot-api**, not paper-api, deliberately. X-Warden is sold to servers
running both, and a Paper-only method compiles and links exactly like any other — it fails only on
the server that lacks it. Building the contract against Spigot makes that a compile error here
instead of a `NoSuchMethodError` on somebody's server.

## Licence

GPL-3.0. See `LICENSE.txt`.
