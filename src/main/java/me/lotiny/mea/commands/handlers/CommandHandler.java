package me.lotiny.mea.commands.handlers;

import me.lotiny.mea.Mea;
import me.lotiny.mea.commands.MapCommand;
import me.lotiny.mea.commands.VoteCommand;
import org.bukkit.entity.Player;
import revxrsal.commands.autocomplete.SuggestionProviderFactory;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private final BukkitCommandHandler commandHandler;

    public CommandHandler(Mea plugin) {
        commandHandler = BukkitCommandHandler.create(plugin);
        commandHandler.registerDependency(Mea.class, plugin);
        commandHandler.setExceptionHandler(new CommandExceptionHandler());

        commandHandler.getAutoCompleter().registerSuggestionFactory(0, SuggestionProviderFactory.forType(Player.class, (args, sender, command) -> {
            List<String> list = new ArrayList<>();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                list.add(player.getName());
            }

            return list;
        }));

        register();
    }

    private void register() {
        commandHandler.register(new MapCommand());
        commandHandler.register(new VoteCommand());
    }
}
