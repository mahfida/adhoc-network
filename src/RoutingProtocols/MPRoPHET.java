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
//MULTI-COPY PROPHET

public class MPRoPHET extends PRoPHET{
static double[][] AvgICDuration; //Average inter contact duration between any two nodes
int cases;
public MPRoPHET()
    {
    }
    @Override
public void setPerimeters()
        
    {
    super.setPerimeters();
    AvgICDuration=new double[size][size];  // The average inter-contact duration between any two nodes
        for(int m=0;m<size;m++)
            for(int n=0;n<size;n++)
                      AvgICDuration[m][n]=0;
    }

@Override
public void Encounter(int x,int y)  //x and y are sender and reciever nodes
{
if(AgeCounter[x][y]>0) // The current inter-contact duration is stored in k
    {
    if(AvgICDuration[x][y]>0)
        //Update the value of t that stores InterContact Durations
        AvgICDuration[x][y]=(AvgICDuration[x][y]+AgeCounter[x][y])/2;  //if node x encoutners y multiple times in history
    else
        AvgICDuration[x][y]=AgeCounter[x][y]; //Maximum value of time lapse after which x and y encounters

    }
super.Encounter(x, y);//to update preditibility value and set AgeCounter to zero
}

@Override
public void Deliver(Node nx,Node ny)
{
    Encounter(nx.nodeID-1,ny.nodeID-1);
    Transitivity(nx.nodeID-1,ny.nodeID-1);
    Aging(nx.nodeID-1,ny.nodeID-1);
   
  if(NodeMovement.warmupPeriod==size) //Warming Period finished
    {
      if(!warmFlag)
      {
        for(int h=0;h<dtnrouting.areBundlesDelivered.size();h++)
        {
            Bundle bundleObj=dtnrouting.areBundlesDelivered.get(h);
            bundleObj.bundleTTL=bundleObj.maxTTL;
            bundleObj.bundleLatency=0;
        }
          dtnrouting.CommentsTA.append(" FINISHED\n");
          dtnrouting.delay=0;
      }
      warmFlag=true;
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
        if((bundleObj.isBundleDelivered==false)   // if contact duration is enough to transfer the message
        && (bundleObj.bundleSize<=dtnrouting.contactDuration[nx.nodeID-1][ny.nodeID-1]))
        {
            //If destination encounters, hands over bundle to it
            if(ny==destNode)
                   cases=1;//handle bundle to destination and delete its copy

            else
               if((dtnrouting.p[ny.nodeID-1][destNode.nodeID-1])>(dtnrouting.p[nx.nodeID-1][destNode.nodeID-1]))
               {
                   if((AvgICDuration[nx.nodeID-1][destNode.nodeID-1]>0.0) && ((AgeCounter[nx.nodeID-1][destNode.nodeID-1])>(0.67*AvgICDuration[nx.nodeID-1][destNode.nodeID-1]))&&(dtnrouting.p[nx.nodeID-1][destNode.nodeID-1]>0.0))
                    cases=2;//give bundle to ny and keep its copy with itself too
                   else
                    cases=3; //give bundle to ny and delete its local copy
               }
             else   //if ny may meet destination soon
                 if((AvgICDuration[ny.nodeID-1][destNode.nodeID-1]>0.0) && ((AgeCounter[ny.nodeID-1][destNode.nodeID-1])>(0.67*AvgICDuration[ny.nodeID-1][destNode.nodeID-1]))&&(dtnrouting.p[ny.nodeID-1][destNode.nodeID-1]>0.0))
                     cases=2; // there is chances that ny may meet sooner than nx with ny, so hand over a copy to ny and keep one with nx
           
            //Hand Over the bundle to encountered Node
            bundleObj.bundleBandwidth+=1; //Since bundle is transfered
            ny.bundleIDHash.add(bundleObj.bundleName);
            ny.queueSizeLeft-=bundleObj.bundleSize;
            bundleObj.bundleTTL-=1;

            //Perform case specific funtions
            switch(cases)
           {
                case 1:
                   //deliver bundel to destination
                   ny.DestNBundle.put(bundleObj,null);
                   bundleObj.isBundleDelivered=true;
                   bundleObj.bundleLatency=dtnrouting.delay;
                   //Delete bundle copy from nx
                   nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                   nx.bundleIDHash.remove(bundleObj.bundleName);
                   i.remove();
                   break;
               
                case 2:
                   //deliver bundel to ny and keep copy with nx too
                   ny.DestNBundle.put(bundleObj,destNode);
                   break;

                case 3:
                   ny.DestNBundle.put(bundleObj,destNode);
                   //Delete bundle copy from nx
                   nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                   nx.bundleIDHash.remove(bundleObj.bundleName);
                   i.remove();
                   break;
               }
            //Display Result
             dtnrouting.CommentsTA.append(nx.name+" delivers "+bundleObj.bundleName+"  to "+ny.name+"\n");
            }

           
         }
       }
    }
//Chech whether forwading of bundles have ended
 checkForwardingEnds();
} //End of Deliver()
} //End of MCPRoPHET


