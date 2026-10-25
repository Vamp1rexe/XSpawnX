package dev.xspawnx;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public final class XSpawnXPlugin extends JavaPlugin {
	/** /spawn benutzen. Ops standardmäßig, andere über LuckPerms. */
	public static final String PERM_SPAWN = "xspawnx.spawn";
	/** /setspawn benutzen. */
	public static final String PERM_SETSPAWN = "xspawnx.setspawn";
	/** /spawn reload benutzen. */
	public static final String PERM_RELOAD = "xspawnx.reload";
	/** /spawn resetcooldown benutzen. */
	public static final String PERM_RESETCOOLDOWN = "xspawnx.resetcooldown";
	/** Wartezeit und Abklingzeit ignorieren. */
	public static final String PERM_BYPASS = "xspawnx.bypass";

	private SpawnStore store;
	private SpawnService service;

	@Override
	public void onEnable() {
		// Erstellt plugins/XSpawnX/config.yml mit allen Standardwerten, falls sie fehlt
		saveDefaultConfig();

		store = new SpawnStore(this);
		store.load();

		registerPermission(PERM_SPAWN, "Erlaubt /spawn");
		registerPermission(PERM_SETSPAWN, "Erlaubt /setspawn");
		registerPermission(PERM_RELOAD, "Erlaubt /spawn reload");
		registerPermission(PERM_RESETCOOLDOWN, "Erlaubt /spawn resetcooldown");
		registerPermission(PERM_BYPASS, "Ignoriert Wartezeit und Abklingzeit von /spawn");

		service = new SpawnService(this, store);
		getServer().getPluginManager().registerEvents(service, this);
		getServer().getPluginManager().registerEvents(new SpawnListener(this, service), this);

		getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
				XSpawnXCommands.register(event.registrar(), this, store, service));

		if (!store.isSet()) {
			getLogger().warning("Es ist noch kein Spawn gesetzt. Stelle dich an die gewünschte Stelle und nutze /setspawn.");
		}
	}

	private static void registerPermission(String name, String description) {
		Bukkit.getPluginManager().addPermission(new Permission(name, description, PermissionDefault.OP));
	}
}
