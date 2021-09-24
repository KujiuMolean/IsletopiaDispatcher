package com.molean.isletopiadispatcher;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.obj.PlayerInfoObject;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getScheduler;


public final class IsletopiaDispatcher extends JavaPlugin implements MessageHandler<PlayerInfoObject> {

    private static IsletopiaDispatcher plugin;

    public static IsletopiaDispatcher getPlugin() {
        return plugin;
    }

    private static final String serverName = new File(System.getProperty("user.dir")).getName();

    public static String getServerName() {
        return serverName;
    }

    private static final Map<String, Long> serverPlayTime = new HashMap<>();

    public static Map<String, Long> getServerPlayTime() {
        return new HashMap<>(serverPlayTime);
    }

    public static String getMinTimeServer() {
        Long min = Long.MAX_VALUE;
        String minKey = null;
        for (String s : serverPlayTime.keySet()) {
            Long aLong = serverPlayTime.get(s);
            if (aLong < min) {
                min = aLong;
                minKey = s;
            }
        }
        return minKey;
    }

    private static final List<String> onlinePlayers = new ArrayList<>();

    public static List<String> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers);
    }

    private static final List<String> servers = new ArrayList<>();

    public static List<String> getServers() {
        return new ArrayList<>(servers);
    }


    @Override
    public void onEnable() {
        plugin = this;
        new AutoSwitchServer();
        new BirthdayCommand();
        RedisMessageListener.init();
        getScheduler().runTaskTimerAsynchronously(this, this::updatePlayTime, 60, 20 * 60);
        RedisMessageListener.setHandler("PlayerInfo", this, PlayerInfoObject.class);
    }

    @Override
    public void onDisable() {
        RedisMessageListener.destroy();
    }

    public void updatePlayTime() {
        System.out.println("update");
        long start = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000;
        for (String server : getServers()) {
            if (!server.startsWith("server")) {
                continue;
            }
            long serverRecentPlayTime = PlayTimeStatisticsDao.getServerRecentPlayTime(server, start);
            serverPlayTime.put(server, serverRecentPlayTime);
        }

    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject,PlayerInfoObject message) {
        List<String> players = message.getPlayers();
        Map<String, List<String>> playersPerServer = message.getPlayersPerServer();

        onlinePlayers.clear();
        onlinePlayers.addAll(players);

        for (String server : playersPerServer.keySet()) {
            List<String> serverPlayers = playersPerServer.get(server);
            playersPerServer.put(server, new ArrayList<>(serverPlayers));
        }

        servers.clear();
        servers.addAll(new ArrayList<>(playersPerServer.keySet()));
    }
}
