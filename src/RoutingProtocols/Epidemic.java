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
    boolean removepacketcopy=false,forward=false;

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
 
   //if nx has packet and ny has to receive it
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
        if((!ny.packetIDHash.contains(packetObj.packetName))&& packetObj.packetSize<=dtnrouting.linkCapacities[nx.ID-1][ny.ID-1]){
       //If encountered Node has not yet received packet, packet is yet not delivered,in ny's buffer enough space is free to occupy the packet and packet TTL is not expired 
        if((ny.queueSizeLeft>packetObj.packetSize)&&(packetObj.ispacketDelivered==false))
            { 
                packetObj.packetBandwidth+=1; //Since packet is transfered
                packetObj.packetLoad+=1;    //packet copy increments
                if(destNode==ny)
                {
                    ny.DestNPacket.put(packetObj,null);
                    ny.packetIDHash.add(packetObj.packetName);
                    ny.queueSizeLeft-=packetObj.packetSize;
                    ny.packetTimeSlots.put(packetObj.packetName,0);
                    packetObj.ispacketDelivered=true;
                    dtnrouting.linkCapacities[nx.ID-1][ny.ID-1] -= packetObj.packetSize;
                    

                    //update nx
                    nx.queueSizeLeft+=packetObj.packetSize; // the whole space
                    nx.packetIDHash.remove(packetObj.packetName);
                    nx.packetTimeSlots.remove(packetObj.packetName);
                    nx.packetCopies.remove(packetObj.packetName);

                    
                    packetObj.packetLoad-=1;    //packetCopy decrements
                    i.remove();
                    dtnrouting.CommentsTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.name);
                }
                else if (ny.name.startsWith("R")||ny.name.startsWith("D"))  
                	   {

                	    ny.DestNPacket.put(packetObj,destNode);
                	    ny.packetIDHash.add(packetObj.packetName);
                	    ny.packetTimeSlots.put(packetObj.packetName, 0);
                	    ny.packetCopies.put(packetObj.packetName,1);
                	    ny.queueSizeLeft-=packetObj.packetSize;
                	    dtnrouting.linkCapacities[nx.ID-1][ny.ID-1] -= packetObj.packetSize;
                        
                	    //increase the number of copies made by nx of this packetObj
                	    nx.packetCopies.put(packetObj.packetName,nx.packetCopies.get(packetObj.packetName)+1);
                	    dtnrouting.CommentsTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.name);
                	   	}
             }} //delivery of packet ends
        }//End of for loop
          
} // If the nx has packets to forward

//Check whether forwarding of packets have ended
 checkForwardingEnds();
}
}//end of method
}//end of class
