//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGE
import java.util.*;

//******************************************************************************
//START OF CLASS NODE
public class Node
{
    //Instance variables
	public static int ID_INCREMENTER;
    public ArrayList<Integer> x_coord = new ArrayList<Integer>();
    public ArrayList<Integer> y_coord = new ArrayList<Integer>();
   //probability of following a coordinate pair
    public ArrayList<Double>  prob_coord = new ArrayList<Double>();
    public int ID, x_inc=0,y_inc=0,nodeX,nodeY,positionTracker;
    // Used to follow part of a data set part
    public int startTracker,direction=1,reliability;
    public int queueSizeLeft, wholeQueueSize,radioRange,speed=0,nodeTracker=0;
    public String name; 
    public NodeMovement node_nm;
    private Random rand =new Random();
  
    //packets destined to this node and parameters
    public LinkedList <Packet> nodePackets =  new LinkedList<Packet>();
    public double msg_latency=0, msg_hops=0, msg_dl=0, msg_relibility=0; 
    
    //public HashMap <String, Integer> packetTimeSlots=new HashMap<String, Integer>();
    public HashMap <String, Integer> packetCopies=new HashMap<String, Integer>();
    public HashSet <String> packetIDHash=new HashSet<String>();
    public HashMap <Packet, Node> DestNPacket=new HashMap<Packet, Node>();
    public int num_packets, packets_ttl;
    
    public LinkedList<Integer> n1_neighborhood = new LinkedList<Integer>(); 
    public LinkedList<Integer> n2_neighborhood = new LinkedList<Integer>();
    public LinkedList<Double>  link_capacity = new LinkedList<Double>();
    public double time_slot=1.0, capacity=0;
    // When node is from Datasets
    public int nodePath[][],x_max,y_max;
//******************************************************************************
//EMPTY CONSTRUCTOR
public  Node() {
	node_nm = new NodeMovement();
	node_nm.InitialNodePositions(this);
	this.reliability = rand.nextInt(4)+1;
	if(this.reliability==5) {
		this.reliability =4;
	}
			
}

//******************************************************************************

public void refreshNodeSettings()
{
	
	packetIDHash.clear();     
    queueSizeLeft=this.wholeQueueSize;
	DestNPacket.clear();
	x_coord.clear();
	y_coord.clear();
	nodePackets.clear();
	prob_coord.clear();
	node_nm.InitialNodePositions(this);
	msg_latency= msg_hops = msg_dl= msg_relibility =0;
	time_slot=1.0;
	capacity=0;
	
}


//******************************************************************************

//ASSIGNING RADIO RANGE TO A NODE
public void setRadioRange(int radioRange)
{
    this.radioRange=(radioRange+1)*15;
}

//******************************************************************************

//RETURNING RADIO RANGE OF A NODE
public int getRadioRange()
{
    return this.radioRange;
}

}
