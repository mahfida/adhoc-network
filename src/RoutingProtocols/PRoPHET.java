//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import MovementPattern.NodeMovement;
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
    Encounter(nx.nodeID-1,ny.nodeID-1);
    Transitivity(nx.nodeID-1,ny.nodeID-1);
    Aging(nx.nodeID-1,ny.nodeID-1);
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
          for(int h=0;h<dtnrouting.areBundlesDelivered.size();h++)
            {
                Bundle bundleObj=dtnrouting.areBundlesDelivered.get(h);
                bundleObj.bundleTTL=bundleObj.maxTTL;
                bundleObj.bundleLatency=0;
            }
          dtnrouting.delay=0;
      }
      warmFlag=true;
    //if nx has bundle and ny has to recieve it
   if(!nx.DestNBundle.isEmpty())
   {
    //Update the time spent by bundles within a node nx
         nx. updateBundleTimestamp(nx);
    //Transfer the bundels

   for (Iterator<Map.Entry<Bundle,Node>> i = nx.DestNBundle.entrySet().iterator(); i.hasNext(); )
    {
    Map.Entry<Bundle,Node> entry = i.next();
    Bundle bundleObj = entry.getKey();
    Node   destNode = entry.getValue();
          
    //If destiantion has not enough size to recieve bundle
    //OR if its TTL is expired, , it bundle cannot be sent

     if(checkTTLandSize(nx,ny,destNode,bundleObj)==true);

    //If destiantion has enough size to recieve bundle
    //and if its TTL is not expired, , it bundle can be sent
    // if contact duration is enough to transfer the message
    else
    if(bundleObj.bundleSize<=dtnrouting.contactDuration[nx.nodeID-1][ny.nodeID-1]){

    //If encountered Node has not yet recieved bundle, bundle is yet not delivered,in ny's buffer enough space is free to occupy the bundle and bundle TTL is not expired
    if((ny.bundleIDHash.contains(bundleObj.bundleName) == false) && (ny.queueSizeLeft > bundleObj.bundleSize) && (bundleObj.isBundleDelivered == false) && (bundleObj.bundleTTL > 0))
    {
        if(ny==destNode)
        {
        bundleObj.bundleBandwidth+=1; //Since bundle is transfered
        ny.DestNBundle.put(bundleObj,null);
        ny.bundleIDHash.add(bundleObj.bundleName);
        ny.queueSizeLeft-=bundleObj.bundleSize;
        ny.bundleTimeSlots.put(bundleObj.bundleName,0);
        bundleObj.isBundleDelivered=true;
        //update nx
        nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
        nx.bundleIDHash.remove(bundleObj.bundleName);
        nx.bundleTimeSlots.remove(bundleObj.bundleName);

        i.remove();
        //Display Result
          dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
        }

        else  if((!ny.bundleIDHash.contains(bundleObj.bundleName))&&((dtnrouting.p[ny.nodeID-1][destNode.nodeID-1])>(dtnrouting.p[nx.nodeID-1][destNode.nodeID-1])))
        {
         bundleObj.bundleBandwidth+=1; //Since bundle is transfered
         ny.DestNBundle.put(bundleObj,destNode);
         ny.bundleIDHash.add(bundleObj.bundleName);
         ny.queueSizeLeft-=bundleObj.bundleSize;
         ny.bundleTimeSlots.put(bundleObj.bundleName,0);
         bundleObj.bundleLoad+=1;
             //Display Result
         dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
        }
     }
    }
   }//End of for loop
}

}

//Check whether forwading of bundles have ended
checkForwardingEnds();
  }

 }   //end of Deliver()

//******************************************************************************

}//End of class





