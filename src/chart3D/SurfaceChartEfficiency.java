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

public class SurfaceChartEfficiency {
	public static void createData() throws IOException{
		//TraceFile.ConvertTraceFile();
		double totalDrop[]=new double[TraceFile.getListNodes().size()];
        for(int i=0;i<TraceFile.getListPacket().size();i++){
        	Packet packet = TraceFile.getListPacket().get(i);
        	if(!packet.isSuccess)
        		totalDrop[Integer.parseInt(packet.sourceID)]++;
        }
        
		FileOutputStream fos= new FileOutputStream("DataEfficiency",false);
        PrintWriter pw= new PrintWriter(fos);
        for(int i=0;i<totalDrop.length;i++){
        	NodeTrace node = TraceFile.getListNodes().get(i);
        	if (totalDrop[i] !=0)
        		pw.println(node.x+" "+node.y+" "+totalDrop[i]);
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
	  
	        BufferedReader br = new BufferedReader(new FileReader("GnuplotEfficiency"));
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
