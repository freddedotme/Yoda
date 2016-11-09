package se.fredde.yoda;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Main
extends JavaPlugin
implements Listener {
    final String GENERATING = "&7Generating message...";
    final String ERROR = "&7Uh oh, something bad happened!";
    final String DIVIDER = "&6---";
    final String PERMISSION = "&7Insufficient permissions.";

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        final String message = e.getMessage();
        final Player player = e.getPlayer();
        final Server server = this.getServer();
        if (message.startsWith("!yoda ")) {
            e.setCancelled(true);
            if (player.hasPermission("yoda.use")) {
                String[] yoda = message.split(" ", 2);
                if (yoda.length == 2) {
                    final String chosenOne = yoda[1];
                    player.sendMessage(this.cc("&7Generating message..."));
                    this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, new BukkitRunnable(){

                        public void run() {
                            String AsyncChosenOne = null;
                            try {
                                AsyncChosenOne = URLEncoder.encode(chosenOne, "UTF-8");
                                String address = "https://yoda.p.mashape.com/yoda?sentence=" + AsyncChosenOne;
                                URL url = null;
                                try {
                                    url = new URL(address);
                                    URLConnection conn = null;
                                    try {
                                        conn = url.openConnection();
                                        conn.setRequestProperty("X-Mashape-Key", "fJq9SaVIJ6mshcQE32mab76hOy3Rp1fMPpCjsnZgSrviznqfHV");
                                        conn.setRequestProperty("Accept", "text/plain");
                                        InputStream is = conn.getInputStream();
                                        if (conn instanceof HttpURLConnection) {
                                            HttpURLConnection httpConn = (HttpURLConnection)conn;
                                            if (httpConn.getResponseCode() == 200) {
                                                server.broadcastMessage(Main.this.cc("&6---"));
                                                server.broadcastMessage(Main.this.cc("&a&lYoda:&r&a " + Main.convertStreamToString(is)));
                                                server.broadcastMessage(Main.this.cc("&8Message: &7" + message));
                                                server.broadcastMessage(Main.this.cc(new StringBuilder().append("&8Submitted by: &7").append(player.getName()).toString()) + "");
                                                server.broadcastMessage(Main.this.cc("&6---"));
                                            } else {
                                                player.sendMessage(Main.this.cc("&7Uh oh, something bad happened!"));
                                            }
                                        }
                                    }
                                    catch (IOException e1) {
                                        e1.printStackTrace();
                                        player.sendMessage(Main.this.cc("&7Uh oh, something bad happened!"));
                                    }
                                }
                                catch (MalformedURLException e1) {
                                    e1.printStackTrace();
                                    player.sendMessage(Main.this.cc("&7Uh oh, something bad happened!"));
                                }
                            }
                            catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                                player.sendMessage(Main.this.cc("&7Uh oh, something bad happened!"));
                            }
                        }
                    });
                } else {
                    player.sendMessage(this.cc("&7Uh oh, something bad happened!"));
                }
            } else {
                player.sendMessage(this.cc("&7Insufficient permissions."));
            }
        }
    }

    public String cc(String message) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)message);
    }

    static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is);
        Throwable throwable = null;
        try {
            String string = s.useDelimiter("\\A").hasNext() ? s.next() : "";
            return string;
        }
        catch (Throwable var3_4) {
            throwable = var3_4;
            throw var3_4;
        }
        finally {
            if (s != null) {
                if (throwable != null) {
                    try {
                        s.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                } else {
                    s.close();
                }
            }
        }
    }

}
