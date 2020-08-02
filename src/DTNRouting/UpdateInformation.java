// PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import Results.*;
import RoutingProtocols.RoutingProtocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.io.IOException;
import java.util.Random;




//******************************************************************************
// CLASS MADE FOR UPDATING THE INFORMATION DURING SIMULATION

public class UpdateInformation {
    //Instance Variables
	RoutingProtocol rp;
    RP_Performance rpp=new RP_Performance();
    Random rand;
    PrintWriter writer=null;
    private StringBuilder sb = new StringBuilder();
    private static DecimalFormat df2 = new DecimalFormat("#.##");

//******************************************************************************
//Constructor
public UpdateInformation(){
}


//******************************************************************************
//Update TTL and packet Latency
public void UpdateTTLandLatency()
{   
      df2.setRoundingMode(RoundingMode.DOWN);
	  dtnrouting.delay=dtnrouting.delay+1;
      for(int h=0;h < dtnrouting.arePacketsDelivered.size();h++)
      {
          Packet packetObj=dtnrouting.arePacketsDelivered.get(h);
          packetObj.packetTTL-=1;
          
          if(packetObj.isTTLExpired == false && packetObj.ispacketDelivered==false) {
                  packetObj.packetLatency=dtnrouting.delay;
          
          if(packetObj.packetTTL==0){
          //if packet's TTL expires, it cannot be delivered else if
            packetObj.isTTLExpired=true;
           // System.out.println("Expired: "+dtnrouting.delay);
           //System.out.println(packetObj.packetName + " is expired");
            dtnrouting.NumPacketsDeliverExpired += 1;}}
      }
      
     
     if(dtnrouting.NumPacketsDeliverExpired==dtnrouting.arePacketsDelivered.size())
     {
    	 
  
    	 dtnrouting.deliveryTA.setText("");
    	 dtnrouting.deliveryTA.setText("PACKET DEDLIVER || EXPIRED");
	      // STOP SIMULATION 

	      for(int h=0;h<dtnrouting.Destinations.size();h++)
	      {
	    	  double expired = 0;
	    	  for(int m=0; m < dtnrouting.Destinations.get(h).nodePackets.size(); m++)
	    	  {
	    		  dtnrouting.Destinations.get(h).msg_latency+=dtnrouting.Destinations.get(h).nodePackets.get(m).packetLatency;
	    		  dtnrouting.Destinations.get(h).msg_hops+=dtnrouting.Destinations.get(h).nodePackets.get(m).packetHops;  
	    		  dtnrouting.Destinations.get(h).msg_relibility+=dtnrouting.Destinations.get(h).nodePackets.get(m).packetReliability;
	    		  if(dtnrouting.Destinations.get(h).nodePackets.get(m).ispacketDelivered) 
	    		    dtnrouting.Destinations.get(h).msg_dl+=1;
	    		  if(dtnrouting.Destinations.get(h).nodePackets.get(m).isTTLExpired)
	    			  expired +=1;
	    	  }
	    	  
	    	  dtnrouting.Destinations.get(h).msg_latency = (double)dtnrouting.Destinations.get(h).msg_latency / dtnrouting.Destinations.get(h).nodePackets.size();
	    	  dtnrouting.Destinations.get(h).msg_hops = (int)dtnrouting.Destinations.get(h).msg_hops / dtnrouting.Destinations.get(h).nodePackets.size();
	    	  dtnrouting.Destinations.get(h).msg_dl =  (double)dtnrouting.Destinations.get(h).msg_dl / dtnrouting.Destinations.get(h).nodePackets.size();
	    	  dtnrouting.Destinations.get(h).msg_relibility = (double)dtnrouting.Destinations.get(h).msg_relibility / dtnrouting.Destinations.get(h).nodePackets.size();
	  	      
	    	  if(sb.length()==0)
	    	  sb.append("simulation_run, dest,  latency, hop , delivery, failure, reliability\n");
			  
	  	      sb.append(
	  	      (dtnrouting.TOTAL_SIMULATION_RUNS-dtnrouting.SIMULATION_N0 +1) + ", " +
			  (h+1) + ", " + df2.format(dtnrouting.Destinations.get(h).msg_latency)+ ", " +
			  dtnrouting.Destinations.get(h).msg_hops + ", " +
			  df2.format(dtnrouting.Destinations.get(h).msg_dl)+  ", " +
			  df2.format(expired/dtnrouting.Destinations.get(h).nodePackets.size())+  ", " +
			  df2.format(dtnrouting.Destinations.get(h).msg_relibility)+"\n");
	  	      dtnrouting.deliveryTA.append("\n"+dtnrouting.Destinations.get(h).name+", dl: "+dtnrouting.Destinations.get(h).msg_dl);
	  	   
	      }
	      
	      
	         
	       dtnrouting.THIS_SIMULATION_ENDED=true;
	    
	        // Add to file 
	    	//First simulation run
	    	if(dtnrouting.SIMULATION_N0==dtnrouting.TOTAL_SIMULATION_RUNS) {
	    	     try {
	    	     writer = new PrintWriter(new File("/Users/mahrukh/manet_results.csv"));
	    	      } catch (FileNotFoundException e) {
	    	            e.printStackTrace();}}

	    	//Last simulation run 
	    	if(dtnrouting.SIMULATION_N0==1) {
	    		writer.write(sb.toString());
	    		writer.flush();
	    		writer.close();}
   
     }
}

//******************************************************************************

public void nextPositionForMovement() throws IOException
{
	//NODE MOVEMENT
		if(dtnrouting.movementtype.equals("Random"))
	    for(int i=0; i< dtnrouting.allNodes.size();i++)
	    	     dtnrouting.allNodes.get(i).node_nm.RandomMovement(dtnrouting.allNodes.get(i));
	
	    else if(dtnrouting.movementtype.equals("Pseudorandom"))
	    	 for(int i=0; i< dtnrouting.allNodes.size();i++)
	    		 dtnrouting.allNodes.get(i).node_nm.Follow_PseudoRandomPath(dtnrouting.allNodes.get(i));
	   
	    else if(dtnrouting.movementtype.equals("Dataset"))
	    	 for(int i=0; i< dtnrouting.allNodes.size();i++)
	    		 dtnrouting.allNodes.get(i).node_nm.Follow_DatasetPath(dtnrouting.allNodes.get(i));
	
}

//******************************************************************************
//Clear all the settings when clear (eraser) button is clicked

public void clearSettings()
{
	 
        dtnrouting.allNodes.clear();
        Node.ID_INCREMENTER=0;
        dtnrouting.simulationTime=0;
        dtnrouting.NumPacketsDeliverExpired=0;
        
        //Clearings the array lists of source, destination, their packets and their parameter
        dtnrouting.Sources.clear();     
        dtnrouting.Destinations.clear();
        //Set movement model to null
        dtnrouting.movementtype="Random";
        dtnrouting.arePacketsDelivered.clear();
        dtnrouting.SIMULATION_N0 = dtnrouting.TOTAL_SIMULATION_RUNS;
        Packet.packetID=0; 
               
        //Empty Text areas
        dtnrouting.sdpTA.setText("Source    Dest.    packet");
        dtnrouting.contactsTA.setText("");
        dtnrouting.transferTA.setText("");
        dtnrouting.deliveryTA.setText("");
        rpp.clearData(); //clear data from table and charts
        dtnrouting.THIS_SIMULATION_ENDED=false;
        dtnrouting.SIMULATION_RUNNING=false;
        
}

//******************************************************************************
//When a simulation run completes
public void simulationSettings(dtnrouting dtn)
{
 
	dtnrouting.SIMULATION_N0=dtnrouting.SIMULATION_N0-1; 
    //dtnrouting.dataset_simulation_index = rand.nextInt(dtnrouting.allNodes.get(dtnrouting.first_regular_node_index).x_coord.size());
    dtnrouting.simulationTime=0;
  

    dtnrouting.NumPacketsDeliverExpired=0;
    dtnrouting.sdpTA.setText("Source    Dest.    packet");
    dtnrouting.contactsTA.setText("");
    dtnrouting.transferTA.setText("");
    
    
    //...Display the result when all SIMULATIONS END
    if(dtnrouting.SIMULATION_N0==0)  
    	dtnrouting.SIMULATION_RUNNING=false;

   
    //...When a simulation run ends, update the average results
    else if(dtnrouting.SIMULATION_N0>0)  
    {
    	
        	//Remove packets and destination information from nodes
        	for(int g=0;g<dtnrouting.allNodes.size();g++)
        		dtnrouting.allNodes.get(g).refreshNodeSettings();
           
           //Clear all packets and generate messages again
           dtnrouting.arePacketsDelivered.clear();
           Packet.packetID=0;
	       
	       //Take a break of one second
	       try
	         { Thread.sleep(1000);
	         } catch (InterruptedException ex) {}
	

	        if(dtnrouting.movementtype.equals("Pseudorandom"))
	            for(int i=0; i< dtnrouting.allNodes.size(); i++)
	            if(dtnrouting.allNodes.get(i).name.substring(0,1).equals("R"))
	           	dtnrouting.allNodes.get(i).node_nm.InitializePsuedoPath(dtnrouting.allNodes.get(i));
	        
	        //Generate packets again and assign sources/destinations
	        CreatePacket cp = new CreatePacket();
	        cp.CreateMessageAtSource();
	        dtnrouting.deliveryTA.setText("");
	        //System.out.println(dtnrouting.arePacketsDelivered.size());
	        runSimulation();
    }
}//end of the method

//******************************************************************************

public void runSimulation()
{  
    dtnrouting.delay=0;
    dtnrouting.THIS_SIMULATION_ENDED=false;
    dtnrouting.SIMULATION_RUNNING=true;
}
//******************************************************************************

} // END OF CLASS
