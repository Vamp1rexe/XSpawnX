package dev.xspawnx;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Spawn beim ersten Join und beim Tod. Der Vanilla-Weltspawn wird dabei komplett übergangen,
 * es zählt nur der Spawn aus spawn.yml.
 */
public final class SpawnListener implements Listener {
	private final JavaPlugin plugin;
	private final SpawnService service;

	public SpawnListener(JavaPlugin plugin, SpawnService service) {
		this.plugin = plugin;
		this.service = service;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!plugin.getConfig().getBoolean("first-join.enabled", true)) {
			return;
		}
		Player player = event.getPlayer();
		if (player.hasPlayedBefore()) {
			return;
		}
		Location spawn = service.spawnLocation();
		if (spawn != null) {
			player.teleport(spawn);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (!plugin.getConfig().getBoolean("respawn.enabled", true)) {
			return;
		}
		boolean respectBed = plugin.getConfig().getBoolean("respawn.respect-bed-and-anchor", true);
		boolean hasBedOrAnchor = event.isBedSpawn() || event.isAnchorSpawn();
		if (respectBed && hasBedOrAnchor) {
			return;
		}
		Location spawn = service.spawnLocation();
		if (spawn != null) {
			event.setRespawnLocation(spawn);
		}
	}
}
