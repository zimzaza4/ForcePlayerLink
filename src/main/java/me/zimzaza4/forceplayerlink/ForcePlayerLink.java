package me.zimzaza4.forceplayerlink;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.InstanceHolder;
import org.geysermc.floodgate.api.link.PlayerLink;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class ForcePlayerLink extends JavaPlugin implements Listener {

    private static ForcePlayerLink instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {

        PlayerLink playerLink = InstanceHolder.getPlayerLink();

        FloodgateApi floodgateApi = FloodgateApi.getInstance();
        if (floodgateApi.isFloodgatePlayer(event.getUniqueId())) {
            getLogger().info("Bedrock Player");
            playerLink.isLinkedPlayer(event.getUniqueId()).whenComplete((isLinked, t) -> {
                if (isLinked) {
                    return;
                }
                getLogger().info("Linked");
                FloodgatePlayer floodgatePlayer = floodgateApi.getPlayer(event.getUniqueId());
                String name = floodgatePlayer.getUsername();
                if (getConfig().getBoolean("replace-space")) {
                    name = name.replace(" ", "_");
                }
                playerLink.linkPlayer(event.getUniqueId(), createOfflinePlayerUuid(name), name);

                event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "正在为您LinkAccount, 请重新进入")

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player player = Bukkit.getPlayer(event.getUniqueId());
                        if (player != null) {
                            player.kickPlayer("正在为您LinkAccount, 请重新进入");
                        }
                    }
                }.runTaskLater(this, 2);
            });
        }
    }


    private UUID createOfflinePlayerUuid(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }
}
