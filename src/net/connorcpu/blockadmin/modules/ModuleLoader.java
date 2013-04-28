package net.connorcpu.blockadmin.modules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.connorcpu.blockadmin.BlockAdmin;

public class ModuleLoader {
	private static final String moduleDirectory = "./plugins/BlockAdmin/modules/";
	private final ScriptEngineManager manager = new ScriptEngineManager();
	private final Map<String, Module> moduleEngines = new HashMap<>();
	private final File rootFile = new File(moduleDirectory + "root.js");
	private final File rootModFile = new File(moduleDirectory + "root.mod");

	public void onEnable() throws IOException {
		rootFile.mkdirs();
		if (!rootFile.exists()) {
			FileOutputStream outputStream = new FileOutputStream(rootFile);
			try {
				FileChannel outputChannel = outputStream.getChannel();
				InputStream inputStream = BlockAdmin.class
						.getResourceAsStream("net/connorcpu/blockadmin/scripts/root.js");
				ReadableByteChannel inputChannel = Channels
						.newChannel(inputStream);
				outputChannel.transferFrom(inputChannel, 0,
						inputStream.available());
				outputChannel.close();
			} finally {
				outputStream.close();
			}
		}
		if (!rootModFile.exists()) {
			FileOutputStream outputStream = new FileOutputStream(rootModFile);
			try {
				FileChannel outputChannel = outputStream.getChannel();
				InputStream inputStream = BlockAdmin.class
						.getResourceAsStream("net/connorcpu/blockadmin/scripts/root.mod");
				ReadableByteChannel inputChannel = Channels
						.newChannel(inputStream);
				outputChannel.transferFrom(inputChannel, 0,
						inputStream.available());
				outputChannel.close();
			} finally {
				outputStream.close();
			}
		}
	}

	public Module getModule(String name) throws IOException, ScriptException {
		Module module = moduleEngines.get(name);
		if (module == null || !module.canReuse()) {
			module = new Module(manager, new File(moduleDirectory + name
					+ ".js"));
		}

		return module;
	}
}
