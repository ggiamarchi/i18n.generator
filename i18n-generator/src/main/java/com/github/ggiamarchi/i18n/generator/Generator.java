package com.github.ggiamarchi.i18n.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
		cfg.setClassForTemplateLoading(GeneratorLauncher.class, "/templates");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}

	
	/**
	 * code source file generation. This method is a shotcut to {@link Generator#generate(Map, String, Writer)}
	 * that construct a {@link FileWriter}.
	 */
	public void generate(Map<String, Object> model, String templateName, String file) {
		try {
			File outFile = new File(file);
			outFile.getParentFile().mkdirs();
			FileWriter fw = new FileWriter(outFile);
			generate(model, templateName, fw);
		}
		catch (IOException e) {
			throw new GeneratorException(e);
		}
	}

	/**
	 * code source file generation.
	 * 
	 * @param model map containing data that will be used in the generation template
	 * @param templateName freemarker template name
	 * @param file writer to write into. THe writer is closed at the end of the generation process
	 */
	public void generate(Map<String, Object> model, String templateName, Writer writer) {
		try {
			Template template = cfg.getTemplate(templateName);
			template.process(model, writer);
			writer.close();
		}
		catch (IOException e) {
			throw new GeneratorException(e);
		}
		catch (TemplateException e) {
			throw new GeneratorException(e);
		}
	}

}
