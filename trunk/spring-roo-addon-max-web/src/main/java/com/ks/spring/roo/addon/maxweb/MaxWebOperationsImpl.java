package com.ks.spring.roo.addon.maxweb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.TemplateUtils;

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
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + "backoffice" + separator + entityStrings + separator + "create.jsp" ),"entity-create.jsp-template");
		// update.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + "backoffice" + separator + entityStrings + separator + "update.jsp" ),"entity-update.jsp-template");
		// show.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + "backoffice" + separator + entityStrings + separator + "show.jsp" ),"entity-show.jsp-template");
		// list.jsp
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF" + separator + "views" + separator + "backoffice" + separator + entityStrings + separator + "list.jsp" ),"entity-list.jsp-template");
		
		for (Entry<String, String> entry : map.entrySet()) {
			templateSetup(webClazz, serviceClazz, entityStringA, entityStringa, entityStrings, entry);
		}
		// properties
		String entityFully = serviceClazz.getFullyQualifiedTypeName().replace("Service", "").replace("service", "domain");
		//			new JavaType(entityFully);
		insertI18nMessages(entityFully);
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
	private void insertI18nMessages(String entityFully) {
		String applicationProperties = pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/i18n/application_ko.properties");
		MutableFile mutableApplicationProperties = null;
		entityFully = entityFully.replace(".", "_").toLowerCase();
		try {
			if (fileManager.exists(applicationProperties)) {
				mutableApplicationProperties = fileManager.updateFile(applicationProperties);
				String originalData = convertStreamToString(mutableApplicationProperties.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(mutableApplicationProperties.getOutputStream()));
				// entity class를 받아야 함.
				out.write(originalData);
				out.write(entityFully+"_id=Id\n");
				out.write(entityFully+"_name=Name\n");
				out.write(entityFully+"_email=Email\n");
				out.close();

			} else {
				throw new IllegalStateException("Could not acquire " + applicationProperties);
			}
		} catch (Exception e) {
			System.out.println("---> " + e.getMessage());
			throw new IllegalStateException(e);
		}

	}

	private String convertStreamToString(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}	
}