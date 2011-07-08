package com.ks.spring.roo.addon.maxosgi;

import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.support.logging.HandlerUtils;

/**
 * Implementation of operations this add-on offers.
 *
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class MaxOsgiOperationsImpl implements MaxOsgiOperations {
	
	/**
	 * MetadataService offers access to Roo's metadata model, use it to retrieve any available metadata by its MID
	 */
	@Reference private MetadataService metadataService;
	
	/**
	 * Use the PhysicalTypeMetadataProvider to access information about a physical type in the project
	 */
	@Reference private PhysicalTypeMetadataProvider physicalTypeMetadataProvider;
	
	/**
	 * Use ProjectOperations to install new dependencies, plugins, properties, etc into the project configuration
	 */
	@Reference private ProjectOperations projectOperations;

	/**
	 * Use TypeLocationService to find types which are annotated with a given annotation in the project
	 */
	@Reference private TypeLocationService typeLocationService;

	@Reference private FileManager fileManager;
	@Reference private PathResolver pathResolver;
	@Reference private Shell shell;
	private static final Logger logger = HandlerUtils.getLogger(MaxOsgiOperationsImpl.class);
	
	/** {@inheritDoc} */
	public boolean isCommandAvailable() {
		// 프로젝트가 생성하기 전,후와 상관없이 항상  command가 나와야 한다.
//		return getProjectMetadata() == null;
		return true;
	}

	public final ProjectMetadata getProjectMetadata() {
		return (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
	}
	
	/** {@inheritDoc} */
	public void install(String version) {
		// MAX OSGI INSTALL
		logger.info("[MAX] install start... ");
		shell.executeCommand("osgi start --url http://spring-roo-addon-max.googlecode.com/svn/repo/com.ks.spring.roo.addon.maxsetup-" + version + ".jar");
		logger.info("[MAX] add bundle - com.ks.spring.roo.addon.maxsetup-" + version + ".jar...");
		shell.executeCommand("osgi start --url http://spring-roo-addon-max.googlecode.com/svn/repo/com.ks.spring.roo.addon.maxservice-" + version + ".jar");
		logger.info("[MAX] add bundle - com.ks.spring.roo.addon.maxservice-" + version + ".jar...");
		shell.executeCommand("osgi start --url http://spring-roo-addon-max.googlecode.com/svn/repo/com.ks.spring.roo.addon.maxweb-" + version + ".jar");
		logger.info("[MAX] add bundle - com.ks.spring.roo.addon.maxweb-" + version + ".jar...");
		shell.executeCommand("osgi start --url http://spring-roo-addon-max.googlecode.com/svn/repo/com.ks.spring.roo.addon.maxshow-" + version + ".jar");
		logger.info("[MAX] add bundle - com.ks.spring.roo.addon.maxshow-" + version + ".jar...");
		logger.info("[MAX] install end... ");
	}

	/** {@inheritDoc} */
	public void uninstall() {
		// MAX OSGI UNINSTALL
		logger.info("[MAX] uninstall start... ");
		shell.executeCommand("osgi uninstall --bundleSymbolicName com.ks.spring.roo.addon.maxsetup");
		logger.info("[MAX] remove bundle com.ks.spring.roo.addon.maxsetup... ");
		shell.executeCommand("osgi uninstall --bundleSymbolicName com.ks.spring.roo.addon.maxservice");
		logger.info("[MAX] remove bundle com.ks.spring.roo.addon.maxservice... ");
		shell.executeCommand("osgi uninstall --bundleSymbolicName com.ks.spring.roo.addon.maxweb");
		logger.info("[MAX] remove bundle com.ks.spring.roo.addon.maxweb... ");
		shell.executeCommand("osgi uninstall --bundleSymbolicName com.ks.spring.roo.addon.maxshow");
		logger.info("[MAX] remove bundle com.ks.spring.roo.addon.maxshow... ");
		logger.info("[MAX] uninstall end... ");
	}
}