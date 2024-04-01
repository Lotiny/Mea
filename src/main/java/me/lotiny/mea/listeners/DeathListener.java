package me.lotiny.mea.listeners;

import me.lotiny.mea.Mea;
import me.lotiny.mea.enums.PlayerState;
import me.lotiny.mea.profile.Profile;
import me.lotiny.mea.utils.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final Mea plugin = Mea.getInstance();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            Profile killerProfile = plugin.getProfileManager().getProfile(killer.getUniqueId());

            killerProfile.getStats().getKills().increase();
        }

        player.getWorld().dropItemNaturally(player.getLocation().add(0.5, 0.5, 0.5), Utilities.getGoldenHead());
        profile.getStats().getDeaths().increase();
        profile.setState(PlayerState.SPEC);
    }
}
