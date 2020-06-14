//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;

import java.util.Iterator;
import java.util.Map;

//******************************************************************************
//A MULTI-COPY FREQUENCY OF ENCOUNTER BASED ROUTING PROTOCOL

public class PRoPHET extends RoutingProtocol
{
    //Instance Variables
    boolean warmFlag=false;
    public static int size;    //i and j are source and destination declared in main program
    double beta,gamma,p_encounter;
    static double [][] AgeCounter;//stores the time passed since last encounter

//******************************************************************************
//CONSTUCTOR

public  PRoPHET() {}

//******************************************************************************

@Override
public void setPerimeters()
{
    size=dtnrouting.allNodes.size();
    AgeCounter=new double[size][size];
    dtnrouting.p=new double[size][size];
    beta=0.75;
    gamma=0.75;
    p_encounter=0.5;
    for(int m=0;m<size;m++)
        for(int n=0;n<size;n++)
        {
            AgeCounter[m][n]=0;
            if(m==n)
            dtnrouting.p[m][n]=1.0; //same node has 1 as predictibility value with itself
            else
            dtnrouting.p[m][n]=0.0; //different nodes has 0 as initial predictibility value
        }
   dtnrouting.CommentsTA.append("\nWARMUP PERIOD");
}

//******************************************************************************
//UPON ENCOUNTER RAISE THE DELIVERY PREDICTABILITY

@Override
public void Encounter(int x,int y)  //x and y are sender and reciever nodes
{
    dtnrouting.p[x][y]=dtnrouting.p[x][y]+((1-dtnrouting.p[x][y])*p_encounter); //Setting probability of encountering between the two nodes
    AgeCounter[x][y]=0;   //initially when two nodes are encountered its age counter is 0
             //and when they go out of range its age increases gradually
}

//******************************************************************************
//RAISE DP WHEN INDIRECT ENCOUNTER VIA A MUTUAL FRIEND HAPPENS

@Override
public void Transitivity(int x, int y)
{
  if(dtnrouting.p[x][y]>0.75)
  {
      for(int z=0;z<size;z++)
          if(z!=x && z!=y)  //suppose z is 3rd node check that it is not x or y
          {
              if(dtnrouting.p[y][z]>0.75)   //if two nodes x and y has high probability
           //to be within range of each other and y & z has same relation wiht each other then so is the case of x &z
              dtnrouting.p[z][x]=dtnrouting.p[x][z]=dtnrouting.p[x][z]+(1-dtnrouting.p[x][z])*dtnrouting.p[x][y]*dtnrouting.p[y][z]*beta;
                           

          }
   }
}

//******************************************************************************
//DECREASE DP AS TIME OF LAST ENCOUNTER INCREASES

@Override
public void Aging(int x,int y)
{
    for(int z=0;z<size;z++)
        if(z!=x && z!=y)
        {
            //update aging of x and z
            dtnrouting.p[z][x]=dtnrouting.p[x][z]=dtnrouting.p[x][z]*Math.pow(gamma,AgeCounter[x][z]);
            AgeCounter[z][x]=AgeCounter[x][z]=AgeCounter[x][z]+0.001;  //when two nodes go away from each other its age counter is increase gradually
            //update aging of y and z
            dtnrouting.p[z][y]=dtnrouting.p[y][z]=dtnrouting.p[y][z]*Math.pow(gamma,AgeCounter[y][z]);
            AgeCounter[z][y]=AgeCounter[y][z]=AgeCounter[y][z]+0.001;  //when two nodes go away from each other its aging is increase gradually
         }
}

//******************************************************************************

public void Deliver(Node nx,Node ny)
{  
    Encounter(nx.ID-1,ny.ID-1);
    Transitivity(nx.ID-1,ny.ID-1);
    Aging(nx.ID-1,ny.ID-1);
    //Bidirectional connectivity
    DeliverMessage(nx, ny);
    DeliverMessage(ny, nx);
}

//******************************************************************************
//DELIVER MESSAGE

public void DeliverMessage(Node nx, Node ny)
{
  if(dtnrouting.isRun==true ){
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

        else  if((!ny.packetIDHash.contains(packetObj.packetName))&&((dtnrouting.p[ny.ID-1][destNode.ID-1])>(dtnrouting.p[nx.ID-1][destNode.ID-1])))
        {
         packetObj.packetBandwidth+=1; //Since packet is transfered
         ny.DestNPacket.put(packetObj,destNode);
         ny.packetIDHash.add(packetObj.packetName);
         ny.queueSizeLeft-=packetObj.packetSize;
         ny.packetTimeSlots.put(packetObj.packetName,0);
         packetObj.packetLoad+=1;
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





