package com.github.ggiamarchi.i18n.generator.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.github.ggiamarchi.i18n.generator.GeneratorLauncher;

public class I18NGeneratorTask extends Task {
	
	private String bundle;
	private String dir;
	private String interfaceName;
	private String className;
	private String output;
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public void execute() throws BuildException {
		if (bundle == null || bundle.isEmpty()) {
			throw new BuildException("'bundle' property must be set for 'i18n-generator' task");
		}
		if (dir == null || dir.isEmpty()) {
			throw new BuildException("'dir' property must be set for 'i18n-generator' task");
		}
		if (output == null || output.isEmpty()) {
			throw new BuildException("'output' property must be set for 'i18n-generator' task");
		}
		GeneratorLauncher launcher = new GeneratorLauncher();
		launcher.execute(bundle, interfaceName, className, null, new String [] { getProject().getBaseDir() + "/" + dir}, getProject().getBaseDir() + "/" + output);
	}

}
