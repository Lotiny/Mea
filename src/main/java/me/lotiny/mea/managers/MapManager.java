package me.lotiny.mea.managers;

import lombok.Getter;
import me.lotiny.mea.Mea;
import me.lotiny.mea.assets.GameMap;
import me.lotiny.mea.utils.CC;
import me.lotiny.mea.utils.ConfigFile;
import me.lotiny.mea.utils.LocationSerialization;
import me.lotiny.mea.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MapManager {

    private final Mea plugin;
    private final File folder;

    private final Map<Integer, GameMap> maps = new ConcurrentHashMap<>();

    public MapManager(Mea plugin) {
        this.plugin = plugin;
        this.folder = new File(this.plugin.getDataFolder(), "worlds");
        if (!this.folder.exists()) {
            this.folder.mkdir();
        }

        String[] files = this.folder.list();
        if (files == null || files.length == 0) {
            Utilities.log("&cThere is no world to play yet. Please setup it.");
            return;
        }

        int id = 1;
        for (String file : files) {
            GameMap map = new GameMap(id, file, new File(this.folder, file));
            createData(map);

            if (file.equalsIgnoreCase(this.plugin.getConfigFile().getString("deathmatch.map"))) {
                this.plugin.getGameManager().setDeathMatchMap(map);
                map.setDeathmatchMap(true);
                map.load();
                return;
            }

            this.maps.put(id, map);
            id++;
        }

        if (this.plugin.getGameManager().getDeathMatchMap() == null) {
            Utilities.log("&cCannot found deathmatch map, disabling the plugin...");
            Utilities.disablePlugin();
        }
    }

    public void voteMap(Player player, int mapId) {
        GameMap map = getMapById(mapId);
        if (map == null) {
            return;
        }

        if (map.vote(player)) {
            player.sendMessage(CC.translate("&aYou have vote to map: " + map.getName()));
        } else {
            map.getVotedPlayers().remove(player.getUniqueId());
            player.sendMessage(CC.translate("&cYou have un-vote to map: " + map.getName()));
        }
    }

    public GameMap getHighestVoteMap() {
        GameMap map = null;
        for (GameMap maps : this.maps.values()) {
            if (map == null || map.getScore() < maps.getScore()) {
                map = maps;
            }
        }

        return map;
    }

    public String convertLocation(Location location) {
        for (GameMap map : this.maps.values()) {
            if (map.getWorld() == location.getWorld()) {
                return LocationSerialization.serialize(location).replace(location.getWorld().getName(), map.getSource().getName());
            }
        }

        return null;
    }

    public void createData(GameMap map) {
        ConfigFile configFile = new ConfigFile("worlds/" + map.getName(), "data.yml");
        map.setConfig(configFile);
    }

    public GameMap getMapById(int id) {
        return this.maps.get(id);
    }
}
