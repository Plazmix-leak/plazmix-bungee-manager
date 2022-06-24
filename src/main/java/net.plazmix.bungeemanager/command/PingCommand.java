package net.plazmix.bungeemanager.command;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.core.group.GroupManager;
import net.plazmix.coreconnector.core.language.LanguageManager;
import net.plazmix.coreconnector.utility.localization.LocalizationResource;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {

            if (!(commandSender instanceof ProxiedPlayer)) {
                commandSender.sendMessage(ChatColor.RED + "Usage - /ping <player>");
                return;
            }

            sendPlayerPing(commandSender, ((ProxiedPlayer) commandSender));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        sendPlayerPing(commandSender, target);
    }

    private void sendPlayerPing(@NonNull CommandSender commandSender, ProxiedPlayer player) {

        if (!(commandSender instanceof ProxiedPlayer)) {
            if (player == null) {
                commandSender.sendMessage(ChatColor.RED + "Player is offline!");
                return;
            }

            commandSender.sendMessage(ChatColor.GREEN + "Ping of " + player.getName() + " - " + player.getPing() + "ms");
            return;
        }

        LocalizationResource localizationResource = LanguageManager.INSTANCE.getPlayerLanguage(commandSender.getName()).getResource();

        // Check target player online
        if (player == null) {
            commandSender.sendMessage(localizationResource.getText("PLAYER_OFFLINE"));
            return;
        }

        // Yourself check
        if (commandSender.getName().equalsIgnoreCase(player.getName())) {
            commandSender.sendMessage(localizationResource.getMessage("PING_YOURSELF_MESSAGE")
                    .replace("%ping%", player.getPing()).toText());
        }

        // Other check
        else {
            Group playerGroup = GroupManager.INSTANCE.getPlayerGroup(commandSender.getName());
            if (playerGroup.getLevel() < Group.STAR.getLevel()) {

                commandSender.sendMessage(localizationResource.getMessage("MINIMAL_GROUP")
                        .replace("%group%", Group.STAR.getColouredName()).toText());
                return;
            }

            commandSender.sendMessage(localizationResource.getMessage("PING_OTHER_MESSAGE")
                    .replace("%player%", GroupManager.INSTANCE.getPlayerGroup(player.getName()).getPrefix() + " " + player.getName())
                    .replace("%ping%", player.getPing())
                    .toText());
        }
    }

}
