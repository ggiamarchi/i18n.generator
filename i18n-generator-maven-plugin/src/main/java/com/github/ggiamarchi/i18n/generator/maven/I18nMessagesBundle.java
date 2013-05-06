package com.github.ggiamarchi.i18n.generator.maven;


public class I18nMessagesBundle {
	
	private String name;
	private String outputDirectory;
	private String interfaceName;
	private String className;
	private boolean useSrcFolder = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isUseSrcFolder() {
		return useSrcFolder;
	}

	public void setUseSrcFolder(boolean useSrcFolder) {
		this.useSrcFolder = useSrcFolder;
	}
	
}
