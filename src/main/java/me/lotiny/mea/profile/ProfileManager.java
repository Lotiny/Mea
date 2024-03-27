package me.lotiny.mea.profile;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lotiny.mea.Mea;
import me.lotiny.mea.utils.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
public class ProfileManager {

    private final Mea plugin;

    private final Map<UUID, Profile> profiles = new ConcurrentHashMap<>();

    public void createProfile(Player player) {
        Profile profile = new Profile(player.getUniqueId());
        profile.setPlayerName(player.getName());

        this.profiles.put(profile.getUniqueId(), profile);

        Tasks.runAsync(() -> loadData(profile));
    }

    public void loadData(Profile profile) {
        Document document = plugin.getMongoManager().getCollection().find(Filters.eq("uuid", profile.getUniqueId().toString())).first();
        if (document == null) {
            Tasks.runAsync(() -> {
                profile.setLoaded(true);
                this.saveData(profile);
            });
            return;
        }

        profile.setPlayerName(document.getString("name"));
        profile.getStats().getChestOpened().setAmount(getData(document, "chestOpened", 1000));
        profile.getStats().getKills().setAmount(getData(document, "kills", 0));
        profile.getStats().getDeaths().setAmount(getData(document, "deaths", 0));
        profile.getStats().getWins().setAmount(getData(document, "wins", 0));
        profile.getStats().getGamePlayed().setAmount(getData(document, "gamePlayed", 0));

        profile.setLoaded(true);
    }

    public void saveData(Profile profile) {
        if (profile == null) {
            return;
        }

        if (!profile.isLoaded()) {
            return;
        }

        Document document = new Document();

        document.put("uuid", profile.getUniqueId().toString());
        document.put("name", profile.getPlayerName());
        document.put("kills", profile.getStats().getKills().getAmount());
        document.put("deaths", profile.getStats().getDeaths().getAmount());
        document.put("chestOpened", profile.getStats().getChestOpened().getAmount());
        document.put("wins", profile.getStats().getWins().getAmount());
        document.put("gamePlayed", profile.getStats().getGamePlayed().getAmount());

        plugin.getMongoManager().getCollection().replaceOne(Filters.eq("uuid", profile.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public void removeProfile(Profile profile) {
        Tasks.runAsync(() -> {
            this.saveData(profile);
            this.profiles.remove(profile.getUniqueId());
        });
    }

    public Collection<Profile> getAllData() {
        return this.profiles.values();
    }

    public Profile getOfflineProfile(String name) {
        Player player = Bukkit.getPlayer(name);

        if (player != null) {
            Profile profile = getProfile(player.getUniqueId());

            if (profile != null) {
                return profile;
            }
        }

        Document document = plugin.getMongoManager().getCollection().find(Filters.eq("name", name)).first();

        if (document == null) {
            return null;
        }

        Profile profile = new Profile(UUID.fromString(document.getString("uuid")));
        this.profiles.put(profile.getUniqueId(), profile);
        loadData(profile);

        return profile;
    }

    public Profile getOfflineProfile(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            Profile profile = getProfile(player.getUniqueId());

            if (profile != null) {
                return profile;
            }
        }

        Document document = plugin.getMongoManager().getCollection().find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return null;
        }

        Profile profile = new Profile(UUID.fromString(document.getString("uuid")));
        this.profiles.put(profile.getUniqueId(), profile);
        loadData(profile);

        return profile;
    }

    public Profile getProfile(UUID uuid) {
        if (Bukkit.getPlayer(uuid) != null) {
            if (!this.profiles.containsKey(uuid)) {
                createProfile(Bukkit.getPlayer(uuid));
            }
        }

        return this.profiles.get(uuid);
    }

    private int getData(Document document, String path, int def) {
        if (!document.containsKey(path)) {
            document.put(path, def);
        }
        return document.getInteger(path);
    }
}
