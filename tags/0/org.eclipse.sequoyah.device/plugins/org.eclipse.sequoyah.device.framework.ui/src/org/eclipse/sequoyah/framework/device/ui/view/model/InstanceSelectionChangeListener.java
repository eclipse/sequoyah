/********************************************************************************
 * Copyright (c) 2009 Motorola Inc. and Other. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Julia Martinez Perdigueiro (Eldorado Research Institute) 
 * [244805] - Improvements on Instance view  
 *
 * Contributors:
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [280981] - Add suport for selecting instances programatically 
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui.view.model;

import org.eclipse.tml.framework.device.ui.view.model.InstanceSelectionChangeEvent;

public interface InstanceSelectionChangeListener{

	
	public void instanceSelectionChanged(InstanceSelectionChangeEvent event);


}