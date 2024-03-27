package me.lotiny.mea.assets;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

@Getter
public class LootItem {

    private final Material material;
    private final short durability;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    private final int amount;

    public LootItem(ConfigurationSection section) {
        Material material;

        try {
            material = Material.getMaterial(section.getString("Material"));
        } catch (Exception e) {
            material = Material.AIR;
        }

        this.material = material;
        this.durability = (short) section.getInt("Durability");

        ConfigurationSection enchantSection = section.getConfigurationSection("Enchantment");
        if (enchantSection != null) {
            for (String key : enchantSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByName(key);
                if (enchantment != null) {
                    int level = enchantSection.getInt(key);
                    this.enchantments.put(enchantment, level);
                }
            }
        }

        this.amount = section.getInt("Amount");
    }

    public ItemStack create() {
        ItemStack item = new ItemStack(this.material, this.amount, this.durability);

        if (!this.enchantments.isEmpty()) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            this.enchantments.forEach((enchantment, level) -> {
                meta.addStoredEnchant(enchantment, level, true);
            });
            item.setItemMeta(meta);
        }

        return item;
    }
}
