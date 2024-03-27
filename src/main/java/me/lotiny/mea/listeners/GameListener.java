package me.lotiny.mea.listeners;

import me.lotiny.mea.Mea;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class GameListener implements Listener {

    private final Mea plugin = Mea.getInstance();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (plugin.getGameManager().isPlayer(player) && plugin.getGameManager().isInGameState()) {
            event.getBlock().setMetadata(plugin.getGameManager().getBlockMeta(), new FixedMetadataValue(plugin, true));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Block block = event.getBlock();
        if (plugin.getGameManager().getBreakableBlocks().contains(block.getType()) || block.getType() == Material.WEB) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block.hasMetadata(plugin.getGameManager().getBlockMeta())) {
                if (block.getType() == Material.WEB) {
                    return;
                }

                block.breakNaturally();
            }
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (result.getType() == Material.GOLDEN_APPLE && result.getDurability() == 1) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
