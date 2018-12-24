package io.github.basicmark.MultiverseWorldReset.commands;

import java.util.List;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import io.github.basicmark.MultiverseWorldReset.MultiverseWorldReset;
import io.github.basicmark.MultiverseWorldReset.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class AutomaticResetCommand extends WorldResetCommand {

    public AutomaticResetCommand(MultiverseWorldReset plugin) {
        super(plugin);
        setName("Automatic world reset enable/disable");
        setCommandUsage("/mvwr reset " + ChatColor.GREEN + "{world} {enable/disable}");
        setArgRange(2, 2);
        addKey("mvwr reset");
        addKey("mvwrr");
        addKey("mvwrreset");
        setPermission("multiverse.worldreset.reset", "Control automatic world reset", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        boolean enable;
        if (args.get(1).equalsIgnoreCase("enable")) {
            enable = true;
        } else if (args.get(1).equalsIgnoreCase("disable")) {
            enable = false;
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown parameter " + args.get(1));
            return;
        }

        WorldManager manager = plugin.getManager(args.get(0));
        if (manager == null) {
            sender.sendMessage(ChatColor.RED + "Unknown world " + args.get(0) + ", have you added to WorldReset?");
        } else {
            manager.setAutomaticReset(enable);
            sender.sendMessage( ChatColor.GREEN + "Successfully " + (enable ? "enabled":"disabled") + " automatic world reset for " + args.get(0));
        }
    }
}
