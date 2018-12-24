package com.pcl.core.cache.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
spring.cache.cache-names=visiondata-24h,visiondata-4h,visiondata-1h,visiondata-30m,visiondata-no-expiry,visiondata-update-cache
spring.cache.dynacache[0].cache-name
spring.cache.dynacache[0].jndi
spring.cache.dynacache[0].ttl
spring.cache.dynacache[1].cache-name
spring.cache.dynacache[1].jndi
spring.cache.dynacache[1].ttl
*/

@ConfigurationProperties(prefix="spring.cache")
public class CacheProperties {
	private List<String> cacheNames;
	private final Exscale exscale = new Exscale();
	private final List<DynaCache> dynacache = new ArrayList<>();
	
	public static class Exscale {
		private String gridName;
		private String url;
		
		public String getGridName() {
			return gridName;
		}

		public void setGridName(String gridName) {
			this.gridName = gridName;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "Exscale [gridName=" + gridName + "]";
		}
	}
	public static class DynaCache {
		private String cacheName;
		private int ttl;
		private String jndi;
		public String getCacheName() {
			return cacheName;
		}
		public void setCacheName(String cacheName) {
			this.cacheName = cacheName;
		}
		public int getTtl() {
			return ttl;
		}
		public void setTtl(int ttl) {
			this.ttl = ttl;
		}
		public String getJndi() {
			return jndi;
		}
		public void setJndi(String jndi) {
			this.jndi = jndi;
		}
		@Override
		public String toString() {
			return "DynaCache [cacheName=" + cacheName + ", ttl=" + ttl + ", jndi=" + jndi + "]";
		}
	}	
	public List<String> getCacheNames() {
		return cacheNames;
	}
	public void setCacheNames(List<String> cacheNames) {
		this.cacheNames = cacheNames;
	}
	
	public Exscale getExscale() {
		return exscale;
	}

	public List<DynaCache> getDynacache() {
		return dynacache;
	}
	@Override
	public String toString() {
		return "CacheProperties [cacheNames=" + cacheNames + "]";
	}
	
}
