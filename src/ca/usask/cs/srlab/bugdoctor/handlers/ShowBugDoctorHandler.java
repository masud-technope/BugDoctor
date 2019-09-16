package ca.usask.cs.srlab.bugdoctor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import ca.usask.cs.srlab.bugdoctor.Activator;

public class ShowBugDoctorHandler extends AbstractHandler {

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		try {
			
			String REBviewID="ca.usask.cs.srlab.rack.views.RACKExampleView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(REBviewID);
			
			//code for showing SurfClipse View
			String SEviewID="ca.usask.cs.srlab.rack.views.RACKDashboardView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SEviewID);
			
			String BDviewID = "ca.usask.cs.srlab.bugdoctor.views.BugDoctorDashboardView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(BDviewID);
		
			System.out.println("BugDoctor windows shown successfully");

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

}
