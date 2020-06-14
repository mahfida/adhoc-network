
//******************************************************************************
//PACKAGE

package DTNRouting;

import java.util.ArrayList;
//******************************************************************************
//IMPORT FILES
import java.util.Random;

//******************************************************************************
//MOVEMENT PROCESS OF A NODE

public class NodeMovement extends dtnrouting
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
    public int numNodes=0,count=0;

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
	  n.x_coord.clear();
	  n.y_coord.clear();
	  n.nodeX = rand.nextInt(dtnrouting.width-2*n.getRadioRange()) + dtnrouting.x_start;
      n.x_coord.add(n.nodeX);  //generate random value for x and y positions of node
      n.nodeY = rand.nextInt(dtnrouting.height-2*n.getRadioRange()) + dtnrouting.y_start;
      n.y_coord.add(n.nodeY);
      n.positionTracker=0;
     
      
}

//******************************************************************************
//RANDOM PATH FOR A NODE

public void RandomMovement(Node n)
{

  speedController=rand.nextInt(100)+1;
  if(!n.name.substring(0,1).equals("S"))
     {
        if(n.speed>=speedController)
        {
        	
        	if (n.x_coord.get(n.positionTracker)< n.nodeX)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
                           n.nodeX--;

            else if(n.x_coord.get(n.positionTracker)>n.nodeX)
                            n.nodeX++;

       
        	if(n.y_coord.get(n.positionTracker)< n.nodeY)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
                           n.nodeY--;

            else if(n.y_coord.get(n.positionTracker)>n.nodeY)
                            n.nodeY++;
           
           // Choose random location for x-coordinate and y-coordinate
           if(n.x_coord.get(n.positionTracker)==n.nodeX & n.y_coord.get(n.positionTracker)==n.nodeY){
             n.x_coord.add(rand.nextInt(dtnrouting.width-2*n.getRadioRange()) + dtnrouting.x_start);  
             n.y_coord.add(rand.nextInt(dtnrouting.height-2*n.getRadioRange()) + dtnrouting.y_start);
             n.positionTracker=n.positionTracker+1;
             }
            
         }
       }
 }


//******************************************************************************
//FINDING OUT LOCATIONS WHICH A NODE VISITS WHILE FOLLOWING A PSEUDORANDOM PATH

public void InitializePsuedoPath(Node n)
{
 
	int loc=rand.nextInt(20)+5;
	for(int i=0;i< loc-1 ;i++) {
		  n.x_coord.add(rand.nextInt(dtnrouting.width-2*n.getRadioRange()) + dtnrouting.x_start);  
          n.y_coord.add(rand.nextInt(dtnrouting.height-2*n.getRadioRange()) + dtnrouting.y_start);
   }
}

//******************************************************************************
//MOVEMENT OF A NODE IN PSUDORANDOM PATH

public void PseudoRandomMovement(Node n)
{
	speedController=rand.nextInt(100)+1;
	  if(!n.name.substring(0,1).equals("S"))
	     {
	        if(n.speed>=speedController)
	        {
	        	
	        	if (n.x_coord.get(n.positionTracker)< n.nodeX)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
	                           n.nodeX--;

	            else if(n.x_coord.get(n.positionTracker)>n.nodeX)
	                            n.nodeX++;

	       
	        	if(n.y_coord.get(n.positionTracker)< n.nodeY)  //nextX show the new position of x coordinates its value is randomly generated above and x_n[i] is the random no genarated above
	                           n.nodeY--;

	            else if(n.y_coord.get(n.positionTracker)>n.nodeY)
	                            n.nodeY++;
	           
	           // Choose random location for x-coordinate and y-coordinate
	           if(n.x_coord.get(n.positionTracker)==n.nodeX & n.y_coord.get(n.positionTracker)==n.nodeY){
	               if(n.positionTracker==n.x_coord.size()) { //If reached end, traverse back
	        	   n.positionTracker=n.positionTracker-1;
	        	   n.direction=-1;}
	               
	               else if(n.positionTracker==0) { // If at 0 index, move forward
	            	   n.positionTracker=n.positionTracker+1;
		        	   n.direction=1;  
	               }
	               
	               else
	            	   n.positionTracker=n.positionTracker+n.direction; //follow the direction
	               
	             }
	            
	         }
	       }
 
}//End of method


//******************************************************************************

} //end of Program




