package net.steeeve.dfchat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.steeeve.dfchat.events.ChatEvent;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "dfchat",
        name = "DF-Chat",
        version = "1.1-SNAPSHOT",
        description = "DarkFamine's Chat plugin for velocity. A simple chat plugin with a simple purpose.",
        authors = {"DarkFamine"},
        dependencies = {
            @Dependency(id = "luckperms")
        }
)
public class Dfchat {

    @Inject
    private final Logger logger;
    private final ProxyServer server;
    public static LuckPerms api;
    public static Config config;
    public static Path dataDir;

    //@Inject
    //private static Logger logger;
    //private ProxyServer server;

    @Inject
    public Dfchat(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.config = config;
        this.dataDir = dataDirectory;

        logger.info("DarkFamine's Chat Loaded!");
        this.config = new Config(dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new ChatEvent(this.server, this.logger, this.config));
        setApi(LuckPermsProvider.get());
    }

    public static void setApi(LuckPerms api) {
        Dfchat.api = api;
    }

    public static LuckPerms getApi() {
        return api;
    }

    public Config getConfig() {
        return config;
    }
}
