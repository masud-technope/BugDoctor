package ca.usask.cs.srlab.bugdoctor.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import ca.usask.cs.srlab.bugdoctor.handlers.ViewContentProviderEx;
import style.JavaLineStyler;
import bugdoctor.core.CodeMethod;

public class BugDoctorExampleView extends ViewPart {

	public TableViewer viewer;
	public static final String ID = "ca.usask.cs.srlab.bugdoctor.views.BugDoctorExampleView";
	GridLayout gridLayout = null;
	StyledText codeViewer = null;
	final int TEXT_MARGIN = 3;
	final Display currDisplay = Display.getCurrent();
	final TextLayout textLayout = new TextLayout(currDisplay);
	Font font1 = new Font(currDisplay, "Arial", 13, SWT.BOLD);
	Font font2 = new Font(currDisplay, "Arial", 10, SWT.NORMAL);
	Font font3 = new Font(currDisplay, "Arial", 10, SWT.NORMAL);
	Font codeFont = new Font(currDisplay, "Courier New", 11, SWT.BOLD);

	Color blue = currDisplay.getSystemColor(SWT.COLOR_BLUE);
	Color green = currDisplay.getSystemColor(SWT.COLOR_DARK_GREEN);
	Color gray = currDisplay.getSystemColor(SWT.COLOR_DARK_GRAY);
	Color selected = currDisplay.getSystemColor(SWT.COLOR_YELLOW);
	Color textColor = currDisplay.getSystemColor(SWT.COLOR_BLACK);

	TextStyle style1 = new TextStyle(font1, green, null);
	TextStyle style2 = new TextStyle(font2, gray, null);
	TextStyle style3 = new TextStyle(font3, blue, null);
	TextStyle textStyle = new TextStyle(codeFont, textColor, null);

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = 15;
		glayout.marginHeight = 10;
		parent.setLayout(glayout);

		GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayoutData(gdata);
		// adding the result table
		this.addResultTable(parent);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "ca.usask.cs.srlab.bugdoctor.viewer");
	}

	@SuppressWarnings("deprecation")
	protected void addResultTable(Composite parent) {
		// adding the result table
		// result panel display
		
		gridLayout = new GridLayout(3, false);
		//gridLayout.marginWidth = 0;
		//gridLayout.marginHeight = 10;
		//gridLayout.verticalSpacing = 5;
		//gridLayout.horizontalSpacing = 5;
		

		final Composite composite3 = new Composite(parent, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 3;
		gridLayout.horizontalSpacing = 0;
		composite3.setLayout(gridLayout2);

		GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite3.setLayoutData(gridData2); 

		final SashForm divider = new SashForm(composite3, SWT.HORIZONTAL
				| SWT.BORDER);

		divider.setLayout(gridLayout2);
		divider.setLayoutData(gridData2);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer = new TableViewer(divider, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		final Table table = viewer.getTable();
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		String[] columns = { "Query Keyword", "Relevance" };
		int[] colWidth = { 500, 100 };
		int[] colAlignment = { SWT.LEFT, SWT.LEFT };
		for (int i = 0; i < columns.length; i++) {
			// stored for sorting
			// final int columnNum = i;
			TableColumn col = new TableColumn(table, colAlignment[i]);
			col.setText(columns[i]);
			col.setWidth(colWidth[i]);
		}

		// adding the content and label provider
		viewer.setContentProvider(new ViewContentProviderEx());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());
		
		//add double click listener
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub
				IStructuredSelection selection=(IStructuredSelection) event.getSelection();
				CodeMethod codeMethod=(CodeMethod) selection.getFirstElement();
				codeViewer.setText(codeMethod.methodBody);
			}
		});
		
		// now add the code viewer
		// Composite composite4=new Composite(divider, SWT.NONE);
		codeViewer = new StyledText(divider, SWT.BORDER | SWT.READ_ONLY
				| SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		codeViewer.setFont(new Font(divider.getDisplay(), "Courier New", 10,
				SWT.NORMAL));
		codeViewer.addLineStyleListener(new JavaLineStyler());
		codeViewer.setMargins(0, 10, 0, 0);

		// adding the paint item
		setItemHeight(table);
		setPaintItem(table);
		// setKeyEventItems(table);
		// setting relative weights
		// divider.setWeights(new int[] { 2, 1, 2 });
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
					//System.out.println(title);
					textLayout.setText(title);
					textLayout.setStyle(textStyle, 0, title.length());
					textLayout.draw(event.gc, event.x, event.y);

				} else if (event.index >= 1 && event.index <= 3) {
					GC gc = event.gc;
					int index = event.index;
					TableItem item = (TableItem) event.item;
					int percent = (int) Double.parseDouble(item.getText(index));
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					// gc.setForeground(new Color(null, 11, 59, 23));
					Color myforeground = new Color(null, 11, 97, 11);

					if (index == 1) {
						myforeground = new Color(null, 11, 97, 11);
					}

					gc.setForeground(myforeground);
					gc.setBackground(new Color(null, 255, 255, 255));

					int col2Width = 100;
					int width = (col2Width - 1) * percent / 100;
					int height = 25;
					// gc.fillRectangle(event.x, event.y + 10, width,
					// height);
					gc.fillGradientRectangle(event.x, event.y, width, height,
							false);
					Rectangle rect2 = new Rectangle(event.x, event.y,
							width - 1, height - 1);
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

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			CodeMethod myresult = (CodeMethod) obj;
			switch (index) {
			case 0:
				if (myresult.keywords != null)
					return myresult.keywords;
				return "";

			case 1:
				if (myresult.totalRelevanceScore > 0)
					return String.format("%.2f",
							myresult.totalRelevanceScore * 100);
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
			return ImageDescriptor.createFromFile(ViewLabelProvider.class,
					"code.png").createImage();
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
