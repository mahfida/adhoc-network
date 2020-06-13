//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.util.ArrayList;
import java.util.Iterator;
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
private static ArrayList<Node> allNodes=new ArrayList();
public static boolean isContactPresent[][];

//******************************************************************************
//EMPTY CONSTRUCTOR

public PlayField() {}

//******************************************************************************
//DRAW NODES ALONG WITH THEIR BUNDLES IN THE PLAYFIELD OF APPLET

public void drawNodesBundles(Graphics g)
{
Graphics2D g2 = (Graphics2D)g;
g.setFont(new Font("Dialog",Font.BOLD,9));

//Displaying Nodes and the bundle that they hold
for (int k=0;k<dtnrouting.allNodes.size();k++)
{
    //Access one node at a time from its array list
    Node n=new Node();
    n=dtnrouting.allNodes.get(k);
    int r=n.getRadioRange();  //set the size of nodes
    r=r/2;
    g2.setStroke(new BasicStroke(3));
    g.setColor(Color.black);

    //Drawing nodes of different names with differnt colors
    if(n.name.substring(0,1).equals("R"))
    {
        if(Setting.enviroment.equals("Non-Real Life"))
            g2.setPaint(Color.LIGHT_GRAY);

        else if(Setting.realType.equals("Map"))
            g2.setPaint(Color.RED);
        else if(Setting.realType.equals("Dataset"))
            g2.setPaint(new Color(0xb0c4de));
    }
    
    else if(n.name.substring(0, 1).equals("F"))   g2.setPaint(Color.PINK);

    else if(n.name.substring(0, 1).equals("D"))   g2.setPaint(Color.BLUE);
    
    else if(n.name.equals("CH"))                  g2.setPaint(Color.GREEN);
 

    Ellipse2D e = new Ellipse2D.Double(n.nodeX, n.nodeY, r, r);
    e.setFrame(n.nodeX, n.nodeY, r, r);
    g2.draw(e);

    //Put name of node inside node circle
    g.setColor(Color.black);
    g.drawString(n.name, n.nodeX+(r)/2-5, n.nodeY+(r)/2+2);

    //Show whether bundel is present NOTE: for one bundle only
    if(!n.DestNBundle.isEmpty())
    {
        int b=n.DestNBundle.size();
        Set setBundle=n.DestNBundle.keySet();
        Iterator it=setBundle.iterator();
        int x=n.nodeX+r/2-6;
        int y=n.nodeY+r/2-4;
        while(it.hasNext())
        {
            Bundle bundleObj=(Bundle)it.next();
            g.setColor(bundleObj.color);
            g.fillOval(x, y, 10, 10);

            //If bundle is expired or large enough to be stored inside a node then discard it
            if(bundleObj.isTTLExpired==true ||bundleObj.isLargeSize==true )
            {

                g.setColor(Color.BLACK);
                g2.drawLine(x,y,x+10,y+10);
                g2.drawLine(x+10,y,x,y+10);

            }
            //If node has more than one bundle then next bundle is displayed
            //near the ealier one in the same node
            x=x+11;
        } //End of while loop
    }

    g.setColor(Color.black);
                  //divide it by two so that the text comes in the mid of the node
    } //End of if statement
}

//******************************************************************************
//DRAW REGIONS IN PLAYFIELD

public void  drawRegions(Graphics g)
{
    Graphics2D g2 = (Graphics2D)g;
    for(int f=0;f<dtnrouting.RegionArray.size();f++)
    {
        Regions ob=dtnrouting.RegionArray.get(f);
        g2.drawRect(ob.x,ob.y,ob.w,ob.h);
    }
     
}

//******************************************************************************
//FIND WHETHER A CONTACT IS PRESENT BETWEEN ANY PAIR OF NODES

boolean FindIntersection(Node ni,Node nj) //to find the intersection between nodes
{

   //******************************************************
   //if Data set then if corrdinates match
   if(Setting.realType.equals("Dataset"))
   {
        //System.out.println("Dataset");
        if(ni.nodeX==nj.nodeX && ni.nodeY==nj.nodeY)
       
            //System.out.println(ni.nodeID+":Intersect:"+ni.nodeX+","+ni.nodeY);
                //  System.out.println(nj.nodeID+":Intersect:"+nj.nodeX+","+nj.nodeY);
            return true;
    
        else
            return false;
   }

   //*******************************************************

    //If not Dataset then
    else
    {
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
    }
   //*********************************************************
    }

//******************************************************************************
//IF CONTACT PRESENT TRANSFER BUNDLE, IF NEEDED

public void transferBundle()
{

outerloop: for (int i =0;i< (dtnrouting.allNodes.size()-1); i++)
            for(int j=i+1;j <dtnrouting.allNodes.size();j++)
            if(i!=j) //if i and j are separate nodes
            {

                Node ni=new Node();  //node i
                Node nj=new Node(); // node j
                ni=dtnrouting.allNodes.get(i);
                nj=dtnrouting.allNodes.get(j);
                  // System.out.println(ni.nodeID+":"+ni.nodeX+","+ni.nodeY);
                  //System.out.println(nj.nodeID+":"+nj.nodeX+","+nj.nodeY);
                //contacts of nodes
                dtnrouting.isIntersect=FindIntersection(ni,nj);
                if(dtnrouting.isIntersect)
                {
                   
                    if(isContactPresent[i][j]==true)
                    {
                      dtnrouting.contactDuration[ni.nodeID - 1][nj.nodeID - 1] += 1;
                      dtnrouting.contactDuration[nj.nodeID - 1][ni.nodeID - 1] += 1;
                      if(dtnrouting.protocolName.equals("NECTAR"))
                      {
                            dtnrouting.ob.ContactCounter(ni.nodeID - 1, nj.nodeID - 1);
                            dtnrouting.ob.ContactCounter(nj.nodeID - 1, ni.nodeID - 1);
                      }
                    }
                    // when new nodes comes into contact then deliver the message
                   if(isContactPresent[i][j]==false)
                    {
                    //when new nodes comes into contact then display it on the current situation textarea
                    dtnrouting.currentSituatonTA.insert(ni.name+" <--->"+nj.name+"\n", 0);
                   // if(ni.nodeID==7 || nj.nodeID==7)
                 //   System.out.print(ni.name+" <--->"+nj.name+"\n");

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
                   dtnrouting.contactDuration[ni.nodeID-1][nj.nodeID-1]=0;
                   dtnrouting.contactDuration[nj.nodeID-1][ni.nodeID-1]=0;
                }
            }//End of 2 for loops and their if statement
}

//******************************************************************************

}//END OF PLAYFIELD CLASS
