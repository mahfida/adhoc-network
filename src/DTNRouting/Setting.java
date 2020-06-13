//PACKAGE NAME
package DTNRouting;

//IMPORT CLASSES


import RoutingProtocols.RoutingProtocol;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

//SETTING MENU BAR CLASS
public class Setting implements ItemListener, ActionListener {

    JFrame jf=new JFrame("Simulation Settings");
    Label simRuns=new Label("Simulation Runs");
    Label environment=new Label("Environment Type");
    Label lmap=new Label("Map");
    Label lRegions=new Label("No. of Regions");
    Label lds=new Label("Data");
    Label lreaParent=new Label("Secenario:");

    Choice sRuns=new Choice();
    Choice cenvironment=new Choice();
    Choice cmap=new Choice();
    Choice cRegions=new Choice();
    Choice cds=new Choice();

    //controls for real scenario
    CheckboxGroup treal=new CheckboxGroup();
    Checkbox rmap=new Checkbox("Map-Based",treal,false);
    Checkbox rds=new Checkbox("Dataset",treal,true);

    //Create panels for real world sxenarios
     Panel  realParent,mapRChild, dsRChild;
     CardLayout cardLO;

    Button close=new Button("Close");
    Button ok=new Button("OK");
    
    int h,w,j,k,Sim=1,RegionArray[][]=new int[50][50];;
    static int numRegions=1;
    public static String mapCity="Karachi", enviroment="Non-Real Life",realType="", dataset="";
    
  
//******************************************************************************

//Constructor
 public Setting() {}
    
//******************************************************************************
 
public void createSettingFrame()
{
   jf.setLayout (new   GridLayout(6,2,1,1));
   cardLO=new CardLayout();
   realParent=new Panel();
   realParent.setLayout(cardLO);
   realParent.setLayout(cardLO);

 // Number of Simulation Runs
   for(int i=0;i<10;i++)             sRuns.add((i+1)+"");

//Type of enviroments
   cenvironment.add("Real Life");    cenvironment.add("Non-Real Life");

//Number of regions in playfield
  cRegions.add("1");
  for (int i=2;i<=12;i=i+2)          cRegions.add((i)+"");

//Add maps in cmap choice list
  cmap.add("Islamabad");  cmap.add("Peshawar");
  cmap.add("Karachi");    cmap.add("Lahore");   cmap.add("Quetta");

//Add real world map and data
  cds.add("St.Andrew Uni");
  cds.add("iMote Traces");
  cds.add("R. Mining");


//Sub panel in realParent Panel when map is selected
   mapRChild=new Panel();
   mapRChild.add(lmap);       mapRChild.add(cmap);

//Sub panel in  realParent panel when dataset is selected
   dsRChild=new Panel();
   dsRChild.add(lds);         dsRChild.add(cds);

//Adding all the mapRChild and dsRChild to realParent panel
   realParent.add(mapRChild,"Map-Based");
   realParent.add(dsRChild,"Dataset");

//Register to Listeners
    rmap.addItemListener(this);
    rds.addItemListener(this);
    cmap.addItemListener((ItemListener)this);
    cds.addItemListener((ItemListener)this);
    cenvironment.addItemListener((ItemListener)this);
    cRegions.addItemListener((ItemListener)this);
    sRuns.addItemListener((ItemListener)this);
    ok.addActionListener ((ActionListener)this);
    close.addActionListener((ActionListener)this);
           
//Disable Components
    rmap.setEnabled(false);         rds.setEnabled(false);
    //By defalut map and regions choice boxes are disabled
    lmap.setEnabled(false);         cmap.setEnabled(false);
    realParent.setEnabled(false);
    lRegions.setEnabled(false);     cRegions.setEnabled(false);

//Add Components
    jf.add(simRuns);                jf.add(sRuns);
    jf.add(environment);            jf.add(cenvironment);
    jf.add(rmap);                   jf.add(rds);
    jf.add(lreaParent);             jf.add(realParent);
    jf.add(lRegions);               jf.add(cRegions);
    jf.add(ok);                     jf.add(close);

// Set window size
    jf.setSize(new Dimension(350,300));
    jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    jf.setResizable(false);
    jf.setVisible(true);

}

//******************************************************************************

public void itemStateChanged(ItemEvent e) {
Object b=e.getSource();

if(b.equals(sRuns))
{
        this.Sim = Integer.parseInt(sRuns.getSelectedItem());
        //Number of elemets in routing protocol arrays are set to number of simulation
        RoutingProtocol.metrixArray(this.Sim);
}

else if(b.equals(cenvironment))
{
    if(cenvironment.getSelectedItem().equals("Real Life"))
    {
        enviroment="Real Life";
        cRegions.setEnabled(false);
        lRegions.setEnabled(false);
        lmap.setEnabled(true);
        cmap.setEnabled(true);
        rmap.setEnabled(true);
        rds.setEnabled(true);
        realParent.setEnabled(true);
        numRegions=1;
     }
    else  if(cenvironment.getSelectedItem().equals("Non-Real Life"))
    {
        enviroment="Non-Real Life";
        cRegions.setEnabled(true);
        lRegions.setEnabled(true);
        rmap.setEnabled(false);
        rds.setEnabled(false);
        realParent.setEnabled(false);
        dtnrouting.nm_prandom.setEnabled(false);
        dtnrouting.nm_random.setEnabled(false);
        dtnrouting.nm_mapBased.setEnabled(true);
        dtnrouting.nm_ds.setEnabled(false);
    }

 }
else if (b.equals(cRegions))
    numRegions=Integer.parseInt(cRegions.getSelectedItem());

else if(b.equals(cmap))      mapCity = cmap.getSelectedItem();
else if(b.equals(cds))      dataset= cds.getSelectedItem();
//Real Options
if(enviroment.equals("Real Life") && rmap.getState()==true)        //If map is selected
{
        realType ="Map";                  cardLO.first(realParent);
}
else if (enviroment.equals("Real Life") && rds.getState() == true) //If pigeon based ferry
{
        realType="Dataset";             cardLO.last(realParent);
}

}

//******************************************************************************

public void actionPerformed(ActionEvent e)
{

String action=e.getActionCommand();
int x=dtnrouting.x_start, y=dtnrouting.y_start;

//IF BUTTON PRESSED IS OK
if(action.equals("OK"))
{


    if(cenvironment.getSelectedItem().equals("Real Life"))
    {
         dtnrouting.nm_random.setEnabled(false);
         dtnrouting.nm_prandom.setEnabled(false);
        if(realType.equals("Map"))
        {
            dtnrouting.nm_mapBased.setEnabled(true);
            dtnrouting.nm_ds.setEnabled(false);
            dtnrouting.dsNode.setEnabled(false);
            dtnrouting.createNode.setEnabled(true);
        }
        else
        {
            dtnrouting.nm_mapBased.setEnabled(false);
            dtnrouting.nm_ds.setEnabled(true);
            dtnrouting.dsNode.setEnabled(true);
            dtnrouting.createNode.setEnabled(false);
            dtnrouting.movementtype="Dataset";
        }
        cds.add("St.Andrew Uni");
        cds.add("iMote Traces");
    }
    
    else
    {
       dtnrouting.nm_random.setEnabled(true);
       dtnrouting.nm_prandom.setEnabled(true);
       dtnrouting.nm_mapBased.setEnabled(false);
       dtnrouting.dsNode.setEnabled(false);
       dtnrouting.createNode.setEnabled(true);
    }

    switch(numRegions)
    {
    case 1:
                 w=dtnrouting.width/1;          h=dtnrouting.height/1;
                 j=1;                             k=1;
                 break;
    case 2:
                 w=dtnrouting.width/2;          h=dtnrouting.height/1;
                 j=2;                           k=1;
                 break;
    case 4:
                 w=dtnrouting.width/2;          h=dtnrouting.height/2;
                 j=2;                           k=2;
                 break;
    case 6:
                 w=dtnrouting.width/3;          h=dtnrouting.height/2;
                 j=3;                           k=2;
                 break;
    case 8:
                 w=dtnrouting.width/4;          h=dtnrouting.height/2;
                 j=4;                           k=2;  
                 break;
    case 10:
                 w=dtnrouting.width/5;          h=dtnrouting.height/2;
                 j=5;                           k=2;   
                 break;
    case 12:
                 w=dtnrouting.width/4;          h=dtnrouting.height/3;
                 j=4;                           k=3; 
                 break;
    default:
                 JOptionPane.showMessageDialog(jf,"The Region must be even and less than 13");
    }
    int g=0;
        
    for(int a=0;a<k;a++)
    {
          for(int b=0;b<j;b++)
          {
           RegionArray[g][0]=dtnrouting.x_start+b*w;
           RegionArray[g][1]=dtnrouting.y_start+a*h;
           RegionArray[g][2]=w;
           RegionArray[g][3]=h;
           Regions r=new Regions(RegionArray[g][0],RegionArray[g][1],RegionArray[g][2],RegionArray[g][3]);
           g=g+1;
          }
                 
    }

     //inside a single region pigeon-based feerying cannot be used
     if(numRegions==0 || numRegions==1)
             DeviceBasedSettings.isRegionOne=true;
     else
             DeviceBasedSettings.isRegionOne=false;

    UpdateInformation.Sim=this.Sim;          UpdateInformation.runs=this.Sim;
    jf.dispose();
    setSettingsFrameEnable(false);
    ResetPerimeters();
 } //END of OK IF

//IF BUTTON PRESSED IS CLOSED
if(action.equals("Close"))
 {
     jf.dispose();
    // setSettingsFrameEnable(false);
 }
}

//******************************************************************************

public void ResetPerimeters() // reset perimeters of a node
{
        sRuns.select(0);
        
}
public static void ClearPerimeters()
{
       Setting.enviroment="Non-Real Life";
       Setting.realType="";
       Setting.dataset="";
}
//******************************************************************************

public void setSettingsFrameEnable(boolean b)
{
    dtnrouting.settings.setEnabled(b);
}
//******************************************************************************
} //END OF CLASS
