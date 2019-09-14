package ca.usask.cs.srlab.bugdoctor;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bugdoctor.core.StaticData;

public class WorkbenchPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	Logger logger = LoggerFactory.getLogger(WorkbenchPreferencePage.class);

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	protected void addStopWordDir(Composite composite, GridData gridData, GridData labelGridData,
			GridData buttonGridData) {
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
					DirectoryDialog dirDialog = new DirectoryDialog(composite.getShell());
					String stopHomeDir = dirDialog.open();
					stopDirText.setText(stopHomeDir);
					IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("STOPWORD_DIR", stopDirText.getText());
				} catch (Exception exc) {
					System.err.println("STOPWORD_DIR missing!");
					logger.error("STOPWORD_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

	}

	protected void addSamuraiDir(Composite composite, GridData gridData, GridData labelGridData,
			GridData buttonGridData) {
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
					DirectoryDialog dirDialog = new DirectoryDialog(composite.getShell());
					String samuraiHomeDir = dirDialog.open();
					samuraiDirText.setText(samuraiHomeDir);
					IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("SAMURAI_DIR", samuraiDirText.getText());
				} catch (Exception exc) {
					System.err.println("SAMURAI_DIR missing!");
					logger.error("SAMURAI_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

	}

	protected void addMaxEntDir(Composite composite, GridData gridData, GridData labelGridData,
			GridData buttonGridData) {
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
					DirectoryDialog dirDialog = new DirectoryDialog(composite.getShell());
					String maxEntModelHomeDir = dirDialog.open();
					maxEntModelDirText.setText(maxEntModelHomeDir);
					IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("MAX_ENT_MODEL_DIR", maxEntModelDirText.getText());
				} catch (Exception exc) {
					System.err.println("MODEL_DIR missing!");
					logger.error("MODEL_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
	}

	protected void addAvailableRepositories(Composite composite, GridData gridData, GridData labelGridData,
			GridData buttonGridData) {
		/***** Selected Repository *****/
		Label repoLabel = new Label(composite, SWT.BOLD);
		repoLabel.setLayoutData(labelGridData);
		repoLabel.setText("Selected Project:");

		String[] availableRepos = new String[] { "ecf", "eclipse.jdt.core", "eclipse.jdt.debug", "eclipse.jdt.ui",
				"eclipse.pde.ui", "log4j", "sling", "tomcat70" };
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
					String selected_repository = availableRepos[repos.getSelectionIndex()];
					IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("SELECTED_REPOSITORY", selected_repository);
				} catch (Exception exc) {
					System.err.println("No repository selected!");
					logger.error("No repository selected!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

	}

	protected void addGroundTruth(Composite composite, GridData gridData, GridData labelGridData,
			GridData buttonGridData) {
		/**** Ground Truth Directory ***/
		Label gtruthDirLabel = new Label(composite, SWT.BOLD);
		gtruthDirLabel.setLayoutData(labelGridData);
		gtruthDirLabel.setText("Ground Truth Directory:");

		Text gtruthDirText = new Text(composite, SWT.NONE);
		gtruthDirText.setLayoutData(gridData);
		gtruthDirText.setText("Enter the ground truth directory");

		Button gtruthHomeButton = new Button(composite, SWT.PUSH);
		gtruthHomeButton.setLayoutData(buttonGridData);
		gtruthHomeButton.setText("Change");

		gtruthHomeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				try {
					DirectoryDialog dirDialog = new DirectoryDialog(composite.getShell());
					String gtruthDir = dirDialog.open();
					gtruthDirText.setText(gtruthDir);
					IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("GROUND_TRUTH_DIR", gtruthDirText.getText());
				} catch (Exception exc) {
					System.err.println("GROUND_TRUTH_DIR missing!");
					logger.error("GROUND_TRUTH_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

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

		GridData labelGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		labelGridData.heightHint = 25;
		labelGridData.widthHint = 120;

		GridData buttonGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
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
					DirectoryDialog dirDialog = new DirectoryDialog(composite.getShell());
					String homeDir = dirDialog.open();
					homeDirText.setText(homeDir);
					IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
					store.put("HOME_DIR", homeDirText.getText());
					String HOME_DIR = homeDirText.getText();

					// also update the home directory of all projects
					StaticData.HOME_DIR = HOME_DIR;
					qd.config.StaticData.HOME_DIR = HOME_DIR;
					query.exec.config.StaticData.HOME_DIR = HOME_DIR;
					strict.ca.usask.cs.srlab.strict.config.StaticData.HOME_DIR = HOME_DIR;
					blizzard.config.StaticData.HOME_DIR = HOME_DIR;
					acer.ca.usask.cs.srlab.coderank.tool.config.StaticData.HOME_DIR = HOME_DIR;
					blader.config.StaticData.BLADER_EXP = HOME_DIR;

					// now add other items for reference
					store.put("STOPWORD_DIR", HOME_DIR + "/pp-data");
					store.put("SAMURAI_DIR", HOME_DIR + "/samurai-data");
					store.put("MAX_ENT_MODEL_DIR", HOME_DIR + "/models");
					store.put("SELECTED_REPOSITORY", "eclipse.jdt.debug");
					store.put("GROUND_TRUTH_DIR", HOME_DIR + "/goldset");
					
					
					//adding the same items to RACK+NLP2API
					IEclipsePreferences storeGS = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.rack");
					storeGS.put("HOME_DIR", homeDirText.getText());
					

				} catch (Exception exc) {
					System.err.println("HOME_DIR missing!");
					logger.error("HOME_DIR missing!");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		return new Composite(parent, SWT.NONE);
	}

}
