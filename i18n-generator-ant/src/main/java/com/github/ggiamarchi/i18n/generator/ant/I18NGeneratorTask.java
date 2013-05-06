package com.github.ggiamarchi.i18n.generator.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.github.ggiamarchi.i18n.generator.GeneratorLauncher;

public class I18NGeneratorTask extends Task {
	
	private String bundle;
	private String dir;
	private String output;
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public void execute() throws BuildException {
		System.out.println(bundle);
		System.out.println(dir);
		System.out.println(output);
		
		GeneratorLauncher launcher = new GeneratorLauncher();
		
		launcher.execute(bundle, null, null, null, new String [] {dir}, output);

		
	}
	
}
