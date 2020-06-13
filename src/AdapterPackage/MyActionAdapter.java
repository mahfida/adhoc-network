         /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */     
     
package AdapterPackage;
 
import RoutingProtocols.RoutingProtocol;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Results.*;
import DTNRouting.*;
import MovementPattern.SingleFerryMovement;
import Algorithms.*;
import java.util.Random;
/**
 *
 * @author fwu
 */
public class MyActionAdapter implements ActionListener {
dtnrouting dtn;
UpdateInformation updateInfo;
Random rand;
static DeviceBasedSettings deviceSettingsObj=null;
public static String protocol="ContactOblivious", mobilityPattern;
private DSNodes dsnode;
private CreateNode cnode;
public MyActionAdapter(dtnrouting dtn)
    {
    this.dtn=dtn;
    }
public void actionPerformed (ActionEvent ae)

{
 String buttonname;

 buttonname=ae.getActionCommand();
 // Information of new bundle to be set
    if(buttonname.equals("Settings"))
    {
          Setting setting=new Setting();
          setting.createSettingFrame();

    }
    else if(buttonname.equals("Create Bundle"))
     {
         Bundle bundleObj=new Bundle();
         bundleObj.BudleDialog();
         bundleObj.CreateBundle();
     }
    else if(buttonname.equals("Fixed Relay(s)"))
     {
        deviceSettingsObj=new DeviceBasedSettings();
        deviceSettingsObj.fixedRelayDialogBox();
     }
    else if (buttonname.equals("Ferr(y/ies)"))
     {
        deviceSettingsObj=new DeviceBasedSettings();
        deviceSettingsObj.ferryDialogBox();
     }
     // The mobility patterns
     else if (buttonname.equals("Random"))
     {
       dtnrouting.movementtype="Random"; mobilityPattern=dtnrouting.movementtype;
     }

    else if(buttonname.equals("Pseudorandom"))
     {
        dtnrouting.movementtype="Pseudorandom"; mobilityPattern=dtnrouting.movementtype;
        dtnrouting.nodemovement.InitializePsuedoPath();
      }
  else if(buttonname.equals("Dataset"))
     {
        dtnrouting.movementtype="Dataset"; mobilityPattern=dtnrouting.movementtype;

     }
    else if(buttonname.equals("MapBased"))
     {
        dtnrouting.movementtype="MapBased"; mobilityPattern=dtnrouting.movementtype;
     }
    // The menu items of Node Menu
    else if (buttonname.equals("End Nodes"))
     {

         EndNodes endnodes=new EndNodes();

     }
   else if (buttonname.equals("Create Node"))
    {
   
          cnode=new CreateNode();
    }
   else if (buttonname.equals("Dataset Node"))
          dsnode=new DSNodes();

 // The routing Protocols

    else if(buttonname.equals("Direct Delivery"))
    {

        dtnrouting.protocolName="Direct Delivery";
        protocol="ContactOblivious";
        dtn.ExecuteProtocol();
    }


    else if(buttonname.equals("First Contact"))
    {
       dtnrouting.protocolName="First Contact";
       protocol="ContactOblivious";
       dtn.ExecuteProtocol();

     }

    else if(buttonname.equals("Epidemic"))
    {
       dtnrouting.protocolName="Epidemic";
       protocol="ContactOblivious";
       dtn.ExecuteProtocol();

     }

    else if(buttonname.equals("Spray&Wait Binary"))
    {
       dtnrouting.protocolName="Spray&WaitB";
       protocol="ContactOblivious";
       dtn.ExecuteProtocol();
     }

    else if(buttonname.equals("Spray&Wait Normal"))
    {
        dtnrouting.protocolName="Spray&WaitN";
        protocol="ContactOblivious";
        dtn.ExecuteProtocol();
    }

    else if (buttonname.equals("PRoPHET"))
    {
       dtnrouting.protocolName="PRoPHET";
       protocol="ContactBased";
       dtn.ExecuteProtocol();
    }

    else if (buttonname.equals("FRESH"))
    {
       dtnrouting.protocolName="FRESH";
       protocol="ContactBased";
       dtn.ExecuteProtocol();
    }
    else if(buttonname.equals("NECTAR"))
    {
      dtnrouting.protocolName="NECTAR";
      protocol="ContactBased";
      dtn.ExecuteProtocol();
    }

    else if(buttonname.equals("MPRoPHET"))
    {
       dtnrouting.protocolName="MPRoPHET";
       protocol="ContactBased";
       dtn.ExecuteProtocol();
    }

   else if (buttonname.equals("CAoICD"))
    {
       dtnrouting.protocolName="CAoICD";
       protocol="ContactBased";
       dtn.ExecuteProtocol();

    }
    else if(buttonname.equals("SimBet"))
    {
      dtnrouting.protocolName="SimBet";
      protocol="socialRelation";
      dtn.ExecuteProtocol();
    }

    else if(buttonname.equals("BubbleRap"))
    {
       dtnrouting.protocolName="BubbleRap";
       protocol="socialRelation";
       dtn.ExecuteProtocol();
    }

   else if (buttonname.equals("CHRP"))
    {
       dtnrouting.protocolName="CHRP";
       protocol="socialRelation";
       dtn.ExecuteProtocol();
    }

//Community button
    else if (buttonname.equals("Community"))
    {
      CDSInterface cds=new CDSInterface();
      cds.start();
    }
 //Clear button
    else if (buttonname.equals("Clear"))
    {
      updateInfo=new UpdateInformation();
      updateInfo.ClearSettings();
      //Reset Perimeters
      if(deviceSettingsObj!=null)
      deviceSettingsObj.setPerimeters();
    }
 //Refresh button
    else if(buttonname.equals("Refresh"))
    {
      updateInfo=new UpdateInformation();
      updateInfo.RefreshSettings();
      if(DeviceBasedSettings.ferryType.equals("Single-Ferry"))
      SingleFerryMovement.setPerimeters();
    }
 //Run the Simulation
    else if(buttonname.equals("Run"))
    {
        if(dtnrouting.ob!=null && ! dtnrouting.movementtype.equals(" "))
        {
             PlayField.isContactPresent=new boolean[dtnrouting.allNodes.size()][dtnrouting.allNodes.size()];
             dtnrouting.contactDuration=new int[dtnrouting.allNodes.size()][dtnrouting.allNodes.size()];
             for(int i=0;i<dtnrouting.allNodes.size();i++)
                for(int j=0;j<dtnrouting.allNodes.size();j++)
                {
                    PlayField.isContactPresent[i][j] = false;
                    dtnrouting.contactDuration[i][j]=0;
                }
                if(protocol.equals("ContactBased"))
                {
                    dtnrouting.ob.setPerimeters();
                }
                
               if(dtnrouting.protocolName.equals("CHRP"))
                {
                    if(Setting.dataset.equals("St.Andrew Uni"))
                    dtnrouting.ob.setPerimeters("..\\Datasets\\SA",25);
                    if(Setting.dataset.equals("iMote Traces"))
                    dtnrouting.ob.setPerimeters("..\\Datasets\\MT",36);
                }
                RoutingProtocol.index=0;

             //NUmber of bundle copies generated when Spray and WaitB is selected
             if(dtnrouting.protocolName.equals("Spray&WaitB"))
             {
               rand=new Random();
               double r;  //declare double variable r
               for(int k=0;k<dtnrouting.Sources.size();k++)
               {
               r=Math.log10(dtnrouting.areBundlesDelivered.get(k).sourceNode.nodeRegion.getNetworkNodes().size())/Math.log10(2);    //random function set the value of r between 0 and 1
               int l=(int)(Math.floor(r));  //multiply r with 3 so that the power of 2 go to 3 and then ceil it
               l=rand.nextInt(l+1);
               dtnrouting.areBundlesDelivered.get(k).bundleLoad=(int)Math.pow(2,l); //take r as power of 2 and assign it to NoDuplicate
               dtnrouting.Sources.get(k).bundleCopies.put(dtnrouting.areBundlesDelivered.get(k).bundleName,dtnrouting.areBundlesDelivered.get(k).bundleLoad );
               dtnrouting.CommentsTA.append("\nCopies Generated by "+dtnrouting.Sources.get(k).name+" : "+dtnrouting.areBundlesDelivered.get(k).bundleLoad);

               //if the region of destination is different then there too, Spray and Wait Binary is used
                if(dtnrouting.areBundlesDelivered.get(k).endNodesRegion.equals("Different"))
                    {
                       r=Math.log10(dtnrouting.areBundlesDelivered.get(k).destNode.nodeRegion.getNetworkNodes().size())/Math.log10(2);    //random function set the value of r between 0 and 1
                       int i=(int)(Math.floor(r));  //multiply r with 3 so that the power of 2 go to 3 and then ceil it
                       dtnrouting.areBundlesDelivered.get(k).destRegionBundleCopies=(int) Math.pow(2,rand.nextInt(i+1));
                    }
               }
             }
             //Number of bundle copies generated when Spray and WaitB is selected
            if(dtnrouting.protocolName.equals("Spray&WaitN"))
            {
              rand=new Random();

               for(int k=0;k<dtnrouting.Sources.size();k++)
               {

               dtnrouting.areBundlesDelivered.get(k).bundleLoad=rand.nextInt( dtnrouting.areBundlesDelivered.get(k).sourceNode.nodeRegion.getNetworkNodes().size())+1; //random function set the value of r between 1 and 10
               dtnrouting.Sources.get(k).bundleCopies.put(dtnrouting.areBundlesDelivered.get(k).bundleName,dtnrouting.areBundlesDelivered.get(k).bundleLoad );
               dtnrouting.CommentsTA.append("\nCopies Generated by "+dtnrouting.Sources.get(k).name+" : "+dtnrouting.areBundlesDelivered.get(k).bundleLoad);
               //if the region of destination is different then there too, Spray and Wait Binary is used
               if(dtnrouting.areBundlesDelivered.get(k).endNodesRegion.equals("Different"))
                   {
                      dtnrouting.areBundlesDelivered.get(k).destRegionBundleCopies=rand.nextInt(dtnrouting.areBundlesDelivered.get(k).destNode.nodeRegion.getNetworkNodes().size())+1;
                   }
                }
            }
        }
            dtnrouting.delay=0;
            dtnrouting.isRun=true;

    }  //Showing performance table and chart
 if(buttonname.equals("Performance Table"))
    {
      RP_Performance rp=new RP_Performance();
      rp.CreateGUI();
      rp.displayTable();
    }

 if(buttonname.equals("Bar Chart"))
    {
      RP_Performance rp=new RP_Performance();
      rp.CreateGUI();
      rp.displayChart();
    }

 }
}
