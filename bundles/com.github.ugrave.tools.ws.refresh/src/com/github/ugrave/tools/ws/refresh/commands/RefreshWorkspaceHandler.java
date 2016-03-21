/*******************************************************************************
 * Copyright (c) 2016 Ulrich Grave.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.github.ugrave.tools.ws.refresh.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.handlers.HandlerUtil;

public class RefreshWorkspaceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		RefreshAction refresh = new RefreshAction(activeWorkbenchWindow);
		refresh.refreshAll();
		return null;
	}

}
