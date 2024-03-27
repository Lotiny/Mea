package me.lotiny.mea.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.lotiny.mea.Mea;
import me.lotiny.mea.utils.ConfigFile;
import me.lotiny.mea.utils.FileUtils;
import me.lotiny.mea.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class GameMap {

    private final Mea plugin = Mea.getInstance();

    private final int id;
    private final String name;
    private final File source;

    private final Set<UUID> votedPlayers = new HashSet<>();
    private final List<Location> spawnedPoint = new ArrayList<>();

    @Setter
    private ConfigFile config;
    @Setter
    private Location center;

    private World world;

    @Setter
    private boolean deathmatchMap = false;
    private boolean loaded = false;

    public boolean load() {
        if (this.loaded) {
            return true;
        }

        String worldFolder = source.getName() + "_" + System.currentTimeMillis();
        try {
            FileUtils.copy(source, new File(Bukkit.getWorldContainer().getParentFile(), worldFolder));
            Utilities.log("&aSuccessfully copy " + source.getName() + " to " + worldFolder);
        } catch (Exception e) {
            Utilities.log("&cFailed to load map from " + source.getName());
            return false;
        }

        this.world = Bukkit.createWorld(new WorldCreator(worldFolder));

        if (this.world != null) {
            ConfigurationSection centerSection = this.config.getConfigurationSection("center");
            if (centerSection != null) {
                this.center = new Location(this.world, centerSection.getDouble("x"), centerSection.getDouble("y"), centerSection.getDouble("z"));
            }

            ConfigurationSection spawnSection = this.config.getConfigurationSection("spawn-points");
            if (spawnSection != null) {
                for (String key : spawnSection.getKeys(false)) {
                    this.spawnedPoint.add(new Location(this.world, spawnSection.getDouble(key + ".x"), spawnSection.getDouble(key + ".y"), spawnSection.getDouble(key + ".z")));
                }
            }

            this.world.setAutoSave(false);
            this.world.setTime(1000);
            this.world.setGameRuleValue("naturalRegeneration", "false");
            this.world.setGameRuleValue("doDaylightCycle", "false");
            this.loaded = true;
            return true;
        }

        return false;
    }

    public boolean vote(Player player) {
        return !votedPlayers.add(player.getUniqueId());
    }

    public int getScore() {
        return this.votedPlayers.size();
    }

    public boolean isReadyToUse() {
        return this.center != null && this.spawnedPoint.size() == plugin.getGameManager().getMaxPlayers();
    }

    public List<Teleport> getTeleportLocation() {
        List<Teleport> locations = new ArrayList<>();
        Random random = new Random();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getGameManager().isPlayer(player) && !this.spawnedPoint.isEmpty()) {
                locations.add(new Teleport(player, this.spawnedPoint.remove(random.nextInt(this.spawnedPoint.size()))));
            }
        }

        return locations;
    }

    public boolean addSpawnPoint(Location location) {
        if (this.deathmatchMap) {
            if (this.spawnedPoint.size() >= plugin.getGameManager().getDeathMatchPlayers()) {
                return false;
            }
        } else {
            if (this.spawnedPoint.size() >= plugin.getGameManager().getMaxPlayers()) {
                return false;
            }
        }

        return this.spawnedPoint.add(location);
    }

    public boolean teleport(Player player) {
        if (this.center == null) {
            player.teleport(new Location(this.world, 0, this.world.getHighestBlockYAt(0, 0) + 20, 0));
            return false;
        } else {
            player.teleport(this.center);
            return true;
        }
    }
}
