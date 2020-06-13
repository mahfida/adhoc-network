//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGE
import java.util.*;

//******************************************************************************
//START OF CLASS NODE
public class Node
{
    //Instance variables
    public static int ID,rid,tid,fid,trainID,padID,carID;
    public int posX,posY,nodeX,nodeY,nextX, nextY,nodeID,trainNo,padNo,carNo,nodeDSPath[][],nodePath[][],x_inc=0,y_inc=0,x_max,y_max;
    public int queueSizeLeft, wholeQueueSize,radioRange,speed=0,nodeTracker=0,positionTracker=0;
    public double pointProbability[];
    public String name;

    //For pigeon
    public boolean isNewPosition=false,takeNewPosition=true;
    public  Regions nodeRegion,visitingRegion;

    //Bundle related properties of a node
    public HashMap <String, Integer> bundleTimeSlots=new HashMap();
    public HashMap <String,Integer> bundleCopies=new HashMap();
    public HashSet <String> bundleIDHash=new HashSet();
    public HashMap <Bundle,Node> DestNBundle=new HashMap();
    
//******************************************************************************
//EMPTY CONSTRUCTOR
public  Node() {}

//******************************************************************************

//UPDATING TIME STAMP VALUES OF BUNDLES INSIDE A NODE
public void updateBundleTimestamp(Node node)
{
        for (Iterator<Map.Entry<String,Integer>> i = node.bundleTimeSlots.entrySet().iterator(); i.hasNext(); )
        {
           Map.Entry<String,Integer> entry = i.next();
           String bundleName = entry.getKey();
           Integer TS = entry.getValue();
           TS=TS+1;
           //Time spent by a bundle inside a nodes
           node.bundleTimeSlots.put(bundleName, TS);
        }
}

//******************************************************************************

//ASSIGNING AN INITIAL POSITION TO A NODE
public void setStartPositions()
{
    //posX and posY stores initial positions of a node
    posX=nodeX;
    posY=nodeY;
    x_inc=y_inc=0;
    if(nodePath!=null)
    x_max=y_max=nodePath.length;
}

//******************************************************************************

//RETURN INITIAL POSITION OF A NODE
public void getStartPositions()
{
    //NodeX and NodeY denotes current while nextX and nextY denotes next
    //position of a node
    nodeX=nextX=posX;
    nodeY=nextY=posY;
    x_inc=y_inc=0;
            if(nodePath!=null)
    x_max=y_max=nodePath.length;
    //Tracking presence of a node especially of a relay node in different region
    nodeTracker=-1;
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

//******************************************************************************
//PSEUDORANDAM PATH GENERATION FOR A NODE
public void setPath()
{

       //When a real data set movemnent pattern is selected
   
        Random rand=new Random();
        int l=this.nodeRegion.locations.length;
        x_max=y_max=10;
        nodePath=new int[10][2];
        int path[][]=new int[10][2];
        path=this.nodeRegion.locations;
        for(int i=0;i<10;i++)
        {
                int loc=rand.nextInt(l);
                nodePath[i][0] = path[loc][0];
                nodePath[i][1] = path[loc][1];
        }
    
}
}//END OF NODE CLASS
