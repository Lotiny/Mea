package me.lotiny.mea.managers;

import lombok.Getter;
import lombok.Setter;
import me.lotiny.mea.Mea;
import me.lotiny.mea.assets.GameMap;
import me.lotiny.mea.enums.GameState;
import me.lotiny.mea.utils.LocationSerialization;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GameManager {

    private final Mea plugin;

    private List<UUID> players = new ArrayList<>();
    private List<UUID> spectators = new ArrayList<>();
    private List<Material> breakableBlocks = new ArrayList<>();

    private int minPlayers, maxPlayers, deathMatchPlayers;

    private GameState state;
    private GameMap map, deathMatchMap;
    private Location spawnLocation;

    private String blockMeta;

    public GameManager(Mea plugin) {
        this.plugin = plugin;
        this.minPlayers = this.plugin.getConfigFile().getInt("settings.min-players");
        this.maxPlayers = this.plugin.getConfigFile().getInt("settings.max-players");
        this.deathMatchPlayers = this.plugin.getConfigFile().getInt("deathmatch.target-players");
        this.state = GameState.WAIT;
        this.spawnLocation = LocationSerialization.deserialize(this.plugin.getConfigFile().getString("settings.main-spawn-location"));
        this.blockMeta = "Gk09Ffj327fFAWEncw";

        plugin.getConfigFile().getStringList("breakable-blocks").forEach(s -> {
            Material material = Material.getMaterial(s);
            if (material == null) {
                return;
            }

            this.breakableBlocks.add(material);
        });
    }

    public boolean isPlayer(Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public int getPlayersAmount() {
        return this.players.size();
    }

    public int getSpectatorsAmount() {
        return this.spectators.size();
    }

    public boolean isInGameState() {
        return this.state == GameState.GAME || this.state == GameState.DM || this.state == GameState.END;
    }
}
