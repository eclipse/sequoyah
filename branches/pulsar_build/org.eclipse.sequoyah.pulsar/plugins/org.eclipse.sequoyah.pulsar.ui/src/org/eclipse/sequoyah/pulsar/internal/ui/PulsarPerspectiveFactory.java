/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gustavo de Paula (Motorola) - Initial version
 */
package org.eclipse.sequoyah.pulsar.internal.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.sequoyah.pulsar.Activator;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

/**
 * Pulsar perspective factory 
 */
public class PulsarPerspectiveFactory implements IPerspectiveFactory {

	/**
	 * Perspective page layout
	 */
    private IPageLayout layout;
	
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout(IPageLayout layout) {
        this.layout = layout;
        addViews();
        addActionSets();
        addNewWizardShortcuts();
        addPerspectiveShortcuts();
        addViewShortcuts();
    }

    private void addPerspectiveShortcuts() {
        layout.addPerspectiveShortcut(JavaUI.ID_PERSPECTIVE);
        layout.addPerspectiveShortcut(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);
    }

    private void addViews() {
        // Add Package Explorer, Navigator and Hierarchy views to left
        IFolderLayout topLeft = layout.createFolder("topLeft", //$NON-NLS-1$
                IPageLayout.LEFT, 0.25f, layout.getEditorArea());
        topLeft.addView(JavaUI.ID_PACKAGES);
        topLeft.addView(JavaUI.ID_TYPE_HIERARCHY);
        // Add Problems, Javadoc and Tasks views to bottom
        IFolderLayout bottom = layout.createFolder("bottomRight", //$NON-NLS-1$
                IPageLayout.BOTTOM, 0.65f, layout.getEditorArea());
        bottom.addView(Activator.QUICKINSTALL_VIEW_ID);
        // Add Outline view to right
        layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.75f, layout
                .getEditorArea());
    }

    private void addActionSets() {
        layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
        layout.addActionSet(JavaUI.ID_ACTION_SET);
        layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
        layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
    }

    private void addNewWizardShortcuts() {
        layout
                .addNewWizardShortcut("org.eclipse.sequoyah.ui.wizards.NewJ2MEProjectWizard"); //$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.sequoyah.ui.wizards.NewMidletWizard"); //$NON-NLS-1$        
        layout
        		.addNewWizardShortcut("org.eclipse.sequoyah.ui.wizards.NewMidletFromTemplate"); //$NON-NLS-1$     

        layout
                .addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard"); //$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewJavaWorkingSetWizard"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.sequoyah.j2meunit.wizards.NewJ2METestCaseWizard"); //$NON-NLS-1$
        layout
                .addNewWizardShortcut("org.eclipse.sequoyah.j2meunit.internal.ui.wizards.NewJ2METestSuiteCreationWizard"); //$NON-NLS-1$

    }

    private void addViewShortcuts() {
    	
    	layout.addShowViewShortcut(Activator.QUICKINSTALL_VIEW_ID);
        // views - error log
        layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView"); //$NON-NLS-1$

        // views - java
        layout.addShowViewShortcut(JavaUI.ID_PACKAGES);
        layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);
        layout.addShowViewShortcut(JavaUI.ID_SOURCE_VIEW);
        layout.addShowViewShortcut(JavaUI.ID_JAVADOC_VIEW);

        // views - debugging
        layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

        // views - standard workbench
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
    }
}
