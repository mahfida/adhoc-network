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
   //if nx has packet and ny has to recieve it
   if(!nx.DestNPacket.isEmpty())
   {
   nx.updatepacketTimestamp(nx);

   for (Iterator<Map.Entry<Packet,Node>> i = nx.DestNPacket.entrySet().iterator(); i.hasNext(); )
    {
        Map.Entry<Packet,Node> entry = i.next();
        Packet packetObj = entry.getKey();
        Node   destNode = entry.getValue();

        //If destiantion has not enough size to recieve packet
        //OR if its TTL is expired, , it packet cannot be sent

        if(checkTTLandSize(nx,ny,destNode,packetObj)==true);

        //If destination has enough size to recieve packet
        //and if its TTL is not expired, , it packet can be sent
        // if contact duration is enough to transfer the message
        else
        if(packetObj.packetSize<=dtnrouting.contactDuration[nx.ID-1][ny.ID-1] && !ny.packetIDHash.contains(packetObj.packetName))
        {
                //If encountered Node is destination and packet is yet not delivered,in ny's buffer enough space is free to occupy the packet and packet TTL is not expired
               if((packetObj.ispacketDelivered==false)&&(ny.queueSizeLeft-packetObj.packetSize>=0)&&(packetObj.packetTTL>0))
                {
                   if(destNode==ny)
                   {

                           ny.DestNPacket.put(packetObj,null);
                           deliverPacket(nx,ny,packetObj);
                           packetObj.ispacketDelivered=true;
                           i.remove();
                   }
                   else if(communityLabel[nx.ID-1]==communityLabel[destNode.ID-1])
                   {
                        if((communityLabel[ny.ID-1]==communityLabel[destNode.ID-1]) &&
                            (localRanking[ny.ID-1]>localRanking[nx.ID-1]))
                        {
                               ny.DestNPacket.put(packetObj,destNode);
                               deliverPacket(nx,ny,packetObj);
                               i.remove();
                        }

                    }
                    else if((communityLabel[ny.ID-1]==communityLabel[destNode.ID-1])

                            ||(globalRanking[ny.ID-1]>globalRanking[nx.ID-1]))
                    {
                           ny.DestNPacket.put(packetObj,destNode);
                           deliverPacket(nx,ny,packetObj);
                           i.remove();
                    }
            }

        }
    
  }
//Check whether forwarding of packets have ended
checkForwardingEnds();
}}
}//end of method name

//******************************************************************************

@Override
public void deliverPacket(Node nx, Node ny, Packet packetObj)
   {
        packetObj.packetBandwidth+=1; //Since packet is transfered
        ny.packetIDHash.add(packetObj.packetName);
        ny.packetTimeSlots.put(packetObj.packetName, 0);
        ny.packetCopies.put(packetObj.packetName,1);
        ny.queueSizeLeft-=packetObj.packetSize;
        dtnrouting.CommentsTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.name);
         //update nx
        nx.queueSizeLeft+=packetObj.packetSize; // the whole space            nx.packetIDHash.remove(packetObj.packetName);
        nx.packetTimeSlots.remove(packetObj.packetName);
        nx.packetCopies.remove(packetObj.packetName);
   }

//******************************************************************************

}// end of class