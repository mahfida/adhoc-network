
//******************************************************************************
//PACKAGE

package MovementPattern;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import java.util.ArrayList;
import java.util.Random;

//******************************************************************************
//MOVEMENT OF A PIGEON

public class PigeonMovement
{
    //Instance Variable
    Random rand;

//******************************************************************************
//CONSTRUCTOR

public PigeonMovement(){}

//******************************************************************************
//INITIAL POSITION OF A PIGEON

public void InitialPosition(Node n )
{
     n.nextX=n.nodeX=n.nodeRegion.getMidX()-n.getRadioRange()/2;
     n.nextY=n.nodeY=n.nodeRegion.getMidY()-n.getRadioRange()/2;
     n.setStartPositions();
}

//******************************************************************************
//PATH OF A NODE

public void movementPath( )
{
//path of clusterHead in static network

 for(int i=0;i<dtnrouting.ferryArray.size();i++)
 {
     Node n=dtnrouting.ferryArray.get(i);
     if(n.name.equals("CH"))
     {
            if(n.isNewPosition==true)
            {
              if(n.nodeTracker==(dtnrouting.RegionArray.get(0).getNetworkNodes().size()-1))  n.nodeTracker=-1;
              else n.nodeTracker+=1;               n.isNewPosition=false;
            }
            clusterHeadMovement(n,n.nodeTracker);
     }
    else
         ferryMovement(n);
 }
}

//******************************************************************************
//MOVEMENT OF A CLUSTER HEAD PIGEON

public void clusterHeadMovement(Node n,int i)
{
//Find Region of the clusterHead node
     Regions regionObj=n.nodeRegion;
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
        if(n.nextX==n.nodeX && n.nextY==n.nodeY)
        {
            if(i==-1)
            {
                n.nextX=regionObj.getMidX()-n.getRadioRange()/2;
                n.nextY=regionObj.getMidY()-n.getRadioRange()/2;
            }

            else
            {
                n.nextX=regionObj.getNetworkNodes().get(i).nodeX;
                n.nextY=regionObj.getNetworkNodes().get(i).nodeY;
            }
            n.isNewPosition=true;
        }
    }
}

//******************************************************************************
//MOVEMENT OF A FERRY

public void ferryMovement(Node n)
{
    Regions regionObj=new Regions();    regionObj=n.nodeRegion;
    //if the ferry hase messages, it will deliver them on the basis of shortest path towards their destination
    ArrayList <Regions> tempRegionArray =new ArrayList();

    //Time for selecting new positions
    if(n.isNewPosition==true)
    {
        if(n.DestNBundle.isEmpty()==false)
           {

                for (int j=0;j<dtnrouting.nodeArray.size();j++)
                {
                Node node=dtnrouting.nodeArray.get(j);
                if(n.DestNBundle.containsValue(node)==true && tempRegionArray.contains(node.nodeRegion)==false)
                    {
                        tempRegionArray.add(node.nodeRegion);
                     }
                }

                regionObj=nextNearestRegion(n, n.visitingRegion,tempRegionArray);
          }
        else
            n.isNewPosition=false;
     }

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
        if(n.nextX==n.nodeX && n.nextY==n.nodeY)
        {
            n.nextX=regionObj.getMidX()-n.getRadioRange()/2;
            n.nextY=regionObj.getMidY()-n.getRadioRange()/2;
            n.isNewPosition=true;
        }
    }
    }

//******************************************************************************
//FINDING NEXT NEAREST REGION FOR A PIGEON TO VISIT TO

public Regions nextNearestRegion(Node n,Regions vistingRegion, ArrayList <Regions> tempRegionArray)
{
    Regions returnRegion=new Regions();
    double min=10000.0;
    for(int i=0;i<tempRegionArray.size(); i++)
    {
        Regions reg=tempRegionArray.get(i);
        double d=Math.sqrt(Math.pow(vistingRegion.getMidX()-reg.getMidX(), 2.0) +Math.pow(vistingRegion.getMidY()-reg.getMidY(), 2.0));
        if(min>d)
        {
            min=d;
            returnRegion=n.visitingRegion=reg;
         }
     }
    return(returnRegion);
}//End of method

//******************************************************************************

}//End of class

