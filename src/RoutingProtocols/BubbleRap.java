//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//BUBBLERAP ROUTING PROTOCOL

public class BubbleRap extends RoutingProtocol
{
public double localRanking[];//local centrality of nodes
public double  globalRanking[];//global centrality of nodes
public int communityLabel[];//ID of the community to which this node belongs
//******************************************************************************
//CONSTRUCTOR
public BubbleRap()
{

}

//******************************************************************************
//INITIALIZE RANKINGS AND COMMUNITY MEMBERSHIP
public void setPerimeters(int size)
{
    localRanking=new double[size];
    globalRanking=new double[size];
    communityLabel=new int[size]; 


}

//******************************************************************************

public void Deliver(Node nx,Node ny)    //x and y are intermediet sender and reciever
{
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
                   if(destNode==ny)
                   {

                           ny.DestNBundle.put(bundleObj,null);
                           deliverBundle(nx,ny,bundleObj);
                           bundleObj.isBundleDelivered=true;
                           i.remove();
                   }
                   else if(communityLabel[nx.nodeID-1]==communityLabel[destNode.nodeID-1])
                   {
                        if((communityLabel[ny.nodeID-1]==communityLabel[destNode.nodeID-1]) &&
                            (localRanking[ny.nodeID-1]>localRanking[nx.nodeID-1]))
                        {
                               ny.DestNBundle.put(bundleObj,destNode);
                               deliverBundle(nx,ny,bundleObj);
                               i.remove();
                        }

                    }
                    else if((communityLabel[ny.nodeID-1]==communityLabel[destNode.nodeID-1])

                            ||(globalRanking[ny.nodeID-1]>globalRanking[nx.nodeID-1]))
                    {
                           ny.DestNBundle.put(bundleObj,destNode);
                           deliverBundle(nx,ny,bundleObj);
                           i.remove();
                    }
            }

        }
    
  }
//Chech whether forwading of bundles have ended
checkForwardingEnds();
}}
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

}// end of class