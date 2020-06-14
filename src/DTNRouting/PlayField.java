//PACKAGE NAME
package DTNRouting;

import java.util.Iterator;
//IMPORT PACKAGES
import java.util.Set;
import java.awt.*;
import java.awt.geom.*;

//******************************************************************************
//START OF THE CLASS PLAYFIED, DISPLAYING MOVING NODES AND REGIONS/MAP

public class PlayField
{
//Instance Variables
static boolean hasDeliverCalled[][]=new boolean[dtnrouting.allNodes.size()][dtnrouting.allNodes.size()];
static dtnrouting dtn=new dtnrouting();
public static boolean isContactPresent[][];

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

boolean FindIntersection(Node ni,Node nj) //to find the intersection between nodes
{

   //******************************************************
   //mid point and radius of ni
    double x1=(ni.nodeX+ni.nodeX+(ni.getRadioRange()))/2;
    double y1=(ni.nodeY+ni.nodeY+(ni.getRadioRange()))/2;
    double r1=(ni.getRadioRange())/2;

    //mid point and radius of nj
    double x2=(nj.nodeX+nj.nodeX+(nj.getRadioRange()))/2;
    double y2=(nj.nodeY+nj.nodeY+(nj.getRadioRange()))/2;
    double r2=(nj.getRadioRange())/2;
    
    double d=Math.sqrt(Math.pow((y2-y1),2)+Math.pow((x2-x1),2));
    double r=r1+r2;
    if(d<=r) return true;
    else return false;

   //*********************************************************
}

//******************************************************************************
//IF CONTACT PRESENT TRANSFER packet, IF NEEDED

public void transferpacket()
{

for (int i =0;i< (dtnrouting.allNodes.size()-1); i++)
            for(int j=i+1;j <dtnrouting.allNodes.size();j++)
            if(i!=j) //if i and j are separate nodes
            {
            	Node ni=new Node();  //node i
                Node nj=new Node(); // node j
                ni=dtnrouting.allNodes.get(i);
                nj=dtnrouting.allNodes.get(j);
     
                //contacts of nodes
                dtnrouting.isIntersect=FindIntersection(ni,nj);
                if(dtnrouting.isIntersect)
                {
                   
                    if(isContactPresent[i][j]==true)
                    {
                      dtnrouting.contactDuration[ni.ID - 1][nj.ID - 1] += 1;
                      dtnrouting.contactDuration[nj.ID - 1][ni.ID - 1] += 1;

                    }
                    // when new nodes comes into contact then deliver the message
                   if(isContactPresent[i][j]==false)
                    {
                    //when new nodes comes into contact then display it on the current situation text area
                    dtnrouting.currentSituatonTA.insert(ni.ID+" <--->"+nj.ID+"\n", 0);
                   
                    }
                    //dtnrouting.ob.Deliver(ni, nj);
                    dtnrouting.isIntersect=false;
                    isContactPresent[i][j]=true;
                 }
                else
                {
                   if(isContactPresent[i][j]==true)
                        dtnrouting.ob.Deliver(ni, nj);
                   isContactPresent[i][j]=false;
                   dtnrouting.contactDuration[ni.ID-1][nj.ID-1]=0;
                   dtnrouting.contactDuration[nj.ID-1][ni.ID-1]=0;
                }
            }//End of 2 for loops and their if statement
}

//******************************************************************************

}//END OF PLAYFIELD CLASS
