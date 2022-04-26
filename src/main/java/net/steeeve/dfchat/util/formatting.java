package net.steeeve.dfchat.util;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.steeeve.dfchat.Dfchat;
import net.steeeve.dfchat.Config;

import java.util.Optional;

public class formatting {

    private Config config = Dfchat.config;

    public formatting(Config configuration) {
        //Grab config settings
        this.config = configuration;
    }

    public String formatMessage(Player player, String message) {

        //LuckPerms init
        LuckPerms luckperms = Dfchat.getApi();
        User user = luckperms.getPlayerAdapter(Player.class).getUser(player);

        //Fancy code in case of null prefix/suffix
        String lp_prefix = Optional.ofNullable(user.getCachedData().getMetaData().getPrefix()).orElse("").replaceAll("&", "§");
        String lp_suffix = Optional.ofNullable(user.getCachedData().getMetaData().getSuffix()).orElse("").replaceAll("&", "§");

        //Get config stuffz
        String GlobalChatFormat = config.GLOBAL_CHAT_FORMAT;

        if(player.hasPermission("SteeeveChat.colors")) {
            message = message.replaceAll("&", "§");
        }

        String toSend = GlobalChatFormat
                .replaceAll("&", "§")
                .replaceAll("%player%", player.getUsername())
                .replaceAll("%message%", message)
                .replaceAll("%server%", player.getCurrentServer().toString())
                .replaceAll("%lp_prefix%", lp_prefix)
                .replaceAll("%lp_suffix%", lp_suffix);


        return toSend;
    }

    public String basicFormatting(Player player, String message) {

        //LuckPerms init
        LuckPerms luckperms = Dfchat.getApi();
        User user = luckperms.getPlayerAdapter(Player.class).getUser(player);

        //Fancy code in case of null prefix/suffix
        String lp_prefix = Optional.ofNullable(user.getCachedData().getMetaData().getPrefix()).orElse("").replaceAll("&", "§");
        String lp_suffix = Optional.ofNullable(user.getCachedData().getMetaData().getSuffix()).orElse("").replaceAll("&", "§");

        if(player.hasPermission("SteeeveChat.colors")) {
            message = message.replaceAll("&", "§");
        }

        String toSend = message
                .replaceAll("&", "§")
                .replaceAll("%player%", player.getUsername())
                .replaceAll("%server%", player.getCurrentServer().toString())
                .replaceAll("%lp_prefix%", lp_prefix)
                .replaceAll("%lp_suffix%", lp_suffix);

        return toSend;
    }
}
