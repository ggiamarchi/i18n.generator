package com.github.ggiamarchi.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

/**
 * 
 * @author G. Giamarchi
 */
public class Generator {

	private static final Logger LOG = LoggerFactory.getLogger(Generator.class);

	private static Configuration cfg;
	
	static {
		/*
		 * Init freemarker generation engine
		 */
		cfg = new Configuration();
		cfg.setClassForTemplateLoading(Generator.class, "/templates");
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

        LOG.info("");
        LOG.info("I18N bundle configuration :");
        LOG.info("  Bundles   => {}", bundles);
        LOG.info("  Output    => {}", outputDirectory);
        LOG.info("  Sources   => {}", srcDirectory);
        for (String res : resourcesDirectories) {
        	LOG.info("  Resources => {}", res);
        }

        for (String bundleName : bundles) {

        	// TODO file path implementation
        	
        	String dir = resourcesDirectories[0] + "/";
        	int i = bundleName.lastIndexOf('.');
        	String packageName = bundleName.substring(0, i);
        	dir += packageName.replace('.', '/');
        	String bundleSimpleName = bundleName.substring(i+1);
        	String className = bundleSimpleName.substring(0, 1).toUpperCase() + bundleSimpleName.substring(1);
        	String outputClassFileName = outputDirectory + "/" + bundleName.substring(0, i).replace('.', '/') + "/" + className + "Impl.java";
        	String outputInterfaceFileName = outputDirectory + "/" + bundleName.substring(0, i).replace('.', '/') + "/" + className + ".java";

        	File directory = new File(dir);
        	
        	if (!directory.isDirectory()) {
        		// TODO failure
        	}

    		String defaultBundleFile = null; 
    		
    		Pattern patternDefault = Pattern.compile(bundleSimpleName + ".properties");
        	Pattern patternLangageOnly = Pattern.compile(bundleSimpleName + "_([a-z][a-z]).properties");
        	Pattern patternLangageAndCountry = Pattern.compile(bundleSimpleName + "_([a-z][a-z])_([A-Z][A-Z]).properties");

        	Map<Locale, String> localizedBundleFiles = new HashMap<Locale, String>();
        	
        	for (String bundleFile : directory.list()) {
        		Matcher m = patternDefault.matcher(bundleFile);
        		if (m.find()) {
        			// This is the default bundle message file. Nothing to do.
        			defaultBundleFile = bundleFile;
        			continue;
        		}
        		m = patternLangageOnly.matcher(bundleFile);
        		if (m.find()) {
        			localizedBundleFiles.put(new Locale(m.group(1)), bundleFile);
        		}
        		else {
        			m = patternLangageAndCountry.matcher(bundleFile);
            		if (m.find()) {
            			localizedBundleFiles.put(new Locale(m.group(1), m.group(2)), bundleFile);
            		}
            		else {
            			// TODO or not todo ?
            			// LOG.warn("bundle file name {} is not correct regarding the local format. This file will be ignored.", bundleFile);
            		}
        		}
        	}
        	
        	if (defaultBundleFile == null) {
        		throw new IllegalStateException("default bundle file '" + bundleSimpleName + ".properties' cannot be found.");
        	}
        	
            LOG.info("");
        	LOG.info("Locales found => {}", localizedBundleFiles);
        	
        	// Compute method list
        	
        	List<Method> methods = computeMethodList(directory + "/" + defaultBundleFile); // TODO file path implementation

    		// Building model for generation
    		
    		Map<String, Object> model = new HashMap<String, Object>();
    		
    		model.put("packageName", packageName);
    		model.put("interfaceName", className);
    		model.put("methods", methods);       	
    		model.put("className", className + "Impl");
    		model.put("bundleName", bundleName);

    		// Running interface code generation
    		
    		generate(model, "i18n-java-interface.ftl", outputInterfaceFileName);

    		// Running implementation class code generation
    		
    		generate(model, "i18n-java-class.ftl", outputClassFileName);

       }

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
	 * Class representing a method to be generated for a given message bundle property.
	 */
	public class Method {

		private String property;
		private String name;
		private String [] parameters;

		/**
		 * @param property name of the property in the bundle
		 * @param name name of the generated method
		 * @param parameters parameters list in case of messages with placeholders
		 */
		public Method(String property, String name, String... parameters) {
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

	}

}
