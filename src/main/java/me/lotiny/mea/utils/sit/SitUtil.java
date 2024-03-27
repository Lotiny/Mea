package me.lotiny.mea.utils.sit;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class SitUtil {

    private final Map<UUID, Object> frozenPlayers = new ConcurrentHashMap<>();

    public void sitPlayer(Player player) {
        if (!frozenPlayers.containsKey(player.getUniqueId())) {
            player.setAllowFlight(true);
            Location playerLocation = player.getLocation();

            try {
                Object world = Reflection.getHandle(playerLocation.getWorld());
                Object entityHorse = buildEntityHorse(world, playerLocation);
                Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                Object id = NMSClass.Entity.getDeclaredMethod("getId").invoke(entityHorse);
                Constructor<?> firstConstructor = ((Class) Objects.requireNonNull(Reflection.getNMSClass("PacketPlayOutSpawnEntityLiving"))).getConstructor(NMSClass.EntityLiving);
                Object firstPacket = firstConstructor.newInstance(entityHorse);
                Reflection.sendPacket(player, firstPacket);
                Constructor<?> secondConstructor = ((Class) Objects.requireNonNull(Reflection.getNMSClass("PacketPlayOutAttachEntity"))).getConstructor(Integer.TYPE, NMSClass.Entity, NMSClass.Entity);
                Object secondPacket = secondConstructor.newInstance(0, playerHandle, entityHorse);
                Reflection.sendPacket(player, secondPacket);

                frozenPlayers.put(player.getUniqueId(), id);
            } catch (Exception ignored) {
            }
        }
    }

    public void unsitPlayer(Player player) {
        if (frozenPlayers.containsKey(player.getUniqueId())) {
            player.setAllowFlight(false);

            try {
                Object packet = NMSClass.PacketPlayOutEntityDestroy.getDeclaredConstructor(int[].class).newInstance((Object) new int[]{Integer.parseInt(frozenPlayers.get(player.getUniqueId()).toString())});
                Reflection.sendPacket(player, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }

            frozenPlayers.remove(player.getUniqueId());
        }
    }

    public void unsitAll() {
        Bukkit.getOnlinePlayers().forEach(SitUtil::unsitPlayer);
    }

    private Object buildEntityHorse(Object world, Location loc) throws Exception {
        Object horse = NMSClass.EntityHorse.getConstructor(NMSClass.World).newInstance(world);
        NMSClass.Entity.getDeclaredField("locX").set(horse, loc.getX());
        NMSClass.Entity.getDeclaredField("locY").set(horse, loc.getY());
        NMSClass.Entity.getDeclaredField("locZ").set(horse, loc.getZ());

        if (Reflection.getVersion().contains("1_8")) {
            NMSClass.Entity.getDeclaredMethod("setInvisible", Boolean.TYPE).invoke(horse, true);
            NMSClass.Entity.getDeclaredMethod("setCustomNameVisible", Boolean.TYPE).invoke(horse, false);
        } else {
            NMSClass.Entity.getDeclaredMethod("setInvisible", Boolean.TYPE).invoke(horse, true);
            NMSClass.EntityInsentient.getDeclaredMethod("setCustomNameVisible", Boolean.TYPE).invoke(horse, false);
        }

        return horse;
    }
}
