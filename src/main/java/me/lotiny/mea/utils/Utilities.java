package me.lotiny.mea.utils;

import com.google.common.collect.ImmutableSet;
import lombok.experimental.UtilityClass;
import me.lotiny.mea.Mea;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@UtilityClass
public class Utilities {

    private final Mea plugin = Mea.getInstance();

    public void disablePlugin() {
        Bukkit.getScheduler().cancelTasks(plugin);
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + plugin.getDescription().getName() + "] " + CC.translate(message));
    }

    public void sendMessage(String message) {
        Bukkit.broadcastMessage(CC.translate(message));
    }

    public void resetPlayer(Player player, GameMode gameMode) {
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(gameMode);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setExp(0);

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    public ItemStack getGoldenHead() {
        ItemBuilder item = new ItemBuilder(Material.GOLDEN_APPLE);
        item.setName("&6Golden Head");

        return item.toItemStack();
    }

    public void playSound(Sound sound) {
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1, 1));
    }

    public Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();

        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;

        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw (new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e));
        }

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if (className != null) {
                Class<?> clazz = null;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }

        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (ImmutableSet.copyOf(classes));
    }
}
