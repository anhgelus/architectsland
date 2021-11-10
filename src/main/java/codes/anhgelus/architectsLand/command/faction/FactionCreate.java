package main.java.codes.anhgelus.architectsLand.command.faction;

import main.java.codes.anhgelus.architectsLand.ArchitectsLand;
import main.java.codes.anhgelus.architectsLand.command.FactionCommand;
import main.java.codes.anhgelus.architectsLand.util.Static;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class FactionCreate {
    private final String[] strings;
    private final CommandSender commandSender;
    private final ArchitectsLand main;

    public static final String PERMISSION = FactionCommand.PERMISSION_FACTION + "create";

    public FactionCreate (String[] strings, CommandSender commandSender, ArchitectsLand main) {
        this.strings = strings;
        this.commandSender = commandSender;
        this.main = main;
    }
    public boolean command() {
        if (strings.length > 1) {
            File basesFile = new FactionCommand(main).getFactionsData();
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(basesFile);

            final String playerUUID = String.valueOf(((Player) commandSender).getUniqueId());

            final String key = strings[1].toLowerCase();
            final String status = ".status";

            // Check if the faction exist
            if (FactionCommand.doubleFaction(config, key)) {
                commandSender.sendMessage(Static.ERROR + "This faction already exist!");
                return true;
            }

            final String prefixBrut = key.substring(0, 3).toUpperCase();
            final String prefixTeam = Static.prefixCreatorJson(prefixBrut, "white");
            final String prefix = ChatColor.YELLOW + "[" + ChatColor.WHITE + "" + prefixBrut + ChatColor.YELLOW + "]";

            // Set every args in factions.yml
            config.set(key + ".owner", playerUUID);
            config.set(key + ".members", playerUUID + ",");
            config.set(key + status + ".description", "No description set. Use /f modify description to set it.");
            config.set(key + status + ".prefix", prefix);
            config.set(key + status + ".name", strings[1]);
            config.set(key + status + ".color", "white");

            FactionCommand.saveFile(config, basesFile);

            // Set every args in Minecraft team in game
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "team add " + key);
            Bukkit.dispatchCommand(console, "team join " + key + " " + ((Player) commandSender).getDisplayName());
            Bukkit.dispatchCommand(console, "team modify " + key + " prefix " + prefixTeam);

            commandSender.sendMessage(Static.SUCCESS + "The faction was created!");
            ArchitectsLand.LOGGER.info("Faction " + strings[1] + " was created by " + ((Player) commandSender).getDisplayName());
        } else {
            commandSender.sendMessage(Static.ERROR + "You need to specify the faction's name to create a faction!");
        }
        return true;
    }
}
