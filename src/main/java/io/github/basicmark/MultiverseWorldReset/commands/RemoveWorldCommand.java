package io.github.basicmark.MultiverseWorldReset.commands;

import java.util.List;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import io.github.basicmark.MultiverseWorldReset.MultiverseWorldReset;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class RemoveWorldCommand extends WorldResetCommand {

    public RemoveWorldCommand(MultiverseWorldReset plugin) {
        super(plugin);
        setName("Rmove world to world resetter");
        setCommandUsage("/mvwr remove " + ChatColor.GREEN + "{world}");
        setArgRange(1, 1);
        addKey("mvwr remove");
        addKey("mvwra");
        addKey("mvwradd");
        setPermission("multiverse.worldreset.add", "Remove a world to the world reset manager.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {

        if (plugin.removeWorld(args.get(0))) {
            sender.sendMessage(ChatColor.GREEN + "Succesfully removed world " + args.get(0));
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to remove " + args.get(0) + ". Was it added?");
        }
    }
}
