/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sequoyah.android.cdt.build.core.INDKService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author dschaefer
 *
 */
public class AddNativeNDKPage extends WizardPage {

	private static final String ANDROID_VER = AddNativeNDKPage.class.getName() + ".androidVer";
	private static final String GCC_VER = AddNativeNDKPage.class.getName() + ".gccVer";
	private static final String ARCH = AddNativeNDKPage.class.getName() + ".arch";
	
	private static final String DEFAULT_ANDROID_VER = "android-4";
	private static final String DEFAULT_GCC_VER = "4.4.0";
	private static final String DEFAULT_ARCH = "armeabi";
	
	private Text location;
	private Text androidVer;
	private Text gccVer;
	private Text arch;
	
	public AddNativeNDKPage() {
		super("ndkPage");
		setTitle("Build");
		setDescription("Settings for adjusting the build");
	}

	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		addNDKLocation(comp);
		addAndroidVer(comp);
		addGCCVer(comp);
		addArch(comp);
		
		setControl(comp);
	}

	private void addNDKLocation(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("NDK Location");

		location = new Text(group, SWT.BORDER);
		location.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		location.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateNDKLocation();
			}
		});
		validateNDKLocation();
		
		File ndkLocDir = Activator.getDefault().getService(INDKService.class).getNDKLocation();
		if (ndkLocDir != null)
			location.setText(ndkLocDir.getAbsolutePath());
		
		Button browse = new Button(group, SWT.NONE);
		browse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		browse.setText("Browse");
		browse.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(location.getShell());
				dialog.setMessage("NDK Location");
				String dir = dialog.open();
				if (dir != null)
					location.setText(dir);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public boolean isNDKLocationValid() {
		String locStr = location.getText();
		File locFile = new File(locStr);
		if (locFile.exists()) {
			return true;
		}
		
		return false;
	}
	
	private void validateNDKLocation() {
		if (isNDKLocationValid())
			setErrorMessage(null);
		else
			setErrorMessage("Invalid Android NDK location");
		
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}
	
	// TODO this should go away once we get this into the build env
	public String getNDKLocation() {
		return location.getText();
	}
	
	private void addAndroidVer(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("Android platform version");
		
		IDialogSettings settings = getWizard().getDialogSettings();
		String ver = settings.get(ANDROID_VER);
		if (ver == null)
			ver = DEFAULT_ANDROID_VER;

		androidVer = new Text(group, SWT.BORDER);
		androidVer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		androidVer.setText(ver);
	}
	
	public String getAndroidVer() {
		return androidVer.getText();
	}

	private void addGCCVer(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("GCC version");
		
		IDialogSettings settings = getWizard().getDialogSettings();
		String ver = settings.get(GCC_VER);
		if (ver == null)
			ver = DEFAULT_GCC_VER;

		gccVer = new Text(group, SWT.BORDER);
		gccVer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		gccVer.setText(ver);
	}
	
	public String getGCCVer() {
		return gccVer.getText();
	}
	
	private void addArch(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("Target architecture");
		
		IDialogSettings settings = getWizard().getDialogSettings();
		String ver = settings.get(ARCH);
		if (ver == null)
			ver = DEFAULT_ARCH;

		arch = new Text(group, SWT.BORDER);
		arch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		arch.setText(ver);
	}
	
	public String getArch() {
		return arch.getText();
	}
	
	public void saveSettings() {
		Activator.getDefault().getService(INDKService.class).setNDKLocation(new File(location.getText()));
		
		IDialogSettings settings = getWizard().getDialogSettings();
		settings.put(ANDROID_VER, androidVer.getText());
		settings.put(GCC_VER, gccVer.getText());
		settings.put(ARCH, arch.getText());
	}
	
}
