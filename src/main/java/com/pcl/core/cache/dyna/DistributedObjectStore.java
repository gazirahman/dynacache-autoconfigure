package com.pcl.core.cache.dyna;

import java.util.ArrayList;
import java.util.Collection;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;

import com.ibm.websphere.cache.DistributedMap;
import com.pcl.core.cache.PCCacheManager;
import com.pcl.core.cache.dyna.DistributedObject;
import com.pcl.core.cache.dyna.DistributedObjectStore;


public class DistributedObjectStore {
	public final static String defaultCacheJndiName = "services/cache/distributedmap";
	private InitialContext context;
	private NameResolver nameResolver;
	
	private static DistributedObjectStore store;
	private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(DistributedObjectStore.class);	
	
	private Collection<DistributedObject> maps =  new ArrayList<DistributedObject>();

    public static DistributedObjectStore create() {
        synchronized (DistributedObjectStore.class) {
            if (store == null) {
            	store = new DistributedObjectStore();
            }
            return store;
        }
    }
	
	public void destroy() throws NamingException {
		if(context!=null) {
			context.close();
		}
		log.info("good job... jndi context is closed.");
	} 
	
	/**
	 * Specify the collection of Distributed Map instances to use for this CacheManager.
	 */
	
	public DistributedMap getMap(String jndiName) {
		DistributedMap out = null;
		if(!maps.isEmpty()) {
			for(DistributedObject obj: maps) {
				if(obj.getJndiName().equals(jndiName)) {
					out = obj.getDistributedMap();
					break;
				}
			}
		}
		if( out==null ) {
			try {
				DistributedObject obj = getDistributedObject(jndiName);
				maps.add(obj);
				out = obj.getDistributedMap();
			} catch (NamingException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return out;
	}
	
	private DistributedObjectStore() {
		nameResolver = new DefaultNameCacheResolver();
	}
	
	private DistributedObject getDistributedObject(String cacheInstance) throws NamingException {
		String jndiName = nameResolver.getJndiNameForRegion(cacheInstance);
		DistributedMap objectCache;
		context = new InitialContext();
		try {
			objectCache = (DistributedMap) context.lookup(jndiName);
		} catch (NameNotFoundException e) {
			try {
				objectCache = (DistributedMap) context.lookup(defaultCacheJndiName);
			} catch (NameNotFoundException e2) {
				log.error("failed to load cache -" + defaultCacheJndiName);
				throw new NameNotFoundException(
						"Could not find the default DistributedObjectCache: Check whether WebSphere's DynaCache service is enabled!");
			}
		}
		return new DistributedObject(jndiName, objectCache);
	}
	
	/**
	 * 
	 * NameResolver Interface
	 *
	 */
	static interface NameResolver {
		String getJndiNameForRegion(String regionName);
	}

	/**
	 * 
	 * DefaultNameCacheResolver inner class for cache name identity
	 *
	 */
	static class DefaultNameCacheResolver implements NameResolver {
		public String getJndiNameForRegion(String regionName) {
			if("".equals(regionName) || PCCacheManager.defaultCacheName.equals(regionName)) {
				return defaultCacheJndiName;
			} else {
				return regionName;
			}
		}
	}
}