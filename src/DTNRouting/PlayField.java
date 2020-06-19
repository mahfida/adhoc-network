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
			r=r/2;
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

				//int b=n.DestNPacket.size();
				Set<Packet> setPacket=n.DestNPacket.keySet();
				Iterator<Packet> it=setPacket.iterator();
				int x=n.nodeX+r/2-6;
				int y=n.nodeY+r/2-1;
				while(it.hasNext())
				{
					Packet packetObj=(Packet)it.next();
					g.setColor(packetObj.packet_color);
					g.fillOval(x, y, 10, 10);

					//If packet is expired or large enough to be stored inside a node then discard it
					if(packetObj.isTTLExpired==true ||packetObj.isLargeSize==true )
					{

						g.setColor(Color.BLACK);
						g2.drawLine(x,y,x+10,y+10);
						g2.drawLine(x+10,y,x,y+10);

					}
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

		/*		System.out.println(ni.nodeX + ", " + ni.nodeY + ", " + ni.getRadioRange() + "; " + nj.nodeX + ", " + nj.nodeY + ", " + nj.getRadioRange()); 
		System.out.println(x1 + ", " + y1 + ", " + r1 + "; " + x2 + ", " + y2 + ", " + r2);
		System.out.println(r + ", " + distance_km);
		System.exit(0); */

		if(distance_km <= r)  return getLinkCapacity(distance_km);
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
		// Generate n1_neiborhood
		for (int i = 0; i < (dtnrouting.allNodes.size()-1); i++) 
			for(int j = i+1; j < dtnrouting.allNodes.size(); j++) 
				{
					Node ni = dtnrouting.allNodes.get(i);  //node i
					Node nj = dtnrouting.allNodes.get(j);  // node j				
					//If contact is present between nodes in current time stamp
					dtnrouting.linkCapacities[ni.ID-1][nj.ID-1] = FindIntersection(ni, nj);
					dtnrouting.linkCapacities[nj.ID-1][ni.ID-1] = dtnrouting.linkCapacities[ni.ID-1][nj.ID-1];
					
					if(dtnrouting.linkCapacities[ni.ID-1][nj.ID-1] > 0.0)
					{
						// when new nodes comes into contact then deliver the message		
						dtnrouting.n1_neighborhood[ni.ID-1][nj.ID-1] = 1; 
						dtnrouting.n1_neighborhood[nj.ID-1][ni.ID-1] = 1;
						dtnrouting.n2_neighborhood[ni.ID-1][nj.ID-1] = 1;
						dtnrouting.n2_neighborhood[nj.ID-1][ni.ID-1] = 1;
					}
					else 
					{
						dtnrouting.n1_neighborhood[ni.ID-1][nj.ID-1] = 0; 
						dtnrouting.n1_neighborhood[nj.ID-1][ni.ID-1] = 0;
						dtnrouting.n2_neighborhood[ni.ID-1][nj.ID-1] = 0;
						dtnrouting.n2_neighborhood[nj.ID-1][ni.ID-1] = 0;
				
					}}
		
		// Generate n2_neighborhood from n1_neiborhood
		for (int i = 0; i < (dtnrouting.allNodes.size()-1); i++) 
			for(int j = i+1; j < dtnrouting.allNodes.size(); j++) 
				{
					if(dtnrouting.n1_neighborhood[i][j] == 1) 
					{
					for(int k = 1; k < dtnrouting.allNodes.size(); k++) {
							if(dtnrouting.n1_neighborhood[j][k]==1 ) {
							if(i!=k) {
									dtnrouting.n2_neighborhood[i][k] = 1;
									dtnrouting.n2_neighborhood[k][i] = 1;
					}}}}}
		//Transfer Packets according to N2-Coloring
		TransferPackets(); 					
	}// Find neighbor hood ended

	public void TransferPackets()
	{
		/*Decide from N2 Neighbor-hood which two nodes can 
		 transfer packets within current time stamp
		 also update link capacities accordingly */
		
		//dtnrouting.currentSituatonTA.insert(ni.ID+" <--->"+nj.ID+"\n", 0); //when new nodes comes into contact then display it on the current situation text area
		//dtnrouting.ob.Deliver(ni, nj);
	}
	

//******************************************************************************

}//END OF PLAYFIELD CLASS
