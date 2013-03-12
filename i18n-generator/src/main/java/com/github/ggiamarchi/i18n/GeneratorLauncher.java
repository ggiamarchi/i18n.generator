package com.github.ggiamarchi.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GeneratorLauncher {

	private static final Logger LOG = LoggerFactory.getLogger(GeneratorLauncher.class);

	/**
	 * 
	 * @param bundles
	 * @param interfaceName 
	 * @param className
	 * @param srcDirectory
	 * @param resourcesDirectories
	 * @param outputDirectory
	 */
	public void execute(
			String bundleName, String interfaceName, String className,
			String srcDirectory, String [] resourcesDirectories,
			String outputDirectory) {

		String normalizedSrcDirectory = normalizeFolderPath(srcDirectory);
		String normalizedOutputDirectory = normalizeFolderPath(outputDirectory);
		String [] normalizedResourcesDirectories = new String[resourcesDirectories.length];

        LOG.info("");
        LOG.info("I18N bundle configuration :");
        LOG.info("  Bundle    => {}", bundleName);
        LOG.info("  Output    => {}", normalizedOutputDirectory);
        LOG.info("  Sources   => {}", normalizedSrcDirectory);

        for (int i = 0 ; i < resourcesDirectories.length ; i++) {
        	normalizedResourcesDirectories[i] = normalizeFolderPath(resourcesDirectories[i]);
        	LOG.info("  Resources => {}", normalizedResourcesDirectories[i]);
        }

    	int indexStartBundleSimpleName = bundleName.lastIndexOf('.');
    	
    	String bundlePackageName;
    	String bundleSimpleName;
    	if (indexStartBundleSimpleName == -1) {
    		bundlePackageName = "";
    		bundleSimpleName = bundleName;
    	}
    	else {
    		bundlePackageName = bundleName.substring(0, indexStartBundleSimpleName);
    		bundleSimpleName = bundleName.substring(indexStartBundleSimpleName+1);
    	}

    	String bundleSimpleNameFirstUpper = bundleSimpleName.substring(0, 1).toUpperCase() + bundleSimpleName.substring(1);

    	String defaultQualifiedPackagePathName = normalizeFolderPath(bundlePackageName.replace('.', '/'));

    	// TODO bundles are searched only in the first resource folder found. Implement search into all resource folders and src folder
    	String bundleDirectoryName = normalizeFolderPath(normalizedResourcesDirectories[0] + bundlePackageName.replace('.', '/'));


    	// Compute output file name for generated interface
    	
    	String interfacePackageName;
    	String simpleInterfaceName;
    	String outputInterfaceFileName;
    	if (interfaceName == null) {
    		interfacePackageName = bundlePackageName;
    		simpleInterfaceName = bundleSimpleNameFirstUpper;
    		outputInterfaceFileName = normalizedOutputDirectory + defaultQualifiedPackagePathName + simpleInterfaceName + ".java";
    	}
    	else {
    		int i = interfaceName.lastIndexOf('.');
    		if (i == -1) {
    			interfacePackageName = "";
    			simpleInterfaceName = interfaceName;    			
    		}
    		else {
    			interfacePackageName = interfaceName.substring(0, i);
    			simpleInterfaceName = interfaceName.substring(i+1);    			
    		}
    		String qualifiedPackagePathName = normalizeFolderPath(interfacePackageName.replace('.', '/'));
    		outputInterfaceFileName = normalizedOutputDirectory + qualifiedPackagePathName + simpleInterfaceName + ".java";
    	}

    	// Compute output file name for implementation class

    	String classPackageName;
    	String simpleClassName;
    	String outputClassFileName;
    	if (className == null) {
    		classPackageName = bundlePackageName;
    		simpleClassName = bundleSimpleNameFirstUpper + "Impl";
    		outputClassFileName = normalizedOutputDirectory + defaultQualifiedPackagePathName + simpleClassName + ".java";
    	}
    	else {
    		int i = className.lastIndexOf('.');
    		if (i == -1) {
    			classPackageName = "";
    			simpleClassName = className;    			
    		}
    		else {
    			classPackageName = className.substring(0, i);
    			simpleClassName = className.substring(i+1);    			
    		}
    		String qualifiedPackagePathName = normalizeFolderPath(classPackageName.replace('.', '/'));
    		outputClassFileName = normalizedOutputDirectory + qualifiedPackagePathName + simpleClassName + ".java";
    	}

    	//
    	
    	File bundleDirectory = new File(bundleDirectoryName);

    	if (!bundleDirectory.isDirectory()) {
    		throw new GeneratorException("'" + bundleDirectoryName + "' is not a directory");
    	}

		String defaultBundleFilePath = null; 
		
		Pattern patternDefault = Pattern.compile(bundleSimpleName + "\\.properties");
    	Pattern patternLangageOnly = Pattern.compile(bundleSimpleName + "_([a-z][a-z])\\.properties");
    	Pattern patternLangageAndCountry = Pattern.compile(bundleSimpleName + "_([a-z][a-z])_([A-Z][A-Z])\\.properties");
    	Pattern patternInvalidLocale = Pattern.compile(bundleSimpleName + "_.*\\.properties");

    	Map<Locale, String> localizedBundleFiles = new HashMap<Locale, String>();
    	
    	for (String bundleFileName : bundleDirectory.list()) {
    		Matcher m = patternDefault.matcher(bundleFileName);
    		if (m.find()) {
    			// This is the default bundle message file. Nothing to do.
    			defaultBundleFilePath = normalizeFolderPath(bundleDirectoryName + bundleFileName);
    			continue;
    		}
    		m = patternLangageOnly.matcher(bundleFileName);
    		if (m.find()) {
    			localizedBundleFiles.put(new Locale(m.group(1)), bundleFileName);
    		}
    		else {
    			m = patternLangageAndCountry.matcher(bundleFileName);
        		if (m.find()) {
        			localizedBundleFiles.put(new Locale(m.group(1), m.group(2)), bundleFileName);
        		}
        		else {
        			m = patternInvalidLocale.matcher(bundleFileName);
        			if (m.find()) {
        				LOG.warn("bundle file name {} is not correct regarding the local format. This file will be ignored.", bundleFileName);
        			}
        		}
    		}
    	}
    	
        LOG.info("");
    	LOG.info("Locales found => {}", localizedBundleFiles);
    	
    	if (defaultBundleFilePath == null) {
    		throw new IllegalStateException("default bundle file '" + bundleSimpleName + ".properties' cannot be found.");
    	}

    	// Compute method list
    	
    	List<Method> methods = computeMethodList(defaultBundleFilePath);

		// Building model for generation
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("interfacePackageName", interfacePackageName);
		model.put("classPackageName", classPackageName);
		model.put("interfaceName", simpleInterfaceName);
		model.put("className", simpleClassName);
		model.put("methods", methods);
		model.put("bundleName", bundleName);

		// Running code generation
		
		Generator generator = new Generator();
		
		generator.generate(model, "i18n-java-interface.ftl", outputInterfaceFileName);
		generator.generate(model, "i18n-java-class.ftl", outputClassFileName);

	}

	private List<Method> computeMethodList(String defaultBundleFile) {
		List<Method> methods = new ArrayList<Method>();

		ResourceBundle defaultBundle;
		try {
			defaultBundle = new PropertyResourceBundle(new FileInputStream(new File(defaultBundleFile)));
		}
		catch (FileNotFoundException e) {
			throw new GeneratorException(e);
		}
		catch (IOException e) {
			throw new GeneratorException(e);
		}

		Set<String> orderedKeySet = new TreeSet<String>();
		Enumeration<String> keys = defaultBundle.getKeys();
		while (keys.hasMoreElements()) {
			orderedKeySet.add(keys.nextElement());
		}

		Pattern p = Pattern.compile("\\{([0-9]+)\\}");

		for (String key : orderedKeySet) {
			Matcher m = p.matcher(defaultBundle.getString(key));
			List<String> params = new ArrayList<String>();
			int index = 0;
			while (m.find()) {
				String paramIdx = m.group(1);
				for (int j = index ; j < Integer.valueOf(paramIdx) ; j++) {
					params.add("arg" + j);
					index++;
				}
				params.add("arg" + paramIdx);
				index++;
			}
			methods.add(new Method(key, key.replace('.', '_'), params.toArray(new String [params.size()])));
		}

		//TODO implement checks for warning that messages are not present in for all locale

		return methods;
	}

	
	/**
	 * It normalize the input string according to the following rules :
	 * <ul>
	 *   <li>All occurrences of '\' are replaced by '/'</li>
	 *   <li>Several consecutive occurrences of '/' or '\' are replaced by a single character '/'</li>
	 *   <li>If it does not end with a '/', one is adding at the end of the string</li>
	 * </ul>
	 * 
	 * @param path path to normalize
	 * @return the normalized path
	 */
	public static String normalizeFolderPath(String path) {
		if (path == null || path.isEmpty()) {
			return "";
		}
		String s = path.replaceAll("(/|\\\\)+", "/");
		if (!s.endsWith("/")) {
			s += "/";
		}
		return s;
	}
	
	/**
	 * Class representing a method to be generated for a given message property.
	 */
	public static class Method {

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

}
