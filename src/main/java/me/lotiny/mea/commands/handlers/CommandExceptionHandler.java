package me.lotiny.mea.commands.handlers;

import me.lotiny.mea.utils.CC;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.exception.BukkitExceptionAdapter;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

public class CommandExceptionHandler extends BukkitExceptionAdapter {

    @Override
    public void missingArgument(@NotNull CommandActor actor, @NotNull MissingArgumentException exception) {
        actor.error(CC.translate("&cUsage: /" + exception.getCommand().getUsage()));
    }

    @Override
    public void invalidPlayer(@NotNull CommandActor actor, @NotNull InvalidPlayerException exception) {
        actor.error(CC.translate("&cThe player you specified is not online."));
    }

    @Override
    public void noPermission(@NotNull CommandActor actor, @NotNull NoPermissionException exception) {
        actor.error(CC.translate("&cYou do not have permissions to execute this command."));
    }
}
