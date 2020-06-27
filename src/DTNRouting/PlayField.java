//PACKAGE NAME
package DTNRouting;

import java.util.Iterator;
//IMPORT PACKAGES
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.IOException;

//******************************************************************************
//START OF THE CLASS PLAYFIED, DISPLAYING MOVING NODES AND REGIONS/MAP

public class PlayField
{
	//Instance Variables
	static boolean hasDeliverCalled[][]=new boolean[dtnrouting.allNodes.size()][dtnrouting.allNodes.size()];
	static dtnrouting dtn=new dtnrouting();	

	//******************************************************************************
	//EMPTY CONSTRUCTOR

	public PlayField() {}

	//******************************************************************************
	//DRAW NODES ALONG WITH THEIR packetS IN THE PLAYFIELD OF APPLET

	public void drawNodesPackets(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g.setFont(new Font("Dialog",Font.PLAIN,12));

		//Displaying Nodes and the packet that they hold
		for (int k=0;k<dtnrouting.allNodes.size();k++)
		{
			//Access one node at a time from its array list
			Node n=new Node();
			n=dtnrouting.allNodes.get(k);
			int r=n.getRadioRange();  //set the size of nodes
			g2.setStroke(new BasicStroke(3));
			g.setColor(Color.black);

			//Drawing nodes of different names with different colors
			if(n.name.substring(0,1).equals("R"))         g2.setPaint(Color.YELLOW);
			else if(n.name.substring(0, 1).equals("D"))   g2.setPaint(Color.BLUE);
			else if(n.name.substring(0, 1).equals("S"))   g2.setPaint(Color.RED);


			Ellipse2D e = new Ellipse2D.Double(n.nodeX, n.nodeY, r, r);
			e.setFrame(n.nodeX, n.nodeY, r, r);
			g2.draw(e);

			//Put name of node inside node circle
			g.setColor(Color.black);
			g.drawString(n.ID+"", n.nodeX+(r)/2-5, n.nodeY+(r)/2+2);

			//Show whether packet is present: for one packet only
			if(!n.DestNPacket.isEmpty())
			{
				 //System.out.println("PLAY" + dtnrouting.delay);
				//int b=n.DestNPacket.size();
				Set<Packet> setPacket=n.DestNPacket.keySet();
				Iterator<Packet> it=setPacket.iterator();
				int x=n.nodeX+r/2-6;
				int y=n.nodeY+r/2-1;
				while(it.hasNext())
				{
					Packet packetObj=(Packet)it.next();
					if(packetObj.isTTLExpired==true)
						g.setColor(Color.RED);
					else if(packetObj.ispacketDelivered==true)
						g.setColor(Color.GREEN);
					else
						g.setColor(Color.BLUE);
					//g.drawString(packetObj.packetName.substring(1)+",", x-40, y+15);
					g.fillOval(x-40, y+15, 10, 10);

					
					//If node has more than one packet then next packet is displayed
					//near the earlier one in the same node
					x=x+11;
				} //End of while loop
			}

			g.setColor(Color.gray);
			//divide it by two so that the text comes in the mid of the node
		} //End of if statement
	}

	//******************************************************************************
	//FIND WHETHER A CONTACT IS PRESENT BETWEEN ANY PAIR OF NODES

	double FindIntersection(Node ni,Node nj) //to find the intersection between nodes
	{

		//******************************************************
		//mid point and radius of ni
		double x1 = (ni.nodeX + ni.nodeX + (ni.getRadioRange()))/2;
		double y1 = (ni.nodeY + ni.nodeY + (ni.getRadioRange()))/2;
		double r1 = (ni.getRadioRange())/2;

		//mid point and radius of nj
		double x2 = (nj.nodeX + nj.nodeX + (nj.getRadioRange()))/2;
		double y2 = (nj.nodeY + nj.nodeY + (nj.getRadioRange()))/2;
		double r2 = (nj.getRadioRange())/2;

		double distance_km = Math.sqrt(Math.pow((y2-y1),2) + Math.pow((x2-x1),2));
		double r = r1 + r2;

		if(distance_km <= r) {
			double dist_min = 1.5, dist_max = 6.4345, range_min = 0 , range_max = r;
			distance_km = ((distance_km - range_min) / (range_max - range_min)) * (dist_max - dist_min) + dist_min;
			return getLinkCapacity(distance_km);}
	
		else                  return 0.0;

		//*********************************************************
	}

	double getLinkCapacity(double distance_km) {

		/* Random rand = new Random();
		double mean_dB = 0.0, sd_dB = 1.0, RandomFading_dB = rand.nextGaussian()*sd_dB + mean_dB;*/
		double Beta = 4.0, thisRate = 0.0, freq_Mhz = 2400;
		double[] rates = {0.0, 7.2, 14.4, 21.7, 28.9, 43.3, 57.8, 65.0, 72.2};
		double[] snrThreshold = {0.0, 2.0, 5.0, 9.0, 11.0, 15.0, 18.0, 20.0, 25.0}; 

		/*if (RandomFading_dB < 0.0) 
			RandomFading_dB = 0.0; // Do not make fading improve signal */

		double PathLoss_db = - 32.45 - Beta * 10 * Math.log10(freq_Mhz * distance_km) /*- RandomFading_dB*/;  
		double Radio_Pwr_dBm = 20.0;
		double rcdPower_dBm  = Radio_Pwr_dBm + PathLoss_db;
		double noise_cuttoff = -180.0;
		double sinr_dB = rcdPower_dBm - noise_cuttoff;	 

		for (int i = 0; i < snrThreshold.length; i++) {
			if (sinr_dB > snrThreshold[i]) 	thisRate = rates[i]; 
			else 							break;
		}	  

		return (thisRate);
	}

	//******************************************************************************
	//IF CONTACT PRESENT TRANSFER packet, IF NEEDED
	
	public void FindNeighborhoods()
	{
	
		// Empty previous linked lists
		for (int i = 0; i < dtnrouting.allNodes.size(); i++) {
			dtnrouting.allNodes.get(i).link_capacity.clear();
			dtnrouting.allNodes.get(i).n1_neighborhood.clear();
			dtnrouting.allNodes.get(i).n2_neighborhood.clear();
			dtnrouting.contactsTA.setText("");
		}
		
		// Generate n1 and n2_neiborhood
		for (int i = 0; i < (dtnrouting.allNodes.size()-1); i++) 
			for(int j = i+1; j < dtnrouting.allNodes.size(); j++) 
				{
					Node ni = dtnrouting.allNodes.get(i);  //node i
					Node nj = dtnrouting.allNodes.get(j);  // node j				
					//If contact is present between nodes in current time stamp
					double capacity = FindIntersection(ni, nj);
					
					// If two nodes are neighbor, then update the neighborhood information
					if(capacity > 0.0)
					{
						dtnrouting.contactsTA.insert(ni.ID+"<-->"+nj.ID+"\n", 0);
						// when new nodes comes into contact then deliver the message
						ni.link_capacity.add(capacity);
						nj.link_capacity.add(capacity);
						
						ni.n1_neighborhood.add(j);
						nj.n1_neighborhood.add(i);
						
						ni.n2_neighborhood.add(j);
						nj.n2_neighborhood.add(i);
					}}
		
		/*Generate n2_neighborhood from n1_neiborhood and
		 allocate time slots (link capacities) according
		 packets with neighboring n2 nodes
		*/
		// for each node in the network
		for (int i = 0; i < dtnrouting.allNodes.size(); i++) {
			Node ni = dtnrouting.allNodes.get(i);
			
			// Find n2 neighborhoods only if this node has data to transmit
			if(ni.DestNPacket.size() > 0 && ni.n1_neighborhood.size()>0) {
			// for each n1 neighbor
			for(int j = 0; j < ni.n1_neighborhood.size(); j++) 
				{
					Node nj = dtnrouting.allNodes.get(ni.n1_neighborhood.get(j));
					// find n2 neighbors
					for(int k = 0; k < nj.n1_neighborhood.size(); k++) 
					{
						Node nk = dtnrouting.allNodes.get(nj.n1_neighborhood.get(k));
						// If nk is not in n2 linked list yet
						if(ni.ID != nk.ID & !ni.n2_neighborhood.contains(nj.n1_neighborhood.get(k)) )
								ni.n2_neighborhood.add(nj.n1_neighborhood.get(k));	
					}}
				//After n2 neighbors is decided, do dynamic slot allocation and transfer data
				TransferPackets(ni); 	}}				
	

	}// Find neighbor hood ended

	//******************************************************************************

	public void TransferPackets(Node ni)
	{
		//Coloring or slot allocation by looking at ni's n2 neighbors
		ni.time_slot =  (double)(1.0/(ni.n2_neighborhood.size()+1.0)); // time_slot is all time
		//System.out.println(ni.n2_neighborhood.size());
		//System.out.println(ni.time_slot);
		
		// See which n2 nodes have no data to transmit
		// and see how much data, transmitting node have
		int silent_nodes = 0;
		int all_packets=0;
		for(int k = 0; k < ni.n2_neighborhood.size(); k++) {
			int data_size = dtnrouting.allNodes.get(ni.n2_neighborhood.get(k)).DestNPacket.size();
			if(data_size ==0) silent_nodes +=1;
			else all_packets+= data_size;		
		}
		 
		ni.time_slot  = ni.time_slot + ni.time_slot *( ni.DestNPacket.size()* silent_nodes)/all_packets;
		
		// Update the time_slot of ni, according to above information
		for(int k =0; k <  ni.n1_neighborhood.size(); k++) {
			// Available capacity for link with k n1_neighbor
			ni.capacity= (double)(ni.link_capacity.get(k)*ni.time_slot);
			//System.out.println("DELIVER: "+ni.ID+" and "+dtnrouting.allNodes.get(ni.n1_neighborhood.get(k)).ID);
			//System.out.println(ni.link_capacity.get(k)+", "+ni.time_slot);
			//System.out.println(ni.ID+","+ dtnrouting.allNodes.get(ni.n1_neighborhood.get(k)).ID);
			if(!dtnrouting.allNodes.get(ni.n1_neighborhood.get(k)).name.contains("S"))
			dtnrouting.ob.DeliverData(ni, dtnrouting.allNodes.get(ni.n1_neighborhood.get(k)));
		}
}
	

//******************************************************************************

}//END OF PLAYFIELD CLASS
