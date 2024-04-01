package me.lotiny.mea.providers;

import fr.mrmicky.fastboard.FastBoard;
import me.lotiny.mea.Mea;
import me.lotiny.mea.profile.Profile;
import me.lotiny.mea.utils.CC;
import me.lotiny.mea.utils.Replacement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class BoardProvider extends Thread implements Listener {

    private final Mea plugin;
    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public BoardProvider(Mea plugin) {
        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (FastBoard board : this.boards.values()) {
                    Player player = board.getPlayer();

                    board.updateLines(getLines(player));
                }

                sleep(5 * 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FastBoard board = new FastBoard(player);

        board.updateTitle(CC.translate(plugin.getScoreboardFile().getString("scoreboard.title")));
        this.boards.put(player.getUniqueId(), board);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FastBoard board = this.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    private List<String> getLines(Player player) {
        List<String> toReturn = new ArrayList<>();
        List<String> lines;
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (plugin.getGameManager().isInGameState()) {
            lines = plugin.getScoreboardFile().getStringList("scoreboard.game");
        } else {
            lines = plugin.getScoreboardFile().getStringList("scoreboard.lobby");
        }

        for (String line : lines) {
            toReturn.add(formatLine(line, profile));
        }

        return toReturn;
    }

    private String formatLine(String line, Profile profile) {
        Replacement replacement = new Replacement(line);

        replacement.add("<player>", profile.getPlayerName());
        replacement.add("<remaining>", plugin.getGameManager().getPlayersAmount());
        replacement.add("<max_players>", plugin.getGameManager().getMaxPlayers());

        if (plugin.getGameManager().isInGameState()) {
            replacement.add("<spectators>", plugin.getGameManager().getSpectatorsAmount());
            replacement.add("<kills>", profile.getKills());
        }

        return replacement.toString();
    }
}
