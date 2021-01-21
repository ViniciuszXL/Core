package br.com.vinicius.core.global.redis;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.global.logging.FormattedLogger;
import br.com.vinicius.core.global.query.Query.Search;
import br.com.vinicius.core.global.redis.connect.RedisConnect;
import br.com.vinicius.core.global.utilitaries.Util;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

public class Redis {

	// CHANNELS //
	protected static final String SERVER_INFO_DATA_CHANNEL = "@SERVER_INFO";

	// KEYS //
	protected static final String CURRENT_SERVER_KEY = "#CURRENT_SERVER";

	protected static final String CURRENT_TAG_KEY = "#CURRENT_TAG";

	protected static final String FAKE_KEY = "#FAKE";

	protected static final String SKIN_KEY = "#SKIN";

	protected static final String STAFFCHAT_STATUS_KEY = "#IS_STAFFCHAT";

	protected static final String MODERATION_STATUS_KEY = "#IS_MODERATING";

	protected static final String PREMIUM_KEY = "#PREMIUM";

	protected static final String LOGGED_KEY = "#LOGGED";
	
	private final Core plugin;
	private final FormattedLogger logger;
	private final RedisConnect redisConnect;

	public Redis(Core plugin, FormattedLogger logger) {
		this.plugin = plugin;
		this.logger = logger;

		this.redisConnect = new RedisConnect(this.plugin, this.logger);
	}

	public final boolean initialize() {
		if (!redisConnect.loadConfiguration())
			return false;
		if (!redisConnect.initialize())
			return false;
		return true;
	}

	public final Jedis connection() {
		return redisConnect.getConnection();
	}

	public static byte[][] encodeMany(final String... strs) {
		byte[][] many = new byte[strs.length][];
		for (int i = 0; i < strs.length; i++) {
			many[i] = encode(strs[i]);
		}
		return many;
	}

	public static byte[] encode(final String str) {
		try {
			if (str == null)
				throw new JedisDataException("value sent to redis cannot be null");
			return str.getBytes(Protocol.CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new JedisException(e);
		}
	}

	public boolean setCurrentServer(String name, String serverName) {
		long millis = System.currentTimeMillis();

		try {
			connection().hset(CURRENT_SERVER_KEY, name.toLowerCase(), serverName);
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to set current server for player '%s'", name);
			return false;
		} finally {
			logger.log("setCurrentServer: " + Util.formatMillis(millis));
		}
	}

	public Search<String> getCurrentServer(String... names) {
		long millis = System.currentTimeMillis();

		try {
			if (names.length < 1)
				return Search.notFound();
			for (int i = 0; i < names.length; i++)
				names[i] = names[i].toLowerCase();
			List<String> result = connection().hmget(CURRENT_SERVER_KEY, names);
			return result.size() > 1 ? Search.foundMultiple(result) : Search.foundOne(result.get(0));
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to get current servers for the players '%s'", Arrays.toString(names));
			return Search.error();
		} finally {
			logger.log("getCurrentServer: " + Util.formatMillis(millis));
		}
	}

	public Search<String> getCurrentServer(List<String> names) {
		String[] namesArray = names.toArray(new String[names.size()]);
		return getCurrentServer(namesArray);
	}

	public final boolean setCurrentTag(String name, int id) {
		long millis = System.currentTimeMillis();

		try {
			connection().hset(CURRENT_TAG_KEY, name.toLowerCase(), String.valueOf(id));
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to get the current tag for the player '%s'", name);
			return false;
		} finally {
			logger.log("setCurrentTag: " + Util.formatMillis(millis));
		}
	}

	public final boolean removeCurrentTag(String name) {
		long millis = System.currentTimeMillis();

		try {
			connection().hdel(CURRENT_TAG_KEY, name.toLowerCase());
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to unset the current tag for the player '%s'", name);
			return false;
		} finally {
			logger.log("removeCurrentTag: " + Util.formatMillis(millis));
		}
	}

	public final boolean setStaffChat(String name) {
		long millis = System.currentTimeMillis();

		try {
			connection().hset(STAFFCHAT_STATUS_KEY, name.toLowerCase(), String.valueOf(true));
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to set the staffchat for the player '%s'", name);
			return false;
		} finally {
			logger.log("setStaffChat: " + Util.formatMillis(millis));
		}
	}

	public final boolean unsetStaffChat(String name) {
		long millis = System.currentTimeMillis();

		try {
			connection().hdel(STAFFCHAT_STATUS_KEY, name.toLowerCase());
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to unset the staffchat for the player '%s'", name);
			return false;
		} finally {
			logger.log("unsetStaffChat: " + Util.formatMillis(millis));
		}
	}

	public final Search<Boolean> isStaffChatting(String name) {
		long millis = System.currentTimeMillis();

		try {
			return Search.foundOne(Boolean.parseBoolean(connection().hget(STAFFCHAT_STATUS_KEY, name.toLowerCase())));
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Falha ao verificar o status de staff chat do player '%s'! Detalhes abaixo:",
					name);
			return Search.error();
		} finally {
			logger.log("isStaffChatting: " + Util.formatMillis(millis));
		}
	}

	public final boolean setModerating(String name) {
		long millis = System.currentTimeMillis();

		try {
			connection().hset(MODERATION_STATUS_KEY, name.toLowerCase(), String.valueOf(true));
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Falha ao definir o status de moderacao para o jogador '%s'! Detalhes abaixo:",
					name);
			return false;
		} finally {
			logger.log("setModerating: " + Util.formatMillis(millis));
		}
	}

	public final boolean unsetModerating(String name) {
		long millis = System.currentTimeMillis();

		try {
			connection().hdel(MODERATION_STATUS_KEY, name.toLowerCase());
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Falha ao desmarcar o status de moderacao do player '%s'! Detalhes abaixo:",
					name);
			return false;
		} finally {
			logger.log("unsetModerating: " + Util.formatMillis(millis));
		}
	}

	public final Search<Boolean> isModerating(String name) {
		long millis = System.currentTimeMillis();

		try {
			return Search.foundOne(Boolean.parseBoolean(connection().hget(MODERATION_STATUS_KEY, name.toLowerCase())));
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Falha ao verificar o status de moderacao do player '%s'! Detalhes abaixo:",
					name);
			return Search.error();
		} finally {
			logger.log("isModerating: " + Util.formatMillis(millis));
		}
	}

	public boolean setFake(String name, String fakeName) {
		long millis = System.currentTimeMillis();

		try {
			connection().hset(FAKE_KEY, name.toLowerCase(), fakeName.toString());
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to set fake for player '%s'", name.toLowerCase());
			return false;
		} finally {
			logger.log("setFake: " + Util.formatMillis(millis));
		}
	}

	public Search<String> getFake(String name) {
		long millis = System.currentTimeMillis();

		try {
			return Search.foundOne(connection().hget(FAKE_KEY, name.toLowerCase()));
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to get fake for player '%s'", name.toLowerCase());
			return Search.error();
		} finally {
			logger.log("getFake: " + Util.formatMillis(millis));
		}
	}
	
	public boolean setSkin(String name, String skinName) {
		long millis = System.currentTimeMillis();

		try {
			connection().hset(SKIN_KEY, name.toLowerCase(), skinName.toString());
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to set skin for player '%s'", name.toLowerCase());
			return false;
		} finally {
			logger.log("setSkin: " + Util.formatMillis(millis));
		}
	}

	public Search<String> getSkin(String name) {
		long millis = System.currentTimeMillis();

		try {
			return Search.foundOne(connection().hget(SKIN_KEY, name.toLowerCase()));
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to get skin for player '%s'", name.toLowerCase());
			return Search.error();
		} finally {
			logger.log("getSkin: " + Util.formatMillis(millis));
		}
	}

	public boolean setPremium(String name, boolean premium) {
		long millis = System.currentTimeMillis();

		try {
			connection().hset(PREMIUM_KEY, name.toLowerCase(), Boolean.toString(premium));
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to set premium boolean for player '%s'", name);
			return false;
		} finally {
			logger.log("setPremium: " + Util.formatMillis(millis));
		}
	}

	public Search<Boolean> getPremium(String name) {
		long millis = System.currentTimeMillis();

		try {
			return Search.foundOne(Boolean.parseBoolean(connection().hget(PREMIUM_KEY, name.toLowerCase())));
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to get premium boolean for player '%s'", name);
			return Search.error();
		} finally {
			logger.log("isPremium: " + Util.formatMillis(millis));
		}
	}

	public boolean setLogged(String name, boolean logged) {
		long millis = System.currentTimeMillis();

		try {
			connection().hset(LOGGED_KEY, name.toLowerCase(), Boolean.toString(logged));
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to set logged boolean for player '%s'", name);
			return false;
		} finally {
			logger.log("setLogged: " + Util.formatMillis(millis));
		}
	}

	public Search<Boolean> isLogged(String name) {
		long millis = System.currentTimeMillis();

		try {
			return Search.foundOne(Boolean.parseBoolean(connection().hget(LOGGED_KEY, name.toLowerCase())));
		} catch (Exception e) {
			logger.log(Level.WARNING, e, "Failed to get logged boolean for player '%s'", name);
			return Search.error();
		} finally {
			logger.log("isLogged: " + Util.formatMillis(millis));
		}
	}

}
