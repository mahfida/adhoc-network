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
public class PedestrianParser {
  public static int[][][] padArray=new int[100][100][2];
  public static int i=-1;
  int j;
  boolean isPosition_X=true;
  String strLine,pathName;
  public PedestrianParser(){

   }
   public void Parsing() throws FileNotFoundException, IOException{
   //pathName="E:\\Simulator\\Paths\\"+DTNRouting.Setting.mapCity+"Paths\\Padestrian.wkt";
   pathName="pedestrian_paths.wkt";
   FileInputStream fstream = new FileInputStream(pathName); // Get the object of DataInputStream
   DataInputStream in = new DataInputStream(fstream);
   BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while ((strLine = br.readLine())!= null)   {
               StringTokenizer st = new StringTokenizer( strLine,"(),- ");
               while(st.hasMoreTokens())
               {
                  String str = st.nextToken();
                  //Once a LINESTRING is entered
                  if(!str.equals("LINESTRING"))
                    {
                        if(isPosition_X==true)
                        {
                            j++;
                            padArray[i][j][0]=(int) (dtnrouting.x_start+Math.round(Double.parseDouble(str)));
                            isPosition_X=false;

                        }

                        else if(isPosition_X==false)
                        {
                          padArray[i][j][1]=(int)(dtnrouting.y_start+Math.round(Double.parseDouble(str)));
                          isPosition_X=true;

                        }

                    }

                  //for new LINESTRING
                    else
                    {
                        i++;
                        j=-1;

                    }

            }
        }
   }
   }






