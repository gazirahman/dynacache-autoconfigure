package com.pcl.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;



public class PCCacheManager extends AbstractCacheManager {
	private Collection<? extends PCCache> caches = new ArrayList<PCCache>();
	
    public static final String defaultCacheName = "visiondata-update-cache";	
    
    public PCCacheManager(List<? extends PCCache> caches) {
    	this.caches = caches;
    }

	@Override
	protected Collection<? extends PCCache> loadCaches() {
		return this.caches;
	}

}
