package com.pcl.core.cache.dyna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.StringUtils;

//WebSphere DynaCache API
import com.ibm.websphere.cache.DistributedMap;
import com.ibm.websphere.cache.EntryInfo;
import com.pcl.core.cache.PCCache;


public class WASCache implements PCCache {

	private DistributedMap distCache = null;
	private String cacheRegion = null;
	private String instanceJNDIName;
	private int timeToLiveInMinutes = 1440*14; // 2 weeks
	private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(WASCache.class);

	public WASCache(){
	}
	public WASCache(Builder builder) {
		this.distCache = DistributedObjectStore.create().getMap(builder.instanceJNDIName);
		this.timeToLiveInMinutes = builder.timeToLiveInMinutes*60;
		this.distCache.setSharingPolicy(EntryInfo.SHARED_PUSH_PULL);
		
		this.cacheRegion = builder.cacheRegion;
		this.instanceJNDIName = builder.instanceJNDIName;
	}

	public String getName() {
		return cacheRegion;
	}

	public DistributedMap getNativeCache() {
		return this.distCache;
	}

	public ValueWrapper get(Object key) {
		Object val = distCache.get(asWASKey(key));
		//( val==null ) check is very important :) spent a day to figure it out from the spring code
		SimpleValueWrapper localSimpleValueWrapper = ( val==null )?null:unwrapValue(val);
		log.debug("_DGET['" + key + "']=" + localSimpleValueWrapper);
		return localSimpleValueWrapper;
	}

	public void put(Object key, Object value) {
		if(value!=null) {
			log.info("_DPUT['" + key + "']=" + value);
			distCache.put(asWASKey(key),  wrapValue((Serializable)value), 1, this.timeToLiveInMinutes, this.timeToLiveInMinutes, EntryInfo.SHARED_PUSH_PULL, new Object[]{});
		}
	}

	@Override
	public void putAll(Map<String, Object> cacheEntries) {
		for(String key : cacheEntries.keySet()){
			Object value = cacheEntries.get(key);
			if ( value instanceof Serializable ) {
				put(key, value);
				log.info("_DPUT['" + key + "']=" + value);
			} else {
				log.error("The object stored in cache must implement Serializable");
			}
		}
	}
	
	public Object read(Object key) {
		return get(asWASKey(key));
	}

	public void evict(Object key) {
		String inKey = key.toString();
		if(inKey.indexOf("*")!=-1) { // for admin clear with wild card *PC_PRODUCT_SVC*
			inKey = StringUtils.replace(inKey, "*", "");
			Set<String> keys =  distCache.keySet();
			for(String keyEntry : keys){			
				if(keyEntry.contains(inKey)){
					distCache.remove(keyEntry);
				}
			}
		} else {
			distCache.remove(asWASKey(key));
		}
	}
	public void clearAllEntries() {
		distCache.clear();
	}
	public void clear() {
		distCache.clear();
	}


	private String asWASKey(Object suppliedKey) {
		return this.instanceJNDIName + "." + this.cacheRegion + '.' + suppliedKey;
	}
	private void printAllEntries() {
		List<String> keys = new ArrayList<String>(distCache.keySet());
		for(int i = 0; i < keys.size(); i++){
			if(i==0) {
				log.info("Print all entries - " + keys.size());
			}
			String key = keys.get(i);
			Object value = distCache.get(key);
			log.info( i + " . " + key + " => " + value);
		}
	}

	public <T> T get(Object key, Class<T> arg1) {
		return (T)get(key);
	}
	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	private static byte[] objectToByteArray(Serializable paramObject) {
		return SerializationUtils.serialize(paramObject);
	}

	private static Object byteArrayToObject(byte[] paramArrayOfByte) {
		return SerializationUtils.deserialize(paramArrayOfByte);
	}

	private static SimpleValueWrapper wrapValue(Serializable paramObject) {
		byte[] arrayOfByte = objectToByteArray(paramObject);
		return new SimpleValueWrapper(arrayOfByte);
	}
	
	private static SimpleValueWrapper unwrapValue(Object byteLocalObject) {
		SimpleValueWrapper localCacheValueWrapper = (SimpleValueWrapper) byteLocalObject;
		Object localObject = null;
		
		try {
			localObject = byteArrayToObject((byte [])localCacheValueWrapper.get());
		} catch (Exception e) {
			throw new ValueRetrievalException(byteLocalObject, null, e.getCause());
		}	
		return new SimpleValueWrapper(localObject);
	}
	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static final class Builder {
		private String cacheRegion = null;
		private String instanceJNDIName;
		private int timeToLiveInMinutes = 1440*14; // 2 weeks
		public Builder withCacheRegion(String val) {
			this.cacheRegion = val;
			return this;
		}
		public Builder withInstanceJNDIName(String val) {
			this.instanceJNDIName = val;
			return this;
		}
		public Builder withTimeToLiveInMinutes(int val) {
			this.timeToLiveInMinutes = val;
			return this;
		}
		public WASCache build() {
			return new WASCache(this);
		}
		public Builder print() {
			log.info("Ttl set for name " + instanceJNDIName + " - " + timeToLiveInMinutes);
			return this;
		}
	}
	
}