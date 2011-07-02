package com.ks.spring.roo.addon.maxweb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;

import org.springframework.roo.addon.entity.EntityMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.annotations.AnnotationAttributeValue;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadata;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper class which generates the contents of the various jsp documents
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class JspDocumentHelper extends JspDocumentCommonHelper {
	
//	private List<FieldMetadata> fields; 
//	private MetadataService metadataService; // super
	private SimpleDateFormat dateFormatLocalized;
	private List<FieldMetadata> fields;
	private JavaType entity;
//	private EntityMetadata entityMetadata;
//	private String entityString;

	
	
	
	public JspDocumentHelper(MetadataService metadataService, JavaType entity, EntityMetadata entityMetadata, List<FieldMetadata> fields) {
		Assert.notNull(fields, "List of fields required");
		Assert.notNull(metadataService, "Metadata service required");
		this.fields = fields;
		this.metadataService = metadataService;
		this.dateFormatLocalized = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
		this.entity = entity;
	}
	
	public Document getListDocument() {
		DocumentBuilder builder = XmlUtils.getDocumentBuilder();
		Document document = builder.newDocument();	
		document.appendChild(document.createElement("html"));
		document = addHeaders(document);		
		document = getListContent(document);		
		document = addFooter(document);
		
		return document;
	}
	
	public Document getShowDocument() {
		DocumentBuilder builder = XmlUtils.getDocumentBuilder();
		Document document = builder.newDocument();		
		document.appendChild(document.createElement("html"));
		document = addHeaders(document);		
		document = getShowContent(document);		
		document = addFooter(document);
		
		return document;
	}
	
	public Document getCreateDocument() {
		DocumentBuilder builder = XmlUtils.getDocumentBuilder();
		Document document = builder.newDocument();		
		document.appendChild(document.createElement("html"));		
		document = addHeaders(document);		
		document = getCreateContent(document);		
		document = addFooter(document);
		
		return document;
	}
	
	public Document getUpdateDocument() {
		DocumentBuilder builder = XmlUtils.getDocumentBuilder();
		Document document = builder.newDocument();		
		document.appendChild(document.createElement("html"));		
		document = addHeaders(document);		
		document = getUpdateContent(document);		
		document = addFooter(document);
		
		return document;
	}
	
//	public Document getFinderDocument(String finderName) {
//		DocumentBuilder builder = XmlUtils.getDocumentBuilder();
//		Document document = builder.newDocument();		
//		document.appendChild(document.createAttribute("div"));
//		document.appendChild(document.createElement("div"));		
//		document = addHeaders(document);		
//		document = getFinderContent(document, finderName);		
//		document = addFooter(document);
//		return document;
//	}
	/**
	 * *********************************************************************************
	 * 목록
	 * *********************************************************************************
	 */
	private Document getListContent(Document document) {
		Assert.notNull(document, "Document required");
		String entityName = entity.getSimpleTypeName().toLowerCase();
		Element bodyElement = document.createElement("body");
		
		//header -------------------------------------
		bodyElement = getDivHeader(document, bodyElement, entity.getSimpleTypeName());
		document = printCommentMessage(document,"List Start...");
		
		Element ifElement = document.createElement("c:if");
		ifElement.setAttribute("test", "${not empty result}");
		Element tableElement = document.createElement("table");
		tableElement.setAttribute("summary", "This is Entity List");
		tableElement.setAttribute("id", "tbl_list");
		tableElement.setAttribute("class", "tbl_list");
		ifElement.appendChild(tableElement);
		// caption
		Element caption = document.createElement("caption");
		caption.setTextContent("Entity List");
		tableElement.appendChild(caption);
		//col
		int colCount=0;
		Element col = document.createElement("col"); //no
		col.setAttribute("width", "10%");
		tableElement.appendChild(col);
		for (FieldMetadata field : fields) {	
			col = document.createElement("col");
			col.setAttribute("width", "10%");
			if(++colCount < 7) {
				if(!field.getFieldName().getSymbolName().toLowerCase().equals("version")){
					tableElement.appendChild(col);
				}
			}
		}
		col = document.createElement("col"); // manage
		col.setAttribute("width", (10-(colCount+1))+"0%");
		tableElement.appendChild(col);
		
		Element theadElement = document.createElement("thead");
		tableElement.appendChild(theadElement);
		Element trElement = document.createElement("tr");
		theadElement.appendChild(trElement);

		int fieldCounter = 0;
		for (FieldMetadata field : fields) {
			Element thElement = document.createElement("th");
			if(fieldCounter == 0) thElement.setAttribute("class", "first");
			thElement.setTextContent(field.getFieldName().getReadableSymbolName());//
			if(++fieldCounter < 7) {
				if(!field.getFieldName().getSymbolName().toLowerCase().equals("version")){
					trElement.appendChild(thElement);
					trElement.appendChild(document.createTextNode("\n\n"));
				}
			}
		}
	
		Element manageElement = document.createElement("th"); // 보기,수정,삭제 버튼
		manageElement.setTextContent("Management");
		trElement.appendChild(manageElement);
		
		Element tbodyElement = document.createElement("tbody");
		tableElement.appendChild(tbodyElement);
		
		// show,update,delete url ================
		Element showUrl = document.createElement("spring:url");
		showUrl.setAttribute("var", "show_url");
		showUrl.setAttribute("value", "${BACK_CTX}/"+entityName+"s/show"); //entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		tbodyElement.appendChild(showUrl);
		Element updateUrl = document.createElement("spring:url");
		updateUrl.setAttribute("var", "update_url");
		updateUrl.setAttribute("value", "${BACK_CTX}/"+entityName+"s/update"); //entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		tbodyElement.appendChild(updateUrl);
		Element deleteUrl = document.createElement("spring:url");
		deleteUrl.setAttribute("var", "delete_url");
		deleteUrl.setAttribute("value", "${BACK_CTX}/"+entityName+"s/delete/${"+entityName+".id}"); //entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		tbodyElement.appendChild(deleteUrl);
		
		//<spring:url var="img" value="/resources/"/>
		Element imgTag = document.createElement("spring:url");
		imgTag.setAttribute("var", "img");
		imgTag.setAttribute("value", "/resources");
		tbodyElement.appendChild(imgTag);
		
		Element forEachElement = document.createElement("c:forEach");
		forEachElement.setAttribute("var", entityName);
		forEachElement.setAttribute("items", "${result." + entityName + "List}");// entityMetadata.getPlural().toLowerCase()
		tbodyElement.appendChild(forEachElement);
		Element trElement2 = document.createElement("tr");
		forEachElement.appendChild(trElement2);

		fieldCounter = 0;
		for (FieldMetadata field : fields) {
			Element tdElement = document.createElement("td");
			if (field.getFieldType().isCommonCollectionType()) {
				tdElement.setTextContent("${fn:length(" + entityName + "." + field.getFieldName().getSymbolName() + ")}");
			} else if (field.getFieldType().equals(new JavaType(Date.class.getName()))) {
				Element fmt = document.createElement("fmt:formatDate");
				fmt.setAttribute("value", "${" + entityName + "." + field.getFieldName().getSymbolName() + "}");
				fmt.setAttribute("type", "DATE");
				fmt.setAttribute("pattern", dateFormatLocalized.toPattern());
				tdElement.appendChild(fmt);
			} else {
				tdElement.setTextContent("${fn:substring(" + entityName + "." + field.getFieldName().getSymbolName() + ", 0, 20)}");
			}
			if(++fieldCounter < 7) {
				if(!field.getFieldName().getSymbolName().toLowerCase().equals("version")){
					trElement2.appendChild(tdElement);
				}
			}
		}
		//show
		Element rowManage = document.createElement("td");
		Element showLink = document.createElement("a");
		showLink.setAttribute("href", "${show_url}/${"+entityName+".id}");
		showLink.setTextContent("[View]");
		rowManage.appendChild(showLink);
		//update
		Element updateLink = document.createElement("a");
		updateLink.setAttribute("href", "${update_url}/${"+entityName+".id}");
		updateLink.setTextContent("[Modify]");
		rowManage.appendChild(updateLink);
		//delete
		Element deleteLink = document.createElement("a");
		deleteLink.setAttribute("href", "${delete_url}/${"+entityName+".id}");
		deleteLink.setTextContent("[Delete]");
		rowManage.appendChild(deleteLink);
		trElement2.appendChild(rowManage);
		
		Element elseElement = document.createElement("c:if");
		elseElement.setAttribute("test", "${empty result}");//entityMetadata.getPlural().toLowerCase()
		Element colspanTr = document.createElement("tr");
		Element colspanTd = document.createElement("td");
		colspanTd.setAttribute("colspan", ""+(fieldCounter+1));
		colspanTd.setTextContent("Not Found for "+entityName+" Data");
		colspanTr.appendChild(colspanTd);
		elseElement.appendChild(colspanTr);
		tbodyElement.appendChild(elseElement);
		
		bodyElement.appendChild(ifElement);
		document.getDocumentElement().appendChild(bodyElement);
		
		//footer -------------------------------------
		document = printCommentMessage(document,"List End...");
		bodyElement = getDivFooter(document, bodyElement);
		return document;
	}
	/**
	 * *********************************************************************************
	 * 상세보기
	 * *********************************************************************************
	 */
	private Document getShowContent(Document document) {
		Assert.notNull(document, "Document required");
		String entityName = entity.getSimpleTypeName().toLowerCase();
		Element bodyElement = document.createElement("body");
		
		//header -------------------------------------
		bodyElement = getDivHeaderView(document, bodyElement, entity.getSimpleTypeName());
		document = printCommentMessage(document,"Show Start...");
		
		Element ifElement = document.createElement("c:if");
		ifElement.setAttribute("test", "${not empty result}");
		bodyElement.appendChild(ifElement);		

		// table header start---------
		Element tableElement = document.createElement("table");
		tableElement.setAttribute("summary", "This is Entity Show");
		tableElement.setAttribute("class", "tbl_view");
		tableElement.setAttribute("style", "margin-bottom:30px;");
		ifElement.appendChild(tableElement);
		// caption
		Element caption = document.createElement("caption");
		caption.setTextContent("Entity Show");
		tableElement.appendChild(caption);
		//col
		Element col_1 = document.createElement("col"); //no1
		col_1.setAttribute("width", "10%");
		tableElement.appendChild(col_1);
		Element col_2 = document.createElement("col"); //no2
		col_2.setAttribute("width", "10%");
		tableElement.appendChild(col_2);
		Element col_3 = document.createElement("col"); //no3
		col_3.setAttribute("width", "80%");
		tableElement.appendChild(col_3);

		Element tbodyElement = document.createElement("tbody");
		tableElement.appendChild(tbodyElement);
		
		for (FieldMetadata field : fields) {
			if(!field.getFieldName().getSymbolName().toLowerCase().equals("version") ||
					!field.getFieldName().getSymbolName().toLowerCase().equals("id")){
				Element trElement = document.createElement("tr");
				trElement.setAttribute("id", "roo_" + entityName + "_" + field.getFieldName().getSymbolName());
					
				Element th = document.createElement("th");
				th.setAttribute("colspan", "2");
				th.setTextContent(field.getFieldName().getSymbolName() + ":");
				trElement.appendChild(th);
				
				Element td = document.createElement("td");
				if (field.equals(new JavaType(Date.class.getName()))) {
					Element fmt = document.createElement("fmt:formatDate");
					fmt.setAttribute("value", "${result." + entityName + "." + field.getFieldName().getSymbolName() + "}");
					fmt.setAttribute("type", "DATE");
					fmt.setAttribute("pattern", dateFormatLocalized.toPattern());
					td.appendChild(fmt);
				} else {
					td.setTextContent("${result." + entityName + "." + field.getFieldName().getSymbolName() + "}");
				}
				trElement.appendChild(td);
				tbodyElement.appendChild(trElement);
				tbodyElement.appendChild(document.createTextNode("\n\n"));
			}
		}

		Element elseElement = document.createElement("c:if");
		elseElement.setAttribute("test", "${empty result}");
		elseElement.setTextContent("No " + entity.getSimpleTypeName() + " found with this id.");
		
		bodyElement.appendChild(elseElement);
		document.getDocumentElement().appendChild(bodyElement);
		
		//footer -------------------------------------
		document = printCommentMessage(document,"Show End...");
		bodyElement = getDivFooterView(document, bodyElement, entityName);
		return document;
	}
	/**
	 * *********************************************************************************
	 * 생성
	 * *********************************************************************************
	 */	
	private Document getCreateContent(Document document) {
		Assert.notNull(document, "Document required");
		String entityName = entity.getSimpleTypeName().toLowerCase();
		Element bodyElement = document.createElement("body");
		
		//header -------------------------------------
		bodyElement = getDivHeaderView(document, bodyElement, entity.getSimpleTypeName());
		document = printCommentMessage(document,"Create Start...");
		
		Element url = document.createElement("c:url");
		url.setAttribute("var", "form_url");
		url.setAttribute("value", "/backoffice/" + entityName + "s/create");
		bodyElement.appendChild(url);
		
		Element formElement = document.createElement("form:form");
		formElement.setAttribute("modelAttribute", entityName);
		formElement.setAttribute("action", "${form_url}");
		formElement.setAttribute("method", "POST");
		formElement.setAttribute("id", "action_form");
		formElement.setAttribute("onsubmit", "return false;");
		//form
		createFieldsForCreateAndUpdate(document, formElement);

		bodyElement.appendChild(formElement);
		document.getDocumentElement().appendChild(bodyElement);
		
		//footer -------------------------------------
		document = printCommentMessage(document,"Create End...");
		bodyElement = getDivFooterForm(document, bodyElement, entityName);
		return document;
	}
	/**
	 * *********************************************************************************
	 * 수정
	 * *********************************************************************************
	 */	
	private Document getUpdateContent(Document document) {
		Assert.notNull(document, "Document required");
		String entityName = entity.getSimpleTypeName().toLowerCase();
		Element bodyElement = document.createElement("body");
		
		//header -------------------------------------
		bodyElement = getDivHeaderView(document, bodyElement, entity.getSimpleTypeName());
		document = printCommentMessage(document,"Update Start...");
		
		Element url = document.createElement("c:url");
		url.setAttribute("var", "form_url");
		url.setAttribute("value", "/backoffice/" + entityName + "s/update");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		bodyElement.appendChild(url);
		
		Element formElement = document.createElement("form:form");
		formElement.setAttribute("modelAttribute", entityName);
		formElement.setAttribute("action", "${form_url}");
		formElement.setAttribute("method", "POST");		
		formElement.setAttribute("id", "action_form");		
		formElement.setAttribute("onsubmit", "return false;");		
		//form
		createFieldsForCreateAndUpdate(document, formElement);
		
		Element formHiddenId = document.createElement("form:hidden");
		formHiddenId.setAttribute("path", "id");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		formHiddenId.setAttribute("id", "_id");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		formElement.appendChild(formHiddenId);
		Element formHiddenVersion = document.createElement("form:hidden");
		formHiddenVersion.setAttribute("path", "version");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		formHiddenVersion.setAttribute("id", "_version");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		formElement.appendChild(formHiddenVersion);

		bodyElement.appendChild(formElement);
		document.getDocumentElement().appendChild(bodyElement);
		
		//footer -------------------------------------
		document = printCommentMessage(document,"Update End...");
		bodyElement = getDivFooterForm(document, bodyElement, entityName);
		return document;
	}
	
//	private Document getFinderContent(Document document, String finderName) {
//		Assert.notNull(document, "Document required");
//		Assert.hasText(finderName, "finder name required");
//		
//		String entityName = entityBean.getSimpleTypeName().toLowerCase();
//		
//		Element titleDivElement = DojoUtils.getTitlePaneDojo(document, new JavaSymbolName(finderName).getReadableSymbolName());
//		
//		Element url = document.createElement("c:url");
//		url.setAttribute("var", "form_url");
//		url.setAttribute("value", "/" + entityName + "/find/" + finderName.replace("find" + entityMetadata.getPlural(), ""));
//		titleDivElement.appendChild(url);
//		
//		Element formElement = document.createElement("form:form");
//		formElement.setAttribute("action", "${form_url}");
//		formElement.setAttribute("method", "GET");		
//
//		MethodMetadata methodMetadata = finderMetadata.getDynamicFinderMethod(finderName);
//		
//		List<JavaType> types = AnnotatedJavaType.convertFromAnnotatedJavaTypes(methodMetadata.getParameterTypes());
//		List<JavaSymbolName> paramNames = methodMetadata.getParameterNames();
//		
//		for (int i = 0; i < types.size(); i++) {
//			
//			JavaType type = types.get(i);
//			JavaSymbolName paramName = paramNames.get(i);
//			
//			Element divElement = document.createElement("div");
//			divElement.setAttribute("id", "roo_" + entityName + "_" + paramName.getSymbolName().toLowerCase());
//
//			Element labelElement = document.createElement("label");
//			labelElement.setAttribute("for", "_" + paramName.getSymbolName().toLowerCase());
//			labelElement.setTextContent(paramName.getReadableSymbolName()  + ":");
//			
//			if (isSpecialType(type)) {
//				EntityMetadata typeEntityMetadata = (EntityMetadata) metadataService.get(EntityMetadata.createIdentifier(type, Path.SRC_MAIN_JAVA));
//				if (typeEntityMetadata != null) {
//					Element ifElement = document.createElement("c:if");
//					ifElement.setAttribute("test", "${not empty " + typeEntityMetadata.getPlural().toLowerCase() + "}");
//					divElement.appendChild(ifElement);
//					ifElement.appendChild(labelElement);
//
//					Element select = document.createElement("select");
//					select.setAttribute("style", "width:250px");
//					select.setAttribute("name", paramName.getSymbolName().toLowerCase());
//					Element forEach = document.createElement("c:forEach");
//					forEach.setAttribute("items", "${" + typeEntityMetadata.getPlural().toLowerCase() + "}");
//					forEach.setAttribute("var", paramName.getSymbolName().toLowerCase());
//					select.appendChild(forEach);
//					Element option = document.createElement("option");
//					option.setAttribute("value", "${" + paramName.getSymbolName().toLowerCase() + "." + entityMetadata.getIdentifierField().getFieldName() + "}");
//					option.setTextContent("${" + paramName.getSymbolName().toLowerCase() + "}");
//					forEach.appendChild(option);
//					ifElement.appendChild(select);		
//				}
//			} else if (isEnumType(type)) {
//				divElement.appendChild(labelElement);
//				divElement.appendChild(JspUtils.getEnumSelectBox(document, paramName));		
//				divElement.appendChild(DojoUtils.getSelectDojo(document, paramName));
//				divElement.appendChild(document.createElement("br"));
//				formElement.appendChild(divElement);
//				formElement.appendChild(document.createElement("br"));	
//			} else if (type.getFullyQualifiedTypeName().equals(Boolean.class.getName())
//					|| type.getFullyQualifiedTypeName().equals(boolean.class.getName())) {	
//				divElement.appendChild(labelElement);
//				Element formCheckTrue = document.createElement("input");
//				formCheckTrue.setAttribute("type", "radio");
//				formCheckTrue.setAttribute("id", "_" + paramName.getSymbolName());
//				formCheckTrue.setAttribute("name", paramName.getSymbolName().toLowerCase());
//				formCheckTrue.setAttribute("value", "true");
//				formCheckTrue.setAttribute("checked", "checked");
//				formCheckTrue.setTextContent("(true)");
//				divElement.appendChild(formCheckTrue);
//				Element formCheckFalse = (Element)formCheckTrue.cloneNode(false);
//				formCheckFalse.setAttribute("value", "false");
//				formCheckFalse.setTextContent("(false)");
//				formCheckFalse.removeAttribute("checked");
//				divElement.appendChild(formCheckFalse);
//				formElement.appendChild(divElement);
//				formElement.appendChild(document.createElement("br"));
//			} else {	
//				divElement.appendChild(labelElement);
//				Element formInput = document.createElement("input");
//				formInput.setAttribute("name", paramName.getSymbolName().toLowerCase());
//				formInput.setAttribute("id", "_" + paramName.getSymbolName().toLowerCase());
//				formInput.setAttribute("size", "0");
//				formInput.setAttribute("style", "width:250px");
//				divElement.appendChild(formInput);
//				divElement.appendChild(DojoUtils.getSimpleValidationDojo(document, paramName));
//				
//				if (type.getFullyQualifiedTypeName().equals(Date.class.getName()) ||
//						// should be tested with instanceof
//						type.getFullyQualifiedTypeName().equals(Calendar.class.getName())) {
//							divElement.appendChild(DojoUtils.getRequiredDateDojo(document, paramName));
//				}
//			}
//
//			formElement.appendChild(divElement);
//			formElement.appendChild(document.createElement("br"));
//		}		
//		
//		Element divSubmitElement = document.createElement("div");
//		divSubmitElement.setAttribute("id", "roo_" + entityName + "_submit");
//		divSubmitElement.setAttribute("class", "submit");
//		
//		Element inputElement = document.createElement("input");
//		inputElement.setAttribute("type", "submit");
//		inputElement.setAttribute("value", "Find");
//		inputElement.setAttribute("id", "proceed");
//		divSubmitElement.appendChild(DojoUtils.getSubmitButtonDojo(document, "proceed"));
//		divSubmitElement.appendChild(inputElement);
//		formElement.appendChild(divSubmitElement);
//
//		titleDivElement.appendChild(formElement);
//		document.getDocumentElement().appendChild(titleDivElement);
//	
//		return document;
//	}
	
	private void createFieldsForCreateAndUpdate(Document document, Element formElement) {
		String entityName = entity.getSimpleTypeName().toLowerCase();
		
		// table header start---------
		Element tableElement = document.createElement("table");
		tableElement.setAttribute("summary", "This is Entity Show");
		tableElement.setAttribute("class", "tbl_view");
		tableElement.setAttribute("style", "margin-bottom:30px;");
		formElement.appendChild(tableElement);
		// caption
		Element caption = document.createElement("caption");
		caption.setTextContent("Entity Create");
		tableElement.appendChild(caption);
		//col
		Element col_1 = document.createElement("col"); //no1
		col_1.setAttribute("width", "10%");
		tableElement.appendChild(col_1);
		Element col_2 = document.createElement("col"); //no2
		col_2.setAttribute("width", "10%");
		tableElement.appendChild(col_2);
		Element col_3 = document.createElement("col"); //no3
		col_3.setAttribute("width", "80%");
		tableElement.appendChild(col_3);

		Element tbodyElement = document.createElement("tbody");
		tableElement.appendChild(tbodyElement);
		for (FieldMetadata field : fields) {
			tbodyElement.appendChild(document.createComment("fields : " + field.getFieldName().getSymbolName().toLowerCase())); // log
			//version,id는 제외
			if(field.getFieldName().getSymbolName().toLowerCase().equals("version") || field.getFieldName().getSymbolName().toLowerCase().equals("id")){
				tbodyElement.appendChild(document.createComment("skip : " + field.getFieldName().getSymbolName()));
			}else{
				
				JavaType fieldType = field.getFieldType();
				if(fieldType.isCommonCollectionType() && fieldType.equals(new JavaType(Set.class.getName()))) {
					if (fieldType.getParameters().size() != 1) {
						throw new IllegalArgumentException();
					}
					fieldType = fieldType.getParameters().get(0);
				}
				
				Element trElement = document.createElement("tr");
				trElement.setAttribute("id", "roo_" + field.getFieldName().getSymbolName());
				Element th = document.createElement("th");
				th.setAttribute("colspan", "2");
				th.setTextContent(field.getFieldName().getSymbolName() + ":");
				trElement.appendChild(th);
				Element td = document.createElement("td");
				
				if (fieldType.getFullyQualifiedTypeName().equals(Boolean.class.getName())
						|| fieldType.getFullyQualifiedTypeName().equals(boolean.class.getName())) {
					td.appendChild(JspUtils.getCheckBox(document, new JavaSymbolName(field.getFieldName().getSymbolName())));					
				}else{
					boolean specialAnnotation = false;
					for (AnnotationMetadata annotation : field.getAnnotations()) {
						if (annotation.getAnnotationType().getFullyQualifiedTypeName().equals("javax.persistence.ManyToOne")
								|| annotation.getAnnotationType().getFullyQualifiedTypeName().equals("javax.persistence.OneToMany")) {

							EntityMetadata typeEntityMetadata = null;
							
							if (field.getFieldType().isCommonCollectionType()) {
								typeEntityMetadata = (EntityMetadata) metadataService.get(EntityMetadata.createIdentifier(field.getFieldType().getParameters().get(0), Path.SRC_MAIN_JAVA));
							} else {
								typeEntityMetadata = (EntityMetadata) metadataService.get(EntityMetadata.createIdentifier(field.getFieldType(), Path.SRC_MAIN_JAVA));
							}
		
							if(typeEntityMetadata == null) {
								throw new IllegalStateException("Could not determine the plural name for the " + field.getFieldName().getSymbolNameCapitalisedFirstLetter() + " field");
							}
							String plural = typeEntityMetadata.getPlural().toLowerCase();
							
							Element ifElement = document.createElement("c:if");
							ifElement.setAttribute("test", "${not empty " + plural + "}");
							td.appendChild(ifElement);
							
//							td.removeChild(labelElement);
//							ifElement.appendChild(labelElement);
							ifElement.appendChild(JspUtils.getSelectBox(document, field.getFieldName(), plural));		

							specialAnnotation = true;
							
							if(annotation.getAnnotationType().getFullyQualifiedTypeName().equals("javax.persistence.ManyToOne")) {
								ifElement.setTextContent( field.getFieldName() + "is ManyToOne :)");
							} else {
								ifElement.setTextContent( field.getFieldName() + "is Many :)");
							}
						} else if (annotation.getAnnotationType().getFullyQualifiedTypeName().equals("javax.validation.constraints.Size")) {
							AnnotationAttributeValue<?> max = annotation.getAttribute(new JavaSymbolName("max"));
							if(max != null) {
								int maxValue = (Integer)max.getValue();
								if(maxValue > 30) {		
									td.appendChild(JspUtils.getTextArea(document, new JavaSymbolName(field.getFieldName().getSymbolName()), maxValue));
								} else {
									td.appendChild(JspUtils.getInputBox(document, new JavaSymbolName(field.getFieldName().getSymbolName()), maxValue));
								}							
								specialAnnotation = true;
							}
						} else if (isEnumType(field.getFieldType())) {
							td.appendChild(JspUtils.getEnumSelectBox(document, new JavaSymbolName(field.getFieldName().getSymbolName())));		
							specialAnnotation = true;
						}
					}
					if (!specialAnnotation) {
						td.appendChild(JspUtils.getInputBox(document, new JavaSymbolName(field.getFieldName().getSymbolName()), 30));
						
						if (fieldType.getFullyQualifiedTypeName().equals(Date.class.getName()) ||
								// should be tested with instanceof
										fieldType.getFullyQualifiedTypeName().equals(Calendar.class.getName())) {
							
							Element aLink = document.createElement("a");
							aLink.setAttribute("href", "#");
							aLink.setAttribute("onclick", "showCal(this)");
							
							Element img = document.createElement("img");
							img.setAttribute("src", "${BACK_IMAGE_PATH}/icon_calendar.gif");
							img.setAttribute("alt", "Show Calendar");
							
							aLink.appendChild(img);
							td.appendChild(aLink);
						}
					}					
				}
				////////////////////////////////////////////////////////////////////
				td.appendChild(document.createElement("br"));
				Element errorsElement = JspUtils.getErrorsElement(document, new JavaSymbolName(field.getFieldName().getSymbolName()));
				errorsElement.setAttribute("cssClass", "errors");
				td.appendChild(errorsElement);					

				trElement.appendChild(td);
				trElement.appendChild(document.createTextNode("\n\n"));			
				tbodyElement.appendChild(trElement);
			}
		}
		formElement.appendChild(tableElement);
	}

	private Document printCommentMessage(Document document, String message) {
		document.getDocumentElement().appendChild(document.createTextNode("\n\n"));
		document.getDocumentElement().appendChild(document.createComment(" ------------ " + message + " ------------ "));
		document.getDocumentElement().appendChild(document.createTextNode("\n\n"));
		return document;
	}	
}