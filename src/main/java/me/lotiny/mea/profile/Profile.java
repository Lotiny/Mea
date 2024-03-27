package me.lotiny.mea.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.lotiny.mea.Mea;
import me.lotiny.mea.enums.PlayerState;
import me.lotiny.mea.profile.stats.Statistics;
import me.lotiny.mea.utils.HotbarItem;
import me.lotiny.mea.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Profile {

    private final UUID uniqueId;
    private String playerName;

    private Mea plugin = Mea.getInstance();

    private Statistics stats = new Statistics();
    private PlayerState state;

    private int kills = 0;

    private boolean loaded = false;

    public void setState(PlayerState state) {
        this.state = state;

        Player player = Bukkit.getPlayer(this.uniqueId);
        if (state == PlayerState.PLAY) {
            plugin.getGameManager().getPlayers().add(this.uniqueId);
            plugin.getGameManager().getSpectators().remove(this.uniqueId);

            Utilities.resetPlayer(player, GameMode.SURVIVAL);

            Bukkit.getOnlinePlayers().forEach(online -> {
                online.showPlayer(player);
                player.showPlayer(online);
            });
        } else {
            plugin.getGameManager().getSpectators().add(this.uniqueId);
            plugin.getGameManager().getPlayers().remove(this.uniqueId);

            Utilities.resetPlayer(player, GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);

            Bukkit.getOnlinePlayers().forEach(online -> {
                if (plugin.getGameManager().isPlayer(online)) {
                    player.showPlayer(online);
                } else {
                    player.hidePlayer(online);
                }

                online.hidePlayer(player);
            });
        }

        HotbarItem.apply(player);
    }
}
