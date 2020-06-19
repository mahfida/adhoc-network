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
//SPRAY ADN WAIT NORMAL ROUTING SCHEME

public class SprayAndWaitN extends RoutingProtocol
{
    //Instance Variables
    dtnrouting dtn=new dtnrouting();
    static boolean start_delivery;
    Random rand;
    String ferryNode=" ";
    boolean removepacketCopy=false;

//******************************************************************************
//CONSTRUCTOR

public SprayAndWaitN()
    {         start_delivery=false; }

//******************************************************************************

public void Deliver(Node nx,Node ny)    
//x and y are intermediate sender and receiver
{
    //Bidirectional connectivity
    DeliverMessage(nx, ny);
    DeliverMessage(ny, nx);
}

//******************************************************************************
//DELIVER MESSAGE

public void DeliverMessage(Node nx, Node ny)
{
if(dtnrouting.isRun==true)
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
        else if (packetObj.packetSize <= dtnrouting.linkCapacities[nx.ID - 1][ny.ID - 1]) {
        
        //If encountered Node has not yet received packet, packet is yet not delivered,in ny's buffer enough space is free to occupy the packet and packet TTL is not expired 
        if((ny.packetIDHash.contains(packetObj.packetName)==false)&&(ny.queueSizeLeft>packetObj.packetSize)&&(packetObj.ispacketDelivered==false)&&(packetObj.packetTTL>0))
        {
                if(ny==destNode)
                {
                    int bcopy=nx.packetCopies.get(packetObj.packetName);

                    packetObj.packetBandwidth+=1;
                    ny.DestNPacket.put(packetObj,null);
                    ny.packetIDHash.add(packetObj.packetName);
                    ny.packetTimeSlots.put(packetObj.packetName,0);
                    ny.packetCopies.put(packetObj.packetName,1);
                    ny.queueSizeLeft-=packetObj.packetSize;
                    packetObj.ispacketDelivered=true;
                    dtnrouting.linkCapacities[nx.ID-1][ny.ID-1] -= packetObj.packetSize;
                    
                    //update nx
                    nx.packetCopies.put(packetObj.packetName, bcopy-1);
                    if((bcopy-1)<1)
                    {
                    nx.queueSizeLeft += packetObj.packetSize; // the whole space
                    nx.packetTimeSlots.remove(packetObj.packetName);
                    }
                    i.remove();
                    dtnrouting.CommentsTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.name);    }

                //with end nodes within same region
                 else if(nx.packetCopies.get(packetObj.packetName)>1 )
                 if (ny.name.startsWith("R")||ny.name.startsWith("D"))
                 {
                            int bcopy=nx.packetCopies.get(packetObj.packetName);
                            
                            ny.packetCopies.put(packetObj.packetName,1);
                            nx.packetCopies.put(packetObj.packetName, bcopy-1);
                            dtnrouting.linkCapacities[nx.ID-1][ny.ID-1] -= packetObj.packetSize;
                            packetObj.packetBandwidth+=1;
                            
                            ny.DestNPacket.put(packetObj,destNode);
                            ny.packetIDHash.add(packetObj.packetName);
                            ny.packetTimeSlots.put(packetObj.packetName,0);
                            ny.packetCopies.put(packetObj.packetName,1);
                            ny.queueSizeLeft-=packetObj.packetSize;
                            dtnrouting.CommentsTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.name);            }
                }}
           
        }

    }
    //Check whether forwarding of packets have ended
    checkForwardingEnds();
    }
}  //end of method name

//******************************************************************************

}// End of class
