  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package WktReaders;

import DTNRouting.dtnrouting;
import java.io.*;
import java.util.StringTokenizer;

/**
 *
 * @author STS
 */
public class TrainParser {
   public static int[][][] trainArray=new int[100][100][2];
   public static int i=-1;
   public static int j=-1;
   boolean isPosition_X=true;
   String strLine,pathName;
  public TrainParser(){
        
   }
   public void Parsing() throws FileNotFoundException, IOException{
       pathName="E:\\Simulator\\Paths\\"+DTNRouting.Setting.mapCity+"Paths\\Train.wkt";
       FileInputStream fstream = new FileInputStream(pathName); // Get the object of DataInputStream
       DataInputStream in = new DataInputStream(fstream);
       BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while ((strLine = br.readLine())!= null)
        {
                 StringTokenizer st = new StringTokenizer( strLine,"(),- "); 
                 while(st.hasMoreTokens())
                 {
                         String str = st.nextToken();
                         if(!str.equals("LINESTRING"))
                        {
                            if(isPosition_X==true)
                            {
                                j=j+1;
                                trainArray[i][j][0]=(int)(dtnrouting.x_start+Math.round(Double.parseDouble(str)));
                                isPosition_X=false;
                            }
                            else if(isPosition_X==false)
                            {
                               trainArray[i][j][1]=(int) (dtnrouting.y_start+Double.parseDouble(str));
                               isPosition_X=true;
                            }

                          }
                         else
                         {
                           i++;    //outermost loop incrementer
                           j=-1;   //inner loop incrementer
                         }
                      

                   }
                
        }
     
                 
   }
}


   
       
    


