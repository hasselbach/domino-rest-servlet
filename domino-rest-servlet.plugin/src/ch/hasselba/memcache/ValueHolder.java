package ch.hasselba.memcache;

public abstract class ValueHolder<T> {

	final static int DEFAULT_TTL = 1;
	final static NullObject nullObject = new NullObject();

	private String id;
	private int ttl = DEFAULT_TTL;

	@SuppressWarnings("unused")
	private ValueHolder() {
	};

	public ValueHolder(final String id) {
		this.id = id;
	}

	public ValueHolder(final String id, final int ttl) {
		this.id = id;
		this.ttl = ttl;
	}

	public T getValue(final boolean noCache) {
		if (noCache)
			return loadValue();

		return getValue();
	}

	public T getUnCachedValue() {
		return getValue(true);
	}

	public T getValue() {
		return getValue(ttl);
	}

	@SuppressWarnings("unchecked")
	public T getValue(final int ttl) {
		MemcachedService c = MemcachedService.getInstance();

		Object result = c.get(id);
		if (result == null) {
			System.out.println("ID: " + id + " is null ");
		} else {
			System.out.println("ID: " + id + " HIT! ");
		}
		if (result instanceof NullObject) {
			return null;
		}
		if (result != null)
			return (T) result;

		result = loadValue();

		if (result == null) {
			c.set(id, ttl, nullObject);
		} else {
			c.set(id, ttl, result);
		}
		return (T) result;
	}

	public abstract T loadValue();
}
