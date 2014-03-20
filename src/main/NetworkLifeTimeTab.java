package main;

import java.io.IOException;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

import com.ibm.icu.text.DecimalFormat;

import parser.*;

class NetworkLifeTimeTab extends Tab {
  
  /* The example layout instance */
  FillLayout fillLayout;
  Text deadPercentText,lifeTimeText,energyNodeDeadText;

  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  NetworkLifeTimeTab(Analyze instance) {
    super(instance);
  }

  /**
   * Creates the widgets in the "child" group.
   */
  void createChildWidgets() {
    /* Add common controls */
    super.createChildWidgets();

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 1;
    
    Label lblE = new Label(childGroup, SWT.NONE);
	lblE.setText("Life time =");
	lblE.setLayoutData(gridData);
	lifeTimeText = new Text(childGroup, SWT.BORDER);
	lifeTimeText.setEditable(false);
	lifeTimeText.setSize(40, 40);
	//lifeTimeText.setLayoutData(gridData);
	Label lblTime = new Label(childGroup, SWT.NONE);
	lblTime.setText("(s)");
	lblTime.setLayoutData(gridData);
	
	
  }

  /**
   * Creates the control widgets.
   */
  void createControlWidgets() {
    /* Controls the type of Throughput */
	   // GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
	   
	  	Label energyNodeDeadLabel=new Label(controlGroup, SWT.None);
	  	energyNodeDeadLabel.setText(Analyze.getResourceString("Energy of node dead = "));
	  	energyNodeDeadLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	  	energyNodeDeadText = new Text(controlGroup, SWT.BORDER);
	  	energyNodeDeadText.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	  	Label deadPercentLabel=new Label(controlGroup, SWT.None);
	    deadPercentLabel.setText(Analyze.getResourceString("Deadth Percent = "));
	    deadPercentLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    deadPercentText = new Text(controlGroup, SWT.BORDER);
	    deadPercentText.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    Label percentLabel=new Label(controlGroup, SWT.None);
	    percentLabel.setText(Analyze.getResourceString("% "));
	    percentLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    	    	    	    
	    analyze = new Button(controlGroup, SWT.PUSH);
	    analyze.setText(Analyze.getResourceString("Analyze"));
	    analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    /* Analyze listener  */
	    analyze.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    		//lifeTimeText.setText(deadPercentText.getText());
	    	  int percentNodeDead;
	    	  table.removeAll();
	    	  int No=1;
	    	  try{
	    		  TraceFile.energyNodeDead=Double.parseDouble(energyNodeDeadText.getText());
		    	  try{
		    		 percentNodeDead= Integer.parseInt(deadPercentText.getText());
		    		 if(percentNodeDead==0 || percentNodeDead>100){
		    			 MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
							dialog.setText("Error");
							dialog.setMessage("Input không hợp lệ! Bạn phải nhập vào ô phần trăm một số >0 và <=100");
						    dialog.open(); 
						    deadPercentText.setText("");
		    		 } 
		    		 else{
		    			 TraceFile.numberNodeDead=TraceFile.getListNodes().size()*percentNodeDead/100;
				    		 //	System.out.println("so not "+TraceFile.numberNodeDead);
				    		 //	System.out.println("% :"+percentNodeDead);
			    		 TraceFile.setNetworkLifeTime();
			    		 lifeTimeText.setText(TraceFile.lifeTime);
			    		 for(String i : TraceFile.listNodeDead.keySet()){
				    		 TableItem tableItem= new TableItem(table, SWT.NONE);
							 tableItem.setText(0,Integer.toString(No++));
							 tableItem.setText(1,i);
							 tableItem.setText(2,TraceFile.listNodeDead.get(i));
			    		 }
		    		 }
		    	  }
		    	  catch(NumberFormatException e1){
		    		  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
						dialog.setText("Error");
						dialog.setMessage("Input không hợp lệ! Bạn phải nhập vào ô phần trăm một số tự nhiên");
					    dialog.open(); 
					    deadPercentText.setText("");
		    	  } catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	  }
	    	  catch(NumberFormatException e2){
	    		  MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage("Input không hợp lệ! Bạn phải nhập vào ô năng lượng một số");
				    dialog.open(); 
				    energyNodeDeadText.setText("");
	    	  }
	      }
	    });    
	   
	    /* Add common controls */
	    super.createControlWidgets();
   
  }
  
  /**
   * Creates the example layout.
   */
  void createLayout() {
    fillLayout = new FillLayout();
    layoutComposite.setLayout(fillLayout);
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
    return new String[] { "No", "NodeID","Dead time" };
  }

  /**
   * Gets the text for the tab folder item.
   */
  String getTabText() {
    return "Network life time";
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
	        
	  }
  /**
   * Sets the state of the layout.
   */
  void setLayoutState() {
    
  }
}


