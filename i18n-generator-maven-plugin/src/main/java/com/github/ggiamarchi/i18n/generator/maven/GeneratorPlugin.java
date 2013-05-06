package com.github.ggiamarchi.i18n.generator.maven;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import com.github.ggiamarchi.i18n.generator.GeneratorLauncher;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = false)
public class GeneratorPlugin extends AbstractMojo {

	private Logger log;
	
	/* *** Configurable parameters **************************************************** */
	
	@Parameter(required = false)
	private I18nMessagesBundle i18nMessagesBundle;

	@Parameter(required = false)
	private List<I18nMessagesBundle> i18nMessagesBundles;

	@Parameter
    private String outputDirectory;

	/* *** Readonly project's configuration parameters ******************************** */
	
	@Parameter(defaultValue = "${project.basedir.absolutePath}", readonly = true)
    private String projectBaseDir;
	
	@Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true)	
	private String srcDirectory;
	
	@Parameter(defaultValue = "${project.build.directory}/generated-sources/i18n", readonly = true)
	private String defaultOutputDirectory;

	@Parameter(defaultValue = "${project.build.resources}", readonly = true)	
	private List<Resource> resources;
	
	/* *** Internal parameters ******************************************************** */
	
	private String [] resourcesDirectories;

	/* ******************************************************************************** */

	public void execute() throws MojoExecutionException, MojoFailureException {

	    // bind slf4j to maven log
	    StaticLoggerBinder.getSingleton().setLog(getLog());
	    log = LoggerFactory.getLogger(GeneratorPlugin.class);
	    
	    resourcesDirectories = new String[resources.size()];
	    for (int i = 0 ; i < resourcesDirectories.length ; i++) {
	    	resourcesDirectories[i] = resources.get(i).getDirectory();
	    }

	    if (i18nMessagesBundle == null && i18nMessagesBundles == null) {
	    	throw new MojoFailureException("<i18nMessagesBundle> or <i18nMessagesBundles> must be configured");
	    }

	    if (i18nMessagesBundle != null && i18nMessagesBundles != null) {
	    	throw new MojoFailureException("<i18nMessagesBundle> and <i18nMessagesBundles> cannot be configured simultaneously");
	    }
	    
	    Set<I18nMessagesBundle> bundles = new HashSet<I18nMessagesBundle>();
	    
	    if (i18nMessagesBundle != null) {
	    	bundles.add(i18nMessagesBundle);
	    }
	    else {
	    	bundles.addAll(i18nMessagesBundles);
		}

	    // Check pre-conditions
	    
	    for (I18nMessagesBundle i18nMessagesBundle : bundles) {
			String name = i18nMessagesBundle.getName();
			if (name == null || name.isEmpty()) {
				throw new MojoFailureException("I18nMessagesBundle name must be defined");
			}
	    }
	    
	    // Run generations
	    
	    for (I18nMessagesBundle i18nMessagesBundle : bundles) {
	    	generate(i18nMessagesBundle);
	    }
    }

	private void generate(I18nMessagesBundle bundle) throws MojoFailureException {

		log.info("\nStating generation with bundle \"{}\"", bundle.getName());

		String outputDirectory;

		if (bundle.getOutputDirectory() != null) {
			outputDirectory = projectBaseDir + "/" + bundle.getOutputDirectory();
		}
		else {
			if (this.outputDirectory == null) {
				outputDirectory = defaultOutputDirectory;
			}
			else {
				outputDirectory = projectBaseDir + "/" + this.outputDirectory;
			}
		}
		
		if (outputDirectory == null) {
			throw new MojoFailureException("<outputDirectory> value cannot be empty");
		}
		
    	new GeneratorLauncher().execute(
    			bundle.getName(), bundle.getInterfaceName(), bundle.getClassName(),
    			srcDirectory, resourcesDirectories, outputDirectory);
	}

}
