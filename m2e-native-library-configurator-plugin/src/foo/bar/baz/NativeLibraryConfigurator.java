package foo.bar.baz;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;
import org.eclipse.m2e.jdt.IClasspathManager;

public class NativeLibraryConfigurator extends AbstractJavaProjectConfigurator {

	@Override
	public void configureRawClasspath(ProjectConfigurationRequest request,
			IClasspathDescriptor classpath, IProgressMonitor monitor)
			throws CoreException {

		MavenProject mavenProject = request.getMavenProject();
		Plugin plugin = mavenProject.getPlugin("org.apache.maven.plugins:maven-surefire-plugin");
		Object configuration = plugin.getConfiguration();

		if (configuration instanceof Xpp3Dom) {
			Xpp3Dom confDom = (Xpp3Dom) configuration;
			Xpp3Dom argLine = confDom.getChild("argLine");
			if (argLine != null) {
				String argLineValue = argLine.getValue();
				if (argLineValue != null && !argLineValue.equals("")) {
					String javaLibraryPath = getJavaLibraryPath(argLineValue);
					if (javaLibraryPath != null) {
						addNativesPathToMavenContainer(
								classpath.getEntryDescriptors(),
								javaLibraryPath);
					}
				}
			}
		}

	}

	private void addNativesPathToMavenContainer(
			List<IClasspathEntryDescriptor> entrydescriptors, String nativesPath) {
		for (int i = 0; i < entrydescriptors.size(); i++) {
			IClasspathEntryDescriptor entry = entrydescriptors.get(i);
			if (isMaven2ClasspathContainer(entry.getPath())) {
				IClasspathAttribute nativeAttr = JavaRuntime
						.newLibraryPathsAttribute(new String[] { nativesPath });
				entry.setClasspathAttribute(nativeAttr.getName(),
						nativeAttr.getValue());
			}
		}
	}

	private boolean isMaven2ClasspathContainer(IPath containerPath) {
		return containerPath != null
				&& containerPath.segmentCount() > 0
				&& IClasspathManager.CONTAINER_ID.equals(containerPath
						.segment(0));
	}

	public static String getJavaLibraryPath(String argLineValue) {

		String regex = "-Djava\\.library\\.path=\"(.*)\\s*\"";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(argLineValue);
		
		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}
}
