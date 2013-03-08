package com.github.ggiamarchi.i18n;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Generator {

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
	 * code source file generation.
	 * 
	 * @param model map containing data that will be used in the generation template
	 * @param templateName freemarker template name
	 * @param file file path for generation output
	 */
	public void generate(Map<String, Object> model, String templateName, String file) {
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
	
}
