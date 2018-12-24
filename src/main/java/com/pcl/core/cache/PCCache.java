package com.pcl.core.cache;

import java.util.Map;

import org.springframework.cache.Cache;

public interface PCCache extends Cache {
	public void putAll(Map<String, Object> cacheEntries);
}
