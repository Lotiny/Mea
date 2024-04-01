package me.lotiny.mea.tasks;

import me.lotiny.mea.Mea;
import me.lotiny.mea.utils.Tasks;
import me.lotiny.mea.utils.Utilities;
import me.lotiny.mea.utils.sit.SitUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class DMTask extends BukkitRunnable {

    private final Mea plugin;

    private int seconds = 10;

    public DMTask(Mea plugin) {
        this.plugin = plugin;
        runTaskTimerAsynchronously(this.plugin, 20L, 20L);
    }

    @Override
    public void run() {
        if (plugin.getGameManager().getPlayersAmount() == 1 ) {
            new ShutdownTask(plugin);
            cancel();
            return;
        }

        if (seconds == 0) {
            Utilities.sendMessage("&eDeathmatch Started!");
            Tasks.run(SitUtil::unsitAll);
        }

        if (seconds > 0) {
            Utilities.sendMessage("&7Deathmatch starts in &e" + seconds + "&7 second(s).");
        }

        --seconds;
    }
}
