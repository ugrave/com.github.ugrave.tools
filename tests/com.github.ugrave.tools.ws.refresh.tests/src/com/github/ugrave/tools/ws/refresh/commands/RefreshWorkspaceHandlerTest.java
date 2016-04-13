/*******************************************************************************
 * Copyright (c) 2016 Ulrich Grave.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.github.ugrave.tools.ws.refresh.commands;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.ugrave.tools.rules.EclipseProjectRule;
import com.github.ugrave.tools.rules.EclipseProjectRule.FileDescription;

public class RefreshWorkspaceHandlerTest {

	private static final String FILE_NAME = "Test 2.txt";

	@Rule
	public EclipseProjectRule projects = EclipseProjectRule.create();

	private IProject project_1;
	private IProject project_2;

	private IHandlerService handlerService;

	@Before
	public void setup() {
		project_1 = projects.createProject("Test 1", FileDescription.newFile("Test.txt").content("Test 123"));
		project_2 = projects.createProject("Test 2");

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		handlerService = window.getService(IHandlerService.class);

	}

	@After
	public void shoutdown() {
		handlerService = null;
		project_1 = null;
		project_2 = null;
	}

	@Test
	public void test_new_file_exists_after_refreshAll() throws Exception {

		File newFile_1 = new File(project_1.getLocation().toFile(), FILE_NAME);
		Assert.assertTrue(newFile_1.createNewFile());
		File newFile_2 = new File(project_2.getLocation().toFile(), FILE_NAME);
		Assert.assertTrue(newFile_2.createNewFile());

		IFile ws_file_1 = project_1.getFile(FILE_NAME);
		IFile ws_file_2 = project_2.getFile(FILE_NAME);

		Assert.assertFalse(ws_file_1.exists());
		Assert.assertFalse(ws_file_2.exists());

		handlerService.executeCommand("com.github.ugrave.tools.ws.refresh.commands.RefreshWorkspaceHandler", null);

		Job refreshJob = findJob("refresh");
		if(refreshJob != null) {
			refreshJob.join(5000L, null);
		}

		Assert.assertTrue(ws_file_1.exists());
		Assert.assertTrue(ws_file_2.exists());
	}

	private Job findJob(String name) {
		for (Job job : Job.getJobManager().find(null)) {
			if(job.getName().equals(name)) {
				return job;
			}
		}
		return null;
	}

}
