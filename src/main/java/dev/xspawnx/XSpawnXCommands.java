package dev.xspawnx;

import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

/**
 * /spawn           teleportiert zum Spawn (xspawnx.spawn)
 * /spawn reload    lädt die config.yml neu (xspawnx.reload)
 * /setspawn        setzt den Spawn an deine Position (xspawnx.setspawn)
 */
public final class XSpawnXCommands {
	private XSpawnXCommands() {
	}

	public static void register(Commands commands, JavaPlugin plugin, SpawnStore store, SpawnService service) {
		commands.register(Commands.literal("spawn")
				.requires(src -> src.getSender().hasPermission(XSpawnXPlugin.PERM_SPAWN)
						|| src.getSender().hasPermission(XSpawnXPlugin.PERM_RELOAD))
				.executes(ctx -> {
					CommandSender sender = ctx.getSource().getSender();
					if (!(sender instanceof Player player)) {
						service.send(sender, "only-players");
						return 0;
					}
					if (!player.hasPermission(XSpawnXPlugin.PERM_SPAWN)) {
						service.send(player, "no-permission");
						return 0;
					}
					service.teleportToSpawn(player);
					return 1;
				})
				.then(Commands.literal("reload")
						.requires(src -> src.getSender().hasPermission(XSpawnXPlugin.PERM_RELOAD))
						.executes(ctx -> {
							plugin.reloadConfig();
							store.load();
							service.send(ctx.getSource().getSender(), "reload-done");
							return 1;
						}))
				.build(), "Teleportiert dich zum Spawn");

		commands.register(Commands.literal("setspawn")
				.requires(src -> src.getSender().hasPermission(XSpawnXPlugin.PERM_SETSPAWN))
				.executes(ctx -> {
					CommandSender sender = ctx.getSource().getSender();
					if (!(sender instanceof Player player)) {
						service.send(sender, "only-players");
						return 0;
					}
					Location loc = player.getLocation();
					store.set(loc);
					service.send(player, "spawn-set",
							Placeholder.unparsed("x", format(loc.getX())),
							Placeholder.unparsed("y", format(loc.getY())),
							Placeholder.unparsed("z", format(loc.getZ())),
							Placeholder.unparsed("world", loc.getWorld().getName()));
					return 1;
				})
				.build(), "Setzt den Spawn an deine Position");
	}

	private static String format(double value) {
		return String.format(Locale.ROOT, "%.1f", value);
	}
}
