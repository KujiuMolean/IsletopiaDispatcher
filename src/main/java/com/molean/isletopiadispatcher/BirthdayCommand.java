package com.molean.isletopiadispatcher;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class BirthdayCommand implements CommandExecutor {
    public BirthdayCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("birthday")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 3) {
            commandSender.sendMessage("§c参数不足!");
            return true;
        }
        String raw = strings[0] + "-" + strings[1] + "-" + strings[2];
        LocalDate parse = null;
        try {
           parse= LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyy-M-d"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (parse == null) {
            commandSender.sendMessage("§c输入有误, 检查后重试!");
            System.out.println("parse " + raw + " failed");
            return true;
        }


        ParameterDao.set(commandSender.getName(), "Birthday", raw);
        Player player = (Player) commandSender;
        player.kick(Component.text("#填写生日后请重新进入游戏"));

        return true;
    }
}
