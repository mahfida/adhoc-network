// PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import AdapterPackage.MyActionAdapter;
import Results.*;
import RoutingProtocols.RoutingProtocol;
import java.util.Random;

//******************************************************************************
// CLASS MADE FOR UPDATING THE INFORMATION DURING SIMULATION

public class UpdateInformation {
    //Instance Variables
	RoutingProtocol rp;
    RP_Performance rpp=new RP_Performance();
   

//******************************************************************************
//Constructor
public void UpdateInformation() {}

//******************************************************************************
//Reset all the settings when refresh button is clicked
public void RefreshSettings()
{ 
	 dtnrouting.THIS_SIMULATION=dtnrouting.TOTAL_SIMULATION_RUNS;
     UpdateNodeArray();
     dtnrouting.ob=null; dtnrouting.isRun=false;

     //Movement Paths
     if(dtnrouting.movementtype.equals("Pseudorandom"))
     for(int i=0; i< dtnrouting.allNodes.size(); i++)
     if(dtnrouting.allNodes.get(i).name.substring(0,1).equals("R"))
    		dtnrouting.allNodes.get(i).node_nm.InitializePsuedoPath(dtnrouting.allNodes.get(i));
     
     //Setting parameters as zero
     dtnrouting.DR_avg=dtnrouting.DR=dtnrouting.latency=dtnrouting.delay=0;
     dtnrouting.latency_avg=dtnrouting.load=dtnrouting.load_avg=0;
     dtnrouting.bandwidth=dtnrouting.bandwidth_avg=0;
     //Empty text areas lying in bottom panel
     dtnrouting.CommentsTA.setText(" "); dtnrouting.currentSituatonTA.setText(" ");
 }

//******************************************************************************
//Clear all the settings when clear (eraser) button is clicked

public void ClearSettings()
{
       
        dtnrouting.allNodes.clear();
        Node.ID_INCREMENTER=0;
        
        //Clearings the arraylists of source, destination, their packets and their parameter
        dtnrouting.Sources.clear();     dtnrouting.Destinations.clear();
        dtnrouting.arePacketsDelivered.clear();
        dtnrouting.latency=dtnrouting.delay=dtnrouting.latency_avg=0;
        dtnrouting.load=dtnrouting.load_avg=dtnrouting.bandwidth=dtnrouting.bandwidth_avg=0;
        dtnrouting.DR_avg=dtnrouting.DR=0;

        //Set movement model to null
        dtnrouting.movementtype=" ";
        //dtnrouting.endNodes.setEnabled(false);
        //Empty Text areas
        dtnrouting.CommentsTA.setText("");
        dtnrouting.currentSituatonTA.setText("");
        dtnrouting.tDetail.setText("Source    Dest.    packet");
        rpp.clearData(); //clear data from table and charts
        dtnrouting.isRun=false;
}

//******************************************************************************
//When a simulation completes

public void simulationSettings(dtnrouting dtn)
{
    setPerimeters();
    if(dtnrouting.THIS_SIMULATION<=0)  //Display the result and end Simulation
    	FinalResult();
    
    else if(dtnrouting.THIS_SIMULATION>0)  //When a simulation run ends, update the average results
    {
       UpdateNodeArray();
       dtnrouting.CommentsTA.setText(" ");
       dtnrouting.currentSituatonTA.setText(" ");
       
       //Take a break of one second
       try
         { Thread.sleep(1000);
         } catch (InterruptedException ex) {}

        dtn.ExecuteProtocol();
        if(dtnrouting.movementtype.equals("Pseudorandom"))
            for(int i=0; i< dtnrouting.allNodes.size(); i++)
            if(dtnrouting.allNodes.get(i).name.substring(0,1).equals("R"))
           	dtnrouting.allNodes.get(i).node_nm.InitializePsuedoPath(dtnrouting.allNodes.get(i));
         
        runSimulation();
    }
}//end of the method
//******************************************************************************
// Reset Settings
public void UpdateNodeArray()
{

    //Remove packets and destination information from nodes
    for(int g=0;g<dtnrouting.allNodes.size();g++)
    {
        Node n=dtnrouting.allNodes.get(g);
        n.packetIDHash.clear();     n.queueSizeLeft=n.wholeQueueSize;
        n.DestNPacket.clear();      n.node_nm.InitialNodePositions(n);

    }

    //Clear all packets
    dtnrouting.arePacketsDelivered.clear();
    //Generate packets again and assign sources/destinations
    CreatePacket cp = new CreatePacket();
    cp.CreateMessageAtSource();
}//End of method

//******************************************************************************
//After a simulation runs end calculate end result and clear tracking variables

public void setPerimeters()
{
        dtnrouting.ob=null; //ob poins to not any routing protocols
        dtnrouting.isRun=false;        
        
        dtnrouting.THIS_SIMULATION=dtnrouting.THIS_SIMULATION-1; //Decrement Sim
        
        //Updating packet perimeters
        for(int h=0;h<dtnrouting.arePacketsDelivered.size();h++)
        dtnrouting.arePacketsDelivered.get(h).refreshPacketSettings();

        //Value of Perimeters after a simulation run completes
        dtnrouting.latency_avg+=dtnrouting.latency;
        dtnrouting.load_avg+=dtnrouting.load;
        dtnrouting.bandwidth_avg+=dtnrouting.bandwidth;
        dtnrouting.DR_avg+=dtnrouting.DR;
        dtnrouting.load=dtnrouting.bandwidth=dtnrouting.latency=dtnrouting.DR=0;
        dtnrouting.SIMULATION_ENDED=false;
}

//******************************************************************************
//Provide final result to the result table and the graph

public void FinalResult()
 {
    dtnrouting.SIMULATION_ENDED=false;
    dtnrouting.CommentsTA.append("\n\nAverage Result:\n");
    dtnrouting.CommentsTA.append("Latency: "+dtnrouting.latency_avg/dtnrouting.TOTAL_SIMULATION_RUNS+",Load: "+dtnrouting.load_avg/dtnrouting.TOTAL_SIMULATION_RUNS+"\nLinks: "+dtnrouting.bandwidth_avg/dtnrouting.TOTAL_SIMULATION_RUNS+",DR: "+dtnrouting.DR_avg/dtnrouting.TOTAL_SIMULATION_RUNS);
    RoutingProtocol.standardDeviation(dtnrouting.latency_avg/dtnrouting.TOTAL_SIMULATION_RUNS,dtnrouting.load_avg/dtnrouting.TOTAL_SIMULATION_RUNS,dtnrouting.bandwidth_avg/dtnrouting.TOTAL_SIMULATION_RUNS,dtnrouting.DR_avg/dtnrouting.TOTAL_SIMULATION_RUNS);
    rpp.setAvgData(dtnrouting.protocolName,dtnrouting.latency_avg/dtnrouting.TOTAL_SIMULATION_RUNS,dtnrouting.load_avg/dtnrouting.TOTAL_SIMULATION_RUNS,dtnrouting.bandwidth_avg/dtnrouting.TOTAL_SIMULATION_RUNS,dtnrouting.DR_avg/dtnrouting.TOTAL_SIMULATION_RUNS);
 
 }

//******************************************************************************



//******************************************************************************

public void runSimulation()
{
    if(dtnrouting.ob!=null && !dtnrouting.movementtype.equals(" "))
    {
    
        for(int i=0;i<dtnrouting.allNodes.size();i++)
        for(int j=0;j<dtnrouting.allNodes.size();j++)
        {
        	dtnrouting.n1_neighborhood[i][j] = 0;
            dtnrouting.linkCapacities[i][j]=0.0;
        }

       if(MyActionAdapter.protocol.equals("ContactBased"))
            {
                dtnrouting.ob.setPerimeters();
            }
     //Number of packet copies generated when Spray and WaitB is selected
     if(dtnrouting.protocolName.equals("Spray&WaitB"))
     {
       double r;  //declare double variable r
       for(int k=0;k<dtnrouting.Sources.size();k++)
       {
           r=Math.random();    //random function set the value of r between 0 and 1
           r=(int)(Math.ceil(3*r));  //multiply r with 3 so that the power of 2 go to 3 and then ceil it
           dtnrouting.arePacketsDelivered.get(k).packetLoad=(int)Math.pow(2,r); //take r as power of 2 and assign it to NoDuplicate
           dtnrouting.Sources.get(k).packetCopies.put(dtnrouting.arePacketsDelivered.get(k).name,dtnrouting.arePacketsDelivered.get(k).packetLoad );
           dtnrouting.CommentsTA.append("\nCopies Generated by "+dtnrouting.Sources.get(k).ID+" : "+dtnrouting.arePacketsDelivered.get(k).packetLoad);
       }
     }

    //Number of packet copies generated when Spray and WaitB is selected
    if(dtnrouting.protocolName.equals("Spray&WaitN"))
    {
      Random rand=new Random();

       for(int k=0;k<dtnrouting.Sources.size();k++)
       {
           dtnrouting.arePacketsDelivered.get(k).packetLoad=rand.nextInt(10)+1; //random function set the value of r between 1 and 10
           dtnrouting.Sources.get(k).packetCopies.put(dtnrouting.arePacketsDelivered.get(k).name,dtnrouting.arePacketsDelivered.get(k).packetLoad );
           dtnrouting.CommentsTA.append("\nCopies Generated by "+dtnrouting.Sources.get(k).ID+" : "+dtnrouting.arePacketsDelivered.get(k).packetLoad);
       }
    }
    dtnrouting.delay=0;
    dtnrouting.isRun=true;
    
    }
}
//******************************************************************************

} // END OF CLASS
