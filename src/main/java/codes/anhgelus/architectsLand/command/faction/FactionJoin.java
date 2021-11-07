package main.java.codes.anhgelus.architectsLand.command.faction;

import main.java.codes.anhgelus.architectsLand.ArchitectsLand;
import main.java.codes.anhgelus.architectsLand.command.FactionCommand;
import main.java.codes.anhgelus.architectsLand.util.Static;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;

public class FactionJoin {

    private final String[] strings;
    private final CommandSender commandSender;
    private final ArchitectsLand main;

    public static final String PERMISSION = FactionCommand.PERMISSION_FACTION + "join";

    public FactionJoin (String[] strings, CommandSender commandSender, ArchitectsLand main) {
        this.strings = strings;
        this.commandSender = commandSender;
        this.main = main;
    }
    public boolean command() {
        if (strings.length > 1) {
            final File basesFile = new FactionCommand(main).getFactionsData();
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(basesFile);

            // Check if the faction exist
            if (config.getString(strings[1]) != null) {
                final String faction = strings[1].toLowerCase();
                final String playersString = config.getString(faction + ".members");
                final String[] players = playersString.split(FactionCommand.UUID_SEPARATOR);

                // Check if the player is in the faction
                for (String i : players) {
                    if (Objects.equals(i, String.valueOf(((Player) commandSender).getUniqueId()))) {
                        commandSender.sendMessage(Static.ERROR + "You're already in this faction.");
                        return true;
                    }
                }

                config.set(strings[1] + ".members", playersString + ((Player) commandSender).getUniqueId() + FactionCommand.UUID_SEPARATOR);

                FactionCommand.saveFile(config, basesFile);
                commandSender.sendMessage(Static.SUCCESS + "You joined " + strings[1] + "!");
                ArchitectsLand.LOGGER.info(((Player) commandSender).getDisplayName() + " joined " + strings[1]);
            } else {
                commandSender.sendMessage(Static.ERROR + "This faction doesn't exist.");
            }
        } else {
            commandSender.sendMessage(Static.ERROR + "You need to specify the faction's name to join it!");
        }
        return true;
    }
}