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
		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		RefreshAction refresh = new RefreshAction(activeWorkbenchWindow);
		refresh.refreshAll();
		return null;
	}

}
