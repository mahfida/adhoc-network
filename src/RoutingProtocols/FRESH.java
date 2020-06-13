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
//FRESH ROUTING ALGORITHM - BASED ON RECENCY OF ENCOUNTERS

public class FRESH extends RoutingProtocol
{
    //Instance Variables
    boolean warmFlag=false;
    public static int size;    //i and j are source and destination declared in main program
    static double [][] LastEncounterTime;//stores the last encounter Time

//******************************************************************************
//CONSTRUCTOR
public   FRESH() {}

//******************************************************************************

@Override
public void setPerimeters()
{
    size=dtnrouting.allNodes.size();
    LastEncounterTime=new double[size][size];
    
    for(int m=0;m<size;m++)
        for(int n=0;n<size;n++)
           LastEncounterTime[m][n]=-1; //Initially last encounter time is unknown shown by -1
    dtnrouting.CommentsTA.append("\nWARMUP PERIOD");
}

//******************************************************************************

public void updateLastEncounterTime(int x,int y)
{
    LastEncounterTime[y][x]=LastEncounterTime[y][x]=dtnrouting.simulationTime;
}

//******************************************************************************
public void Deliver(Node nx,Node ny)    //x and y are intermediet sender and reciever
{
    updateLastEncounterTime(nx.nodeID-1,ny.nodeID-1);
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
  if(NodeMovement.warmupPeriod==size) //Warming Period finished
  {
      if(!warmFlag)
      {
          dtnrouting.CommentsTA.append(" FINISHED ");
          for(int h=0;h<dtnrouting.areBundlesDelivered.size();h++)
            {
                Bundle bundleObj=dtnrouting.areBundlesDelivered.get(h);
                bundleObj.bundleTTL=bundleObj.maxTTL;
                bundleObj.bundleLatency=0;
            }
          dtnrouting.delay=0;
      }
      warmFlag=true;
      //if nx has bundle and ny has to recieve it
       if(!nx.DestNBundle.isEmpty())
       {
        //Update the time spent by bundles within a node nx
             nx. updateBundleTimestamp(nx);
        //Transfer the bundels

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
        if(bundleObj.bundleSize<=dtnrouting.contactDuration[nx.nodeID-1][ny.nodeID-1]){

        //If encountered Node has not yet recieved bundle, bundle is yet not delivered,in ny's buffer enough space is free to occupy the bundle and bundle TTL is not expired
        if((ny.bundleIDHash.contains(bundleObj.bundleName) == false) && (ny.queueSizeLeft > bundleObj.bundleSize) && (bundleObj.isBundleDelivered == false) && (bundleObj.bundleTTL > 0))
        {
            if(ny==destNode)
            {
                bundleObj.bundleBandwidth+=1; //Since bundle is transfered
                ny.DestNBundle.put(bundleObj,null);
                ny.bundleIDHash.add(bundleObj.bundleName);
                ny.queueSizeLeft-=bundleObj.bundleSize;
                ny.bundleTimeSlots.put(bundleObj.bundleName,0);
                bundleObj.isBundleDelivered=true;
                //update nx
                nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                nx.bundleIDHash.remove(bundleObj.bundleName);
                nx.bundleTimeSlots.remove(bundleObj.bundleName);
                i.remove();
                //Display Result
                dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
            }

            else  if((!ny.bundleIDHash.contains(bundleObj.bundleName))&&(LastEncounterTime[nx.nodeID-1][destNode.nodeID-1]<LastEncounterTime[ny.nodeID-1][destNode.nodeID-1]))
            {
                 bundleObj.bundleBandwidth+=1; //Since bundle is transfered
                 ny.DestNBundle.put(bundleObj,destNode);
                 ny.bundleIDHash.add(bundleObj.bundleName);
                 ny.queueSizeLeft-=bundleObj.bundleSize;
                 ny.bundleTimeSlots.put(bundleObj.bundleName,0);

                 //update nx
                 nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                // nx.bundleIDHash.remove(bundleObj.bundleName);
                 nx.bundleTimeSlots.remove(bundleObj.bundleName);
                 i.remove();
                 //Display Result
                 dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
            }
        }
        }
     }//End of for loop
   }
}

//Check whether forwading of bundles have ended
checkForwardingEnds();
}
}   //end of Deliver()

//******************************************************************************

}//End of class






