package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Carlos Alberto Souto Junior
 *
 */
public class MessageUtils

{

    public static void showErrorDialog(final String title, final String message)
    {
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                MessageDialog.openError(ww.getShell(), title, message);
            }
        });
    }

    public static void showInformationDialog(final String title, final String message)
    {
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                IWorkbench workbench = PlatformUI.getWorkbench();
                IWorkbenchWindow ww = workbench.getActiveWorkbenchWindow();
                Shell shell = ww.getShell();
                MessageDialog.openInformation(shell, title, message);
            }
        });
    }
}
