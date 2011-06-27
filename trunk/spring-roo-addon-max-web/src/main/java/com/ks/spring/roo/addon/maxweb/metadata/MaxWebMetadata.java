package com.ks.spring.roo.addon.maxweb.metadata;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.style.ToStringCreator;
import org.springframework.roo.support.util.Assert;


/**
 * This type produces metadata for a new ITD. It uses an {@link ItdTypeDetailsBuilder} provided by 
 * {@link AbstractItdTypeDetailsProvidingMetadataItem} to register a field in the ITD and a new method.
 * 
 * @since 1.1.0
 */
public class MaxWebMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {
	private static final String PROVIDES_TYPE_STRING = MaxWebMetadata.class.getName();
	private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);
	private MaxWebAnnotationValues annotationValues;
	private JavaType targetObject;
	
	//////////////////////////////////////////////////////////
	// biz start...
	//////////////////////////////////////////////////////////
	
	public MaxWebMetadata(String identifier, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, MaxWebAnnotationValues annotationValues) {
		super(identifier, aspectName, governorPhysicalTypeMetadata);
		Assert.isTrue(isValid(identifier), "Metadata identification string '" + identifier + "' does not appear to be a valid");
		
		this.annotationValues = annotationValues; 
		this.targetObject = annotationValues.getTargetObject();
		
		
		
		
		// Adding a new sample method definition
//		builder.addMethod(getSaveOrUpdateMethod());
//		builder.addMethod(getRemoveMethod());
//		builder.addMethod(getFindByIdMethod());
//		builder.addMethod(getFindAccountEntriesMethod());
		builder.addMethod(getSampleMethod());
		// Create a representation of the desired output ITD
		itdTypeDetails = builder.build();
	}

	private MethodMetadata getSampleMethod() {
		// Specify the desired method name
		JavaSymbolName methodName = new JavaSymbolName("sampleMethod");
		
		// Check if a method with the same signature already exists in the target type
		MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
		if (method != null) {
			// If it already exists, just return the method and omit its generation via the ITD
			return method;
		}
		
		// Define method annotations (none in this case)
		List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
		
		// Define method throws types (none in this case)
		List<JavaType> throwsTypes = new ArrayList<JavaType>();
		
		// Define method parameter types (none in this case)
		List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
		
		// Define method parameter names (none in this case)
		List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
		
		// Create the method body
		InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
		bodyBuilder.appendFormalLine("System.out.println(\"Hello World\");");
		
		// Use the MethodMetadataBuilder for easy creation of MethodMetadata
		MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, JavaType.VOID_PRIMITIVE, parameterTypes, parameterNames, bodyBuilder);
		methodBuilder.setAnnotations(annotations);
		methodBuilder.setThrowsTypes(throwsTypes);
		
		return methodBuilder.build(); // Build and return a MethodMetadata instance
	}	
	/**
	 * 
	@Override
	public AccountServiceResult findAccountEntries(int firstResult, int maxResults) {
		return new AccountServiceResult(Account.findEntries(firstResult, maxResults),Account.countAccounts(),null);
	}
	 */
	private MethodMetadata getFindAccountEntriesMethod() {
		// Specify the desired method name
		JavaSymbolName methodName = new JavaSymbolName("findAccountEntries");
		// Check if a method with the same signature already exists in the target type
		MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
		if (method != null) {
			// If it already exists, just return the method and omit its generation via the ITD
			return method;
		}
		// Define method annotations (none in this case)
		List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
		// Define method throws types (none in this case)
		List<JavaType> throwsTypes = new ArrayList<JavaType>();
		// Define method parameter types (none in this case)
		List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
		parameterTypes.add(new AnnotatedJavaType(new JavaType(Integer.class.getName()), null));
		parameterTypes.add(new AnnotatedJavaType(new JavaType(Integer.class.getName()), null));
		// Define method parameter names (none in this case)
		List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
		parameterNames.add(new JavaSymbolName("firstResult"));
		parameterNames.add(new JavaSymbolName("maxResults"));
		// Create the method body
		InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
		String T = this.targetObject.getSimpleTypeName();
		bodyBuilder.appendFormalLine("return new "+T+"ServiceResult("+T+".findEntries(firstResult, maxResults),"+T+".count"+T+"s(),null);");
		
		// Use the MethodMetadataBuilder for easy creation of MethodMetadata
		JavaType returnObject = new JavaType(this.targetObject.getFullyQualifiedTypeName()+"ServiceResult");
		MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, returnObject, parameterTypes, parameterNames, bodyBuilder);
		methodBuilder.setAnnotations(annotations);
		methodBuilder.setThrowsTypes(throwsTypes);
		
		return methodBuilder.build(); // Build and return a MethodMetadata instance	
	}
	/**
	 * 
	@Override
	public Account findById(Long id) {
		return Account.findAccount(id);
	}
	 */
	private MethodMetadata getFindByIdMethod() {
		// Specify the desired method name
		JavaSymbolName methodName = new JavaSymbolName("findById");
		// Check if a method with the same signature already exists in the target type
		MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
		if (method != null) {
			// If it already exists, just return the method and omit its generation via the ITD
			return method;
		}
		// Define method annotations (none in this case)
		List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
		// Define method throws types (none in this case)
		List<JavaType> throwsTypes = new ArrayList<JavaType>();
		// Define method parameter types (none in this case)
		List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
		parameterTypes.add(new AnnotatedJavaType(new JavaType(Long.class.getName()), null));
		// Define method parameter names (none in this case)
		List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
		parameterNames.add(new JavaSymbolName("id"));
		// Create the method body
		InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
		bodyBuilder.appendFormalLine("return "+this.targetObject.getSimpleTypeName()+".findAccount(id);");
		
		// Use the MethodMetadataBuilder for easy creation of MethodMetadata
		MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, this.targetObject, parameterTypes, parameterNames, bodyBuilder);
		methodBuilder.setAnnotations(annotations);
		methodBuilder.setThrowsTypes(throwsTypes);
		
		return methodBuilder.build(); // Build and return a MethodMetadata instance	
	}
	/**
	 * 
	@Override
	public void remove(Account account) {
		account.remove();
	}
	 */
	private MethodMetadata getRemoveMethod() {
		// Specify the desired method name
		JavaSymbolName methodName = new JavaSymbolName("remove");
		// Check if a method with the same signature already exists in the target type
		MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
		if (method != null) {
			// If it already exists, just return the method and omit its generation via the ITD
			return method;
		}
		// Define method annotations (none in this case)
		List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
		// Define method throws types (none in this case)
		List<JavaType> throwsTypes = new ArrayList<JavaType>();
		// Define method parameter types (none in this case)
		List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
		parameterTypes.add(new AnnotatedJavaType(this.targetObject, null));
		// Define method parameter names (none in this case)
		List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
		parameterNames.add(this.targetObject.getArgName());
		// Create the method body
		InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
		String t = this.targetObject.getSimpleTypeName().toLowerCase();
		bodyBuilder.appendFormalLine(t+".remove();");
		
		// Use the MethodMetadataBuilder for easy creation of MethodMetadata
		MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, JavaType.VOID_PRIMITIVE, parameterTypes, parameterNames, bodyBuilder);
		methodBuilder.setAnnotations(annotations);
		methodBuilder.setThrowsTypes(throwsTypes);
		
		return methodBuilder.build(); // Build and return a MethodMetadata instance		
	}

	/**
	@Override
	public void saveOrUpdate(Account account) {
		if(account.getId() == null) {
			account.persist();
		}else{
			account.merge();
		}
	}
	 */
	private MethodMetadata getSaveOrUpdateMethod() {
		// Specify the desired method name
		JavaSymbolName methodName = new JavaSymbolName("saveOrUpdate");
		// Check if a method with the same signature already exists in the target type
		MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
		if (method != null) {
			// If it already exists, just return the method and omit its generation via the ITD
			return method;
		}
		// Define method annotations (none in this case)
		List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
		// Define method throws types (none in this case)
		List<JavaType> throwsTypes = new ArrayList<JavaType>();
		// Define method parameter types (none in this case)
		List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
		parameterTypes.add(new AnnotatedJavaType(this.targetObject, null));
		// Define method parameter names (none in this case)
		List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
		parameterNames.add(this.targetObject.getArgName());
		// Create the method body
		InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
		String t = this.targetObject.getSimpleTypeName().toLowerCase();
		bodyBuilder.appendFormalLine("if("+t+".getId() == null) { ");
		bodyBuilder.appendFormalLine(t+".persist();");
		bodyBuilder.appendFormalLine("}else{");
		bodyBuilder.appendFormalLine(t+".merge();");
		bodyBuilder.appendFormalLine("}");
		
		// Use the MethodMetadataBuilder for easy creation of MethodMetadata
		MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, JavaType.VOID_PRIMITIVE, parameterTypes, parameterNames, bodyBuilder);
		methodBuilder.setAnnotations(annotations);
		methodBuilder.setThrowsTypes(throwsTypes);
		
		return methodBuilder.build(); // Build and return a MethodMetadata instance
	}
		
	//////////////////////////////////////////////////////////
	// biz end...
	//////////////////////////////////////////////////////////

	private MethodMetadata methodExists(JavaSymbolName methodName, List<AnnotatedJavaType> paramTypes) {
		// We have no access to method parameter information, so we scan by name alone and treat any match as authoritative
		// We do not scan the superclass, as the caller is expected to know we'll only scan the current class
		for (MethodMetadata method : governorTypeDetails.getDeclaredMethods()) {
			if (method.getMethodName().equals(methodName) && method.getParameterTypes().equals(paramTypes)) {
				// Found a method of the expected name; we won't check method parameters though
				return method;
			}
		}
		return null;
	}
	
	// Typically, no changes are required beyond this point
	public String toString() {
		ToStringCreator tsc = new ToStringCreator(this);
		tsc.append("identifier", getId());
		tsc.append("valid", valid);
		tsc.append("aspectName", aspectName);
		tsc.append("destinationType", destination);
		tsc.append("governor", governorPhysicalTypeMetadata.getId());
		tsc.append("itdTypeDetails", itdTypeDetails);
		return tsc.toString();
	}

	public static final String getMetadataIdentiferType() {
		return PROVIDES_TYPE;
	}
	
	public static final String createIdentifier(JavaType javaType, Path path) {
		return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
	}

	public static final JavaType getJavaType(String metadataIdentificationString) {
		return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}

	public static final Path getPath(String metadataIdentificationString) {
		return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}

	public static boolean isValid(String metadataIdentificationString) {
		return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}
}
