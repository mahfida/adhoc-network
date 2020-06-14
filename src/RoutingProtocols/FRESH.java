//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;

import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//FRESH ROUTING ALGORITHM - BASED ON RECENCY OF ENCOUNTERS

public class FRESH extends RoutingProtocol
{
    //Instance Variables
    boolean warmFlag=false;
    public static int size;    //i and j are source and destination declared in main program
    static double [][] LastEncounterTime;//stores the last encounter Time

//******************************************************************************
//CONSTRUCTOR
public   FRESH() {}

//******************************************************************************

@Override
public void setPerimeters()
{
    size=dtnrouting.allNodes.size();
    LastEncounterTime=new double[size][size];
    
    for(int m=0;m<size;m++)
        for(int n=0;n<size;n++)
           LastEncounterTime[m][n]=-1; //Initially last encounter time is unknown shown by -1
    dtnrouting.CommentsTA.append("\nWARMUP PERIOD");
}

//******************************************************************************

public void updateLastEncounterTime(int x,int y)
{
    LastEncounterTime[y][x]=LastEncounterTime[y][x]=dtnrouting.simulationTime;
}

//******************************************************************************
public void Deliver(Node nx,Node ny)    //x and y are intermediet sender and reciever
{
    updateLastEncounterTime(nx.ID-1,ny.ID-1);
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
  if(NodeMovement.warmupPeriod==size) //Warming Period finished
  {
      if(!warmFlag)
      {
          dtnrouting.CommentsTA.append(" FINISHED ");
          for(int h=0;h<dtnrouting.arePacketsDelivered.size();h++)
            {
                Packet packetObj=dtnrouting.arePacketsDelivered.get(h);
                packetObj.packetTTL=packetObj.maxTTL;
                packetObj.packetLatency=0;
            }
          dtnrouting.delay=0;
      }
      warmFlag=true;
      //if nx has packet and ny has to recieve it
       if(!nx.DestNPacket.isEmpty())
       {
        //Update the time spent by packets within a node nx
             nx. updatepacketTimestamp(nx);
        //Transfer the bundels

       for (Iterator<Map.Entry<Packet,Node>> i = nx.DestNPacket.entrySet().iterator(); i.hasNext(); )
        {
        Map.Entry<Packet,Node> entry = i.next();
        Packet packetObj = entry.getKey();
        Node   destNode = entry.getValue();

        //If destiantion has not enough size to recieve packet
        //OR if its TTL is expired, , it packet cannot be sent

         if(checkTTLandSize(nx,ny,destNode,packetObj)==true);

        //If destiantion has enough size to recieve packet
        //and if its TTL is not expired, , it packet can be sent
        // if contact duration is enough to transfer the message
        else
        if(packetObj.packetSize<=dtnrouting.contactDuration[nx.ID-1][ny.ID-1]){

        //If encountered Node has not yet recieved packet, packet is yet not delivered,in ny's buffer enough space is free to occupy the packet and packet TTL is not expired
        if((ny.packetIDHash.contains(packetObj.packetName) == false) && (ny.queueSizeLeft > packetObj.packetSize) && (packetObj.ispacketDelivered == false) && (packetObj.packetTTL > 0))
        {
            if(ny==destNode)
            {
                packetObj.packetBandwidth+=1; //Since packet is transfered
                ny.DestNPacket.put(packetObj,null);
                ny.packetIDHash.add(packetObj.packetName);
                ny.queueSizeLeft-=packetObj.packetSize;
                ny.packetTimeSlots.put(packetObj.packetName,0);
                packetObj.ispacketDelivered=true;
                //update nx
                nx.queueSizeLeft+=packetObj.packetSize; // the whole space
                nx.packetIDHash.remove(packetObj.packetName);
                nx.packetTimeSlots.remove(packetObj.packetName);
                i.remove();
                //Display Result
                dtnrouting.CommentsTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.name);
            }

            else  if((!ny.packetIDHash.contains(packetObj.packetName))&&(LastEncounterTime[nx.ID-1][destNode.ID-1]<LastEncounterTime[ny.ID-1][destNode.ID-1]))
            {
                 packetObj.packetBandwidth+=1; //Since packet is transfered
                 ny.DestNPacket.put(packetObj,destNode);
                 ny.packetIDHash.add(packetObj.packetName);
                 ny.queueSizeLeft-=packetObj.packetSize;
                 ny.packetTimeSlots.put(packetObj.packetName,0);

                 //update nx
                 nx.queueSizeLeft+=packetObj.packetSize; // the whole space
                // nx.packetIDHash.remove(packetObj.packetName);
                 nx.packetTimeSlots.remove(packetObj.packetName);
                 i.remove();
                 //Display Result
                 dtnrouting.CommentsTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.name);
            }
        }
        }
     }//End of for loop
   }
}

//Check whether forwading of packets have ended
checkForwardingEnds();
}
}   //end of Deliver()

//******************************************************************************

}//End of class






