//******************************************************************************
//PACKAGE NAME

package MovementPattern;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import WktReaders.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

//******************************************************************************
//MAP-BASED MOVEMENT CLASS

public class MapBasedMovement extends dtnrouting
{
    
    //Instance Variables
    int h;
    static int speedController=0;
    TrainParser trainParserObj;
    PedestrianParser padParserObj;
    CarsParser carParserObj;
    FixedNodeParser fixedNodeParserObj;
    int value;
    Random random;
    boolean flag=true;

//******************************************************************************
//CONSTRUCTOR

public MapBasedMovement()
{    
          random=new Random();  
}

//******************************************************************************
//SETTING MOVEMENT PATH FOR EACH NODE ON THE MAP

@SuppressWarnings("static-access")
public void initializepath(Node n) throws FileNotFoundException, IOException
{
//When node is a fixed relay
if(n.name.charAt(0)=='D')
      {
         fixedNodeParserObj=new FixedNodeParser();
         fixedNodeParserObj.Parsing();
         int m=(CreateNode.FixedRelayArray.size()-1)%(fixedNodeParserObj.i+1);
         n.nextX=n.nodeX=fixedNodeParserObj.fixedNodeArray[m][0]-n.getRadioRange()/2;
         n.nextY=n.nodeY=fixedNodeParserObj.fixedNodeArray[m][1]-n.getRadioRange()/2;
     }

////When node is a regular train
else if (n.name.charAt(1) == 'T')
      {
         trainParserObj=new TrainParser();
         trainParserObj.Parsing();
         int m=(CreateNode.trainList.size()-1)%(TrainParser.i+1);
         int k=random.nextInt(20);
         n.nextX=n.nodeX=TrainParser.trainArray[m][k][0]-n.getRadioRange()/2;
         n.nextY=n.nodeY=TrainParser.trainArray[m][k][1]-n.getRadioRange()/2;
     }

//When node is a regular pedestrian
else if(n.name.charAt(1)=='P')
    {
         padParserObj=new PedestrianParser();
         padParserObj.Parsing();
         int m=(CreateNode.padList.size()-1)%PedestrianParser.i;
         n.nodeX=n.nextX=PedestrianParser.padArray[m][0][0]-n.getRadioRange()/2;
         n.nodeY=n.nextY=PedestrianParser.padArray[m][0][1]-n.getRadioRange()/2;
     }

//When node is a regular car
else if(n.name.charAt(1)=='C')
     {
         carParserObj=new CarsParser();
         carParserObj.Parsing();
         int m=(CreateNode.carList.size()-1)%CarsParser.i;
         n.nodeX=n.nextX=CarsParser.carArray[m][0][0]-n.getRadioRange()/2;
         n.nodeY=n.nextY=CarsParser.carArray[m][0][1]-n.getRadioRange()/2;
    }

//store the initial positions of node
n.setStartPositions();
}

//******************************************************************************
//EACH TYPE OF REGULAR NODE CALSS ITS MOVEMENT PATH METHOD

public void mapMovement() throws FileNotFoundException, IOException
{

    if(!CreateNode.trainList.isEmpty())
        trainMovement();
      if(!CreateNode.padList.isEmpty())
        pedestrianMovement();
      if(!CreateNode.carList.isEmpty())
        carMovement();
}

//******************************************************************************
//MOVEMENT PATH FOR TRAIN

public void trainMovement()
{
int m=CreateNode.trainList.size();
speedController=random.nextInt(100)+1;
for (int k=0;k<m;k++)
{
    //Access one node at a time from its array list
    Node n=new Node();
    n=CreateNode.trainList.get(k);
    if(n.speed>=speedController)
    {

        if(n.nextX<n.nodeX)
                        n.nodeX--;

        else if(n.nextX>n.nodeX)
                        n.nodeX++;

        if(n.nextY<n.nodeY)
                        n.nodeY --;

        else if(n.nextY>n.nodeY)
                        n.nodeY ++;

        if((n.nextX==n.nodeX)&&(n.nextY==n.nodeY))
        {
            n.positionTracker+=1; //increment position
            if(TrainParser.trainArray[k][n.positionTracker][0]==0 && TrainParser.trainArray[k][n.positionTracker][1]==0)
                n.positionTracker=0;
            n.nextX=TrainParser.trainArray[k][n.positionTracker][0]-n.getRadioRange()/2;
            n.nextY=TrainParser.trainArray[k][n.positionTracker][1]-n.getRadioRange()/2;
        }
    }
}//End of for loop
}//End of method

//******************************************************************************
//MOVEMENT PATH FOR PEDESTRIANS

public void pedestrianMovement()
{
int m=CreateNode.padList.size();
speedController=random.nextInt(100)+1;

for (int k=0;k<m;k++)
{
    //Access one node at a time from its array list
    Node n=new Node();
    n=CreateNode.padList.get(k);
    if(n.speed>=speedController)
    {

    if(n.nextX<n.nodeX)
                    n.nodeX--;

    else if(n.nextX>n.nodeX)
                    n.nodeX++;

    if(n.nextY<n.nodeY)
                    n.nodeY --;

    else if(n.nextY>n.nodeY)
                    n.nodeY ++;

    if((n.nextX==n.nodeX)&&(n.nextY==n.nodeY))
        {
            n.positionTracker+=1; //increment position
            if(PedestrianParser.padArray[k][n.positionTracker][0]==0 && PedestrianParser.padArray[k][n.positionTracker][1]==0)
                n.positionTracker=0;
            n.nextX=PedestrianParser.padArray[k][n.positionTracker][0]-n.getRadioRange()/2;
            n.nextY=PedestrianParser.padArray[k][n.positionTracker][1]-n.getRadioRange()/2;
        }
    }
}//End of for loop
}//End of method

//******************************************************************************
//MOVEMENT PATH FOR CAR

public void carMovement()
{
int m=CreateNode.carList.size();
speedController=random.nextInt(100)+1;

for (int k=0;k<m;k++)
{
    //Access one node at a time from its array list
    Node n=new Node();
    n=CreateNode.carList.get(k);
    if(n.speed>=speedController)
    {
        if(n.nextX<n.nodeX)
                        n.nodeX--;

        else if(n.nextX>n.nodeX)
                        n.nodeX++;

        if(n.nextY<n.nodeY)
                        n.nodeY --;

        else if(n.nextY>n.nodeY)
                        n.nodeY ++;

        if((n.nextX==n.nodeX)&&(n.nextY==n.nodeY))
            {
                n.positionTracker+=1; //increment position
                if(CarsParser.carArray[k][n.positionTracker][0]==0 && CarsParser.carArray[k][n.positionTracker][1]==0)
                    n.positionTracker=0;
                n.nextX=CarsParser.carArray[k][n.positionTracker][0]-n.getRadioRange()/2;
                n.nextY=CarsParser.carArray[k][n.positionTracker][1]-n.getRadioRange()/2;
            }
    }
}//End of for loop
}//end of method

//******************************************************************************

}//End of class
