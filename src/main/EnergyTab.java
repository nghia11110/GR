package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

import chart3D.SurfaceChartEnergy;


import parser.*;


class EnergyTab extends Tab {
  
  /* The example layout instance */
  FillLayout fillLayout;

  Combo filterByCombo,equalCombo; 

  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  EnergyTab(Analyze instance) {
    super(instance);
  }

  /**
   * Creates the widgets in the "child" group.
   */
  void createChildWidgets() {
    /* Add common controls */
    super.createChildWidgets();  
    
    }

  /**
   * Creates the control widgets.
   */
  void createControlWidgets() {
    /* Controls the type of Throughput */
	   // GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
	   
	    Label filterByLabel=new Label(controlGroup, SWT.None);
	    filterByLabel.setText(Analyze.getResourceString("Filter by"));
	    filterByLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    filterByCombo = new Combo(controlGroup, SWT.READ_ONLY);
	    filterByCombo.setItems(new String[] {"Node ID", "Label ID"});
	    filterByCombo.select(0);
	    /* Add listener */
	    filterByCombo.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent e) {
	          //System.out.println(filterByCombo.getSelectionIndex());
	        	setItemEqualCombo();
	        }
	        public void widgetDefaultSelected(SelectionEvent e) {
	         // System.out.println("nghia");
	        }
	      });
	    
	    Label equalLabel=new Label(controlGroup, SWT.None);
	    equalLabel.setText(Analyze.getResourceString("equals to"));
	    equalLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    equalCombo = new Combo(controlGroup, SWT.READ_ONLY);	   
	    setItemEqualCombo();
	    
	    final Button drawChart3D = new Button(layoutGroup, SWT.PUSH);
	    drawChart3D.setText(Analyze.getResourceString("Draw 3Dchart"));
	    drawChart3D.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    analyze = new Button(controlGroup, SWT.PUSH);
	    analyze.setText(Analyze.getResourceString("Analyze"));
	    analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    /* Add listener to add an element to the table */
	    analyze.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    		if(equalCombo.getSelectionIndex()==-1 ){
					MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
							dialog.setText("Error");
							dialog.setMessage("Bạn phải chọn node muốn phân tích năng lượng!");
						    dialog.open(); 
				}
				else{	
					table.removeAll();
					int No=1;
					//System.out.println(equalCombo.getItem(equalCombo.getSelectionIndex()));
					ArrayList<NodeEnergy> listNodeEnergy= new ArrayList<NodeEnergy>();
					
					if(!equalCombo.getItem(equalCombo.getSelectionIndex()).equals("All nodes")){
						/*
						  try {
						 
							listNodeEnergy=TraceFile.getNodeEnergy(TraceFile.getListNodes().get(Integer.parseInt(equalCombo.getItem(equalCombo.getSelectionIndex()))));
						} catch (NumberFormatException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
							for(int i=0;i<listNodeEnergy.size();i++){ 
								NodeEnergy node=listNodeEnergy.get(i);
								 TableItem tableItem= new TableItem(table, SWT.NONE);
								 tableItem.setText(0,Integer.toString(No++));
								 tableItem.setText(1,equalCombo.getItem(equalCombo.getSelectionIndex()));
								 tableItem.setText(3,node.getTime());
								 tableItem.setText(4,node.getEnergy());
							}
						*/	 
						
							listNodeEnergy =TraceFile.listEnergy.get(Integer.parseInt(equalCombo.getItem(equalCombo.getSelectionIndex())));
							for(int i=0;i<listNodeEnergy.size();i++){ 
								NodeEnergy node=listNodeEnergy.get(i);
								 TableItem tableItem= new TableItem(table, SWT.NONE);
								 tableItem.setText(0,Integer.toString(No++));
								 tableItem.setText(1,equalCombo.getItem(equalCombo.getSelectionIndex()));
								 tableItem.setText(3,node.getTime());
								 tableItem.setText(4,node.getEnergy());
							}
							
						
						//init line chart
						initXYseries(listNodeEnergy);
					}
					else{
						FileOutputStream fos;
						try {
							fos = new FileOutputStream("DataEnergy",false);
							PrintWriter pw= new PrintWriter(fos);
							for(int i=0;i<TraceFile.listEnergy.size();i++){ 
								listNodeEnergy =TraceFile.listEnergy.get(i);
								 TableItem tableItem= new TableItem(table, SWT.NONE);
								 tableItem.setText(0,Integer.toString(No++));
								 tableItem.setText(1,Integer.toString(i));
								 tableItem.setText(3,Double.toString(Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getTime())-Double.parseDouble(listNodeEnergy.get(0).getTime())));
								 tableItem.setText(4,Double.toString(Double.parseDouble(listNodeEnergy.get(0).getEnergy())-Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getEnergy())));
								 xSeries=new double[0];
								 ySeries=new double[0];
								 
								 /*init dataEnergy*/
								 
						      pw.println(TraceFile.getListNodes().get(i).x+" "+TraceFile.getListNodes().get(i).y+" "+Double.toString(Double.parseDouble(listNodeEnergy.get(0).getEnergy())-Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getEnergy())));	        						    							
							}
						    pw.close();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						
						/*Add listener to button drawChart*/
					     drawChart3D.addSelectionListener(new SelectionAdapter() {
						      public void widgetSelected(SelectionEvent e) {
						       SurfaceChartEnergy.drawChart3D();
						      }
						    });
					}
					resetEditors();
				}
	        
	      }
	    });    
	   
	    /* Add common controls */
	    super.createControlWidgets();

   
  }
  /* Set up item for equalCombo */
  void setItemEqualCombo(){
	  int i;
	  if(filterByCombo.getSelectionIndex()==0){
		  String[] itemList=new String[TraceFile.getListNodes().size()+1] ; 
			if(TraceFile.getListNodes().size()>0)
			{
				itemList[0]="All nodes";
				for (i=0;i<TraceFile.getListNodes().size();i++){ 
					 NodeTrace node=TraceFile.getListNodes().get(i);
					 itemList[i+1]=Integer.toString(node.id);
				}
				equalCombo.setItems(itemList);
			}
	  }
	  if(filterByCombo.getSelectionIndex()==1){
		 equalCombo.setItems(new String[] {});
	  }
  }

		
	
	public void initXYseries(ArrayList<NodeEnergy> listNodeEnergy){
		int j=0;
		xSeries=new double[listNodeEnergy.size()];
		ySeries=new double[listNodeEnergy.size()];
		if(listNodeEnergy.size()!=0){
			for (int i=0;i<listNodeEnergy.size();i++) {
				NodeEnergy node=listNodeEnergy.get(i);
				ySeries[j]=Double.parseDouble(node.getEnergy());
				xSeries[j]=Double.parseDouble(node.getTime());
				j++;
			}
		}
	}
	
  /**
   * Creates the example layout.
   */
  void createLayout() {
    fillLayout = new FillLayout();
    layoutComposite.setLayout(fillLayout);
    super.createLayout();
  }

  /**
   * Disposes the editors without placing their contents into the table.
   */
  void disposeEditors() {
    
  }

  /**
   * Returns the layout data field names.
   */
  String[] getLayoutDataFieldNames() {
    return new String[] { "No", "NodeId","Label","Time","Energy" };
  }

  /**
   * Gets the text for the tab folder item.
   */
  String getTabText() {
    return "Energy";
  }

  /**
   * Takes information from TableEditors and stores it.
   */
  void resetEditors() {
    setLayoutState();
    refreshLayoutComposite();
    layoutComposite.layout(true);
    layoutGroup.layout(true);
  }
  void refreshLayoutComposite() {
	    super.refreshLayoutComposite();
	    chart = new Chart(layoutComposite, SWT.NONE);
        chart.getTitle().setText("Energy");
        chart.getAxisSet().getXAxis(0).getTitle().setText("Time(s)");
        chart.getAxisSet().getYAxis(0).getTitle().setText("Remain Energy");
        // create line series
        ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE, "line series");
        lineSeries.setYSeries(ySeries);
        lineSeries.setXSeries(xSeries);
        lineSeries.enableStep(true);
        lineSeries.setSymbolSize(3);
        // adjust the axis range
        chart.getAxisSet().adjustRange();
        /* export listener  */
        exportImage.addSelectionListener(new SelectionAdapter() {
  	      public void widgetSelected(SelectionEvent e) {
  	    	  FileDialog fd = new FileDialog(new Shell(), SWT.SAVE);
  	          fd.setText("Save");
  	          fd.setFilterPath("D:\\");
  	          String[] filterExt = { "*.png" };
  	          fd.setFilterExtensions(filterExt);
  	          String selected = fd.open();
  	         // System.out.println("nghia "+selected);
  	          if(selected != null){
  		          GC gc = new GC(chart);
  		    	  Rectangle bounds = chart.getBounds();
  		    	  Image image = new Image(chart.getDisplay(), bounds);
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
  	    }); 
	  }
  /**
   * Sets the state of the layout.
   */
  void setLayoutState() {
    
  }
}

