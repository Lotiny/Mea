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

    private final Set<Location> openedChest = new HashSet<>();
    private final Map<Integer, LootChest> lootChests = new ConcurrentHashMap<>();

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
        LootChest chest = new LootChest(chestTier, plugin.getLootItemsFile().getInt("Loot-Chests.Tier" + chestTier + ".Item-Amount"), plugin.getLootItemsFile().getInt("Loot-Chests.Tier" + chestTier + ".Chance"));
        ConfigurationSection itemSection = plugin.getLootItemsFile().getConfigurationSection("Loot-Items.Chest-Tier" + chestTier);

        if (itemSection == null) {
            Utilities.log("&cPlease setup your Tier" + chestTier + " chest in `loot-items.yml`");
            return;
        }

        for (String key : itemSection.getKeys(false)) {
            ConfigurationSection section = itemSection.getConfigurationSection(key);
            chest.getLootItems().add(new LootItem(section));
        }

        this.lootChests.put(chestTier, chest);
    }

    public void fill(Inventory inventory, int chestTier) {
        inventory.clear();

        Set<Integer> slots = new HashSet<>();
        LootChest lootChest = this.lootChests.get(chestTier);

        int count = 0;
        while (count < lootChest.getItemAmount()) {
            int slot = random.nextInt(inventory.getSize());

            if (!slots.contains(slot)) {
                slots.add(slot);
                count++;

                List<LootItem> items = lootChest.getLootItems();
                LootItem lootItem = items.get(random.nextInt(items.size()));

                inventory.setItem(slot, lootItem.create());
            }
        }
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

        return 0;
    }

    public void markAsOpened(Location location) {
        this.openedChest.add(location);
    }

    public boolean hasBeenOpened(Location location) {
        return this.openedChest.contains(location);
    }

    public void refillChests() {
        this.openedChest.clear();
    }
}
