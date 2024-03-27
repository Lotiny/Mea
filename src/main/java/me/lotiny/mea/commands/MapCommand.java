package me.lotiny.mea.commands;

import me.lotiny.mea.Mea;
import me.lotiny.mea.assets.GameMap;
import me.lotiny.mea.utils.CC;
import me.lotiny.mea.utils.Clickable;
import me.lotiny.mea.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;
import java.io.IOException;

@Command("map") @CommandPermission("mea.command.map")
public class MapCommand {

    @Dependency
    private Mea plugin;

    @DefaultFor("map")
    public void getHelp(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c/map list - show all maps"));
        player.sendMessage(CC.translate("&c/map teleport <mapId> - teleport to the map"));
        player.sendMessage(CC.translate("&c/map load <mapId> - load the map"));
        player.sendMessage(CC.translate("&c/map setcenter <mapId> - set the center location"));
        player.sendMessage(CC.translate("&c/map addspawn <mapId> - add spawn point"));
        player.sendMessage(CC.translate("&c/map save <mapId> - save map change"));
        player.sendMessage(CC.translate("&c*REMINDER - for deathmatch map id using 999"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Subcommand("list")
    public void listMap(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        plugin.getMapManager().getMaps().values().forEach(map -> {
            if (map.isLoaded()) {
                if (map.isReadyToUse()) {
                    new Clickable("&b" + map.getId() + ") &a" + map.getName(), "Click to teleport to map", "/map teleport " + map.getId())
                            .sendToPlayer(player);
                } else {
                    new Clickable("&b" + map.getId() + ") &e" + map.getName(), "Click to load the map", "/map teleport " + map.getId())
                            .sendToPlayer(player);
                }
            } else {
                new Clickable("&b" + map.getId() + ") &c" + map.getName(), "Click to load the map", "/map load " + map.getId())
                        .sendToPlayer(player);
            }
        });
        player.sendMessage(CC.CHAT_BAR);
    }

    @Subcommand("teleport") @Usage("map teleport <mapId>")
    public void teleportMap(Player player, @Range(min = 1) int mapId) {
        GameMap map = mapId == 999 ? plugin.getGameManager().getDeathMatchMap() : plugin.getMapManager().getMapById(mapId);
        if (map == null) {
            player.sendMessage(CC.translate("&cMap not found. Use `/map list` to see all available maps!"));
            return;
        }

        if (!map.isLoaded()) {
            player.sendMessage(CC.translate("&cFailed to teleport to this map. Please load the map by using `/map load <mapId>` first."));
            return;
        }

        if (!map.teleport(player)) {
            player.sendMessage(CC.translate("&cFailed to find location to teleport. Use `/map setcenter <mapId>` to set the location to teleport."));
        }
    }

    @Subcommand("load") @Usage("map load <mapId>")
    public void loadMap(Player player, @Range(min = 1) int mapId) {
        GameMap map = mapId == 999 ? plugin.getGameManager().getDeathMatchMap() : plugin.getMapManager().getMapById(mapId);
        if (map == null) {
            player.sendMessage(CC.translate("&cMap not found. Use `/map list` to see all available maps!"));
            return;
        }

        if (!map.load()) {
            player.sendMessage(CC.translate("&cFailed to load map: " + map.getName()));
            return;
        }

        player.sendMessage(CC.translate("&aSuccessfully load map: " + map.getName() + "!"));
        new Clickable("&7Click to teleport to map world. &e[Click]", "Teleport", "/map teleport " + map.getId()).sendToPlayer(player);
    }

    @Subcommand("setcenter") @Usage("map setcenter <mapId>")
    public void setCenterMap(Player player, @Range(min = 1) int mapId) {
        GameMap map = mapId == 999 ? plugin.getGameManager().getDeathMatchMap() : plugin.getMapManager().getMapById(mapId);
        if (map == null) {
            player.sendMessage(CC.translate("&cMap not found. Use `/map list` to see all available maps!"));
            return;
        }

        if (!map.isLoaded()) {
            player.sendMessage(CC.translate("&cMap is not loaded yet. Please load the map by using `/map load <mapId>`"));
            return;
        }

        if (player.getWorld() != map.getWorld()) {
            player.sendMessage(CC.translate("&cYou're not in the right world."));
            return;
        }

        map.setCenter(player.getLocation());
        player.sendMessage(CC.translate("&eSet your location to center."));
    }

    @Subcommand("addspawn") @Usage("map addspawn <mapId>")
    public void addSpawnMap(Player player, @Range(min = 1) int mapId) {
        GameMap map = mapId == 999 ? plugin.getGameManager().getDeathMatchMap() : plugin.getMapManager().getMapById(mapId);
        if (map == null) {
            player.sendMessage(CC.translate("&cMap not found. Use `/map list` to see all available maps!"));
            return;
        }

        if (!map.isLoaded()) {
            player.sendMessage(CC.translate("&cMap is not loaded yet. Please load the map by using `/map load <mapId>`"));
            return;
        }

        if (player.getWorld() != map.getWorld()) {
            player.sendMessage(CC.translate("&cYou're not in the right world."));
            return;
        }

        if (!map.addSpawnPoint(player.getLocation())) {
            player.sendMessage(CC.translate("&cCannot add this location to spawn locations."));
            return;
        }

        player.sendMessage(CC.translate("&aLocation #" + map.getSpawnedPoint().size() + " has been set!"));
        player.sendMessage(CC.translate("&cThere are &4&l"  + (map.isDeathmatchMap() ? (plugin.getGameManager().getDeathMatchPlayers() - map.getSpawnedPoint().size()) : (plugin.getGameManager().getMaxPlayers() - map.getSpawnedPoint().size())) + "&c more spawn point to set."));
    }

    @Subcommand("save") @Usage("map save <mapId>")
    public void saveMap(Player player, @Range(min = 1) int mapId) {
        GameMap map = mapId == 999 ? plugin.getGameManager().getDeathMatchMap() : plugin.getMapManager().getMapById(mapId);
        if (map == null) {
            player.sendMessage(CC.translate("&cMap not found. Use `/map list` to see all available maps!"));
            return;
        }

        World world = map.getWorld();
        if (player.getWorld() == world) {
            player.teleport(plugin.getGameManager().getSpawnLocation());
        }

        Bukkit.unloadWorld(world, true);

        try {
            FileUtils.delete(map.getSource());
            FileUtils.copy(world.getWorldFolder(), new File(plugin.getMapManager().getFolder(), map.getSource().getName()));
            FileUtils.delete(world.getWorldFolder());
        } catch (IOException e) {
            player.sendMessage(CC.translate("&cFailed to copy the world file."));
        }

        plugin.getMapManager().createData(map);

        if (map.getCenter() != null) {
            map.getConfig().set("center.x", map.getCenter().getX());
            map.getConfig().set("center.y", map.getCenter().getY());
            map.getConfig().set("center.z", map.getCenter().getZ());
        }

        if (!map.getSpawnedPoint().isEmpty()) {
            for (int i = 0; i < map.getSpawnedPoint().size(); i++) {
                Location location = map.getSpawnedPoint().get(i);
                map.getConfig().set("spawn-points." + i + ".x", location.getX());
                map.getConfig().set("spawn-points." + i + ".y", location.getY());
                map.getConfig().set("spawn-points." + i + ".z", location.getZ());
            }
        }

        map.getConfig().save();
        player.sendMessage(CC.translate("&aSaved map: " + map.getName() + "!"));
    }
}
