package dev.xspawnx;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/** Speichert den Spawn in plugins/XSpawnX/spawn.yml, unabhängig vom Vanilla-Weltspawn. */
public final class SpawnStore {
	private final JavaPlugin plugin;
	private final File file;

	private String world;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private boolean set;

	public SpawnStore(JavaPlugin plugin) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), "spawn.yml");
	}

	public boolean isSet() {
		return set;
	}

	/** Der gespeicherte Spawn, oder null, wenn keiner gesetzt ist oder die Welt fehlt. */
	public Location get() {
		if (!set) {
			return null;
		}
		World w = Bukkit.getWorld(world);
		if (w == null) {
			return null;
		}
		return new Location(w, x, y, z, yaw, pitch);
	}

	public void load() {
		if (!file.exists()) {
			set = false;
			return;
		}
		YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
		world = y.getString("world");
		this.x = y.getDouble("x");
		this.y = y.getDouble("y");
		this.z = y.getDouble("z");
		this.yaw = (float) y.getDouble("yaw");
		this.pitch = (float) y.getDouble("pitch");
		set = world != null && !world.isBlank();
	}

	public void set(Location loc) {
		this.world = loc.getWorld().getName();
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.yaw = loc.getYaw();
		this.pitch = loc.getPitch();
		this.set = true;
		save();
	}

	private void save() {
		YamlConfiguration y = new YamlConfiguration();
		y.set("world", world);
		y.set("x", x);
		y.set("y", this.y);
		y.set("z", z);
		y.set("yaw", yaw);
		y.set("pitch", pitch);
		try {
			plugin.getDataFolder().mkdirs();
			y.save(file);
		} catch (IOException e) {
			plugin.getLogger().severe("spawn.yml konnte nicht gespeichert werden: " + e.getMessage());
		}
	}
}
