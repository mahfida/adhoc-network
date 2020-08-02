package DTNRouting;

public class DeliveryThread extends Thread {
	   PlayField playField=new PlayField();
	   UpdateInformation updateInformation=new UpdateInformation();
    public void run() {
        System.out.println("Hello from a thread!");
        if(dtnrouting.SIMULATION_RUNNING) 
        {
     	   //System.out.println("dtn: "+delay);
     	   //for drawing nodes and the packets that they hold
            //playField.drawNodesPackets(g);
            // Following code will be called once, only in beginning,
            
            // if nodes are static
            //if(allNodes.get(allNodes.size()-1).speed>0)
         	//   playField.FindNeighborhoods();
            
            // Update the TTL field of all packets along with latency of the packet
     	  
        	updateInformation.UpdateTTLandLatency();
            

            //Call transfer function to deliver messages in each time unit
            if(!dtnrouting.THIS_SIMULATION_ENDED)
            playField.TransferPackets();
            
            //Stores the time units elapsed in the simulation environment
            dtnrouting.simulationTime+=1;
        }
    }

}