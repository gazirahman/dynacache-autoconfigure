package com.pcl.core.cache.dyna;

import com.ibm.websphere.cache.DistributedMap;

public class DistributedObject {
	private String jndiName;
	private DistributedMap distributedMap;
	public DistributedObject(String jndiName, DistributedMap distributedMap) {
		this.jndiName = jndiName;
		this.distributedMap = distributedMap;
	}
	/**
	 * @return the jndiName
	 */
	public String getJndiName() {
		return jndiName;
	}
	/**
	 * @return the distributedMap
	 */
	public DistributedMap getDistributedMap() {
		return distributedMap;
	}
	
}