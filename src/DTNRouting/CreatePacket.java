//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
//import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
//import javax.swing.JFrame;
//import javax.swing.WindowConstants;

//------------------------------------------------------------------------------
//START OF CLASS packet
public class CreatePacket implements ActionListener
{
  /*public JFrame jf=new JFrame("Create packet");
  public Label lpacketNumber=new Label("No. of packets");
  public TextField tpacketNumber=new TextField("3");
  public Label lpacketTTL=new Label("TTL Value");
  public TextField tpacketTTL=new TextField("1000");
  public Label lpacketSize=new Label("Size (MB)");
  public TextField tpacketSize=new TextField("1");
  public Button ok=new Button("Add");
  public Button close=new Button("Close");*/
  public int num_packets,ttl_packets,size_packets;
  public PlayField pf = new PlayField();
  public shortestPath  sp;
  Random rand=new Random();

//******************************************************************************

public CreatePacket(){}

//******************************************************************************
//packet FRAME GUI
/*public void GenerateFrame()
{
	  jf.setLayout(new GridLayout(7,2,5,5));
	  jf.add(lpacketNumber);  jf.add(tpacketNumber);
	  jf.add(lpacketTTL);     jf.add(tpacketTTL);
	  jf.add(lpacketSize);    jf.add(tpacketSize);
	  jf.add(ok);             jf.add(close);
	  ok.addActionListener(this);
	  close.addActionListener(this);
	  jf.setSize(new Dimension(200,200));
	  jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	  jf.setResizable(false);
	  jf.setVisible(true);
}
*/
//******************************************************************************
/*
public void actionPerformed(ActionEvent e)
{
 String value=e.getActionCommand();
 String action;
 action=e.getActionCommand();

 if(action.equals("Close"))				jf.dispose();
 else if(value.equals("Add")) {			
	 num_packets = Integer.parseInt(tpacketNumber.getText());
	 ttl_packets = Integer.parseInt(tpacketTTL.getText());
	 size_packets = Integer.parseInt(tpacketSize.getText());
	 CreateMessageAtSource();
 }
		
 
} */   

//******************************************************************************
public void CreateMessageAtSource() {
	
	
		// Calculate Adjacency matrix
	    dtnrouting.source_index = new int[dtnrouting.Sources.size()];
	    dtnrouting.dest_index = new int[dtnrouting.Destinations.size()];
	    int s_counter=0 , d_counter=0;
		
	    for (int i = 0; i < (dtnrouting.allNodes.size()-1); i++) {
			dtnrouting.adjacencyMatrix[i][i] = 0;
			if(dtnrouting.allNodes.get(i).name.substring(0,1).equals("S"))
				dtnrouting.source_index[s_counter++] = i;
			else if(dtnrouting.allNodes.get(i).name.substring(0,1).equals("D"))
				dtnrouting.dest_index[d_counter++] = i;
			}
			
	    pf.FindNeighborhoods();
			
		
	    // For last node
	    dtnrouting.adjacencyMatrix[dtnrouting.allNodes.size()-1][dtnrouting.allNodes.size()-1]=0;
		if(dtnrouting.allNodes.get(dtnrouting.allNodes.size()-1).name.substring(0,1).equals("S"))
			dtnrouting.source_index[s_counter++] = dtnrouting.allNodes.size()-1;
		else if(dtnrouting.allNodes.get(dtnrouting.allNodes.size()-1).name.substring(0,1).equals("D"))
			dtnrouting.dest_index[d_counter++] = dtnrouting.allNodes.size()-1;
		
		// Find shortest path from each source too each destination
	     for(int s=0; s < dtnrouting.source_index.length; s++) {
	    	 sp = new shortestPath();
	    	 dtnrouting.allNodes.get(dtnrouting.source_index[s]).ptD = new pathToDestination(dtnrouting.dest_index.length);
	    	 sp.runDijkstra(dtnrouting.adjacencyMatrix, dtnrouting.dest_index , 
	    			 dtnrouting.source_index[s], dtnrouting.allNodes.get(dtnrouting.source_index[s]));
	    	 //System.out.println(dtnrouting.allNodes.get(dtnrouting.source_index[s]).ptD.paths);
	    	 sp=null;
	     }
	
	    // CHOOSE BEST SOURCE FOR EACH DESTINATION 
	    int source_id =-1;
        for(int d=0; d < dtnrouting.Destinations.size(); d ++) {
        	double distance = 1000.0; // max distance
        
        	/// *****************************
        	// parameters, node capacity, reliability, hop count (calculate from source.ptd.path)
        	//Choose source node--------------------
        	for(int s=0; s < dtnrouting.source_index.length; s++) {
        		//System.out.println(dtnrouting.allNodes.get(dtnrouting.source_index[s]).ptD.dest_distance[d]);
        		if(dtnrouting.allNodes.get(dtnrouting.source_index[s]).ptD.dest_distance[d] < distance) {
        			source_id = dtnrouting.source_index[s];
        			distance = dtnrouting.allNodes.get(dtnrouting.source_index[s]).ptD.dest_distance[d];}
        		}
        	// If no path choose source randomly
        	if(distance==1000.0)
        			source_id = rand.nextInt(dtnrouting.Sources.size());
        	
        	///********************************
        	System.out.print("\nS->D:(" + (dtnrouting.source_index[source_id]) + "->"+ (dtnrouting.dest_index[d])+") ");
        	System.out.println(dtnrouting.allNodes.get(dtnrouting.source_index[source_id]).ptD.paths.get(d));
        	
        	//Destination chooses number of packets randomly
        	Node destNode = dtnrouting.allNodes.get(dtnrouting.dest_index[d]);
        	
        	
        	destNode.num_packets =rand.nextInt(20)+1;
        	destNode.packets_ttl =rand.nextInt(100)+50;
        	
        	//Below code generates packets for each destination
        	//stores the path a packet will take from the shortest path from its source to destination
        	// Stores them in its selected source buffer
        	for(int j=0; j< destNode.num_packets; j++) {//number of packets that each source will transmit..
           	    Packet p =new Packet();
           	    p.maxTTL=destNode.packets_ttl;
           	    p.refreshPacketSettings();
           	    destNode.nodePackets.add(p);
           	    dtnrouting.arePacketsDelivered.add(p);

           	    // Source node of the destination
           	    Node sourceNode = dtnrouting.allNodes.get(source_id);
           	    //System.out.print("Packet "+ p.packetName+": path ");
           	    // Packet will follow the shortest path from source too destination
           	    for(int c=0; c < sourceNode.ptD.paths.get(d).size(); c ++) {
           	    	if(sourceNode.ptD.paths.get(d).get(c)==(-1)) {
           	    		break; // store no path for packet
           	    	}
           	    	else {
           	    		p.pathHops.add(dtnrouting.allNodes.get(sourceNode.ptD.paths.get(d).get(c)));
           	    		//System.out.print(dtnrouting.allNodes.get(sourceNode.ptD.paths.get(d).get(c)).name+",");
           	    	}}
           	 
           	  // Store packet to inside source buffer
              if(dtnrouting.Sources.get(source_id).queueSizeLeft > p.packetSize)
               {    
            	    dtnrouting.Sources.get(source_id).queueSizeLeft-=p.packetSize; //update queue space after putting packet in it
                    dtnrouting.Sources.get(source_id).packetIDHash.add(p.packetName); //Store ID of packet in the source as Hash value
                    //dtnrouting.Sources.get(source_id).packetTimeSlots.put(p.packetName,0);
                    dtnrouting.Sources.get(source_id).packetCopies.put(p.packetName,1);
                    // these packets have no path
                    if(p.pathHops.size()==0) {
                    	dtnrouting.Sources.get(source_id).DestNPacket.put(p,null);
                    	dtnrouting.Sources.get(source_id).number_packet_arrived+=1;}
                    else
                    dtnrouting.Sources.get(source_id).DestNPacket.put(p,destNode);
                    //System.out.println("SOURCE: "+dtnrouting.Sources.get(source_id).name+", DEST: "+destNode.name);
                    dtnrouting.sdpTA.append("\n "+dtnrouting.Sources.get(source_id).ID+"--"+destNode.ID+" ("+p.packetName+")");
                 }
              
              else    //If queue of the packet has not enough space to store the new packet then
              dtnrouting.sdpTA.append("\nSource "+ dtnrouting.Sources.get(source_id).ID+" has not enough space to occupy "+p.packetName);  
           }  //all packets of the destination assigned to the source of dest.     
	    }  
        
}

@Override
public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
	
}

//******************************************************************************
}//END OF packet CLASS
