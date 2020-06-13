/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MovementPattern;

import DTNRouting.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author sarmad
 */
public class SingleFerryMovement {
    Random rand;
    static boolean isNewPosition=false, isNewRegion=false;
    static Regions nextRegionObj =dtnrouting.RegionwithNodes.get(0);
    static int index=0,nodeTracker=0, lastRegionID=0;
    static ArrayList<Regions> tempRegionWithNodes=new ArrayList();

    //for random movement initial position is random
    public SingleFerryMovement()
    {
     
    }
    public void nonRealInitialPosition(Node node)
    {
    
    tempRegionWithNodes.add(dtnrouting.RegionwithNodes.get(0));
    Regions    regionObj=dtnrouting.RegionwithNodes.get(0);
    node.nextX=node.nodeX=regionObj.x+(regionObj.w)/2-node.getRadioRange()/2;
    node.nextY=node.nodeY=regionObj.y+(regionObj.h)/2-node.getRadioRange()/2;
    node.setStartPositions();
    }
    //for random movement all the points within the regions are random selected
    public void randomPath(Node node)
    {
    //if single region then move randomly within that region
    if(dtnrouting.RegionwithNodes.size()==1)
        randomMovement(node,dtnrouting.RegionwithNodes.get(0));
    //if more than one region then every time there will be a new Reagion
    else
            {
                if(isNewPosition==true)
                {
                nextRegionObj=nextNearestRegion(nextRegionObj,lastRegionID);
                isNewPosition=false;
                }
                randomMovement(node,nextRegionObj);
            }

    }
    //in real scenario fixed polygon path will be followed
    public void fixedRealInitialPosition()
    {

    }
    public void fixedRealPath()
    {

    }

    //if nodes are static the ferry will traverse each node wihtin a region
    public void fixedPathwithinRegions(Node node)
    {
       if(dtnrouting.RegionwithNodes.size()==1)
        {      //when all the nodes are traversed go the first node
           if(isNewPosition==true)
           {
               if(nodeTracker==(dtnrouting.RegionwithNodes.get(0).getNetworkNodes().size()-1))  nodeTracker=0;
               else nodeTracker+=1;
               isNewPosition=false;
           }
           movementInSaticNetwork(node,dtnrouting.RegionwithNodes.get(0),nodeTracker);
        }
         
            else
            {
                //if more than one region then every time there will be a new Region
               if(isNewPosition==true)
                {
                        if(nodeTracker==(nextRegionObj.getNetworkNodes().size()-1))  { nodeTracker=0; isNewRegion=true;}
                        else nodeTracker+=1;
                        isNewPosition=false;
                }
                 //if more than one region then every time there will be a new Region
                if(isNewRegion==true)
                {
                nextRegionObj=nextNearestRegion(nextRegionObj,lastRegionID);
                node.visitingRegion=nextRegionObj;
                isNewRegion=false;
                }
                movementInSaticNetwork(node,nextRegionObj,nodeTracker);
            }
    }
    public void movementInSaticNetwork(Node n,Regions regionObj,int i)
    {
     rand=new Random();
    int speedController=rand.nextInt(100)+1;
        if(n.speed>=speedController)
        {
            if (n.nextX < n.nodeX)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
                            n.nodeX--;

            else if(n.nextX>n.nodeX)
                            n.nodeX++;

            if(n.nextY<n.nodeY)
                            n.nodeY--;

            else if(n.nextY>n.nodeY)
                            n.nodeY++;
            if(n.nextX==n.nodeX && n.nextY==n.nodeY)
            {
                System.out.println("Name of Region: "+regionObj.RegionName+" node number: "+i);
                n.nextX=regionObj.getNetworkNodes().get(i).nodeX;
                n.nextY=regionObj.getNetworkNodes().get(i).nodeY;
                isNewPosition=true;
            }

        }
    }
    public void randomMovement(Node n, Regions regionObj)
    {
    rand=new Random();
    int speedController=rand.nextInt(100)+1;
        if(n.speed>=speedController)
        {
            if (n.nextX < n.nodeX)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
                            n.nodeX--;

            else if(n.nextX>n.nodeX)
                            n.nodeX++;
                        
            if(n.nextY<n.nodeY)
                            n.nodeY--;

            else if(n.nextY>n.nodeY)
                            n.nodeY++;

           
            //network is static
            if(n.nextX==n.nodeX && n.nextY==n.nodeY && regionObj.getNetworkType().equals("Mobile"))
            {
                n.nextX = rand.nextInt(regionObj.w - n.getRadioRange()) + regionObj.x;  //genearte random value for x and y positions of node
                n.nextY = rand.nextInt(regionObj.h - n.getRadioRange()) + regionObj.y;
                isNewPosition=true;
            }
            //network is mobile
            if(n.nextX==n.nodeX && n.nextY==n.nodeY && regionObj.getNetworkType().equals("Static"))
            {
                n.nextX = regionObj.getMidX()-n.getRadioRange()/2;  //genearte random value for x and y positions of node
                n.nextY = regionObj.getMidY()-n.getRadioRange()/2;
                isNewPosition=true;
            }
        }
   }
   public Regions nextNearestRegion(Regions regionObj,int lastRegionNumber)
    {
    Regions nextRegion=new Regions();
    double min=10000.0;
    //If tempRegionWithNodesis Empty
    if(tempRegionWithNodes.size()==1)
    {
        tempRegionWithNodes.clear();
        tempRegionWithNodes.addAll(dtnrouting.RegionwithNodes);
    }
    
    tempRegionWithNodes.remove(regionObj);
  
    for(int i=0;i<tempRegionWithNodes.size(); i++)
    {
            
            Regions reg=tempRegionWithNodes.get(i);
            double d=Math.sqrt(Math.pow(regionObj.getMidX()-reg.getMidX(), 2.0) +Math.pow(regionObj.getMidY()-reg.getMidY(), 2.0));
            if(min>d)
            {
                min=d;
                nextRegion=reg;
                lastRegionID=i;
            }
        
        
    }

    return(nextRegion);
   }
   public static void setPerimeters()
    {
      isNewPosition=false; isNewRegion=false;
      nextRegionObj =dtnrouting.RegionwithNodes.get(0);
      index=0;nodeTracker=0; lastRegionID=0;
   }

}
