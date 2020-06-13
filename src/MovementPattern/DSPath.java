//*****************************************************************************
//PACKAGE NAME

package MovementPattern;

//*****************************************************************************
//IMPORT FILES

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import DTNRouting.*;



//*****************************************************************************
//ALLOCATING MOVEMENT PATH TO NODES IN A DATA SET

public class DSPath
{
    //Instance Variables
    public static double pathDetail[][][];
   
    int rows, srows,sdays,nodes;
    double pow2,r;
    String filename;

//*****************************************************************************
//CONSTRUCTOR

public DSPath(String filename, int nodes)
{
         while(pow2<nodes)
        {
            pow2=Math.pow(2, r);
            r=r+1;
        }
        this.filename = filename;
        srows=(int)pow2*7*4;
        sdays=(int)pow2*4;
        this.rows =nodes*srows;
        this.nodes=nodes;
        //source,dest, day, hour, presence/chances, x-coord, y-coord
        pathDetail=new double[nodes][srows][6];//nodes,total number of rows for a node

}



//*****************************************************************************
//RETRIEVE DATA FROM DATASET AND GENERATE PATH

public void retrivePath() throws IOException
{
    int c=1;
    int token=-1,i=0,j=0,k=0;
    boolean pathAssigned[]=new boolean[rows];
    dtnrouting.nodeArray.get(0).nodeDSPath= new int[srows][2];
    dtnrouting.nodeArray.get(0).x_max=(srows);
    dtnrouting.nodeArray.get(0).y_max=(srows);
    try
    {
        //File to be read
        FileInputStream fstream = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null)
        {
         StringTokenizer st = new StringTokenizer( strLine,"\t");
         String s="";
         while(st.hasMoreTokens())
         {
             token=token+1;
             s=st.nextToken();
             if(token<5)         pathDetail[j][k][token]=Double.parseDouble(s);//source
             else if(token==5)   dtnrouting.nodeArray.get(j).nodeDSPath[k][0]=Integer.parseInt(s);//x-coord
             else if(token==6)   dtnrouting.nodeArray.get(j).nodeDSPath[k][1]=Integer.parseInt(s);//y-coord
         }
         i=i+1;
         if(k==(srows-1))
         {     
              k=0; j=j+1;
              if(j<nodes)
              {
                    dtnrouting.nodeArray.get(j).nodeDSPath= new int[srows][2];
                    dtnrouting.nodeArray.get(j).x_max=srows;
                    dtnrouting.nodeArray.get(j).y_max=srows;
              }
          }
          else k = k + 1;//next row
          token=-1;
    }

    //Close the input stream
    in.close();

    }
    catch (Exception e)
    {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
    }
  

}//End of method
}//End of class
