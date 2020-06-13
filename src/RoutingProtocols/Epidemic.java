//******************************************************************************
//PACKAGE
package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//FLOOD ROUTING PROTOCOL

public class Epidemic extends RoutingProtocol
{
    //Instance Variables
    String ferryNode=" ";
    boolean removeBundlecopy=false,forward=false;

//******************************************************************************
//CONSTRUCTOR
public Epidemic()  {}

//******************************************************************************

@SuppressWarnings("static-access")
public void Deliver(Node nx,Node ny)    //x and y are intermediet sender and reciever
{
    //Bidirectional connectivity
    DeliverMessage(nx, ny);
    DeliverMessage(ny, nx);
}

//******************************************************************************
//DELIVER MESSAGES

public void DeliverMessage(Node nx, Node ny)
{
   if(dtnrouting.isRun==true ){
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
        if((!ny.bundleIDHash.contains(bundleObj.bundleName))&& bundleObj.bundleSize<=dtnrouting.contactDuration[nx.nodeID-1][ny.nodeID-1]){
       //If encountered Node has not yet recieved bundle, bundle is yet not delivered,in ny's buffer enough space is free to occupy the bundle and bundle TTL is not expired 
        if((ny.queueSizeLeft>bundleObj.bundleSize)&&(bundleObj.isBundleDelivered==false))
            { 
                bundleObj.bundleBandwidth+=1; //Since bundle is transfered
                bundleObj.bundleLoad+=1;    //bundle copy increments
                if(destNode==ny)
                {
                    ny.DestNBundle.put(bundleObj,null);
                    ny.bundleIDHash.add(bundleObj.bundleName);
                    ny.queueSizeLeft-=bundleObj.bundleSize;
                    ny.bundleTimeSlots.put(bundleObj.bundleName,0);
                    bundleObj.isBundleDelivered=true;

                    //update nx
                    nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                    nx.bundleIDHash.remove(bundleObj.bundleName);
                    nx.bundleTimeSlots.remove(bundleObj.bundleName);
                    nx.bundleCopies.remove(bundleObj.bundleName);

                    bundleObj.bundleLoad-=1;    //bundleCopy decrements
                    i.remove();
                    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
                }
                else
                {
                   if(bundleObj.endNodesRegion.equals("Same"))
                   {
                   if (ny.name.startsWith("R")||ny.name.startsWith("D"))  deliverBundle(nx,ny,destNode,bundleObj); }

                   //in single ferry and destination as different region the ferry is intermediate destination
                   else
                   {

                          boolean returnValue = deviceBasedBundleTransfer(nx, ny, destNode, bundleObj,true);
                          //whether the copy be removed from the
                          if(removeBundlecopy==true) {i.remove(); removeBundlecopy=false;}
                        
                          if(returnValue==false)
                           {
                                if((nx.name.startsWith("R")||nx.name.startsWith("T"))&& (ny.name.startsWith("R")||ny.name.startsWith("T")))
                                    if( (ny.nodeRegion.equals(bundleObj.sourceNode.nodeRegion)&& ferryNode.equals(" "))||!ny.nodeRegion.equals(bundleObj.sourceNode.nodeRegion))
                                        deliverBundle(nx,ny,destNode,bundleObj);
                           }

                    }
                }
           
            } //delivery of bundle ends
        }
          
    }
}

//Chech whether forwading of bundles have ended
 checkForwardingEnds();
}

 }   //end of method

//******************************************************************************
//DELIVER BUNDLE TRUOUGH A RELAY DEVICE

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
                     System.out.println("Single ferry");
                    deliverBundle(nx,ny,destNode,bundleObj);
                    //update nx
                    nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                    nx.bundleTimeSlots.remove(bundleObj.bundleName);
                    nx.bundleCopies.remove(bundleObj.bundleName);
                    bundleObj.bundleLoad-=1;    //bundleCopy decrements
                    removeBundlecopy=true;
                    returnValue=true;
                    return(returnValue);
       }

    }

    else if(DeviceBasedSettings.ferryType.equals("Pigeon"))
    {

    if( ny.name.substring(0, 2).equals("CH"))
    {
        if((nx.nodeRegion.equals(bundleObj.sourceNode.nodeRegion))&& nx.nodeRegion.equals(ny.nodeRegion))
        {
                //CH of source region
                ferryNode="RecievedMessage";
                deliverBundle(nx,ny,destNode,bundleObj);
                 //update nx
                nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                nx.bundleTimeSlots.remove(bundleObj.bundleName);
                nx.bundleCopies.remove(bundleObj.bundleName);
                bundleObj.bundleLoad-=1;    //bundleCopy decrements
                removeBundlecopy=true;
                returnValue=true;
                return(returnValue);

        }
        else if( (nx.name.substring(0, 1).equals("F") && destNode.nodeRegion.equals(ny.nodeRegion) && !nx.nodeRegion.equals(ny.nodeRegion)))
            {
             //when source Ferry encounters destination CH

                deliverBundle(nx,ny,destNode,bundleObj);
                nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                nx.bundleTimeSlots.remove(bundleObj.bundleName);
                nx.bundleCopies.remove(bundleObj.bundleName);
                bundleObj.bundleLoad-=1;    //bundleCopy decrements
                removeBundlecopy=true;
                returnValue=true;
                return(returnValue);
         }
   }
    else if (nx.name.substring(0, 2).equals("CH"))
    {

        if(ny.name.substring(0, 1).equals("F") && !destNode.nodeRegion.equals(ny.nodeRegion))
            {
                //when CH encounters source Ferry

                deliverBundle(nx,ny,destNode,bundleObj);
                nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                nx.bundleTimeSlots.remove(bundleObj.bundleName);
                nx.bundleCopies.remove(bundleObj.bundleName);
                bundleObj.bundleLoad-=1;    //bundleCopy decrements
                removeBundlecopy=true;
                returnValue=true;
                return(returnValue);
            }

        else if((ny.name.startsWith("R")||ny.name.startsWith("T")) && ny.nodeRegion.equals(destNode.nodeRegion))
           {
             //when destination CH encounters a regular node in destination region

                deliverBundle(nx,ny,destNode,bundleObj);
                returnValue=true;
                return(returnValue);
            }
    }

    }//end of pigeon
     return(returnValue);
}

//******************************************************************************
//DELIVER BUNDLE TO AN INTERMEDIATE NODE

public void deliverBundle(Node nx,Node ny, Node destNode, Bundle bundleObj)
{
    ny.DestNBundle.put(bundleObj,destNode);
    ny.bundleIDHash.add(bundleObj.bundleName);
    ny.bundleTimeSlots.put(bundleObj.bundleName, 0);
    ny.bundleCopies.put(bundleObj.bundleName,1);
    ny.queueSizeLeft-=bundleObj.bundleSize;

    //increase the number of copies made by nx of this bundleObj
    nx.bundleCopies.put(bundleObj.bundleName,nx.bundleCopies.get(bundleObj.bundleName)+1);
    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);

}
}// end of class



