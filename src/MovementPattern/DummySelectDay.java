
//******************************************************************************
//PACKAGE
package MovementPattern;

//******************************************************************************
//IMPORT FILES
import DTNRouting.dtnrouting;
import java.util.Random;

//******************************************************************************
//SELECT ONE OF THE DAY IN 7 DAYS
public class DummySelectDay
{
//Instance Variables
static int day=0;
int start;
int rows;//number of rows
int nodes;//Number of nodes
Random rand;
double pow2,r;

//******************************************************************************
//CONSTRUCTOR
public DummySelectDay(){}

//******************************************************************************
public DummySelectDay(int nodes)
{
     while(pow2<nodes)
        {
            pow2=Math.pow(2, r);
            r=r+1;
        }
    this.rows=(int)pow2*4;
    this.nodes=nodes;
    //Initialize path arrays
    for(int i=0;i<nodes;i++){
    dtnrouting.nodeArray.get(i).nodePath=new int[rows][2];
    //probability of following a coordinate pair
    dtnrouting.nodeArray.get(i).pointProbability=new double[rows];
    }

}

//******************************************************************************
//PUT PATH OF THE SELECTED DAY IN NODEPATH VARIABLE OF NODE
public void generatePath()
{
    rand=new Random(); //For generatig path
    day=rand.nextInt(7);
    start=day*rows;
    System.out.println("Day selected:"+day);
    for(int j=0;j<nodes;j++)//Every source Node
        for(int k=0;k<rows;k++)//Destinaiton
        {
         dtnrouting.nodeArray.get(j).nodePath[k][0]=dtnrouting.nodeArray.get(j).nodeDSPath[start+k][0];
         dtnrouting.nodeArray.get(j).nodePath[k][1]=dtnrouting.nodeArray.get(j).nodeDSPath[start+k][1];
         dtnrouting.nodeArray.get(j).pointProbability[k]=DSPath.pathDetail[j][start+k][4];
         //System.out.println("Node:"+(j+1)+">"+dtnrouting.nodeArray.get(j).nodeDSPath[start+k][0]+","+dtnrouting.nodeArray.get(j).nodeDSPath[start+k][1]);
        }
}
//******************************************************************************

public static int getDay()
{
    return day;
}
}
