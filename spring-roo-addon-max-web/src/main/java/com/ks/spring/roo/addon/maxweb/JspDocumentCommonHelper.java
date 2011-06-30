package com.ks.spring.roo.addon.maxweb;

import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.PhysicalTypeDetails;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JspDocumentCommonHelper {

	protected MetadataService metadataService;
	
	/**
	 **********************************************************************
	 * Page DIV Header, Footer  
	 ********************************************************************** 
	 */	
	protected Element getDivHeader(Document document, Element bodyElement, String entity) {
		//		<spring:url var="excel_url" value="${BACK_CTX}/__ENTITYS__/excel"/>
		//		<spring:url var="create_url" value="${BACK_CTX}/__ENTITYS__/create"/>
		Element urlExcel = document.createElement("spring:url");
		urlExcel.setAttribute("var", "excel_url");
		urlExcel.setAttribute("value", "${BACK_CTX}/"+entity.toLowerCase()+"s/excel");
		Element urlCreate = document.createElement("spring:url");
		urlCreate.setAttribute("var", "create_url");
		urlCreate.setAttribute("value", "${BACK_CTX}/"+entity.toLowerCase()+"s/create");
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
		span2.setAttribute("class", "button bluebtn");
		Element a2 = document.createElement("a");
		a2.setAttribute("href", "${create_url}");
		a2.setTextContent("Create");
		span2.appendChild(a2);

		div2_1.appendChild(span1);
		div2_1.appendChild(span2);
		div2.appendChild(div2_1);
		
		bodyElement.appendChild(urlExcel);
		bodyElement.appendChild(urlCreate);
		bodyElement.appendChild(div1);
		bodyElement.appendChild(h3);
		bodyElement.appendChild(div2);
		return bodyElement;
	}
	protected Element getDivHeaderView(Document document, Element bodyElement, String entity) {
		//		<spring:url var="excel_url" value="${BACK_CTX}/__ENTITYS__"/>
		Element urlList = document.createElement("spring:url");
		urlList.setAttribute("var", "list_url");
		urlList.setAttribute("value", "${BACK_CTX}/"+entity.toLowerCase()+"s");
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
		h3.setTextContent("View");
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
		a1.setAttribute("href", "${list_url}");
		a1.setTextContent("List");
		span1.appendChild(a1);
		
		div2_1.appendChild(span1);
		div2.appendChild(div2_1);
		
		bodyElement.appendChild(urlList);
		bodyElement.appendChild(div1);
		bodyElement.appendChild(h3);
		bodyElement.appendChild(div2);
		return bodyElement;
	}
	protected Element getDivFooter(Document document, Element bodyElement) {
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
		span2.setAttribute("class", "button bluebtn");
		Element a2 = document.createElement("a");
		a2.setAttribute("href", "${create_url}");
		a2.setTextContent("Create");
		span2.appendChild(a2);

		div_2.appendChild(span1);
		div_2.appendChild(span2);
		div.appendChild(div_1);
		div.appendChild(div_2);
		bodyElement.appendChild(div);
		return bodyElement;
	}
	protected Element getDivFooterForm(Document document, Element bodyElement, String entity) {
		//	<div class="btnarea">
		//		<div class="btn_cen">
		//			<span class="button bluebtn">
		//				<a href="#" onclick="checkForm()">등록하기</a>
		//			</span>
		//		</div>
		//
		//		<div class="btn_rgt02">
		//			<span class="button whitebtn">
		//				<a href='<spring:url value="/__ENTITYS__/list"/>'>목록으로</a>
		//			</span> 
		//		</div>
		//	</div>		
		
		Element div = document.createElement("div");
		div.setAttribute("class", "btnarea");
		Element div_1 = document.createElement("div");
		div_1.setAttribute("class", "btn_cen");
		
		Element div_2 = document.createElement("div");
		div_2.setAttribute("class", "btn_rgt02");
		
		Element span1 = document.createElement("span");
		span1.setAttribute("class", "button bluebtn");
		Element a1 = document.createElement("a");
		a1.setAttribute("href", "#");
		a1.setAttribute("onclick", "checkForm()");
		a1.setTextContent("Apply");
		span1.appendChild(a1);
		
		div_1.appendChild(span1);

		Element span3 = document.createElement("span");
		span3.setAttribute("class", "button whitebtn");
		Element sp_url = document.createElement("spring:url");
		sp_url.setAttribute("value", "/backoffice/" + entity.toLowerCase() + "s");
		sp_url.setAttribute("var", "list_url");
		span3.appendChild(sp_url);
		
		Element a3 = document.createElement("a");
		a3.setAttribute("href", "${list_url}");
		a3.setTextContent("List");
		span3.appendChild(a3);
		
		div_2.appendChild(span3);
		
		div.appendChild(div_1);
		div.appendChild(div_2);
		bodyElement.appendChild(div);
		return bodyElement;
	}	
	protected Element getDivFooterView(Document document, Element bodyElement, String entity) {
		//	<div class="btnarea"> 
		//      <spring:url var="update_url" value="${BACK_CTX}/accounts/update/${result.account.id}"/>
		//		<div class="btn_rgt">
		//			<span class="button whitebtn">
		//				<a href="${update_url}">수정하기</a>
		//			</span>
		//			<span class="button bluebtn">
		//				<a href="${list_url}">목록보기</a>
		//			</span>
		//		</div>
		//	</div>

		Element div = document.createElement("div");
		div.setAttribute("class", "btnarea");
		
		Element update_url = document.createElement("spring:url");
		update_url.setAttribute("var", "update_url");
		update_url.setAttribute("value", "${BACK_CTX}/"+entity+"s/update/${result."+entity+".id}"); //entityMetadata.getIdentifierField().getFieldName().getSymbolName()
		div.appendChild(update_url);
		Element div_1 = document.createElement("div");
		div_1.setAttribute("class", "btn_rgt");
		
		Element span1 = document.createElement("span");
		span1.setAttribute("class", "button bluebtn");
		Element a1 = document.createElement("a");
		a1.setAttribute("href", "${update_url}");
		a1.setTextContent("Update");
		span1.appendChild(a1);
		
		Element span2 = document.createElement("span");
		span2.setAttribute("class", "button whitebtn");
		Element a2 = document.createElement("a");
		a2.setAttribute("href", "${list_url}");
		a2.setTextContent("List");
		span2.appendChild(a2);

		div_1.appendChild(span1);
		div_1.appendChild(span2);
		div.appendChild(div_1);
		bodyElement.appendChild(div);
		return bodyElement;
	}	
	/**
	 **********************************************************************
	 * Title Header, Footer  
	 ********************************************************************** 
	 */
	protected Document addHeaders(Document document) {		
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

	protected Document addFooter(Document document) {
		Element includeFooter = document.createElement("jsp:directive.include");
		includeFooter.setAttribute("file", "/WEB-INF/views/backoffice/__footer.jsp");
		document.getDocumentElement().appendChild(includeFooter);
		return document;
	}	
	protected boolean isEnumType(JavaType type) {
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
	
	protected boolean isSpecialType(JavaType javaType) {
		String physicalTypeIdentifier = PhysicalTypeIdentifier.createIdentifier(javaType, Path.SRC_MAIN_JAVA);
		//we are only interested if the type is part of our application and if no editor exists for it already
		if (metadataService.get(physicalTypeIdentifier) != null) {
		  return true;
		}		
		return false;
	}	
}
