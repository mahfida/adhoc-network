//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//DIRECT DELIVERY ROUTING PROTOCOL

public class DirectDelivery extends RoutingProtocol
{

//******************************************************************************
//CONSTRUCTOR
public DirectDelivery() {}

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
                           System.out.println(bundleObj.bundleName+" "+bundleObj.bundleLatency);
                   }
                 boolean returnValue=deviceBasedBundleTransfer(nx, ny,destNode,bundleObj);
                 if(returnValue==true)  i.remove();

                 }

         }

    }
  }
//Chech whether forwading of bundles have ended
checkForwardingEnds();
}
}//end of method name

//*****************************************************************************

public boolean deviceBasedBundleTransfer(Node nx, Node ny,Node destNode, Bundle bundleObj)
{
     boolean returnValue=false;
     //if single ferry is used
       if((DeviceBasedSettings.ferryType.equals("Single-Ferry")) && (ny.name.substring(0, 1).equals("F")))
       {
                if((bundleObj.endNodesRegion.equals("Different")==true)||ny.visitingRegion.getNetworkType().equals("Static"))
                {
                        //if encountered node is a ferry then deliver bundle to is
                        ny.DestNBundle.put(bundleObj,destNode);
                        deliverBundle(nx,ny,bundleObj);
                        returnValue=true;
                        return(returnValue);

                }
        }

        if(DeviceBasedSettings.ferryType.equals("Pigeon"))
       {
            if((nx.name.substring(0,1).equals("R")||nx.name.startsWith("D"))&& ny.name.substring(0, 2).equals("CH"))
            if((bundleObj.endNodesRegion.equals("Different")==true)||ny.nodeRegion.getNetworkType().equals("Static"))
             {
                        //if encountered node is a ferry then deliver bundle to is
                        ny.DestNBundle.put(bundleObj,destNode);
                        deliverBundle(nx,ny,bundleObj);
                          returnValue=true;
                          return(returnValue);

           }
            if(nx.name.substring(0,2).equals("CH")&& (ny.name.substring(0, 1).equals("F") && !destNode.nodeRegion.equals(ny.nodeRegion)))
            {
                        //if encountered node is a ferry then deliver bundle to is
                        ny.DestNBundle.put(bundleObj,destNode);
                        deliverBundle(nx,ny,bundleObj);
                           returnValue=true;
                           return(returnValue);
            }
             if(ny.name.substring(0,2).equals("CH")&& (nx.name.substring(0, 1).equals("F") && destNode.nodeRegion.equals(ny.nodeRegion) && !nx.nodeRegion.equals(ny.nodeRegion)))
             {
                     //if encountered node is a ferry then deliver bundle to is
                        ny.DestNBundle.put(bundleObj,destNode);
                        deliverBundle(nx,ny,bundleObj);
                          returnValue=true;
                          return(returnValue);
             }
        }
 return(returnValue);
}

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
