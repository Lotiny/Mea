package me.lotiny.mea.tasks;

import me.lotiny.mea.Mea;
import me.lotiny.mea.assets.GameMap;
import me.lotiny.mea.utils.Tasks;
import me.lotiny.mea.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteTask extends BukkitRunnable {

    private final Mea plugin;

    private int seconds = 60;

    public VoteTask(Mea plugin) {
        this.plugin = plugin;
        runTaskTimerAsynchronously(this.plugin, 20L, 20L);
    }

    @Override
    public void run() {
        if (seconds % 10 == 0 || seconds <= 5) {
            if (seconds == 10 && plugin.getGameManager().getMap() == null) {
                GameMap map = plugin.getMapManager().getHighestVoteMap();
                Utilities.sendMessage("&cVote ended. &a" + map.getName() + " won the vote!");
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                });

                Tasks.run(() -> {
                    if (map.load()) {
                        plugin.getGameManager().setMap(map);
                    } else {
                        cancel();
                    }
                });
            }

            Utilities.sendMessage("&7Start teleport in &e" + seconds + "&7 second(s).");
        }

        --seconds;

        if (seconds == 0) {
            new TeleportTask(plugin, plugin.getGameManager().getMap().getTeleportLocation());
            cancel();
        }
    }
}
