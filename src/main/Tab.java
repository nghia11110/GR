package main;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.swtchart.Chart;



abstract class Tab {
  /* Common groups and composites */
  Composite tabFolderPage;

  SashForm sash;

  Group layoutGroup, controlGroup, childGroup;

  /* The composite that contains the example layout */
  Composite layoutComposite;

  /* Common controls for modifying the example layout */
  String[] names;

  Control[] children;

  Button analyze,exportImage;
  Chart chart;

  /* Common values for working with TableEditors */
  Table table;
  double[] ySeries ;
  double[] xSeries;
  int index;

  TableItem newItem, lastSelected;

  Vector data = new Vector();

  /* Controlling instance */
  final Analyze instance;

  /* Listeners */
  SelectionListener selectionListener = new SelectionAdapter() {
    public void widgetSelected(SelectionEvent e) {
      resetEditors();
    }
  };

  TraverseListener traverseListener = new TraverseListener() {
    public void keyTraversed(TraverseEvent e) {
      if (e.detail == SWT.TRAVERSE_RETURN) {
        e.doit = false;
        resetEditors();
      }
    }
  };

  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  Tab(Analyze instance) {
    this.instance = instance;
  }

  /**
   * Creates the "child" group. This is the group that allows you to add
   * children to the layout. It exists within the controlGroup.
   */
  void createChildGroup() {
    childGroup = new Group(controlGroup, SWT.NONE);
    childGroup.setText(Analyze.getResourceString("Results"));
    GridLayout layout = new GridLayout();
    layout.numColumns = 7;
    childGroup.setLayout(layout);
    GridData data = new GridData(GridData.FILL_BOTH);
    data.horizontalSpan = 7;
    childGroup.setLayoutData(data);
    createChildWidgets();
  }

  /**
   * Creates the controls for modifying the "children" table, and the table
   * itself. Subclasses override this method to augment the standard table.
   */
  void createChildWidgets() {
    /* Controls for adding and removing children */
    
    /* Create the "children" table */
    table = new Table(childGroup, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL
        | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
    table.setLinesVisible(true);
    table.setHeaderVisible(true);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 7;
    gridData.heightHint = 150;
    table.setLayoutData(gridData);
    table.addTraverseListener(traverseListener);
    
    /* Add columns to the table */
    String[] columnHeaders = getLayoutDataFieldNames();
    for (int i = 0; i < columnHeaders.length; i++) {
      TableColumn column = new TableColumn(table, SWT.NONE);
      column.setText(columnHeaders[i]);
      if (i == 0)
        column.setWidth(30);
      else if (i == 1 || i==2 || i==3)
        column.setWidth(100);
      else
        column.pack();
    }
    
  }

 
  /**
   * Creates the "control" group. This is the group on the right half of each
   * example tab. It contains controls for adding new children to the
   * layoutComposite, and for modifying the children's layout data.
   */
  void createControlGroup() {
    controlGroup = new Group(sash, SWT.NONE);
    controlGroup.setText(Analyze.getResourceString("Analyze"));
    GridLayout layout = new GridLayout();
    layout.numColumns = 8;
    controlGroup.setLayout(layout);
    createControlWidgets();
  }

  /**
   * Creates the "control" widget children. Subclasses override this method to
   * augment the standard controls created.
   */
  void createControlWidgets() {
	  	
	  createChildGroup();
    
    /* Position the sash */
    sash.setWeights(new int[] { 1, 1 });
    
  }

  /**
   * Creates the example layout. Subclasses override this method.
   */
  void createLayout() {
	  	exportImage = new Button(layoutGroup, SWT.PUSH);
	  	exportImage.setText(Analyze.getResourceString("Export to Image"));
	    exportImage.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    /* export listener  */
	    exportImage.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    	  if(layoutComposite.getChildren().length > 0){
		    	  FileDialog fd = new FileDialog(new Shell(), SWT.SAVE);
		          fd.setText("Save");
		          fd.setFilterPath("D:\\");
		          String[] filterExt = { "*.png" };
		          fd.setFilterExtensions(filterExt);
		          String selected = fd.open();
		         // System.out.println("nghia "+selected);
		          if(selected != null ){
			          GC gc = new GC(layoutComposite.getChildren()[0]);
			    	  Rectangle bounds = layoutComposite.getChildren()[0].getBounds();
			    	  Image image = new Image(layoutComposite.getChildren()[0].getDisplay(), bounds);
			    	  try {
			    	      gc.copyArea(image, 0, 0);
			    	      ImageLoader imageLoader = new ImageLoader();
			    	      imageLoader.data = new ImageData[]{ image.getImageData() };
			    	      imageLoader.save(selected, SWT.IMAGE_PNG);
			    	  } finally {
			    	      image.dispose();
			    	      gc.dispose();
			    	  }
		          }
	    	  }
	    }
	    }); 
  }

  /**
   * Creates the composite that contains the example layout.
   */
  void createLayoutComposite() {
    layoutComposite = new Composite(layoutGroup, SWT.BORDER);
    layoutComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    createLayout();
  }

  /**
   * Creates the layout group. This is the group on the left half of each
   * example tab.
   */
  void createLayoutGroup() {
    layoutGroup = new Group(sash, SWT.NONE);
    layoutGroup.setText(Analyze.getResourceString("Chart"));
    layoutGroup.setLayout(new GridLayout());
    createLayoutComposite();
  }

  /**
   * Creates the tab folder page.
   * 
   * @param tabFolder
   *            org.eclipse.swt.widgets.TabFolder
   * @return the new page for the tab folder
   */
  Composite createTabFolderPage(TabFolder tabFolder) {
    /* Create a two column page with a SashForm */
    tabFolderPage = new Composite(tabFolder, SWT.NULL);
    tabFolderPage.setLayout(new FillLayout());
    sash = new SashForm(tabFolderPage, SWT.HORIZONTAL);

    /* Create the "layout" and "control" columns */
    createLayoutGroup();
    createControlGroup();

    return tabFolderPage;
  }

  

  /**
   * Disposes the editors without placing their contents into the table.
   * Subclasses override this method.
   */
  void disposeEditors() {
  }

  
 

  /**
   * Returns the layout data field names. Subclasses override this method.
   */
  String[] getLayoutDataFieldNames() {
    return new String[] {};
  }

  /**
   * Gets the text for the tab folder item. Subclasses override this method.
   */
  String getTabText() {
    return "";
  }

  /**
   * Refreshes the composite and draws all controls in the layout example.
   */
  void refreshLayoutComposite() {
    /* Remove children that are already laid out */
    children = layoutComposite.getChildren();
    for (int i = 0; i < children.length; i++) {
      children[i].dispose();
    }
  }

  /**
   * Takes information from TableEditors and stores it. Subclasses override
   * this method.
   */
  void resetEditors() {
    resetEditors(false);
  }

  void resetEditors(boolean tab) {
  }

  /**
   * Sets the layout data for the children of the layout. Subclasses override
   * this method.
   */
  void setLayoutData() {
  }

  /**
   * Sets the state of the layout. Subclasses override this method.
   */
  void setLayoutState() {
  }
}
