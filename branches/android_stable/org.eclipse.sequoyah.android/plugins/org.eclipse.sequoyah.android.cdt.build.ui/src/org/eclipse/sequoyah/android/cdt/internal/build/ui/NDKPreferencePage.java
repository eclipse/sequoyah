package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.sequoyah.android.cdt.build.core.NDKUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * DESCRIPTION: 
 * This class represents the preference page for Android NDK.
 * It gives the user the option to set NDK and Cygwin (windows only) paths.
 * @author Carlos Alberto Souto Junior
 * */

public class NDKPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private DirectoryFieldEditor directoryEditorNDK;

    private boolean isNDKPathValid = true;

    private NDKPreferencePage preferencePage;

    private final static String WINDOWS = "windows";

    private final static String LINUX = "linux";

    private final static String MAC = "darwin";

    private final static String BIN_DIR = "bin";

    // Text constants
    private final String UI_PREFERENCES_NDK_PATH_LABEL = "NDK Location"; //$NON-NLS-N$

    private final String UI_PREFERENCES_NDK_INVALID_PATH_ERROR_MSG = "Invalid path for NDK"; //$NON-NLS-N$

    //search for windows folder
    private static FileFilter winFilter = new FileFilter()
    {
        public boolean accept(File file)
        {
            return file.getName().toLowerCase().indexOf(WINDOWS) > -1;
        }
    };

    //search for linux folder
    private static FileFilter linuxFilter = new FileFilter()
    {
        public boolean accept(File file)
        {
            return file.getName().toLowerCase().indexOf(LINUX) > -1;
        }
    };

    //search for mac folder
    private static FileFilter macFilter = new FileFilter()
    {
        public boolean accept(File file)
        {
            return file.getName().toLowerCase().indexOf(MAC) > -1;
        }
    };

    //search for arm-eabi folder
    private static FileFilter armFilter = new FileFilter()
    {
        public boolean accept(File file)
        {
            return file.getName().toLowerCase().indexOf("arm-eabi") > -1;
        }
    };

    //search for bin folder
    private static FileFilter binFilter = new FileFilter()
    {
        public boolean accept(File file)
        {
            return file.getName().equals(BIN_DIR);
        }
    };

    //search for gcc executable
    private static FileFilter gccFilter = new FileFilter()
    {
        public boolean accept(File file)
        {
            boolean showFile = file.getName().toLowerCase().indexOf("arm-eabi-gcc") > -1;

            if (Platform.getOS().equals(Platform.OS_WIN32))
            {
                showFile = showFile && file.getName().endsWith(".exe");
            }
            return showFile;
        }
    };

    private static String getPathPrefix()
    {
        String pathPrefix;
        if (Platform.getOS().equals(Platform.OS_WIN32))
        {
            pathPrefix = "\\" + "build" + "\\" + "prebuilt";
        }
        //linux or mac
        else
        {
            pathPrefix = "/" + "build" + "/" + "prebuilt";
        }
        return pathPrefix;

    }

    public NDKPreferencePage()
    {
        preferencePage = this;
        setPreferenceStore(PlatformUI.getPreferenceStore());

    }

    @Override
    /**
     * create preference page elements
     */
    protected Control createContents(Composite parent)
    {
        Composite main = new Composite(parent, SWT.FILL);
        main.setLayoutData(new GridData(GridData.FILL_BOTH));
        main.setLayout(new GridLayout(1, false));

        //NDK directory chooser
        directoryEditorNDK =
                new DirectoryFieldEditor(UIPlugin.NDK_LOCATION_PREFERENCE,
                        UI_PREFERENCES_NDK_PATH_LABEL, main);

        directoryEditorNDK.getTextControl(main).addModifyListener(new NDKListener());

        directoryEditorNDK.setStringValue(getPreferenceStore().getString(
                UIPlugin.NDK_LOCATION_PREFERENCE));

        return main;
    }

    /**
     * NDK folder validator
     *
     * @return
     */
    public static boolean validateNDKDirectory(String path)
    {

        boolean isValid = true;

        if (path.length() > 0)
        {
            //must be a directory and exist
            File NDKRootFolder = new File(path);
            if (!NDKRootFolder.isDirectory() || !NDKRootFolder.exists())
            {
                isValid = false;
            }

            if (isValid)
            {
                // <ndk_root>/build/prebuilt
                path += getPathPrefix();
                File preBuiltFolder = new File(path);
                if (preBuiltFolder.exists())
                {
                    File[] OSList = null;
                    //OS folder
                    if (Platform.getOS().equals(Platform.OS_WIN32))
                    {
                        OSList = preBuiltFolder.listFiles(winFilter);
                    }
                    else if (Platform.getOS().equals(Platform.OS_LINUX))
                    {
                        OSList = preBuiltFolder.listFiles(linuxFilter);
                    }
                    else if (Platform.getOS().equals(Platform.OS_MACOSX))
                    {
                        OSList = preBuiltFolder.listFiles(macFilter);
                    }
                    //check gcc executable
                    if ((OSList != null) && (OSList.length > 0))
                    {
                        isValid = gccExists(OSList[0]);
                    }
                }
                else
                {
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    /**
     * looks forward to gcc executable
     * 
     * @param osFolder
     * @return
     */
    private static boolean gccExists(File osFolder)
    {
        boolean returnValue = false;
        //arm folder
        File[] armList = osFolder.listFiles(armFilter);
        if (armList.length > 0)
        {
            File armFolder = armList[0];
            //bin folder
            File[] binList = armFolder.listFiles(binFilter);
            if (binList.length > 0)
            {
                File binFolder = binList[0];
                //gcc executable
                File[] gccList = binFolder.listFiles(gccFilter);
                if (gccList.length > 0)
                {
                    returnValue = true;
                }
            }
        }
        return returnValue;
    }

    @Override
    /**
     * called when apply is pressed
     */
    protected void performApply()
    {
        getPreferenceStore().setValue(UIPlugin.NDK_LOCATION_PREFERENCE,
                directoryEditorNDK.getStringValue());
        // Set NDK location in the Sequoyah framework
        NDKUtils.setNDKLocation(directoryEditorNDK.getStringValue());

    }

    @Override
    /**
     * default value is an empty string
     */
    protected void performDefaults()
    {
        getPreferenceStore().setToDefault(UIPlugin.NDK_LOCATION_PREFERENCE);
        directoryEditorNDK.setStringValue(getPreferenceStore().getString(
                UIPlugin.NDK_LOCATION_PREFERENCE));
        // Set NDK location in the Sequoyah framework
        NDKUtils.setNDKLocation(directoryEditorNDK.getStringValue());

    }

    @Override
    /**
     * called when ok button is pressed
     */
    public boolean performOk()
    {
        boolean canReturn = true;

        //cannot finish with invalid values
        if (!isNDKPathValid)
        {
            canReturn = false;
        }
        //set directories values 
        else
        {
            getPreferenceStore().setValue(UIPlugin.NDK_LOCATION_PREFERENCE,
                    directoryEditorNDK.getStringValue());
            // Set NDK location in the Sequoyah framework
            NDKUtils.setNDKLocation(directoryEditorNDK.getStringValue());
            canReturn = super.performOk();

        }

        return canReturn;
    }

    public void init(IWorkbench workbench)
    {
    }

    class NDKListener implements ModifyListener
    {
        //validate path as user types or select a folder
        public void modifyText(ModifyEvent e)
        {
            String path = directoryEditorNDK.getStringValue().trim();
            isNDKPathValid = validateNDKDirectory(path);
            //error message handling
            if (!isNDKPathValid)
            {
                preferencePage.setErrorMessage(UI_PREFERENCES_NDK_INVALID_PATH_ERROR_MSG);
                preferencePage.setValid(false);
            }
            else
            {
                preferencePage.setErrorMessage(null);
                preferencePage.setValid(true);
            }
            preferencePage.updateApplyButton();
        }
    }

}
