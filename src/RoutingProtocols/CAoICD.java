//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;

import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//COMULATIVE AVERAGE BASED ROUTING PROTOCOL

public class CAoICD extends RoutingProtocol
{
    //Instance variables
    boolean warmFlag=false;
    public static int size;    //i and j are source and destination declared in main program
    long encounterDuration[][],noOfEncounters[][],lastPeriodStartTime[][],encounterTimeLeft[][],encounterPeriod[][],lastEncounterTime[][],timeLeftNx=-1,timeLeftNy=-1;
    double CAoICDuration[][];//stores the time passed since last encounter
    static double firstEncounterPosition[][][];

//******************************************************************************
//CONSTRUCTOR

public CAoICD() {}

//******************************************************************************

@Override
public void setPerimeters()
{
    size=dtnrouting.allNodes.size();
    CAoICDuration=new double[size][size];
    firstEncounterPosition=new double[size][size][4];
    encounterDuration=new long[size][size];
    noOfEncounters=new long[size][size];
    encounterTimeLeft=new long[size][size];
    encounterPeriod=new long[size][size];
    lastPeriodStartTime=new long[size][size];
    lastEncounterTime=new long[size][size];

    //Intialize encounterDuration and CAoICD
    for(int m=0;m<size;m++)
        for(int n=0;n<size;n++)
        {
            CAoICDuration[m][n]=-1;
            encounterDuration[m][n]=0;
            noOfEncounters[m][n]=0;
            encounterTimeLeft[m][n]=-1;
            encounterPeriod[m][n]=-1;
            lastPeriodStartTime[m][n]=-1;
            lastEncounterTime[m][n]=0;
        }
   dtnrouting.CommentsTA.append("\nWARMUP PERIOD");
}

//******************************************************************************

public void updateAICDuration(int m,int n)
{
    //Increase number of encounters by 1
    noOfEncounters[n][m]=noOfEncounters[m][n]=noOfEncounters[m][n]+1;
    
    //Update encounter duration
    encounterDuration[n][m]=encounterDuration[m][n]= dtnrouting.simulationTime- lastEncounterTime[m][n];
    lastEncounterTime[n][m]=lastEncounterTime[m][n]=dtnrouting.simulationTime;
    //If this is first
    if(CAoICDuration[m][n]==-1) CAoICDuration[n][m]=CAoICDuration[m][n]=0;
    //Update mutual interContactDuration
    CAoICDuration[n][m]=CAoICDuration[m][n]=
    (encounterDuration[m][n]+(noOfEncounters[m][n]-1)*CAoICDuration[m][n])/(noOfEncounters[m][n]);

}

//******************************************************************************

public void findPeriodOfEncounters(Node nx,Node ny)
{
int m=nx.ID-1; int n=ny.ID-1;
//Start of period

if(encounterPeriod[m][n]==-1)
{
    encounterPeriod[n][m]=encounterPeriod[m][n]=dtnrouting.simulationTime;
    firstEncounterPosition[m][n][0]=nx.nodeX;
    firstEncounterPosition[m][n][1]=nx.nodeY;
    firstEncounterPosition[m][n][2]=ny.nodeX;
    firstEncounterPosition[m][n][3]=ny.nodeY;
    lastPeriodStartTime[n][m]=lastPeriodStartTime[m][n]=dtnrouting.simulationTime;
  
}
//Total period
else if(firstEncounterPosition[m][n][0]==nx.nodeX &&
     firstEncounterPosition[m][n][1]==nx.nodeY &&
     firstEncounterPosition[m][n][2]==ny.nodeX &&
     firstEncounterPosition[m][n][3]==ny.nodeY)
  {
   encounterPeriod[n][m]= encounterPeriod[m][n]=dtnrouting.simulationTime-encounterPeriod[m][n];
   lastPeriodStartTime[n][m]=lastPeriodStartTime[m][n]=dtnrouting.simulationTime;
  }



}

//******************************************************************************

 public void minExpectedDelay(Node nx,Node ny,Node destNode ){
//Time left for node x to encounter destination node
int i=nx.ID-1,j=ny.ID-1,k=destNode.ID-1;
long timex=(dtnrouting.simulationTime-lastPeriodStartTime[i][k])%((long)CAoICDuration[i][k]);
long timey=(dtnrouting.simulationTime-lastPeriodStartTime[j][k])%((long)CAoICDuration[j][k]);
if(timex==0)
timeLeftNx=0;
else
    timeLeftNx=((long)CAoICDuration[i][k])-timex;

//Time left for node x to encounter destination node
if(timey==0)
timeLeftNy=0;
else
    timeLeftNy=((long)CAoICDuration[j][k])-timey;

 }

//******************************************************************************
 
public void Deliver(Node nx,Node ny)    //x and y are intermediet sender and reciever
{

   findPeriodOfEncounters(nx,ny);
   updateAICDuration(nx.ID-1,ny.ID-1);
    //Bidirectional connectivity
    DeliverMessage(nx, ny);
    DeliverMessage(ny, nx);

}

//******************************************************************************

public void DeliverMessage(Node nx, Node ny)
{
  if(dtnrouting.isRun==true ){

  if(NodeMovement.warmupPeriod==size) //Warming Period finished
  {
     
      if(!warmFlag)
      {
          dtnrouting.CommentsTA.append(" FINISHED");
          for(int h=0;h<dtnrouting.arePacketsDelivered.size();h++)
            {
                Packet packetObj=dtnrouting.arePacketsDelivered.get(h);
                packetObj.packetTTL=packetObj.maxTTL;
                packetObj.packetLatency=0;
            }
          dtnrouting.delay=0;
      }
      warmFlag=true;
   if(!nx.DestNPacket.isEmpty())
   {
    //Update the time spent by packets within a node nx
   nx.updatepacketTimestamp(nx);
   //Transfer the packets

   for (Iterator<Map.Entry<Packet,Node>> i = nx.DestNPacket.entrySet().iterator(); i.hasNext(); )
    {
         Map.Entry<Packet,Node> entry = i.next();
         Packet packetObj = entry.getKey();
         Node   destNode = entry.getValue();
         transfer=false;
         
        //If destination has not enough size to receIve packet
        //OR if its TTL is expired, , it Packet cannot be sent
        if(checkTTLandSize(nx,ny,destNode,packetObj));

        //If destination has enough size to receive packet
        //and if its TTL is not expired, , it Packet can be sent
        // if contact duration is enough to transfer the message
        else if(packetObj.packetSize <= dtnrouting.linkCapacities[nx.ID - 1][ny.ID - 1])
        {
	        //If encountered Node has not yet received packet, Packet is yet not delivered,in ny's buffer enough space is free to occupy the Packet and Packet TTL is not expired
	        if((!ny.packetIDHash.contains(packetObj.packetName))&&(ny.queueSizeLeft>packetObj.packetSize)&&(packetObj.ispacketDelivered==false)&&(packetObj.packetTTL>0))
	        {
	            //if ny is destination
	            if(destNode.equals(ny))
	            {
	                ny.DestNPacket.put(packetObj,null);
	                packetObj.ispacketDelivered=true;
	                deliverPacket(nx,ny,packetObj);
	                dtnrouting.linkCapacities[nx.ID-1][ny.ID-1]-=packetObj.packetSize;
	                i.remove();
	            }
	            
	            //if ny is not a destination
	            else
	            {
	               minExpectedDelay(nx,ny,destNode);
	               if((CAoICDuration[ny.ID-1][destNode.ID-1]>-1)&&(timeLeftNx>timeLeftNy))
	            	   transfer = true;
	               
	               //Within same node Packet be forwarded to regular or fixed node
	               if(transfer &&(ny.name.startsWith("R")||(nx.name.startsWith("D"))))
	                {
	                     packetObj.packetBandwidth+=1;
	                     ny.DestNPacket.put(packetObj, destNode);
	                     dtnrouting.linkCapacities[nx.ID-1][ny.ID-1]-=packetObj.packetSize;
	                     deliverPacket(nx,ny,packetObj);
	                     i.remove();
	      }}}}

        }

    }
//Check whether forwarding of packets have ended
 checkForwardingEnds();

   }}
      
}//End of method

//******************************************************************************

}//End of class

