package ca.usask.cs.srlab.bugdoctor.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.usask.cs.srlab.bugdoctor.Activator;

public class ShowBugDoctorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		try {
			// code for showing SurfClipse View
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (window != null) {
				IStructuredSelection selection = (IStructuredSelection) window
						.getActivePage().getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof IAdaptable) {
					IProject project = (IProject) ((IAdaptable) firstElement)
							.getAdapter(IProject.class);
					// IPath path = project.getFullPath();
					// System.out.println(project.getName());
					Activator.SELECTED_REPOSITORY = project.getName();
					System.out.println(Activator.SELECTED_REPOSITORY);
				}
			}

			String REBviewID = "ca.usask.cs.srlab.bugdoctor.views.BugDoctorExampleView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(REBviewID);
			
			String SEviewID = "ca.usask.cs.srlab.bugdoctor.views.BugDoctorDashboardView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(SEviewID);
			

			System.out.println("BugDoctor windows shown successfully");

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

}
