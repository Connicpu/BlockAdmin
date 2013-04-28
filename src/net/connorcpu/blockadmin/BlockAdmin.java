package net.connorcpu.blockadmin;

import javax.script.ScriptException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockAdmin extends JavaPlugin implements Listener {
	public static BlockAdmin pluginInstance;
	
	WebServer webServer;
	
	public BlockAdmin() {
		super();
		pluginInstance = this;
		webServer = new WebServer(this, 7070);
	}
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		try {
			webServer.enable();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		webServer.disable();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		return false;
	}
}
