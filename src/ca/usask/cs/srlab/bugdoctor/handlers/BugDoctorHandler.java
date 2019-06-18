package ca.usask.cs.srlab.bugdoctor.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


public class BugDoctorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		//System.out.println("I am clicked");
		//perform the search
		try{
			
			IWorkbenchPage page =(IWorkbenchPage)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage();
			ISelection selection=page.getSelection();
			if(selection instanceof ITextSelection){
				final String searchQuery=((ITextSelection) selection).getText();
				//String searchQuery = input.getText().trim();
				if (!searchQuery.isEmpty()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		}catch(Exception exc){
			//handle the exception
		}
		return null;
	}
}
