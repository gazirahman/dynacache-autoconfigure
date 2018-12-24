package com.pcl.core.cache.config;

import static java.util.stream.Collectors.toList;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pcl.core.cache.PCCacheManager;
import com.pcl.core.cache.dyna.WASCache;


@Configuration
@ConditionalOnProperty(name="spring.cache.cache-names")
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {       
	private final CacheProperties  properties;
	
	public CacheAutoConfiguration(CacheProperties properties) {
		this.properties = properties;
	}
	
	@Bean(name="cacheManager")
	@ConditionalOnProperty(name="spring.cache.dynacache[0].cache-name")
	public CacheManager dynaCacheManager() {
		return new PCCacheManager(properties.getDynacache().stream().map(cache -> new WASCache.Builder()
					.withCacheRegion(cache.getCacheName())
					.withInstanceJNDIName(cache.getJndi())
					.withTimeToLiveInMinutes(cache.getTtl())
					.print()
					.build())
		.collect(toList()));
	}
}
