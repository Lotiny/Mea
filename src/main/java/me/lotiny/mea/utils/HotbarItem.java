package me.lotiny.mea.utils;

import lombok.experimental.UtilityClass;
import me.lotiny.mea.Mea;
import me.lotiny.mea.enums.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

@UtilityClass
public class HotbarItem {

    private final Mea plugin = Mea.getInstance();

    public void apply(Player player) {
        PlayerInventory inventory = player.getInventory();

        if (plugin.getGameManager().getState() == GameState.VOTE) {
            inventory.addItem(new ItemBuilder(Material.PAPER)
                    .setName("&aVote")
                    .toItemStack());
        } else if (!plugin.getGameManager().isPlayer(player)) {
            inventory.addItem(new ItemBuilder(Material.COMPASS)
                    .setName("&aAlive Players")
                    .toItemStack());
        }
    }
}
