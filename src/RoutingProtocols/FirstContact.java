//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//DELIVER MESSAGE TO FIRST ENCOUNTERED NODE

public class FirstContact extends RoutingProtocol
{

//******************************************************************************
//CONSTRUCTOR

public FirstContact()     {}

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
    if(!nx.DestNBundle.isEmpty())
    {
        //Update the time spent by bundles within a node nx
        nx. updateBundleTimestamp(nx);
        //Transfer the bundles
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
                if(bundleObj.bundleSize<=dtnrouting.contactDuration[nx.nodeID-1][ny.nodeID-1])
                {

                        //If encountered Node has not yet recieved bundle, bundle is yet not delivered,in ny's buffer enough space is free to occupy the bundle and bundle TTL is not expired
                        if((ny.bundleIDHash.contains(bundleObj.bundleName)==false)&&(ny.queueSizeLeft>bundleObj.bundleSize)&&(bundleObj.isBundleDelivered==false)&&(bundleObj.bundleTTL>0))
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
                               if(bundleObj.endNodesRegion.equals("Same"))
                               {
                                   if((ny.name.startsWith("R")||ny.name.startsWith("D")))
                                   {
                                     ny.DestNBundle.put(bundleObj, destNode);
                                     deliverBundle(nx,ny,bundleObj);
                                     i.remove();
                                   }

                               }
                                       //in single ferry and destinaation as different region the ferry is intermediate destination
                               else
                               {

                                       boolean returnValue = deviceBasedBundleTransfer(nx, ny, destNode, bundleObj);
                                       if(returnValue==true) i.remove();

                                       else if((nx.name.startsWith("R")||nx.name.startsWith("T"))&& (ny.name.startsWith("R")||ny.name.startsWith("T")))
                                        {
                                             ny.DestNBundle.put(bundleObj, destNode);
                                             deliverBundle(nx,ny,bundleObj);
                                             i.remove();
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
}

//******************************************************************************
//USE RELAY DEVICE FOR MESSAGE TRANSFER

public boolean deviceBasedBundleTransfer(Node nx, Node ny,Node destNode, Bundle bundleObj)
{
     boolean returnValue=false;
     //if single ferry is used
       if(DeviceBasedSettings.ferryType.equals("Single-Ferry"))
       {
           //if destination is ferry
           if((ny.name.startsWith("F"))||(nx.name.startsWith("F") && ny.nodeRegion.equals(destNode.nodeRegion)&&(ny.nodeRegion.getNetworkType().equals("Mobile"))))
           {   //if encountered node is a ferry then deliver bundle to is
                        ny.DestNBundle.put(bundleObj,destNode);
                        deliverBundle(nx,ny,bundleObj);
                        returnValue=true;
                        return(returnValue);
           }

        }

        if(DeviceBasedSettings.ferryType.equals("Pigeon"))
       {
            if( ny.name.substring(0, 2).equals("CH")&& nx.nodeRegion.equals(bundleObj.sourceNode.nodeRegion))
                {
                        //CH of source region
                          ny.DestNBundle.put(bundleObj,destNode);
                          deliverBundle(nx,ny,bundleObj);
                          returnValue=true;
                          return(returnValue);

                }
            if(nx.name.substring(0,2).equals("CH")&& (ny.name.substring(0, 1).equals("F") && !destNode.nodeRegion.equals(ny.nodeRegion)))
            {
                        //when CH encounters source Ferry
                        ny.DestNBundle.put(bundleObj,destNode);
                        deliverBundle(nx,ny,bundleObj);
                           returnValue=true;
                           return(returnValue);
            }
             if(ny.name.substring(0,2).equals("CH")&& (nx.name.substring(0, 1).equals("F") && destNode.nodeRegion.equals(ny.nodeRegion) && !nx.nodeRegion.equals(ny.nodeRegion)))
             {
                     //when source Ferry encounters destination CH
                        ny.DestNBundle.put(bundleObj,destNode);
                        deliverBundle(nx,ny,bundleObj);
                          returnValue=true;
                          return(returnValue);
             }
            if(nx.name.startsWith("C")&& (ny.name.startsWith("R")||ny.name.startsWith("T"))&& ny.nodeRegion.equals(destNode.nodeRegion))
                   {
                     //when destination CH encounters a regular node in destination region
                        ny.DestNBundle.put(bundleObj,destNode);
                        deliverBundle(nx,ny,bundleObj);
                        returnValue=true;
                        return(returnValue);
             }
        }
     return(returnValue);
}
//******************************************************************************
//DELIVER BUNDLE

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

}//End of class

