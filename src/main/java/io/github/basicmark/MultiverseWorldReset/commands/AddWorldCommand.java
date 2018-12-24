package io.github.basicmark.MultiverseWorldReset.commands;

import java.util.List;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import io.github.basicmark.MultiverseWorldReset.MultiverseWorldReset;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class AddWorldCommand extends WorldResetCommand {

    public AddWorldCommand(MultiverseWorldReset plugin) {
        super(plugin);
        setName("Add world to world resetter");
        setCommandUsage("/mvwr add " + ChatColor.GREEN + "{world}");
        setArgRange(1, 1);
        addKey("mvwr add");
        addKey("mvwra");
        addKey("mvwradd");
        setPermission("multiverse.worldreset.add", "Add a world to the world reset manager.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        MultiverseWorld world = this.plugin.getCore().getMVWorldManager().getMVWorld(args.get(0));
        if(world == null) {
            sender.sendMessage(ChatColor.RED + "That world doesn't exist or is not known to Multiverse");
            return;
        }

        if (this.plugin.addWorld(world)) {
            sender.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.GOLD + world.getName() + ChatColor.GREEN + " to WorldReset");
            sender.sendMessage(ChatColor.GREEN + "Automatic reset is disabled by default, please set the reset period and enable via the commands");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to add " + world.getName() + " has it already been added?");
        }
    }
}
