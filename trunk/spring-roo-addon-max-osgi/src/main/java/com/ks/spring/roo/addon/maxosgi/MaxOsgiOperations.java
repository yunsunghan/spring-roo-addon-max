package com.ks.spring.roo.addon.maxosgi;


/**
 * Interface of operations this add-on offers. Typically used by a command type or an external add-on.
 *
 * @since 1.1
 */
public interface MaxOsgiOperations {

	/**
	 * Indicate commands should be available
	 * 
	 * @return true if it should be available, otherwise false
	 */
	boolean isCommandAvailable();

	/**
	 * install
	 */
	void install(String version);
	
	/**
	 * uninstall
	 */
	void uninstall();
}