package io.github.basicmark.MultiverseWorldReset.commands;

import java.util.List;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import io.github.basicmark.MultiverseWorldReset.MultiverseWorldReset;
import io.github.basicmark.MultiverseWorldReset.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class SetPeriodCommand extends WorldResetCommand {

    public SetPeriodCommand(MultiverseWorldReset plugin) {
        super(plugin);
        setName("Automatic world reset period");
        setCommandUsage("/mvwr period " + ChatColor.GREEN + "{world} {period}");
        setArgRange(2, 2);
        addKey("mvwr period");
        addKey("mvwrp");
        addKey("mvwrperiod");
        setPermission("multiverse.worldreset.period", "Set the period between world resets.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        WorldManager manager = plugin.getManager(args.get(0));
        if (manager == null) {
            sender.sendMessage(ChatColor.RED + "Unknown world " + args.get(0) + ", have you added to WorldReset?");
        } else {
            manager.setResetPeriod(args.get(1), sender);
        }
    }
}
