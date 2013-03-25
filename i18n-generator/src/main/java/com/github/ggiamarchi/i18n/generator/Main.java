package com.github.ggiamarchi.i18n.generator;

import java.io.File;


/*

-bundle <bundle name> | -b <bundle name>
-dir <bundle directory> | -d <bundle directory>
-output <generation directory> | -o <generation directory>
[ -i <interface name> | -interface <interface name> ]
[ -c <impl class name> | -class <impl class name> ]
[ -noimpl ]

*/

public class Main {

	private String bundle = null;
	private String dir = null;
	private String output = null;
	private String interfaceName = null;
	private String className = null;
	private boolean noimpl = false;	

	public String getBundle() {
		return bundle;
	}

	public String getDir() {
		return dir;
	}

	public String getOutput() {
		return output;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getClassName() {
		return className;
	}

	public boolean isNoimpl() {
		return noimpl;
	}

	public void getArgs(String... args) {

		String arg;
		int i = -1;

		while (i < args.length - 1) {

			arg = args[++i];

			if ("-bundle".equals(arg) || "-b".equals(arg)) {
				bundle = args[++i];
				continue;
			}

			if ("-dir".equals(arg) || "-d".equals(arg)) {
				File dirFile = new File(args[++i]);
				dirFile.getParentFile().mkdirs();				
				dir = dirFile.getAbsolutePath();
				continue;
			}

			if ("-output".equals(arg) || "-o".equals(arg)) {
				File dirFile = new File(args[++i]);
				dirFile.getParentFile().mkdirs();				
				output = dirFile.getAbsolutePath();
				continue;
			}

			if ("-interface".equals(arg) || "-i".equals(arg)) {
				interfaceName = args[++i];
				continue;
			}

			if ("-class".equals(arg) || "-c".equals(arg)) {
				className = args[++i];
				continue;
			}

			if ("-noimpl".equals(arg)) {
				noimpl = true;
				continue;
			}

		}

	}
	
	public static void main(String[] args) {

		Main main = new Main();
		
		main.getArgs(args);

		GeneratorLauncher generator = new GeneratorLauncher();

		generator.execute(
				main.bundle,
				main.interfaceName,
				main.className,
				null,
				new String [] { main.dir },
				main.output);

	}


}
