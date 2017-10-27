package com.onpositive.semantic.generator.model;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.onpositive.semantic.language.model.ElementModel;


public class XMLStreamGenerator
{
	protected CandidatesHolder curHolder;
	
	
	public XMLStreamGenerator(CandidatesHolder holder)
	{
		curHolder = holder;
	}
	
	public StreamResult serializeToXML()
	{
		Document doc = makeDoc();
		return prettyFormat(doc);
	}
	
	/** Generate the XML document */
	  protected Document makeDoc() {
	    try {
	      DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
	      DocumentBuilder parser = fact.newDocumentBuilder();
	      Document doc = parser.newDocument();
	      Node root = createRootElement(doc);
	      doc.appendChild(root);
	      createModel(doc, root);
	      createPropertiesFields(doc, root);
	      return doc;

	    } catch (Exception ex) {
	      System.err.println("+============================+");
	      System.err.println("|        XML Error           |");
	      System.err.println("+============================+");
	      System.err.println(ex.getClass());
	      System.err.println(ex.getMessage());
	      System.err.println("+============================+");
	      return null;
	    }
	  }

	protected void createPropertiesFields(Document doc, Node root)
	{
		List<CandidateConfigurationListElement> properties = curHolder.getProperties();
		for (Iterator iterator = properties.iterator(); iterator.hasNext();)
		{
			CandidateConfigurationListElement element = (CandidateConfigurationListElement) iterator
					.next();
			if (element.isUsed())
			{
				Element uiElement = element.getCreator().getConfigurator().getElement(element.getCurCandidate(),doc);
				root.appendChild(uiElement);
			}
		}		
	}
	
	protected StreamResult prettyFormat(Document doc)
	{
		try {
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult(byteArrayOutputStream);
            Transformer serializer = TransformerFactory.newInstance()
                    .newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING,Charset.defaultCharset().name());
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            serializer.transform(new DOMSource(doc), streamResult);
            System.out.println("Привет");
            return streamResult;
        } 
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected Element createModel(Document doc, Node root)
	{
		Element model = doc.createElement("model");
		root.appendChild(model);
		List<CandidateConfigurationListElement> properties = curHolder.getProperties();
		for (Iterator iterator = properties.iterator(); iterator.hasNext();)
		{
			CandidateConfigurationListElement element = (CandidateConfigurationListElement) iterator
					.next();
			if (element.isUsed())
			{
				Element bindingElement = element.getCreator().getIBindingConfigurator().getBindingElement(element.getCurCandidate(),doc);
				model.appendChild(bindingElement);
			}
		}		
		root.appendChild(model);
		return model;
	}

	protected Node createRootElement(Document doc)
	{
		ElementModel curModel = curHolder.getCurMainWindowModel();
		Element rootElement = doc.createElement(curModel.getName());
		rootElement.setAttribute("xmlns", curModel.getOwner().getPath());
		return rootElement;
	}
}
