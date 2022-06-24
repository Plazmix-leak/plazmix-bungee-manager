package net.plazmix.bungeemanager.announer;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.plazmix.bungeemanager.BungeeManagerPlugin;
import net.plazmix.coreconnector.core.language.LanguageManager;
import net.plazmix.coreconnector.utility.localization.LocalizationResource;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public final class AnnounceManager {

    public LinkedList<AnnounceMessage> getAnnounceMessages(BungeeManagerPlugin bungeeManagerPlugin) {
        LinkedList<AnnounceMessage> announceMessages = new LinkedList<>();
        Configuration configuration = bungeeManagerPlugin.getConfig().getSection("announcer");

        for (String announceMessageKey : configuration.getKeys()) {
            Configuration messageSection = configuration.getSection(announceMessageKey);

            String messageKey   = messageSection.getString("message_key");
            String hoverKey     = messageSection.getString("hover_key");


            ClickEvent.Action clickAction = null;
            String clickActionContext = null;

            if (messageSection.get("click") != null) {

                clickAction = ClickEvent.Action.valueOf(messageSection.getString("click.action"));
                clickActionContext = messageSection.getString("click.context");
            }

            announceMessages.add(new AnnounceMessage(messageKey, hoverKey, clickAction, clickActionContext));
        }

        return announceMessages;
    }

    public void startAnnounceTask(int periodInMinutes, @NonNull BungeeManagerPlugin bungeeManagerPlugin) {
        LinkedList<AnnounceMessage> announceMessages = getAnnounceMessages(bungeeManagerPlugin);

        if (announceMessages.isEmpty()) {
            return;
        }

        bungeeManagerPlugin.getProxy().getScheduler().schedule(bungeeManagerPlugin, new Runnable() {
            private int messageCounter = 0;

            @Override
            public void run() {
                AnnounceMessage announceMessage = announceMessages.get(messageCounter);

                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    announceMessage.sendTo(player);
                }

                messageCounter++;

                if (messageCounter >= announceMessages.size()) {
                    messageCounter = 0;
                }
            }

        }, periodInMinutes, periodInMinutes, TimeUnit.MINUTES);
    }

    @Getter
    @RequiredArgsConstructor
    private static class AnnounceMessage {

        private final String messageKey;
        private final String hoverKey;

        private final ClickEvent.Action clickAction;
        private final String clickActionContext;


        private void sendTo(@NonNull ProxiedPlayer player) {
            LocalizationResource localizationResource = LanguageManager.INSTANCE.getPlayerLanguage(player.getName()).getResource();

            if (messageKey == null) {
                return;
            }

            ComponentBuilder componentBuilder = new ComponentBuilder(
                    !localizationResource.hasMessage(messageKey) ? ChatColor.RED + messageKey : localizationResource.isText(messageKey) ? localizationResource.getText(messageKey) : Joiner.on("\n").join(localizationResource.getTextList(messageKey))
            );


            if (clickAction != null && clickActionContext != null) {
                componentBuilder.event(new ClickEvent(clickAction, clickActionContext));
            }

            if (hoverKey != null) {
                componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                        !localizationResource.hasMessage(hoverKey) ? ChatColor.RED + hoverKey : localizationResource.isText(hoverKey) ? localizationResource.getText(hoverKey) : Joiner.on("\n").join(localizationResource.getTextList(hoverKey))
                )));
            }

            player.sendMessage(ChatMessageType.CHAT, componentBuilder.create());
        }
    }
}
