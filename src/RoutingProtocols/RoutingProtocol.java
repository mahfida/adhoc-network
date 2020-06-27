//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import Results.*;

//******************************************************************************
//BASE CLASS FOR ALL ROUTING PROTOCOLS

public abstract class RoutingProtocol
{

    public abstract void DeliverData(Node n1, Node n2);
    public static boolean transfer=false, isDestination=false;

//******************************************************************************
   
public void setPerimeters(){}
public void setPerimeters(String filename, int size){}
   
 //******************************************************************************

//******************************************************************************

public void Transitivity(int i, int j)
{
     throw new UnsupportedOperationException("Not yet implemented");
}

//******************************************************************************

public void Aging(int i, int j)
{
     throw new UnsupportedOperationException("Not yet implemented");
}

//******************************************************************************

public void Encounter(int i, int j)
{
        throw new UnsupportedOperationException("Not yet implemented");
}

//******************************************************************************

public void ContactCounter(int i, int j){}

//******************************************************************************

public boolean expiredTTL_LargeSize(Node nx,Node ny, Packet packetObj)
{
         // if packet's TTL expires remove it from the node's memory
		 if(packetObj.isTTLExpired==true || packetObj.ispacketDelivered==true) {
		   
		   if(packetObj.packetTTL==0)
		   nx.queueSizeLeft+=packetObj.packetSize; // the whole space
		   
		   nx.packetIDHash.remove(packetObj.packetName);
		   //nx.packetTimeSlots.remove(packetObj.packetName);
		   nx.packetCopies.remove(packetObj.packetName); 
		   return true; } 
		 
		 //If size of packet is smaller than the buffer space of the node
		 // and packet be transmitted in the current slot
		 // and ny does not contain the packet
		 if(packetObj.packetSize <= ny.queueSizeLeft &&
			packetObj.packetSize <= nx.capacity && 
			!ny.packetIDHash.contains(packetObj.packetName))
	       return false;
        else return true;
}




//******************************************************************************

public void deliver_Destination(Node nx, Node ny, Packet packetObj)
{
	 		// Give message to destination
		    //Since packet is transfered
			packetObj.packetReliability = (Math.min(packetObj.packetReliability, nx.reliability));
			packetObj.packetHops+=1;
			
            ny.DestNPacket.put(packetObj,null);
            ny.packetIDHash.add(packetObj.packetName);
            //ny.packetTimeSlots.put(packetObj.packetName, 0);
            ny.packetCopies.put(packetObj.packetName,1);
            //ny.queueSizeLeft-=packetObj.packetSize;
            dtnrouting.deliveryTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.packetName);
            
            dtnrouting.NumPacketsDeliverExpired+=1;
            // packet delivered
            packetObj.ispacketDelivered=true;
            //System.out.println(packetObj.packetName + " is delivered");
            
            //update nx memory 
     		nx.capacity -= packetObj.packetSize;
     		nx.DestNPacket.remove(packetObj);
            nx.queueSizeLeft+=packetObj.packetSize; // the whole space            
            nx.packetIDHash.remove(packetObj.packetName);
            nx.packetCopies.remove(packetObj.packetName);
}

//******************************************************************************

public void deliver_Relay(Node nx, Node ny, Node destNode, Packet packetObj, boolean nx_remove_packet)
{
	 	  
		  //Since packet is transfered
		  packetObj.packetReliability = (Math.min(packetObj.packetReliability, nx.reliability));
		  packetObj.packetHops+=1; 
		  
          // Give message to relay
          ny.DestNPacket.put(packetObj,destNode);
          ny.packetIDHash.add(packetObj.packetName);
          //ny.packetTimeSlots.put(packetObj.packetName, 0);
          ny.packetCopies.put(packetObj.packetName,1);
          ny.queueSizeLeft-=packetObj.packetSize;
          dtnrouting.transferTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.packetName);
          
          
          //update nx memory 
   		  nx.capacity -= packetObj.packetSize;
   		  
   		  if(nx_remove_packet){
   		  nx.DestNPacket.remove(packetObj);
          nx.queueSizeLeft+=packetObj.packetSize; // the whole space            
          //nx.packetIDHash.remove(packetObj.packetName);
          nx.packetCopies.remove(packetObj.packetName);}
}




}



