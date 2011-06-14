package com.ks.spring.roo.addon.maxservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.DependencyScope;
import org.springframework.roo.project.DependencyType;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Repository;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.TemplateUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * Implementation of operations this add-on offers.
 *
 * @since 1.1
 */
@Component // Use these Apache Felix annotations to register your commands class in the Roo container
@Service
public class MaxServiceOperationsImpl implements MaxServiceOperations {
	
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
	@Reference private TypeManagementService typeManagementService;
	@Reference private FileManager fileManager;
	@Reference private PathResolver pathResolver;
	@Reference private Shell shell;
	private static char separator = File.separatorChar;
	
	/** {@inheritDoc} */
	public boolean isCommandAvailable() {
		// Check if a project has been created
		return projectOperations.isProjectAvailable();
	}

	/** {@inheritDoc} */
	public void newMaxServiceClass(JavaType serviceClazz, JavaType entityClazz) {
		// Use Roo's Assert type for null checks
		Assert.notNull(serviceClazz, "serviceClazz Java type required");
		Assert.notNull(entityClazz, "entityClazz Java type required");

		// Retrieve metadata for the Java source type the annotation is being added to
		String id = physicalTypeMetadataProvider.findIdentifier(entityClazz);
		if (id == null) {
			throw new IllegalArgumentException("Cannot locate source for '" + entityClazz.getFullyQualifiedTypeName() + "'");
		}

		Map<String, String> map = new HashMap<String, String>();

		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_JAVA, serviceClazz.getFullyQualifiedTypeName().replace('.', separator)+".java" ),"EntityService.java-template");

		JavaType SM_JavaType = new JavaType(serviceClazz.getFullyQualifiedTypeName()+"Impl");
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_JAVA, SM_JavaType.getFullyQualifiedTypeName().replace('.', separator)+".java" ),"EntityServiceImpl.java-template");

		JavaType DTO_JavaType = new JavaType(serviceClazz.getFullyQualifiedTypeName()+"Result");
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_JAVA, DTO_JavaType.getFullyQualifiedTypeName().replace('.', separator)+".java" ),"EntityServiceResult.java-template");

		for (Entry<String, String> entry : map.entrySet()) {

			MutableFile mutableFile = null;

			String path = entry.getKey();
			String file = entry.getValue();
			
			if (fileManager.exists(path)){
				mutableFile = fileManager.updateFile(path);
			}else{
				InputStream templateInputStream = TemplateUtils.getTemplate(getClass(), file);
				try {
					// Read template and insert the user's package
					String input = FileCopyUtils.copyToString(new InputStreamReader(templateInputStream));
					
					input = input.replace("__TOP_LEVEL_PACKAGE__", serviceClazz.getPackage().getFullyQualifiedPackageName());//topLevelPackage.getFullyQualifiedPackageName()
					
					String fqEntityName = physicalTypeMetadataProvider.findIdentifier(entityClazz);
					if (fqEntityName == null) {
						throw new IllegalArgumentException("Cannot locate source for '" + entityClazz.getFullyQualifiedTypeName() + "'");
					}
					String entityName = fqEntityName.substring(fqEntityName.lastIndexOf(".") + 1);
					input = input.replace("Entity", entityName);
					input = input.replace("entity", entityName.toLowerCase());
					input = input.replace("__ENTITY_LEVEL_PACKAGE__", entityClazz.getFullyQualifiedTypeName());
					input = input.replace("__ENTITY_SERVICE__", serviceClazz.getSimpleTypeName());
					// Output the file for the user
					mutableFile = fileManager.createFile(path);
					FileCopyUtils.copy(input.getBytes(), mutableFile.getOutputStream());
				} catch (IOException ioe) {
					throw new IllegalStateException("Unable to create '" + file + "'", ioe);
				}
			}
		}
	}

}