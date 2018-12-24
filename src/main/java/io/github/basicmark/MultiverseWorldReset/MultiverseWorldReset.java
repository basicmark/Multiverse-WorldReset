package io.github.basicmark.MultiverseWorldReset;



import java.util.*;
import java.util.logging.Level;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVPlugin;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commands.HelpCommand;
import com.pneumaticraft.commandhandler.multiverse.CommandHandler;

import io.github.basicmark.MultiverseWorldReset.commands.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MultiverseWorldReset extends JavaPlugin implements MVPlugin, Listener {
    /* FIXME: When a world is regenerated a new MVWorld is created, use work name or UUID instead */
	private Map<String, WorldManager> worldManagers;
	private Map<WorldManager, Date> nextNotification;
    private BukkitTask timer;

    private final static int requiresProtocol = 19;
    private MultiverseCore core;
    private CommandHandler commandHandler;
    private String worldEnterFormat = "Warning: This is a temporary world and it will be reset on %date";
    private String periodicNoticeFormat = "&7Info:  World %world will be reset in %days days, %hours hours and %mins + minutes";
    private String lastResetInfoFormat = "Last reset:  %date";
    private String nextResetInfoFormat = "Next reset:  %date";

    public void reloadConfig() {
        super.reloadConfig();
        FileConfiguration config = getConfig();

        /*
         * Worlds might be added/removed when the configuration is reloaded so
         * create new maps for the manager and notifications
         */
        worldManagers = new HashMap<String, WorldManager>();
        nextNotification = new HashMap<WorldManager, Date>();
        if (config != null) {
            ConfigurationSection worlds = config.getConfigurationSection("worlds");
            if (worlds != null) {
                Set<String> keys = worlds.getKeys(false);
                for (String key : keys) {
                    MultiverseWorld world = core.getMVWorldManager().getMVWorld(key);
                    if (world != null) {
                        WorldManager manager =  new WorldManager(this, key, worlds.getConfigurationSection(world.getName()));
                        manager.loadConfig();
                        worldManagers.put(key, manager);
                    } else {
                        getLogger().info("Failed to find Multiverse world for " + key);
                    }
                }
            }

            worldEnterFormat = config.getString("worldenterformat", worldEnterFormat);
            periodicNoticeFormat = config.getString("periodicnoticeformat", periodicNoticeFormat);
            lastResetInfoFormat = config.getString("lastresetifoformat", lastResetInfoFormat);
            nextResetInfoFormat = config.getString("nextresetinfoformat", nextResetInfoFormat);
        }
    }

	public void onEnable() {
        /*
         * Find Multiverse-Core and hook into it is present
         * otherwise bail out.
         */
        core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (core == null) {
            getLogger().info("Multiverse-Core not found, will keep looking.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (core.getProtocolVersion() < requiresProtocol) {
            getLogger().severe("Your Multiverse-Core is OUT OF DATE");
            getLogger().severe("This version of Multiverse Portals requires Protocol Level: " + requiresProtocol);
            getLogger().severe("Your of Core Protocol Level is: " + this.core.getProtocolVersion());
            getLogger().severe("Grab an updated copy at: ");
            getLogger().severe("http://ci.onarandombox.com/view/Multiverse/job/Multiverse-Core/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        core.incrementPluginCount();

        /*
         * Register our commands with Multiverse-Core
         */
        commandHandler = core.getCommandHandler();
        commandHandler.registerCommand(new InfoCommand(this));
        commandHandler.registerCommand(new AddWorldCommand(this));
        commandHandler.registerCommand(new RemoveWorldCommand(this));
        commandHandler.registerCommand(new AutomaticResetCommand(this));
        commandHandler.registerCommand(new SetPeriodCommand(this));
        commandHandler.registerCommand(new ForceResetCommand(this));
        commandHandler.registerCommand(new ReloadCommand(this));

        for (com.pneumaticraft.commandhandler.multiverse.Command c : commandHandler.getAllCommands()) {
            if (c instanceof HelpCommand) {
                c.addKey("mvwr");
            }
        }

        // Create/load the config file
        saveDefaultConfig();
        reloadConfig();

        timer = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                Date date = new Date();

                for(WorldManager manager : worldManagers.values()) {
                    Calendar calendar = Calendar.getInstance();
                    Date resetDate = manager.getNextReset();
                    if (resetDate == null) {
                        continue;
                    }

                    /* First check if the world is due for a reset */
                    calendar.setTime(resetDate);
                    if (date.after(calendar.getTime())) {
                        manager.forceReset();
                        /* Remove the next update time to force a broadcast now */
                        nextNotification.remove(manager);
                    }

                    /* Then check if a notification broadcast is required */
                    Date nextDate = nextNotification.get(manager);
                    if (nextDate == null) {
                        nextDate = date;
                        nextNotification.put(manager, nextDate);
                    }

                    calendar.setTime(nextDate);
                    if (date.after(calendar.getTime())) {
                        Calendar target = Calendar.getInstance();
                        target.setTime(manager.getNextReset());
                        Calendar now = Calendar.getInstance();
                        int years = target.get(Calendar.YEAR) - now.get(Calendar.YEAR);
                        int days = target.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
                        days += years * 365;
                        int hours = target.get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY);
                        int mins = target.get(Calendar.MINUTE) - now.get(Calendar.MINUTE);
                        if (mins < 0) {
                            hours -= 1;
                        }
                        if (hours < 0) {
                            days -= 1;
                        }

                        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                            player.sendMessage(manager.getNextResetString(periodicNoticeFormat));
                        }

                        Calendar next = Calendar.getInstance();
                        if (days >= 1) {
                            next.add(Calendar.HOUR, 1);
                        } else {
                            next.add(Calendar.MINUTE, 5);
                        }
                        nextNotification.put(manager,next.getTime());
                    }
                }
            }
        }, 0, 20);

        getServer().getPluginManager().registerEvents(this, this);
	}

	public void onDisable(){
        timer.cancel();
	}

    public void setCore(MultiverseCore multiverseCore) {
        core = multiverseCore;
    }

    @Override
    public int getProtocolVersion() {
        return 0;
    }

    @Override
    public String dumpVersionInfo(String s) {
        return null;
    }

    public MultiverseCore getCore() {
        return core;
    }

    public WorldManager getManager(String worldName){
        return worldManagers.get(worldName);
    }

    public String getLastResetInfoFormat() {
        return lastResetInfoFormat;
    }

    public String getNextResetInfoFormat() {
        return nextResetInfoFormat;
    }

    public boolean addWorld(MultiverseWorld world) {
        if (!worldManagers.containsKey(world)) {
            WorldManager manager = new WorldManager(this, world.getName(), getConfig().createSection("worlds." + world.getName()));
            worldManagers.put(world.getName(), manager);
            saveConfig();
            return true;
        }
        return false;
    }

    public boolean removeWorld(String worldName) {
        WorldManager manager = worldManagers.get(worldName);
        if (manager != null) {
            //manager.remove();
            getConfig().set("worlds." + worldName, null);
            worldManagers.remove(worldName);
            saveConfig();
            return true;
        }
        return false;
    }

    public void worldsInfo(CommandSender sender) {
        for (WorldManager manager : worldManagers.values()) {
            manager.info(sender);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!this.isEnabled()) {
            sender.sendMessage("This plugin is Disabled!");
            return true;
        }
        ArrayList<String> allArgs = new ArrayList<String>(Arrays.asList(args));
        allArgs.add(0, command.getName());
        return this.commandHandler.locateAndRunCommand(sender, allArgs);
    }

    @Override
    public void log(Level level, String s) {

    }

    @EventHandler
    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String worldName = event.getPlayer().getWorld().getName();
        if (worldManagers.containsKey(worldName)) {
            WorldManager manager = worldManagers.get(worldName);
            player.sendMessage(manager.getNextResetString(worldEnterFormat));
        }
    }
}

