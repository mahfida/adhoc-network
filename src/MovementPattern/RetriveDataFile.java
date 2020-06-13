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
import java.util.Random;
import java.util.StringTokenizer;
import DTNRouting.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

//*****************************************************************************
//ALLOCATING MOVEMENT PATH TO NODES IN A DATA SET

public class RetriveDataFile
{
    //Instance Variables
    public static double pathDetail[][][];
    Random rand;
    int rows, srows,nodes;
    String filename;

//*****************************************************************************
//CONSTRUCTOR

public RetriveDataFile(String filename, int nodes)
{
        this.filename = filename;
        //Find nearest power of two number
        double r=1,pow2=1;
        while(pow2<nodes)
        {
            pow2=Math.pow(2, r);
            r=r+1;
        }
        srows=(int)pow2*7*4;
        this.rows =nodes*srows;
        this.nodes=nodes;
        rand=new Random(); //For generatig path
        //source,dest, day, hour, presence/chances, x-coord, y-coord
        pathDetail=new double[nodes][srows][6];//nodes,total number of rows for a node
        
}

//*****************************************************************************
//RETRIEVE DATA FROM DATASET AND GENERATE PATH

public void retrivePath() throws IOException
{
    int c=1;
    int token=0,i=0,j=0,k=0;
    boolean pathAssigned[]=new boolean[rows];
    dtnrouting.nodeArray.get(0).nodeDSPath= new int[srows][2];
    Writer output;
    output= new BufferedWriter(new FileWriter("D:\\SA4.txt"));
    try{
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
             if(token==1)        pathDetail[j][k][0]=Double.parseDouble(s);//source
             else if(token==2)   pathDetail[j][k][1]=Double.parseDouble(s);//dest
             else if(token==3)   pathDetail[j][k][2]=Double.parseDouble(s);//day
             else if(token==4)   pathDetail[j][k][3]=Double.parseDouble(s);//hour
             else if(token==5)
             {
                pathDetail[j][k][4]=Double.parseDouble(s);//presence/chances
                dtnrouting.nodeArray.get(j).nodeDSPath[k][0]=rand.nextInt(dtnrouting.RegionArray.get(0).w-dtnrouting.nodeArray.get(0).getRadioRange())+dtnrouting.RegionArray.get(0).x;//x-coord
                dtnrouting.nodeArray.get(j).nodeDSPath[k][1]=rand.nextInt(dtnrouting.RegionArray.get(0).h-dtnrouting.nodeArray.get(0).getRadioRange())+dtnrouting.RegionArray.get(0).y;//y-coord
             }
         }
      i=i+1;
      output.write(strLine+"\t"+dtnrouting.nodeArray.get(j).nodeDSPath[k][0]+"\t"+dtnrouting.nodeArray.get(j).nodeDSPath[k][1]+"\r");
      if(k==(srows-1)) 
      {
          k=0; j=j+1;
          if (j<=(nodes-1))dtnrouting.nodeArray.get(j).nodeDSPath= new int[srows][2];
      }
        else k = k + 1;//next row

      token=0;
    }

    //Close the input stream
    in.close();
    output.close();
    }
    catch (Exception e)
    {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
    }



}//End of method

//*****************************************************************************


}//end of class