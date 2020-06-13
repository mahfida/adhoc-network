//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import MovementPattern.SelectDay;
import java.util.Iterator;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

//******************************************************************************
//COMMUITY BASED HEURISTIC ROUTING PROTOCOL

//******************************************************************************
//ENCOUNTER CLASS:USED IN CHRP CLASS
/*class Encounter
{

    int destination;//dest node
    int day;//week day
    int quarter;//quarter of the day
    double encounterProb;//Chances of encounter
    //CONSTRUCTOR
    Encounter(int destination,int day,int quarter, double encounterProb )
    {

        this.destination=destination;
        this.day=day;
        this.quarter=quarter;
        this.encounterProb=encounterProb;
    }
   public int getDestination()
   {return(destination);}
   public int getDay()
   {return(day);}
   public int getQuarter()
   {return(quarter);}
   public double getProbability()
   {return(encounterProb);}
}//end of class

 */
//******************************************************************************

//******************************************************************************
//ROUTING CLASS
public class DummyCHRP extends RoutingProtocol
{
public static int DAY=0; //The day when message is to be routed
public static int QUARTER=0;//current quarter of the day
public static int ROW_START=0;//the current row in temporal community
public static int ROW_END=0;//the last row in temporal community of current quarter
int SIy=0; //social indicator of the encountered node
Double [][][][] TemporalCommunity;
ArrayList<Integer>[] FriendList = null; //[TotalNodes][MaximumPossibleNumberofFriends]
ArrayList<Integer>[][] DayFriends=null;//Friends on a particular day
ArrayList<Integer>[][] AcquaintanceTable = null;//[TotalNodes][MaximumPossibleNumberofAcuaintedList]
ArrayList<Integer>[][][] DayAcquaintanceTable = null;//[TotalNodes][MaximumPossibleNumberofAcuaintedList][DAY]
HashMap<Integer,Integer>[] SocialIndicator=null;
HashMap<Integer,Integer>[][] DaySocialIndicator=null;

//******************************************************************************
//CONSTRUCTOR
public DummyCHRP() {}

//******************************************************************************
    @Override
public void setPerimeters(String filename,int size)
{
    DAY=SelectDay.getDay(UpdateInformation.Sim);
    TemporalCommunity=new Double[size][7][4][size];//[sources][Day][quarter][destination];
    SocialIndicator=new HashMap[size];
    DaySocialIndicator=new HashMap[size][7];
    FriendList=new ArrayList[size];
    DayFriends=new ArrayList[size][7];
    AcquaintanceTable=new ArrayList[size][size];
    DayAcquaintanceTable=new ArrayList[size][size][7];

    for(int i=0;i<size;i++)
    {
       // TemporalCommunity[i]=new ArrayList<Integer>();
        FriendList[i]=new ArrayList();

        SocialIndicator[i]=new HashMap();
        for(int j=0;j<size;j++)
        {
        AcquaintanceTable[i][j]=new ArrayList();
        for(int k=0;k<7;k++)
            DayAcquaintanceTable[i][j][k]=new ArrayList();
        }
        for(int k=0;k<7;k++)
        {
            DayFriends[i][k] = new ArrayList();
            DaySocialIndicator[i][k]=new HashMap();
        }

    }
    fillDataStructures(filename,size);

}
//******************************************************************************

public void Deliver(Node nx,Node ny)    //x and y are intermediet sender and reciever
{
    //The quater of day
    QUARTER=(int)Math.ceil((nx.x_inc+1)/(nx.x_max/4.0))-1;
   // if(nx.nodeID==1 || ny.nodeID==1)
   // System.out.println(nx.nodeID+" "+ny.nodeID+ " "+(QUARTER+1));
   // ROW_START=DAY*nx.x_max+nx.x_inc; //
   // ROW_END=QUARTER*(int)(nx.x_max/4.0);
    //Bidirectional connectivity
    DeliverMessage(nx, ny);
    DeliverMessage(ny, nx);
}

//******************************************************************************
//DELIVER MESSAGE

public void DeliverMessage(Node nx, Node ny)
{
  if(dtnrouting.isRun==true )
  {
   //if nx has bundle and ny has to recieve it
   if(!nx.DestNBundle.isEmpty())
   {
   nx.updateBundleTimestamp(nx);

   for (Iterator<Map.Entry<Bundle,Node>> i = nx.DestNBundle.entrySet().iterator(); i.hasNext(); )
    {
       Map.Entry<Bundle,Node> entry = i.next();
       Bundle bundleObj = entry.getKey();
       Node   destNode = entry.getValue();

       //If destiantion has not enough size to recieve bundle
        //OR if its TTL is expired, , it bundle cannot be sent

         if(checkTTLandSize(nx,ny,destNode,bundleObj)==true);

        //If destiantion has enough size to recieve bundle
        //and if its TTL is not expired, , it bundle can be sent
        // if contact duration is enough to transfer the message
        else
        if(bundleObj.bundleSize<=dtnrouting.contactDuration[nx.nodeID-1][ny.nodeID-1] && !ny.bundleIDHash.contains(bundleObj.bundleName))
        {
        //If encountered Node is destination and bundle is yet not delivered,in ny's buffer enough space is free to occupy the bundle and bundle TTL is not expired
        if((bundleObj.isBundleDelivered==false)&&(ny.queueSizeLeft-bundleObj.bundleSize>=0)&&(bundleObj.bundleTTL>0))
        {
            //if current encounter node is destination
           if(destNode==ny)
           {
               ny.DestNBundle.put(bundleObj,null);
               deliverBundle(nx,ny,bundleObj);
               bundleObj.isBundleDelivered=true;
               System.out.println(bundleObj.bundleName);
               i.remove();
           }

         //if destination is friend of both nodes
         else if(DayFriends[nx.nodeID-1][DAY].contains(destNode.nodeID) && DayFriends[ny.nodeID-1][DAY].contains(destNode.nodeID))//DayAcquaintanceTable[nx.nodeID-1][destNode.nodeID-1][DAY].contains(ny.nodeID))
         {
          /* Chances of both nodes to encounter destination in current time slot*/
          double nxProb=0.0,nyProb=0.0;

          //Finding probability of encounter with destination in current time slot
          nxProb = TemporalCommunity[nx.nodeID - 1][DAY][QUARTER][destNode.nodeID-1];
          // System.out.println("nx.nodeID "+nx.nodeID+" dest "+destNode.nodeID+ " nxProb"+nxProb);

          //Finding probability of encounter with destination in current time slot
          nyProb = TemporalCommunity[ny.nodeID - 1][DAY][QUARTER][destNode.nodeID-1];
          //System.out.println("nx.nodeID "+ny.nodeID+" dest "+destNode.nodeID+ " nyProb"+nxProb);

          //If probabitl of encounter of ny is higher with destination
              if(nxProb<nyProb){
                ny.DestNBundle.put(bundleObj,destNode);
                deliverBundle(nx,ny,bundleObj);
                i.remove();}
          }
         else if(!DayFriends[nx.nodeID - 1][DAY].contains(destNode.nodeID))
         {
               //if destNode is  not in friend list of souce then msg is handed over to encountered Node if destination is its friend

           if(DayAcquaintanceTable[nx.nodeID - 1][destNode.nodeID - 1][DAY].contains(ny.nodeID))
           {
                ny.DestNBundle.put(bundleObj,destNode);
                deliverBundle(nx,ny,bundleObj);
                i.remove();
           }

           else if (DayAcquaintanceTable[nx.nodeID - 1][destNode.nodeID - 1][DAY].isEmpty())
           {
             //generate multiple copies of the message if destination is not known
             if(bundleObj.bundleLoad==1 && nx==bundleObj.sourceNode)
                generateCopies(nx,bundleObj);

             //Fin SI of the node ny
             SIy=DaySocialIndicator[nx.nodeID-1][DAY].get(ny.nodeID);

             //Hand over a copy to highe SI node
             if(nx==bundleObj.sourceNode && SIy>3)
                {
                int bcopy=nx.bundleCopies.get(bundleObj.bundleName);
                ny.bundleCopies.put(bundleObj.bundleName,1);
                nx.bundleCopies.put(bundleObj.bundleName, bcopy-1);
                //System.out.println(ny.nodeID+" is social than"+ nx.nodeID);
                 deliverBundle(nx, ny, destNode, bundleObj);
                if(bcopy==1) i.remove();
                }
          }
         }

          }

         }
    }
  }
//Chech whether forwading of bundles have ended
checkForwardingEnds();
}
}//end of method name

//******************************************************************************
@Override

public void deliverBundle(Node nx, Node ny, Bundle bundleObj)
   {
        bundleObj.bundleBandwidth+=1; //Since bundle is transfered
        ny.bundleIDHash.add(bundleObj.bundleName);
        ny.bundleTimeSlots.put(bundleObj.bundleName, 0);
        ny.bundleCopies.put(bundleObj.bundleName,1);
        ny.queueSizeLeft-=bundleObj.bundleSize;
        dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
         //update nx
        nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space            nx.bundleIDHash.remove(bundleObj.bundleName);
        nx.bundleTimeSlots.remove(bundleObj.bundleName);
        nx.bundleCopies.remove(bundleObj.bundleName);
   }

//******************************************************************************
//******************************************************************************
//DELIVER BUNDLE

public void deliverBundle(Node nx, Node ny, Node destNode,Bundle bundleObj)
{
    bundleObj.bundleBandwidth+=1;
    ny.DestNBundle.put(bundleObj,destNode);
    ny.bundleIDHash.add(bundleObj.bundleName);
    ny.bundleTimeSlots.put(bundleObj.bundleName,0);
    ny.bundleCopies.put(bundleObj.bundleName,1);
    ny.queueSizeLeft-=bundleObj.bundleSize;
    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
     //update nx if it has left no copy of the message
    if(nx.bundleCopies.get(bundleObj.bundleName)==0)
    {
        nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space            nx.bundleIDHash.remove(bundleObj.bundleName);
        nx.bundleTimeSlots.remove(bundleObj.bundleName);
        nx.bundleCopies.remove(bundleObj.bundleName);
    }

}
//******************************************************************************
//FILLING THE DATA STRUCTURES FREIND LIST, ACQ. TABLE AND SOCIAL INDICATOR

public void fillDataStructures(String filename, int size)
{
int token=0,source=0,destination=0,day=0,quarter=0;
double encounterProb=0.0;
//Generation of FriendTable
try{
    FileInputStream fstream1 = new FileInputStream(filename+"Lists.txt");
    DataInputStream in1 = new DataInputStream(fstream1);
    BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));

    FileInputStream fstream2 = new FileInputStream(filename+"Community.txt");
    DataInputStream in2 = new DataInputStream(fstream2);
    BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

    FileInputStream fstream3 = new FileInputStream(filename+"DList.txt");
    DataInputStream in3 = new DataInputStream(fstream3);
    BufferedReader br3= new BufferedReader(new InputStreamReader(in3));

    String strLine1,strLine2,strLine3;
    /////////////READING FIRST FILE///////////////////////////////
        while ((strLine1 = br1.readLine()) != null)
        {
          StringTokenizer st1 = new StringTokenizer( strLine1,"\t");
          String s=st1.nextToken();
          String d=st1.nextToken();
          FriendList[Integer.parseInt(s)-1].add(Integer.parseInt(d));

        }
    //Close the input stream
    in1.close();
    /////////////////READING SECOND FILE///////////////////////////
        while ((strLine2 = br2.readLine()) != null)
        {
          StringTokenizer st2 = new StringTokenizer( strLine2,"\t");
          while(st2.hasMoreTokens())
          {
              token=token+1;
              String s=st2.nextToken();

              if(token==1) source=Integer.parseInt(s);
              else if(token==2)destination = Integer.parseInt(s);
              else if(token==3)day=Integer.parseInt(s);
              else if(token==4)quarter=Integer.parseInt(s);
              else if(token==5)encounterProb=Double.parseDouble(s);

          }
          TemporalCommunity[source-1][day][quarter-1][destination-1]=encounterProb;
          token=0;
        }
    //Close the input stream2
    in2.close();
    /////////////////READING THIRD FILE///////////////////////////
        while ((strLine3 = br3.readLine()) != null)
        {
          StringTokenizer st3 = new StringTokenizer( strLine3,"\t");
          while(st3.hasMoreTokens())
          {
              token=token+1;
              String s=st3.nextToken();

              if(token==1) source=Integer.parseInt(s);
              else if(token==2)destination = Integer.parseInt(s);
              else if(token==3)day=Integer.parseInt(s);

          }
          DayFriends[source-1][day].add(destination);
          token=0;
        }
    //Close the input stream
    in3.close();
    }catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
    }
///////////////////////////////////////////////////////////////////////////////
//Display of Friend List
/*
 System.out.println("\nFRIEND LISTS ");
 for(int i=0;i<size;i++)
   {
        System.out.print("Node"+(i+1)+": ");
        for(int j=0;j<DayFriends[i][1].size();j++)
          System.out.print(DayFriends[i][1].get(j)+" ");
         System.out.println("");
   }
 */
////////////////////////////////////////////////////////////////////////////////
//Generation of Acquaintance Table

    int node=1,list=0;
    for(int i=0;i<size;i++)//all source nodes
        {
        node=1;// Intial node is 1
        while(node<size)
        {
           boolean flag=true;
           if (((i + 1) == node)|| (FriendList[i].contains(node)))//Do not count this node
           {
                 flag = false;
           }
           //////////////////////////////////////////////////////////
           if(flag)//If is the node is neither this nor friend node
           {
               for(int j=0;j<size;j++)
                   if(FriendList[i].contains((j+1)))
                    {

                       if(FriendList[j].contains(node))
                      {
                          AcquaintanceTable[i][node-1].add(j+1);
                      }
                    }
           }
            /////////////////////////////////////////////////////////////
              node=node+1;
         } //End of while loop

     }
///////////////////////////////////////////////////////////////////////////////
    //Generation of Day Acquaintance Table
   node=0; list=0;
    for(int i=0;i<size;i++)//all source nodes
    {
        for(int j=0;j<7;j++)//for each day
        {
        node=1;// Intial node is 1
        while(node<size)
        {
           boolean flag=true;
           if (((i + 1) == node)|| (DayFriends[i][j].contains(node)))//Do not count this node
           {
                 flag = false;
           }
           //////////////////////////////////////////////////////////
           if(flag)//If is the node is neither this nor friend node
           {
               for(int k=0;k<size;k++)
                   if(DayFriends[i][j].contains((k+1)))
                    {

                       if(DayFriends[k][j].contains(node))
                      {
                          DayAcquaintanceTable[i][node-1][j].add(k+1);
                      }
                    }
           }
            /////////////////////////////////////////////////////////////
              node=node+1;
         } //End of while loop

     }
    }

///////////////////////////////////////////////////////////////////////////////
//Display of day Acquaintance Table
/*
    System.out.println("\nACQAINTANCE TABLES ");
    for(int i=0;i<size;i++)
    {
        System.out.println("\nACQ. TABLE OF NODE  "+(i+1)+": ");
        for(int j=0;j<size;j++)
        {
         if( !DayAcquaintanceTable[i][j][1].isEmpty())
         {
                 System.out.print("Node: "+(j+1)+": ");
                 for(int k=0;k<DayAcquaintanceTable[i][j][1].size();k++)
                    System.out.print(DayAcquaintanceTable[i][j][1].get(k)+" ");
                 System.out.println("");
         }
        }
    }
*/
///////////////////////////////////////////////////////////////////////////////
//Generation of Social Indicator

    int count=0;
    for(int i=0;i<size;i++)
    {
        for(int j=0;j<size;j++)
        {

            for(int k=0;k<size;k++)
            if(AcquaintanceTable[i][k].contains(j+1))
            {
                count = count + 1;
            }

            if(count>0)
            SocialIndicator[i].put(j+1, count); count=0;

        }
    }
///////////////////////////////////////////////////////////////////////////////
    //Generation of Day Social Indicator

    count=0;
    for(int i=0;i<size;i++)//for each node
    {
        for(int j=0;j<size;j++)//for each of the friend nodes
        {
            for(int l=0;l<7;l++)//for each day
            {
                for(int k=0;k<size;k++)//for each acquainted node
                if(DayAcquaintanceTable[i][k][l].contains(j+1))
                {
                    count = count + 1;
                }
                if(count>0)
                DaySocialIndicator[i][l].put(j+1, count); count=0;
            }
           }

    }
//////////////////////////////////////////////////////////////////////////////
//Display of Social Indicator
 /*
  for(int i=0;i<size;i++)
    {
       System.out.println("\n\nSocial TABLE OF NODE  "+(i+1)+": ");
       for ( Map.Entry<Integer,Integer> e : DaySocialIndicator[i][1].entrySet() )
       System.out.print("("+e.getKey()+","+e.getValue()+") ");
       System.out.println();
   }

*/


}//end of method

//******************************************************************************
//Multiple copies generated and spread to high social nodes in case dest is not known
public void generateCopies(Node source,Bundle bundle)
{
    Random rand=new Random();
    int r=rand.nextInt(3)+2;
    bundle.bundleLoad=r;
    source.bundleCopies.put(bundle.bundleName,bundle.bundleLoad );
    dtnrouting.CommentsTA.append("\nCopies Generated by "+source.name+" : "+bundle.bundleLoad);
}//End of method

//******************************************************************************
}// end of class

