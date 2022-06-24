package net.plazmix.bungeemanager;

import com.google.common.base.Joiner;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.auth.AuthManager;
import net.plazmix.coreconnector.core.auth.AuthPlayer;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.coreconnector.core.group.GroupManager;
import net.plazmix.coreconnector.core.language.LanguageManager;
import net.plazmix.coreconnector.core.language.LanguageType;

import java.util.*;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

@RequiredArgsConstructor
public final class BungeeListener implements Listener {

    private final BungeeManagerPlugin bungeeManager;

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        PendingConnection pendingConnection = event.getConnection();
        LanguageType translation = getTranslation(pendingConnection);

        String serverMotd   = Joiner.on("\n").join(translation.getResource().getTextList("BUNGEE_SERVER_MOTD_TEXT"));
        String serverHover  = Joiner.on("\n").join(translation.getResource().getTextList("BUNGEE_SERVER_MOTD_HOVER"));

        // Check whitelist enable
        if (bungeeManager.getWhitelistManager().isEnable()) {

            serverMotd = bungeeManager.getWhitelistManager().getWhitelistMotd(translation);
            serverHover = bungeeManager.getWhitelistManager().getWhitelistHover(translation);
        }

        // Check Core connection.
        if (!CoreConnector.getInstance().isConnected()) {
            serverMotd = bungeeManager.getWhitelistManager().getCoreNotConnectedMotd(translation);
            serverHover = bungeeManager.getWhitelistManager().getCoreNotConnectedHover(translation);
        }

        // Set Colors
        if (serverMotd != null) serverMotd = ChatColor.translateAlternateColorCodes('&', serverMotd);
        if (serverHover != null) serverHover = ChatColor.translateAlternateColorCodes('&', serverHover);


        // Server MOTD
        ServerPing serverPing = event.getResponse();
        serverPing.setDescription(serverMotd);

        // Players hover
        ServerPing.Players players = serverPing.getPlayers();
        players.setSample(Arrays.stream(serverHover.split("\n")).map(line -> new ServerPing.PlayerInfo(line, UUID.randomUUID().toString())).toArray(ServerPing.PlayerInfo[]::new));
        players.setMax(1);

        serverPing.setPlayers(players);

        // Version hover
        ServerPing.Protocol protocol = serverPing.getVersion();

        protocol.setName(ChatColor.RESET + "Plazmix Network 1.8 - 1.17+");
        protocol.setProtocol(!CoreConnector.getInstance().isConnected() && bungeeManager.getWhitelistManager().isEnable() ? 0 : protocol.getProtocol());

        serverPing.setVersion(protocol);
    }

    @EventHandler
    public void onServerConnect(LoginEvent event) {
        PendingConnection pendingConnection = event.getConnection();
        String playerName = pendingConnection.getName();

        if (bungeeManager.getWhitelistManager().isEnable() && (!bungeeManager.getWhitelistManager().getPlayerNames().contains(playerName.toLowerCase()))) {
            LanguageType translation = getTranslation(pendingConnection);

            event.setCancelReason(bungeeManager.getWhitelistManager().getKickMessage(translation));
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
    }

    private LanguageType getTranslation(@NonNull PendingConnection pendingConnection) {
        AuthPlayer authPlayer = AuthManager.INSTANCE.findPlayer(pendingConnection.getAddress().getHostName());

        if (authPlayer == null) {
            return LanguageType.RUSSIAN;
        }

        return LanguageManager.INSTANCE.getPlayerLanguage(authPlayer.getPlayerName());
    }

}
