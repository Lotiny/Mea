package me.lotiny.mea.listeners;

import me.lotiny.mea.Mea;
import me.lotiny.mea.managers.ChestManager;
import me.lotiny.mea.profile.Profile;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
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

            String title = chest.getBlockInventory().getTitle();
            if (title.startsWith("Tier")) {
                chestManager.markAsOpened(chest.getLocation());
                chestManager.fill(chest.getBlockInventory(), Integer.parseInt(title.split(" ")[1]));
                return;
            }

            int chestTier = chest.getWorld() == plugin.getGameManager().getDeathMatchMap().getWorld() ? 3 : chestManager.randomChestTier();
            World nmsWorld = ((CraftWorld) chest.getWorld()).getHandle();
            TileEntity te = nmsWorld.getTileEntity(new BlockPosition(chest.getLocation().getBlockX(),
                    chest.getLocation().getBlockY(),
                    chest.getLocation().getBlockZ()));
            ((TileEntityChest) te).a("Tier " + chestTier);

            chestManager.markAsOpened(chest.getLocation());
            chestManager.fill(chest.getBlockInventory(), chestTier);
            profile.getStats().getChestOpened().increase();
        } else if (holder instanceof DoubleChest) {
            DoubleChest chest = (DoubleChest) holder;
            if (plugin.getChestManager().hasBeenOpened(chest.getLocation())) {
                return;
            }

            String title = chest.getInventory().getTitle();
            if (title.startsWith("Tier")) {
                chestManager.markAsOpened(chest.getLocation());
                chestManager.fill(chest.getInventory(), Integer.parseInt(title.split(" ")[1]));
                return;
            }

            int chestTier = chest.getWorld() == plugin.getGameManager().getDeathMatchMap().getWorld() ? 3 : chestManager.randomChestTier();
            World nmsWorld = ((CraftWorld) chest.getWorld()).getHandle();
            TileEntity te = nmsWorld.getTileEntity(new BlockPosition(chest.getLocation().getBlockX(),
                    chest.getLocation().getBlockY(),
                    chest.getLocation().getBlockZ()));
            ((TileEntityChest) te).a("Tier " + chestTier);

            chestManager.markAsOpened(chest.getLocation());
            chestManager.fill(chest.getInventory(), chestTier);
            profile.getStats().getChestOpened().increase();
        }
    }
}
