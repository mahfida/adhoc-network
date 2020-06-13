//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import MovementPattern.NodeMovement;
import java.util.ArrayList;

//------------------------------------------------------------------------------

//START OF THE CLASS FIXED RELAY DEPLOYMENT
public class FixedRelayDeployment extends dtnrouting
{
    //Instance variables
    int num,w,h,j,k,GridArray[][]=new int[20][20],tx,ty,tw,th;
    NodeMovement nm;

//******************************************************************************

//EMPTY CONSTRUCTOR
FixedRelayDeployment(){}

//******************************************************************************

//RANDOMLY DEPLOYING FIXED RELAYS IN A REGION
public void RandomDeployement(ArrayList<Node> gFixedRelay)
{
    nm=new NodeMovement();
    for(int l=0;l<gFixedRelay.size();l++)
    {
       Node FixedRelay=gFixedRelay.get(l);
       nm.InitialNodePositions(FixedRelay);
    }
}

//******************************************************************************

//DEPLOYING FIXED RELAYS ON A MAP
public void MapBasedDeployment(ArrayList<Node> gFixedRelay)
{
}

//******************************************************************************

//DEPLOYING FIXED RELAYS IN GRID-FORM IN A REGION
public void Grid_BasedDeployement(Regions tRegion)
{
    //Dummy array for grid-based fixed relays
    ArrayList <Node> gFixedRelay=new ArrayList();

    for(int j=0;j<dtnrouting.FixedRelayArray.size();j++)
    {
        Node node=dtnrouting.FixedRelayArray.get(j);
        if(node.nodeRegion==tRegion)
            gFixedRelay.add(node);
    }

    switch(gFixedRelay.size())
    {
    case 1:
                 w=tRegion.w/1;          h=tRegion.h/1;
                 j=1;                           k=1;
                 break;
    case 2:
                 w=tRegion.w/2;          h=tRegion.h/1;
                 j=2;                           k=1;
                 break;
    case 4:
                 w=tRegion.w/2;          h=tRegion.h/2;
                 j=2;                           k=2;
                 break;
    case 6:
                 w=tRegion.w/3;          h=tRegion.h/2;
                 j=3;                           k=2;
                 break;
    case 8:
                 w=tRegion.w/4;          h=tRegion.h/2;
                 j=4;                           k=2;
                 break;
    case 10:
                 w=tRegion.w/5;          h=tRegion.h/2;
                 j=5;                           k=2;
                 break;
    case 12:
                 w=tRegion.w/4;          h=tRegion.h/3;
                 j=4;                           k=3;
                 break;
     }//End of switch statement
    int tbNumber=0;

    //Assingment of grid-based fixed relays positions in the form of Grid
    for(int a=0;a<k;a++)
      for(int b=0;b<j;b++)
      {
           tx=tRegion.x+b*w;    ty=tRegion.y+a*h;   tw=w;   th=h;
           Node tbox=gFixedRelay.get(tbNumber);
           tbox.nodeX=tx+tw/2-(int)tbox.getRadioRange();  tbox.nodeY=ty+th/2-(int)tbox.getRadioRange();
           tbox.setStartPositions();                 ++tbNumber;
       }
} //end of method

//******************************************************************************

}//END OF CLASS FIXEDRELAYDEPLOYEMENT
