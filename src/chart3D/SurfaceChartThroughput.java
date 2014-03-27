package chart3D;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import parser.NodeTrace;
import parser.Packet;
import parser.TraceFile;

public class SurfaceChartThroughput {
	
	public static void createData() throws IOException{
		//TraceFile.ConvertTraceFile();
		double totalSize[]=new double[TraceFile.getListNodes().size()];
        double totalTime[]=new double[TraceFile.getListNodes().size()];
        for(int i=0;i<TraceFile.getListPacket().size();i++){
        	Packet packet = TraceFile.getListPacket().get(i);
        	totalSize[Integer.parseInt(packet.sourceID)]+=Double.parseDouble(packet.size);
        	totalTime[Integer.parseInt(packet.sourceID)]+=(Double.parseDouble(packet.endTime)-Double.parseDouble(packet.startTime));
        }
        
		FileOutputStream fos= new FileOutputStream("DataThroughput",false);
        PrintWriter pw= new PrintWriter(fos);
        for(int i=0;i<totalSize.length;i++){
        	NodeTrace node = TraceFile.getListNodes().get(i);
        	if (totalTime[i] !=0)
        		pw.println(node.x+" "+node.y+" "+totalSize[i]/totalTime[i]);
        }
        pw.close();
	}
	public static void drawChart3D(){
		try {
			createData();
	        Runtime rt = Runtime.getRuntime();
	        Process proc = rt.exec("D://hoctap/Do_an/Analyzer_2.0/Analyzer2.0/exe/pgnuplot.exe");

	            java.io.OutputStream opStream = proc.getOutputStream();
	            PrintWriter gp = new PrintWriter(new BufferedWriter(new OutputStreamWriter(opStream)));
	  
	        BufferedReader br = new BufferedReader(new FileReader("GnuplotThroughput"));
	        String line = null;
	        while ((line = br.readLine()) != null)
	        	gp.println(line+"");
	        gp.println("pause mouse close;\n");
	        br.close();
	        gp.close();
	        int exitVal = proc.waitFor();
	        System.out.println("Exited with error code "+exitVal);
	    } catch(Exception e) {
	        System.out.println(e.toString());
	        e.printStackTrace();
	    }
	}
}
