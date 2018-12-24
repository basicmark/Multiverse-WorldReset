package io.github.basicmark.MultiverseWorldReset.commands;

import java.util.List;

import io.github.basicmark.MultiverseWorldReset.MultiverseWorldReset;
import io.github.basicmark.MultiverseWorldReset.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class InfoCommand extends WorldResetCommand {

    public InfoCommand(MultiverseWorldReset plugin) {
        super(plugin);
        setName("World reset information");
        setCommandUsage("/mvwr info " + ChatColor.GREEN + "{world}");
        setArgRange(0, 1);
        addKey("mvwr info");
        addKey("mvwri");
        addKey("mvwrinfo");
        setPermission("multiverse.worldreset.info", "Displays reset information about a world.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() == 0) {
            plugin.worldsInfo(sender);
        } else {
            WorldManager manager = plugin.getManager(args.get(0));
            if (manager == null) {
                sender.sendMessage(ChatColor.RED + "Unknown world " + args.get(0) + ", have you added to WorldReset?");
            } else {
                manager.info(sender);
            }
        }
    }
}
