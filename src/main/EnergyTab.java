package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

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

import chart2D.BarChart;
import chart2D.ChartAllNode;
import chart2D.ChartAllNodeEnergy;
import chart3D.SurfaceChartEnergy;


import parser.*;


class EnergyTab extends Tab implements Observer{
  
  /* The example layout instance */
  FillLayout fillLayout;
  Combo filterByCombo,equalCombo; 
  Button resetButton;
  ArrayList<ArrayList<NodeTrace>> listNodeArea;
  ChartAllNodeEnergy chartAllNodeEnergy;
  ArrayList<Double> listEnergyOfOneArea;
  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  EnergyTab(Analyze instance) {
    super(instance);
    listNodeArea = new ArrayList<ArrayList<NodeTrace>>();
    listEnergyOfOneArea = new ArrayList<Double>(); 
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
	    
	    analyze = new Button(controlGroup, SWT.PUSH);
	    analyze.setText(Analyze.getResourceString("Analyze"));
	    analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    /* Add listener to add an element to the table */
	    analyze.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    	  if(filterByCombo.getSelectionIndex()==0){
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
							    SurfaceChartEnergy.drawChart3D();
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}
														
						}
						resetEditors();
					}
	    	  }
	    	  
	    	  else{
	    		  if(listNodeArea.size() == 0){
	    			  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("Chưa chọn vùng!");
					    dialog.open(); 
	    		  }
	    		  else{
	    			  listEnergyOfOneArea.clear();
	    			  double areaEnergy;
	    			  ArrayList<NodeEnergy> listNodeEnergy= new ArrayList<NodeEnergy>();
		    			  for(int i=0; i<listNodeArea.size(); i++){
		    				  ArrayList<NodeTrace> listNodeOfOneArea = listNodeArea.get(i);
		    				  areaEnergy = 0;
		    				  for(int j=0; j<listNodeOfOneArea.size(); j++){
		    					  listNodeEnergy = TraceFile.listEnergy.get(listNodeOfOneArea.get(j).id);
		    					  areaEnergy += Double.parseDouble(listNodeEnergy.get(0).getEnergy())
		    							       -Double.parseDouble(listNodeEnergy.get(listNodeEnergy.size()-1).getEnergy());
		    				  }
		    				  listEnergyOfOneArea.add(areaEnergy);
		    			  }
		    		  Shell shell = new Shell();	  
	    			  new BarChart(shell,listEnergyOfOneArea);
	    			  
	    		  }
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
		resetButton.setVisible(false);
	  }
	  if(filterByCombo.getSelectionIndex()==1){
		 equalCombo.setItems(new String[] {});
		 super.refreshLayoutComposite();
		 
		 ySeries = new double[TraceFile.getListNodes().size()];
	     xSeries = new double[TraceFile.getListNodes().size()];    
			for(int j=0;j<TraceFile.getListNodes().size();j++) {
				NodeTrace node = TraceFile.getListNodes().get(j);
				xSeries[j]=node.x;
				ySeries[j]=node.y;
			}
		 chartAllNodeEnergy = new ChartAllNodeEnergy(xSeries, ySeries);
		 chartAllNodeEnergy.addObserver(this);
		 chartAllNodeEnergy.createChart(layoutComposite);
		 resetButton.setVisible(true);
	  }
  }
  @Override
  public void update(Observable arg0, Object arg1) {
  	if (arg0 instanceof ChartAllNodeEnergy ) {
          this.listNodeArea=((ChartAllNodeEnergy) arg0).listNodeArea; 
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
    resetButton = new Button(layoutGroup, SWT.PUSH);
    resetButton.setText(Analyze.getResourceString("Reset"));
    resetButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    /*Add listener to button drawChart*/
    resetButton.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    	 listEnergyOfOneArea.clear();
	         chartAllNodeEnergy.listNodeArea.clear();
	         chartAllNodeEnergy.chartAllNode.getPlotArea().redraw();
	      }
	    });
    resetButton.setVisible(false);
    /*Add Layout common*/
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
        
	  }
  /**
   * Sets the state of the layout.
   */
  void setLayoutState() {
    
  }
}

