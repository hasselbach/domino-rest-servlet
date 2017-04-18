package ch.hasselba.memcache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;

/**
 * MemcachedService
 * 
 * @author Sven Hasselbach
 *
 */
public class MemcachedService {

	private static final String MEMCACHE_NAMESPACE = MemcachedService.class.getCanonicalName();
	private static final String MEMCACHE_HOST = "127.0.0.1";
	private static final String MEMCACHE_PORT = "11211";
	private static MemcachedService instance = null;
	private static MemcachedClient client = null;

	private MemcachedService() {
		reInit();
	}

	public static synchronized MemcachedService getInstance() {

		if (instance == null) {
			instance = new MemcachedService();
		}
		return instance;
	}

	public void set(final String key, final int ttl, final Object o) {
		try {
			client.set(MEMCACHE_NAMESPACE + key, ttl, o);
		} catch (IllegalStateException ise) {
			ise.printStackTrace();
		} catch (OperationTimeoutException ote) {
			ote.printStackTrace();
		}
	}

	public Object get(String key) {
		Object o = null;

		try {
			o = client.get(MEMCACHE_NAMESPACE + key);
		} catch (IllegalStateException ise) {
			ise.printStackTrace();
		} catch (OperationTimeoutException ote) {
			ote.printStackTrace();
		}
		return o;
	}

	public Object delete(String key) {
		return getCache().delete(MEMCACHE_NAMESPACE + key);
	}

	public MemcachedClient getCache() {
		return client;
	}

	public void shutdown() {
		client.shutdown();
	}

	public void reInit() {
		try {
			List<InetSocketAddress> addr = AddrUtil.getAddresses(MEMCACHE_HOST + ":" + MEMCACHE_PORT);
			BinaryConnectionFactory factory = new BinaryConnectionFactory();
			client = new MemcachedClient(factory, addr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}