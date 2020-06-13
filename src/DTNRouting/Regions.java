//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES AND CLASSES
import java.util.ArrayList;
import java.util.Random;

//******************************************************************************
// START OF CLASS REGIONS

public class Regions
{
// INSTANCE VARIABLES
    Random rand;
    public int x,y,w,h,locations[][],numNodes=0,count=0;
    public static int RegionID;
    public String RegionName,networkType="Mobile";
    ArrayList <Node> networkNodes=new ArrayList();

//******************************************************************************
//EMPTY CONSTRUCTOR

public Regions(){}

//******************************************************************************
//CONSTRUCTOR WITH FOUR ARGUMENTS FOR SETTING DIMENSIONS OF REGION

public Regions(int x, int y, int w, int h)
{
    this.x=x;   this.y=y;   this.w=w;   this.h=h;
    RegionID++; RegionName="Region"+RegionID;   dtnrouting.RegionArray.add(this);  
}

//******************************************************************************
//GET MID POINT OF WIDTH

public int getMidX()
{     return(x+w/2);     }

//******************************************************************************

//GET MID POINT OF HEIGHT
public int getMidY()
{    return(y+h/2);     }

//******************************************************************************

//RETURN NAME OF NETWORK TYPE, STATIC OR MOBILE
public String getNetworkType()
{     return networkType; }

//******************************************************************************

//SETTING REGION TO A SPECIFIC NETWORK TYPE, STATIC OR MOBILE
public void setNetworkType(String networkType)
{   this.networkType=networkType;     }

//******************************************************************************

//ADDING NODES TO A SPECIFIC REGION
public void setNetworkNodes(Node node)
{
    if(networkNodes.isEmpty()==true)
        dtnrouting.RegionwithNodes.add(this);
    networkNodes.add(node);
}

//******************************************************************************

//RETURNING NODES OF A SPECIFIC REGION
public ArrayList<Node> getNetworkNodes()
{
    return networkNodes;
}
//******************************************************************************

//Find out hot spots in the region
public void hotspots()
{

    //When a non-real life psedorandom pattern is selected

        rand=new Random();
        int loc=rand.nextInt(20)+5;
        //Number of Nodes
        numNodes=networkNodes.size();
        locations=new int[loc][2];
        for(int p=0;p<loc;p++)
        {
         locations[p][0]=rand.nextInt(w-networkNodes.get(0).getRadioRange())+x;
         locations[p][1]=rand.nextInt(h-networkNodes.get(0).getRadioRange())+y;
        }
}
//******************************************************************************



}//END OF THE CLASS REGION