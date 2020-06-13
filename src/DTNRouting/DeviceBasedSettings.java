//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JFrame;

//START OF THE CLASS THAT IS MADE FOR MAKING SETTINGS FOR SUPPORTING RELAY DEVICES
public class DeviceBasedSettings implements ItemListener, ActionListener
{
//Instance variables
  JFrame jf;

  //controls for fixed relay
  CheckboxGroup tcbg=new CheckboxGroup();
  Checkbox grid=new Checkbox("Grid-Based",tcbg,false);
  Checkbox random=new Checkbox("Random",tcbg,false);

  //controls for ferry
  CheckboxGroup fcbg=new CheckboxGroup();
  Checkbox sFerry=new Checkbox("Single-Ferry",fcbg,false);
  Checkbox pFerry=new Checkbox("Pigeons",fcbg,false);

 // Checkbox mFerry=new Checkbox("Multi-Ferry",fcbg,false);
 // Checkbox cFixedRelay=new Checkbox("Add Fixed Relays",false);

  //Movable or static cluster head for pegion based system
  CheckboxGroup pegioncbg=new CheckboxGroup();
  Checkbox clusterHead1=new Checkbox("Fixed Relay",pegioncbg,true);
  Checkbox clusterHead2=new Checkbox("Ferry",pegioncbg,false);

  Panel  ferryParent,singlefChild, pigeonfChild;
  CardLayout cardLO;

  Label ldeviceDetail=new Label("Deployment");
  Label lpigeonClusterHead=new Label("Cluster Head");
  Label lmovementType=new Label("Network Type");
  Choice cmovementType=new Choice();
 
  //buttons
  Button ok=new Button("OK");
  Button cancel=new Button("Close");

  static  boolean Grid=false,pigeon=false, isRegionOne=true,FixedRelay=false,ferry=false;
  public static String tDeploymentType=" ", ferryType="",fDeploymentType="Mobile",pigeonClusterHead="Fixed Relay", relay="";

//*****************************************************************************

//CONSTRUCTOR
public DeviceBasedSettings()
{ relay=""; }

//*****************************************************************************

//DIALOG BOX FOR FIXED RELAY
public void fixedRelayDialogBox()
{
   relay="FixedRelay";
   jf=new JFrame("Deploying Fixed Relay(s)");
   jf.setLayout(new FlowLayout());
   jf.add(ldeviceDetail); jf.add(grid); jf.add(random);

//Buttons
   ok.setPreferredSize(new Dimension(50,20));
   cancel.setPreferredSize(new Dimension(50,20));
   jf.add(ok);            jf.add(cancel);

//registration of events
   grid.addItemListener(this);
   random.addItemListener(this);
   ok.addActionListener(this);
   cancel.addActionListener(this);

//showing frame
   jf.setSize(new Dimension(300,200));
   jf.setResizable(false);
   jf.setVisible(true);
}
//*****************************************************************************

//DIALOG BOX FOR FERRY
public void ferryDialogBox()
{
  relay="ferry";
  jf=new JFrame("Deploying Mobile Relays");
  jf.setLayout(new FlowLayout());
  cardLO=new CardLayout();
  ferryParent=new Panel();
  ferryParent.setLayout(cardLO);
  ferryParent.setLayout(cardLO);
      
 //Number of ferries
   jf.add(ldeviceDetail);
   jf.add(sFerry); jf.add(pFerry);
   jf.add(ferryParent);

//sub panel in ferryParent Panel when Single Ferry is selected
   singlefChild=new Panel();
   cmovementType.add("Mobile");           cmovementType.add("Static");
   singlefChild.add(lmovementType);       singlefChild.add(cmovementType);
       
//sub panel in ferryParent panel when Pigeon is selected
   pigeonfChild=new Panel();
   pigeonfChild.add(lpigeonClusterHead);    pigeonfChild.add(clusterHead1); pigeonfChild.add(clusterHead2);

//adding all the singlefChild,multifChild and pigeonfChild panels to ferryParent panel
   ferryParent.add(singlefChild,"singleFerryPanel"); ferryParent.add(pigeonfChild,"pigeonPanel");

//Adding buttons
    ok.setPreferredSize(new Dimension(50,20));
    cancel.setPreferredSize(new Dimension(50,20));
    jf.add(ok);            jf.add(cancel);

//Registration to listeners
   clusterHead1.addItemListener(this);
   clusterHead2.addItemListener(this);
   cmovementType.addItemListener(this);
   cmovementType.select(1);
   ok.addActionListener(this);
   cancel.addActionListener(this);
   sFerry.addItemListener(this);
   pFerry.addItemListener(this);

//if region is only one then pigeon-based routing cannot be used
    if(isRegionOne==true)
      pFerry.setEnabled(false);
    else
      pFerry.setEnabled(true);

//Opening Ferry Frame
    jf.setSize(new Dimension(300,200));
    jf.setVisible(true);
}

//*****************************************************************************

public void itemStateChanged(ItemEvent e)
{
 Object ob=e.getSource();
               
 //Types of FixedRelay deployement i.e Grid or Random based
 if(grid.getState()==true)                                           tDeploymentType="Grid";
 else if(random.getState()==true)                                    tDeploymentType="Random";

 //Network type chosen for single ferry
 else if(cmovementType.getSelectedItem().equals("Mobile"))           fDeploymentType ="Mobile";
 else if(cmovementType.getSelectedItem().equals("Static"))           fDeploymentType ="Static";

 //Type of ferries
 if(sFerry.getState()==true)        //If single ferry
    { ferryType ="Single-Ferry";                  cardLO.first(ferryParent); ferry=true;}
 else if(pFerry.getState() == true) //If pigeon based ferry
    {
         pigeon=true;
         cardLO.last(ferryParent);
         ferryType="Pigeon";
         ferry=true;
         if(clusterHead1.getState()==true)  pigeonClusterHead="Fixed Relay";
         if(clusterHead2.getState()==true)  pigeonClusterHead="Ferry";
     }
}

//*****************************************************************************

public void actionPerformed(ActionEvent e)
{
     String action;
     action=e.getActionCommand();
     if(action.equals("OK"))
     {
        if(relay.equals("FixedRelay"))  FixedRelay=true;
        else if(relay.equals("ferry"))  ferry=true;
        jf.dispose();
     }
     if(action.equals("Close"))
     jf.dispose();
}

//*****************************************************************************

//RESETTING PERIMETERS OF RELAY DEVICES
public void setPerimeters()
{
    Grid=pigeon=ferry=FixedRelay=false;
    isRegionOne=true;
    ferryType=tDeploymentType=fDeploymentType="";
}

//*****************************************************************************

}//END OF DEVICEBASEDSETTING CLASS
