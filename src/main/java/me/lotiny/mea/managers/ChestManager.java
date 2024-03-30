package me.lotiny.mea.managers;

import lombok.Getter;
import me.lotiny.mea.Mea;
import me.lotiny.mea.assets.LootChest;
import me.lotiny.mea.assets.LootItem;
import me.lotiny.mea.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ChestManager {

    private final Mea plugin;
    private final Random random;

    private final Map<Location, Boolean> openedChest = new ConcurrentHashMap<>();
    private final Map<Integer, LootChest> lootChests = new ConcurrentHashMap<>();
    private final Map<Integer, List<LootItem>> lootItems = new ConcurrentHashMap<>();

    private final int refillTime;

    public ChestManager(Mea plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.refillTime = this.plugin.getConfigFile().getInt("chest-refill-time");
        setupLootItems();
    }

    public void setupLootItems() {
        ConfigurationSection section = plugin.getLootItemsFile().getConfigurationSection("Loot-Chests");

        if (section == null) {
            Utilities.log("&cPlease setup your Loot-Chests in `loot-items.yml`");
            return;
        }

        int totalChance = 0;
        for (String key : section.getKeys(false)) {
            int chance = section.getInt(key + ".Chance");
            if (chance > 0) {
                int tier = Integer.parseInt(key.replaceAll("\\D", ""));
                totalChance += chance;
                setupLootItem(tier);
            }
        }

        if (totalChance != 100) {
            Utilities.log("&cThe Loot-Chests chance need to combine to 100 please setup again.");
            this.lootChests.clear();
        }
    }

    public void setupLootItem(int chestTier) {
        LootChest chest = new LootChest(chestTier, plugin.getLootItemsFile().getInt("Loot-Chests.Tier" + chestTier + ".Chance"));
        ConfigurationSection itemSection = plugin.getLootItemsFile().getConfigurationSection("Loot-Items.Chest-Tier" + chestTier);

        if (itemSection == null) {
            Utilities.log("&cPlease setup your Tier" + chestTier + " chest in `loot-items.yml`");
            return;
        }

        List<LootItem> items = new ArrayList<>();
        for (String key : itemSection.getKeys(false)) {
            ConfigurationSection section = itemSection.getConfigurationSection(key);
            items.add(new LootItem(section));
        }
        lootItems.put(chestTier, items);

        ConfigurationSection chestSection = plugin.getLootItemsFile().getConfigurationSection("Loot-Chests.Tier" + chestTier + ".Item-Amount");
        if (chestSection == null) {
            Utilities.log("&cPlease setup your Tier" + chestTier + " chest in `loot-items.yml`");
            return;
        }

        for (String key : chestSection.getKeys(false)) {
            int tier = Integer.parseInt(key.replaceAll("\\D", ""));
            chest.getItems().put(tier, chestSection.getInt(key));
        }

        this.lootChests.put(chestTier, chest);
    }

    public void fill(Inventory inventory, int chestTier) {
        inventory.clear();

        Set<Integer> slots = new HashSet<>();
        LootChest lootChest = this.lootChests.get(chestTier);

        lootChest.getItems().forEach((itemTier, itemAmount) -> {
            for (int i = 0; i < itemAmount; i++) {
                int slot = random.nextInt(inventory.getSize());

                if (!slots.contains(slot)) {
                    slots.add(slot);

                    List<LootItem> items = lootItems.get(itemTier);
                    LootItem item = items.get(random.nextInt(items.size()));

                    inventory.setItem(slot, item.create());
                }
            }
        });
    }

    public int randomChestTier() {
        int chance = 0;
        int random = this.random.nextInt(100);
        for (LootChest lootChest : this.lootChests.values()) {
            chance += lootChest.getChance();
            if (random < chance) {
                return lootChest.getTier();
            }
        }

        return 1;
    }

    public void markAsOpened(Location location) {
        this.openedChest.put(location, true);
    }

    public boolean hasBeenOpened(Location location) {
        if (this.openedChest.containsKey(location)) {
            return this.openedChest.get(location);
        }

        return false;
    }

    public void refillChests() {
        this.openedChest.forEach((location, bool) -> {
            if (bool) {
                this.openedChest.replace(location, false);
            }
        });
    }
}
