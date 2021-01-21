package br.com.vinicius.core.global.mariadb.connect;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.global.logging.FormattedLogger;
import br.com.vinicius.core.global.utilitaries.Util;

public class MariaConnect {

	protected static final String CONFIG_FILE_NAME = "database.yml";

	private final Core plugin;
	private final File configFile;
	private final FormattedLogger logger;

	private String address;
	private int port;
	private String username;
	private String password;
	private String schema;
	private String url;

	private Connection connection;

	public MariaConnect(Core plugin, FormattedLogger logger) {
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
		this.logger = logger;

		this.address = "localhost";
		this.port = 3306;
		this.username = "root";
		this.password = "password";
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
				if (connectionSection.contains("username")) {
					this.username = connectionSection.getString("username");
				} else {
					logger.log(Level.WARNING, "Atributo ausente 'username' na configuracao. Usando o padrao '%s'",
							username);
				}
				if (connectionSection.contains("password")) {
					this.password = connectionSection.getString("password");
				} else {
					logger.log(Level.WARNING, "Atributo ausente 'password' na configuracao. Usando o padrao '%s'",
							password);
				}
				if (connectionSection.contains("schema")) {
					this.schema = connectionSection.getString("schema");
				} else {
					logger.log(Level.WARNING, "Atributo ausente 'schema' na configuracao. Usando o padrao '%s'",
							schema);
				}
			}

			this.url = "jdbc:mariadb://" + this.address + ":" + this.port + "/" + this.schema;
			return initializeConnection();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e,
					"Ocorreu um erro ao carregar a configuracao do banco de dados! Detalhes abaixo:");
			return false;
		}
	}

	public final boolean initializeConnection() {
		try {
			this.connection = DriverManager.getConnection(this.url, this.username, this.password);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public final boolean initialize() {
		try {
			Class.forName("org.mariadb.jdbc.Driver").newInstance();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public final Connection getConnection() {
		try {
			if (!this.connection.isClosed() && this.connection != null)
				return this.connection;
			
			Util.closeConnection(connection);
			connection = null;
			
			return connection = DriverManager.getConnection(this.url, this.username, this.password);
		} catch (SQLException e) {
			return null;
		}
	}

}
