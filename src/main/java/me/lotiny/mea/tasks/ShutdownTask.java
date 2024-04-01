package me.lotiny.mea.tasks;

import me.lotiny.mea.Mea;
import me.lotiny.mea.enums.GameState;
import me.lotiny.mea.utils.Tasks;
import me.lotiny.mea.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ShutdownTask extends BukkitRunnable {

    private final Mea plugin;

    private int seconds = 30;

    public ShutdownTask(Mea plugin) {
        this.plugin = plugin;
        this.plugin.getGameManager().setState(GameState.END);
        announceWinner();
        runTaskTimerAsynchronously(this.plugin, 20L, 20L);
    }

    @Override
    public void run() {
        if (seconds == 0) {
            Tasks.run(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer("Game ended.");
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
            });
        }

        if (seconds % 10 == 0 || seconds <= 5 && seconds > 0) {
            Utilities.sendMessage("&cServer restarting in " + seconds + " second(s).");
        }

        --seconds;
    }

    private void announceWinner() {
        Player player = Bukkit.getPlayer(plugin.getGameManager().getPlayers().get(0));
        if (player == null) {
            return;
        }

        Utilities.playSound(Sound.WOLF_HOWL);
        Utilities.sendMessage("&aGame ended! &eWinner: &r" + player.getName());
    }
}
