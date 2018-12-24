package io.github.basicmark.MultiverseWorldReset.commands;

import java.util.List;

import com.pneumaticraft.commandhandler.multiverse.Command;
import org.bukkit.command.CommandSender;
import io.github.basicmark.MultiverseWorldReset.MultiverseWorldReset;

public abstract class WorldResetCommand extends Command {

    protected MultiverseWorldReset plugin;
    public WorldResetCommand(MultiverseWorldReset plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public abstract void runCommand(CommandSender sender, List<String> args);

}
