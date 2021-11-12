package com.molean.isletopiadispatcher;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.database.PlayTimeStatisticsDao;
import com.molean.isletopia.shared.pojo.obj.PlayerInfoObject;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getScheduler;


public final class IsletopiaDispatcher extends JavaPlugin implements Listener {



    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
    }

}
