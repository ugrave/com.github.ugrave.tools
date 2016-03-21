package com.github.ugrave.tools.rules;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.DeleteResourcesOperation;
import org.junit.rules.ExternalResource;

public class EclipseProjectRule extends ExternalResource {

	private IWorkspace workspace;

	private EclipseProjectRule() {
	}

	@Override
	protected void before() throws Throwable {
		workspace = ResourcesPlugin.getWorkspace();
	}

	public IProject createProject(String name, FileDescription... files) {
		IProject project = createProject(name);
		for (FileDescription fd : files) {
			IFile file = project.getFile(fd.name);
			try(InputStream inputStream = fd.getInputStream()){
				file.create(inputStream, IResource.FORCE, null);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return project;
	}

	public IProject createProject(String name) {
		IProjectDescription description = workspace.newProjectDescription(name);

		final CreateProjectOperation op = new CreateProjectOperation(description, "");
		try {
			workspace.run(new IWorkspaceRunnable() {

				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						op.execute(monitor, null);
					} catch (ExecutionException e) {
						throw new RuntimeException(e);
					}
				}
			}, new NullProgressMonitor());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

		return workspace.getRoot().getProject(name);
	}

	@Override
	protected void after() {
		IProject[] projects = workspace.getRoot().getProjects();
		if(projects.length < 1) {
			return;
		}
		final DeleteResourcesOperation op = new DeleteResourcesOperation(projects, "", true);
		try {
			workspace.run(new IWorkspaceRunnable() {

				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						op.execute(monitor, null);
					} catch (ExecutionException e) {
						throw new RuntimeException(e);
					}
				}
			}, new NullProgressMonitor());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

		workspace= null;
	}

	public static EclipseProjectRule create() {
		return new EclipseProjectRule();
	}


	public static class FileDescription {

		private String name;
		private String content;

		private FileDescription (String name) {
			this.name = name;
		}

		private InputStream getInputStream() {
			if(content == null) {
				return new ByteArrayInputStream("".getBytes());
			}
			return new ByteArrayInputStream(content.getBytes());

		}

		public FileDescription content(String content) {
			this.content = content;
			return this;
		}

		public static FileDescription newFile(String name) {
			return new FileDescription(name);
		}
	}
}
