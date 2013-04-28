package net.connorcpu.blockadmin.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;

import net.connorcpu.blockadmin.BlockAdmin;

public class Module {
	protected final ScriptEngine engine;
	private final Invocable engineInvocable;
	
	public Module(ScriptEngineManager manager, File scriptFile) throws IOException, ScriptException {
		engine = manager.getEngineByName("JavaScript");
		engine.put("plugin", BlockAdmin.pluginInstance);
		engine.put("config", BlockAdmin.pluginInstance.getConfig());
		engine.put("server", BlockAdmin.pluginInstance.getServer());
		engine.eval(new InputStreamReader(new FileInputStream(scriptFile), "UTF-8"));
		engineInvocable = (Invocable)engine;
	}
	
	public void handleRequest(HttpServletResponse response, String action, String method, Object params, Object body) throws NoSuchMethodException, ScriptException {
		Object handler = engine.get("handler");
		engineInvocable.invokeMethod(handler, action, method, params, body);
	}
	
	public boolean canReuse() {
		try {
			return (Boolean)engineInvocable.invokeMethod(engine.get("meta"), "canReuse");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return false;
	}
}
