/**
 * Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Contributors:
 * 	David Dubrow
 *  David Marques (Motorola) - Renaming getImageDescriptor method.
 *  David Marques (Motorola) - Handling exceptions when loading icons.
 *  Euclides Neto (Motorola) - Adding SDK Category description support.
 */

package org.eclipse.sequoyah.pulsar.internal.ui.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.p2.ui.ProvUIImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKCategory;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKRepository;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class InstallersLabelProvider extends ColumnLabelProvider {

    private static final int IMAGE_DIMENSION = 16;
    private Device device;
    private Map<Object, Image> imageCache;

    public InstallersLabelProvider(Device device) {
        this.device = device;
    }

    public void dispose() {
        for (Image image : imageCache.values()) {
            image.dispose();
        }
        super.dispose();
    }

    @Override
    public Image getImage(Object element) {
        Object object = ((TreeNode) element).getValue();
        if (object instanceof ISDKRepository) {
            return getRepositoryImage((ISDKRepository) object);
        } else if (object instanceof ISDK) {
            return getProvUIImage(ProvUIImages.IMG_IU);
        } else if (object instanceof ISDKCategory) {
            return getProvUIImage(ProvUIImages.IMG_CATEGORY);
        } else if (object instanceof String) { // old category
            return getProvUIImage(ProvUIImages.IMG_CATEGORY);
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        Object object = ((TreeNode) element).getValue();
        if (object instanceof ISDK) {
            return ((ISDK) object).getName();
        } else if (object instanceof ISDKRepository) {
            return ((ISDKRepository) object).getName();
        } else if (object instanceof ISDKCategory) {
            return ((ISDKCategory) object).getName();
        } else if (object instanceof String) {
            return (String) object;
        }

        return null;
    }

    private void ensureImageCache() {
        if (imageCache == null)
            imageCache = new HashMap<Object, Image>();
    }

    private Image getProvUIImage(String key) {
        ensureImageCache();
        if (!imageCache.containsKey(key)) {
            ImageDescriptor imageDescriptor = ProvUIActivator.getDefault()
                    .getImageRegistry().getDescriptor(key);
            imageCache.put(key, imageDescriptor.createImage(device));
        }

        return imageCache.get(key);
    }

    private Image getRepositoryImage(ISDKRepository repository) {
        ensureImageCache();
        if (!imageCache.containsKey(repository)) {
            Image image = null;
            try {
                ImageDescriptor imageDescriptor = repository
                        .getIconImageDescriptor();
                ImageData imageData = imageDescriptor.getImageData();
                if (imageData != null) {
                    if (imageData.width == IMAGE_DIMENSION
                            && imageData.height == IMAGE_DIMENSION) {
                        image = imageDescriptor.createImage(device);
                    } else {
                        ImageData scaledImageData = imageData.scaledTo(
                                IMAGE_DIMENSION, IMAGE_DIMENSION);
                        image = new Image(device, scaledImageData);
                    }
                }
            } catch (Exception e) {
                // Do nothing... use default image icon...
            }
            if (image == null) {
                image = PlatformUI.getWorkbench().getSharedImages()
                        .getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER)
                        .createImage();
            }
            imageCache.put(repository, image);
        }
        return imageCache.get(repository);
    }
}
