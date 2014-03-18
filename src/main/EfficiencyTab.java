package main;

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

class EfficiencyTab extends Tab {
  
  /* The example layout instance */
  FillLayout fillLayout;
  Text eText;
  Combo criteriaCombo; 

  /**
   * Creates the Tab within a given instance of LayoutExample.
   */
  EfficiencyTab(Analyze instance) {
    super(instance);
  }

  /**
   * Creates the widgets in the "child" group.
   */
  void createChildWidgets() {
    /* Add common controls */
    super.createChildWidgets();

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    
    Label lblE = new Label(childGroup, SWT.NONE);
	lblE.setText("E=");
	lblE.setLayoutData(gridData);
	eText = new Text(childGroup, SWT.BORDER);
	eText.setEditable(false);
	eText.setLayoutData(gridData);
	
	
  }

  /**
   * Creates the control widgets.
   */
  void createControlWidgets() {
    /* Controls the type of Throughput */
	   // GridData gridData=new GridData(GridData.FILL_HORIZONTAL);
	   
	    Label criteriaLabel=new Label(controlGroup, SWT.None);
	    criteriaLabel.setText(Analyze.getResourceString("Criteria"));
	    criteriaLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    criteriaCombo = new Combo(controlGroup, SWT.READ_ONLY);
	    criteriaCombo.setItems(new String[] {"data bits transmitted/data bit delivered", "control bits transmitted/data bit delivered","control and data packets transmitted/data packet delivered"});
	    criteriaCombo.select(0);
	    	    	    	    
	    analyze = new Button(controlGroup, SWT.PUSH);
	    analyze.setText(Analyze.getResourceString("Analyze"));
	    analyze.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
	    
	    /* Analyze listener  */
	    analyze.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	    		if(criteriaCombo.getSelectionIndex()==0){
	    			DecimalFormat df = new DecimalFormat("0.00");
					String str= df.format(ratioDroppedPacket());
					eText.setText(str+"%");
	    		}
	    		if(criteriaCombo.getSelectionIndex()==1){
	    			//todo
	    		}
	    		if(criteriaCombo.getSelectionIndex()==2){
	    			//todo
	    		}
	      }
	    });    
	   
	    /* Add common controls */
	    super.createControlWidgets();
   
  }
  double ratioDroppedPacket(){
	  double isDroppedPacket=0;
	  for (int i=0;i<TraceFile.getListPacket().size();i++){ 
		  Packet packet=TraceFile.getListPacket().get(i);
		  if(!packet.isSuccess)
			 isDroppedPacket++;
	  }
	  return (isDroppedPacket/TraceFile.getListPacket().size())*100;
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
    return new String[] { "No", "Packet","Total size","isDropped","HeaderSize","PayloadSize" };
  }

  /**
   * Gets the text for the tab folder item.
   */
  String getTabText() {
    return "Efficiency";
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


