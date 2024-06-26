package me.lotiny.mea.utils.menus;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import lombok.experimental.UtilityClass;
import me.lotiny.mea.Mea;
import me.lotiny.mea.assets.GameMap;
import me.lotiny.mea.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class VoteMenu {

    private final Mea plugin = Mea.getInstance();

    public void open(Player player) {
        SGMenu menu = plugin.getSpiGUI().create("Vote", (int) Math.ceil((double) plugin.getMapManager().getMaps().size() / 9));
        for (GameMap map : plugin.getMapManager().getMaps().values()) {
            if (map.isDeathmatchMap()) {
                return;
            }

            menu.addButton(new SGButton(getMapSlot(player, map))
                    .withListener((InventoryClickEvent event) -> {
                        plugin.getMapManager().voteMap(player, map.getId());
                        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                        player.closeInventory();
                    }));
        }

        player.openInventory(menu.getInventory());
    }

    private ItemStack getMapSlot(Player player, GameMap map) {
        ItemBuilder item = new ItemBuilder(Material.PAPER);
        item.setName("&e" + map.getName() + "&7: &a" + map.getScore());
        if (map.getVotedPlayers().contains(player.getUniqueId())) {
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            item.addLoreLine("&a* Voted!");
        }

        return item.toItemStack();
    }
}
