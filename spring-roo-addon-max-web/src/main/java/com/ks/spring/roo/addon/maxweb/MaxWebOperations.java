package com.ks.spring.roo.addon.maxweb;

import org.springframework.roo.model.JavaType;

/**
 * Interface of operations this add-on offers. Typically used by a command type or an external add-on.
 *
 * @since 1.1
 */
public interface MaxWebOperations {

	boolean isCommandAvailable();
	void newMaxWebClass(JavaType webClazz, JavaType serviceClazz);
}