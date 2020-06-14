//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;

import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//MULTI-COPY PROPHET

public class MPRoPHET extends PRoPHET{
static double[][] AvgICDuration; //Average inter contact duration between any two nodes
int cases;
public MPRoPHET()
    {
    }
    @Override
public void setPerimeters()
        
    {
    super.setPerimeters();
    AvgICDuration=new double[size][size];  // The average inter-contact duration between any two nodes
        for(int m=0;m<size;m++)
            for(int n=0;n<size;n++)
                      AvgICDuration[m][n]=0;
    }

@Override
public void Encounter(int x,int y)  //x and y are sender and reciever nodes
{
if(AgeCounter[x][y]>0) // The current inter-contact duration is stored in k
    {
    if(AvgICDuration[x][y]>0)
        //Update the value of t that stores InterContact Durations
        AvgICDuration[x][y]=(AvgICDuration[x][y]+AgeCounter[x][y])/2;  //if node x encoutners y multiple times in history
    else
        AvgICDuration[x][y]=AgeCounter[x][y]; //Maximum value of time lapse after which x and y encounters

    }
super.Encounter(x, y);//to update preditibility value and set AgeCounter to zero
}

@Override
public void Deliver(Node nx,Node ny)
{
    Encounter(nx.ID-1,ny.ID-1);
    Transitivity(nx.ID-1,ny.ID-1);
    Aging(nx.ID-1,ny.ID-1);
   
  if(NodeMovement.warmupPeriod==size) //Warming Period finished
    {
      if(!warmFlag)
      {
        for(int h=0;h<dtnrouting.arePacketsDelivered.size();h++)
        {
            Packet packetObj=dtnrouting.arePacketsDelivered.get(h);
            packetObj.packetTTL=packetObj.maxTTL;
            packetObj.packetLatency=0;
        }
          dtnrouting.CommentsTA.append(" FINISHED\n");
          dtnrouting.delay=0;
      }
      warmFlag=true;
   if(!nx.DestNPacket.isEmpty())
   {
    //Update the time spent by packets within a node nx
   nx. updatepacketTimestamp(nx);
   //Transfer the bundels
   for (Iterator<Map.Entry<	Packet,Node>> i = nx.DestNPacket.entrySet().iterator(); i.hasNext(); )
    {
        Map.Entry<Packet,Node> entry = i.next();
        Packet packetObj = entry.getKey();
        Node   destNode = entry.getValue();
        if((packetObj.ispacketDelivered==false)   // if contact duration is enough to transfer the message
        && (packetObj.packetSize<=dtnrouting.contactDuration[nx.ID-1][ny.ID-1]))
        {
            //If destination encounters, hands over packet to it
            if(ny==destNode)
                   cases=1;//handle packet to destination and delete its copy

            else
               if((dtnrouting.p[ny.ID-1][destNode.ID-1])>(dtnrouting.p[nx.ID-1][destNode.ID-1]))
               {
                   if((AvgICDuration[nx.ID-1][destNode.ID-1]>0.0) && ((AgeCounter[nx.ID-1][destNode.ID-1])>(0.67*AvgICDuration[nx.ID-1][destNode.ID-1]))&&(dtnrouting.p[nx.ID-1][destNode.ID-1]>0.0))
                    cases=2;//give packet to ny and keep its copy with itself too
                   else
                    cases=3; //give packet to ny and delete its local copy
               }
             else   //if ny may meet destination soon
                 if((AvgICDuration[ny.ID-1][destNode.ID-1]>0.0) && ((AgeCounter[ny.ID-1][destNode.ID-1])>(0.67*AvgICDuration[ny.ID-1][destNode.ID-1]))&&(dtnrouting.p[ny.ID-1][destNode.ID-1]>0.0))
                     cases=2; // there is chances that ny may meet sooner than nx with ny, so hand over a copy to ny and keep one with nx
           
            //Hand Over the packet to encountered Node
            packetObj.packetBandwidth+=1; //Since packet is transfered
            ny.packetIDHash.add(packetObj.packetName);
            ny.queueSizeLeft-=packetObj.packetSize;
            packetObj.packetTTL-=1;

            //Perform case specific functions
            switch(cases)
           {
                case 1:
                   //deliver packet to destination
                   ny.DestNPacket.put(packetObj,null);
                   packetObj.ispacketDelivered=true;
                   packetObj.packetLatency=dtnrouting.delay;
                   //Delete packet copy from nx
                   nx.queueSizeLeft+=packetObj.packetSize; // the whole space
                   nx.packetIDHash.remove(packetObj.packetName);
                   i.remove();
                   break;
               
                case 2:
                   //deliver packet to ny and keep copy with nx too
                   ny.DestNPacket.put(packetObj,destNode);
                   break;

                case 3:
                   ny.DestNPacket.put(packetObj,destNode);
                   //Delete packet copy from nx
                   nx.queueSizeLeft+=packetObj.packetSize; // the whole space
                   nx.packetIDHash.remove(packetObj.packetName);
                   i.remove();
                   break;
               }
            	//Display Result
            	dtnrouting.CommentsTA.append("\n"+nx.ID+" ---> "+ny.ID+":"+packetObj.name);
            }

           
         }
       }
    }
//Check whether forwarding of packets have ended
 checkForwardingEnds();
} //End of Deliver()
} //End of MCPRoPHET


