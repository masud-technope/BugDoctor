package ca.usask.cs.srlab.bugdoctor.views;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.usask.cs.srlab.bugdoctor.handlers.ViewContentProvider;
import qd.core.EntropyCalc;
import qd.core.QDModelLoader;
import query.exec.lucene.LuceneSearcher;
import query.exec.lucene.ResultFile;
import strict.query.SearchTermProvider;
import strict.text.normalizer.TextNormalizer;
import style.JavaLineStyler;
import utility.ConfigManager;
import utility.ContentLoader;
import utility.MiscUtility;
import acer.coderank.query.expansion.CodeRankQueryExpansionProvider;
import blader.deep.query.expansion.BLADERQueryProvider;
import blizzard.bug.report.classification.BugReportClassifier;
import blizzard.query.BLIZZARDQueryProvider;
import bugdoctor.core.CodeMethod;
import bugdoctor.core.Result;
import bugdoctor.core.StaticData;

public class BugDoctorDashboardView extends ViewPart {

	// public TableViewer viewer;
	public CheckboxTableViewer viewer;
	public TableViewer resultViewer;
	public static final String ID = "ca.usask.cs.srlab.bugdoctor.views.BugDoctorDashboardView";
	public Text input = null;
	Label bugIDLabel = null;
	GridLayout gridLayout = null;
	Button associateContext;
	StyledText codeViewer = null;
	StyledText bugReportViewer = null;
	SourceViewer sourceViewer = null;
	ArrayList<String> suggestions = new ArrayList<>();
	ContentProposalAdapter adapter = null;
	ArrayList<String> queryTokenList = new ArrayList<>();
	final int TEXT_MARGIN = 3;
	public static String OPENED_BUG_REPORT;
	ArrayList<String> currentGoldSet = new ArrayList<>();

	final Display currDisplay = Display.getCurrent();
	final TextLayout textLayout = new TextLayout(currDisplay);
	Font font1 = new Font(currDisplay, "Arial", 13, SWT.BOLD);
	Font font2 = new Font(currDisplay, "Arial", 10, SWT.NORMAL);
	Font font3 = new Font(currDisplay, "Arial", 10, SWT.NORMAL);
	Font codeFont = new Font(currDisplay, "Courier New", 12, SWT.BOLD);

	Color blue = currDisplay.getSystemColor(SWT.COLOR_BLUE);
	Color green = currDisplay.getSystemColor(SWT.COLOR_DARK_GREEN);
	Color gray = currDisplay.getSystemColor(SWT.COLOR_DARK_GRAY);
	Color selected = currDisplay.getSystemColor(SWT.COLOR_YELLOW);
	Color textColor = currDisplay.getSystemColor(SWT.COLOR_BLACK);
	Color maroon = currDisplay.getSystemColor(SWT.COLOR_DARK_MAGENTA);

	TextStyle style1 = new TextStyle(font1, green, null);
	TextStyle style2 = new TextStyle(font2, gray, null);
	TextStyle style3 = new TextStyle(font3, blue, null);
	TextStyle textStyle = new TextStyle(codeFont, textColor, null);

	// available methods
	Button strictButton = null;
	Button acerButton = null;
	Button blizzardButton = null;
	Button bladerButton = null;
	Button baselineButton = null;

	Button openButton = null;

	// search buttons
	Button searchButton = null;

	// collected results
	ArrayList<CodeMethod> collectedResults;

	// logger class
	Logger logger = LoggerFactory.getLogger(BugDoctorDashboardView.class);

	// pre-loaded items
	EntropyCalc entCalc = null;
	HashMap<String, String> keyFileMap = new HashMap<String, String>();

	public BugDoctorDashboardView() {
		// default handler
	}

	protected void initializeHeavyItems() {
		// initialize the heavy items
		IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
		String HOME_DIR = store.get("HOME_DIR", "C:\\MyWorks\\PhDThesisTool");

		// setting up the home directory for all modules
		StaticData.HOME_DIR = HOME_DIR;
		ConfigManager.setHomeDir(HOME_DIR);

		// setting up other items
		ConfigManager.setGlobalConfigs(HOME_DIR);
	}

	protected void loadProjectSpecificItems(String repoName) {
		IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
		String HOME_DIR = store.get("HOME_DIR", "C:\\MyWorks\\PhDThesisTool");
		ConfigManager.setRepoSpecificItems(HOME_DIR, repoName);

		try {
			// load class rank keys
			if (keyFileMap.isEmpty()) {

				String keyFile = query.exec.config.StaticData.HOME_DIR + "/corpus/" + repoName + ".ckeys";
				query.exec.lucene.ClassResultRankMgr.loadKeys(keyFile);
				this.keyFileMap = query.exec.lucene.ClassResultRankMgr.keyMap;

				System.out.println("Loaded keymap:" + repoName + " with " + this.keyFileMap.size() + " entries!");
			}
		} catch (Exception exc) {
			logger.error("Failed to load project specific items: " + exc.getMessage());
		}
	}

	protected GridLayout makeGridLayout(int numberOfColumns) {
		GridLayout gridLayout = new GridLayout(numberOfColumns, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 5;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		return gridLayout;
	}

	protected GridLayout makeGridLayout(int numberOfColumns, boolean equalColumns) {
		GridLayout gridLayout = new GridLayout(numberOfColumns, equalColumns);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 5;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		return gridLayout;
	}

	protected void addBugReportMetaData(Composite parent) {
		// adding bug report meta data
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout brMetaDataGridLayout = makeGridLayout(8);
		composite.setLayout(brMetaDataGridLayout);

		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite.setLayoutData(gridData);

		// gridData = new GridData(SWT.DEFAULT, SWT.FILL, false, false);
		GridData gdata2 = new GridData();
		gdata2.heightHint = 25;
		gdata2.widthHint = 80;
		gdata2.horizontalAlignment = SWT.BEGINNING;
		gdata2.verticalAlignment = SWT.CENTER;
		gdata2.grabExcessHorizontalSpace = false;

		GridData gdata3 = new GridData();
		gdata3.heightHint = 25;
		gdata3.widthHint = 170;
		gdata3.horizontalAlignment = SWT.BEGINNING;
		gdata3.verticalAlignment = SWT.CENTER;
		gdata3.grabExcessHorizontalSpace = false;

		Label keywordlabel = new Label(composite, SWT.NONE);
		keywordlabel.setText("Project:");
		keywordlabel.setFont(new Font(composite.getDisplay(), "Arial", 14, SWT.BOLD));
		keywordlabel.setLayoutData(gdata2);

		// Label projectlabel = new Label(composite, SWT.NONE);
		IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
		// projectlabel.setText(" " + store.get("SELECTED_REPOSITORY", "None"));

		Combo projectCombo = new Combo(composite, SWT.DROP_DOWN);
		String[] repos = { "ecf", "eclipse.jdt.core", "eclipse.jdt.debug", "eclipse.jdt.ui", "eclipse.pde.ui", "log4j",
				"sling", "tomcat70" };
		projectCombo.setItems(repos);
		projectCombo.setFont(new Font(composite.getDisplay(), "Arial", 14, SWT.BOLD));
		projectCombo.setForeground(new Color(null, 168, 64, 48));

		projectCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				int selectedIndex = projectCombo.getSelectionIndex();
				if (selectedIndex >= 0) {

					String selectedRepo = repos[selectedIndex];
					store.put("SELECTED_REPOSITORY", selectedRepo);
					System.out.println("Selected repo:" + selectedRepo);

					// clearing previous key map
					keyFileMap.clear();

					loadProjectSpecificItems(selectedRepo);
					// showing the configuration
					ConfigManager.showConfigs();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// projectCombo.setLayout(gdat);
		// projectlabel.setFont(new Font(composite.getDisplay(), "Arial", 14,
		// SWT.BOLD));
		// projectlabel.setForeground(new Color(null, 168, 64, 48));
		// projectlabel.setLayoutData(gdata3);

		Label _bugIDlabel = new Label(composite, SWT.NONE);
		_bugIDlabel.setText("    ID#:");
		_bugIDlabel.setFont(new Font(composite.getDisplay(), "Arial", 14, SWT.BOLD));
		_bugIDlabel.setLayoutData(gdata2);

		bugIDLabel = new Label(composite, SWT.NONE);
		bugIDLabel.setText("None");
		bugIDLabel.setFont(new Font(composite.getDisplay(), "Arial", 14, SWT.BOLD));
		bugIDLabel.setForeground(new Color(null, 168, 64, 48));
		bugIDLabel.setLayoutData(gdata3);

		GridData gdata4 = new GridData();
		gdata4.heightHint = 30;
		gdata4.widthHint = 220;
		gdata4.horizontalAlignment = SWT.BEGINNING;

		openButton = new Button(composite, SWT.PUSH);
		openButton.setText("Open Issue Report");
		openButton.setToolTipText("Open a new issue report");
		openButton.setFont(font1);
		openButton.setImage(getIssueReportImage());
		openButton.setLayoutData(gdata4);
		openButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

				// storing on the pref
				IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");

				// choose bug report
				FileDialog fileDialog = new FileDialog(composite.getShell(), SWT.NONE);
				String targetDir = store.get("HOME_DIR", "C:\\MyWorks\\PhDThesisTool") + "/bug-report/"
						+ store.get("SELECTED_REPOSITORY", "eclipse.jdt.debug");
				fileDialog.setFilterPath(targetDir);
				final String fileName = fileDialog.open();
				
				// OPENED_BUG_REPORT = fileName;
				File bugReportFile = new File(fileName);
				String bugID = bugReportFile.getName().split("\\.")[0];
				bugIDLabel.setText(bugID);

				store.put("SELECTED_BUGID", bugID);

				// System.out.println(fileName);
				// showing the bug report
				String bugReportText = ContentLoader.loadFileContent(fileName);
				String[] reportLines = bugReportText.split("\n");
				String title = reportLines[0];
				String description = new String();
				for (int i = 1; i < reportLines.length; i++) {
					description += reportLines[i] + "\n";
				}
				bugReportViewer.setText(title + "\n\n" + description);

				StyleRange style1 = new StyleRange();
				style1.start = 0;
				style1.length = title.length();
				style1.fontStyle = SWT.BOLD;
				bugReportViewer.setStyleRange(style1);

				// loading the ground truth
				String repoName = store.get("SELECTED_REPOSITORY", "default_repo");
				int myBugID = Integer.parseInt(store.get("SELECTED_BUGID", "default_bug_id"));
				loadGroundTruth(repoName, myBugID);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// concept mode
		Button concept = new Button(composite, SWT.RADIO);
		concept.setText("Concepts");
		concept.setSelection(true);
		concept.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// adding the concept location engine
				setupConceptLocalization();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// buggy mode
		Button buggy = new Button(composite, SWT.RADIO);
		buggy.setText("Bugs");
		buggy.setSelection(false);
		buggy.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// enabling the bug localization engine
				setupBugLocalization();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// adding the settings icon
		addSettingsPanel(composite);

	}

	protected void loadGroundTruth(String repoName, int bugID) {
		// clear old values
		try {
			this.currentGoldSet.clear();
			// now add the new values
			IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
			String gtruthDir = store.get("GROUND_TRUTH_DIR", "default_gtruth");
			String goldsetFile = gtruthDir + "/" + repoName + "/gold/" + bugID + ".txt";
			ArrayList<String> temp = ContentLoader.getAllLinesList(goldsetFile);
			if (temp.isEmpty()) {
				System.out.println("Failed to load the ground truth !");
			}
			for (String goldFile : temp) {
				if (goldFile.endsWith(".java")) {
					String tempFile = goldFile.replace('\\', '.');
					tempFile = tempFile.replace('/', '.');
					this.currentGoldSet.add(tempFile);
				}
			}
		} catch (Exception exc) {
			logger.error("Failed to load the ground truth !" + exc.getMessage());
			System.out.println("Failed to load the ground truth !" + exc.getMessage());
		}
	}

	protected ArrayList<Integer> getTruePositives(ArrayList<Result> searchResults) {
		int index = 0;
		ArrayList<Integer> found = new ArrayList<Integer>();
		for (Result sResult : searchResults) {
			String mySrcPath = sResult.srcFilePath;
			mySrcPath = mySrcPath.replace('\\', '.');
			mySrcPath = mySrcPath.replace('/', '.');
			for (String mygFile : this.currentGoldSet) {
				if (mySrcPath.endsWith(mygFile)) {
					found.add(index);
					break;
				}
				/*
				 * else if (mySrcPath.contains(mygFile + "#")) { found.add(index); break; }
				 */
			}
			index++;
		}
		return found;
	}

	protected void addSearchPanel(Composite parent) {
		// adding the search panel
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout searchGridLayout = makeGridLayout(4);
		composite.setLayout(searchGridLayout);

		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite.setLayoutData(gridData);

		// gridData = new GridData(SWT.DEFAULT, SWT.FILL, false, false);
		GridData gdata2 = new GridData();
		gdata2.heightHint = 25;
		gdata2.widthHint = 600;
		gdata2.horizontalAlignment = SWT.BEGINNING;
		gdata2.verticalAlignment = SWT.CENTER;
		gdata2.grabExcessHorizontalSpace = false;

		Label keywordlabel = new Label(composite, SWT.NONE);
		keywordlabel.setText("Keywords: ");
		keywordlabel.setFont(new Font(composite.getDisplay(), "Arial", 11, SWT.BOLD));

		input = new Text(composite, SWT.SINGLE | SWT.BORDER);
		input.setEditable(true);
		input.setToolTipText("Enter a query to find your code");
		Font myfont = new Font(composite.getDisplay(), "Arial", 11, SWT.NORMAL);
		input.setFont(myfont);
		input.setLayoutData(gdata2);

		GridData gdata4 = new GridData();
		gdata4.heightHint = 30;
		gdata4.widthHint = 220;
		gdata4.horizontalAlignment = SWT.BEGINNING;

		searchButton = new Button(composite, SWT.PUSH);
		searchButton.setText("Search Relevant Code");
		searchButton.setToolTipText("Click to locate the relevant code");
		searchButton.setFont(font1);
		searchButton.setImage(getCodeSearchImage());
		searchButton.setLayoutData(gdata4);
		searchButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				final String searchQuery = input.getText().trim();
				if (!searchQuery.isEmpty()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {

							IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
							String HOME_DIR = store.get("HOME_DIR", "C:\\MyWorks\\PhDThesisTool");

							String repository = store.get("SELECTED_REPOSITORY", "eclipse.jdt.debug");

							loadProjectSpecificItems(repository);

							// showing configurations
							ConfigManager.showConfigs();

							int bugID = store.getInt("SELECTED_BUGID", 0);

							String indexFolder = store.get("INDEX_DIR", "default_index");
							String searchQuery = input.getText();

							System.out.println("Searching for:" + repository + ", Bug ID:" + bugID);

							LuceneSearcher searcher = new LuceneSearcher(bugID, repository, searchQuery, indexFolder);
							ArrayList<ResultFile> resultFiles = searcher.performVSMSearchListPlus(true);
							System.out.println("Retrieved results:" + resultFiles.size());

							ArrayList<Result> entities = new ArrayList<Result>();
							for (int index = 1; index < resultFiles.size(); index++) {
								ResultFile rfile = resultFiles.get(index);
								double buggyScore = rfile.score;
								if (buggyScore > 0) {
									try {
										Result bentity = new Result();
										bentity.token = rfile.filePath;
										bentity.resultRank = index;
										String fileName = new File(rfile.filePath).getName().trim();
										if (keyFileMap.containsKey(fileName)) {
											String srcFilePath = keyFileMap.get(fileName);
											String className = new File(srcFilePath).getName().split("\\.")[0];
											bentity.token = className;
											bentity.srcFilePath = modifySourceFileURL(srcFilePath);
										}
										bentity.totalScore = buggyScore;
										entities.add(bentity);
									} catch (Exception exc) {
										// handle the exception
									}
								}
								if (entities.size() == StaticData.MAX_SEARCH_RESULT_SIZE)
									break;
							}

							// populate and highlight
							populateResultsToTable(entities);
						}
					});
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		Button expandButton = new Button(composite, SWT.PUSH);
		expandButton.setText("Expand Query");
		expandButton.setToolTipText("Click to expand the query");
		expandButton.setFont(font1);
		expandButton.setImage(getExpandQueryImage());
		GridData gdata5 = new GridData();
		gdata5.heightHint = 30;
		gdata5.widthHint = 150;
		gdata5.horizontalAlignment = SWT.BEGINNING;
		expandButton.setLayoutData(gdata5);
		expandButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// showing configurations
				ConfigManager.showConfigs();

				// TODO Auto-generated method stub
				IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");

				int bugID = store.getInt("SELECTED_BUGID", 0);
				bugID = Integer.parseInt(bugIDLabel.getText());

				String repoName = store.get("SELECTED_REPOSITORY", "eclipse.jdt.debug");

				loadProjectSpecificItems(repoName);

				qd.config.StaticData.HOME_DIR = StaticData.HOME_DIR;

				String searchQuery = input.getText();
				String bestQuery = new String();

				if (acerButton.getSelection() && acerButton.getEnabled()) {
					acer.ca.usask.cs.srlab.coderank.tool.config.StaticData.HOME_DIR = store.get("HOME_DIR",
							"default_home");
					qd.config.StaticData.HOME_DIR = store.get("HOME_DIR", "default_home");
					String indexFolder = store.get("INDEX_DIR", "default_index");
					String repoSourceFolder = store.get("REPOSITORY_SRC_DIR", "default_repo_src");

					CodeRankQueryExpansionProvider crProvider = new CodeRankQueryExpansionProvider(repoName, bugID,
							searchQuery, indexFolder, repoSourceFolder);
					bestQuery = crProvider.getExtendedQuery(StaticData.BR_NL_QR_LEN);

				} else if (bladerButton.getSelection() && bladerButton.getEnabled()) {

					// getting the blader query
					blader.config.StaticData.BLADER_EXP = store.get("HOME_DIR", "default_home");
					qd.config.StaticData.HOME_DIR = store.get("HOME_DIR", "default_home");

					BLADERQueryProvider bqProvider = new BLADERQueryProvider(repoName, bugID);
					bestQuery = bqProvider.deliverBLADERQuery();
				}

				ArrayList<Result> extendedKeywords = new ArrayList<>();
				// adding the title result
				String normalizedTitle = new blizzard.text.normalizer.TextNormalizer(searchQuery).normalizeSimple();
				Result titleResult = new Result();
				titleResult.token = normalizedTitle;
				titleResult.totalScore = 1.00;
				extendedKeywords.add(titleResult);

				// adding the keywords
				ArrayList<String> keywords = qd.utility.MiscUtility.str2List(bestQuery);
				for (int index = 1; index < keywords.size(); index++) {
					String keyword = keywords.get(index);
					double relevance = 1 - (double) index / keywords.size();
					Result rKeyword = new Result();
					// rKeyword.resultRank = index;
					rKeyword.token = keyword;
					rKeyword.totalScore = relevance;
					extendedKeywords.add(rKeyword);
				}

				populateResultsToIDE(extendedKeywords);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

	}

	protected String modifySourceFileURL(String filePath) {
		String repositoryRoot = "ssystems";
		int repoRootIndex = filePath.indexOf(repositoryRoot);
		return StaticData.HOME_DIR + "\\" + filePath.substring(repoRootIndex);
	}

	protected void executeKeyEvent() {
		try {
			Thread.sleep(10);
			Event event = new Event();
			event.type = SWT.KeyDown;
			event.stateMask = SWT.CTRL;
			event.keyCode = SWT.SPACE;
			// Display.getCurrent().post(event);
			input.notifyListeners(SWT.KeyDown, event);
			System.out.println("Event passed successfully.");

		} catch (Exception exc) {
			// handle the exception
		}
	}

	protected Image getBugReportImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "bugdoctor.png").createImage();
	}

	protected Image getIssueReportImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "issue-report.png").createImage();
	}

	protected Image get_search_image() {
		return ImageDescriptor.createFromFile(ViewLabelProvider.class, "searchbt16.gif").createImage();
	}

	protected Image getSettingsImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "settings.png").createImage();
	}

	protected Image getSuggestionImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "suggestion.png").createImage();
	}

	protected Image getQueryImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "search-query.png").createImage();
	}

	protected Image getBugSearchImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "bug-search.png").createImage();
	}

	protected Image getCodeSearchImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "search16.png").createImage();
	}

	protected Image getCodeResultImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "class.png").createImage();
	}

	protected Image getExpandQueryImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "expand.png").createImage();
	}

	protected String getCodeSearchQuery() {
		ArrayList<String> apiNames = new ArrayList<>();
		Object[] checkedElems = viewer.getCheckedElements();
		// System.out.println(checkedElems);
		for (Object cobj : checkedElems) {
			Result result = (Result) cobj;
			apiNames.add(result.token);
		}
		// apiNames.addAll(selections);
		String apiStr = MiscUtility.list2Str(apiNames);
		return apiStr;
	}

	protected void addKeywordSuggestionPanel(SashForm divider) {
		// query suggestion panel

		final Composite keywordSuggestionPanel = new Composite(divider, SWT.NONE);
		GridLayout kwGridLayout = makeGridLayout(1);
		keywordSuggestionPanel.setLayout(kwGridLayout);
		GridData kwGridLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		keywordSuggestionPanel.setLayoutData(kwGridLayoutData);

		// adding the keyword suggestion panel
		GridData kwSearchLayoutData = new GridData(SWT.CENTER, SWT.TOP, true, false);
		final Button kwSuggestButton = new Button(keywordSuggestionPanel, SWT.PUSH);
		kwSuggestButton.setText("Suggest Query Keywords");
		kwSuggestButton.setSize(250, 25);
		kwSuggestButton.setFont(font1);
		kwSuggestButton.setImage(getSuggestionImage());
		kwSuggestButton.setLayoutData(kwSearchLayoutData);
		kwSuggestButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {

						// showing configurations
						ConfigManager.showConfigs();

						IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
						String SELECTED_REPOSITORY = store.get("SELECTED_REPOSITORY", "eclipse.jdt.debug");
						int bugID = Integer.parseInt(bugIDLabel.getText().trim());

						String bugReport = bugReportViewer.getText();
						String title = bugReport.split("\n")[0].trim();
						String bugDoctorQuery = new String();
						
						//load project specific item
						loadProjectSpecificItems(SELECTED_REPOSITORY);
						

						ArrayList<Result> suggestedKeywords = new ArrayList<Result>();
						int startIndex = 1;

						// concepts mode of BugDoctor
						if (strictButton.getSelection() && strictButton.getEnabled()) {

							qd.config.StaticData.HOME_DIR = store.get("HOME_DIR", "default_home");
							strict.ca.usask.cs.srlab.strict.config.StaticData.STOPWORD_DIR = store.get("STOPWORD_DIR",
									"default_stopword");
							strict.ca.usask.cs.srlab.strict.config.StaticData.SAMURAI_DIR = store.get("SAMURAI_DIR",
									"default_samurai");
							strict.ca.usask.cs.srlab.strict.config.StaticData.MAX_ENT_MODELS_DIR = store
									.get("MAX_ENT_MODEL_DIR", "default_model_dir");

							SearchTermProvider stProvider = new SearchTermProvider(SELECTED_REPOSITORY, bugID, title,
									bugReport);
							String bestQuery = stProvider.deliverBestQuery();
							System.out.println(bestQuery);
							bugDoctorQuery = bestQuery;
						}
						// buggy mode of BugDoctor
						else if (blizzardButton.getSelection() && blizzardButton.getEnabled()) {
							blizzard.config.StaticData.HOME_DIR = store.get("HOME_DIR", "default_home");
							blizzard.config.StaticData.STACK_TRACE_DIR = store.get("STACK_TRACE_DIR", "default_st_dir");
							blizzard.config.StaticData.STOPWORD_DIR = store.get("STOPWORD_DIR", "default_stopword");

							BugReportClassifier brClassifier = new BugReportClassifier(bugReport);
							String reportClass = brClassifier.determineReportClass();
							if (reportClass.equals("ST")) {
								BLIZZARDQueryProvider bzProvider = new BLIZZARDQueryProvider(SELECTED_REPOSITORY, bugID,
										title, bugReport);
								blizzard.config.StaticData.MAX_ST_SUGGESTED_QUERY_LEN = StaticData.BR_ST_QR_LEN;
								bugDoctorQuery = bzProvider.provideBLIZZARDQuery();
								// add the title for noisy
								if (reportClass.equals("ST")) {
									String normalizedTitle = new blizzard.text.normalizer.TextNormalizer(title)
											.normalizeSimple();

									Result titleResult = new Result();
									titleResult.token = normalizedTitle;
									titleResult.totalScore = 1.00;
									suggestedKeywords.add(titleResult);

									// populating the results
									startIndex = reportClass.equals("ST") ? 0 : 1;
								}
							}

						} else {
							MessageBox selectMethod = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ERROR);
							selectMethod.setText("Error!");
							selectMethod.setMessage("Please choose a technique!");
							int returnedCode = selectMethod.open();
							System.out.println(returnedCode);
						}

						// now populate these keywords
						ArrayList<String> keywords = MiscUtility.str2List(bugDoctorQuery);
						for (int index = startIndex; index < keywords.size(); index++) {
							String keyword = keywords.get(index);
							double relevance = 1 - (double) index / keywords.size();
							Result rKeyword = new Result();
							rKeyword.token = keyword;
							rKeyword.totalScore = relevance;
							suggestedKeywords.add(rKeyword);
						}

						populateResultsToIDE(suggestedKeywords);

					}
				});
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// showing the bug report
		GridData brLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		bugReportViewer = new StyledText(keywordSuggestionPanel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		bugReportViewer.setFont(new Font(keywordSuggestionPanel.getDisplay(), "Arial", 10, SWT.NORMAL));
		bugReportViewer.setMargins(0, 0, 0, 0);
		bugReportViewer.setLayoutData(brLayoutData);
	}

	protected void addResultTableOnly(SashForm divider) {

		Composite composite = new Composite(divider, SWT.CENTER);
		GridLayout tableGirdLayout = makeGridLayout(1);
		composite.setLayout(tableGirdLayout);

		// adding the keyword suggestion panel
		GridLayout makeQueryLayout = makeGridLayout(2, true);

		makeQueryLayout.marginWidth = 0;
		makeQueryLayout.marginHeight = 0;
		makeQueryLayout.verticalSpacing = 0;
		makeQueryLayout.horizontalSpacing = 5;

		GridData c2GridData = new GridData(SWT.FILL, SWT.CENTER, true, false);

		Composite composite2 = new Composite(composite, SWT.FILL);
		composite2.setLayout(makeQueryLayout);
		composite2.setLayoutData(c2GridData);

		final Button makeQueryButton = new Button(composite2, SWT.PUSH);
		GridData makeQueryLayoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		makeQueryButton.setText("Make Query");
		makeQueryButton.setSize(250, 25);
		makeQueryButton.setFont(font1);
		makeQueryButton.setImage(getQueryImage());
		makeQueryButton.setLayoutData(makeQueryLayoutData);
		makeQueryButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				final TableItem[] items = viewer.getTable().getItems();
				ArrayList<String> keywordList = new ArrayList<>();
				for (int i = 0; i < items.length; ++i) {
					if (items[i].getChecked()) {
						keywordList.add(items[i].getText());
					}
				}
				// now update the query box
				input.setText(MiscUtility.list2Str(keywordList));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		// selecting all keywords
		GridData selectAllLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		final Button selectAllButton = new Button(composite2, SWT.CHECK);
		selectAllButton.setText("Select All Keywords");
		// selectAllButton.setFont(font1);
		selectAllButton.setLayoutData(selectAllLayoutData);
		selectAllButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (selectAllButton.getSelection()) {
					final Table table = viewer.getTable();
					TableItem[] rows = table.getItems();
					for (int i = 0; i < rows.length; i++) {
						rows[i].setChecked(true);
					}
				} else {
					final Table table = viewer.getTable();
					TableItem[] rows = table.getItems();
					for (int i = 0; i < rows.length; i++) {
						rows[i].setChecked(false);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		GridData resultGridLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer = CheckboxTableViewer.newCheckList(composite,
				SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		final Table table = viewer.getTable();
		table.setLayout(tableGirdLayout);
		table.setLayoutData(resultGridLayoutData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		String[] columns = { "Suggested Keyword", "Relevance" };
		int[] colWidth = { 300, 100 };
		int[] colAlignment = { SWT.LEFT, SWT.LEFT };
		for (int i = 0; i < columns.length; i++) {
			// stored for sorting
			// final int columnNum = i;
			TableColumn col = new TableColumn(table, colAlignment[i]);
			col.setText(columns[i]);
			col.setWidth(colWidth[i]);
		}

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProviderKS());
		viewer.setInput(getViewSite());

		// adding the paint item
		setItemHeight(table);
		setPaintItemKS(table);
		setKeyEventItems(table);
	}

	protected void populateResultsToIDE(ArrayList<Result> results) {
		// code for populating results to IDE
		try {
			ViewContentProvider viewContentProvider = new ViewContentProvider(results);
			this.viewer.setContentProvider(viewContentProvider);
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
	}

	protected void populateResultsToTable(ArrayList<Result> results) {
		try {
			ViewContentProvider viewContentProvider = new ViewContentProvider(results);
			this.resultViewer.setContentProvider(viewContentProvider);
			ArrayList<Integer> found = getTruePositives(results);
			this.highLightBuggyRows(found, this.resultViewer.getTable());
			System.out.println(found);
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
	}

	protected void setupBugLocalization() {
		// setting up the stage for bug localization
		blizzardButton.setEnabled(true);
		bladerButton.setEnabled(true);
		strictButton.setEnabled(false);
		acerButton.setEnabled(false);
		blizzardButton.setSelection(true);

		// setting up the concepts
		openButton.setText("Open Bug Report");
		openButton.setImage(getBugReportImage());
		searchButton.setText("Search Buggy Code");
		searchButton.setImage(getBugSearchImage());

		// modify the table columns
		TableColumn[] cols = resultViewer.getTable().getColumns();
		cols[1].setText("Buggy Entity");
		cols[2].setText("Suspiciousness");
	}

	protected void setupConceptLocalization() {
		// setting up environment for concept location
		blizzardButton.setEnabled(false);
		bladerButton.setEnabled(false);
		strictButton.setEnabled(true);
		acerButton.setEnabled(true);
		strictButton.setSelection(true);

		// setting up the bugs
		openButton.setText("Open Change Request");
		openButton.setImage(getIssueReportImage());
		searchButton.setText("Search Relevant Code");
		searchButton.setImage(getCodeSearchImage());

		// modify the table columns
		TableColumn[] cols = resultViewer.getTable().getColumns();
		cols[1].setText("Code Entity");
		cols[2].setText("Relevance");
	}

	protected void addCommandPanel(SashForm divider) {
		// now add the search button
		final Composite cmdPanel = new Composite(divider, SWT.NONE);
		GridLayout cmdGridLayout = makeGridLayout(1);
		cmdPanel.setLayout(cmdGridLayout);

		GridData cmdGridLayoutData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		cmdGridLayoutData.heightHint = 30;
		cmdPanel.setLayoutData(cmdGridLayoutData);

		// add the result view
		GridData resultGridLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		resultViewer = new TableViewer(cmdPanel,
				SWT.MULTI | SWT.H_SCROLL | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		final Table table = resultViewer.getTable();
		table.setLayoutData(resultGridLayoutData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		String[] columns = { "Rank", "Code Entity", "Relevance" };
		int[] colWidth = { 40, 300, 100 };
		int[] colAlignment = { SWT.LEFT, SWT.LEFT, SWT.LEFT };
		for (int i = 0; i < columns.length; i++) {
			// stored for sorting
			// final int columnNum = i;
			TableColumn col = new TableColumn(table, colAlignment[i]);
			col.setText(columns[i]);
			col.setWidth(colWidth[i]);
		}

		resultViewer.setContentProvider(new ViewContentProvider());
		resultViewer.setLabelProvider(new ViewLabelProvider());
		resultViewer.setInput(getViewSite());

		// adding the paint item
		setItemHeight(table);
		setPaintItemRed(table);
		setKeyEventItems(table);

		resultViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Result result = (Result) selection.getFirstElement();
				String filePath = result.srcFilePath;
				System.out.println(filePath);
				try {
					File fileToOpen = new File(filePath);
					if (fileToOpen.exists() && fileToOpen.isFile()) {
						IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						try {
							IDE.openEditorOnFileStore(page, fileStore);
						} catch (PartInitException e) {
							// Put your exception handler here if you wish to
						}
					} else {
						MessageDialog.openError(null, "Error!", "Failed to load the file. The file does not exist!");
					}

				} catch (Exception exc) {
					// handle the exception
					exc.printStackTrace();
				}
			}
		});

	}

	protected void addCodeViewer(SashForm divider) {
		// now add the editor
		Composite composite = new Composite(divider, SWT.NONE);
		codeViewer = new StyledText(composite, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		codeViewer.setFont(new Font(composite.getDisplay(), "Courier New", 10, SWT.NORMAL));
		codeViewer.addLineStyleListener(new JavaLineStyler());
		codeViewer.setMargins(0, 0, 0, 0);
	}

	@SuppressWarnings("deprecation")
	protected void addResultTable(Composite parent) {
		// adding the result table
		// result panel display
		// final Composite composite3 = new Composite(parent, SWT.NONE);
		GridLayout resultGridLayout = makeGridLayout(4);
		// composite3.setLayout(resultGridLayout);
		GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		// composite3.setLayoutData(gridData2);

		final SashForm divider = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);
		divider.setLayout(resultGridLayout);
		divider.setLayoutData(gridData2);

		// now start adding the items
		this.addKeywordSuggestionPanel(divider);
		this.addResultTableOnly(divider);
		this.addCommandPanel(divider);
		// this.addCodeViewer(divider);

		// setting relative weights
		divider.setWeights(new int[] { 2, 2, 3 });
	}

	protected void selectHighlightCodeViewer() {
		// select and highlight code viewer
		try {
			String query = input.getText().trim();
			String[] queryTokens = query.split("\\s+");
			String content = codeViewer.getText();
			ArrayList<Integer> keyIndices = new ArrayList<>();
			// collect all the indices for the keyword
			outer: for (String keyword : queryTokens) {
				Pattern p = Pattern.compile(keyword);
				Matcher m = p.matcher(content);
				while (m.find()) {
					int start = m.start();
					int length = keyword.length();
					StyleRange range = new StyleRange(start, length, maroon, null, SWT.BOLD);
					codeViewer.setStyleRange(range);
					// keyIndices.add(start);
					// keyIndices.add(length);
					break outer;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected ArrayList<Integer> getItemIndices(String item) {
		// getting item indices
		ArrayList<Integer> indices = new ArrayList<>();
		String code = codeViewer.getText();
		int start = 0;
		while (start < code.length()) {
			int index = code.indexOf(item, start);
			if (index > 0) {
				indices.add(index);
				start += item.length();
			} else
				break;
		}
		return indices;
	}

	protected void tokenizeQuery(String query) {
		// Tokenize the query
		this.queryTokenList.clear();
		String[] words = query.split("\\s+");
		for (String word : words) {
			String[] parts = StringUtils.splitByCharacterTypeCamelCase(word);
			int count = 0;
			while (count < parts.length) {
				if (!this.queryTokenList.contains(parts[count])) {
					this.queryTokenList.add(parts[count]);
				}
				count++;
			}
		}
	}

	protected void setItemHeight(Table table) {
		table.addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				Point size = event.gc.textExtent(text);
				event.width = size.x + 2 * TEXT_MARGIN;
				event.height = 20;// Math.max(min, size.y + TEXT_MARGIN);
			}
		});
		table.addListener(SWT.EraseItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				event.detail &= ~SWT.FOREGROUND;
			}
		});
	}

	protected void setKeyEventItems(Table table) {
		// adding key events with the table
		table.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				System.out.println("Selected:" + event.index + " " + event.item);
			}
		});
	}

	protected void setPaintItemRed(Table table) {
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if (event.index <= 1) {
					// adding layout to code example title
					int index = event.index;
					TableItem item = (TableItem) event.item;
					String title = item.getText(index);
					textLayout.setText(title);
					textLayout.setStyle(textStyle, 0, title.length());
					textLayout.draw(event.gc, event.x, event.y);

					// adding the image
					// item.setImage(getCodeResultImage());

				} else if (event.index >= 2 && event.index <= 4) {
					GC gc = event.gc;
					int index = event.index;
					TableItem item = (TableItem) event.item;
					int percent = (int) Double.parseDouble(item.getText(index));
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					// gc.setForeground(new Color(null, 11, 59, 23));
					Color myforeground = new Color(null, 11, 97, 11);

					if (index == 2) {
						myforeground = new Color(null, 244, 113, 66);
					}

					gc.setForeground(myforeground);
					gc.setBackground(new Color(null, 255, 255, 255));

					int col2Width = 100;
					int width = (col2Width - 1) * percent / 100;
					int height = 25;
					gc.fillGradientRectangle(event.x, event.y, width, height, false);
					Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, height - 1);
					gc.drawRectangle(rect2);
					gc.setForeground(new Color(null, 255, 255, 255));
					String text = percent + "%";
					Point size = event.gc.textExtent(text);
					int offset = Math.max(0, (height - size.y) / 2);
					gc.drawText(text, event.x + 2, event.y + offset, true);
					gc.setForeground(background);
					gc.setBackground(foreground);
				}
			}
		});
	}

	protected void highLightBuggyRows(ArrayList<Integer> buggyIndices, Table table) {
		TableItem[] rows = table.getItems();
		Color red = new Color(null, 255, 0, 0);
		for (int index : buggyIndices) {
			TableItem buggyRow = rows[index];
			buggyRow.setBackground(1, red);
			buggyRow.setBackground(2, red);
			// buggyRow.setBackground(0, red);
		}
	}

	protected void setPaintItemKS(Table table) {
		// adding paint item
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if (event.index == 0) {
					// adding layout to code example title
					int index = event.index;
					TableItem item = (TableItem) event.item;
					String title = item.getText(index);
					textLayout.setText(title);
					textLayout.setStyle(textStyle, 0, title.length());
					textLayout.draw(event.gc, event.x, event.y);

				} else if (event.index == 1) {
					GC gc = event.gc;
					int index = event.index;
					TableItem item = (TableItem) event.item;
					int percent = (int) Double.parseDouble(item.getText(index));
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					// gc.setForeground(new Color(null, 11, 59, 23));
					Color myforeground = new Color(null, 11, 97, 11);

					gc.setForeground(myforeground);
					gc.setBackground(new Color(null, 255, 255, 255));

					int col2Width = 100;
					int width = (col2Width - 1) * percent / 100;
					int height = 25;
					// gc.fillRectangle(event.x, event.y + 10, width,
					// height);
					gc.fillGradientRectangle(event.x, event.y, width, height, false);
					Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, height - 1);
					gc.drawRectangle(rect2);
					gc.setForeground(new Color(null, 255, 255, 255));
					String text = percent + "%";
					Point size = event.gc.textExtent(text);
					int offset = Math.max(0, (height - size.y) / 2);
					gc.drawText(text, event.x + 2, event.y + offset, true);
					gc.setForeground(background);
					gc.setBackground(foreground);
				}
			}
		});
	}

	protected void setPaintItem(Table table) {
		// adding paint item
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if (event.index == 0 || event.index == 1) {
					// adding layout to code example title
					int index = event.index;
					TableItem item = (TableItem) event.item;
					String title = item.getText(index);
					textLayout.setText(title);
					textLayout.setStyle(textStyle, 0, title.length());
					textLayout.draw(event.gc, event.x, event.y);

				} else if (event.index >= 2 && event.index <= 4) {
					GC gc = event.gc;
					int index = event.index;
					TableItem item = (TableItem) event.item;
					int percent = (int) Double.parseDouble(item.getText(index));
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					// gc.setForeground(new Color(null, 11, 59, 23));
					Color myforeground = new Color(null, 11, 97, 11);

					if (index == 2) {
						myforeground = new Color(null, 0, 64, 255);
					}

					if (index == 2) {
						myforeground = new Color(null, 17, 122, 141);
					}
					if (index == 3) {
						myforeground = new Color(null, 99, 67, 98);
					}
					if (index == 4) {
						myforeground = new Color(null, 11, 97, 11);
					}

					gc.setForeground(myforeground);
					gc.setBackground(new Color(null, 255, 255, 255));

					int col2Width = 100;
					int width = (col2Width - 1) * percent / 100;
					int height = 25;
					// gc.fillRectangle(event.x, event.y + 10, width,
					// height);
					gc.fillGradientRectangle(event.x, event.y, width, height, false);
					Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, height - 1);
					gc.drawRectangle(rect2);
					gc.setForeground(new Color(null, 255, 255, 255));
					String text = percent + "%";
					Point size = event.gc.textExtent(text);
					int offset = Math.max(0, (height - size.y) / 2);
					gc.drawText(text, event.x + 2, event.y + offset, true);
					gc.setForeground(background);
					gc.setBackground(foreground);
				}
			}
		});
	}

	protected void addUtilityLayer(Composite parent) {
		// adding the utility layer
		final Composite composite2 = new Composite(parent, SWT.NONE);
		GridLayout gridLayout2 = makeGridLayout(7);
		composite2.setLayout(gridLayout2);

		GridData gridData2 = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite2.setLayoutData(gridData2);

		baselineButton = new Button(composite2, SWT.RADIO);
		baselineButton.setText("Baseline");
		baselineButton.setSelection(false);
		baselineButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (baselineButton.getSelection()) {
					// setting the baseline query
					String reportText = bugReportViewer.getText();
					String baselineQuery = new TextNormalizer(reportText).normalizeBaseline();
					input.setText(baselineQuery);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		strictButton = new Button(composite2, SWT.RADIO);
		strictButton.setText("STRICT");
		strictButton.setSelection(false);

		acerButton = new Button(composite2, SWT.RADIO);
		acerButton.setText("ACER");
		acerButton.setSelection(false);

		blizzardButton = new Button(composite2, SWT.RADIO);
		blizzardButton.setText("BLIZZARD");
		blizzardButton.setSelection(false);

		bladerButton = new Button(composite2, SWT.RADIO);
		bladerButton.setText("BLADER");
		bladerButton.setSelection(false);

		final Button clearButton = new Button(composite2, SWT.CHECK);
		clearButton.setText("Reset search");
		// adding listener to clear button
		clearButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (clearButton.getSelection()) {
					try {
						// clearing all items
						input.setText("");
						viewer.setContentProvider(new ViewContentProvider());
						// codeViewer.setText("");
						bugReportViewer.setText("");
						resultViewer.setContentProvider(new ViewContentProvider());

						// clearing the buttons
						baselineButton.setSelection(false);
						strictButton.setSelection(false);
						acerButton.setSelection(false);
						blizzardButton.setSelection(false);
						bladerButton.setSelection(false);

					} catch (Exception exc3) {
						// handle the exception
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
	}

	protected void addSettingsPanel(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout settingsDataGridLayout = makeGridLayout(1);
		composite.setLayout(settingsDataGridLayout);

		GridData gridData = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		gridData.heightHint = 35;
		gridData.widthHint = 30;
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = false;
		composite.setLayoutData(gridData);

		Button settingsButton = new Button(composite, SWT.PUSH);
		settingsButton.setImage(getSettingsImage());
		settingsButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				MessageBox selectMethod = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
				selectMethod.setText("BugDoctor Settings");
				selectMethod.setMessage(ConfigManager.getToolConfigs());
				int returnedCode = selectMethod.open();
				System.out.println(returnedCode);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void createPartControl(Composite parent) {

		initializeHeavyItems();

		GridLayout glayout = new GridLayout();
		glayout.marginWidth = 5;
		glayout.marginHeight = 10;
		parent.setLayout(glayout);

		GridData gdata = new GridData(SWT.FILL, SWT.FILL, false, true);
		parent.setLayoutData(gdata);

		// addSettingsPanel(parent);

		addBugReportMetaData(parent);
		addSearchPanel(parent);
		addUtilityLayer(parent);
		addResultTable(parent);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "ca.usask.cs.srlab.bugdoctor.viewer");
	}

	class ViewLabelProviderKS extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int index) {
			// TODO Auto-generated method stub
			Result myresult = (Result) element;
			switch (index) {
			case 0:
				if (myresult.token != null)
					return myresult.token;
				return "";
			case 1:
				if (myresult.totalScore > 0)
					return String.format("%.2f", myresult.totalScore * 100);
				return "0";
			default:
				return "";
			}
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			Result myresult = (Result) obj;
			switch (index) {
			case 0:
				if (myresult.resultRank >= 0) {
					return Integer.toString(myresult.resultRank);
				}
				return "0";
			case 1:
				if (myresult.token != null)
					return myresult.token;
				return "";
			case 2:
				if (myresult.totalScore > 0)
					return String.format("%.2f", myresult.totalScore * 100);
				return "0";
			default:
				return "";
			}
		}

		@Override
		public Image getColumnImage(Object obj, int index) {
			// return null;
			Image image = null;
			if (index == 0) {
				image = getImage(obj);
				// System.out.println("Visiting image icon"+image);
			}
			return image;
		}

		public Image getImage(Object obj) {
			return ImageDescriptor.createFromFile(ViewLabelProvider.class, "class.png").createImage();
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
}
