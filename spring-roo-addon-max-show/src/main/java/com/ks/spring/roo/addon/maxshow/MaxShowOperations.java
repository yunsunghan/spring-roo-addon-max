package com.ks.spring.roo.addon.maxshow;


/**
 * Interface of operations this add-on offers. Typically used by a command type or an external add-on.
 *
 * @since 1.1
 */
public interface MaxShowOperations {

	/**
	 * Indicate commands should be available
	 * 
	 * @return true if it should be available, otherwise false
	 */
	boolean isCommandAvailable();

	/**
	 * Setup all add-on artifacts (dependencies in this case)
	 */
	void start(String topLevelPackage, String projectName);
}