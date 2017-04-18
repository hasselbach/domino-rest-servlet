package ch.hasselba.memcache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class MultiValueHolder<T> {
	
	final static int DEFAULT_TTL = 1;
	final static NullObject nullObject = new NullObject();
	
	private Set<String> ids;
	private int ttl = DEFAULT_TTL;
	private String keyPrefix = "";
	
	@SuppressWarnings("unused")
	private MultiValueHolder(){};
	
	public MultiValueHolder( final Set<String> ids ){
		this.ids = ids;
	}
	public MultiValueHolder( final Set<String> ids, final int ttl ){
		this.ids = ids;
		this.ttl = ttl;
	}
	public MultiValueHolder( final Set<String> ids, final String keyPrefix   ){
		this.ids = ids;
		this.keyPrefix = keyPrefix;
	}
	public MultiValueHolder( final Set<String> ids, final String keyPrefix , final int ttl   ){
		this.ids = ids;
		this.keyPrefix = keyPrefix;
		this.ttl = ttl;
	}
	public Map<String, T> getValues( final boolean noCache ){
		if( noCache )
			return loadValues(this.ids);
		
		return getValues();
	}
	
	public Map<String, T>  getUnCachedValues(){
		return getValues( true );
	}
	
	public Map<String, T> getValues(){
		return getValues( ttl );
	}

	@SuppressWarnings("unchecked")
	public Map<String, T> getValues( final int ttl ){
		MemcachedService c = MemcachedService.getInstance();
		
		HashMap<String, T> results = new HashMap<String, T>();
		HashSet<String> idsToLoad = new HashSet<String>();
		for( String id : ids ){
			Object result = c.get( keyPrefix + id );
			if( result instanceof NullObject ){
				results.put(id, null);
				break;
			}
			if( result != null ){
				results.put(id, (T) result);
				break;
			}
			idsToLoad.add( id );
		}
	
		if( idsToLoad.isEmpty() )
			return results;
		
		Map<String, T> loadedResults = loadValues( idsToLoad );

		for( Entry<String, T> entry  : loadedResults.entrySet() ){
			
			if( entry.getValue() == null ){
				c.set( keyPrefix + entry.getKey(), ttl, nullObject );
			}else{
				c.set( keyPrefix + entry.getKey(), ttl, entry.getValue() );
			}
			
			results.put( entry.getKey(), entry.getValue() );
		}

		return results;
	}

	public abstract Map<String, T> loadValues(final Set<String> ids);
}
