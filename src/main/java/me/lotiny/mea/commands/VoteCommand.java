package me.lotiny.mea.commands;

import me.lotiny.mea.Mea;
import me.lotiny.mea.enums.GameState;
import me.lotiny.mea.utils.CC;
import me.lotiny.mea.utils.menus.VoteMenu;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;

public class VoteCommand {

    @Dependency
    private Mea plugin;

    @Command("vote")
    public void execute(Player player) {
        if (plugin.getGameManager().getMap() != null) {
            player.sendMessage(CC.translate("&cThe map have already chosen!"));
        } else if (plugin.getGameManager().getState() == GameState.VOTE) {
            VoteMenu.open(player);
        } else {
            player.sendMessage(CC.translate("&cThe vote is not open yet."));
        }
    }
}
