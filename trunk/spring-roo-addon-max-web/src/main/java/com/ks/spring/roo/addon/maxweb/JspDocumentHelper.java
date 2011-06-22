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
import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.PhysicalTypeDetails;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Helper class which generates the contents of the various jsp documents
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class JspDocumentHelper {
	
//	private List<FieldMetadata> fields; 
	private MetadataService metadataService;
	private SimpleDateFormat dateFormatLocalized;
//	, List<FieldMetadata> fields
	private List<String> fields;
	
	private JavaType entityBean;
//	private EntityMetadata entityMetadata;
	private String entityString;
	 
	public JspDocumentHelper(MetadataService metadataService, String entityString, EntityMetadata entityMetadata, List<String> fields) {
//		Assert.notNull(fields, "List of fields required");
		Assert.notNull(metadataService, "Metadata service required");
//		this.fields = fields;
		this.metadataService = metadataService;
		dateFormatLocalized = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());

		
		
		
		this.entityBean = new JavaType(entityString);
//		this.entityBean = new AnnotatedJavaType(new JavaType(Integer.class.getName()), null);
//		this.entityMetadata = entityMetadata;
		this.fields = fields;
		
		this.entityString = entityString;
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
//		
//	document.appendChild(document.createAttribute("div"));
//		document.appendChild(document.createElement("div"));		
//		document = addHeaders(document);		
//		document = getFinderContent(document, finderName);		
//		document = addFooter(document);
//		
//		return document;
//	}
	/**
	 * *********************************************************************************
	 * 목록
	 * *********************************************************************************
	 */
	private Document getListContent(Document document) {
		Assert.notNull(document, "Document required");
		
		String entityName = entityBean.getSimpleTypeName().toLowerCase();
		Element bodyElement = document.createElement("body");
		//header
		bodyElement = getDivHeader(document, bodyElement);
		
		Element ifElement = document.createElement("c:if");
		ifElement.setAttribute("test", "${not empty result}");
		
		Element tableElement = document.createElement("table");
		tableElement.setAttribute("summary", "This is Entity List");
		tableElement.setAttribute("id", "tbl_list");
		tableElement.setAttribute("class", "tbl_list");
		ifElement.appendChild(tableElement);
		// caption
		Element caption = document.createElement("caption");
		caption.setTextContent("Entity목록");
		tableElement.appendChild(caption);
		//col
		int colCount=0;
		for (String field : fields) {
			Element col = document.createElement("col");
			col.setAttribute("width", "10%");
			if(++colCount < 7) {
				tableElement.appendChild(col);
			}
		}
		
		Element theadElement = document.createElement("thead");
		tableElement.appendChild(theadElement);
		Element trElement = document.createElement("tr");
		theadElement.appendChild(trElement);

		Element idThElement = document.createElement("th");
		idThElement.setAttribute("class", "first");
		idThElement.setTextContent("No");
		theadElement.appendChild(idThElement);
		
		int fieldCounter = 0;
//		for (FieldMetadata field : fields) {
		for (String field : fields) {
			Element thElement = document.createElement("th");
			thElement.setTextContent(field);//.getFieldName().getReadableSymbolName()
			thElement.setTextContent(field);
			if(++fieldCounter < 7) {
				theadElement.appendChild(thElement);
			}
		}
		theadElement.appendChild(document.createElement("th")); // 보기
		theadElement.appendChild(document.createElement("th")); // 수정
		theadElement.appendChild(document.createElement("th")); // 삭제
		
		Element tbodyElement = document.createElement("tbody");
		tableElement.appendChild(tbodyElement);
		
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

		Element idTdElement = document.createElement("td");
		idTdElement.setTextContent("${" + entityName + "." + "id" + "}"); //entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		trElement2.appendChild(idTdElement);
		
		fieldCounter = 0;
//		for (FieldMetadata field : fields) {
		for (String field : fields) {
			Element tdElement = document.createElement("td");
//			if (field.getFieldType().isCommonCollectionType()) {
//				tdElement.setTextContent("${fn:length(" + entityName + "." + field.getFieldName().getSymbolName() + ")}");
//			} else if (field.getFieldType().equals(new JavaType(Date.class.getName()))) {
//				Element fmt = document.createElement("fmt:formatDate");
//				fmt.setAttribute("value", "${" + entityName + "." + field.getFieldName().getSymbolName() + "}");
//				fmt.setAttribute("type", "DATE");
//				fmt.setAttribute("pattern", dateFormatLocalized.toPattern());
//				tdElement.appendChild(fmt);
//			} else {
//				tdElement.setTextContent("${fn:substring(" + entityName + "." + field.getFieldName().getSymbolName() + ", 0, 10)}");
//			}
//			if(++fieldCounter < 7) {
//				trElement2.appendChild(tdElement);
//			}
			if (field == null) {
				tdElement.setTextContent("${fn:length(" + entityName + "." + field + ")}");
			} else if (field.equals(new JavaType(Date.class.getName()))) {
				Element fmt = document.createElement("fmt:formatDate");
				fmt.setAttribute("value", "${" + entityName + "." + field + "}");
				fmt.setAttribute("type", "DATE");
				fmt.setAttribute("pattern", dateFormatLocalized.toPattern());
				tdElement.appendChild(fmt);
			} else {
				tdElement.setTextContent("${fn:substring(" + entityName + "." + field + ", 0, 10)}");
			}
			if(++fieldCounter < 7) {
				trElement2.appendChild(tdElement);
			}
		}
		//show
		Element showElement = document.createElement("td");
		Element showFormElement = document.createElement("form:form");
		Element showUrl = document.createElement("c:url");
		showUrl.setAttribute("var", "show_form_url");
		showUrl.setAttribute("value", "/" + entityName + "/${" + entityName + "." + "id" + "}"); //entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		showElement.appendChild(showUrl);
		showFormElement.setAttribute("action", "${show_form_url}");
		showFormElement.setAttribute("method", "GET");
		Element showImageUrl = document.createElement("c:url");
		showImageUrl.setAttribute("var", "show_image_url");
		showImageUrl.setAttribute("value", "${img}/images/blue/backoffice/btn_view.gif");
		showElement.appendChild(showImageUrl);
		Element showSubmitElement = document.createElement("input");
		showSubmitElement.setAttribute("type", "image");
		showSubmitElement.setAttribute("title", "Show " + entityName);
		showSubmitElement.setAttribute("src", "${show_image_url}");
		showSubmitElement.setAttribute("value", "Show " + entityName);
		showSubmitElement.setAttribute("alt", "Show " + entityName);
		showFormElement.appendChild(showSubmitElement);
		showElement.appendChild(showFormElement);
		trElement2.appendChild(showElement);
		//update
//		if(webScaffoldAnnotationValues.isUpdate()) {
			Element updateElement = document.createElement("td");		
			Element updateFormElement = document.createElement("form:form");
			Element updateUrl = document.createElement("c:url");
			updateUrl.setAttribute("var", "update_form_url");
			updateUrl.setAttribute("value", "/" + entityName + "/${" + entityName + "." + "id" + "}/form"); //entityMetadata.getIdentifierField().getFieldName().getSymbolName()
			updateElement.appendChild(updateUrl);
			updateFormElement.setAttribute("action", "${update_form_url}");
			updateFormElement.setAttribute("method", "GET");
			Element updateImageUrl = document.createElement("c:url");
			updateImageUrl.setAttribute("var", "update_image_url");
			updateImageUrl.setAttribute("value", "${img}/images/blue/backoffice/btn_mod.gif");
			updateElement.appendChild(updateImageUrl);
			Element updateSubmitElement = document.createElement("input");
			updateSubmitElement.setAttribute("type", "image");
			updateSubmitElement.setAttribute("title", "Update " + entityName);
			updateSubmitElement.setAttribute("src", "${update_image_url}");
			updateSubmitElement.setAttribute("value", "Update " + entityName);
			updateSubmitElement.setAttribute("alt", "Update " + entityName);
			updateFormElement.appendChild(updateSubmitElement);
			updateElement.appendChild(updateFormElement);
			trElement2.appendChild(updateElement);
//		}
		//delete
//		if(webScaffoldAnnotationValues.isDelete()) {
			Element deleteElement = document.createElement("td");
			Element deleteFormElement = document.createElement("form:form");
			Element deleteUrl = document.createElement("c:url");
			deleteUrl.setAttribute("var", "delete_form_url");
			deleteUrl.setAttribute("value", "/" + entityName + "/${" + entityName + "." + "id" + "}"); //entityMetadata.getIdentifierField().getFieldName().getSymbolName()
			deleteElement.appendChild(deleteUrl);
			deleteFormElement.setAttribute("action", "${delete_form_url}");
			deleteFormElement.setAttribute("method", "DELETE");
			Element deleteImageUrl = document.createElement("c:url");
			deleteImageUrl.setAttribute("var", "delete_image_url");
			deleteImageUrl.setAttribute("value", "${img}/images/blue/backoffice/btn_del.gif");
			deleteElement.appendChild(deleteImageUrl);
			Element deleteSubmitElement = document.createElement("input");
			deleteSubmitElement.setAttribute("type", "image");
			deleteSubmitElement.setAttribute("title", "Delete " + entityName);
			deleteSubmitElement.setAttribute("src", "${delete_image_url}");
			deleteSubmitElement.setAttribute("value", "Delete " + entityName);
			deleteSubmitElement.setAttribute("alt", "Delete " + entityName);
			deleteFormElement.appendChild(deleteSubmitElement);
			deleteElement.appendChild(deleteFormElement);
			trElement2.appendChild(deleteElement);
//		}
		
		Element elseElement = document.createElement("c:if");
		elseElement.setAttribute("test", "${empty " + entityName + "}");//entityMetadata.getPlural().toLowerCase()
		elseElement.setTextContent("No " + entityName + " found.");//entityMetadata.getPlural()	

		bodyElement.appendChild(ifElement);
		bodyElement.appendChild(elseElement);
		
		document.getDocumentElement().appendChild(bodyElement);
		//footer
		Element div = getDivFooter(document);
		document.getDocumentElement().appendChild(div);
		return document;
	}

private Element getDivHeader(Document document, Element bodyElement) {
	//1) header
	//	<div class="h2box">
	//		<h2>Entity 관리</h2>
	//	</div>
	//
	Element div1 = document.createElement("div");
	div1.setAttribute("class", "h2box");
	Element h2 = document.createElement("h2");
	h2.setTextContent("Entity Management");
	div1.appendChild(h2);
	//	<h3>Entity목록</h3>
	Element h3 = document.createElement("h3");
	h3.setTextContent("Entity List");
	//	<div class="btnarea_top">
	//		<div class="btn_rgt">
	//			<span class="button whitebtn">
	//				<a href="${excel_url}">엑셀다운받기</a>
	//			</span>
	//			<span class="button bluebtn">
	//				<a href="${create_url}">Entity등록</a>
	//			</span>
	//		</div>
	//	</div>
	Element div2 = document.createElement("div");
	div2.setAttribute("class", "btnarea_top");
	Element div2_1 = document.createElement("div");
	div2_1.setAttribute("class", "btn_rgt");
	
	Element span1 = document.createElement("span");
	span1.setAttribute("class", "button whitebtn");
	Element a1 = document.createElement("a");
	a1.setAttribute("href", "${excel_url}");
	a1.setTextContent("Excel Download");
	span1.appendChild(a1);
	
	Element span2 = document.createElement("span");
	span1.setAttribute("class", "button bluebtn");
	Element a2 = document.createElement("a");
	a2.setAttribute("href", "${create_url}");
	a2.setTextContent("Entity 등록");
	span2.appendChild(a2);

	div2_1.appendChild(span1);
	div2_1.appendChild(span2);
	div2.appendChild(div2_1);
	
	bodyElement.appendChild(div1);
	bodyElement.appendChild(h3);
	bodyElement.appendChild(div2);
	return bodyElement;
}

private Element getDivFooter(Document document) {
	//	<div class="paging">
	//		<c:if test="${not empty result.pageView}">
	//	    	${result.pageView}
	//	    </c:if>  
	//		<div class="btn_rgt">
	//			<span class="button whitebtn">
	//				<a href="${excel_url}">엑셀다운받기</a>
	//			</span>
	//			<span class="button bluebtn">
	//				<a href="${create_url}">Entity등록</a>
	//			</span>
	//		</div>
	//	</div>

	Element div = document.createElement("div");
	div.setAttribute("class", "paging");
	Element div_1 = document.createElement("c:if");
	div_1.setAttribute("test", "${not empty result.pageView}");
	div_1.setTextContent("${result.pageView}");
	
	Element div_2 = document.createElement("div");
	div_2.setAttribute("class", "btn_rgt");
	
	Element span1 = document.createElement("span");
	span1.setAttribute("class", "button whitebtn");
	Element a1 = document.createElement("a");
	a1.setAttribute("href", "${excel_url}");
	a1.setTextContent("Excel Download");
	span1.appendChild(a1);
	
	Element span2 = document.createElement("span");
	span1.setAttribute("class", "button bluebtn");
	Element a2 = document.createElement("a");
	a2.setAttribute("href", "${create_url}");
	a2.setTextContent("Entity Create");
	span2.appendChild(a2);

	div_2.appendChild(span1);
	div_2.appendChild(span2);
	div.appendChild(div_1);
	div.appendChild(div_2);
	return div;
}	

	private Document getShowContent(Document document) {
		Assert.notNull(document, "Document required");
		
		String entityName = entityBean.getSimpleTypeName().toLowerCase();
		
		Element divElement = document.createElement("body");//DojoUtils.getTitlePaneDojo(document, "Show " + entityBean.getSimpleTypeName());
			
		Element ifElement = document.createElement("c:if");
		ifElement.setAttribute("test", "${not empty " + entityName + "}");
		divElement.appendChild(ifElement);		

//		for (FieldMetadata field : fields) {
		for (String field : fields) {
			Element divSubmitElement = document.createElement("div");
			divSubmitElement.setAttribute("id", "roo_" + entityName + "_" + field);
				
			Element label = document.createElement("label");
			label.setAttribute("for", "_" + field);
			label.setTextContent(field + ":");
			divSubmitElement.appendChild(label);
			
			Element divContent = document.createElement("div");
			divContent.setAttribute("id", "_" + field);
			divContent.setAttribute("class", "box");
			
			if (field.equals(new JavaType(Date.class.getName()))) {
				Element fmt = document.createElement("fmt:formatDate");
				fmt.setAttribute("value", "${" + entityName + "." + field + "}");
				fmt.setAttribute("type", "DATE");
				fmt.setAttribute("pattern", dateFormatLocalized.toPattern());
				divContent.appendChild(fmt);
			} else {
				divContent.setTextContent("${" + entityName + "." + field + "}");
			}
			divSubmitElement.appendChild(divContent);
			ifElement.appendChild(divSubmitElement);
			ifElement.appendChild(document.createElement("br"));
		}

		Element elseElement = document.createElement("c:if");
		elseElement.setAttribute("test", "${empty " + entityName + "}");
		elseElement.setTextContent("No " + entityBean.getSimpleTypeName() + " found with this id.");
		divElement.appendChild(elseElement);
		
		document.getDocumentElement().appendChild(divElement);
		
		return document;
	}
	/**
	 * *********************************************************************************
	 * 생성
	 * *********************************************************************************
	 */	
	private Document getCreateContent(Document document) {
		Assert.notNull(document, "Document required");
		
		String entityName = entityBean.getSimpleTypeName().toLowerCase();
		
		Element divElement = document.createElement("body");//DojoUtils.getTitlePaneDojo(document, "Create New " + entityBean.getSimpleTypeName());
		
		Element url = document.createElement("c:url");
		url.setAttribute("var", "form_url");
		url.setAttribute("value", "/" + entityName);
		divElement.appendChild(url);
		
		Element formElement = document.createElement("form:form");
		formElement.setAttribute("modelAttribute", entityName);
		formElement.setAttribute("action", "${form_url}");
		formElement.setAttribute("method", "POST");

		createFieldsForCreateAndUpdate(document, formElement);

		Element divSubmitElement = document.createElement("div");
		divSubmitElement.setAttribute("id", "roo_" + entityName + "_submit");
		divSubmitElement.setAttribute("class", "submit");
		
		Element inputElement = document.createElement("input");
		inputElement.setAttribute("type", "submit");
		inputElement.setAttribute("value", "Save");
		inputElement.setAttribute("id", "proceed");
//		divSubmitElement.appendChild(DojoUtils.getSubmitButtonDojo(document, "proceed"));
		divSubmitElement.appendChild(inputElement);
		formElement.appendChild(divSubmitElement);

		divElement.appendChild(formElement);
		document.getDocumentElement().appendChild(divElement);	
		return document;
	}
	/**
	 * *********************************************************************************
	 * 수정
	 * *********************************************************************************
	 */	
	private Document getUpdateContent(Document document) {
		Assert.notNull(document, "Document required");
		
		String entityName = entityBean.getSimpleTypeName().toLowerCase();
		
		Element divElement = document.createElement("body");//DojoUtils.getTitlePaneDojo(document, "Update " + entityBean.getSimpleTypeName());

		Element url = document.createElement("c:url");
		url.setAttribute("var", "form_url");
		url.setAttribute("value", "/" + entityName + "/${" + entityName	+ "." + "id" + "}");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		divElement.appendChild(url);
		
		Element formElement = document.createElement("form:form");
		formElement.setAttribute("modelAttribute", entityName);
		formElement.setAttribute("action", "${form_url}");
		formElement.setAttribute("method", "PUT");		

		createFieldsForCreateAndUpdate(document, formElement);
		
		Element divSubmitElement = document.createElement("div");
		divSubmitElement.setAttribute("id", "roo_" + entityName + "_submit");
		divSubmitElement.setAttribute("class", "submit");
		
		Element inputElement = document.createElement("input");
		inputElement.setAttribute("type", "submit");
		inputElement.setAttribute("value", "Update");
		inputElement.setAttribute("id", "proceed");
//		divSubmitElement.appendChild(DojoUtils.getSubmitButtonDojo(document, "proceed"));
		divSubmitElement.appendChild(inputElement);
		formElement.appendChild(divSubmitElement);
		
		Element formHiddenId = document.createElement("form:hidden");
		formHiddenId.setAttribute("path", "path");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		formHiddenId.setAttribute("id", "_" + "id");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		formElement.appendChild(formHiddenId);
		Element formHiddenVersion = document.createElement("form:hidden");
		formHiddenVersion.setAttribute("path", "path");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		formHiddenVersion.setAttribute("id", "_" + "id");//entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		formElement.appendChild(formHiddenVersion);

		divElement.appendChild(formElement);
		document.getDocumentElement().appendChild(divElement);
	
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
		
		String entityName = entityBean.getSimpleTypeName().toLowerCase();
		
//		for (FieldMetadata field : fields) {
		for (String field : fields) {
			
			JavaType fieldType = new JavaType("java.lang.String");//field.getFieldType();
//			if(fieldType.isCommonCollectionType() && fieldType.equals(new JavaType(Set.class.getName()))) {
//				if (fieldType.getParameters().size() != 1) {
//					throw new IllegalArgumentException();
//				}
//				fieldType = fieldType.getParameters().get(0);
//			}
			
			Element divElement = document.createElement("div");
			divElement.setAttribute("id", "roo_" + entityName + "_" + field);
						
			Element labelElement = document.createElement("label");
			labelElement.setAttribute("for", "_" + field);
			labelElement.setTextContent(field + ":");
			divElement.appendChild(labelElement);
			
			divElement.appendChild(JspUtils.getInputBox(document, new JavaSymbolName(field), 30));
			divElement.appendChild(document.createElement("br"));
			divElement.appendChild(JspUtils.getErrorsElement(document, new JavaSymbolName(field)));
//			divElement.appendChild(DojoUtils.getValidationDojo(document, field));
			
			if (fieldType.getFullyQualifiedTypeName().equals(Date.class.getName()) ||
					// should be tested with instanceof
					fieldType.getFullyQualifiedTypeName().equals(Calendar.class.getName())) {
//				divElement.appendChild(DojoUtils.getDateDojo(document, field));
			}
			
			formElement.appendChild(divElement);
			formElement.appendChild(document.createElement("br"));				
		}
	}
	
	private Document addHeaders(Document document) {		
		Element head = document.createElement("head");

		//<jsp:directive.page contentType="text/html;charset=UTF-8" />
		Element page = document.createElement("jsp:directive.page");
		page.setAttribute("contentType", "text/html;charset=UTF-8");
		head.appendChild(page);

		Element taglibsInclude = document.createElement("jsp:directive.include");
		taglibsInclude.setAttribute("file", "/WEB-INF/views/__system/taglibs.jsp");
		head.appendChild(taglibsInclude);
		
		// this node is just for temporary purpose - it will not be in the final result
		Element headerInclude = document.createElement("jsp:directive.include");
		headerInclude.setAttribute("file", "/WEB-INF/views/backoffice/__header.jsp");
		head.appendChild(headerInclude);
		
		
		document.getDocumentElement().appendChild(head);
		document.getDocumentElement().appendChild(document.createComment("WARNING: This file is Not maintained by ROO! IT WON'T BE OVERWRITTEN unless you specify :)"));
		return document;
	}	

	private Document addFooter(Document document) {
//		Element includeFooter = document.createElement("jsp:directive.include");
//		includeFooter.setAttribute("file", "/WEB-INF/views/backoffice/__footer.jsp");
//		document.getDocumentElement().appendChild(includeFooter);
		return document;
	}
	
	private boolean isEnumType(JavaType type) {
		PhysicalTypeMetadata physicalTypeMetadata  = (PhysicalTypeMetadata) metadataService.get(PhysicalTypeIdentifierNamingUtils.createIdentifier(PhysicalTypeIdentifier.class.getName(), type, Path.SRC_MAIN_JAVA));
		if (physicalTypeMetadata != null) {
			PhysicalTypeDetails details = physicalTypeMetadata.getMemberHoldingTypeDetails();//getPhysicalTypeDetails();
			if (details != null) {
				if (details.getPhysicalTypeCategory().equals(PhysicalTypeCategory.ENUMERATION)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isSpecialType(JavaType javaType) {
		String physicalTypeIdentifier = PhysicalTypeIdentifier.createIdentifier(javaType, Path.SRC_MAIN_JAVA);
		//we are only interested if the type is part of our application and if no editor exists for it already
		if (metadataService.get(physicalTypeIdentifier) != null) {
		  return true;
		}		
		return false;
	}
}