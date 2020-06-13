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
    public static int index=0,elements,latency[],load[],bw[],dr[];
    public static double sd_latency=0,sd_load=0,sd_bw=0,sd_dr=0;
    public abstract void Deliver(Node n1,Node n2);

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

public boolean checkTTLandSize(Node nx,Node ny,Node destNode, Bundle bundleObj)
{
    //If size of bundle is larger than the buffer space of destination node
     if(ny.equals(destNode) && bundleObj.bundleSize > destNode.queueSizeLeft && bundleObj.isBundleDelivered == false && bundleObj.bundleDelivered == 0)
        {

            if(bundleObj.isLargeSize==false)
            dtnrouting.CommentsTA.append("\n"+bundleObj.bundleName+" is not delivered due to its large size");
            bundleObj.isLargeSize=true;
            bundleObj.bundleDelivered = -1;  //The bundle is expired and is of no use,it must be deleted from current buffer
            nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
            nx.bundleIDHash.remove(bundleObj.bundleName);
            nx.bundleTimeSlots.remove(bundleObj.bundleName);  nx.bundleCopies.remove(bundleObj.bundleName);
            return true;

        }
        //if bundle's TTL expires, it cannot be delivered
        else if (bundleObj.bundleTTL <= 0 && bundleObj.isBundleDelivered == false && bundleObj.bundleDelivered == 0)
        {

            if(bundleObj.isTTLExpired==false)
            dtnrouting.CommentsTA.append("\n"+bundleObj.bundleName+"'s TTL's expires");
            bundleObj.isTTLExpired=true;
            bundleObj.bundleDelivered = -1;  //The bundle is expired and is of no use,it must be deleted from current buffer
            nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
            nx.bundleIDHash.remove(bundleObj.bundleName);
            nx.bundleTimeSlots.remove(bundleObj.bundleName); nx.bundleCopies.remove(bundleObj.bundleName);
            return true;
        }
        else return false;
}

//******************************************************************************

public void checkForwardingEnds()
{
        int m=dtnrouting.areBundlesDelivered.size();
        int counter=0;
        for(int h=0;h<m;h++)
            if(dtnrouting.areBundlesDelivered.get(h).isBundleDelivered==true ||dtnrouting.areBundlesDelivered.get(h).bundleDelivered==-1 )
            {
                counter=counter+1;
             

            }
        if(counter==dtnrouting.areBundlesDelivered.size())
        {

            stopSimulation();
            dtnrouting.isRun=false;
           
        }
}//End of checkForwardingEnds

//******************************************************************************

public void stopSimulation()
{
      int numBundlesDelivered=0;
      for(int h=0;h<dtnrouting.areBundlesDelivered.size();h++)
      {
      //Metrics of delivered bundles
        if ((dtnrouting.areBundlesDelivered.get(h).isBundleDelivered == true))
        {
            numBundlesDelivered+=1;
            //System.out.println("Number of delivered:"+numBundlesDelivered);
            dtnrouting.latency+=dtnrouting.areBundlesDelivered.get(h).bundleLatency;
            dtnrouting.load+=dtnrouting.areBundlesDelivered.get(h).bundleLoad;
            dtnrouting.bandwidth+=dtnrouting.areBundlesDelivered.get(h).bundleBandwidth;
        }

      //Metrics of undelivered bundles
       if (dtnrouting.areBundlesDelivered.get(h).bundleDelivered == -1)
       {
            dtnrouting.latency+=dtnrouting.areBundlesDelivered.get(h).maxTTL;
            dtnrouting.load+=dtnrouting.areBundlesDelivered.get(h).bundleLoad;
            dtnrouting.bandwidth+=dtnrouting.areBundlesDelivered.get(h).bundleBandwidth;
      }
      }
  
    dtnrouting.simEnded=true;
    
    //update metrics
    dtnrouting.latency=dtnrouting.latency/dtnrouting.areBundlesDelivered.size();
    dtnrouting.load=(int)Math.ceil((float)(dtnrouting.load*1.0)/dtnrouting.areBundlesDelivered.size());
    dtnrouting.bandwidth=(int)Math.ceil((float)(dtnrouting.bandwidth*1.0)/dtnrouting.areBundlesDelivered.size());
    dtnrouting.DR=((numBundlesDelivered*100)/dtnrouting.areBundlesDelivered.size());
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

public boolean deviceBasedBundleTransfer(Node nx, Node ny,Node destNode, Bundle bundleObj,boolean forward)
{
    boolean returnValue=false;
    //if single ferry is used
    if(DeviceBasedSettings.ferryType.equals("Single-Ferry"))
    {
       //if destination is ferry
       if((ny.name.startsWith("F"))||(nx.name.startsWith("F") && ny.nodeRegion.equals(destNode.nodeRegion)&&(ny.nodeRegion.getNetworkType().equals("Mobile"))))
       {   //if encountered node is a ferry then deliver bundle to is
                    ny.DestNBundle.put(bundleObj,destNode);
                    deliverBundle(nx,ny,bundleObj);
                    returnValue=true;
                    return(returnValue);
       }

    }

    if(DeviceBasedSettings.ferryType.equals("Pigeon"))
    {
        if( ny.name.substring(0, 2).equals("CH")&& nx.nodeRegion.equals(bundleObj.sourceNode.nodeRegion))
            {
                    //CH of source region
                      ny.DestNBundle.put(bundleObj,destNode);
                      deliverBundle(nx,ny,bundleObj);
                      returnValue=true;
                      return(returnValue);

            }
        if(nx.name.substring(0,2).equals("CH")&& (ny.name.substring(0, 1).equals("F") && !destNode.nodeRegion.equals(ny.nodeRegion)))
        {
                    //when CH encounters source Ferry
                    ny.DestNBundle.put(bundleObj,destNode);
                    deliverBundle(nx,ny,bundleObj);
                       returnValue=true;
                       return(returnValue);
        }
         if(ny.name.substring(0,2).equals("CH")&& (nx.name.substring(0, 1).equals("F") && destNode.nodeRegion.equals(ny.nodeRegion) && !nx.nodeRegion.equals(ny.nodeRegion)))
         {
                 //when source Ferry encounters destination CH
                    ny.DestNBundle.put(bundleObj,destNode);
                    deliverBundle(nx,ny,bundleObj);
                      returnValue=true;
                      return(returnValue);
         }
        if(forward && ny.name.substring(0,2).equals("CH")&& (ny.name.startsWith("R"))&& ny.nodeRegion.equals(destNode.nodeRegion))
               {
                 //when destination CH encounters a regular node in destination region
                    ny.DestNBundle.put(bundleObj,destNode);
                    deliverBundle(nx,ny,bundleObj);
                    returnValue=true;
                    return(returnValue);
         }
    }
    return(returnValue);
}

//******************************************************************************

public void deliverBundle(Node nx, Node ny, Bundle bundleObj)
{
            bundleObj.bundleBandwidth+=1; //Since bundle is transfered
            ny.bundleIDHash.add(bundleObj.bundleName);
            ny.bundleTimeSlots.put(bundleObj.bundleName, 0);
            ny.bundleCopies.put(bundleObj.bundleName,1);
            ny.queueSizeLeft-=bundleObj.bundleSize;
            dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
             //update nx
            nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space            nx.bundleIDHash.remove(bundleObj.bundleName);
            nx.bundleTimeSlots.remove(bundleObj.bundleName);
            nx.bundleCopies.remove(bundleObj.bundleName);
}
}



