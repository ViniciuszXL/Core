package br.com.vinicius.core.global.networking.info;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.data.BasePlayerData;
import br.com.vinicius.core.player.BasePlayer;
import br.com.vinicius.core.utilitaries.announceament.Broadcast;
import br.com.vinicius.core.utilitaries.color.ColorUtil;
import br.com.vinicius.core.utilitaries.logging.FormattedLogger;
import net.md_5.bungee.api.ChatColor;

public final class MessageHandler<TPlayer extends BasePlayer<TPlayer, TData>, TData extends BasePlayerData> {

	private static final String CONFIG_FILE_NAME = "messages.yml";

	private final Core<TPlayer, TData> plugin;
	private final File configFile;
	private final FormattedLogger logger;

	private final TreeMap<Integer, String> autoMessages;
	private int autoMessageInterval;
	private String currentAutoMessage;

	public MessageHandler(Core<TPlayer, TData> plugin, FormattedLogger parentLogger) {
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
		this.logger = new FormattedLogger(parentLogger, "Messages");

		this.autoMessages = new TreeMap<Integer, String>();
		this.autoMessageInterval = 300;
		this.currentAutoMessage = null;
	}

	public final boolean loadConfiguration() {
		try {
			if (!configFile.exists()) {
				logger.log(Level.WARNING, "Arquivo de configuracao inexistente '%s'. Criando...", CONFIG_FILE_NAME);
				plugin.saveResource(CONFIG_FILE_NAME, true);
			}

			FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

			ConfigurationSection autoMessageSection = config.getConfigurationSection("auto-messages");
			if (autoMessageSection == null) {
				logger.log(Level.WARNING, "Falta secao 'auto-mensagens'. Usando valores padrao!");
			} else {
				if (autoMessageSection.contains("message-interval")) {
					this.autoMessageInterval = autoMessageSection.getInt("message-interval");
				} else {
					logger.log(Level.WARNING,
							"Falta o atributo 'message-interval' na configuracao. Usando o padrao '%d'",
							autoMessageInterval);
				}
			}

			if (this.autoMessageInterval > 300)
				this.autoMessageInterval = 300;
			return true;
		} catch (

		Exception e) {
			logger.log(Level.SEVERE, e,
					"Ocorreu um erro ao carregar a configuracao automatica da mensagem! Detalhes abaixo:");
			return false;
		}
	}

	public void addAutoMessage(int id, String message) {
		this.autoMessages.put(id, message);
	}

	public void removeAutoMessage(int id) {
		this.autoMessages.remove(id);
	}

	public Map<Integer, String> getAutoMessages() {
		return autoMessages;
	}

	public String getCurrentAutoMessage() {
		return currentAutoMessage;
	}

	private int currentId = -1;
	private int count = 0;

	public synchronized void countMessages() {
		count += 1;

		if (count >= autoMessageInterval) {
			count = 0;
			Entry<Integer, String> nextEntry = autoMessages.higherEntry(currentId);
			if (nextEntry == null)
				nextEntry = autoMessages.firstEntry();
			if (nextEntry == null)
				return;

			currentId = nextEntry.getKey();
			currentAutoMessage = ChatColor.translateAlternateColorCodes('&', nextEntry.getValue());
			Broadcast.all(" ", ColorUtil.BOLD_E + "[DUST] " + currentAutoMessage, " ");
		}
	}
}
