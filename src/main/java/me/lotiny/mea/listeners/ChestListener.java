package me.lotiny.mea.listeners;

import me.lotiny.mea.Mea;
import me.lotiny.mea.managers.ChestManager;
import me.lotiny.mea.profile.Profile;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public class ChestListener implements Listener {

    private final Mea plugin = Mea.getInstance();

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        InventoryHolder holder = event.getInventory().getHolder();
        ChestManager chestManager = plugin.getChestManager();

        if (holder instanceof Chest) {
            Chest chest = (Chest) holder;
            if (chestManager.hasBeenOpened(chest.getLocation())) {
                return;
            }
            int tier = plugin.getChestManager().randomChestTier();

            chestManager.markAsOpened(chest.getLocation());
            chestManager.fill(chest.getBlockInventory(), tier);
            profile.getStats().getChestOpened().increase();
        } else if (holder instanceof DoubleChest) {
            DoubleChest chest = (DoubleChest) holder;
            if (plugin.getChestManager().hasBeenOpened(chest.getLocation())) {
                return;
            }
            int tier = plugin.getChestManager().randomChestTier();

            chestManager.markAsOpened(chest.getLocation());
            chestManager.fill(chest.getInventory(), tier);
            profile.getStats().getChestOpened().increase();
        }
    }
}
