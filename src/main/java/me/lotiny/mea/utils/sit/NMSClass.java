package me.lotiny.mea.utils.sit;

import java.lang.reflect.Field;

public class NMSClass {

    public static Class<?> Entity;
    public static Class<?> EntityLiving;
    public static Class<?> EntityInsentient;
    public static Class<?> EntityHorse;
    public static Class<?> World;
    public static Class<?> PacketPlayOutEntityDestroy;
    public static Class<?> ItemStack;
    private static boolean initialized = false;

    static {
        if (!initialized) {
            for (Field f : NMSClass.class.getDeclaredFields()) {
                if (f.getType().equals(Class.class)) {
                    try {
                        f.set(null, Reflection.getNMSClassWithException(f.getName()));
                    } catch (Exception e) {
                        if (f.getName().equals("WatchableObject")) {
                            try {
                                f.set(null, Reflection.getNMSClassWithException("DataWatcher$WatchableObject"));
                            } catch (Exception ignored) {

                            }
                        }
                    }
                }
            }
            initialized = true;
        }
    }
}
