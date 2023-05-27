package org.taw.anticlog;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class Anticlog extends JavaPlugin implements Listener {

    private int combatTimeout;
    private Map<String, Date> combatPlayers;
    private String logoutMessage;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        combatTimeout = config.getInt("combatTimeout", 35);
        logoutMessage = config.getString("logoutMessage", "Combat log: %player% has logged out.");

       
        combatPlayers = new HashMap<>();


        getServer().getPluginManager().registerEvents(this, this);
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player p1 = (Player) event.getDamager();
            Player p2 = (Player) event.getEntity();


            setInCombat(p1, true);
            setInCombat(p2, true);


            p1.sendMessage(ChatColor.YELLOW + "You are now in combat!");
            p2.sendMessage(ChatColor.YELLOW + "You are now in combat!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();


        if (isInCombat(player)) {

            Bukkit.broadcastMessage(ChatColor.RED + logoutMessage.replace("%player%", player.getName()));


            logCombatLogout(player);
        }
    }

    private void setInCombat(Player player, boolean inCombat) {

        if (inCombat) {
            combatPlayers.put(player.getName(), new Date());
        } else {
            combatPlayers.remove(player.getName());
        }
    }

    private boolean isInCombat(Player player) {

        return combatPlayers.containsKey(player.getName());
    }

    private void logCombatLogout(Player player) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(new Date());

        System.out.println("Combat Logout: " + player.getName() + " logged out at " + currentTime);
    }
}

