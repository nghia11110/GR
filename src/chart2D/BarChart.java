package chart2D;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example to get bounds of bars.
 */
public class BarChart {

    private static double[][] ySeries ;
    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public BarChart(Shell shell,ArrayList<Double> listEnergyOfOneArea) {
        //Display display = new Display();
        
        shell.setText("Energy");
        shell.setSize(600, 500);
        shell.setLayout(new FillLayout());
        
        ySeries = new double[listEnergyOfOneArea.size()][1];
        for(int i=0; i<listEnergyOfOneArea.size(); i++){
        	ySeries[i][0]=listEnergyOfOneArea.get(i);
        }
        createChart(shell);

        shell.open();
        
    }

    /**
     * create the chart.
     * 
     * @param parent
     *            The parent composite
     * @return The created chart
     */
    static public Chart createChart(Composite parent) {

        // create a chart
        final Chart chart = new Chart(parent, SWT.NONE);
        chart.getTitle().setText("Energy");
        
        chart.getAxisSet().getXAxis(0).enableCategory(true);
        chart.getAxisSet().getXAxis(0).setCategorySeries(new String[] {" "});
        chart.getAxisSet().getXAxis(0).getTick().setTickLabelAngle(45);
        // create bar series
       for(int i=0; i<ySeries.length; i++){
	        IBarSeries series = (IBarSeries) chart.getSeriesSet().createSeries(SeriesType.BAR, "Group "+(i+1));
	        series.setYSeries(ySeries[i]);
	        series.setBarColor(Display.getDefault().getSystemColor(i+2));
       }
        // adjust the axis range
        chart.getAxisSet().adjustRange();

        // add mouse move listener to open tooltip on data point
        chart.getPlotArea().addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                for (ISeries series : chart.getSeriesSet().getSeries()) {
                    Rectangle[] rs = ((IBarSeries) series).getBounds();
                    for (int i = 0; i < rs.length; i++) {
                        if (rs[i] != null) {
                            if (rs[i].x < e.x && e.x < rs[i].x + rs[i].width
                                    && rs[i].y < e.y
                                    && e.y < rs[i].y + rs[i].height) {
                                setToolTipText(series, i);
                                return;
                            }
                        }
                    }
                }
                chart.getPlotArea().setToolTipText(null);
            }

            private void setToolTipText(ISeries series, int index) {
                chart.getPlotArea().setToolTipText(
                        "Group: " + series.getId() + "\nEnergy: "
                                + series.getYSeries()[index]);
            }
        });

        return chart;
    }
}
