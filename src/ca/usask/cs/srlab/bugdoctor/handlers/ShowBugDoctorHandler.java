package ca.usask.cs.srlab.bugdoctor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

public class ShowBugDoctorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		try {
			//code for showing SurfClipse View
			String SEviewID="ca.usask.cs.srlab.bugdoctor.views.BugDoctorDashboardView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SEviewID);
			String REBviewID="ca.usask.cs.srlab.bugdoctor.views.BugDoctorExampleView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(REBviewID);
			
			System.out.println("BugDoctor windows shown successfully");
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

}
