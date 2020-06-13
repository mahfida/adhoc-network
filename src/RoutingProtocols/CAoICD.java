//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import MovementPattern.NodeMovement;
import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//COMULATIVE AVERAGE BASED ROUTING PROTOCOL

public class CAoICD extends RoutingProtocol
{
    //Instance variables
    boolean forward=false,warmFlag=false;
    public static int size;    //i and j are source and destination declared in main program
    long encounterDuration[][],noOfEncounters[][],lastPeriodStartTime[][],encounterTimeLeft[][],encounterPeriod[][],lastEncounterTime[][],timeLeftNx=-1,timeLeftNy=-1;
    double CAoICDuration[][];//stores the time passed since last encounter
    static double firstEncounterPosition[][][];

//******************************************************************************
//CONSTRUCTOR

public CAoICD() {}

//******************************************************************************

@Override
public void setPerimeters()
{
    size=dtnrouting.allNodes.size();
    CAoICDuration=new double[size][size];
    firstEncounterPosition=new double[size][size][4];
    encounterDuration=new long[size][size];
    noOfEncounters=new long[size][size];
    encounterTimeLeft=new long[size][size];
    encounterPeriod=new long[size][size];
    lastPeriodStartTime=new long[size][size];
    lastEncounterTime=new long[size][size];

    //Intialize encounterDuration and CAoICD
    for(int m=0;m<size;m++)
        for(int n=0;n<size;n++)
        {
            CAoICDuration[m][n]=-1;
            encounterDuration[m][n]=0;
            noOfEncounters[m][n]=0;
            encounterTimeLeft[m][n]=-1;
            encounterPeriod[m][n]=-1;
            lastPeriodStartTime[m][n]=-1;
            lastEncounterTime[m][n]=0;
        }
   dtnrouting.CommentsTA.append("\nWARMUP PERIOD");
}

//******************************************************************************

public void updateAICDuration(int m,int n)
{
    //Increase number of encounters by 1
    noOfEncounters[n][m]=noOfEncounters[m][n]=noOfEncounters[m][n]+1;
    
    //Update encounter duration
    encounterDuration[n][m]=encounterDuration[m][n]= dtnrouting.simulationTime- lastEncounterTime[m][n];
    lastEncounterTime[n][m]=lastEncounterTime[m][n]=dtnrouting.simulationTime;
    //If this is first
    if(CAoICDuration[m][n]==-1) CAoICDuration[n][m]=CAoICDuration[m][n]=0;
    //Update mutual interContactDuration
    CAoICDuration[n][m]=CAoICDuration[m][n]=
    (encounterDuration[m][n]+(noOfEncounters[m][n]-1)*CAoICDuration[m][n])/(noOfEncounters[m][n]);

}

//******************************************************************************

public void findPeriodOfEncounters(Node nx,Node ny)
{
int m=nx.nodeID-1; int n=ny.nodeID-1;
//Start of period

if(encounterPeriod[m][n]==-1)
{
    encounterPeriod[n][m]=encounterPeriod[m][n]=dtnrouting.simulationTime;
    firstEncounterPosition[m][n][0]=nx.nodeX;
    firstEncounterPosition[m][n][1]=nx.nodeY;
    firstEncounterPosition[m][n][2]=ny.nodeX;
    firstEncounterPosition[m][n][3]=ny.nodeY;
    lastPeriodStartTime[n][m]=lastPeriodStartTime[m][n]=dtnrouting.simulationTime;
  
}
//Total period
else if(firstEncounterPosition[m][n][0]==nx.nodeX &&
     firstEncounterPosition[m][n][1]==nx.nodeY &&
     firstEncounterPosition[m][n][2]==ny.nodeX &&
     firstEncounterPosition[m][n][3]==ny.nodeY)
  {
   encounterPeriod[n][m]= encounterPeriod[m][n]=dtnrouting.simulationTime-encounterPeriod[m][n];
   lastPeriodStartTime[n][m]=lastPeriodStartTime[m][n]=dtnrouting.simulationTime;
  }



}

//******************************************************************************

 public void minExpectedDelay(Node nx,Node ny,Node destNode ){
//Time left for node x to encounter destination node
int i=nx.nodeID-1,j=ny.nodeID-1,k=destNode.nodeID-1;
long timex=(dtnrouting.simulationTime-lastPeriodStartTime[i][k])%((long)CAoICDuration[i][k]);
long timey=(dtnrouting.simulationTime-lastPeriodStartTime[j][k])%((long)CAoICDuration[j][k]);
if(timex==0)
timeLeftNx=0;
else
    timeLeftNx=((long)CAoICDuration[i][k])-timex;

//Time left for node x to encounter destination node
if(timey==0)
timeLeftNy=0;
else
    timeLeftNy=((long)CAoICDuration[j][k])-timey;

 }

//******************************************************************************
 
public void Deliver(Node nx,Node ny)    //x and y are intermediet sender and reciever
{

   findPeriodOfEncounters(nx,ny);
   updateAICDuration(nx.nodeID-1,ny.nodeID-1);
    //Bidirectional connectivity
    DeliverMessage(nx, ny);
    DeliverMessage(ny, nx);

}

//******************************************************************************

public void DeliverMessage(Node nx, Node ny)
{
  if(dtnrouting.isRun==true ){

  if(NodeMovement.warmupPeriod==size) //Warming Period finished
  {
     
      if(!warmFlag)
      {
          dtnrouting.CommentsTA.append(" FINISHED");
          for(int h=0;h<dtnrouting.areBundlesDelivered.size();h++)
            {
                Bundle bundleObj=dtnrouting.areBundlesDelivered.get(h);
                bundleObj.bundleTTL=bundleObj.maxTTL;
                bundleObj.bundleLatency=0;
            }
          dtnrouting.delay=0;
      }
      warmFlag=true;
   if(!nx.DestNBundle.isEmpty())
   {
    //Update the time spent by bundles within a node nx
   nx.updateBundleTimestamp(nx);
   //Transfer the bundles

   for (Iterator<Map.Entry<Bundle,Node>> i = nx.DestNBundle.entrySet().iterator(); i.hasNext(); )
    {
         Map.Entry<Bundle,Node> entry = i.next();
         Bundle bundleObj = entry.getKey();
         Node   destNode = entry.getValue();
         

         //If destiantion has not enough size to recieve bundle
        //OR if its TTL is expired, , it bundle cannot be sent
         if(checkTTLandSize(nx,ny,destNode,bundleObj));

        //If destiantion has enough size to recieve bundle
        //and if its TTL is not expired, , it bundle can be sent
        // if contact duration is enough to transfer the message
        else if(bundleObj.bundleSize <= dtnrouting.contactDuration[nx.nodeID - 1][ny.nodeID - 1])
        {
        
        //If encountered Node has not yet recieved bundle, bundle is yet not delivered,in ny's buffer enough space is free to occupy the bundle and bundle TTL is not expired
        if((!ny.bundleIDHash.contains(bundleObj.bundleName))&&(ny.queueSizeLeft>bundleObj.bundleSize)&&(bundleObj.isBundleDelivered==false)&&(bundleObj.bundleTTL>0))
        {
            //if ny is destination
            if(destNode.equals(ny))
            {
                ny.DestNBundle.put(bundleObj,null);
                bundleObj.isBundleDelivered=true;
                deliverBundle(nx,ny,bundleObj);
                i.remove();
            }
            //if ny is not a destination
            else
            {
               minExpectedDelay(nx,ny,destNode);
               if((CAoICDuration[ny.nodeID-1][destNode.nodeID-1]>-1)&&(timeLeftNx>timeLeftNy))
               {
                   forward = true;
                
               }
               if(bundleObj.endNodesRegion.equals("Same"))
               {
                //Within same node bundle be forwarded to reguar or fixed node
                   if(forward &&(ny.name.startsWith("R")||(nx.name.startsWith("D"))))
                   {
                     bundleObj.bundleBandwidth+=1;
                     ny.DestNBundle.put(bundleObj, destNode);
                     deliverBundle(nx,ny,bundleObj);
                     i.remove();
                   }

               }
                       //in single ferry and destinaation as different region the ferry is intermediate destination
               else
               {

                boolean returnValue = deviceBasedBundleTransfer(nx, ny, destNode, bundleObj,forward);
                if(returnValue==true){ i.remove(); bundleObj.bundleBandwidth+=1;}
                   else if(forward && (nx.name.startsWith("R")||nx.name.startsWith("D"))&& (ny.name.startsWith("R")||ny.name.startsWith("D")))
                    {
                         ny.DestNBundle.put(bundleObj, destNode);
                         deliverBundle(nx,ny,bundleObj);
                         bundleObj.bundleBandwidth+=1;
                         i.remove();
                    }

               }
            }
            forward=false;
         }
         }

        }

    }
//Chech whether forwading of bundles have ended
 checkForwardingEnds();

   }}
      
}//End of method

//******************************************************************************

}//End of class

