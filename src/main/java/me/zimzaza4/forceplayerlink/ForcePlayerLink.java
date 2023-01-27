package me.zimzaza4.forceplayerlink;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {

                PlayerLink playerLink = InstanceHolder.getPlayerLink();

                FloodgateApi floodgateApi = FloodgateApi.getInstance();
                if (floodgateApi.isFloodgatePlayer(event.getPlayer().getUniqueId())) {
                    System.out.println("Bedrock Player");
                    playerLink.isLinkedPlayer(event.getPlayer().getUniqueId()).whenComplete((isLinked, t) -> {
                        if (isLinked) {
                            return;
                        }
                        System.out.println("Linked");
                        FloodgatePlayer floodgatePlayer = floodgateApi.getPlayer(event.getPlayer().getUniqueId());
                        String name = floodgatePlayer.getUsername();
                        playerLink.linkPlayer(event.getPlayer().getUniqueId(), createOfflinePlayerUuid(name), name);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                event.getPlayer().kickPlayer("正在为您LinkAccount, 请重新进入");
                            }
                        }.runTask(instance);
                    });
                }
            }
        }.runTaskLater(this, 2);
    }


    private UUID createOfflinePlayerUuid(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }
}
