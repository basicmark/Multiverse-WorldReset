package io.github.basicmark.MultiverseWorldReset;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Calendar;
import java.util.Date;

public class WorldManager {
    private MultiverseWorldReset plugin;
    private String worldName;
    private ConfigurationSection config;

    private int periodMonths = 0;
    private int periodDays = 0;
    private int periodHours = 0;
    private int periodMinutes = 0;
    private boolean autoReset = false;
    private Date lastReset = null;
    private Date nextReset = null;
    
    WorldManager(MultiverseWorldReset plugin, String worldName, ConfigurationSection config) {
        this.plugin = plugin;
        this.worldName = worldName;
        this.config = config;
    }

    public String getNextResetString(String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(nextReset);
        Calendar now = Calendar.getInstance();
        int years = cal.get(Calendar.YEAR) - now.get(Calendar.YEAR);
        int days = cal.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
        days += years * 365;
        int hours = cal.get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY);
        int mins = cal.get(Calendar.MINUTE) - now.get(Calendar.MINUTE);
        if (mins < 0) {
            mins += 60;
            hours -= 1;
        }
        if (hours < 0) {
            hours += 24;
            days -= 1;
        }

        String ret = format.replace("%world", worldName);
        ret = ret.replace("%days", "" + days);
        ret = ret.replace("%hours", "" + hours);
        ret = ret.replace("%mins", "" + mins);
        ret = ret.replace("%date", "" + nextReset);
        return ChatColor.translateAlternateColorCodes('&', ret);
    }

    public String getLastResetString(String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastReset);
        Calendar now = Calendar.getInstance();
        int years = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
        int days = now.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
        days += years * 365;
        int hours = now.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY);
        int mins = now.get(Calendar.MINUTE) - cal.get(Calendar.MINUTE);
        if (mins < 0) {
            mins += 60;
            hours -= 1;
        }
        if (hours < 0) {
            hours += 24;
            days -= 1;
        }

        String ret = format.replace("%world", worldName);
        ret = ret.replace("%days", "" + days);
        ret = ret.replace("%hours", "" + hours);
        ret = ret.replace("%mins", "" + mins);
        ret = ret.replace("%date", "" + lastReset);
        return ChatColor.translateAlternateColorCodes('&', ret);
    }

    public void info(CommandSender sender) {
        sender.sendMessage(worldName + ":");
        sender.sendMessage("Auto reset:" + (autoReset?"enabled":"disabled"));
        if (lastReset != null) {
            sender.sendMessage(getLastResetString(plugin.getLastResetInfoFormat()));
        }
        if (nextReset != null) {
            sender.sendMessage(getNextResetString(plugin.getNextResetInfoFormat()));
        }
    }
    
    public void forceReset() {
        resetWorld();
        lastReset = new Date();
        if (autoReset) {
            recalculateReset(lastReset);
        }
        saveConfig();
    }
    
    public void setAutomaticReset(boolean enable) {
        if (autoReset == enable) {
            return;
        }

        autoReset = enable;
        lastReset = new Date();
        if (autoReset) {
            recalculateReset(lastReset);
            saveConfig();
        }
    }
    
    public boolean getAutomaticReset() {
        return autoReset;
    }
    
    public void setResetPeriod(String periodString, CommandSender sender) {
        /*
         * The string format is as follows:
         * 1M2D3H4m
         *
         * where:
         * M = Months
         * D = Days
         * H = Hours
         * m = minutes
         *
         * If one or more units are missing they are assigned a value of 0
         */
        int tmpPeriodMonths = 0;
        int tmpPeriodDays = 0;
        int tmpPeriodHours = 0;
        int tmpPeriodMinutes = 0;

        int monthsEnd = periodString.indexOf("M");
        int daysEnd = periodString.indexOf("D");
        int hoursEnd = periodString.indexOf("H");
        int minutesEnd = periodString.indexOf("m");
        int nextStart = 0;

        if (monthsEnd != -1) {
            tmpPeriodMonths = Integer.valueOf(periodString.substring(nextStart, monthsEnd));
            nextStart = Math.min(monthsEnd + 1, periodString.length() - 1);
        }
        if (daysEnd != -1) {
            tmpPeriodDays = Integer.valueOf(periodString.substring(nextStart, daysEnd));
            nextStart = Math.min(daysEnd + 1, periodString.length() - 1);
        }
        if (hoursEnd != -1) {
            tmpPeriodHours = Integer.valueOf(periodString.substring(nextStart, hoursEnd));
            nextStart = Math.min(hoursEnd + 1, periodString.length() - 1);
        }
        if (minutesEnd != -1) {
            tmpPeriodMinutes = Integer.valueOf(periodString.substring(nextStart, minutesEnd));
            nextStart = Math.min(minutesEnd + 1, periodString.length() - 1);
        }
        
        if (nextStart == 0) {
            sender.sendMessage("Failed to process period, no valid units found!");
            return;
        } else if (nextStart != (periodString.length() - 1)) {
            sender.sendMessage("Failed to process period, tokens found after processing!");
            return;
        }
        
        /* Successfully processed the string, save the values */
        periodMonths = tmpPeriodMonths;
        periodDays = tmpPeriodDays;
        periodHours = tmpPeriodHours;
        periodMinutes = tmpPeriodMinutes;
        sender.sendMessage("Reset period set to " + periodMonths + " months, " + periodDays + " days, " + periodHours + " hours, " + periodMinutes + " minutes");
        if (autoReset) {
            recalculateReset(lastReset);
        }
        saveConfig();
    }

    public Date getNextReset() {
        if (autoReset) {
            return nextReset;
        } else {
            return null;
        }
    }

    public String getWorldName() {
        return worldName;
    }
    
    public void saveConfig() {
        config.set("periodmonths", periodMonths);
        config.set("perioddays", periodDays);
        config.set("periodhours", periodHours);
        config.set("periodminutes", periodMinutes);
        config.set("autoreset", autoReset);
        if (lastReset != null) {
            config.set("lastreset", lastReset.getTime());
        }
        if (nextReset != null) {
            config.set("nextreset", nextReset.getTime());
        }
        plugin.saveConfig();
    }

    public void loadConfig() {
        periodMonths = config.getInt("periodmonths", periodMonths);
        periodDays = config.getInt("perioddays", periodDays);
        periodHours = config.getInt("periodhours", periodHours);
        periodMinutes = config.getInt("periodminutes", periodMinutes);
        autoReset = config.getBoolean("autoreset", autoReset);

        if (config.contains("lastreset")) {
            lastReset = new Date();
            lastReset.setTime(config.getLong("lastreset", lastReset.getTime()));
        }
 
        if (config.contains("nextreset")) {
            nextReset = new Date();
            nextReset.setTime(config.getLong("nextreset", nextReset.getTime()));
        }
    }

    private void recalculateReset(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, periodMonths);
        calendar.add(Calendar.DAY_OF_YEAR, periodDays);
        calendar.add(Calendar.HOUR_OF_DAY, periodHours);
        calendar.add(Calendar.MINUTE, periodMinutes);
        nextReset = calendar.getTime();
    }

    private void resetWorld() {
        plugin.getCore().getMVWorldManager().regenWorld(worldName, true, true, "");
    }
}