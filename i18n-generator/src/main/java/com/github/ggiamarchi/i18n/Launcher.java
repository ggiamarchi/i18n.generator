package com.github.ggiamarchi.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class Launcher {

	private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

	private static Configuration cfg;
	
	static {
		/*
		 * Init freemarker generation engine
		 */
		cfg = new Configuration();
		cfg.setClassForTemplateLoading(Launcher.class, "/templates");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}

	/**
	 * 
	 * @param bundles
	 * @param srcDirectory
	 * @param resourcesDirectories
	 * @param outputDirectory
	 */
	public void run(
			String [] bundles,
			String srcDirectory,
			String [] resourcesDirectories,
			String outputDirectory) {

		String normalizedSrcDirectory = normalizeFolderPath(srcDirectory);
		String normalizedOutputDirectory = normalizeFolderPath(outputDirectory);
		String [] normalizedResourcesDirectories = new String[resourcesDirectories.length];

        LOG.info("");
        LOG.info("I18N bundle configuration :");
        LOG.info("  Bundles   => {}", bundles);
        LOG.info("  Output    => {}", normalizedOutputDirectory);
        LOG.info("  Sources   => {}", normalizedSrcDirectory);

        for (int i = 0 ; i < resourcesDirectories.length ; i++) {
        	normalizedResourcesDirectories[i] = normalizeFolderPath(resourcesDirectories[i]);
        	LOG.info("  Resources => {}", normalizedResourcesDirectories[i]);
        }

        for (String bundleName : bundles) {

        	int i = bundleName.lastIndexOf('.');
        	String packageName = bundleName.substring(0, i);
        	// TODO bundles are searched only in the first resource folder found. Implement search into all resource folders and src folder
        	String bundleDirectoryName = normalizeFolderPath(normalizedResourcesDirectories[0] + packageName.replace('.', '/'));
        	
        	String bundleSimpleName = bundleName.substring(i+1);
        	String bundleSimpleNameFirstUpper = bundleSimpleName.substring(0, 1).toUpperCase() + bundleSimpleName.substring(1);

        	String fullyQualifiedPackagePathName = normalizeFolderPath(bundleName.substring(0, i).replace('.', '/'));
        	String outputClassFileName = normalizedOutputDirectory + fullyQualifiedPackagePathName + getImplementationName(bundleSimpleNameFirstUpper, true);
        	String outputInterfaceFileName = normalizedOutputDirectory + fullyQualifiedPackagePathName + getInterfaceName(bundleSimpleNameFirstUpper, true);

        	File bundleDirectory = new File(bundleDirectoryName);

        	if (!bundleDirectory.isDirectory()) {
        		throw new I18NGeneratorException("'" + bundleDirectoryName + "' is not a directory");
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
    		
    		model.put("packageName", packageName);
    		model.put("interfaceName", getInterfaceName(bundleSimpleNameFirstUpper, false));
    		model.put("methods", methods);       	
    		model.put("className", getImplementationName(bundleSimpleNameFirstUpper, false));
    		model.put("bundleName", bundleName);

    		// Running interface code generation
    		
    		generate(model, "i18n-java-interface.ftl", outputInterfaceFileName);

    		// Running implementation class code generation
    		
    		generate(model, "i18n-java-class.ftl", outputClassFileName);

       }

	}
	
	private String getImplementationName(String bundleSimpleNameFirstUpper, boolean extension) {
		if (extension) {
			return bundleSimpleNameFirstUpper + "Impl.java";
		}
		return bundleSimpleNameFirstUpper + "Impl";
	}

	private String getInterfaceName(String bundleSimpleNameFirstUpper, boolean extension) {
		if (extension) {
			return bundleSimpleNameFirstUpper + ".java";
		}
		return bundleSimpleNameFirstUpper;
	}
	

	private List<Method> computeMethodList(String defaultBundleFile) {
		List<Method> methods = new ArrayList<Method>();

		ResourceBundle defaultBundle;
		try {
			defaultBundle = new PropertyResourceBundle(new FileInputStream(new File(defaultBundleFile)));
		}
		catch (FileNotFoundException e) {
			throw new I18NGeneratorException(e);
		}
		catch (IOException e) {
			throw new I18NGeneratorException(e);
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
	 * code source file generation.
	 * 
	 * @param model map containing data that will be used in the generation template
	 * @param templateName freemarker template name
	 * @param file file path for generation output
	 */
	private void generate(Map<String, Object> model, String templateName, String file) {
		try {
			File outFile = new File(file);
			outFile.getParentFile().mkdirs();
			FileWriter fw = new FileWriter(outFile);
			Template template = cfg.getTemplate(templateName);
			template.process(model, fw);
			fw.close();
		}
		catch (IOException e) {
			throw new I18NGeneratorException(e);
		}
		catch (TemplateException e) {
			throw new I18NGeneratorException(e);
		}
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
