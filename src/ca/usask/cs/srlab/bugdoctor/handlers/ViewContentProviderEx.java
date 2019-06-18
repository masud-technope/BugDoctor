package ca.usask.cs.srlab.bugdoctor.handlers;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import bugdoctor.core.CodeMethod;


public class ViewContentProviderEx implements IStructuredContentProvider {
	ArrayList<CodeMethod> collectedResults;

	public ViewContentProviderEx() {
		// TODO Auto-generated constructor stub
		this.collectedResults = new ArrayList<CodeMethod>();
	}

	public ViewContentProviderEx(ArrayList<CodeMethod> results) {
		this.collectedResults = results;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		CodeMethod[] results = new CodeMethod[this.collectedResults.size()];
		return this.collectedResults.toArray(results);
	}
}

