/********************************************************************************
 * Copyright (c) 2008 Motorola Inc. and Other. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Julia Martinez Perdigueiro (Eldorado Research Institute) 
 * [244805] - Improvements on Instance view  
 *
 * Contributors:
 * Julia Martinez Perdigueiro (Eldorado Research Institute) - [247085] - Instance manage view buttons are resizing after applying services filter  
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui.view;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.handler.ServiceHandlerAction;
import org.eclipse.tml.framework.device.ui.DeviceUIPlugin;

public class InstanceServicesComposite extends Composite {

	private static boolean showAllServices = true;
	private static int buttonsOrienation = SWT.HORIZONTAL;
	private IInstance instance = null;
	
	private static final String SERVICES_LABEL = "Services";
	private static final String SERVICES_FILTERED_LABEL = "Services (filtered)";
	private static final String NO_LABEL = "";
	private static final int DEFAULT_BUTTONS_WIDTH = 120;
	private static final int DEFAULT_BUTTONS_HEIGHT = 30;
	private static final int MAX_BUTTON_STRING_SIZE = DEFAULT_BUTTONS_WIDTH / 10;
	private static final int DEFAULT_BUTTON_IMAGE_SIZE = 16;
	
	private CLabel label;
	private ToolBar toolBar;

	private ViewForm viewForm;
	
	private class ServicesFilterAction extends Action
	{
		public ServicesFilterAction()
		{
			super("filter");
			setToolTipText("Filter services by availability");
			setChecked(!showAllServices);
			setImageDescriptor(DeviceUIPlugin.getDefault().getImageDescriptor(DeviceUIPlugin.ICON_SERVICE));
		}
		
		public void run() {
			showAllServices = !showAllServices;
			setChecked(!showAllServices);
			createServicesArea();
		}		
	}
	
	private class ServicesOrientationAction extends Action
	{
	    public ServicesOrientationAction()
	    {
	        super("orientation");
	        setToolTipText("Toggle vertical/horizontal orientation");
	        if (buttonsOrienation ==  SWT.HORIZONTAL)
            {
                setImageDescriptor(DeviceUIPlugin.getDefault().getImageDescriptor(DeviceUIPlugin.ICON_HORIZONTAL));
            }
            else
            {
                setImageDescriptor(DeviceUIPlugin.getDefault().getImageDescriptor(DeviceUIPlugin.ICON_VERTICAL));
            }
	    }
	    
	    public void run()
	    {
	        if (buttonsOrienation ==  SWT.HORIZONTAL)
	        {
	            buttonsOrienation = SWT.VERTICAL;
	            setImageDescriptor(DeviceUIPlugin.getDefault().getImageDescriptor(DeviceUIPlugin.ICON_VERTICAL));
	        }
	        else
	        {
	            buttonsOrienation = SWT.HORIZONTAL;
	            setImageDescriptor(DeviceUIPlugin.getDefault().getImageDescriptor(DeviceUIPlugin.ICON_HORIZONTAL));
	        }
	        createServicesArea();
	    }
	}

	public InstanceServicesComposite(Composite parent) {
		super(parent, SWT.NONE);

		createContents();
	}

	public void setSelectedInstance(IInstance instance)
	{
		this.instance = instance;
		
		createServicesArea();
	}	

	private void createContents()
	{
		setLayout(new FillLayout());
		viewForm = new ViewForm(this, SWT.NONE);

		viewForm.setLayout(new GridLayout());

		// populate top part of area
		createToolbarArea();
		
		// populate the services area
		createServicesArea();        
	}
	
	private void createToolbarArea()
	{
		label= new CLabel(viewForm, SWT.NONE);
		label.setText("Services");
		viewForm.setTopLeft(label);
		toolBar= new ToolBar(viewForm, SWT.FLAT | SWT.WRAP);
		viewForm.setTopCenter(toolBar);
		ToolBarManager toolBarMgr = new ToolBarManager(toolBar);
		toolBarMgr.add(new ServicesFilterAction());
		toolBarMgr.add(new Separator());
		toolBarMgr.add(new ServicesOrientationAction());
		toolBarMgr.update(true);
	}

	private void createServicesArea()
	{
		ScrolledComposite scrollComposite = new ScrolledComposite(viewForm, SWT.V_SCROLL | SWT.H_SCROLL);
		
		Composite servicesComposite = new Composite(scrollComposite,SWT.NONE);
		
		servicesComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.type = buttonsOrienation;
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 5;
		
		servicesComposite.setLayout(rowLayout);

		if (instance != null)
		{
			IDevice device = DeviceManager.getInstance().getDevice(instance);
			List<IService> services = device.getServices();
			for (IService service:services){
				if (service.isVisible()) {
					boolean isServiceEnabled = (service.getStatusTransitions(instance.getStatus()) != null);
					
					if ((showAllServices) || (isServiceEnabled))
					{
						Button serviceButton = new Button(servicesComposite, SWT.PUSH);
						serviceButton.setEnabled(isServiceEnabled);
						serviceButton.addListener(SWT.Selection,  new ServiceHandlerAction(instance,service.getHandler()));

						RowData data = new RowData();
						data.width = DEFAULT_BUTTONS_WIDTH;
						data.height = DEFAULT_BUTTONS_HEIGHT;
						serviceButton.setLayoutData(data);
						
						// set button text and tooltip
						String serviceName = service.getName();
						serviceButton.setToolTipText(serviceName);
						if (serviceName.length() > MAX_BUTTON_STRING_SIZE)
						{
							// if text will not fit, shorten it
							serviceName = serviceName.substring(0, MAX_BUTTON_STRING_SIZE).concat(Dialog.ELLIPSIS);
						}
						serviceButton.setText(serviceName);
						
						// set the button image to 16x16 image
						ImageData serviceImageData = service.getImage().getImageData().scaledTo(DEFAULT_BUTTON_IMAGE_SIZE, DEFAULT_BUTTON_IMAGE_SIZE);
						Image serviceImage = new Image(serviceButton.getDisplay(), serviceImageData);
						serviceButton.setImage(serviceImage);

					}
				}
			}
		}
		
		scrollComposite.setContent(servicesComposite);
		scrollComposite.setExpandVertical(true);
	    scrollComposite.setExpandHorizontal(true);
	    Point compositeSize = servicesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    scrollComposite.setMinSize(DEFAULT_BUTTONS_WIDTH + 10, compositeSize.y);
		viewForm.setContent(scrollComposite);
		
		if (instance == null)
		{
			label.setText(NO_LABEL);
			toolBar.setVisible(false);
		}
		else
		{
			if (showAllServices)
			{
				label.setText(SERVICES_LABEL);
			}
			else
			{
				label.setText(SERVICES_FILTERED_LABEL);
			}

			toolBar.setVisible(true);
		}
	}

}
