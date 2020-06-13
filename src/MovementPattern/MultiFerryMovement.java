//******************************************************************************
//PACKAGE
package MovementPattern;

//******************************************************************************
//IMPORT FILES
import DTNRouting.*;
import java.util.ArrayList;
import java.util.Random;

//******************************************************************************
//MOVEMENT OF MULTIPLE FERRY NODES CLASS

public class MultiFerryMovement
{
    //Instance Variables
    Random rand;
    static boolean isNewPosition=false, isNewRegion=false;
    static Regions nextRegionObj =dtnrouting.RegionArray.get(0);
    static int index=0,nodeTracker=0, lastRegionID=0;
    static ArrayList<Regions> tempRegionArray=new ArrayList();

//******************************************************************************
//CONSTRUCTOR

public MultiFerryMovement() {}

//******************************************************************************
//INITIAL POSITIONS OF FERRIES IN A NON-REAL SITUATION

public void nonRealInitialPosition(Node node)
{
    tempRegionArray.add(dtnrouting.RegionArray.get(0));
    Regions regionObj=dtnrouting.RegionArray.get(0);
    node.nextX=node.nodeX=regionObj.x+(regionObj.w)/2-node.getRadioRange()/2;
    node.nextY=node.nodeY=regionObj.y+(regionObj.h)/2-node.getRadioRange()/2;
}

//******************************************************************************
//FOR RANDOM MOVEMENT OF NODES IN A REGION

public void randomPath(Node node)
{
//if region is only one then ferry will move randomly within only that region
if(dtnrouting.RegionArray.size()==1)
    randomMovement(node,dtnrouting.RegionArray.get(0));
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
//******************************************************************************
//IN REAL SCENARIO A FIXED POLYGON PATH IS FOLLOWED

public void fixedRealInitialPosition() {}
public void fixedRealPath() {}

//******************************************************************************
//IF NODES ARE STATIC THE FERRY WILL VISIT EACH NODE OF THE REGION

public void fixedPathwithinRegions(Node node)
{
    if(dtnrouting.RegionArray.size()==1)
    {
       //when all the nodes are traversed go the first node
       if(isNewPosition==true)
       {
           if(nodeTracker==(dtnrouting.RegionArray.get(0).getNetworkNodes().size()-1))  nodeTracker=0;
           else nodeTracker+=1;
           isNewPosition=false;
       }
       movementInSaticNetwork(node,dtnrouting.RegionArray.get(0),nodeTracker);
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
        isNewRegion=false;
        }
        movementInSaticNetwork(node,nextRegionObj,nodeTracker);
    }
}//End of method()

//******************************************************************************
//MOVEMENT PROCESS OF A NODE INSIDE A STATIC NETWORK

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
            n.nextX=regionObj.getNetworkNodes().get(i).nodeX;
            n.nextY=regionObj.getNetworkNodes().get(i).nodeY;
            isNewPosition=true;
        }

    }
}

//******************************************************************************
//
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
        if(n.nextX==n.nodeX && n.nextY==n.nodeY && regionObj.getNetworkType().equals("Static"))
        {
            n.nextX = rand.nextInt(regionObj.w - n.getRadioRange()) + regionObj.x;  //genearte random value for x and y positions of node
            n.nextY = rand.nextInt(regionObj.h - n.getRadioRange()) + regionObj.y;
            isNewPosition=true;
        }
        //network is mobile
        if(n.nextX==n.nodeX && n.nextY==n.nodeY && regionObj.getNetworkType().equals("Mobile"))
        {
            n.nextX = regionObj.getMidX()-n.getRadioRange()/2;  //genearte random value for x and y positions of node
            n.nextY = regionObj.getMidY()-n.getRadioRange()/2;
            isNewPosition=true;
         }
    }
}

//******************************************************************************
// FINDING OUT NET NEAREST REGION FOR FERRY TO VISIT

public Regions nextNearestRegion(Regions regionObj,int lastRegionNumber)
{
    Regions nextRegion=new Regions();
    double min=10000.0;

    //If tempRegionArrayis Empty
    if(tempRegionArray.size()==1)
    {
        tempRegionArray.clear();
        tempRegionArray.addAll(dtnrouting.RegionArray);
    }
    else
        tempRegionArray.remove(regionObj);

        for(int i=0;i<tempRegionArray.size(); i++)
        {
                if(lastRegionNumber!=i || tempRegionArray.size()==1)
                {
                Regions reg=tempRegionArray.get(i);
                double d=Math.sqrt(Math.pow(regionObj.getMidX()-reg.getMidX(), 2.0) +Math.pow(regionObj.getMidY()-reg.getMidY(), 2.0));
                    if(min>d)
                    {
                        min=d;
                        nextRegion=reg;
                        lastRegionID=i;
                    }
                }
        }
    return(nextRegion);
} //End of method
}//End of class
