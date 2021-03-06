/********************************************************************************
 * Copyright (c) 2008 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Otavio Ferranti (Motorola)
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.sequoyah.device.linuxtools.tools.cpuload;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.sequoyah.device.linuxtools.LinuxToolsPlugin;
import org.eclipse.sequoyah.device.linuxtools.network.IConstants.EventCode;
import org.eclipse.sequoyah.device.linuxtools.network.IConstants.OperationCode;
import org.eclipse.sequoyah.device.linuxtools.tools.IListener;
import org.eclipse.sequoyah.device.linuxtools.tools.INotifier;
import org.eclipse.sequoyah.device.linuxtools.tools.ITool;
import org.eclipse.sequoyah.device.linuxtools.ui.DialogLogin;
import org.eclipse.sequoyah.device.linuxtools.ui.IToolViewPart;
import org.eclipse.sequoyah.device.linuxtools.ui.ViewActionConnect;
import org.eclipse.sequoyah.device.linuxtools.ui.ViewActionDisconnect;
import org.eclipse.sequoyah.device.linuxtools.ui.ViewActionPause;
import org.eclipse.sequoyah.device.linuxtools.ui.ViewActionRun;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Otavio Ferranti
 */
public class CpuLoadView extends ViewPart implements IToolViewPart, IListener {

	private class CpuSorter extends ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			int result = 0;
			try {
				int a = new Integer(((String[]) e1)[0]).intValue();
				int b = new Integer(((String[]) e2)[0]).intValue();
				if (a > b) {
					result = 1;
				} else if (a < b) {
					result = -1;
				};
			}
			catch (NumberFormatException nfe) {
				//TODO: Nothing ?
			}
			return result;
		}
	}
	final private String COL_LABEL_CPU = Messages.CpuLoadView_Col_Label_Cpu;
	final private String COL_LABEL_USER_MODE = Messages.CpuLoadView_Col_Label_User_Mode;
	final private String COL_LABEL_NICE = Messages.CpuLoadView_Col_Label_Nice;
	final private String COL_LABEL_SYSTEM = Messages.CpuLoadView_Col_Label_System;
	final private String COL_LABEL_IDLE = Messages.CpuLoadView_Col_Label_Idle;
	final private String COL_LABEL_WAIT = Messages.CpuLoadView_Col_Label_Wait;
	final private String COL_LABEL_HIRQ = Messages.CpuLoadView_Col_label_HIrq;
	
	final private String COL_LABEL_SIRQ = Messages.CpuLoadView_Col_Label_SIrq;

	private ITool tool = null;
	private TableViewer viewer;
	
	private Action runAction;
	private Action pauseAction;
	private Action optionsAction;
	private Action disconnectAction;
	private Action connectAction;
	
	private boolean receivedData = false;

	private IPartListener partActivationListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
		}
		
		public void partBroughtToTop(IWorkbenchPart part) {
		}
			 
		public void partClosed(IWorkbenchPart part) {
			if (CpuLoadView.this.getSite().getPart() == part) {
				ITool tool = CpuLoadView.this.getTool();
				if (null != tool) {
					tool.disconnect();
				}
			}
		}
		
		public void partDeactivated(IWorkbenchPart part) {
		}
			 
		public void partOpened(IWorkbenchPart part) {
		}
	};
	
	/**
	 * The constructor.
	 */
	public CpuLoadView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.FULL_SELECTION |
										 SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new CpuLoadViewContentProvider());
		viewer.setLabelProvider(new CpuLoadViewLabelProvider());
		viewer.setSorter(new CpuSorter());

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		
		new TableColumn(table, SWT.LEFT).setText(COL_LABEL_CPU);
		new TableColumn(table, SWT.LEFT).setText(COL_LABEL_USER_MODE);
		new TableColumn(table, SWT.LEFT).setText(COL_LABEL_NICE);
		new TableColumn(table, SWT.LEFT).setText(COL_LABEL_SYSTEM);
		new TableColumn(table, SWT.LEFT).setText(COL_LABEL_IDLE);
		new TableColumn(table, SWT.LEFT).setText(COL_LABEL_WAIT);
		new TableColumn(table, SWT.LEFT).setText(COL_LABEL_HIRQ);
		new TableColumn(table, SWT.LEFT).setText(COL_LABEL_SIRQ);
		
		refresh();
		resize();
		
		makeActions();
		// hookDoubleClickAction();
		addToToolBar();
		
		getViewSite()
			.getWorkbenchWindow()
			.getPartService()
			.addPartListener(partActivationListener);
		
		setConnectEnabled(true);
		setRunPauseEnabled(false, false);
	}

	private void addToToolBar() {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBarMmanager = actionBars.getToolBarManager();
		toolBarMmanager.add(runAction);
		toolBarMmanager.add(pauseAction);
		toolBarMmanager.add(optionsAction);
		toolBarMmanager.add(disconnectAction);
		toolBarMmanager.add(connectAction);
	}
	
	private void makeActions() {
		
		runAction = new Action() {
			public void run() {
				IViewActionDelegate delegate = new ViewActionRun();
		        delegate.init(CpuLoadView.this);
	            delegate.run(this);
	            setRunPauseEnabled(false, true);
			}
		};
		runAction.setToolTipText(Messages.CpuLoadView_Action_Run);
		runAction.setImageDescriptor(
				LinuxToolsPlugin.getDefault().getImageDescriptor(LinuxToolsPlugin.ICON_RUN));
		
		pauseAction = new Action() {
			public void run() {
				IViewActionDelegate delegate = new ViewActionPause();
		        delegate.init(CpuLoadView.this);
	            delegate.run(this);
	            setRunPauseEnabled(true, false);
			}
		};
		pauseAction.setToolTipText(Messages.CpuLoadView_Action_Pause);
		pauseAction.setImageDescriptor(
				LinuxToolsPlugin.getDefault().getImageDescriptor(LinuxToolsPlugin.ICON_PAUSE));
		
		optionsAction = new Action() {
			public void run() {
				IViewActionDelegate delegate = new ViewActionOptions();
		        delegate.init(CpuLoadView.this);
	            delegate.run(this);
			}
		};
		optionsAction.setToolTipText(Messages.CpuLoadView_Action_Options);
		optionsAction.setImageDescriptor(
				LinuxToolsPlugin.getDefault().getImageDescriptor(LinuxToolsPlugin.ICON_OPTIONS));

		disconnectAction = new Action() {
			public void run() {
				IViewActionDelegate delegate = new ViewActionDisconnect();
		        delegate.init(CpuLoadView.this);
	            delegate.run(this);
			}
		};
		disconnectAction.setToolTipText(Messages.CpuLoadView_Action_Disconnect);
		disconnectAction.setImageDescriptor(
				LinuxToolsPlugin.getDefault().getImageDescriptor(LinuxToolsPlugin.ICON_DISCONNECT));
		
		connectAction = new Action() {
			public void run() {
				IViewActionDelegate delegate = new ViewActionConnect();
		        delegate.init(CpuLoadView.this);
	            delegate.run(this);
			}
		};
		connectAction.setToolTipText(Messages.CpuLoadView_Action_Connect);
		connectAction.setImageDescriptor(
				LinuxToolsPlugin.getDefault().getImageDescriptor(LinuxToolsPlugin.ICON_CONNECT));
	}
	
	private void setConnectEnabled(boolean bool) {
		connectAction.setEnabled(bool);
		disconnectAction.setEnabled(!bool);
	}

	private void setRunPauseEnabled(boolean runBool, boolean pauseBool) {
		runAction.setEnabled(runBool);
		pauseAction.setEnabled(pauseBool);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.sequoyah.device.linuxtools.ui.IToolView#getTool()
	 */
	public ITool getTool() {
		if(null == tool) {
			tool = new CpuLoadTool();
			tool.addListener(this);
		}
		return tool;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.sequoyah.device.linuxtools.network.IListener#notify(org.eclipse.sequoyah.device.linuxtools.network.INotifier, org.eclipse.sequoyah.device.linuxtools.network.IConstants.EventCode, java.lang.Object)
	 */
	public void notify(INotifier notifier, EventCode event, Object result) {
		if (notifier == this.tool) {
			final Object finalResult = result;
			final EventCode finalEvent = event;
			final ViewPart finalView = this;
			final ITool finalTool = this.tool;
			
			this.getViewSite().getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					switch(finalEvent) {
						case EVT_TOOL_REFRESH_VIEW:
							viewer.setInput(finalResult);
		        			refresh();
		        			if (!receivedData) {
		        				resize();
		        				receivedData = true;
		        			}
						break;
						case EVT_TOOL_CONNECT_FINISHED:
						case EVT_TOOL_LOGIN_FINISHED:
							switch ((OperationCode)finalResult) {
								case SUCCESS: 
									setConnectEnabled(false);
									setRunPauseEnabled(false, true);									
								break;
								case LOGIN_REQUIRED: {
									final DialogLogin dialog = new DialogLogin(
											finalView.getViewSite().getShell(),
											finalTool, false);
									dialog.open();
								}
								break;
								case LOGIN_FAILED: {
									final DialogLogin dialog = new DialogLogin(
											finalView.getViewSite().getShell(),
											finalTool, true);
									dialog.open();
								}
								break;
							}
						break;
						case EVT_TOOL_DISCONNECT_FINISHED:
							setConnectEnabled(true);
							setRunPauseEnabled(false, false);
						break;
					}
				}
			});
		}
	}
	
	/**
	 * 
	 */
	public void refresh() {
		viewer.refresh();
	}
	
	/**
	 * 
	 */
	public void resize() {
		Table table = viewer.getTable();
	    for (int i = 0, n = table.getColumnCount(); i < n; i++) {
	    	table.getColumn(i).pack();
	    }
	}

	/**
	 * @param data
	 */
	public void setData (Object data) {
		viewer.setInput(data);
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}