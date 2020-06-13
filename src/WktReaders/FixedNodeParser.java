/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package WktReaders;
 
import DTNRouting.dtnrouting;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/** 
 *
 * @author STS
 */
public class FixedNodeParser {
public static int[][] fixedNodeArray=new int[100][2];
String strLine;
public static int i=-1;
String pathName;
public FixedNodeParser(){

}
public void Parsing() throws IOException{

    pathName="E:\\Simulator\\Paths\\"+DTNRouting.Setting.mapCity+"Paths\\fixedNode.wkt";
    FileInputStream fstream = new FileInputStream(pathName); // Get the object of DataInputStream
    DataInputStream in = new DataInputStream(fstream);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));

 
    //Read File Line By Line
    while ((strLine = br.readLine()) != null)   {
        StringTokenizer st = new StringTokenizer( strLine,"POINT()-, ");
        while(st.hasMoreTokens()) {
           String key = st.nextToken();                 String val = st.nextToken();
           i=i+1;
           fixedNodeArray[i][0]=((int)(dtnrouting.x_start+Float.parseFloat(key)));
           fixedNodeArray[i][1]=(int)(dtnrouting.y_start+ Float.parseFloat(val));
           

  } }

}
}