// PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import AdapterPackage.MyActionAdapter;
import MovementPattern.NodeMovement;
import Results.*;
import RoutingProtocols.RoutingProtocol;
import java.util.Random;

//******************************************************************************
// CLASS MADE FOR UPDATING THE INFORMATION DURING SIMULATION

public class UpdateInformation {
    //Instance Variables
    RoutingProtocol rp;
    RP_Performance rpp=new RP_Performance();
    DeviceBasedSettings dbs=new DeviceBasedSettings();
    public static int Sim=1,runs=1;

//******************************************************************************
//Constructor
public void UpdateInformation() {}

//******************************************************************************
//Reset all the settings when refresh button is clicked
public void RefreshSettings()
{
     Sim=runs; UpdateNodeArray();
     dtnrouting.ob=null; dtnrouting.isRun=false;

     //Movement Paths
     if(dtnrouting.movementtype.equals("Dataset")){
     DSNodes.sday.generatePath(Sim-1);
     dtnrouting.nodemovement.InitialDSPath();}
     else if(dtnrouting.movementtype.equals("Pseudorandom"))
        dtnrouting.nodemovement.InitializePsuedoPath();
     
     //Setting parameters as zero
     dtnrouting.DR_avg=dtnrouting.DR=dtnrouting.latency=dtnrouting.delay=0;
     dtnrouting.latency_avg=dtnrouting.load=dtnrouting.load_avg=0;
     dtnrouting.bandwidth=dtnrouting.bandwidth_avg=0;
     //Empty text areas lying in bottom panel
     dtnrouting.CommentsTA.setText(" "); dtnrouting.currentSituatonTA.setText(" ");
     System.out.println(dtnrouting.DR_avg);
 }

//******************************************************************************
//Clear all the settings when clear (eraser) button is clicked

public void ClearSettings()
{
        //Clear variables of Setting class
        Setting.ClearPerimeters();

        //Clear Device Based Settings
        dbs.setPerimeters();

        //Remove nodes from the node lists in CreateNode class
        CreateNode.trainList.clear(); CreateNode.padList.clear(); CreateNode.carList.clear();
        //Remove all nodes from the nodeArray
        dtnrouting.nodeArray.clear();   dtnrouting.FixedRelayArray.clear();
        dtnrouting.ferryArray.clear();  dtnrouting.allNodes.clear();
        dtnrouting.RegionArray.clear(); Setting.numRegions=1;

        // Resetting static data members
        Node.trainID=Node.padID=Node.carID=Node.ID=0;
        Node.fid=Node.rid=Node.tid=Regions.RegionID=0;

        //Clearings the arraylists of source, destination, their bundles and their parameter
        dtnrouting.Sources.clear();     dtnrouting.Destinations.clear();
        dtnrouting.areBundlesDelivered.clear();
        dtnrouting.latency=dtnrouting.delay=dtnrouting.latency_avg=0;
        dtnrouting.load=dtnrouting.load_avg=dtnrouting.bandwidth=dtnrouting.bandwidth_avg=0;
        dtnrouting.DR_avg=dtnrouting.DR=0;

        //Set movement model to null
        dtnrouting.movementtype=" ";

        //Disable end nodes and create bundle menu items
        dtnrouting.createBundle.setEnabled(false);
        dtnrouting.endNodes.setEnabled(false);
        //Empty Text areas
        dtnrouting.CommentsTA.setText("");
        dtnrouting.currentSituatonTA.setText("");
        dtnrouting.tDetail.setText("Source    Dest.    Bundle");
        rpp.clearData(); //clear data from table and charts
        dtnrouting.settings.setEnabled(true);           dtnrouting.isRun=false;
}

//******************************************************************************
//When a simulation completes

public void simulationSettings(dtnrouting dtn)
{
    setPerimeters();
    if(Sim<=0)  //Display the result and end Simulation
    {
        if(dtnrouting.movementtype.equals("Dataset")||dtnrouting.movementtype.equals("Pseudorandom"))
        {
          NodeMovement.resetPerimeters();
        }
      //  dtnrouting.movementtype=" ";
        FinalResult();
    }
    else if(Sim>0)  //When a simulation run ends, update the average results
    {
       UpdateNodeArray();
       dtnrouting.CommentsTA.setText(" ");//creat Textarea on p2
       dtnrouting.currentSituatonTA.setText(" ");//creat Textarea  on p2
       //Take a break of one second
      try
         { Thread.sleep(1000);
         } catch (InterruptedException ex) {}

       if(dtnrouting.protocolName.equals("NECTAR"))
           dtnrouting.ob=new RoutingProtocols.NECTAR("");
       else
           dtn.ExecuteProtocol();
        if(MyActionAdapter.mobilityPattern.equals("Pseudorandom"))
        dtnrouting.nodemovement.InitializePsuedoPath();
      if(dtnrouting.movementtype.equals("Dataset"))
        {
          DSNodes.sday.generatePath(Sim-1);
          dtnrouting.nodemovement.InitialDSPath();
        }
        runSimulation();
    }
}//end of the method

//******************************************************************************
// Reset Settings

public void UpdateNodeArray()
{

    //Remove bundles and destination information from nodes
    for(int g=0;g<dtnrouting.allNodes.size();g++)
    {
        Node n=dtnrouting.allNodes.get(g);
        n.bundleIDHash.clear();     n.queueSizeLeft=n.wholeQueueSize;
        n.DestNBundle.clear();      n.getStartPositions();
        n.isNewPosition=false;
    }

    //Resart the routing process by assigning the bundles to only its source
    //for next simulation run
    for(int g=0;g<dtnrouting.Sources.size();g++)
    {  
        //Refresh bundle settings
       dtnrouting.areBundlesDelivered.get(g).refreshBundleSettings( dtnrouting.areBundlesDelivered.get(g));

       //Assign bundles to source Nodes again
        Node node=new Node();
        node=dtnrouting.Sources.get(g);
        node.DestNBundle.put(dtnrouting.areBundlesDelivered.get(g), dtnrouting.Destinations.get(g));
        node.queueSizeLeft-=dtnrouting.areBundlesDelivered.get(g).bundleSize;
        node.bundleIDHash.add(dtnrouting.areBundlesDelivered.get(g).bundleName);
        node.bundleCopies.put(dtnrouting.areBundlesDelivered.get(g).bundleName, dtnrouting.areBundlesDelivered.get(g).bundleLoad);
    }
}//End of method

//******************************************************************************
//After a simulation runs end calculate end result and clear tracking variables

public void setPerimeters()
{
        dtnrouting.ob=null; //ob poitns to not any routing protocols
        dtnrouting.isRun=false;        Sim=Sim-1; //Decrement Sim
        
        //Updating bundle perimeters
        for(int h=0;h<dtnrouting.areBundlesDelivered.size();h++)
           dtnrouting.areBundlesDelivered.get(h).refreshBundleSettings(dtnrouting.areBundlesDelivered.get(h));

        //Value of Perimeters after a simulation run completes
        dtnrouting.latency_avg+=dtnrouting.latency;
        dtnrouting.load_avg+=dtnrouting.load;
        dtnrouting.bandwidth_avg+=dtnrouting.bandwidth;
        dtnrouting.DR_avg+=dtnrouting.DR;
        dtnrouting.load=dtnrouting.bandwidth=dtnrouting.latency=dtnrouting.DR=0;
        dtnrouting.simEnded=false;
}

//******************************************************************************
//Provide final reasult to the result table and the graph

public void FinalResult()
 {
    dtnrouting.simEnded=false;
    dtnrouting.CommentsTA.append("\n\nAverage Result:\n");
    dtnrouting.CommentsTA.append("Latency: "+dtnrouting.latency_avg/runs+",Load: "+dtnrouting.load_avg/runs+"\nLinks: "+dtnrouting.bandwidth_avg/runs+",DR: "+dtnrouting.DR_avg/runs);
    RoutingProtocol.standardDeviation(dtnrouting.latency_avg/runs,dtnrouting.load_avg/runs,dtnrouting.bandwidth_avg/runs,dtnrouting.DR_avg/runs);
    rpp.setAvgData(dtnrouting.protocolName,dtnrouting.latency_avg/runs,dtnrouting.load_avg/runs,dtnrouting.bandwidth_avg/runs,dtnrouting.DR_avg/runs);
 
 }

//******************************************************************************



//******************************************************************************

public void runSimulation()
{
    if(dtnrouting.ob!=null && !dtnrouting.movementtype.equals(" "))
    {
        System.out.println("Sim:"+Sim);
     PlayField.isContactPresent=new boolean[dtnrouting.allNodes.size()][dtnrouting.allNodes.size()];
     dtnrouting.contactDuration=new int[dtnrouting.allNodes.size()][dtnrouting.allNodes.size()];
     for(int i=0;i<dtnrouting.allNodes.size();i++)
        for(int j=0;j<dtnrouting.allNodes.size();j++)
        {
            PlayField.isContactPresent[i][j] = false;
            dtnrouting.contactDuration[i][j]=0;
        }

       if(MyActionAdapter.protocol.equals("ContactBased"))
            {
                dtnrouting.ob.setPerimeters();
            }

       if(dtnrouting.protocolName.equals("CHRP"))
            {
                if(Setting.dataset.equals("St.Andrew Uni"))
                dtnrouting.ob.setPerimeters("..\\Datasets\\SA",25);
                if(Setting.dataset.equals("iMote Traces"))
                dtnrouting.ob.setPerimeters("..\\Datasets\\MT",36);
            }

     //Number of bundle copies generated when Spray and WaitB is selected
     if(dtnrouting.protocolName.equals("Spray&WaitB"))
     {
       double r;  //declare double variable r
       for(int k=0;k<dtnrouting.Sources.size();k++)
       {
           r=Math.random();    //random function set the value of r between 0 and 1
           r=(int)(Math.ceil(3*r));  //multiply r with 3 so that the power of 2 go to 3 and then ceil it
           dtnrouting.areBundlesDelivered.get(k).bundleLoad=(int)Math.pow(2,r); //take r as power of 2 and assign it to NoDuplicate
           dtnrouting.Sources.get(k).bundleCopies.put(dtnrouting.areBundlesDelivered.get(k).bundleName,dtnrouting.areBundlesDelivered.get(k).bundleLoad );
           dtnrouting.CommentsTA.append("\nCopies Generated by "+dtnrouting.Sources.get(k).name+" : "+dtnrouting.areBundlesDelivered.get(k).bundleLoad);
       }
     }

    //Number of bundle copies generated when Spray and WaitB is selected
    if(dtnrouting.protocolName.equals("Spray&WaitN"))
    {
      Random rand=new Random();

       for(int k=0;k<dtnrouting.Sources.size();k++)
       {
           dtnrouting.areBundlesDelivered.get(k).bundleLoad=rand.nextInt(10)+1; //random function set the value of r between 1 and 10
           dtnrouting.Sources.get(k).bundleCopies.put(dtnrouting.areBundlesDelivered.get(k).bundleName,dtnrouting.areBundlesDelivered.get(k).bundleLoad );
           dtnrouting.CommentsTA.append("\nCopies Generated by "+dtnrouting.Sources.get(k).name+" : "+dtnrouting.areBundlesDelivered.get(k).bundleLoad);
       }
    }
    dtnrouting.delay=0;
    dtnrouting.isRun=true;
    
    }
}
//******************************************************************************

} // END OF CLASS
