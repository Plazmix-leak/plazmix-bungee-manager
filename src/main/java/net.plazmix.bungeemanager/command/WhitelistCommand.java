package net.plazmix.bungeemanager.command;

import com.google.common.base.Joiner;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.plazmix.bungeemanager.BungeeManagerPlugin;

public class WhitelistCommand extends Command {
    private final BungeeManagerPlugin bungeeManager;

    public WhitelistCommand(@NonNull BungeeManagerPlugin bungeeManager) {
        super("whitelist", null, "wl");

        this.bungeeManager = bungeeManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            return;
        }

        if (args.length == 0) {
            sendHelpMessage(commandSender);

            return;
        }

        switch (args[0].toLowerCase()) {

            case "true":
            case "enable":
            case "on": {
                bungeeManager.getWhitelistManager().setEnable(true);

                commandSender.sendMessage("§6§lBungeeManager §8:: §fWhitelist was successfully §aenabled§f!");
                break;
            }

            case "false":
            case "disable":
            case "off": {
                bungeeManager.getWhitelistManager().setEnable(false);

                commandSender.sendMessage("§6§lBungeeManager §8:: §fWhitelist was successfully §cdisabled§f!");
                break;
            }

            case "add": {
                if (args.length < 2) {
                    commandSender.sendMessage("§cError, usage - /whitelist add <player name>");
                    break;
                }

                bungeeManager.getWhitelistManager().addPlayer(args[1]);
                commandSender.sendMessage("§6§lBungeeManager §8:: §fNickname §e" + args[1].toLowerCase() + " §fwas successfully added!");
                break;
            }

            case "del":
            case "delete":
            case "rem":
            case "remove": {
                if (args.length < 2) {
                    commandSender.sendMessage("§cError, usage - /whitelist remove <player name>");
                    break;
                }

                bungeeManager.getWhitelistManager().removePlayer(args[1]);
                commandSender.sendMessage("§6§lBungeeManager §8:: §fNickname §e" + args[1].toLowerCase() + " §fwas successfully removed!");
                break;
            }

            case "list": {

                commandSender.sendMessage("§6§lBungeeManager §8:: §fList of player names in whitelist:");
                commandSender.sendMessage(" §e" + Joiner.on("§7, §e").join(bungeeManager.getWhitelistManager().getPlayerNames()));
                break;
            }

            default:
                sendHelpMessage(commandSender);
        }
    }

    private void sendHelpMessage(@NonNull CommandSender commandSender) {
        commandSender.sendMessage("§6§lBungeeManager §8:: §fHelp commands:");

        commandSender.sendMessage(" §7Enable whitelist system - §e/whitelist on");
        commandSender.sendMessage(" §7Disable whitelist system - §e/whitelist off");

        commandSender.sendMessage(" §7Get whitelist player names - §e/whitelist list");
        commandSender.sendMessage(" §7Add player name to whitelist - §e/whitelist add <player name>");
        commandSender.sendMessage(" §7Remove player name to whitelist - §e/whitelist remove <player name>");
    }

}
