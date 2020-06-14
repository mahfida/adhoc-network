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
    if(!nx.DestNPacket.isEmpty())
    {
        //Update the time spent by packets within a node nx
        nx. updatepacketTimestamp(nx);
        //Transfer the packets
        for (Iterator<Map.Entry<Packet,Node>> i = nx.DestNPacket.entrySet().iterator(); i.hasNext(); )
        {
             Map.Entry<Packet,Node> entry = i.next();
             Packet packetObj = entry.getKey();
             Node   destNode = entry.getValue();

            //If destination has not enough size to receive packet
            //OR if its TTL is expired, , it packet cannot be sent

             if(checkTTLandSize(nx,ny,destNode,packetObj)==true);

            //If destination has enough size to receive packet
            //and if its TTL is not expired, , it packet can be sent
            // if contact duration is enough to transfer the message
            else
                if(packetObj.packetSize<=dtnrouting.contactDuration[nx.ID-1][ny.ID-1])
                {

                        //If encountered Node has not yet received packet, packet is yet not delivered,in ny's buffer enough space is free to occupy the packet and packet TTL is not expired
                        if((ny.packetIDHash.contains(packetObj.packetName)==false)&&(ny.queueSizeLeft>packetObj.packetSize)&&(packetObj.ispacketDelivered==false)&&(packetObj.packetTTL>0))
                        {
                            //if ny is destination
                            if(destNode.equals(ny))
                            {
                                ny.DestNPacket.put(packetObj,null);
                                packetObj.ispacketDelivered=true;
                                deliverPacket(nx,ny,packetObj);
                                i.remove();
                            }
                             //if ny is not a destination
                            else
                            {
                                   if((ny.name.startsWith("R")||ny.name.startsWith("D")))
                                   {
                                     ny.DestNPacket.put(packetObj, destNode);
                                     deliverPacket(nx,ny,packetObj);
                                     i.remove();
                                   }

                               
                       
                            }
                        }
                 }

        }

    }


//Chech whether forwading of packets have ended
 checkForwardingEnds();
 }     
}

//******************************************************************************
//DELIVER packet

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

}//End of class

