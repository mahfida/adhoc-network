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
    public int ID, x_inc=0,y_inc=0,nodeX,nodeY,positionTracker,direction=1;
    public int queueSizeLeft, wholeQueueSize,radioRange,speed=0,nodeTracker=0;
    public String name; 
    public NodeMovement node_nm;
    
    //packet related properties of a node
    public HashMap <String, Integer> packetTimeSlots=new HashMap<String, Integer>();
    public HashMap <String, Integer> packetCopies=new HashMap<String, Integer>();
    public HashSet <String> packetIDHash=new HashSet<String>();
    public HashMap <Packet, Node> DestNPacket=new HashMap<Packet, Node>();
    
//******************************************************************************
//EMPTY CONSTRUCTOR
public  Node() {
	node_nm = new NodeMovement();
	node_nm.InitialNodePositions(this);
}

//******************************************************************************

//UPDATING TIME STAMP VALUES OF packetS INSIDE A NODE
public void updatepacketTimestamp(Node node)
{
        for (Iterator<Map.Entry<String,Integer>> i = node.packetTimeSlots.entrySet().iterator(); i.hasNext(); )
        {
           Map.Entry<String,Integer> entry = i.next();
           String packetName = entry.getKey();
           Integer TS = entry.getValue();
           TS=TS+1;
           //Time spent by a packet inside a nodes
           node.packetTimeSlots.put(packetName, TS);
        }
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
