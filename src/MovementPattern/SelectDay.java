
//******************************************************************************
//PACKAGE
package MovementPattern;

//******************************************************************************
//IMPORT FILES
import DTNRouting.UpdateInformation;
import DTNRouting.dtnrouting;
import java.util.Random;

//******************************************************************************
//SELECT ONE OF THE DAY IN 7 DAYS
public class SelectDay
{
//Instance Variables
static int day[];
int start;
int rows;//number of rows
int nodes;//Number of nodes
Random rand;
double pow2,r;

//******************************************************************************
//CONSTRUCTOR
public SelectDay(){}

//******************************************************************************
public SelectDay(int nodes)
{
     while(pow2<nodes)
        {
            pow2=Math.pow(2, r);
            r=r+1;
        }
    this.rows=7*(int)pow2*4;//locations visited on each day
    this.nodes=nodes;
    //Initialize path arrays
    for(int i=0;i<nodes;i++){
    dtnrouting.nodeArray.get(i).nodePath=new int[rows][2];
    //probability of following a coordinate pair
    dtnrouting.nodeArray.get(i).pointProbability=new double[rows];
    }
    
}
//******************************************************************************

public void selectDays()
{
    day=new int[UpdateInformation.Sim];
    rand=new Random(); //For generatig path
    for(int i=0;i<UpdateInformation.Sim;i++)
    {
        day[i] = rand.nextInt(7);
    }
}
//******************************************************************************
//PUT PATH OF THE SELECTED DAY IN NODEPATH VARIABLE OF NODE
public void generatePath(int d)
{

    int curDay=day[d],dayrows=(int)pow2*4,inc=-1;
    System.out.println("Day selected:"+day[d]);

    for(int j=0;j<nodes;j++)//Every source Node
    {
        for(int l=0;l<7;l++)
        {
            curDay=day[d]+l;
            if(curDay>=7)
                curDay=curDay-7;
            start=curDay*dayrows;

         for(int k=0;k<dayrows;k++)//Destinaiton
            {
             inc=inc+1;
             dtnrouting.nodeArray.get(j).nodePath[inc][0]=dtnrouting.nodeArray.get(j).nodeDSPath[start+k][0];
             dtnrouting.nodeArray.get(j).nodePath[inc][1]=dtnrouting.nodeArray.get(j).nodeDSPath[start+k][1];
             dtnrouting.nodeArray.get(j).pointProbability[k]=DSPath.pathDetail[j][start+k][4];
             //System.out.println("Node:"+(j+1)+">"+dtnrouting.nodeArray.get(j).nodeDSPath[start+k][0]+","+dtnrouting.nodeArray.get(j).nodeDSPath[start+k][1]);
            }
        }
        inc=-1;
    }
    }
//******************************************************************************
//Day of a specific simulation run
public static int getDay(int sim)
{
    return day[sim];
}
}
