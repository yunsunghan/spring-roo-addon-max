package com.ks.spring.roo.addon.maxweb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Sample of a command class. The command class is registered by the Roo shell following an
 * automatic classpath scan. You can provide simple user presentation-related logic in this
 * class. You can return any objects from each method, or use the logger directly if you'd
 * like to emit messages of different severity (and therefore different colours on 
 * non-Windows systems).
 * 
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class MaxWebCommands implements CommandMarker { // All command types must implement the CommandMarker interface
	
	@Reference private MaxWebOperations operations;

	@CliAvailabilityIndicator({"max web"})
	public boolean isCommandAvailable() {
		return operations.isCommandAvailable();
	}

	@CliCommand(value = "max web", help = "Some helpful description")
	public void web(
			@CliOption(key = "class", mandatory = true, help = "The java type to apply this annotation to") JavaType webClazz,
			@CliOption(key = "service", mandatory = true, help = "The java type to apply this annotation to") JavaType serviceClazz
			) {
		operations.newMaxWebClass(webClazz,serviceClazz);
	}

}