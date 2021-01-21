package br.com.vinicius.core.global.mongodb.connect;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.global.logging.FormattedLogger;

public class MongoConnect {

	protected static final String CONFIG_FILE_NAME = "database.yml";

	private final Core plugin;
	private final File configFile;
	private final FormattedLogger logger;

	private String address;
	private int port;
	private String schema;
	private String url;
	
	private MongoClient mongoClient;
	
	public MongoConnect(Core plugin, FormattedLogger logger) {
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
		this.logger = logger;

		this.address = "localhost";
		this.port = 3306;
		this.schema = "dust";
	}
	
	public final boolean loadConfiguration() {
		try {
			if (!configFile.exists()) {
				logger.log(Level.WARNING, "O arquivo de configuracao inexistente '%s'. Criando...", CONFIG_FILE_NAME);
				plugin.saveResource(CONFIG_FILE_NAME, true);
			}

			FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
			ConfigurationSection connectionSection = config.getConfigurationSection("database-connection");
			if (connectionSection == null) {
				logger.log(Level.WARNING, "Seção 'database-connection' em falta! Utilizando os valores padrões.");
			} else {
				if (connectionSection.contains("address")) {
					this.address = connectionSection.getString("address");
				} else {
					logger.log(Level.WARNING, "Falta o atributo 'address' na configuracao. Usando o padrao '%s'",
							address);
				}
				if (connectionSection.contains("port")) {
					this.port = connectionSection.getInt("port");
				} else {
					logger.log(Level.WARNING, "Atributo ausente 'port' na configuracao. Usando o padrao '%s'", port);
				}
				if (connectionSection.contains("schema")) {
					this.schema = connectionSection.getString("schema");
				} else {
					logger.log(Level.WARNING, "Atributo ausente 'schema' na configuracao. Usando o padrao '%s'",
							schema);
				}
			}

			this.url = "mongodb://" + this.address + ":" + this.port;
			return initializeConnection();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e,
					"Ocorreu um erro ao carregar a configuracao do banco de dados! Detalhes abaixo:");
			return false;
		}
	}
	
	public final boolean initializeConnection() {
		try {
			Class.forName("org.mongodb.driver").newInstance();
			this.mongoClient = MongoClients.create(this.url);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public final MongoDatabase getDatabase() {
		try {
			if (mongoClient != null)
				return mongoClient.getDatabase(schema);
			
			this.mongoClient = null;
			this.mongoClient = MongoClients.create(this.url);
			return mongoClient.getDatabase(schema);
		} catch (Exception e) {
			return null;
		}
	}
	
}
