package me.lotiny.mea;

import com.samjakob.spigui.SpiGUI;
import lombok.Getter;
import me.lotiny.mea.commands.handlers.CommandHandler;
import me.lotiny.mea.database.MongoManager;
import me.lotiny.mea.managers.ChestManager;
import me.lotiny.mea.managers.GameManager;
import me.lotiny.mea.managers.MapManager;
import me.lotiny.mea.profile.Profile;
import me.lotiny.mea.profile.ProfileManager;
import me.lotiny.mea.providers.BoardProvider;
import me.lotiny.mea.utils.ConfigFile;
import me.lotiny.mea.utils.Utilities;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public final class Mea extends JavaPlugin {

    @Getter
    private static Mea instance;

    private ConfigFile configFile,
            databaseFile,
            scoreboardFile,
            lootItemsFile;

    private MongoManager mongoManager;
    private ProfileManager profileManager;
    private ChestManager chestManager;
    private MapManager mapManager;
    private GameManager gameManager;

    private SpiGUI spiGUI;

    @Override
    public void onEnable() {
        instance = this;

        loadDependencies();

        this.configFile = new ConfigFile("config.yml");
        this.databaseFile = new ConfigFile("database.yml");
        this.scoreboardFile = new ConfigFile("scoreboard.yml");
        this.lootItemsFile = new ConfigFile("loot-items.yml");

        this.mongoManager = new MongoManager(this);
        this.profileManager = new ProfileManager(this);
        this.chestManager = new ChestManager(this);
        this.gameManager = new GameManager(this);
        this.mapManager = new MapManager(this);

        this.spiGUI = new SpiGUI(this);

        Utilities.getClassesInPackage(this, "me.lotiny.mea.listeners").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), this);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        new CommandHandler(this);
        new BoardProvider(this);

        registerRecipes();
    }

    @Override
    public void onDisable() {
        try {
            for (Profile profile : profileManager.getAllData()) {
                profileManager.saveData(profile);
            }

            mongoManager.disconnect();
        } catch (NullPointerException ignore) {
            // Ignore when some manager doesn't get init.
        }
    }

    private void loadDependencies() {
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);
        libraryManager.addJitPack();
        libraryManager.addMavenCentral();

        Arrays.asList(
                Library.builder().groupId("com{}fasterxml{}jackson{}core").artifactId("jackson-core").version("2.16.1").build(),
                Library.builder().groupId("com{}fasterxml{}jackson{}core").artifactId("jackson-databind").version("2.16.1").build(),
                Library.builder().groupId("com{}fasterxml{}jackson{}core").artifactId("jackson-annotations").version("2.16.1").build(),
                Library.builder().groupId("org{}mongodb").artifactId("mongo-java-driver").version("3.12.14").build(),
                Library.builder().groupId("fr{}mrmicky").artifactId("fastboard").version("2.1.0").build(),
                Library.builder().groupId("com{}github{}Revxrsal{}Lamp").artifactId("common").version("3.1.9").build(),
                Library.builder().groupId("com{}github{}Revxrsal{}Lamp").artifactId("bukkit").version("3.1.9").build(),
                Library.builder().groupId("com{}samjakob").artifactId("SpiGUI").version("v1.3.1").build()
        ).forEach(libraryManager::loadLibrary);
    }

    private void registerRecipes() {
        ShapedRecipe recipe = new ShapedRecipe(Utilities.getGoldenHead());
        recipe.shape("AAA", "ABA", "AAA");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.SKULL_ITEM);
        this.getServer().addRecipe(recipe);
    }
}
