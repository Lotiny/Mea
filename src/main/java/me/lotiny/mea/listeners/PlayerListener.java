package me.lotiny.mea.listeners;

import me.lotiny.mea.Mea;
import me.lotiny.mea.enums.GameState;
import me.lotiny.mea.enums.PlayerState;
import me.lotiny.mea.profile.Profile;
import me.lotiny.mea.tasks.VoteTask;
import me.lotiny.mea.utils.CC;
import me.lotiny.mea.utils.HotbarItem;
import me.lotiny.mea.utils.menus.VoteMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    private final Mea plugin = Mea.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        event.setJoinMessage(null);

        if (plugin.getGameManager().isInGameState() ||  plugin.getGameManager().getState() == GameState.START) {
            profile.setState(PlayerState.SPEC);
            player.teleport(plugin.getGameManager().getMap().getCenter());
        } else {
            profile.setState(PlayerState.PLAY);
            player.teleport(plugin.getGameManager().getSpawnLocation());

            if (plugin.getGameManager().getState() == GameState.WAIT) {
                if (plugin.getGameManager().getPlayersAmount() == plugin.getGameManager().getMinPlayers()) {
                    plugin.getGameManager().setState(GameState.VOTE);
                    Bukkit.getOnlinePlayers().forEach(HotbarItem::apply);
                    new VoteTask(plugin);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        event.setQuitMessage(null);

        if (plugin.getGameManager().isInGameState()) {
            if (plugin.getGameManager().isPlayer(player)) {
                player.setHealth(0);
            }
        }

        plugin.getGameManager().getPlayers().remove(player.getUniqueId());
        plugin.getGameManager().getSpectators().remove(player.getUniqueId());
        plugin.getProfileManager().removeProfile(profile);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!plugin.getGameManager().isInGameState()) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!plugin.getGameManager().isPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!plugin.getGameManager().isInGameState()) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        if (!plugin.getGameManager().isPlayer(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!plugin.getGameManager().isInGameState()) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        if (!plugin.getGameManager().isPlayer(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePlayerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() != Material.GOLDEN_APPLE || item.getItemMeta().getDisplayName() == null) {
            return;
        }

        if (item.getItemMeta().getDisplayName().equals(CC.translate("&6Golden Head"))) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (plugin.getGameManager().getState() == GameState.VOTE) {
            if (event.getAction().toString().startsWith("RIGHT_")) {
                Player player = event.getPlayer();
                ItemStack item = player.getItemInHand();
                if (item == null) {
                    return;
                }

                if (item.getType() == Material.PAPER) {
                    VoteMenu.open(player);
                }
            }
        }
    }
}
