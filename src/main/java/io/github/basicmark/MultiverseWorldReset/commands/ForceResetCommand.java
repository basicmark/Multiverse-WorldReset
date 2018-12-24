package io.github.basicmark.MultiverseWorldReset.commands;

import java.util.List;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import io.github.basicmark.MultiverseWorldReset.MultiverseWorldReset;
import io.github.basicmark.MultiverseWorldReset.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class ForceResetCommand extends WorldResetCommand {

    public ForceResetCommand(MultiverseWorldReset plugin) {
        super(plugin);
        setName("Force world reset");
        setCommandUsage("/mvwr force-reset " + ChatColor.GREEN + "{world}");
        setArgRange(1, 1);
        addKey("mvwr force-reset");
        addKey("mvwrfr");
        addKey("mvwrforce-reset");
        setPermission("multiverse.worldreset.force-reset", "Forces a reset of the world.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        WorldManager manager = plugin.getManager(args.get(0));
        if (manager == null) {
            sender.sendMessage(ChatColor.RED + "Unknown world " + args.get(0) + ", have you added to WorldReset?");
        } else {
            manager.forceReset();
            sender.sendMessage( ChatColor.GREEN + "Forced world reset for " + args.get(0) + " completed");
        }
    }
}
