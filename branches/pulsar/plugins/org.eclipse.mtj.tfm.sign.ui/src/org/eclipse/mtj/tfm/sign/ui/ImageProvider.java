/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Diego Sandin (Motorola) - Initial Version
 */
package org.eclipse.mtj.tfm.sign.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class ImageProvider {

    public final static String ICONS_PATH           = "icons/full/"; //$NON-NLS-1$

    /* The standard icon folders */
    public static final String PATH_LCL             = ICONS_PATH + "elcl16/"; //$NON-NLS-1$
    public static final String PATH_LCL_DISABLED    = ICONS_PATH + "dlcl16/"; //$NON-NLS-1$
    public static final String PATH_OBJ             = ICONS_PATH + "obj16/"; //$NON-NLS-1$
    public static final String PATH_OVR             = ICONS_PATH + "ovr16/"; //$NON-NLS-1$
    public static final String PATH_TOOL            = ICONS_PATH + "etool16/"; //$NON-NLS-1$
    public static final String PATH_VIEW            = ICONS_PATH + "view16/"; //$NON-NLS-1$
    public static final String PATH_WIZBAN          = ICONS_PATH + "wizban/"; //$NON-NLS-1$

    public static final ImageDescriptor LOCKER = create(PATH_OBJ, "locker.png");

    public static final ImageDescriptor OPEN_SECURITY_FW_VIEW = create(
            PATH_WIZBAN, "open_security_fw_view.png");

    public static final ImageDescriptor SIGN_APPLICATION_PACKAGE = create(
            PATH_TOOL, "sign_application_package.png"); //$NON-NLS-1$

    public static final ImageDescriptor UNSIGN_APPLICATION_PACKAGE = create(
            PATH_TOOL, "unsign_application_package.png"); //$NON-NLS-1$

    
    private static ImageRegistry PLUGIN_REGISTRY;

    /**
     * Returns the image associated with the given key in this provider, or
     * <code>null</code> if none.
     * 
     * @param key the key
     * @return the image, or <code>null</code> if none.
     */
    public static Image get(String key) {
        if (PLUGIN_REGISTRY == null) {
            initialize();
        }
        return PLUGIN_REGISTRY.get(key);
    }

    /**
     * Adds an image to this Provider. This method fails if there is already an
     * descriptor for the given key.
     * 
     * @param key the key
     * @param desc non-<code>null</code> image descriptior
     * @return
     */
    public static Image manage(String key, ImageDescriptor desc) {
        Image image = desc.createImage();
        PLUGIN_REGISTRY.put(key, image);
        return image;
    }

    /**
     * Creates and returns a new image descriptor from a image on a given path.
     * 
     * @param path image path
     * @param name image name
     * @return a new image descriptor for the given
     */
    private static ImageDescriptor create(String path, String name) {
        return ImageDescriptor.createFromURL(makeImageURL(path, name));
    }

    /**
     * Initializes the provider registry.
     */
    private static final void initialize() {
        PLUGIN_REGISTRY = new ImageRegistry();
    }

    /**
     * Creates and returns the URL to an image on a given path.
     * 
     * @param _path the image path
     * @param _name the image name
     * @return the url to the given image
     */
    private static URL makeImageURL(String _path, String _name) {
        String path = "$nl$/" + _path + _name; //$NON-NLS-1$
        return FileLocator.find(SignUI.getDefault().getBundle(),
                new Path(path), null);
    }

}
