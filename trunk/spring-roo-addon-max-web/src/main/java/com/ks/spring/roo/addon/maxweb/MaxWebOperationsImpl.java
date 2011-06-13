package com.ks.spring.roo.addon.maxweb;

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
import org.springframework.roo.classpath.PhysicalTypeDetails;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.MutableClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.DependencyScope;
import org.springframework.roo.project.DependencyType;
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
public class MaxWebOperationsImpl implements MaxWebOperations {
	
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
	public void newMaxWebClass(JavaType webClazz, JavaType serviceClazz) {
		// Use Roo's Assert type for null checks
		Assert.notNull(webClazz, "serviceClazz Java type required");
		Assert.notNull(serviceClazz, "entityClazz Java type required");

		String entityStringA = serviceClazz.getSimpleTypeName().replace("Service", "");
		String entityStringa = entityStringA.toLowerCase();
		String entityStrings = entityStringa + "s";
		
		
		// Retrieve metadata for the Java source type the annotation is being added to
		String id = physicalTypeMetadataProvider.findIdentifier(serviceClazz);
		if (id == null) {
			throw new IllegalArgumentException("Cannot locate source for '" + serviceClazz.getFullyQualifiedTypeName() + "'");
		}

		Map<String, String> map = new HashMap<String, String>();
		// Controller.java
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_JAVA, webClazz.getFullyQualifiedTypeName().replace('.', separator)+".java" ),"EntityController.java-template");
		// create.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + entityStrings + separator + "create.jsp" ),"entity-create.jsp-template");
		// update.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + entityStrings + separator + "update.jsp" ),"entity-update.jsp-template");
		// show.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + entityStrings + separator + "show.jsp" ),"entity-show.jsp-template");
		// list.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + entityStrings + separator + "list.jsp" ),"entity-list.jsp-template");
		
		for (Entry<String, String> entry : map.entrySet()) {
			templateSetup(webClazz, serviceClazz, entityStringA, entityStringa,entityStrings, entry);
		}
	}
	
	/** {@inheritDoc} */
	public void setup() {
		// Install the add-on Google code repository needed to get the annotation 
		projectOperations.addRepository(new Repository("Max web Roo add-on repository", "Max web Roo add-on repository", "http://spring-roo-addon-max.googlecode.com/svn/trunk/spring-roo-addon-max-web"));
		List<Dependency> dependencies = new ArrayList<Dependency>();
		// Install the dependency on the add-on jar (
		dependencies.add(new Dependency("com.ks.spring.roo.addon.maxweb", "com.ks.spring.roo.addon.maxweb", "0.1.0.M1", DependencyType.JAR, DependencyScope.PROVIDED));
		// Install dependencies defined in external XML file
		for (Element dependencyElement : XmlUtils.findElements("/configuration/maxweb/dependencies/dependency", XmlUtils.getConfiguration(getClass()))) {
			dependencies.add(new Dependency(dependencyElement));
		}
		// Add all new dependencies to pom.xml
		projectOperations.addDependencies(dependencies);
		
		//////////////////////////////////////////////////////////////////////////////
		
		// common file
		Map<String, String> map = new HashMap<String, String>();
		// taglib.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + "__system" + separator + "taglibs.jsp" ),"common-system_taglibs.jsp-template");
		// taglib.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "layouts" + separator + "admin.jsp" ),"common-layout_admin.jsp-template");
		// front.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "layouts" + separator + "front.jsp" ),"common-layout_front.jsp-template");
		
		for (Entry<String, String> entry : map.entrySet()) {
			commonSetup(entry);
		}		
	}

	private void commonSetup(Entry<String, String> entry) {
		MutableFile mutableFile = null;
		String path = entry.getKey();
		String file = entry.getValue();
		if (fileManager.exists(path)){
			mutableFile = fileManager.updateFile(path);
		}else{
			InputStream templateInputStream = TemplateUtils.getTemplate(getClass(), file);
			try {
				String input = FileCopyUtils.copyToString(new InputStreamReader(templateInputStream));
				mutableFile = fileManager.createFile(path);
				FileCopyUtils.copy(input.getBytes(), mutableFile.getOutputStream());
			} catch (IOException ioe) {
				throw new IllegalStateException("Unable to create '" + file + "'", ioe);
			}
		}
	}
	
	private void templateSetup(JavaType webClazz, JavaType serviceClazz,
			String entityStringA, String entityStringa, String entityStrings,
			Entry<String, String> entry) {
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
				input = input.replace("__TOP_LEVEL_PACKAGE__", webClazz.getPackage().getFullyQualifiedPackageName());
				String fqEntityName = physicalTypeMetadataProvider.findIdentifier(serviceClazz);
				if (fqEntityName == null) {
					throw new IllegalArgumentException("Cannot locate source for '" + serviceClazz.getFullyQualifiedTypeName() + "'");
				}
				input = input.replace("Entity", entityStringA);
				input = input.replace("entity", entityStringa);
				input = input.replace("__ENTITYS__", entityStrings);
				input = input.replace("__ENTITY_LEVEL_PACKAGE__", serviceClazz.getFullyQualifiedTypeName());
				input = input.replace("__ENTITY_CONTROLLER__", webClazz.getSimpleTypeName());
				// Output the file for the user
				mutableFile = fileManager.createFile(path);
				FileCopyUtils.copy(input.getBytes(), mutableFile.getOutputStream());
			} catch (IOException ioe) {
				throw new IllegalStateException("Unable to create '" + file + "'", ioe);
			}
		}
	}	
}