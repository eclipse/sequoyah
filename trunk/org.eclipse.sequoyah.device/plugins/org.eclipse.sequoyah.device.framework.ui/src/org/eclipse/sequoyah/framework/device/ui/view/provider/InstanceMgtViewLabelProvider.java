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
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui.view.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.framework.device.ui.DeviceUIPlugin;
import org.eclipse.tml.framework.device.ui.view.model.ViewerDeviceNode;
import org.eclipse.tml.framework.device.ui.view.model.ViewerInstanceNode;
import org.eclipse.tml.framework.status.IStatus;
import org.eclipse.tml.framework.status.StatusRegistry;

public class InstanceMgtViewLabelProvider extends ColumnLabelProvider
{
    public static final int INSTANCE_COLUMN_NUMBER = 0;
    public static final int STATUS_COLUMN_NUMBER = 1;
    
    private static final String NO_INSTANCE_NAME = "<none>"; //$NON-NLS-1$
        
    /**
     * The column that is being updated by this label provider
     */
    protected int columnIndex;

    /**
     * The index of the column that is currently in the left edge of the viewer
     */
    protected int firstColumnIndex;
    

    private Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>(20);

    /**
     * Sets the index of the column that is currently at the left edge of the viewer
     * 
     * @param firstColumnIndex The index of the column that is currently at the left 
     *                         edge of the viewer
     */
    public void setFirstColumnIndex(int firstColumnIndex)
    {
        this.firstColumnIndex = firstColumnIndex;
    }
    
    /**
     * Retrieves the index of the column that is currently at the left edge of the viewer
     * 
     * @return firstColumnIndex The index of the column that is currently at the left 
     *                          edge of the viewer
     */
    public int getFirstColumnIndex()
    {
        return firstColumnIndex;
    }
    
    /**
     * Tests if the resource being retrieved (text or image) is for a cell at the first column
     * of the tree viewer
     * 
     * @return True if it is providing for the first column. False otherwise
     */
    protected boolean isProvidingForFirstColumn()
    {
        return firstColumnIndex == columnIndex;
    }

    public String getText(Object element)
    {
        String label = ""; //$NON-NLS-1$
        
        if (element instanceof ViewerInstanceNode)
        {
            ViewerInstanceNode node = (ViewerInstanceNode) element;
            label = getText(node, columnIndex);
        }
        else if (element instanceof ViewerDeviceNode)
        {
            if (isProvidingForFirstColumn())
            {
                ViewerDeviceNode node = (ViewerDeviceNode) element;
                label = node.getDeviceName();
            }
        }        
        
        return label;
    }
    
    public String getText(ViewerInstanceNode node, int columnIndex)
    {
        String label = ""; //$NON-NLS-1$
        if (node.containsInstance())
        {
        if (columnIndex == INSTANCE_COLUMN_NUMBER)
        {
            label = node.getInstanceName();
        }
        else if (columnIndex == STATUS_COLUMN_NUMBER)
        {
            String status = node.getInstanceStatus();
            
            label = StatusRegistry.getInstance().getStatus(status).getName();
        }
        }
        else
        {
        	if (isProvidingForFirstColumn())
        	{
        		label = NO_INSTANCE_NAME;
        	}
        }
        
        return label;
    }
    
    public Image getImage(Object element)
    {
        ImageDescriptor descriptor = null;
        Image image = null;
        
        if (element instanceof ViewerInstanceNode) {
            ViewerInstanceNode node = (ViewerInstanceNode) element;
            if (node.containsInstance())
            {
                if (columnIndex == INSTANCE_COLUMN_NUMBER)
                {
                    descriptor = DeviceUIPlugin.getDefault().getImageDescriptor(DeviceUIPlugin.ICON_DEVICE);
                }
                else if (columnIndex == STATUS_COLUMN_NUMBER)
                {
                    IStatus status = StatusRegistry.getInstance().getStatus(node.getInstanceStatus());
                    descriptor = status.getImage();
                }
            }
        } else if (element instanceof ViewerDeviceNode) {
            if (isProvidingForFirstColumn())
            {
                ViewerDeviceNode node = (ViewerDeviceNode) element;
                image = node.getDevice().getImage();
                return image;
            }
        }
        
        if (descriptor != null)
        {
            //obtain the cached image corresponding to the descriptor
            image = (Image)imageCache.get(descriptor);
            if (image == null) {
                image = descriptor.createImage();
                imageCache.put(descriptor, image);
            }
        }

        return image;
    }
    
    public void update(ViewerCell cell)
    {
        // The instance column index is set with the current cell column index, as the logic
        // contained in this class depends on this information. Then after the cell is 
        // updated according to the standard procedure, the column index field is reset so that
        // it does not interfere with subsequent updates. 
        columnIndex = cell.getColumnIndex();
        super.update(cell);
        columnIndex = firstColumnIndex;
    }
}
