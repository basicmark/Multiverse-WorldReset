package io.github.basicmark.MultiverseWorldReset.commands;

import io.github.basicmark.MultiverseWorldReset.MultiverseWorldReset;
import io.github.basicmark.MultiverseWorldReset.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class ReloadCommand extends WorldResetCommand {

    public ReloadCommand(MultiverseWorldReset plugin) {
        super(plugin);
        setName("WorldReset configuration reload");
        setCommandUsage("/mvwr reload");
        setArgRange(0, 0);
        addKey("mvwr reload");
        addKey("mvwrr");
        addKey("mvwrreload");
        setPermission("multiverse.worldreset.reload", "Reload configuration settings for WorldReset.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded");
    }
}
