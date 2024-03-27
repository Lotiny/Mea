package me.lotiny.mea.tasks;

import me.lotiny.mea.Mea;
import me.lotiny.mea.enums.GameState;
import me.lotiny.mea.utils.CC;
import me.lotiny.mea.utils.Tasks;
import me.lotiny.mea.utils.Utilities;
import me.lotiny.mea.utils.sit.SitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private final Mea plugin;

    private int seconds = 0;
    private int dm = 30;

    public GameTask(Mea plugin) {
        this.plugin = plugin;
        runTaskTimerAsynchronously(this.plugin, 20L, 20L);
    }

    @Override
    public void run() {
        if (plugin.getGameManager().getPlayersAmount() <= 1) {
            new ShutdownTask(plugin);
            cancel();
            return;
        }

        if (plugin.getGameManager().getPlayersAmount() <= plugin.getGameManager().getDeathMatchPlayers()) {
            if (dm % 10 == 0 || dm <= 5) {
                Utilities.sendMessage("&7Death match starts in &e" + dm + "&7 second(s).");
            }

            --dm;

            if (dm == 0) {
                plugin.getGameManager().getDeathMatchMap().getTeleportLocation().forEach(teleport -> {
                    Player player = teleport.getPlayer();
                    Tasks.run(() -> {
                        player.teleport(teleport.getLocation());
                        SitUtil.sitPlayer(player);
                    });
                });

                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (!plugin.getGameManager().isPlayer(player)) {
                        player.teleport(plugin.getGameManager().getDeathMatchMap().getCenter());
                    }
                });

                new DMTask(plugin);
                plugin.getGameManager().setState(GameState.DM);
                cancel();
            }
        }

        ++seconds;

        if (seconds == plugin.getChestManager().getRefillTime()) {
            plugin.getChestManager().refillChests();
            Utilities.sendMessage(CC.translate("&eAll chests have been refilled!"));
            Utilities.playSound(Sound.CHEST_OPEN);
        }
    }
}
