package me.lotiny.mea.tasks;

import me.lotiny.mea.Mea;
import me.lotiny.mea.assets.Teleport;
import me.lotiny.mea.enums.GameState;
import me.lotiny.mea.profile.Profile;
import me.lotiny.mea.utils.Tasks;
import me.lotiny.mea.utils.Utilities;
import me.lotiny.mea.utils.sit.SitUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TeleportTask extends BukkitRunnable {

    private final Mea plugin;
    private final List<Teleport> teleports;

    public TeleportTask(Mea plugin, List<Teleport> teleports) {
        this.plugin = plugin;
        this.teleports = teleports;
        runTaskTimerAsynchronously(this.plugin, 10L, 10L);
    }

    @Override
    public void run() {
        if (!teleports.isEmpty()) {
            Teleport tp = teleports.remove(0);
            Player player = tp.getPlayer();

            if (player != null) {
                Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
                Tasks.run(() -> {
                    player.teleport(tp.getLocation());
                    SitUtil.sitPlayer(player);
                });

                player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                profile.getStats().getGamePlayed().increase();
            }
        } else {
            Utilities.sendMessage("&aTeleported all players successfully!");
            new StartTask(plugin);
            plugin.getGameManager().setState(GameState.START);
            cancel();
        }
    }
}
