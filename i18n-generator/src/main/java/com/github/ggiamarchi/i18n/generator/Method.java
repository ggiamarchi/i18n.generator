package com.github.ggiamarchi.i18n.generator;

import java.util.Arrays;

/**
 * Class representing a method to be generated for a given message property.
 */
public class Method {

	private String property;
	private String name;
	private String [] parameters;

	/**
	 * @param property name of the property in the bundle
	 * @param name name of the generated method
	 * @param parameters parameters list in case of messages with placeholders
	 * 
	 * @throws IllegalArgumentException if property or name are empty or <code>null</code>
	 */
	public Method(String property, String name, String... parameters) {
		
		if (property == null || property.length() == 0) {
			throw new IllegalArgumentException("property 'property' must not be null or empty");
		}
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("property 'name' must not be null or empty");
		}
		
		this.property = property;
		this.name = name;
		this.parameters = parameters;
		
	}

	public String getProperty() {
		return property;
	}

	public String getName() {
		return name;
	}

	public String [] getParameters() {
		return parameters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(parameters);
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		Method other = (Method) obj;
		
		if (name == null) {
			if (other.name != null) return false;
		}
		else {
			if (!name.equals(other.name)) return false;
		}
		
		if (!Arrays.equals(parameters, other.parameters)) return false;
		
		if (property == null) {
			if (other.property != null) return false;
		}
		else {
			if (!property.equals(other.property))return false;
		}
		
		return true;

	}

}