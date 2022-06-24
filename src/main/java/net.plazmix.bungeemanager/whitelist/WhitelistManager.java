package net.plazmix.bungeemanager.whitelist;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.plazmix.bungeemanager.BungeeManagerPlugin;
import net.plazmix.coreconnector.core.language.LanguageType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

@Getter
public final class WhitelistManager {

    private BungeeManagerPlugin bungeeManager;

    private boolean enable;
    private final Collection<String> playerNames = new ArrayList<>();


    public void load(@NonNull BungeeManagerPlugin bungeeManager) {
        this.bungeeManager = bungeeManager;

        this.enable = bungeeManager.getConfig().getBoolean("whitelist.enable");

        playerNames.clear();
        playerNames.addAll(bungeeManager.getConfig().getStringList("whitelist.players"));
    }


    public String getWhitelistMotd(@NonNull LanguageType translation) {
        if (bungeeManager == null) {
            return null;
        }

        return Joiner.on("\n").join(translation.getResource().getTextList("BUNGEE_WHITELIST_SERVER_MOTD_TEXT"));
    }

    public String getWhitelistHover(@NonNull LanguageType translation) {
        if (bungeeManager == null) {
            return null;
        }

        return Joiner.on("\n").join(translation.getResource().getTextList("BUNGEE_WHITELIST_SERVER_MOTD_HOVER"));
    }

    public String getCoreNotConnectedMotd(@NonNull LanguageType translation) {
        if (bungeeManager == null) {
            return null;
        }

        return Joiner.on("\n").join(translation.getResource().getTextList("CORE_NOT_CONNECTED_MOTD"));
    }

    public String getCoreNotConnectedHover(@NonNull LanguageType translation) {
        if (bungeeManager == null) {
            return null;
        }

        return Joiner.on("\n").join(translation.getResource().getTextList("CORE_NOT_CONNECTED_HOVER"));
    }

    public String getKickMessage(@NonNull LanguageType translation) {
        String message = Joiner.on("\n").join(translation.getResource().getTextList("WHITELIST_KICK_MESSAGE"));

        return ChatColor.translateAlternateColorCodes('&', message);
    }


    public void setEnable(boolean enable) {
        this.enable = enable;

        bungeeManager.getConfig().set("whitelist.enable", enable);
        bungeeManager.saveConfig();
    }


    public void addPlayer(@NonNull String playerName) {
        this.playerNames.add(playerName.toLowerCase(Locale.ROOT));

        bungeeManager.getConfig().set("whitelist.players", playerNames);
        bungeeManager.saveConfig();
    }

    public void removePlayer(@NonNull String playerName) {
        this.playerNames.remove(playerName.toLowerCase(Locale.ROOT));

        bungeeManager.getConfig().set("whitelist.players", playerNames);
        bungeeManager.saveConfig();
    }
}
