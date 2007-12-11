/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.framework.device.parser;

import org.jdom.Attribute;
import org.jdom.Element;


public class Property {
	private String id;
	private String name;
	private String description;
	private String scope;
	private String rule;
	
	public Property(String id){
		this.id = id;
	}
	
	public static Property readFromElement(Element element){
		Property property = new Property(element.getAttributeValue(XMLConstants.TAG_ID));
		property.setName(element.getChildText(XMLConstants.TAG_NAME));
		property.setDescription(element.getChildText(XMLConstants.TAG_DESCRIPTION));
		property.setScope(element.getChildText(XMLConstants.TAG_SCOPE));
		property.setRule(element.getChildText(XMLConstants.TAG_RULE));
		return property;
	}
	
	public static Element writeIntoElement(Property property) {
		Element element = new Element(XMLConstants.TAG_PROPERTY);
		element.setAttribute(new Attribute(XMLConstants.TAG_ID,property.getId()));
		element.addContent(new Element(XMLConstants.TAG_NAME).setText(property.getName()));
		element.addContent(new Element(XMLConstants.TAG_DESCRIPTION).setText(property.getDescription()));
		element.addContent(new Element(XMLConstants.TAG_SCOPE).setText(property.getScope()));
		element.addContent(new Element(XMLConstants.TAG_RULE).setText(property.getRule()));			
		return element;
	}
	
	public Element writeIntoElement() {
		return writeIntoElement(this);
	}
	

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getScope() {
		return scope;
	}

	public String getRule() {
		return rule;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public String toString(){
		return "[Property: id=" + (id==null?"":id) +
				",name=" + (name==null?"":name) +
				",description=" + (description==null?"":description) +
				",scope=" + (scope==null?"":scope) +
				",rule="+ (rule==null?"":rule) +" ]";
	}
	
	
}
