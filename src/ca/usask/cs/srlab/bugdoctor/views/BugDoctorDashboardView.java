package ca.usask.cs.srlab.bugdoctor.views;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ca.usask.cs.srlab.bugdoctor.Activator;
import ca.usask.cs.srlab.bugdoctor.handlers.ViewContentProvider;
import ca.usask.cs.srlab.bugdoctor.handlers.ViewContentProviderEx;
import qd.core.EntropyCalc;
import strict.query.SearchTermProvider;
import style.JavaLineStyler;
import utility.ContentLoader;
import utility.MiscUtility;
import bugdoctor.core.CodeMethod;
import bugdoctor.core.Result;
import bugdoctor.core.StaticData;

public class BugDoctorDashboardView extends ViewPart {

	// public TableViewer viewer;
	public CheckboxTableViewer viewer;
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

	// collected results
	ArrayList<CodeMethod> collectedResults;

	public BugDoctorDashboardView() {
		// default handler
	}

	protected GridLayout makeGridLayout(int numberOfColumns) {
		GridLayout gridLayout = new GridLayout(numberOfColumns, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		return gridLayout;
	}

	protected void addBugReportMetaData(Composite parent) {
		// adding bug report meta data
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout brMetaDataGridLayout = makeGridLayout(5);
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
		gdata3.widthHint = 150;
		gdata3.horizontalAlignment = SWT.BEGINNING;
		gdata3.verticalAlignment = SWT.CENTER;
		gdata3.grabExcessHorizontalSpace = false;

		Label keywordlabel = new Label(composite, SWT.NONE);
		keywordlabel.setText("Project:");
		keywordlabel.setFont(new Font(composite.getDisplay(), "Arial", 14, SWT.BOLD));
		keywordlabel.setLayoutData(gdata2);

		Label projectlabel = new Label(composite, SWT.NONE);
		projectlabel.setText(" " + Activator.SELECTED_REPOSITORY);
		projectlabel.setFont(new Font(composite.getDisplay(), "Arial", 14, SWT.BOLD));
		projectlabel.setForeground(new Color(null, 0, 255, 0));
		projectlabel.setLayoutData(gdata3);

		Label _bugIDlabel = new Label(composite, SWT.NONE);
		_bugIDlabel.setText("BugID:");
		_bugIDlabel.setFont(new Font(composite.getDisplay(), "Arial", 14, SWT.BOLD));
		_bugIDlabel.setLayoutData(gdata2);

		bugIDLabel = new Label(composite, SWT.NONE);
		bugIDLabel.setText("None");
		bugIDLabel.setFont(new Font(composite.getDisplay(), "Arial", 14, SWT.BOLD));
		bugIDLabel.setForeground(new Color(null, 0, 255, 0));
		bugIDLabel.setLayoutData(gdata3);

		GridData gdata4 = new GridData();
		gdata4.heightHint = 30;
		gdata4.widthHint = 200;
		gdata4.horizontalAlignment = SWT.BEGINNING;

		Button openButton = new Button(composite, SWT.PUSH);
		openButton.setText("Open a Bug Report");
		openButton.setToolTipText("Open a new bug report");
		openButton.setFont(font1);
		openButton.setImage(getRelevantAPIImage());
		openButton.setLayoutData(gdata4);
		openButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// choose bug report
				FileDialog fileDialog = new FileDialog(composite.getShell(), SWT.NONE);
				final String fileName = fileDialog.open();
				OPENED_BUG_REPORT = fileName;
				File bugReportFile = new File(fileName);
				String bugID = bugReportFile.getName().split("\\.")[0];
				bugIDLabel.setText(bugID);
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
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

	}

	protected void addSearchPanel(Composite parent) {
		// adding the search panel
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout searchGridLayout = makeGridLayout(3);
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
		input.setToolTipText("Enter your query to find out the buggy code");
		Font myfont = new Font(composite.getDisplay(), "Arial", 11, SWT.NORMAL);
		input.setFont(myfont);
		input.setLayoutData(gdata2);

		GridData gdata4 = new GridData();
		gdata4.heightHint = 30;
		gdata4.widthHint = 200;
		gdata4.horizontalAlignment = SWT.BEGINNING;

		Button rackButton = new Button(composite, SWT.PUSH);
		rackButton.setText("Search Buggy Code");
		rackButton.setToolTipText("Click to locate the buggy code");
		rackButton.setFont(font1);
		rackButton.setImage(getBugSearchImage());
		rackButton.setLayoutData(gdata4);
		rackButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				final String searchQuery = input.getText().trim();
				if (!searchQuery.isEmpty()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							/*
							 * SearchEventManager searchManager = new SearchEventManager( searchQuery);
							 * searchManager.performSearch();
							 */
						}
					});
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
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

	protected Image getRelevantAPIImage() {
		return ImageDescriptor.createFromFile(BugDoctorDashboardView.class, "bugdoctor.png").createImage();
	}

	protected Image get_search_image() {
		return ImageDescriptor.createFromFile(ViewLabelProvider.class, "searchbt16.gif").createImage();
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
		kwSuggestButton.setText("Suggest Keywords");
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

						/*String reporName = "eclipse.jdt.debug";
						int bugID = Integer.parseInt(bugIDLabel.getText().trim());
						String bugReport = bugReportViewer.getText();
						String title = bugReport.split("\n")[0].trim();
						EntropyCalc entCalc = new EntropyCalc("eclipse.jdt.debug",
								StaticData.HOME_DIR + "/lucene/index-method/eclipse.jdt.debug",
								StaticData.HOME_DIR + "/corpus/norm-method/eclipse.jdt.debug");

						strict.ca.usask.cs.srlab.strict.config.StaticData.STOPWORD_DIR = StaticData.HOME_DIR
								+ "/BugDoctor/pp-data";
						strict.ca.usask.cs.srlab.strict.config.StaticData.SAMURAI_DIR = StaticData.HOME_DIR
								+ "/BugDoctor/samurai-data";

						SearchTermProvider stProvider = new SearchTermProvider(reporName, bugID, title, bugReport);
						String bestQuery = stProvider.deliverBestQuery(entCalc);
						System.out.println(bestQuery); */
						String bestQuery="5653 view checked click debugging Add Debugger post catch exception default Bug DCR Debug ger uncaught";
						// now populate these keywords
						ArrayList<String> keywords = qd.utility.MiscUtility.str2List(bestQuery);
						ArrayList<Result> suggestedKeywords = new ArrayList<Result>();
						for (int index = 1; index < keywords.size(); index++) {
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
		
		Composite composite=new Composite(divider, SWT.NONE);
		GridLayout tableGirdLayout = makeGridLayout(1);
		composite.setLayout(tableGirdLayout);
		
		// adding the keyword suggestion panel
		final Button makeQueryButton = new Button(composite, SWT.PUSH);
		GridData makeQueryLayoutData = new GridData(SWT.CENTER, SWT.TOP, true, false);
		makeQueryButton.setText("Make Query");
		makeQueryButton.setSize(250, 25);
		makeQueryButton.setFont(font1);
		makeQueryButton.setImage(getQueryImage());
		makeQueryButton.setLayoutData(makeQueryLayoutData);
		makeQueryButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				final TableItem [] items = viewer.getTable().getItems();
				ArrayList<String> keywordList=new ArrayList<>();
			    for (int i = 0; i < items.length; ++i) {
			      if (items[i].getChecked()) {
			    	  keywordList.add(items[i].getText());
			      }
			    }
			    //now update the query box
			    input.setText(MiscUtility.list2Str(keywordList));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		

		GridData resultGridLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer = CheckboxTableViewer.newCheckList(composite, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final Table table = viewer.getTable();
		table.setLayout(tableGirdLayout);
		table.setLayoutData(resultGridLayoutData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		String[] columns = { "Suggested Keyword", "Relevance" };
		int[] colWidth = { 200, 200 };
		int[] colAlignment = { SWT.LEFT, SWT.LEFT };
		for (int i = 0; i < columns.length; i++) {
			// stored for sorting
			// final int columnNum = i;
			TableColumn col = new TableColumn(table, colAlignment[i]);
			col.setText(columns[i]);
			col.setWidth(colWidth[i]);
		}

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());

		// adding the paint item
		setItemHeight(table);
		setPaintItem(table);
		setKeyEventItems(table);
	}

	protected void populateResultsToIDE(ArrayList<Result> results) {
		// code for populating results to IDE
		try {
			ViewContentProvider viewContentProvider = new ViewContentProvider(results);
			this.viewer.setContentProvider(viewContentProvider);
		} catch (Exception exc) {
			// handle the exception
		}
	}

	protected void addCommandPanel(SashForm divider) {
		// now add the search button
		final Composite cmdPanel = new Composite(divider, SWT.NONE);
		GridLayout cmdGridLayout = makeGridLayout(1);
		cmdPanel.setLayout(cmdGridLayout);

		GridData cmdGridLayoutData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		cmdPanel.setLayoutData(cmdGridLayoutData);

		final Button searchButton = new Button(cmdPanel, SWT.PUSH);
		searchButton.setText("Show Buggy Code");
		searchButton.setSize(150, 25);
		searchButton.setFont(font1);
		searchButton.setImage(get_search_image());
		searchButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				final String codeSearchQuery = getCodeSearchQuery();
				// setting the query
				if (!codeSearchQuery.isEmpty()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							long start = System.currentTimeMillis();
							// TODO Auto-generated method stub
							input.setText(codeSearchQuery);

						}
					});
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		final Button searchButtonTopK = new Button(cmdPanel, SWT.PUSH);
		searchButtonTopK.setText("Show Top-K Entities");
		searchButtonTopK.setSize(150, 25);
		searchButtonTopK.setImage(get_search_image());
		searchButtonTopK.setFont(font1);
		searchButtonTopK.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

				// populate the already collected results
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							// ArrayList<CodeMethod> results = collectedResults;
							System.out.println("Cached results:" + collectedResults.size());
							IWorkbenchPage page = (IWorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage();
							String viewID = "ca.usask.cs.srlab.bugdoctor.views.BugDoctorExampleView";
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);

						} catch (Exception exc) {
							// handle the exception
							exc.printStackTrace();
						}
					}
				});
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// default selection listener
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
		this.addCodeViewer(divider);

		// setting relative weights
		divider.setWeights(new int[] { 2, 2, 1, 2 });
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

			/*
			 * StyleRange style1 = new StyleRange(); style1.font = codeFont;
			 * style1.background = Display.getDefault().getSystemColor( SWT.COLOR_YELLOW);
			 * style1.foreground = Display.getDefault().getSystemColor( SWT.COLOR_MAGENTA);
			 * StyleRange[] styles = new StyleRange[] {style1};
			 * 
			 * //System.out.println("Indices found:"+keyIndices.size() );
			 * 
			 * int[] ranges = new int[keyIndices.size()];
			 * 
			 * for (int index = 0; index < ranges.length; index++) { ranges[index] =
			 * keyIndices.get(index); } codeViewer.setStyleRanges(ranges, styles);
			 */

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

	protected void setPaintItem(Table table) {
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

				} else if (event.index >= 1 && event.index <= 4) {
					GC gc = event.gc;
					int index = event.index;
					TableItem item = (TableItem) event.item;
					int percent = (int) Double.parseDouble(item.getText(index));
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					// gc.setForeground(new Color(null, 11, 59, 23));
					Color myforeground = new Color(null, 11, 97, 11);

					if (index == 1) {
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
		GridLayout gridLayout2 = makeGridLayout(2);
		composite2.setLayout(gridLayout2);

		GridData gridData2 = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite2.setLayoutData(gridData2);

		// associateContext=new Button(composite2, SWT.CHECK);
		// final Button confirm = new Button(composite2, SWT.CHECK);
		// associateContext.setText("Associate context");
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
						codeViewer.setText("");
						bugReportViewer.setText("");
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

	@Override
	public void createPartControl(Composite parent) {

		GridLayout glayout = new GridLayout();
		glayout.marginWidth = 15;
		glayout.marginHeight = 10;
		parent.setLayout(glayout);

		GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayoutData(gdata);

		addBugReportMetaData(parent);
		addSearchPanel(parent);
		addUtilityLayer(parent);
		addResultTable(parent);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "ca.usask.cs.srlab.bugdoctor.viewer");
	}
	

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			Result myresult = (Result) obj;
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

		public Image getColumnImage(Object obj, int index) {
			// return null;
			Image image = null;
			if (index == 0) {
				image = getImage(obj);
			}
			return image;
		}

		public Image getImage(Object obj) {
			return ImageDescriptor.createFromFile(ViewLabelProvider.class, "code.png").createImage();
		}
		
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
}
