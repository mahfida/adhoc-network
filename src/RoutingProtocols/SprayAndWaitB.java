//******************************************************************************
//PACKAGE NAME

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

//******************************************************************************
//SPRAY AND WAIT BINARY ROUTING SCHEME

public class SprayAndWaitB extends RoutingProtocol
{
    //Instance Variables
    Random rand;
    String ferryNode=" ";
    boolean removeBundleCopy=false;

//******************************************************************************
//CONSTRUCTOR
public SprayAndWaitB() {}

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
    if(dtnrouting.isRun==true)    {
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
        else if (bundleObj.bundleSize <= dtnrouting.contactDuration[nx.nodeID - 1][ny.nodeID - 1]) {
        //If encountered Node is destination and bundle is yet not delivered,in ny's buffer enough space is free to occupy the bundle and bundle TTL is not expired
         if(bundleObj.bundleSize<=ny.queueSizeLeft && bundleObj.isBundleDelivered==false && bundleObj.bundleDelivered==0)
        {
            if(ny==destNode)
            {
                int bcopy=nx.bundleCopies.get(bundleObj.bundleName);
                bundleObj.bundleBandwidth+=1;
                ny.DestNBundle.put(bundleObj,null);
                ny.bundleIDHash.add(bundleObj.bundleName);
                ny.bundleCopies.put(bundleObj.bundleName,1);
                ny.queueSizeLeft-=bundleObj.bundleSize;
                ny.bundleTimeSlots.put(bundleObj.bundleName,0);
                bundleObj.isBundleDelivered=true;
                //update nx
                nx.bundleCopies.put(bundleObj.bundleName, bcopy-1);
                if((bcopy-1)<1)
                nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                nx.bundleTimeSlots.remove(bundleObj.bundleName);
                i.remove();
                dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":("+ny.bundleCopies.get(bundleObj.bundleName)+") "+bundleObj.bundleName);
            }
        
            else if(ny.bundleIDHash.contains(bundleObj.bundleName)==false)
            {
                if(bundleObj.endNodesRegion.equals("Same") && nx.bundleCopies.get(bundleObj.bundleName)>1 )
                {
                    if (ny.name.startsWith("R")||ny.name.startsWith("T"))
                      {
                                        int bcopy=nx.bundleCopies.get(bundleObj.bundleName);
                                        deliverBundle(nx, ny, destNode, bundleObj);
                                        ny.bundleCopies.put(bundleObj.bundleName,bcopy/2);
                                        nx.bundleCopies.put(bundleObj.bundleName, bcopy/2);
                                        dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":("+ny.bundleCopies.get(bundleObj.bundleName)+") "+bundleObj.bundleName);
                      }
                }
                   //in single ferry and destination as different region the ferry is intermediate destination
                else if(bundleObj.endNodesRegion.equals("Different"))
                {     
                                        
                      if((nx.name.startsWith("R")||nx.name.startsWith("T"))&& (ny.name.startsWith("R")||ny.name.startsWith("T")) && nx.bundleCopies.get(bundleObj.bundleName)>1)
                          {
                                    if ((ny.nodeRegion.equals(bundleObj.sourceNode.nodeRegion) && ferryNode.equals(" ")) || ny.nodeRegion.equals(bundleObj.destNode.nodeRegion))
                                    {
                                        int bcopy=nx.bundleCopies.get(bundleObj.bundleName);
                                        deliverBundle(nx, ny, destNode, bundleObj);
                                        ny.bundleCopies.put(bundleObj.bundleName,bcopy/2);
                                        nx.bundleCopies.put(bundleObj.bundleName, bcopy/2);
                                        dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":("+ny.bundleCopies.get(bundleObj.bundleName)+") "+bundleObj.bundleName);
                                    }
                          }
                       else if((!nx.name.startsWith("R")||!nx.name.startsWith("T")||!ny.name.startsWith("T")|| !ny.name.startsWith("R"))&& nx.bundleCopies.get(bundleObj.bundleName)>=1)
                         {
                                    
                                    boolean returnValue = deviceBasedBundleTransfer(nx, ny, destNode, bundleObj);
                                    //whether the copy be removed from the
                                    if(removeBundleCopy==true) {i.remove(); removeBundleCopy=false;}

                        }
                            

                 }
            }
            //Display Result
        }}
    } //End of for loop

    }
    //Check whether forwading of bundles have ended
    checkForwardingEnds();
    }
}//end of deliver method

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
                    ferryNode="RecievedMessage";
                    deliverBundle(nx,ny,destNode,bundleObj);
                    ny.bundleCopies.put(bundleObj.bundleName,1);
                    //update nx
                    nx.queueSizeLeft+=bundleObj.bundleSize; // refresh the whole space
                    nx.bundleTimeSlots.remove(bundleObj.bundleName);
                    nx.bundleCopies.remove(bundleObj.bundleName);
                    removeBundleCopy=true;
                    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":("+ny.bundleCopies.get(bundleObj.bundleName)+") "+bundleObj.bundleName);
                    //if ny is regular node of the destination region, there it again creat number of bundle coies in power of 2
                    if(ny.name.startsWith("R")||ny.name.startsWith("D"))
                    {
                        ny.bundleCopies.put(bundleObj.bundleName, bundleObj.destRegionBundleCopies);
                        bundleObj.bundleLoad+=bundleObj.destRegionBundleCopies-1;
                        dtnrouting.CommentsTA.append("\n"+ny.name+" generated ("+(bundleObj.destRegionBundleCopies)+")"+bundleObj.bundleName);
                    }

                    returnValue=true;
                    return(returnValue);
       }
   }

   else if (DeviceBasedSettings.ferryType.equals("Pigeon"))
   {

        if( ny.name.substring(0, 2).equals("CH"))
        {

            if((nx.nodeRegion.equals(bundleObj.sourceNode.nodeRegion))&& nx.nodeRegion.equals(ny.nodeRegion))
                {
                    //CH of source region
                    ferryNode="RecievedMessage";
                    deliverBundle(nx,ny,destNode,bundleObj);
                    ny.bundleCopies.put(bundleObj.bundleName,1);
                    //update nx
                    nx.queueSizeLeft+=bundleObj.bundleSize; // refresh the whole space
                    nx.bundleTimeSlots.remove(bundleObj.bundleName);
                    nx.bundleCopies.remove(bundleObj.bundleName);
                    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":("+ny.bundleCopies.get(bundleObj.bundleName)+") "+bundleObj.bundleName);
                    removeBundleCopy=true;
                    returnValue=true;
                    return(returnValue);

                }
            else if((nx.name.substring(0, 1).equals("F") && destNode.nodeRegion.equals(ny.nodeRegion) && !nx.nodeRegion.equals(ny.nodeRegion)))
                {

                    deliverBundle(nx,ny,destNode,bundleObj);
                    //update nx
                    ny.bundleCopies.put(bundleObj.bundleName,1);
                    nx.queueSizeLeft+=bundleObj.bundleSize; // refresh the whole space
                    nx.bundleTimeSlots.remove(bundleObj.bundleName);
                    nx.bundleCopies.remove(bundleObj.bundleName);
                    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":("+ny.bundleCopies.get(bundleObj.bundleName)+") "+bundleObj.bundleName);
                    ny.bundleCopies.put(bundleObj.bundleName, bundleObj.destRegionBundleCopies);
                    bundleObj.bundleLoad+=bundleObj.destRegionBundleCopies-1;
                    dtnrouting.CommentsTA.append("\n"+ny.name+" generated ("+(bundleObj.destRegionBundleCopies)+")"+bundleObj.bundleName);
                    removeBundleCopy=true;
                    returnValue=true;
                    return(returnValue);
                 }
         }
        else if(nx.name.substring(0, 2).equals("CH"))
        {

            if(ny.name.substring(0, 1).equals("F") && !destNode.nodeRegion.equals(ny.nodeRegion))
                {
                    //when CH encounters source Ferry
                    deliverBundle(nx,ny,destNode,bundleObj);
                    ny.bundleCopies.put(bundleObj.bundleName,1);
                    //update nx
                    nx.queueSizeLeft+=bundleObj.bundleSize; // refresh the whole space
                    nx.bundleTimeSlots.remove(bundleObj.bundleName);
                    nx.bundleCopies.remove(bundleObj.bundleName);
                    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":("+ny.bundleCopies.get(bundleObj.bundleName)+") "+bundleObj.bundleName);
                    removeBundleCopy=true;
                    returnValue=true;
                    return(returnValue);
                }

            else if((ny.name.startsWith("R")||ny.name.startsWith("T")) && ny.nodeRegion.equals(destNode.nodeRegion))
               {
                    //when destination CH encounters a regular node in destination region
                    int bcopy=nx.bundleCopies.get(bundleObj.bundleName);
                    deliverBundle(nx, ny, destNode, bundleObj);
                    ny.bundleCopies.put(bundleObj.bundleName,bcopy/2);
                    nx.bundleCopies.put(bundleObj.bundleName, bcopy/2);
                    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":("+ny.bundleCopies.get(bundleObj.bundleName)+") "+bundleObj.bundleName);
                    returnValue=true;
                    return(returnValue);
                }
        }
    }//end of pigeon
   return(returnValue);
}

//******************************************************************************
//DELIVER BUNDLE

public void deliverBundle(Node nx, Node ny, Node destNode,Bundle bundleObj)
{         
    bundleObj.bundleBandwidth+=1;
    ny.DestNBundle.put(bundleObj,destNode);
    ny.bundleIDHash.add(bundleObj.bundleName);
    ny.bundleTimeSlots.put(bundleObj.bundleName,0);
    ny.queueSizeLeft-=bundleObj.bundleSize;
    //update nx
}  //end of method name

//******************************************************************************

}// end of class
