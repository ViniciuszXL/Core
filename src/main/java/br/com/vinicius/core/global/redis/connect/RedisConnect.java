package br.com.vinicius.core.global.redis.connect;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.global.logging.FormattedLogger;
import br.com.vinicius.core.global.utilitaries.Util;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisConnect {

	protected static final String CONFIG_FILE_NAME = "redis.yml";

	private final Core plugin;
	protected final File configFile;
	protected final FormattedLogger logger;

	private JedisPool connectionPool;
	private Jedis cachedConnection;

	private String address;
	private int port;
	private String password;

	public RedisConnect(Core plugin, FormattedLogger parentLogger) {
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
		this.logger = new FormattedLogger(parentLogger, "Redis");

		this.address = "localhost";
		this.port = 6379;
		this.password = "password";
	}

	public final boolean loadConfiguration() {
		try {
			if (!configFile.exists()) {
				logger.log(Level.WARNING, "Arquivo de configuracao inexistente '%s'. Criando...", CONFIG_FILE_NAME);
				plugin.saveResource(CONFIG_FILE_NAME, true);
			}

			FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

			ConfigurationSection connectionSection = config.getConfigurationSection("redis-connection");
			if (connectionSection == null) {
				logger.log(Level.WARNING, "Secao ausente 'redis-connection'. Usando valores padrao!");
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
					logger.log(Level.WARNING, "Falta atributo 'port' na configuracao. Usando o padrao '%s'", port);
				}
				if (connectionSection.contains("password")) {
					this.password = connectionSection.getString("password");
				} else {
					logger.log(Level.WARNING, "Falta atributo 'password' na configuracao. Usando o padrao '%s'",
							password);
				}
			}

			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, e,
					"Ocorreu um erro ao carregar a configuracao do banco de dados! Detalhes abaixo:");
			return false;
		}
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getPassword() {
		return password;
	}
	
	public JedisPool getConnectionPool() {
		return connectionPool;
	}

	public final boolean initialize() {
		try {
			logger.log("Conectando ao redis...");
			this.connectionPool = new JedisPool(address, port);

			this.cachedConnection = this.connectionPool.getResource();
			if (password != null && password.length() > 0)
				this.cachedConnection.auth(password);

			logger.log("Conexao estabelecida!");
			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, e, "Ocorreu um erro ao inicializar a conexao redis! Detalhes abaixo:");
			return false;
		}
	}
	
	public Jedis getConnection() {
		try {
			if (cachedConnection != null && cachedConnection.isConnected())
				return cachedConnection;
			
			if (this.connectionPool.isClosed()) {
				Util.closeConnection(this.connectionPool);
				
				this.connectionPool = null;
				this.connectionPool = new JedisPool(this.address, this.port);
				
				Util.closeConnection(this.cachedConnection);
				this.cachedConnection = null;
			}
			
			this.cachedConnection = this.connectionPool.getResource();
			if (password != null && password.length() > 0)
				this.cachedConnection.auth(password);
			return this.cachedConnection;
		} catch (Exception e) {
			return null;
		}
	}

	public final void dispose() {
		try {
			if (connectionPool != null) {
				this.connectionPool.close();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e,
					"Ocorreu um erro ao tentar descartar os recursos do banco de dados! Detalhes abaixo:");
		}
	}
	
}
