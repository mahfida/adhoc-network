
//******************************************************************************
//PACKAGE

package MovementPattern;

//******************************************************************************
//IMPORT FILES
import java.util.Random;
import DTNRouting.*;

//******************************************************************************
//MOVEMENT PROCESS OF A NODE

public class NodeMovement extends dtnrouting
{
    //Instance Variables
    Random rand;
    static int speedController=0; // controls the speed of different nodes
    static int divisor,numOfPoints;
    int value,h;
    static int[] limit_x;
    static int[] limit_y;
    static int pseudo_array[][][];   //first array show no of nodes, 2nd show the position and 3rd show x,y points
    static int[] nodePeriod;
    public static int warmupPeriod=0;
    static boolean event=false,changePositions=false,followPath=true;// decides whether to change the current  position of node or not

//******************************************************************************
//CONSTRUCTOR

public NodeMovement()
{
        rand=new Random();
}

//******************************************************************************
//GIVING INITIAL POSITION TO A NODE

public  void InitialNodePositions(Node n)
{
      n.nextX=n.nodeX=rand.nextInt(n.nodeRegion.w-n.getRadioRange())+n.nodeRegion.x;  //genearte random value for x and y positions of node
      n.nextY=n.nodeY=rand.nextInt(n.nodeRegion.h-n.getRadioRange())+n.nodeRegion.y;
      n.setStartPositions();
}

//******************************************************************************
//RANDOM PATH FOR A NODE

public void RandomMovement()
{
 int m=nodeArray.size();
 speedController=rand.nextInt(100)+1;
 for (int k=0;k<m;k++)
 {
     //Access one node at a time from its array list
     Node n=new Node();
     n=nodeArray.get(k);
     if(n.name.substring(0,1).equals("D"))
         ;  //do nothing
     else
     {
        if(n.speed>=speedController)
        {
            if (n.nextX < n.nodeX)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
                            n.nodeX--;

            else if(n.nextX>n.nodeX)
                            n.nodeX++;

            else if(n.nextX==n.nodeX)
            n.nextX= rand.nextInt(n.nodeRegion.w-n.getRadioRange())+n.nodeRegion.x;  //genearte random value for x and y positions of node
                                             //genearte random value for next x position of node

            if(n.nextY<n.nodeY)
                            n.nodeY--;

            else if(n.nextY>n.nodeY)
                            n.nodeY++;

            else if(n.nextY==n.nodeY)
            n.nextY=rand.nextInt(n.nodeRegion.h-n.getRadioRange())+n.nodeRegion.y;
         }
       }
 }
}

//******************************************************************************
//FINDING OUT LOCATIONS WHICH A NODE VISITS WHILE FOLLOWING A PSEUDORANDOM PATH

public void InitializePsuedoPath()
{
 
            //Find out hot spot locations in each region
            int r=dtnrouting.RegionArray.size();
            nodePeriod=new int[dtnrouting.nodeArray.size()];
            resetPerimeters();
            for(int j=0;j<r;j++)
            {
               Regions region=dtnrouting.RegionArray.get(j);
               //if region jave nodes
               if(region.getNetworkNodes().size()>0)
               region.hotspots();
            }
            //Decide path for each
            h=dtnrouting.nodeArray.size();
            for(int j=0;j<h;j++)
            {
                Node node=dtnrouting.nodeArray.get(j);
                node.setPath();
            }
    
}

//******************************************************************************
//
public int PseudoRanNumGen(int index,char point, Node node)
{
  if(point=='x')
  {

        node.x_inc+=1;   //increment index(node) of limit_x array
        if(node.x_inc==node.x_max)   //when index is equal to numofpoints
        {
            //Find out whether the warup period ended or not
             if(nodePeriod[index]==0)
            {
                nodePeriod[index]=1;
                warmupPeriod+=1;

            }
            node.x_inc = 0;       //set again it to 0
        }

        value=node.nodePath[node.x_inc][0];
     }

  else if(point=='y')
  {
        node.y_inc+=1;   //increment index(node) of limit_x array

        if(node.y_inc==node.y_max)   //when index is equal to numofpoints
        {
            node.y_inc = 0;       //set again it to 0
        }
        value=node.nodePath[node.y_inc][1];
   }

  return(value);
}
//******************************************************************************
//MOVEMENT OF A NODE IN PSUDORANDOM PATH

public void PseudoRandomMovement()
{
    for (int k=0;k<nodeArray.size();k++)
    {
        //Access one node at a time from its array list
        Node n=new Node();
        n=nodeArray.get(k);
        if(n.name.substring(0,1).equals("D"))
         ;  //do nothing
        else
        {

            if(n.nextX<n.nodeX)
               n.nodeX--;

            else if(n.nextX>n.nodeX)
               n.nodeX++;

            else if(n.nextX==n.nodeX)
               x_reached=true;  //do not genearte new no but stop on the same

            if(n.nextY<n.nodeY)
               n.nodeY --;

            else if(n.nextY>n.nodeY)
               n.nodeY ++;

            else if(n.nextY==n.nodeY)
               y_reached=true;

            if((x_reached==true)&&(y_reached==true))
            {
               //Whether to go to new position or not
               if(n.takeNewPosition==true)
               {
                    
                    n.nextX=PseudoRanNumGen(k,'x',n);  //prng=puesodorandomnumbergenerator,take a puesodo no
                    n.nextY=PseudoRanNumGen(k,'y',n);//prng=puesodorandomnumbergenerator
                    
                    n.takeNewPosition=false;
                    n.nodeRegion.count+=1;
                    if(n.nodeRegion.count==n.nodeRegion.numNodes)
                    {
                      n.nodeRegion.count=0;
                      Regions nodezRegion=new Regions();
                      nodezRegion=n.nodeRegion;
                      for(int i=0;i<nodezRegion.numNodes;i++)
                         nodezRegion.getNetworkNodes().get(i).takeNewPosition=true;
                    }
               }
                x_reached=false;  //make it again false
                y_reached=false;
           }
          }//End of else
       }//End of for loop
}//End of method

//******************************************************************************
//INTIAL POSITION FOR NODES OF A DATASET

public  void InitialDSPath()
{
    nodePeriod=new int[dtnrouting.nodeArray.size()];

      for( i=0;i<dtnrouting.nodeArray.size();i++)
      {
          dtnrouting.nodeArray.get(i).nextX=dtnrouting.nodeArray.get(i).nodeX=dtnrouting.nodeArray.get(i).nodePath[0][0];//x-coord
          dtnrouting.nodeArray.get(i).nextY=dtnrouting.nodeArray.get(i).nodeY=dtnrouting.nodeArray.get(i).nodePath[0][1];//y-coord
          dtnrouting.nodeArray.get(i).setStartPositions();
      }
  
}

//******************************************************************************
//PATH FOR A DATA SET NODE
public void DatasetMovement()
{
    
    for (int k=0;k<nodeArray.size();k++)
    {
        //Access one node at a time from its array list
        Node n=new Node();
        n=nodeArray.get(k);
        if(n.nextX<n.nodeX)
           n.nodeX--;

        else if(n.nextX>n.nodeX)
           n.nodeX++;

        else if(n.nextX==n.nodeX)
           x_reached=true;  //do not genearte new no but stop on the same

        if(n.nextY<n.nodeY)
           n.nodeY --;

        else if(n.nextY>n.nodeY)
           n.nodeY ++;

        else if(n.nextY==n.nodeY)
           y_reached=true;

        if((x_reached==true)&&(y_reached==true))
        {

           //Whether to go to new position or not
           if(n.takeNewPosition==true)
           {
               //if(n.nodeID==1)
                 //  System.out.println(n.nodeID+":"+n.nodeX+","+n.nodeY);
                n.takeNewPosition=false;
                n.nodeRegion.count+=1;
                if(n.nodeRegion.count==n.nodeRegion.numNodes)
                {
                  for(h=0;h<dtnrouting.nodeArray.size();h++)
                  {
                      isSamePoint(dtnrouting.nodeArray.get(h));
                      dtnrouting.nodeArray.get(h).nextX=DSNextPoint(h,'x',dtnrouting.nodeArray.get(h));
                      dtnrouting.nodeArray.get(h).nextY=DSNextPoint(h,'y',dtnrouting.nodeArray.get(h));
                      
                  }
                  changePositions=true;
                  n.nodeRegion.count=0;
                }
           }
            x_reached=false;  //make it again false
            y_reached=false;
       }
     
       }//End of for loop
}
//******************************************************************************
//New Positions can be taken
public void newPositions()
{
    if(changePositions==true)
    {
        for (i = 0; i < dtnrouting.nodeArray.size(); i++)
           dtnrouting.nodeArray.get(i).takeNewPosition=true;
        changePositions=false;
    }
}
//******************************************************************************

//NEXT POSITION OF A NODE IN A DS PATH

public int DSNextPoint(int index,char point, Node node)
{
  if(point=='x')
  {

        node.x_inc+=1;   //increment index(node) of limit_x array
        if(node.x_inc==node.x_max)   //when index is equal to numofpoints
        {
            //Find out whether the warup period ended or not
             if(nodePeriod[index]==0)
            {
                nodePeriod[index]=1;
                warmupPeriod+=1;
                System.out.println("WARMUP:"+warmupPeriod);

            }
            node.x_inc = 0;       //set again it to 0
        }
        //Assign path coordinate point according to meetinf probability
        if(followPath==true)
        value=node.nodePath[node.x_inc][0];
        else
        value=rand.nextInt(node.nodeRegion.w-node.getRadioRange())+node.nodeRegion.x;
  }

  else if(point=='y')
  {
        node.y_inc+=1;   //increment index(node) of limit_x array

        if(node.y_inc==node.y_max)   //when index is equal to numofpoints
        {
            node.y_inc = 0;       //set again it to 0
        }
        //Assign path coordinate point according to meetinf probability
        if(followPath==true)
        value=node.nodePath[node.y_inc][1];
        else
        value=rand.nextInt(node.nodeRegion.h-node.getRadioRange())+node.nodeRegion.y;
   }

  return(value);
}
//******************************************************************************
//RESET PATH
public static void resetPerimeters()
{  //In case a data set is used
    for(int i=0;i<dtnrouting.nodeArray.size();i++)
    {
        nodePeriod[i]=0;
      //  dtnrouting.nodeArray.get(i).getStartPositions();
    }
    warmupPeriod=0;
}
//******************************************************************************
//According to meeting probability find whether coordinate-point of
//node Path will be followed or not
public void isSamePoint(Node n)
{
    double r;
    r=rand.nextDouble();
    int x=0;
    if((n.x_inc+1)<n.x_max)
        x=n.x_inc+1;
    if(n.pointProbability[x]==0 || r<=n.pointProbability[x])
        followPath=true;
    else
        followPath=false;

}
//******************************************************************************

} //end of Program




