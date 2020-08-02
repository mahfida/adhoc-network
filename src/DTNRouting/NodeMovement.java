
//******************************************************************************
//PACKAGE

package DTNRouting;

//import java.util.ArrayList;
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
	if(dtnrouting.movementtype.equals("Random") || n.name.substring(0,1).equals("S")){
	  n.x_coord.clear();
	  n.y_coord.clear();
	  n.nodeX =rand.nextInt(n.xwidth) + n.topleft_xcorner; 
	  //rand.nextInt(dtnrouting.width-n.getRadioRange()) + dtnrouting.x_start;
      n.x_coord.add(n.nodeX);  //generate random value for x and y positions of node
      n.nodeY =rand.nextInt(n.yheight) + n.topleft_ycorner; 
      //rand.nextInt(dtnrouting.height-n.getRadioRange()) + dtnrouting.y_start;
      n.y_coord.add(n.nodeY);
      n.prob_coord.add(rand.nextDouble());
      n.positionTracker=0;}
	// For pseudo-random and the dataset
	 else if(dtnrouting.movementtype.equals("Pseudorandom") & !n.name.substring(0,1).equals("S"))
      { 
		n.positionTracker=0;
		//System.out.println(n.name);
		InitializePsuedoPath(n);
        n.nodeX=n.x_coord.get(n.positionTracker); 
    	n.nodeY=n.y_coord.get(n.positionTracker);
      }
	  // For pseudo-random and the dataset
	  else if(dtnrouting.movementtype.equals("Dataset") & !n.name.substring(0,1).equals("S"))
	  {     
	        n.nodeX=n.x_coord.get(n.startTracker); 
	    	n.nodeY=n.y_coord.get(n.startTracker);
	  }
      
	n.moveFlag=true;
}

//******************************************************************************
//RANDOM PATH FOR A NODE

public void RandomMovement(Node n)
{
	
 
  speedController=rand.nextInt(100)+1;
  if(n.moveFlag==false) {
	  n.counter = n.counter+1; 
	  if(n.counter==1000) 
	  { //to stay at a place at least 20 time units before calculating prob.
	  n.moveFlag = (rand.nextDouble() > n.prob_coord.get(n.positionTracker)) ? true : false;
      //System.out.println(n.counter+": "+n.prob_coord.get(n.positionTracker)+" :"+n.moveFlag);
	  n.counter=0;
	  }}
  
  // decide whether to change the position
  if(!n.name.substring(0,1).equals("S") & n.moveFlag)
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
           // 20 different positions	
           if(n.x_coord.get(n.positionTracker)==n.nodeX & n.y_coord.get(n.positionTracker)==n.nodeY){
        	 
        	 if(n.positionTracker==19)
        		 	n.positionTracker =0;
        	 else {
		             n.x_coord.add(rand.nextInt(n.xwidth) + n.topleft_xcorner);  
		             n.y_coord.add(rand.nextInt(n.yheight) + n.topleft_ycorner);
		             n.positionTracker=n.positionTracker+1;
		             n.prob_coord.add(rand.nextDouble());
             }
        	 
        	  
        		  n.moveFlag = (rand.nextDouble() > n.prob_coord.get(n.positionTracker)) ? true : false;
        	 
           }
            
         }
       }
 }


//******************************************************************************
//FINDING OUT LOCATIONS WHICH A NODE VISITS WHILE FOLLOWING A PSEUDORANDOM PATH

public void InitializePsuedoPath(Node n)
{
 
	int loc=rand.nextInt(10)+3;
	for(int i=0;i< loc-1 ;i++) {
		  n.x_coord.add(rand.nextInt(n.xwidth) + n.topleft_xcorner);  
          n.y_coord.add(rand.nextInt(n.yheight) + n.topleft_ycorner);
          n.prob_coord.add(rand.nextDouble()); // probability of staying at this location 
   }
//	System.out.println(n.x_coord);
}

public void InitisalizeDatasetPath(Node n)
{

 n.startTracker=dtnrouting.dataset_simulation_index;

	    
}

//******************************************************************************
//MOVEMENT OF A NODE IN PSUDORANDOM PATH

public void Follow_PseudoRandomPath(Node n)
{
	speedController=rand.nextInt(100)+1;
	 
	  if(n.moveFlag==false) {
		  n.counter = n.counter+1; 
		  if(n.counter==1000) 
		  { //to stay at a place at least 20 time units before calculating prob.
		  n.moveFlag = (rand.nextDouble() > n.prob_coord.get(n.positionTracker)) ? true : false;
	      //System.out.println(n.counter+": "+n.prob_coord.get(n.positionTracker)+" :"+n.moveFlag);
		  n.counter=0;
		  }}
	  
	  if(!n.name.substring(0,1).equals("S") & n.moveFlag)
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
	               if((n.positionTracker+1)==n.x_coord.size())  //If reached end, traverse back
	            	   n.positionTracker=0;
	               else 
	            	   n.positionTracker=n.positionTracker+1;
	               n.moveFlag = (rand.nextDouble() > n.prob_coord.get(n.positionTracker)) ? true : false;}
	        }}
}//End of method

//******************************************************************************
public void Follow_DatasetPath(Node n)
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
	           
	           // Choose next location for x-coordinate and y-coordinate
		       
		           if(n.x_coord.get(n.positionTracker)==n.nodeX & n.y_coord.get(n.positionTracker)==n.nodeY){
		               if((n.positionTracker+1)==n.x_coord.size())  //If reached end, traverse back
		        	   n.positionTracker=0;
		               
		               else 
		            	   n.positionTracker=n.positionTracker+1;}
		        }}
	        
 
}



} //end of Program




