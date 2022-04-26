package net.steeeve.dfchat.events;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.steeeve.dfchat.Config;
import net.steeeve.dfchat.Dfchat;
import net.steeeve.dfchat.util.formatting;
import org.checkerframework.checker.regex.RegexUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChatEvent {

    private final ProxyServer server;
    private final Logger logger;
    private final Config config;
    private final List<String> blockedwords;

    public ChatEvent(ProxyServer server, Logger logger, Config config, List<String> blockedwords) {
        this.server = server;
        this.logger = logger;
        this.config = config;
        this.blockedwords = blockedwords;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerChatEvent(PlayerChatEvent event) throws IOException {

        formatting formatting = new formatting(config);

        String original = formatting.formatMessage(event.getPlayer(), event.getMessage());
        String toSend = formatting.formatMessage(event.getPlayer(), event.getMessage());

        //Log to console if enabled
        if(config.GLOBAL_CHAT_TO_CONSOLE) {logger.info(toSend);}

        Boolean blockedMessage = false;
        String regex = "^word\\W|\\Wword\\W|\\Wword$";

        //Check against blacklist
        if(this.config.FILTER_ENABLE)
            for (String word : this.blockedwords) {
                Pattern pattern = Pattern.compile(regex.replaceAll("word", word), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(toSend);
                if(config.FILTER_CENSOR) {
                    toSend = matcher.replaceAll(config.FILTER_CENSOR_MSG);
                } else {
                    if(matcher.find()) {
                        if (event.getPlayer().hasPermission("SteeeveChat.filter.mature")) {
                            event.getPlayer().sendActionBar(Component.text(formatting.basicFormatting(event.getPlayer(), this.config.FILTER_BLOCKED_ACTION_BAR)));
                        }
                        else {
                            event.getPlayer().sendMessage(Identity.nil(), Component.text(formatting.basicFormatting(event.getPlayer(), this.config.FILTER_BLOCKED_MSG)));
                        }
                        blockedMessage = true;
                        break;
                    }

                }
            }

        sendMessage(event.getPlayer(), toSend, blockedMessage, original);

        //Passthrough check
        if(!config.GLOBAL_CHAT_PASSTHROUGH) {event.setResult(PlayerChatEvent.ChatResult.denied());}
    }

    public void sendMessage(Player sender, String message, boolean mature, String original) {
        for (Player player : this.server.getAllPlayers())
            if (player.hasPermission("SteeeveChat.filter.mature")) {
                player.sendMessage(sender.identity(), Component.text(original));
            }
            else if (!mature){
                player.sendMessage(sender.identity(), Component.text(message));
            }
    }
}
