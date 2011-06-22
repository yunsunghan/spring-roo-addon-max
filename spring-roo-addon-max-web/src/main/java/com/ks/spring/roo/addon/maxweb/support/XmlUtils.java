package com.ks.spring.roo.addon.maxweb.support;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.roo.support.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utilities related to DOM and XML usage.
 * 
 * @author Stefan Schmidt
 * @author Ben Alex
 * @since 1.0
 * 
 */
public abstract class XmlUtils {

	private static final Map<String, XPathExpression> compiledExpressionCache = new HashMap<String, XPathExpression>();
	private static final XPath xpath = XPathFactory.newInstance().newXPath();
	
	public static final void writeXml(OutputStream outputEntry, Document document) {
		writeXml(createIndentingTransformer(), outputEntry, document);
	}

	public static final void writeMalformedXml(OutputStream outputEntry, NodeList nodes) {
		writeMalformedXml(createIndentingTransformer(), outputEntry, nodes);
	}

	public static final void writeXml(Transformer transformer, OutputStream outputEntry, Document document) {
		Assert.notNull(transformer, "Transformer required");
		Assert.notNull(outputEntry, "Output entry required");
		Assert.notNull(document, "Document required");
		
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		
		try {
			transformer.transform(new DOMSource(document), new StreamResult(new OutputStreamWriter(outputEntry, "UTF-8"/* "ISO-8859-1" */)));
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static final void writeMalformedXml(Transformer transformer, OutputStream outputEntry, NodeList nodes) {
		Assert.notNull(transformer, "Transformer required");
		Assert.notNull(outputEntry, "Output entry required");
		Assert.notNull(nodes, "NodeList required");

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		try {
			for (int i = 0; i < nodes.getLength(); i++) {
				transformer.transform(new DOMSource(nodes.item(i)), new StreamResult(new OutputStreamWriter(outputEntry, "UTF-8")));
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Checks in under a given root element whether it can find a child element
	 * which matches the XPath expression supplied. Returns {@link Element} if
	 * exists.
	 * 
	 * Please note that the XPath parser used is NOT namespace aware. So if you
	 * want to find a element <beans><sec:http> you need to use the following
	 * XPath expression '/beans/http'.
	 * 
	 * @param xPathExpression the xPathExpression (required)
	 * @param root the parent DOM element (required)
	 * 
	 * @return the Element if discovered (null if not found)
	 */
	public static Element findFirstElement(String xPathExpression, Element root) {
		if (xPathExpression == null || root == null || xPathExpression.length() == 0) {
			throw new IllegalArgumentException("Xpath expression and root element required");
		}

		Element rootElement = null;
		try {

			XPathExpression expr = compiledExpressionCache.get(xPathExpression);
			if (expr == null) {
				expr = xpath.compile(xPathExpression);
				compiledExpressionCache.put(xPathExpression, expr);
			}
			rootElement = (Element) expr.evaluate(root, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Unable evaluate xpath expression", e);
		}
		return rootElement;
	}

	/**
	 * Checks in under a given root element whether it can find a child element
	 * which matches the name supplied. Returns {@link Element} if exists.
	 * 
	 * @param name the Element name (required)
	 * @param root the parent DOM element (required)
	 * 
	 * @return the Element if discovered
	 */
	public static Element findFirstElementByName(String name, Element root) {
		Assert.hasText(name, "Element name required");
		Assert.notNull(root, "Root element required");
		return (Element) root.getElementsByTagName(name).item(0);
	}

	/**
	 * Checks in under a given root element whether it can find a child element
	 * which matches the XPath expression supplied. The {@link Element} must
	 * exist. Returns {@link Element} if exists.
	 * 
	 * Please note that the XPath parser used is NOT namespace aware. So if you
	 * want to find a element <beans><sec:http> you need to use the following
	 * XPath expression '/beans/http'.
	 * 
	 * @param xPathExpression the xPathExpression (required)
	 * @param root the parent DOM element (required)
	 * 
	 * @return the Element if discovered (never null; an exception is thrown if
	 *         cannot be found)
	 */
	public static Element findRequiredElement(String xPathExpression, Element root) {
		Assert.hasText(xPathExpression, "XPath expression required");
		Assert.notNull(root, "Root element required");
		Element element = findFirstElement(xPathExpression, root);
		Assert.notNull(element, "Unable to obtain required element '" + xPathExpression + "' from element '" + root + "'");
		return element;
	}

	/**
	 * Checks in under a given root element whether it can find a child elements
	 * which match the XPath expression supplied. Returns a {@link List} of
	 * {@link Element} if they exist.
	 * 
	 * Please note that the XPath parser used is NOT namespace aware. So if you
	 * want to find a element <beans><sec:http> you need to use the following
	 * XPath expression '/beans/http'.
	 * 
	 * @param xPathExpression the xPathExpression
	 * @param root the parent DOM element
	 * 
	 * @return a {@link List} of type {@link Element} if discovered, otherwise null
	 */
	public static List<Element> findElements(String xPathExpression, Element root) {
		List<Element> elements = new ArrayList<Element>();

		NodeList nodes = null;

		try {
			XPathExpression expr = xpath.compile(xPathExpression);
			nodes = (NodeList) expr.evaluate(root, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Unable evaluate xpath expression", e);
		}

		for (int i = 0; i < nodes.getLength(); i++) {
			elements.add((Element) nodes.item(i));
		}
		return elements;
	}

	/**
	 * @return a transformer that indents entries by 4 characters (never null)
	 */
	public static final Transformer createIndentingTransformer() {
		Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		return xformer;
	}

	/**
	 * @return a new document builder (never null)
	 */
	public static final DocumentBuilder getDocumentBuilder() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		factory.setNamespaceAware(true);
		try {
			return factory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
