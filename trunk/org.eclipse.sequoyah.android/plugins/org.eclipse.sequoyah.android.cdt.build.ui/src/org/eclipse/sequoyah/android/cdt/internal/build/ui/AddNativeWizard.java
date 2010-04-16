/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IPathEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.internal.core.ToolChain;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sequoyah.android.cdt.build.ui.TemplatedInputStream;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;

/**
 * @author dschaefer
 *
 */
public class AddNativeWizard extends Wizard {

	private final IWorkbenchWindow window;
	private final IProject project;
	
	private AddNativeProjectPage projectPage;
	private AddNativeNDKPage ndkPage;
	
	public AddNativeWizard(IWorkbenchWindow window, IProject project) {
		this.window = window;
		this.project = project;
		
		setWindowTitle("Add Android Native Support");
		setDialogSettings(Activator.getDefault().getDialogSettings());
	}
	
	@Override
	public void addPages() {
		projectPage = new AddNativeProjectPage(project);
		addPage(projectPage);
		
		ndkPage = new AddNativeNDKPage();
		addPage(ndkPage);
	}
	
	@Override
	public boolean canFinish() {
		return ndkPage.isNDKLocationValid();
	}
	
	@Override
	public boolean performFinish() {
		// Switch to the C perspective
		try {
			window.getWorkbench().showPerspective("org.eclipse.cdt.ui.CPerspective", window); //$NON-NLS-1
		} catch (WorkbenchException e) {
			Activator.getDefault().getLog().log(e.getStatus());
		}

		// Grab the data from the pages
		final String libraryName = projectPage.getLibraryName();
		final String sourceFolderName = projectPage.getSourceFolderName();
		final String outputFolderName = projectPage.getOutputFolderName();

		// TODO this should be in the build env
		final String ndkDir = ndkPage.getNDKLocation();
		final String androidVer = ndkPage.getAndroidVer();
		final String gccVer = ndkPage.getGCCVer();
		final String architecture = ndkPage.getArch();
		
		// Save the data
		projectPage.saveSettings();
		ndkPage.saveSettings();
		
		// The operation to do all the dirty work
		IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// This one is constant defined by Android
				String libFolderName = "libs";
				IToolChain toolChain = null;
				
				// Convert to CDT project
				CCorePlugin.getDefault().createCDTProject(project.getDescription(), project, monitor);
				CCProjectNature.addCCNature(project, new SubProgressMonitor(monitor, 1));
							
				// Set up build information
				ICProjectDescriptionManager pdMgr = CoreModel.getDefault().getProjectDescriptionManager();
				ICProjectDescription projDesc = pdMgr.createProjectDescription(project, false);
				ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(project);
				ManagedProject mProj = new ManagedProject(projDesc);
				info.setManagedProject(mProj);
							
				CfgHolder cfgHolder = new CfgHolder(toolChain, null);
				String s = toolChain == null ? "0" : ((ToolChain)toolChain).getId(); //$NON-NLS-1$
				Configuration config = new Configuration(mProj, (ToolChain)toolChain, ManagedBuildManager.calculateChildId(s, null), cfgHolder.getName());
				IBuilder builder = config.getEditableBuilder();
				builder.setManagedBuildOn(false);
				CConfigurationData data = config.getConfigurationData();
				projDesc.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
							
				pdMgr.setProjectDescription(project, projDesc);
					
				// Create the source and output folders
				IFolder sourceFolder = project.getFolder(sourceFolderName);
				if (!sourceFolder.exists())
					sourceFolder.create(true, true, monitor);
				IPathEntry sourceEntry = CoreModel.newSourceEntry(sourceFolder.getFullPath());
				
				IFolder outputFolder = project.getFolder(outputFolderName);
				if (!outputFolder.exists())
					outputFolder.create(true, true, monitor);
				IPathEntry outputEntry = CoreModel.newOutputEntry(outputFolder.getFullPath());
				
				IFolder libFolder = project.getFolder(libFolderName);
				if (!libFolder.exists())
					libFolder.create(true, true, monitor);
				IPathEntry libEntry = CoreModel.newOutputEntry(libFolder.getFullPath());
				
				// Set up the path entries for the source and output folders
				CoreModel model = CCorePlugin.getDefault().getCoreModel();
				ICProject cproject = model.create(project);
				IPathEntry[] pathEntries = cproject.getRawPathEntries();
				List<IPathEntry> newEntries = new ArrayList<IPathEntry>(pathEntries.length + 3);
				for (IPathEntry pathEntry : pathEntries) {
					// remove the old source and output entries
					if (pathEntry.getEntryKind() != IPathEntry.CDT_SOURCE
							&& pathEntry.getEntryKind() != IPathEntry.CDT_OUTPUT) {
						newEntries.add(pathEntry);
					}
				}
				newEntries.add(sourceEntry);
				newEntries.add(outputEntry);
				newEntries.add(libEntry);
				cproject.setRawPathEntries(newEntries.toArray(new IPathEntry[newEntries.size()]), monitor);
				
				// Generate the Makefile
				try {
					Map<String, String> map = new HashMap<String, String>();
					map.put("lib", libraryName);
					map.put("src", sourceFolderName);
					map.put("obj", outputFolderName);
					map.put("ndkDir", ndkDir);
					map.put("arch", architecture);
					map.put("gccVer", gccVer);
					map.put("androidVer", androidVer);
					
					String os = Platform.getOS();
					String host = null;
					if (Platform.OS_WIN32.equals(os))
						host = "windows"; // TODO check this
					else if (Platform.OS_LINUX.equals(os))
						host = "linux-x86";
					else if (Platform.OS_MACOSX.equals(os))
						host = "mac"; // TODO check this
					map.put("host", host);
					
					IPath templatePath = new Path("templates/Makefile");
					URL templateURL = FileLocator.find(Activator.getDefault().getBundle(), templatePath, null);
					InputStream templateIn = templateURL.openStream();
					InputStream in = new TemplatedInputStream(templateIn, map);
					
					IFile makefile = project.getFile("Makefile");
					if (!makefile.exists())
						makefile.create(in, true, monitor);
					else
						makefile.setContents(in, true, false, monitor);
					templateIn.close();
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
				}
			}
		};
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			workspace.run(op, workspace.getRoot(), 0, null);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
		}
		
		return true;
	}

}
