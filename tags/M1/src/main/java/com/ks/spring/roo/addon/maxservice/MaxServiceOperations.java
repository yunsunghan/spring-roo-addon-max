package com.ks.spring.roo.addon.maxservice;

import org.springframework.roo.model.JavaType;

/**
 * Interface of operations this add-on offers. Typically used by a command type or an external add-on.
 *
 * @since 1.1
 */
public interface MaxServiceOperations {

	boolean isCommandAvailable();
	void newMaxServiceClass(JavaType serviceClazz, JavaType entityClazz);
}