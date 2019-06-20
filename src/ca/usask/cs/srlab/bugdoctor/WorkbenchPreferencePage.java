package ca.usask.cs.srlab.bugdoctor;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class WorkbenchPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
		
		
	}

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout prefGridLayout = new GridLayout(3, false);
		composite.setLayout(prefGridLayout);

		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gridData.heightHint = 25;
		gridData.widthHint = 260;

		GridData labelGridData = new GridData(SWT.LEFT, SWT.CENTER, false,
				false);
		labelGridData.heightHint = 25;
		labelGridData.widthHint = 120;

		GridData buttonGridData = new GridData(SWT.LEFT, SWT.CENTER, false,
				false);
		buttonGridData.heightHint = 25;
		buttonGridData.widthHint = 120;

		/****** HOME Directory *****/

		Label homeDirLabel = new Label(composite, SWT.BOLD);
		homeDirLabel.setLayoutData(labelGridData);
		homeDirLabel.setText("Plug-in Home:");

		Text homeDirText = new Text(composite, SWT.NONE);
		homeDirText.setLayoutData(gridData);
		homeDirText.setText("Enter the home directory");

		Button changeHomeButton = new Button(composite, SWT.PUSH);
		changeHomeButton.setLayoutData(buttonGridData);
		changeHomeButton.setText("Change");
		changeHomeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				try {
					DirectoryDialog dirDialog = new DirectoryDialog(composite
							.getShell());
					String homeDir = dirDialog.open();
					homeDirText.setText(homeDir);
					IEclipsePreferences store = InstanceScope.INSTANCE
							.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("HOME_DIR", homeDirText.getText());
				} catch (Exception exc) {
					System.err.println("HOME_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		/*** STOPWORD Directory **/

		Label stopDirLabel = new Label(composite, SWT.BOLD);
		stopDirLabel.setLayoutData(labelGridData);
		stopDirLabel.setText("Stopword Directory:");

		Text stopDirText = new Text(composite, SWT.NONE);
		stopDirText.setLayoutData(gridData);
		stopDirText.setText("Enter the stopword directory");

		Button stopHomeButton = new Button(composite, SWT.PUSH);
		stopHomeButton.setLayoutData(buttonGridData);
		stopHomeButton.setText("Change");

		stopHomeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				try {
					DirectoryDialog dirDialog = new DirectoryDialog(composite
							.getShell());
					String stopHomeDir = dirDialog.open();
					stopDirText.setText(stopHomeDir);
					IEclipsePreferences store = InstanceScope.INSTANCE
							.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("STOPWORD_DIR", stopDirText.getText());
				} catch (Exception exc) {
					System.err.println("STOPWORD_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		/**** SAMURAI DIRECTORY ***/
		Label samuraiDirLabel = new Label(composite, SWT.BOLD);
		samuraiDirLabel.setLayoutData(labelGridData);
		samuraiDirLabel.setText("Samurai Directory:");

		Text samuraiDirText = new Text(composite, SWT.NONE);
		samuraiDirText.setLayoutData(gridData);
		samuraiDirText.setText("Enter the Samurai directory");

		Button samuraiHomeButton = new Button(composite, SWT.PUSH);
		samuraiHomeButton.setLayoutData(buttonGridData);
		samuraiHomeButton.setText("Change");

		samuraiHomeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				try {
					DirectoryDialog dirDialog = new DirectoryDialog(composite
							.getShell());
					String samuraiHomeDir = dirDialog.open();
					samuraiDirText.setText(samuraiHomeDir);
					IEclipsePreferences store = InstanceScope.INSTANCE
							.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("SAMURAI_DIR", samuraiDirText.getText());
				} catch (Exception exc) {
					System.err.println("SAMURAI_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		/**** MAX ENT MODEL ***/
		Label maxEntModelDirLabel = new Label(composite, SWT.BOLD);
		maxEntModelDirLabel.setLayoutData(labelGridData);
		maxEntModelDirLabel.setText("POS Model Directory:");

		Text maxEntModelDirText = new Text(composite, SWT.NONE);
		maxEntModelDirText.setLayoutData(gridData);
		maxEntModelDirText.setText("Enter the POS model directory");

		Button maxEntModelHomeButton = new Button(composite, SWT.PUSH);
		maxEntModelHomeButton.setLayoutData(buttonGridData);
		maxEntModelHomeButton.setText("Change");

		maxEntModelHomeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				try {
					DirectoryDialog dirDialog = new DirectoryDialog(composite
							.getShell());
					String maxEntModelHomeDir = dirDialog.open();
					maxEntModelDirText.setText(maxEntModelHomeDir);
					IEclipsePreferences store = InstanceScope.INSTANCE
							.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("MAX_ENT_MODEL_DIR", maxEntModelDirText.getText());
				} catch (Exception exc) {
					System.err.println("MODEL_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		/***** Selected Repository *****/
		Label repoLabel = new Label(composite, SWT.BOLD);
		repoLabel.setLayoutData(labelGridData);
		repoLabel.setText("Selected Project:");

		String[] availableRepos = new String[] { "ecf", "eclipse.jdt.core",
				"eclipse.jdt.debug", "eclipse.jdt.ui", "eclipse.pde.ui",
				"log4j", "sling", "tomcat70" };
		Combo repos = new Combo(composite, SWT.DROP_DOWN);
		repos.setItems(availableRepos);
		repos.setLayoutData(gridData);

		Button repoButton = new Button(composite, SWT.PUSH);
		repoButton.setLayoutData(buttonGridData);
		repoButton.setText("Change");

		repoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				try {
					String selected_repository = availableRepos[repos
							.getSelectionIndex()];

					IEclipsePreferences store = InstanceScope.INSTANCE
							.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("SELECTED_REPOSITORY", selected_repository);
				} catch (Exception exc) {
					System.err.println("No repository selected!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		try {
			String SEviewID = "ca.usask.cs.srlab.bugdoctor.views.BugDoctorDashboardView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(SEviewID);
		} catch (Exception exc) {
			// handle the exception
		}

		return new Composite(parent, SWT.NONE);
	}

}
