package br.com.vinicius.core;

import org.bukkit.plugin.java.JavaPlugin;

import br.com.vinicius.core.global.GlobalManagement;
import br.com.vinicius.core.global.logging.FormattedLogger;

public abstract class Core extends JavaPlugin {

	private final FormattedLogger logger;
	
	private final GlobalManagement globalManagement;
	
	public Core() {
		this.logger = new FormattedLogger(getLogger(), null);
		this.globalManagement = new GlobalManagement(this, logger);
	}
	
	public void onLoad() {
		
	}
	
	public void onEnable() {
		super.onEnable();
		if (!globalManagement.initialize()) return;
	}
	
	public void onDisable() {
		
	}
	
	public final GlobalManagement global() {
		return globalManagement;
	}
	
}
