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
    //Instance Variables
    RP_Performance rpp=new RP_Performance();
    public static int    index = 0, elements, latency[], load[], bw[], dr[];
    public static double sd_latency = 0, sd_load = 0, sd_bw = 0, sd_dr = 0;
    public abstract void Deliver(Node n1, Node n2);
    public static boolean transfer=false, isDestination=false;

//******************************************************************************
   
public void setPerimeters(){}
public void setPerimeters(String filename, int size){}
   
 //******************************************************************************

public static void metrixArray(int sim)
{
     elements=sim;
     latency=new int[elements];
     load=new int[elements];
     bw=new int[elements];
     dr=new int[elements];

}

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

public boolean checkTTLandSize(Node nx,Node ny,Node destNode, Packet packetObj)
{
    //If size of packet is larger than the buffer space of destination node
     if(ny.equals(destNode) && packetObj.packetSize > destNode.queueSizeLeft && packetObj.ispacketDelivered == false && packetObj.packetDelivered == 0)
        {

            if(packetObj.isLargeSize==false)
            dtnrouting.CommentsTA.append("\n"+packetObj.name+" is not delivered due to its large size");
            packetObj.isLargeSize=true;
            packetObj.packetDelivered = -1;  //The packet is expired and is of no use,it must be deleted from current buffer
            nx.queueSizeLeft+=packetObj.packetSize; // the whole space
            nx.packetIDHash.remove(packetObj.packetName);
            nx.packetTimeSlots.remove(packetObj.packetName);  nx.packetCopies.remove(packetObj.packetName);
            return true;

        }
        //if packet's TTL expires, it cannot be delivered
        else if (packetObj.packetTTL <= 0 && packetObj.ispacketDelivered == false && packetObj.packetDelivered == 0)
        {

            if(packetObj.isTTLExpired==false)
            dtnrouting.CommentsTA.append("\n"+packetObj.name+"'s TTL's expires");
            packetObj.isTTLExpired=true;
            packetObj.packetDelivered = -1;  //The packet is expired and is of no use,it must be deleted from current buffer
            nx.queueSizeLeft+=packetObj.packetSize; // the whole space
            nx.packetIDHash.remove(packetObj.packetName);
            nx.packetTimeSlots.remove(packetObj.packetName); nx.packetCopies.remove(packetObj.packetName);
            return true;
        }
        else return false;
}

//******************************************************************************

public void checkForwardingEnds()
{
        int m=dtnrouting.arePacketsDelivered.size();
        int counter=0;
        for(int h=0;h<m;h++)
            if(dtnrouting.arePacketsDelivered.get(h).ispacketDelivered==true ||dtnrouting.arePacketsDelivered.get(h).packetDelivered==-1 )
            {
                counter=counter+1;
             

            }
        if(counter==dtnrouting.arePacketsDelivered.size())
        {

            stopSimulation();
            dtnrouting.isRun=false;
           
        }
}//End of checkForwardingEnds

//******************************************************************************

public void stopSimulation()
{
      int numpacketsDelivered=0;
      for(int h=0;h<dtnrouting.arePacketsDelivered.size();h++)
      {
      //Metrics of delivered packets
        if ((dtnrouting.arePacketsDelivered.get(h).ispacketDelivered == true))
        {
            numpacketsDelivered+=1;
            //System.out.println("Number of delivered:"+numpacketsDelivered);
            dtnrouting.latency+=dtnrouting.arePacketsDelivered.get(h).packetLatency;
            dtnrouting.load+=dtnrouting.arePacketsDelivered.get(h).packetLoad;
            dtnrouting.bandwidth+=dtnrouting.arePacketsDelivered.get(h).packetBandwidth;
        }

      //Metrics of un-delivered packets
       if (dtnrouting.arePacketsDelivered.get(h).packetDelivered == -1)
       {
            dtnrouting.latency+=dtnrouting.arePacketsDelivered.get(h).maxTTL;
            dtnrouting.load+=dtnrouting.arePacketsDelivered.get(h).packetLoad;
            dtnrouting.bandwidth+=dtnrouting.arePacketsDelivered.get(h).packetBandwidth;
      }
      }
  
    dtnrouting.SIMULATION_ENDED=true;
    
    //update metrics
    dtnrouting.latency=dtnrouting.latency/dtnrouting.arePacketsDelivered.size();
    dtnrouting.load=(int)Math.ceil((float)(dtnrouting.load*1.0)/dtnrouting.arePacketsDelivered.size());
    dtnrouting.bandwidth=(int)Math.ceil((float)(dtnrouting.bandwidth*1.0)/dtnrouting.arePacketsDelivered.size());
    dtnrouting.DR=((numpacketsDelivered*100)/dtnrouting.arePacketsDelivered.size());
   // System.out.println("DR"+dtnrouting.DR);
    
    //assign metrics to arrays for results to display
    latency[index]=dtnrouting.latency;
    load[index]=dtnrouting.load;
    bw[index]=dtnrouting.bandwidth;
    dr[index]=dtnrouting.DR;
    index=index+1;
    
}

//******************************************************************************

public static void standardDeviation(int avgLatency,int avgLoad,int avgBw,int avgDR)
{
  for(int i=0;i<elements;i++)
  {   
    double diffLatency=avgLatency-latency[i];
    double diffLoad=avgLoad-load[i];
    double diffBw=avgBw-bw[i];
    double diffDr=avgDR-dr[i];
    sd_latency+=Math.pow(diffLatency,2.0);
    sd_load+=Math.pow(diffLoad,2.0);
    sd_bw+=Math.pow(diffBw,2.0);
    sd_dr+=Math.pow(diffDr,2.0);
  }
    sd_latency/=elements; sd_latency=Math.sqrt(sd_latency);
    sd_load/=elements;    sd_load=Math.sqrt(sd_load);
    sd_bw/=elements;      sd_bw=Math.sqrt(sd_bw);
    sd_dr/=elements;      sd_dr=Math.sqrt(sd_dr);
    dtnrouting.CommentsTA.append("\n\nStandard Deviation:\n");
    dtnrouting.CommentsTA.append("Latency: "+Math.floor(sd_latency)+",Load: "+Math.floor(sd_load)+"\nLinks: "+Math.floor(sd_bw)+",DR: "+Math.floor(sd_dr));
    RP_Performance.setSTDData(dtnrouting.protocolName,(int)Math.floor(sd_latency),(int)Math.floor(sd_load),(int)Math.floor(sd_bw),(int)Math.floor(sd_dr));
}

//******************************************************************************

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
}



