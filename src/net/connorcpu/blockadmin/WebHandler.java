package net.connorcpu.blockadmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.connorcpu.blockadmin.modules.Module;
import net.connorcpu.blockadmin.modules.ModuleLoader;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class WebHandler extends AbstractHandler {
	
	private final ScriptEngine paramEngine;
	private final ModuleLoader moduleLoader;
	
	public WebHandler() throws ScriptException {
		paramEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		paramEngine.eval(new InputStreamReader(BlockAdmin.class.getResourceAsStream("net/connorcpu/blockadmin/scripts/paramBuilder.js")));
		
		moduleLoader = new ModuleLoader();
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpURI uri = baseRequest.getUri();
		String[] uriSplit = uri.getPath().split("/");
		String method = request.getMethod();
		
		String moduleName = "root";
		String action = "index";
		
		if (uriSplit.length > 1) {
			moduleName = uriSplit[1];
		}
		if (uriSplit.length > 2) {
			action = uriSplit[2];
		}
		
		Object parameters;
		Object requestBody = null;
		try {
			parameters = ((Invocable)paramEngine).invokeFunction("buildFromQuery", new URI(uri.toString()).getRawQuery());
			
			if (request.getContentLength() > 0) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
				requestBody = ((Invocable)paramEngine).invokeFunction("buildFromBody", reader);
				reader.close();
			}
		} catch (NoSuchMethodException | ScriptException | URISyntaxException e) {
			response.setStatus(500);
			response.getWriter().print("{\"message\":\"Internal server error\"}");
			e.printStackTrace();
			return;
		}
		
		response.setContentType("application/json; charset=utf-8");
		response.setStatus(200);
		baseRequest.setHandled(true);
		
		Module module;
		try {
			module = moduleLoader.getModule(moduleName);
		} catch (ScriptException e) {
			response.setStatus(500);
			response.getWriter().print("{\"message\":\"Internal server error\"}");
			e.printStackTrace();
			return;
		}
		
		try {
			module.handleRequest(response, action, method, parameters, requestBody);
		} catch (NoSuchMethodException | NullPointerException | ScriptException e) {
			response.setStatus(500);
			response.getWriter().print("{\"message\":\"Internal server error\"}");
			e.printStackTrace();
			return;
		}
	}
}







