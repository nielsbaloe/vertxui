/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package live.connector.vertxui.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.shared.GwtIncompatible;

import elemental.client.Browser;
import live.connector.vertxui.fluentHtml.Body;
import live.connector.vertxui.fluentHtml.FluentHtml;
import live.connector.vertxui.fluentHtml.Li;

/**
 * HelloWorld application.
 */
public class HelloGWT implements EntryPoint {

	public void onModuleLoad() {

		Body body = FluentHtml.getBody();

		FluentHtml button = body.button("bladie");
		button.click(a -> {
			Browser.getWindow().alert("boew");
		});

		FluentHtml ul = body.ul();
		ul.li("test without loop");
		Arrays.asList("aaaaa", "aaaa", "a").stream().filter(a -> a.length() > 2).map(t -> new Li(t)).peek(a -> {
			consoleLog(a.tag() + " " + a.inner());
			ul.add(a);
		}).forEach(ul::add);
	}

	native void consoleLog(String message) /*-{
											console.log( "me:" + message );
											}-*/;

	@GwtIncompatible
	public static void main(String agrs[]) throws IOException, InterruptedException, UnableToCompleteException {
		boolean debug = true;

		// List<File> list = new ArrayList<>();
		// list.add(new File("src"));
		// ModuleDef module = new ModuleDef(thisClass.getName());// ,
		// module.addResourcePath("src");
		// module.addEntryPointTypeName(thisClass.getName());
		// // module.addisInherited("elemental.Elemental");
		// // module.addPublicPackage("elemental.Elemental", new String[0], new
		// // String[0], new String[0], false, false);
		//
		// // Directly (works, but classpath does not contain 'src'
		// Memory.initialize();
		// SpeedTracerLogger.init();
		// CompilerOptions options = new CompilerOptionsImpl();
		// options.addModuleName(thisClass.getName());
		// options.setStrict(true);
		// options.setClassMetadataDisabled(true);
		// options.setDisableUpdateCheck(true);
		// if (debug) {
		// options.setIncrementalCompileEnabled(true);
		// } else {
		// options.setOptimizationLevel(CompilerOptions.OPTIMIZE_LEVEL_MAX);
		// options.setIncrementalCompileEnabled(false);
		// }
		// com.google.gwt.dev.Compiler.compile(new PrintWriterTreeLogger(),
		// options, module);
		// System.exit(0);

		Class<?> thisClass = MethodHandles.lookup().lookupClass();
		String name = thisClass.getName();
		File gwtXml = new File("src/main/java/" + name.replace(".", "/") + ".gwt.xml");
		try {
			FileUtils.writeStringToFile(gwtXml,
					"<module rename-to='a'><inherits name='elemental.Elemental'/><entry-point class='" + name
							+ "'/><source path=''/></module>");
			String options = "-strict -XnoclassMetadata -XdisableUpdateCheck";
			if (debug) {
				options += " -draftCompile -optimize 0 -incremental";
			} else {
				options += " -nodraftCompile -optimize 9 -noincremental";
			}
			String classpath = System.getProperty("java.class.path") + ";src";
			String line = null;
			String className = MethodHandles.lookup().lookupClass().getName();
			Process p = Runtime.getRuntime()
					.exec("java -cp " + classpath + " com.google.gwt.dev.Compiler " + options + " " + className);
			try (BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
				while (p.isAlive()) {
					while ((line = bri.readLine()) != null) {
						System.out.println(line);
					}
					while ((line = bre.readLine()) != null) {
						System.err.println(line);
					}
					p.waitFor();
				}
			}
		} finally {
			gwtXml.delete();
		}
		FileUtils.writeStringToFile(new File("war/index.html"),
				"<!DOCTYPE html><html><body><script type='text/javascript' src='a/a.nocache.js?time=" + Math.random()
						+ "'></script></body></html>");
	}

}
