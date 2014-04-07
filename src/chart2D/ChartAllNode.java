package chart2D;


import java.util.ArrayList;
import java.util.Observable;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.LineStyle;

import parser.NodeTrace;
import parser.TraceFile;


public class ChartAllNode extends Observable{
  
   double[] ySeries;
   double[] xSeries;
   /* Used to remember location point of mouse down */
   private static double startX;
   private static double startY;
   
   private static double endX;
   private static double endY;

   private static int startXPos;
   private static int startYPos;

   private static int currentX;
   private static int currentY;

   private static boolean drag = false;
   public ArrayList<NodeTrace> listNodeAreaSource,listNodeAreaDest;
   Chart chartAllNode;
   
   public ChartAllNode(double[] xSeries,double[] ySeries){
	   this.xSeries = xSeries;
	   this.ySeries = ySeries;
   }
    /*
    public static void main(String[] args) throws NumberFormatException, IOException {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Line Chart");
        shell.setSize(500, 400);
        shell.setLayout(new FillLayout());
        TraceFile.ConvertTraceFile();
        
        //Cho vao code chinh
        double[] ySeries = new double[TraceFile.getListNodes().size()];
        double[] xSeries = new double[TraceFile.getListNodes().size()];
        
		for(int i=0;i<TraceFile.getListNodes().size();i++) {
			NodeTrace node = TraceFile.getListNodes().get(i);
			xSeries[i]=node.x;
			ySeries[i]=node.y;
		}
		ChartAllNode chart = new ChartAllNode(xSeries, ySeries);
		chart.createChart(shell);
        //cho vao code chinh
        
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
     */
    /**
     * create the chart.
     * 
     * @param parent
     *            The parent composite
     * @return The created chart
     */
    public void createChart(Composite parent) {
    	listNodeAreaSource = new ArrayList<NodeTrace>();
    	listNodeAreaDest = new ArrayList<NodeTrace>();
   
        // create a chart
        chartAllNode = new Chart(parent, SWT.NONE);
       
        // set titles
        chartAllNode.getTitle().setText("All nodes");
        chartAllNode.getAxisSet().getXAxis(0).getTitle().setText("XAxis");
        chartAllNode.getAxisSet().getYAxis(0).getTitle().setText("YAxis");
        
        // create line series
        ILineSeries lineSeries = (ILineSeries) chartAllNode.getSeriesSet().createSeries(SeriesType.LINE,"Node");
        lineSeries.setYSeries(this.ySeries);
        lineSeries.setXSeries(this.xSeries);
        lineSeries.getLineStyle();
		lineSeries.setLineStyle(LineStyle.NONE);
     //   chart.getAxisSet().getYAxis(0).enableLogScale(true);
        // adjust the axis range
        chartAllNode.getAxisSet().adjustRange();
	    
        MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK);
		dialog.setText("");
		dialog.setMessage("Mời bạn chọn 1 vùng các node");
	    dialog.open(); 
	    
        /* Get the plot area and add the mouse listeners */
        final Composite plotArea = chartAllNode.getPlotArea();

        plotArea.addListener(SWT.MouseDown, new Listener() {

        	@Override
            public void handleEvent(Event event) {
                IAxis xAxis = chartAllNode.getAxisSet().getXAxis(0);
                IAxis yAxis = chartAllNode.getAxisSet().getYAxis(0);

                startX = xAxis.getDataCoordinate(event.x);
                startY = yAxis.getDataCoordinate(event.y);

                startXPos = event.x;
                startYPos = event.y;
               
                 drag = true;
                 if(listNodeAreaDest.size() > 0 && listNodeAreaSource.size() > 0){
                	  listNodeAreaDest.clear();
               		  listNodeAreaSource.clear();
                 }
                	 
            }
        });

        plotArea.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                IAxis xAxis = chartAllNode.getAxisSet().getXAxis(0);
                IAxis yAxis = chartAllNode.getAxisSet().getYAxis(0);

                 endX = xAxis.getDataCoordinate(event.x);
                 endY = yAxis.getDataCoordinate(event.y);
                
                boolean answer = MessageDialog.openQuestion(new Shell(),
                          "Question",
                          "Bạn muốn chọn vùng này?");
                if(answer){
                	if(listNodeAreaSource.size() == 0){
	                	for(int i=0;i<TraceFile.getListNodes().size();i++) {
	            			NodeTrace node = TraceFile.getListNodes().get(i);
	            			if(startX <= node.x+2 && endX >= node.x-2 && startY >= node.y-2 && endY <= node.y+2 )
	            				listNodeAreaSource.add(node);
	            		}
                	}
                	else
                		if(listNodeAreaDest.size() == 0){
                			for(int i=0;i<TraceFile.getListNodes().size();i++) {
    	            			NodeTrace node = TraceFile.getListNodes().get(i);
    	            			if(startX <= node.x+2 && endX >= node.x-2 && startY >= node.y-2 && endY <= node.y+2 )
    	            				listNodeAreaDest.add(node);
    	            		}
                		}
                	setChanged();
                    notifyObservers();
                }
                
               	drag = false;

                plotArea.redraw();
            }
           
        });
        
        plotArea.addListener(SWT.MouseMove, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if(drag)
                {
                    currentX = event.x;
                    currentY = event.y;

                    plotArea.redraw();
                }
            }
        });

        plotArea.addListener(SWT.Paint, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if(drag){
                    GC gc = event.gc;

                    gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
                    gc.setAlpha(128);

                    int minX = Math.min(startXPos, currentX);
                    int minY = Math.min(startYPos, currentY);

                    int maxX = Math.max(startXPos, currentX);
                    int maxY = Math.max(startYPos, currentY);

                    int width = maxX - minX;
                    int height = maxY - minY;

                    gc.fillRectangle(minX, minY, width, height);
                   
                }
                if(listNodeAreaSource.size()>0){
                	GC gc = event.gc;
                    gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
                    gc.setAlpha(128);
              
	                for(int i=0;i<listNodeAreaSource.size();i++){
	                	NodeTrace node = listNodeAreaSource.get(i);
	                	IAxis xAxis = chartAllNode.getAxisSet().getXAxis(0);
	                    IAxis yAxis = chartAllNode.getAxisSet().getYAxis(0);

	                    double x = node.x;
	                    double y = node.y;

	                    ISeries[] series = chartAllNode.getSeriesSet().getSeries();

	                    double closestX = 0;
	                    double closestY = 0;
	                    double minDist = Double.MAX_VALUE;

	                    /* over all series */
	                    for (ISeries serie : series) {
	                        double[] xS = serie.getXSeries();
	                        double[] yS = serie.getYSeries();

	                        /* check all data points */
	                        for (int j = 0; j < xS.length; j++) {
	                            /* compute distance to mouse position */
	                            double newDist = Math.sqrt(Math.pow((x - xS[j]), 2)
	                                    + Math.pow((y - yS[j]), 2));

	                            /* if closer to mouse, remember */
	                            if (newDist < minDist) {
	                                minDist = newDist;
	                                closestX = xS[j];
	                                closestY = yS[j];
	                            }
	                        }
	                    }

	                    /* remember closest data point */
	                    int highlightX = xAxis.getPixelCoordinate(closestX);
	                    int highlightY = yAxis.getPixelCoordinate(closestY);
	                    gc.fillOval(highlightX - 5, highlightY - 5, 10, 10);
	                }
	                
                }
                if(listNodeAreaDest.size()>0){
                	GC gc = event.gc;
                    gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
                    gc.setAlpha(128);
              
	                for(int i=0;i<listNodeAreaDest.size();i++){
	                	NodeTrace node = listNodeAreaDest.get(i);
	                	IAxis xAxis = chartAllNode.getAxisSet().getXAxis(0);
	                    IAxis yAxis = chartAllNode.getAxisSet().getYAxis(0);

	                    double x = node.x;
	                    double y = node.y;

	                    ISeries[] series = chartAllNode.getSeriesSet().getSeries();

	                    double closestX = 0;
	                    double closestY = 0;
	                    double minDist = Double.MAX_VALUE;

	                    /* over all series */
	                    for (ISeries serie : series) {
	                        double[] xS = serie.getXSeries();
	                        double[] yS = serie.getYSeries();

	                        /* check all data points */
	                        for (int j = 0; j < xS.length; j++) {
	                            /* compute distance to mouse position */
	                            double newDist = Math.sqrt(Math.pow((x - xS[j]), 2)
	                                    + Math.pow((y - yS[j]), 2));

	                            /* if closer to mouse, remember */
	                            if (newDist < minDist) {
	                                minDist = newDist;
	                                closestX = xS[j];
	                                closestY = yS[j];
	                            }
	                        }
	                    }

	                    /* remember closest data point */
	                    int highlightX = xAxis.getPixelCoordinate(closestX);
	                    int highlightY = yAxis.getPixelCoordinate(closestY);
	                    gc.fillOval(highlightX - 5, highlightY - 5, 10, 10);
	                }
	                
                }
                
            }
        });
       
        
        
        plotArea.addListener(SWT.MouseWheel, new Listener() {

            @Override
            public void handleEvent(Event event) {
            	
                    if (event.count > 0) {
                    	chartAllNode.getAxisSet().zoomIn();
                    } else {
                    	chartAllNode.getAxisSet().zoomOut();
                    }
                
                chartAllNode.redraw();
               
            }

        });
        plotArea.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                for (ISeries series : chartAllNode.getSeriesSet().getSeries()) {
                    for (int i = 0; i < series.getYSeries().length; i++) {
                        Point p = series.getPixelCoordinates(i);
                        double distance = Math.sqrt(Math.pow(e.x - p.x, 2)
                                + Math.pow(e.y - p.y, 2));

                        if (distance < ((ILineSeries) series).getSymbolSize()) {
                            setToolTipText(series,i,i,i);
                            return;
                        }
                    }
                }
                chartAllNode.getPlotArea().setToolTipText(null);
               
            }

            private void setToolTipText(ISeries series, int xIndex,int yIndex,int id) {
                chartAllNode.getPlotArea().setToolTipText(
                		"id: " + id + "\nx: " + series.getXSeries()[xIndex] + "\ny: "
                                + series.getYSeries()[yIndex]);
                //chartAllNode.getPlotArea().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            }
        });
        
      // if(listNodeArea.size() >=1 )
       // return listNodeArea;
    }
    
}

