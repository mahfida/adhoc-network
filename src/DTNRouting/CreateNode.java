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
import javax.swing.*;
import MovementPattern.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

//*****************************************************************************
// START OF CREATENODE CLASS
public class CreateNode extends dtnrouting  implements ItemListener, ActionListener, TextListener
{
//Instance variables
    public Random randObj=new Random();
    public JFrame jf=new JFrame("Create Node");
    public Label nodeType=new Label("Node Type");
    public Label nodeS=new Label("Number of Nodes");
    public Label nodesubcategory=new Label("Sub category");
    public Choice subcategory=new Choice();
    public TextField tnodeS=new TextField("0");
    public Label lspeed=new Label("Speed(m/s)");
    public Choice cspeed=new Choice();
    public Label radiorange=new Label("Radio Range(m)");
    public Choice cradiorange=new Choice();
    public Label queuesize=new Label("QueueSize(MB)");
    public Choice cqueuesize=new Choice();
    public Label lregions=new Label("Select Region");
    public Choice cregions=new Choice();
    public Button Add=new Button("Add");
    public Button Close=new Button("Close");
    public Choice cnType=new Choice();
    
//In order to pass values to the object of CreateNode`
    public int numberofnodes, speedofnode, radiorangeofnode;

//other variables
    public String nameofnode;
    public NodeMovement nm;
    MapBasedMovement mapbasedMovement;
    private Regions nodeRegion;
    private  ArrayList<Node> currentFixedRelays=new ArrayList();
    private  ArrayList<Node> currentFerries=new ArrayList();
    public static ArrayList<Node> trainList=new ArrayList();
    public static ArrayList<Node> carList=new ArrayList();
    public static ArrayList<Node> padList=new ArrayList();
    FixedRelayDeployment tbd;

//check box for node Mobilty
    Label nodeMobilityLabel=new Label("Node Dynamics");
    CheckboxGroup cbg=new CheckboxGroup();
    Checkbox mobileNodeCB=new Checkbox("Mobile",cbg,false);
    Checkbox staticNodeCB=new Checkbox("Static",cbg, true);
    Panel mobilityPanel=new Panel();
    CardLayout cardLO;

//******************************************************************************

//CONSTRUCTOR
public CreateNode()
{
    /*
   if(Setting.numRegions==1)
   {
       Setting.numRegions=1;
       nodeRegion = new Regions(dtnrouting.x_start, dtnrouting.y_start, dtnrouting.width, dtnrouting.height);
    }
   */
//Add region names to the Region Choice box
   for(int j=0;j<RegionArray.size();j++)
   {
       Regions regionObj=new Regions();
       regionObj=RegionArray.get(j);
       cregions.add(regionObj.RegionName);
   }
   nm=new NodeMovement();

   jf.setLayout(new GridLayout(9,2,5,5));

   //Add speeds to the Speed choice box
   for(int speed=0;speed<10;speed++)
        cspeed.add((speed+1)*10+""); // setting speed of the mobile node

   //Add radio ranges and queue sizes to their respective choice boxes
   for(int l=1;l<6;l++)
    {
        cradiorange.add(l+"");//add Radio Range indices
        cqueuesize.add(l*10+"");
    }
            
    cnType.add("Regular");
    //Types of nodes in a map
    subcategory.add("Trains"); subcategory.add("Padestrians"); subcategory.add("Cars");
         
    //Components in Frame window
    lspeed.setEnabled(false);   cspeed.setEnabled(false);
    if(Setting.enviroment.equals("Non-Real Life"))
    {
        nodesubcategory.setEnabled(false);
        subcategory.setEnabled(false);
    }
    else
    {
        nodesubcategory.setEnabled(true);
        subcategory.setEnabled(true);
    }

    jf.add(nodeType);           jf.add(cnType);
    jf.add(nodesubcategory);    jf.add(subcategory);
    jf.add(nodeS);              jf.add(tnodeS);
    jf.add(queuesize);          jf.add(cqueuesize);
    jf.add(nodeMobilityLabel);  mobilityPanel.add(staticNodeCB);   mobilityPanel.add(mobileNodeCB); jf.add(mobilityPanel);
    jf.add(lspeed);             jf.add(cspeed);
    jf.add(radiorange);         jf.add(cradiorange);
    jf.add(lregions);           jf.add(cregions);
    jf.add(Add);                jf.add(Close);

//registering events
    subcategory.addItemListener(this);
    cnType.addItemListener(this);
    staticNodeCB.addItemListener(this);
    mobileNodeCB.addItemListener(this);
    Add.addActionListener(this);
    Close.addActionListener(this);
    tnodeS.addTextListener((TextListener) this);

//Frame Perimeters
    jf.setSize(new Dimension(300,300));
    jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    jf.setVisible(true);
    jf.setResizable(false);

    mapbasedMovement=new MapBasedMovement();

}

//******************************************************************************

public void itemStateChanged(ItemEvent e)
{
        Object b=e.getSource();
        //A regular node can be an end device, it can be either held by a
        //a pedestrian or installed by a car or tram
        if(cnType.getSelectedItem().equals("Regular"))
        {
            nodeS.setEnabled(true);                tnodeS.setEnabled(true);
            lregions.setEnabled(true);             cregions.setEnabled(true);
            nodesubcategory.setEnabled(true);      subcategory.setEnabled(true);
            if(DeviceBasedSettings.fDeploymentType.equals("Mobile"))
            {
                mobileNodeCB.setState(true);
                lspeed.setEnabled(true);    cspeed.setEnabled(true);
            }
            else if(DeviceBasedSettings.fDeploymentType.equals("Static"))
                mobileNodeCB.setState(false);

            }
        //In case node is fixed relay, it should not move
        else if (cnType.getSelectedItem().equals("FixedRelay") )
        {
            if(DeviceBasedSettings.tDeploymentType.equals("Grid"))
            {
                int n=dtnrouting.RegionArray.size();
                JOptionPane.showMessageDialog(jf,"Caution! Assign upto "+(12/n)+" Fixed Relays to each region\n As the simulator allows only 12 relays per simulation" );
            }
            tnodeS.setText("0");
            subcategory.setEnabled(false);
            lspeed.setEnabled(false);
            cspeed.setEnabled(false);
        }
        //If node is a ferry relay device then it must be mobile
        else if(cnType.getSelectedItem().equals("Ferry"))
        {
            lspeed.setEnabled(true);
            cspeed.setEnabled(true);
            staticNodeCB.setState(false);
            if( DeviceBasedSettings.ferryType.equals("Single-Ferry") || DeviceBasedSettings.ferryType.equals("Pigeon" ))
            {
                nodeS.setEnabled(false);                tnodeS.setEnabled(false);
                lregions.setEnabled(false);             cregions.setEnabled(false);
            }

           if (DeviceBasedSettings.ferryType.equals("Multi-Ferry") && Setting.enviroment.equals("Non-Real Life"))
            {
                if(Setting.numRegions==1)
                {
                     nodeS.setEnabled(true);                tnodeS.setEnabled(true);
                     lregions.setEnabled(true);             cregions.setEnabled(true);
                }
                else
                {
                     nodeS.setEnabled(false);                tnodeS.setEnabled(false);
                     lregions.setEnabled(false);             cregions.setEnabled(false);
                }
            }
          }

       //Peshwar and Islamabad has no train services
        if(Setting.mapCity.equals("Peshawar")||Setting.mapCity.equals("Islamabad"))
        {
             subcategory.remove("Trains");
        }

        //Setting child panel according to the node dynamics selected
        if(mobileNodeCB.getState()==true)
        {
            lspeed.setEnabled(true);
            cspeed.setEnabled(true);
            if(cnType.getSelectedItem().equals("FixedRelay"))
            {
                  JOptionPane.showMessageDialog(jf,"Fixed relays cannot move");
                  mobileNodeCB.setState(false);
                  staticNodeCB.setState(true);
                  lspeed.setEnabled(false);
                  cspeed.setEnabled(false);
                  nodesubcategory.setEnabled(false);
                  subcategory.setEnabled(false);
            }
            if(DeviceBasedSettings.fDeploymentType.equals("Static") && cnType.getSelectedItem().equals("Regular"))
            {
                  JOptionPane.showMessageDialog(jf,"Network type is static");
                  mobileNodeCB.setState(false);
                  staticNodeCB.setState(true);
                  lspeed.setEnabled(false);
                  cspeed.setEnabled(false);
                  nodesubcategory.setEnabled(false);
                  subcategory.setEnabled(false);
            }
        }
       if(b.equals(staticNodeCB))
       {
            if(staticNodeCB.getState()==true)
            {
                lspeed.setEnabled(false);
                cspeed.setEnabled(false);
            }
       }
       //set speed for cars and padestrians
       if(subcategory.getSelectedItem().equals("Padestrians"))
       {
            cspeed.removeAll();
            for(int j=10;j<=50;j=j+10)
                cspeed.add(j+"");
       }
        else if((subcategory.getSelectedItem().equals("Cars"))||(subcategory.getSelectedItem().equals("Trains")))
        {
              cspeed.removeAll();
              for(int j=10;j<=100;j=j+10)
                cspeed.add(j+"");
        }

}

//******************************************************************************

public void actionPerformed(ActionEvent e)
{

 String action;
 action=e.getActionCommand();

 if(action.equals("Close")) {cregions.removeAll(); jf.dispose();}

 //Set perimeters of new node
 else if (action.equals("Add"))
 {
       if(cnType.getSelectedItem().equals("Ferry") && !DeviceBasedSettings.ferryType.equals("Multi-Ferry"))
     {       //Create single ferry and ferry option be removed from create node dialog box
             if(DeviceBasedSettings.ferryType.equals("Single-Ferry") && dtnrouting.ferryArray.isEmpty()==true)
             {
                 createSingleFerry();
                 cnType.remove("Ferry");
            }
             else if(DeviceBasedSettings.ferryType.equals("Pigeon") && dtnrouting.ferryArray.isEmpty()==true)
                 createPigeons();
     }
     else
     {
            if(tnodeS.getText().equals("0"))
               JOptionPane.showMessageDialog(jf,"Put number of nodes");
            else
            {
               int i=Integer.parseInt(tnodeS.getText());

                for (int l=0;l<i;l++)
                {
                Node node=new Node();
                Node.ID+=1;
                node.nodeID=Node.ID;
                if(cnType.getSelectedItem().equals("Regular"))
                {
                    Node.rid=Node.rid+1;
                    node.name="R"+Node.rid;
                    if(mobileNodeCB.getState()==true)
                    node.speed=Integer.parseInt(cspeed.getSelectedItem());
                    dtnrouting.nodeArray.add(node);
                }
                else if(cnType.getSelectedItem().equals("Ferry"))
                {
                     Node.fid = Node.fid+1;
                     node.name="F"+Node.fid;
                     node.speed=Integer.parseInt(cspeed.getSelectedItem());
                     dtnrouting.ferryArray.add(node);
                     currentFerries.add(node);
                }
                else if(cnType.getSelectedItem().equals("FixedRelay"))
                {
                    mobileNodeCB.setState(false);
                    subcategory.setEnabled(false);
                    Node.tid=Node.tid+1;
                    node.name="D"+Node.tid;
                    dtnrouting.FixedRelayArray.add(node);
                    currentFixedRelays.add(node);
                }

                nameofnode=cnType.getSelectedItem();
                node.setRadioRange(Integer.parseInt(cradiorange.getSelectedItem()));
                node.wholeQueueSize=node.queueSizeLeft=Integer.parseInt(cqueuesize.getSelectedItem());
                dtnrouting.allNodes.add(node);

                //Attach initial letter of car, padestrian or train with the node id
                if(Setting.enviroment.equals("Real Life")&&(cnType.getSelectedItem().equals("Regular")))
                     appendSubCategoryInitial(node);

                //Setting region and position for the nodes
                for(int f=0;f<RegionArray.size();f++)
                {
                    Regions regionOb=RegionArray.get(f); //Retrieve the required region
                    if(cregions.getSelectedItem().equals(regionOb.RegionName))
                    {
                        nodeRegion=node.nodeRegion=regionOb; //Assign the region object to the region of node

                        //Add the node in the array List of region's network
                        regionOb.setNetworkNodes(node);
                        if(cnType.getSelectedItem().equals("Regular")||(cnType.getSelectedItem().equals("FixedRelay")))
                        {
                             if((Setting.enviroment.equals("Real Life"))&& (Setting.realType.equals("Map")))
                             {
                                 try {
                                      mapbasedMovement.initializepath(node);
                                     } catch (FileNotFoundException ex)
                                     {
                                        Logger.getLogger(CreateNode.class.getName()).log(Level.SEVERE, null, ex);
                                     } catch (IOException ex)
                                     {
                                        Logger.getLogger(CreateNode.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                             }

                            else
                                    nm.InitialNodePositions(node);
                            if(node.speed==0)
                                    nodeRegion.setNetworkType("Static");
                            else
                                    nodeRegion.setNetworkType("Mobile");
                         }
                     }
                   }
                }
              //If nodes selected are throw boxes with random deployement grid
                 if(cnType.getSelectedItem().equals("FixedRelay"))
                 {
                            tbd=new FixedRelayDeployment();
                            if(DeviceBasedSettings.tDeploymentType.equals("Grid"))
                                tbd.Grid_BasedDeployement(nodeRegion);
                            if(DeviceBasedSettings.tDeploymentType.equals("Random"))
                                tbd.RandomDeployement(currentFixedRelays);
                           /* if(DeviceBasedSettings.tDeploymentType.equals("MapBased"))
                                tbd.MapBasedDeployment(currentFixedRelays);*/
                 }
               }
             }  //end of else
     //if FixedRelay based deployment for routing is selected
    if((DeviceBasedSettings.FixedRelay==true || (Setting.enviroment.equals("Real Life")&& Setting.realType.equals("Map")))&& (dtnrouting.FixedRelayArray.size()==0))
        cnType.add("FixedRelay");

    //if ferry-based deplolyment for routing is selected
    if((DeviceBasedSettings.ferry==true && Setting.enviroment.equals("Non-Real Life")) && (dtnrouting.ferryArray.isEmpty()==true ) && (dtnrouting.allNodes.size()>0))
        cnType.add("Ferry");
    ResetPerimeters();
    //Enable End Nodes Menu item
    dtnrouting.endNodes.setEnabled(true);
  }
}

//******************************************************************************

//RESET PERIMETERS OF CREATE NODE CLASS
public void ResetPerimeters() // reset perimeters of a node
{
     tnodeS.setText("0");                   cnType.select(0);
     cradiorange.select(0);                 cspeed.select(0);
     cqueuesize.select(0);
     Setting settings=new Setting();        settings.setSettingsFrameEnable(false);
     nodeS.setEnabled(true);                tnodeS.setEnabled(true);
     lregions.setEnabled(true);             cregions.setEnabled(true);
             
}

//******************************************************************************

 public void textValueChanged(TextEvent e)
{
   if(e.getSource()==tnodeS)
     {
       try{
         int nodes=Integer.parseInt(tnodeS.getText());
         if((cnType.getSelectedItem().equals("FixedRelay"))&&(DeviceBasedSettings.tDeploymentType.equals("Grid")))
         {
             nodes+=dtnrouting.FixedRelayArray.size();
             if(!(((nodes == 1) || (nodes % 2 == 0)) && (nodes<13)))
             {
                 JOptionPane.showMessageDialog(jf, "Put number of FixedRelays as power of 2 and less than 12");
                 tnodeS.setText("0");
             }
         }
         }catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(jf, "Put only integer values");
                 tnodeS.setText("0");     }
     }
}

//******************************************************************************

public void createSingleFerry()
{
    Node node=new Node();
    node.nodeID=Node.ID+1;
    Node.fid = Node.fid+1;
    node.name="F"+Node.fid;
    node.speed=Integer.parseInt(cspeed.getSelectedItem());
    node.visitingRegion=dtnrouting.RegionArray.get(0);
    dtnrouting.ferryArray.add(node);
    node.setRadioRange(Integer.parseInt(cradiorange.getSelectedItem()));
    node.wholeQueueSize=node.queueSizeLeft=Integer.parseInt(cqueuesize.getSelectedItem());
    dtnrouting.allNodes.add(node);
    SingleFerryMovement sfm=new SingleFerryMovement();
    sfm.nonRealInitialPosition(node);
 }

//******************************************************************************

public void createPigeons()
{
    PigeonMovement pm=new PigeonMovement();
    for(int index=0;index<dtnrouting.RegionArray.size();index++)
    {
        Regions regionObj=dtnrouting.RegionArray.get(index);
        Node node=new Node();
        Node.ID=Node.ID+1;
        node.nodeID=Node.ID;
        Node.fid = Node.fid+1;
        node.name="F"+Node.fid;
        dtnrouting.ferryArray.add(node);
        node.speed=Integer.parseInt(cspeed.getSelectedItem());
        node.setRadioRange(Integer.parseInt(cradiorange.getSelectedItem()));
        node.wholeQueueSize=node.queueSizeLeft=Integer.parseInt(cqueuesize.getSelectedItem());
        node.nodeRegion=regionObj;
        node.visitingRegion=regionObj;
        dtnrouting.allNodes.add(node);
        pm.InitialPosition(node);

        //if network has mobile  nodes then clusterhead is fixed
        if(DeviceBasedSettings.pigeonClusterHead.equals("Fixed Relay"))
        {
            Node fixedClusterHead=new Node();
            Node.ID=Node.ID+1;
            fixedClusterHead.nodeID=Node.ID;
            Node.tid = Node.tid+1;
            fixedClusterHead.name="CH";
            dtnrouting.FixedRelayArray.add(fixedClusterHead);
            fixedClusterHead.speed=Integer.parseInt(cspeed.getSelectedItem());
            fixedClusterHead.setRadioRange(Integer.parseInt(cradiorange.getSelectedItem()));
            fixedClusterHead.wholeQueueSize=fixedClusterHead.queueSizeLeft=Integer.parseInt(cqueuesize.getSelectedItem());
            fixedClusterHead.nodeRegion=regionObj;
            dtnrouting.allNodes.add(fixedClusterHead);
            pm.InitialPosition(fixedClusterHead);
        }

        //if network has fixed  nodes then clusterhead is mobile
        else if (DeviceBasedSettings.pigeonClusterHead.equals("Ferry"))
        {
            Node ferryClusterHead=new Node();
            Node.ID=Node.ID+1;
            ferryClusterHead.nodeID=Node.ID;
            Node.tid = Node.tid+1;
            ferryClusterHead.name="CH";
            dtnrouting.ferryArray.add(ferryClusterHead);
            ferryClusterHead.speed=Integer.parseInt(cspeed.getSelectedItem());
            ferryClusterHead.setRadioRange(Integer.parseInt(cradiorange.getSelectedItem()));
            ferryClusterHead.wholeQueueSize=ferryClusterHead.queueSizeLeft=Integer.parseInt(cqueuesize.getSelectedItem());
            ferryClusterHead.nodeRegion=regionObj;
            dtnrouting.allNodes.add(ferryClusterHead);
            pm.InitialPosition(ferryClusterHead);
        }
   }
  }

//******************************************************************************

public void appendSubCategoryInitial(Node node)
{
    node.name=node.name.substring(0,1)+subcategory.getSelectedItem().substring(0,1)+node.name.substring(1,2);
    if(subcategory.getSelectedItem().startsWith("T"))
    {
        trainList.add(node);
        node.trainNo= Node.trainID;
        Node.trainID=Node.trainID+1;
    }
    else if(subcategory.getSelectedItem().startsWith("C"))
    {
        carList.add(node);
        node.carNo= Node.carID;
        Node.carID=Node.carID+1;
    }
    else if(subcategory.getSelectedItem().startsWith("P"))
    {
        padList.add(node);
        node.padNo= Node.padID;
        Node.padID=Node.padID+1;
    }
}

//******************************************************************************

} //END OF CREATENODE CLASS