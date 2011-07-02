package com.ks.spring.roo.addon.maxweb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.addon.entity.EntityMetadata;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.TypeLocationService;
import org.springframework.roo.classpath.TypeManagementService;
import org.springframework.roo.classpath.details.BeanInfoUtils;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.scanner.MemberDetails;
import org.springframework.roo.classpath.scanner.MemberDetailsScanner;
import org.springframework.roo.metadata.MetadataDependencyRegistry;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.support.logging.HandlerUtils;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.StringUtils;
import org.springframework.roo.support.util.TemplateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ks.spring.roo.addon.maxweb.support.XmlUtils;

/**
 * Implementation of operations this add-on offers.
 *
 * @since 1.1
 */
@Component 
@Service
public class MaxWebOperationsImpl implements MaxWebOperations {
	
	@Reference private MetadataService metadataService;
	@Reference private PhysicalTypeMetadataProvider physicalTypeMetadataProvider;
	@Reference private ProjectOperations projectOperations;
	@Reference private TypeLocationService typeLocationService;
	@Reference private TypeManagementService typeManagementService;
	@Reference private FileManager fileManager;
	@Reference private PathResolver pathResolver;
	@Reference private Shell shell;
	
	@Reference private MetadataDependencyRegistry metadataDependencyRegistry;
	@Reference private MemberDetailsScanner memberDetailsScanner;
	
	private static char separator = File.separatorChar;
//	private JavaBeanMetadata beanInfoMetadata;
	private static final Logger logger = HandlerUtils.getLogger(MaxWebOperationsImpl.class);
	private JavaType entity;
	
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
		this.entity = new JavaType(serviceClazz.getFullyQualifiedTypeName().replace("Service", "").replace(".service.", ".domain."));
		// Retrieve metadata for the Java source type the annotation is being added to
		String id = physicalTypeMetadataProvider.findIdentifier(serviceClazz);
		if (id == null) {
			throw new IllegalArgumentException("Cannot locate source for '" + serviceClazz.getFullyQualifiedTypeName() + "'");
		}

		Map<String, String> map = new HashMap<String, String>();
		// 1) Controller.java
		map.put(pathResolver.getIdentifier(Path.SRC_MAIN_JAVA, webClazz.getFullyQualifiedTypeName().replace('.', separator)+".java" ),"EntityController.java-template");
		// order create java file ....
		
		for (Entry<String, String> entry : map.entrySet()) {
			templateSetup(webClazz, serviceClazz, entity, entry);
		}
		
		// 2) jsp
		// We need to lookup the metadata for the entity we are creating
//		BeanInfoMetadata beanInfoMetadata = (BeanInfoMetadata) metadataService.get(beanInfoMetadataKey);
//		this.beanInfoMetadata = beanInfoMetadata;
		////////////////////////////////////////////////////////////////////////////////////
		List<FieldMetadata> elegibleFields = getEligibleFieldMetadata(null);
		////////////////////////////////////////////////////////////////////////////////////
//		EntityMetadata entityMetadata = (EntityMetadata) metadataService.get(entityString);
		EntityMetadata entityMetadata = null;
		
		JspDocumentHelper helper = new JspDocumentHelper(metadataService, entity, entityMetadata, elegibleFields);
		
		String destinationDirectory = pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/views/backoffice/" + entity.getSimpleTypeName().toLowerCase()+"s");
		if (!fileManager.exists(destinationDirectory)) {
			fileManager.createDirectory(destinationDirectory);
		} else {
			File file = new File(destinationDirectory);
			Assert.isTrue(file.isDirectory(), destinationDirectory + " is a file, when a directory was expected");
		}
//		if (webScaffoldMetadata.getAnnotationValues().isList()) {
			// By now we have a directory to put the JSPs inside
			String listPath1 = destinationDirectory + "/list.jsp";
			writeToDiskIfNecessary(listPath1, helper.getListDocument());
//		} 
//		if (webScaffoldMetadata.getAnnotationValues().isShow()) {
			String showPath = destinationDirectory + "/show.jsp";
			writeToDiskIfNecessary(showPath, helper.getShowDocument());
//		}
//		if (webScaffoldMetadata.getAnnotationValues().isUpdate()) {
			String listPath3 = destinationDirectory + "/update.jsp";
			writeToDiskIfNecessary(listPath3, helper.getUpdateDocument());
//		}
//		if (webScaffoldMetadata.getAnnotationValues().isCreate()) {
			String listPath = destinationDirectory + "/create.jsp";
			writeToDiskIfNecessary(listPath, helper.getCreateDocument());
			//add 'create new' menu item
//			menuOperations.addMenuItem(
//					"web_mvc_jsp_" + beanInfoMetadata.getJavaBean().getSimpleTypeName().toLowerCase() + "_category", 
//					beanInfoMetadata.getJavaBean().getSimpleTypeName(), 
//					"web_mvc_jsp_create_" + beanInfoMetadata.getJavaBean().getSimpleTypeName().toLowerCase() + "_menu_item", 
//					"Create new " + beanInfoMetadata.getJavaBean().getSimpleTypeName(),
//					"/" + beanInfoMetadata.getJavaBean().getSimpleTypeName().toLowerCase() + "/form");
			
			// menu.xml ============================================
			
			//		<li<c:if test="${menuType eq 'badwords' or menuType eq 'comments' }"> class="fir on"</c:if>><a href="<spring:url value="/backoffice/comments"/>">응원글 관리</a>
			//			<ul>
			//				<li class="fir"><a href="<spring:url value="/backoffice/comments"/>">응원글 목록</a></li>
			//				<li><a href="<spring:url value="/backoffice/badwords"/>">금칙어 관리</a></li>
			//			</ul>
			//		</li>
			String menuFile = pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/layouts/backoffice_menu.jsp");
			MutableFile mutableMenuFile = null;
			try {
				String entityName = entity.getSimpleTypeName();
				if (fileManager.exists(menuFile)) {
					mutableMenuFile = fileManager.updateFile(menuFile);
					String originalData = convertStreamToString(mutableMenuFile.getInputStream());
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(mutableMenuFile.getOutputStream()));
					out.write(originalData);
					out.write("\n");
					out.write("<li<c:if test=\"${menuType eq '"+entityName.toLowerCase()+"'}\"> class=\"fir on\"</c:if>><a href=\"<spring:url value=\"/backoffice/"+entityName.toLowerCase()+"s\"/>\">"+entityName+" Page</a>\n");
					out.write("	<ul>\n");
					out.write("		<li class=\"fir\"><a href=\"<spring:url value=\"/backoffice/"+entityName.toLowerCase()+"s\"/>\">"+entityName+" List</a></li>\n");
					out.write("		<li><a href=\"<spring:url value=\"/backoffice/"+entityName.toLowerCase()+"s/create\"/>\">"+entityName+" Create</a></li>\n");
					out.write("	</ul>\n");
					out.write("</li>\n");
					out.close();

				} else {
					throw new IllegalStateException("Could not acquire " + menuFile);
				}
			} catch (Exception e) {
				System.out.println("---> " + e.getMessage());
				throw new IllegalStateException(e);
			}
			
//		} else {
//			menuOperations.cleanUpMenuItem("web_mvc_jsp_" + beanInfoMetadata.getJavaBean().getSimpleTypeName().toLowerCase() + "_category", "web_mvc_jsp_create_" + beanInfoMetadata.getJavaBean().getSimpleTypeName().toLowerCase() + "_menu_item");
//		}
		
		////////////////////////////////////////////////////////////////////////////////////
		
		// properties
//		String entityFully = serviceClazz.getFullyQualifiedTypeName().replace("Service", "").replace("service", "domain");
		//			new JavaType(entityFully);
		insertI18nMessages(entity);
	}

	/** return indicates if disk was changed (ie updated or created) */
	private boolean writeToDiskIfNecessary(String jspFilename, Document toWrite) {
		// Build a string representation of the JSP
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XmlUtils.writeMalformedXml(XmlUtils.createIndentingTransformer(), byteArrayOutputStream, toWrite.getChildNodes());
		String jspContent = byteArrayOutputStream.toString();
		
		// If mutableFile becomes non-null, it means we need to use it to write out the contents of jspContent to the file
		MutableFile mutableFile = null;
		if (fileManager.exists(jspFilename)) {
			// First verify if the file has even changed
			File f = new File(jspFilename);
			String existing = null;
			try {
				existing = FileCopyUtils.copyToString(new FileReader(f));
			} catch (IOException ignoreAndJustOverwriteIt) {}
			
			if (!jspContent.equals(existing)) {
				mutableFile = fileManager.updateFile(jspFilename);
			}
			
		} else {
			mutableFile = fileManager.createFile(jspFilename);
			Assert.notNull(mutableFile, "Could not create JSP file '" + jspFilename + "'");
		}
		
		try {
			if (mutableFile != null) {
				// We need to write the file out (it's a new file, or the existing file has different contents)
				FileCopyUtils.copy(jspContent, new OutputStreamWriter(mutableFile.getOutputStream()));
				// Return and indicate we wrote out the file
				return true;
			}
		} catch (IOException ioe) {
			throw new IllegalStateException("Could not output '" + mutableFile.getCanonicalPath() + "'", ioe);
		}
		
		// A file existed, but it contained the same content, so we return false
		return false;

	}	
	
//	private List<FieldMetadata> getElegibleFields() {
//		List<FieldMetadata> fields = new ArrayList<FieldMetadata>();
//		List<MethodMetadata> fieldsOrg = new ArrayList<MethodMetadata>();
//		for (MethodMetadata method : beanInfoMetadata.getPublicAccessors(false)) {
//			
////			JavaSymbolName propertyName = beanInfoMetadata.getPropertyNameForJavaBeanMethod(method);
////			FieldMetadata field = beanInfoMetadata.getFieldForPropertyName(propertyName);
//			
////			FieldMetadata field = beanInfoMetadata.getFieldForPropertyName(propertyName);
//			
////			if(field != null && hasMutator(field)) {
//			if(field != null) {
//				
//				// Never include id field (it shouldn't normally have a mutator anyway, but the user might have added one)
//				if (MemberFindingUtils.getAnnotationOfType(field.getAnnotations(), new JavaType("javax.persistence.Id")) != null) {
//					continue;
//				}
//				// Never include version field (it shouldn't normally have a mutator anyway, but the user might have added one)
//				if (MemberFindingUtils.getAnnotationOfType(field.getAnnotations(), new JavaType("javax.persistence.Version")) != null) {
//					continue;
//				}				
//				fields.add(field);
//			}
//		}
//		return fields;
//	}

	public List<FieldMetadata> getEligibleFieldMetadata(MemberDetails memberDetails) {
		logger.warning("[MaxLog]-param memberDetails("+entity.getFullyQualifiedTypeName()+") : "+memberDetails);
		if(memberDetails == null) memberDetails = getMemberDetails(entity);
//		if(metadataIdentificationString == null) metadataIdentificationString = "javax.persistence.Id";
		
		Assert.notNull(memberDetails, "Member details required");
		
		Map<JavaSymbolName, FieldMetadata> fields = new LinkedHashMap<JavaSymbolName, FieldMetadata>();
		for (MethodMetadata method : MemberFindingUtils.getMethods(memberDetails)) {
			// Only interested in accessors
			if (!BeanInfoUtils.isAccessorMethod(method)) {
				continue;
			}
			JavaSymbolName propertyName = BeanInfoUtils.getPropertyNameForJavaBeanMethod(method);
			FieldMetadata field = BeanInfoUtils.getFieldForPropertyName(memberDetails, propertyName);
			if (field == null || !BeanInfoUtils.hasAccessorAndMutator(field, memberDetails)) {
				continue;
			}
//			registerDependency(method.getDeclaredByMetadataId(), metadataIdentificationString);
			if (!fields.containsKey(propertyName)) {
				fields.put(propertyName, field);
			}
		}
		return Collections.unmodifiableList(new ArrayList<FieldMetadata>(fields.values()));
	}	
	
	
	
	public MemberDetails getMemberDetails(JavaType javaType) {
		PhysicalTypeMetadata physicalTypeMetadata = (PhysicalTypeMetadata) metadataService.get(PhysicalTypeIdentifier.createIdentifier(javaType, Path.SRC_MAIN_JAVA));
		Assert.notNull(physicalTypeMetadata, "Unable to obtain physical type metadata for type " + javaType.getFullyQualifiedTypeName());
		ClassOrInterfaceTypeDetails classOrInterfaceDetails = (ClassOrInterfaceTypeDetails) physicalTypeMetadata.getMemberHoldingTypeDetails();
		MemberDetails memberDetails = memberDetailsScanner.getMemberDetails(javaType.getFullyQualifiedTypeName(), classOrInterfaceDetails);
		logger.warning("[MaxLog]-getMemberDetails javatype : "+javaType);
		logger.warning("[MaxLog]-memberDetails : "+memberDetails);
		logger.warning("[MaxLog]-classOrInterfaceDetails : "+classOrInterfaceDetails);
		return memberDetails;
	}
//	private void registerDependency(String upstreamDependency, String downStreamDependency) {
//		if (metadataDependencyRegistry != null && StringUtils.hasText(upstreamDependency) && StringUtils.hasText(downStreamDependency) && !upstreamDependency.equals(downStreamDependency) && !MetadataIdentificationUtils.getMetadataClass(downStreamDependency).equals(MetadataIdentificationUtils.getMetadataClass(upstreamDependency))) {
//			metadataDependencyRegistry.registerDependency(upstreamDependency, downStreamDependency);
//		}
//	}
	
//	private boolean hasMutator(FieldMetadata fieldMetadata) {
//		for (MethodMetadata mutator : beanInfoMetadata.getPublicMutators()) {
//			if (fieldMetadata.equals(beanInfoMetadata.getFieldForPropertyName(beanInfoMetadata.getPropertyNameForJavaBeanMethod(mutator)))) return true;
//		}
//		return false;
//	}
	
	private void templateSetup(JavaType webClazz, JavaType serviceClazz, JavaType entity, Entry<String, String> entry) {
		
		MutableFile mutableFile = null;
		String path = entry.getKey();
		String file = entry.getValue();
		String entityStringA = entity.getSimpleTypeName();
		String entityStringa = entityStringA.toLowerCase();
		String entityStrings = entityStringa + "s";		
		
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

				if(entityStringA.endsWith("s")){
					input = input.replace("countEntitys", "count" + entityStringA + "es");
					input = input.replace("findAllEntitys", "findAll" + entityStringA + "es");
				}else{
					input = input.replace("countEntitys", "count" + entityStringA + "s");
					input = input.replace("findAllEntitys", "findAll" + entityStringA + "s");
				}
				
				input = input.replace("Entity", entityStringA);
				input = input.replace("entity", entityStringa);
				input = input.replace("__ENTITYS__", entityStrings);
				input = input.replace("__ENTITY_LEVEL_PACKAGE__", entity.getFullyQualifiedTypeName());
				input = input.replace("__ENTITY_SERVICE_LEVEL_PACKAGE__", serviceClazz.getFullyQualifiedTypeName());
				input = input.replace("__ENTITY_CONTROLLER__", webClazz.getSimpleTypeName());
				// Output the file for the user
				mutableFile = fileManager.createFile(path);
				FileCopyUtils.copy(input.getBytes(), mutableFile.getOutputStream());
			} catch (IOException ioe) {
				throw new IllegalStateException("Unable to create '" + file + "'", ioe);
			}
		}
	}
	private void insertI18nMessages(JavaType entity) {
		String applicationProperties = pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/i18n/application_ko.properties");
		MutableFile mutableApplicationProperties = null;
		String entityFully = entity.getFullyQualifiedTypeName().replace(".", "_").toLowerCase();
		try {
			if (fileManager.exists(applicationProperties)) {
				mutableApplicationProperties = fileManager.updateFile(applicationProperties);
				String originalData = convertStreamToString(mutableApplicationProperties.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(mutableApplicationProperties.getOutputStream()));
				// entity class를 받아야 함.
				out.write(originalData);
//				out.write(entityFully+"_id=Id\n");
//				out.write(entityFully+"_name=Name\n");
//				out.write(entityFully+"_email=Email\n");
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