package com.molean.isletopiadispatcher;

import com.molean.isletopia.shared.message.ServerMessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AutoSwitchServer implements Listener {
    public AutoSwitchServer() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaDispatcher.getPlugin());
    }

    @EventHandler
    public void onSwitch(PlayerJoinEvent event) {

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaDispatcher.getPlugin(), () -> {
            Player player = event.getPlayer();

            String birthDay = ParameterDao.get(player.getName(), "Birthday");
            if (birthDay == null || birthDay.isEmpty()) {
                player.sendMessage("§c[防沉迷系统] 你还没未填写生日!");
                player.sendMessage("§c[防沉迷系统] 输入/birthday 年 月 日 进行实名认证!");
                player.sendMessage("§c[防沉迷系统] 例如: /birthday 2019 9 9!");
                return;
            }

            LocalDate parse = null;
            try {
                parse = LocalDate.parse(birthDay, DateTimeFormatter.ofPattern("yyyy-M-d"));
            } catch (Exception e) {
                player.kick(Component.text("#生日数据解析错误,请联系管理员"));

                return;
            }
            LocalDate now = LocalDate.now();
            if (now.minusYears(18).isBefore(parse)) {
                int hour = LocalDateTime.now().getHour();
                if (hour != 20) {
                    Bukkit.getScheduler().runTask(IsletopiaDispatcher.getPlugin(), () -> {

                        player.kick(Component.text("#未成年人只能在周五/周六/周日20:00-21:00游玩"));
                    });
                    return;
                }
                DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
                if (!(dayOfWeek.equals(DayOfWeek.FRIDAY) ||
                        dayOfWeek.equals(DayOfWeek.SATURDAY) ||
                        dayOfWeek.equals(DayOfWeek.SUNDAY))) {
                    Bukkit.getScheduler().runTask(IsletopiaDispatcher.getPlugin(), () -> {

                        player.kick(Component.text("#未成年人只能在周五/周六/周日20:00-21:00游玩"));
                    });
                    return;
                }
            }

            if (player.isOp()) {
                return;
            }

            String server = getParameter(player.getName(), "server");
            if (server == null || server.isEmpty()) {
                String defaultServer = IsletopiaDispatcher.getMinTimeServer();
                setParameter(player.getName(), "server", defaultServer);
                server = defaultServer;
                Bukkit.getLogger().info(player.getName() + " 被分配到 " + server);
            }

            String lastServer = getParameter(player.getName(), "lastServer");
            if (lastServer != null) {
                server = lastServer;
            }
            String finalServer = server;

            Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaDispatcher.getPlugin(), (task) -> {
                if (!player.isOnline()) {
                    task.cancel();
                    return;
                }

                ServerMessageUtils.switchServer(player.getName(), finalServer);
            }, 0, 20 * 10);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
    }

    public static String getParameter(String player, String key) {
        return ParameterDao.get(player, key);
    }

    public static void setParameter(String player, String key, String value) {
        ParameterDao.set(player, key, value);
    }
}
