package net.steeeve.steeeveschat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.steeeve.steeeveschat.events.ChatEvent;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Plugin(
        id = "steeeveschat",
        name = "Steeeves-Chat",
        version = "1.0.0",
        description = "Steeeve's Chat plugin for velocity. A simple chat plugin with a simple purpose.",
        authors = {"DarkFamine", "Steeeve"},
        dependencies = {
            @Dependency(id = "luckperms")
        }
)
public class steeeveschat {

    @Inject
    private final Logger logger;
    private final ProxyServer server;
    public static LuckPerms api;
    public static Config config;
    public static Path dataDir;
    public static List<String> blockedwords;

    //@Inject
    //private static Logger logger;
    //private ProxyServer server;

    @Inject
    public steeeveschat(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDir = dataDirectory;

        logger.info("DarkFamine's Chat Loaded!");
        this.config = new Config(dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        File blockedwordsfile = new File(dataDir.toFile(), "blocked-words.txt");
        //Make sure blacklisted-words.txt exists
        if (!blockedwordsfile.exists()) {
            try {
                InputStream in = this.getClass().getResourceAsStream("/blocked-words.txt");
                Files.copy(in, blockedwordsfile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("ERROR: Can't write default blacklist file (permissions/filesystem error?)");
            }
        }
        try{
            this.blockedwords = Files.readAllLines(blockedwordsfile.toPath());
        }
        catch(IOException e) {
            this.logger.error("IOException: " + e);
        }

        server.getEventManager().register(this, new ChatEvent(this.server, this.logger, this.config, this.blockedwords));
        setApi(LuckPermsProvider.get());
    }

    public static void setApi(LuckPerms api) {
        steeeveschat.api = api;
    }

    public static LuckPerms getApi() {
        return api;
    }

    public Config getConfig() {
        return config;
    }
}
