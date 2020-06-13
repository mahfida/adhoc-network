//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

//------------------------------------------------------------------------------
//START OF CLASS BUNDLE
public class Bundle implements ActionListener
{
//Instance Variables
    public Node destNode=new Node(); //destination node of the bundle
    public Node sourceNode=new Node(); //source node of the bundle
    public int bundleTTL,maxTTL;
    public int bundleSize,bundleLoad=1,bundleBandwidth=0,bundleLatency=0,bundleDelivered=0,destRegionBundleCopies;
    public static int bundleID;
    public boolean isBundleDelivered=false,isTTLExpired=false,isLargeSize=false;
    public String bundleName;
    public JFrame jf=new JFrame("Create Bundle");
    public Label lbundleID=new Label("Bundle ID");
    public TextField tbundleID=new TextField("");
    public Label lbundleTTL=new Label("TTL Value");
    public TextField tbundleTTL=new TextField("");
    public Label lbundleSize=new Label("Size (MB)");
    public TextField tbundleSize=new TextField("");
    public Button ok=new Button("OK");
    public Button close=new Button("Close");
    public Color color;
    public String endNodesRegion="Same";
    Random rand=new Random();
    dtnrouting dtn=new dtnrouting();

//******************************************************************************

public Bundle()
{    }

//******************************************************************************
//BUNDLE FRAME GUI
public void BudleDialog()
{
    jf.setLayout(new GridLayout(7,2,5,5));
    tbundleID.setEditable(false);
    jf.add(lbundleID);      jf.add(tbundleID);
    jf.add(lbundleTTL);     jf.add(tbundleTTL);
    jf.add(lbundleSize);    jf.add(tbundleSize);
    jf.add(ok);             jf.add(close);
    ok.addActionListener(this);
    close.addActionListener(this);
    jf.setSize(new Dimension(200,200));
    jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    jf.setResizable(false);
    jf.setVisible(true);
}

//******************************************************************************
//CREATION OF A BUNDLE
public void CreateBundle()
{
    bundleID=bundleID+1; // increment the id of bundle and then add it in tbundleID
    tbundleID.setText("B"+bundleID); //ID of bundle inside the bundle id text field
    bundleName="B"+bundleID; //Bundle Name for reference in code
}

//******************************************************************************

public void actionPerformed(ActionEvent e)
{
   String value=e.getActionCommand();
   color=new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
       
   if(value.equals("OK"))
   {
       if(tbundleTTL.getText().equals("")||tbundleSize.getText().equals(""))
       {
           JOptionPane.showMessageDialog(jf,"Fill empty values");
       }
       else
       {
            try{
                    maxTTL=bundleTTL=Integer.parseInt(tbundleTTL.getText());
                    bundleSize=Integer.parseInt(tbundleSize.getText());
               }
            catch(NumberFormatException nfe)
               {
                    tbundleTTL.setText("");
                    tbundleSize.setText("");
                    JOptionPane.showMessageDialog(jf,"Enter only Integer");
               }

            //Put bundle in the queue of source end node
            if(dtnrouting.Sources.get((dtnrouting.Sources.size()-1)).queueSizeLeft>this.bundleSize)
            {
                    dtnrouting.areBundlesDelivered.add(this);
                    dtnrouting.Sources.get(dtnrouting.Sources.size()-1).queueSizeLeft-=bundleSize; //update queue space after putting bundle in it
                    dtnrouting.Sources.get(dtnrouting.Sources.size()-1).bundleIDHash.add(bundleName); //Store ID of bundle in the source as Hash value
                    dtnrouting.Sources.get(dtnrouting.Sources.size()-1).bundleTimeSlots.put(this.bundleName,0);
                    dtnrouting.Sources.get(dtnrouting.Sources.size()-1).bundleCopies.put(this.bundleName,1);
                    dtnrouting.Sources.get(dtnrouting.Sources.size()-1).DestNBundle.put(this,dtnrouting.Destinations.get(dtnrouting.Destinations.size()-1));
                    dtnrouting.tDetail.append("\t"+bundleName);
                    sourceNode=dtnrouting.Sources.get(dtnrouting.Sources.size()-1);
                    destNode=dtnrouting.Destinations.get(dtnrouting.Destinations.size()-1);
                    if(sourceNode.nodeRegion.equals(destNode.nodeRegion)==true)
                        this.endNodesRegion="Same";
                    else
                        this.endNodesRegion="Different";
            }
            else    //If queue of the bundle has not enough space to store the new bundle then
                     dtnrouting.CommentsTA.append("\nSource "+ dtnrouting.Sources.get(dtnrouting.Sources.size()-1).name+" has not enough space to occupy "+this.bundleName);
   
            tbundleTTL.setText("");
            tbundleSize.setText("");
            tbundleID.setText("");
        }
        //Enable Create Bundle Menu
         dtnrouting.createBundle.setEnabled(false);
   }
   if(value.equals("Close"))
        jf.dispose();
       
}

//******************************************************************************

public void refreshBundleSettings(Bundle bundle)
{
    bundle.isBundleDelivered=false;
    bundle.bundleLoad=1;
    bundle.bundleTTL=bundle.maxTTL;
    bundle.bundleLatency=bundle.bundleBandwidth=bundle.bundleDelivered=0;
    bundle.bundleDelivered=0;
    bundle.isLargeSize=false;
    bundle.isTTLExpired=false;

}

//******************************************************************************

}//END OF BUNDLE CLASS
