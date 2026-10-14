package dev.xspawnx;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Teleport zum Spawn mit Wartezeit, Abklingzeit und Nachrichten aus der config.yml. */
public final class SpawnService implements Listener {
	private record Pending(BukkitTask task, Location start) {
	}

	private final JavaPlugin plugin;
	private final SpawnStore store;
	private final Map<UUID, Pending> pending = new HashMap<>();
	private final Map<UUID, Long> lastUse = new HashMap<>();

	public SpawnService(JavaPlugin plugin, SpawnStore store) {
		this.plugin = plugin;
		this.store = store;
	}

	private FileConfiguration config() {
		return plugin.getConfig();
	}

	/** Der Spawn aus spawn.yml, oder null, wenn keiner gesetzt ist. Der Vanilla-Weltspawn wird nie benutzt. */
	public Location spawnLocation() {
		return store.get();
	}

	// ---------- Nachrichten ----------

	/** Sendet die Nachricht aus der Config. Leere Nachrichten werden übersprungen. */
	public void send(CommandSender to, String key, TagResolver... resolvers) {
		String raw = config().getString("messages." + key, "");
		if (raw == null || raw.isEmpty()) {
			return;
		}
		Component component = MiniMessage.miniMessage().deserialize(raw, resolvers);
		to.sendMessage(component);
	}

	// ---------- Teleport ----------

	public void teleportToSpawn(Player player) {
		Location target = spawnLocation();
		if (target == null) {
			send(player, "spawn-missing");
			return;
		}

		boolean bypass = player.hasPermission(XSpawnXPlugin.PERM_BYPASS);
		UUID id = player.getUniqueId();

		int cooldown = config().getInt("teleport.cooldown-seconds", 0);
		if (!bypass && cooldown > 0) {
			long left = lastUse.getOrDefault(id, 0L) + cooldown * 1000L - System.currentTimeMillis();
			if (left > 0) {
				send(player, "cooldown", Placeholder.unparsed("seconds", String.valueOf((left + 999) / 1000)));
				return;
			}
		}

		cancelPending(id);

		int delay = bypass ? 0 : config().getInt("teleport.delay-seconds", 0);
		if (delay <= 0) {
			doTeleport(player, target);
			return;
		}

		send(player, "teleporting", Placeholder.unparsed("seconds", String.valueOf(delay)));
		BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
			pending.remove(id);
			Location now = spawnLocation();
			if (player.isOnline() && now != null) {
				doTeleport(player, now);
			}
		}, delay * 20L);
		pending.put(id, new Pending(task, player.getLocation()));
	}

	private void doTeleport(Player player, Location target) {
		player.teleport(target);
		lastUse.put(player.getUniqueId(), System.currentTimeMillis());
		send(player, "teleported");
	}

	private void cancelPending(UUID id) {
		Pending p = pending.remove(id);
		if (p != null) {
			p.task().cancel();
		}
	}

	// ---------- Events ----------

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (pending.isEmpty() || !config().getBoolean("teleport.cancel-on-move", true)) {
			return;
		}
		Player player = event.getPlayer();
		Pending p = pending.get(player.getUniqueId());
		if (p == null) {
			return;
		}
		Location to = event.getTo();
		Location start = p.start();
		boolean moved = to.getBlockX() != start.getBlockX()
				|| to.getBlockY() != start.getBlockY()
				|| to.getBlockZ() != start.getBlockZ();
		if (moved) {
			cancelPending(player.getUniqueId());
			send(player, "cancelled-move");
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		cancelPending(event.getPlayer().getUniqueId());
	}
}
