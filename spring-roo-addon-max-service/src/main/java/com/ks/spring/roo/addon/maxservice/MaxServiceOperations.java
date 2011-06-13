package com.ks.spring.roo.addon.maxservice;

import org.springframework.roo.model.JavaType;

/**
 * Interface of operations this add-on offers. Typically used by a command type or an external add-on.
 *
 * @since 1.1
 */
public interface MaxServiceOperations {

	/**
	 * Indicate commands should be available
	 * 
	 * @return true if it should be available, otherwise false
	 */
	boolean isCommandAvailable();

	/**
	 * Annotate the provided Java type with the trigger of this add-on
	 */
	void newMaxServiceClass(JavaType serviceClazz, JavaType entityClazz);
	
	void setup();
}