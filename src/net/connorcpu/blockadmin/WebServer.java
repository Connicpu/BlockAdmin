package net.connorcpu.blockadmin;

import java.util.logging.Level;

import javax.script.ScriptException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

public class WebServer implements Runnable {
	public boolean succes;
	public final int port;
	private Server server;
	private BlockAdmin plugin;

	public WebServer(BlockAdmin instance, int port) {
		this.plugin = instance;
		this.port = port;
		this.succes = true;
	}

	public void enable() throws ScriptException {
		this.server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(this.port);
		this.server.addConnector(connector);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, new WebHandler() });
		this.server.setHandler(handlers);

		Thread webserverThread = new Thread(this);
		webserverThread.start();
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			this.plugin.getLogger().log(
					Level.SEVERE,
					"Cannot sleep thread for 500ms (0.5 seconds): "
							+ e.getMessage());
		}
	}

	public void run() {
		try {
			this.server.start();
			this.server.join();
		} catch (Exception e) {
			this.succes = false;
			this.plugin.getLogger().log(Level.SEVERE,
					"Cannot start web server at port " + this.port + "!");
		}
	}

	public void disable() {
		try {
			this.server.stop();
		} catch (Exception e) {
			this.succes = false;
			this.plugin.getLogger().log(Level.SEVERE,
					"Cannot stop web server at port " + this.port + "!");
		}
	}
}