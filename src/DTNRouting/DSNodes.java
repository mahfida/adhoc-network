//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import MovementPattern.*;
import java.util.Random;
import MovementPattern.DSPath;


//*****************************************************************************
// START OF CREATENODE CLASS
public class DSNodes extends dtnrouting  implements ItemListener, ActionListener, TextListener
{
//Instance variables
    public Random randObj=new Random();
    public JFrame jf=new JFrame("Data Set Nodes");
    public Label nodeS=new Label("Number of Nodes");
    public Label dsnodeS=new Label("");
    public Label lspeed=new Label("Speed(m/s)");
    public Choice cspeed=new Choice();
    public Label radiorange=new Label("Radio Range(m)");
    public Choice cradiorange=new Choice();
    public Label queuesize=new Label("QueueSize(MB)");
    public Choice cqueuesize=new Choice();
    public Button ok=new Button("OK");
    public Button Close=new Button("Close");
    public static SelectDay sday;
    public static DSPath dsObj;
   
//In order to pass values to the object of CreateNode`
    public int speedofnode, radiorangeofnode;
    
//other variables
   
    public NodeMovement nm;
    private Regions nodeRegion;


//******************************************************************************
//CONSTRUCTOR

public DSNodes()
{
   //All the nodes in a dataset belongs to a single region
   nodeRegion = new Regions(dtnrouting.x_start, dtnrouting.y_start, dtnrouting.width, dtnrouting.height);
   nm=new NodeMovement();
   jf.setLayout(new GridLayout(5,2,5,5));

   //Add speeds to the Speed choice box
   for(int speed=0;speed<5;speed++)
        cspeed.add((speed+1)*10+""); // setting speed of the mobile node

   //Add radio ranges and queue sizes to their respective choice boxes
   for(int l=1;l<6;l++)
    {
        cradiorange.add(l+"");//add Radio Range indices
        cqueuesize.add(l*10+"");
    }
    // Number of nodes in data set
    if(Setting.dataset.equals("St.Andrew Uni")) dsnodeS.setText("25");
    else if(Setting.dataset.equals("iMote Traces")) dsnodeS.setText("36");
    else if(Setting.dataset.equals("R. Mining")) dsnodeS.setText("25");

    //Add Components to the frame
    jf.add(nodeS);              jf.add(dsnodeS);
    jf.add(queuesize);          jf.add(cqueuesize);
    jf.add(lspeed);             jf.add(cspeed);
    jf.add(radiorange);         jf.add(cradiorange);
    jf.add(ok);                 jf.add(Close);

//registering events
   
    ok.addActionListener(this);
    Close.addActionListener(this);
   

//Frame Perimeters
    jf.setSize(new Dimension(200,200));
    jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    jf.setVisible(true);
    jf.setResizable(false);


}

//******************************************************************************

public void itemStateChanged(ItemEvent e)
{
        Object b=e.getSource();
        
}

//******************************************************************************

public void actionPerformed(ActionEvent e)
{

 String action;
 action=e.getActionCommand();

 if(action.equals("Close")) { jf.dispose();}

 //Set perimeters of new node
 else if (action.equals("OK"))
 {
 
               int i=Integer.parseInt(dsnodeS.getText());

                for (int l=0;l<i;l++)
                {
                    Node node=new Node();
                    Node.ID+=1;
                    node.nodeID=Node.ID;
                    Node.rid=Node.rid+1;
                    node.name="R"+Node.rid;
                    node.speed=Integer.parseInt(cspeed.getSelectedItem())+50;
                    dtnrouting.nodeArray.add(node);
                    node.setRadioRange(Integer.parseInt(cradiorange.getSelectedItem()));
                    node.wholeQueueSize=node.queueSizeLeft=Integer.parseInt(cqueuesize.getSelectedItem());
                    dtnrouting.allNodes.add(node);
                    node.nodeRegion=nodeRegion; //Assign the region object to the region of node
                    //Add the node in the array List of region's network
                    nodeRegion.setNetworkNodes(node);

                }
                nodeRegion.numNodes=i;
               
                 if(Setting.dataset.equals("St.Andrew Uni"))  
                 {
                     /* RetriveDataFile rdf=new RetriveDataFile("D:\\4QOldSA.txt",25);
                       try {
                            rdf.retrivePath();
                        } catch (IOException ex) {
                            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
                        }  */
                     
                    dsObj=new DSPath("..\\Datasets\\SA6.txt",25);
                    sday=new SelectDay(25);

                    try { 
                        
                        dsObj.retrivePath();
                        sday.selectDays();
                        sday.generatePath(UpdateInformation.Sim-1);
                        }
                    catch (IOException ex) { Logger.getLogger(DSNodes.class.getName()).log(Level.SEVERE, null, ex); }
               
                      }

                 else if(Setting.dataset.equals("iMote Traces"))
                 {
                     ////////////////////////////////////////////////////////////
                     /*   RetriveDataFile rdf=new RetriveDataFile("D:\\76ND.txt",36);
                       try {
                            rdf.retrivePath();
                        } catch (IOException ex) {
                            Logger.getLogger(Setting.class.getName()).log(Level.SEVERE, null, ex);
                        }  * */
                      ////////////////////////////////////////////////////////////////////
                    dsObj=new DSPath("..\\Datasets\\ITC6.txt",36);
                    sday=new SelectDay(36);

                    try {
                        dsObj.retrivePath();
                        sday.selectDays();
                        sday.generatePath(UpdateInformation.Sim-1);
                    }
                    catch (IOException ex) { Logger.getLogger(DSNodes.class.getName()).log(Level.SEVERE, null, ex);}
                 

                 }
                 else if(Setting.dataset.equals("R. Mining"));
                 
                 //Setting Initial Position
                 nm.InitialDSPath();
                // System.out.println(dtnrouting.nodeArray.get(0).nodeRegion.w+" "+dtnrouting.nodeArray.get(0).nodeRegion.x);
               //  System.out.println(dtnrouting.nodeArray.get(0).nodeRegion.h+" "+dtnrouting.nodeArray.get(0).nodeRegion.y);
                 dtnrouting.endNodes.setEnabled(true);
   }

    ResetPerimeters();
  
}

//******************************************************************************

//RESET PERIMETERS OF CREATE NODE CLASS
public void ResetPerimeters() // reset perimeters of a node
{
     cradiorange.select(0);                 cspeed.select(0);
     cqueuesize.select(0);
     Setting settings=new Setting();        settings.setSettingsFrameEnable(false);
   

}

    public void textValueChanged(TextEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



//******************************************************************************

} //END OF CREATENODE CLASS