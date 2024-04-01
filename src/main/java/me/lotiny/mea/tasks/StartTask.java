package me.lotiny.mea.tasks;

import me.lotiny.mea.Mea;
import me.lotiny.mea.enums.GameState;
import me.lotiny.mea.utils.Tasks;
import me.lotiny.mea.utils.Utilities;
import me.lotiny.mea.utils.sit.SitUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class StartTask extends BukkitRunnable {

    private final Mea plugin;

    private int seconds = 20;

    public StartTask(Mea plugin) {
        this.plugin = plugin;
        runTaskTimerAsynchronously(this.plugin, 20L, 20L);
    }

    @Override
    public void run() {
        seconds--;

        if (seconds % 10 == 0 || seconds <= 5) {
            Utilities.sendMessage("&7Game starts in &e" + seconds + "&7 second(s).");
        }

        if (seconds == 0) {
            Utilities.sendMessage("&eGame started!");
            Tasks.run(SitUtil::unsitAll);
            plugin.getGameManager().setState(GameState.GAME);
            new GameTask(plugin);
            cancel();
        }
    }
}
