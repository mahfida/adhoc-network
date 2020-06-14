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
import Algorithms.*;
import java.util.Random;
/**
 *
 * @author nil
 */
public class MyActionAdapter implements ActionListener {
dtnrouting dtn;
UpdateInformation updateInfo;
Random rand;
public static String protocol="ContactOblivious", mobilityPattern;
public MyActionAdapter(dtnrouting dtn)
    {
    this.dtn=dtn;
    }


public void actionPerformed (ActionEvent ae)

{
 String buttonname;

 buttonname=ae.getActionCommand();
 
// CREATE PACKET 
    if(buttonname.equals("Packet"))
     {
         CreatePacket packetObj=new CreatePacket();
         packetObj.GenerateFrame();
     }
    
// MOBILITY PATTERN
 if(buttonname.equals("Pseudorandom"))
     dtnrouting.movementtype="Pseudorandom";
   
// CREATE NODE   
    else if (buttonname.equals("Node"))
    {
   
	   CreateNode cnodeObj=new CreateNode();
	   cnodeObj.GenerateFrame();
    }
 
 // ROUTING PROTOCOLS
 else if(buttonname.equals("Direct Delivery"))
    {

        dtnrouting.protocolName="Direct Delivery";
        protocol="ContactOblivious";
        dtn.ExecuteProtocol();
    }else if(buttonname.equals("First Contact"))
    {
       dtnrouting.protocolName="First Contact";
       protocol="ContactOblivious";
       dtn.ExecuteProtocol();

     }else if(buttonname.equals("Epidemic"))
    {
       dtnrouting.protocolName="Epidemic";
       protocol="ContactOblivious";
       dtn.ExecuteProtocol();
    }else if(buttonname.equals("Spray&Wait Binary"))
    {
       dtnrouting.protocolName="Spray&WaitB";
       protocol="ContactOblivious";
       dtn.ExecuteProtocol();
     }else if(buttonname.equals("Spray&Wait Normal"))
    {
        dtnrouting.protocolName="Spray&WaitN";
        protocol="ContactOblivious";
        dtn.ExecuteProtocol();
    }else if (buttonname.equals("PRoPHET"))
    {
       dtnrouting.protocolName="PRoPHET";
       protocol="ContactBased";
       dtn.ExecuteProtocol();
    }else if (buttonname.equals("FRESH"))
    {
       dtnrouting.protocolName="FRESH";
       protocol="ContactBased";
       dtn.ExecuteProtocol();
    }else if(buttonname.equals("MPRoPHET"))
    {
       dtnrouting.protocolName="MPRoPHET";
       protocol="ContactBased";
       dtn.ExecuteProtocol();
    }else if (buttonname.equals("CAoICD"))
    {
       dtnrouting.protocolName="CAoICD";
       protocol="ContactBased";
       dtn.ExecuteProtocol();
    }else if(buttonname.equals("BubbleRap"))
    {
       dtnrouting.protocolName="BubbleRap";
       protocol="socialRelation";
       dtn.ExecuteProtocol();
    } 


 // CLEAR SIMULATION
    else if (buttonname.equals("Clear"))
    {
      updateInfo=new UpdateInformation();
      updateInfo.ClearSettings();
    }
 // REFRESH SIMULATION
    else if(buttonname.equals("Refresh"))
    {
      updateInfo=new UpdateInformation();
      updateInfo.RefreshSettings();

    }
 // RUN SIMULATION
  else if(buttonname.equals("Run"))
  {
	    RoutingProtocol.metrixArray(dtnrouting.TOTAL_SIMULATION_RUNS);
	  	//FOR SOME ROUTING PROTOCOLS
        if(dtnrouting.ob!=null)
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
                
              RoutingProtocol.index=0;

             //Number of packet copies generated when Spray and WaitB is selected
             if(dtnrouting.protocolName.equals("Spray&WaitB"))
             {
               rand=new Random();
               double r;  //declare double variable r
               for(int k=0;k<dtnrouting.Sources.size();k++)
               {
	               r=Math.log10(dtnrouting.allNodes.size())/Math.log10(2);    //random function set the value of r between 0 and 1
	               int l=(int)(Math.floor(r));  //multiply r with 3 so that the power of 2 go to 3 and then ceil it
	               l=rand.nextInt(l+1);
	               dtnrouting.arePacketsDelivered.get(k).packetLoad=(int)Math.pow(2,l); //take r as power of 2 and assign it to NoDuplicate
	               dtnrouting.Sources.get(k).packetCopies.put(dtnrouting.arePacketsDelivered.get(k).packetName,dtnrouting.arePacketsDelivered.get(k).packetLoad );
	               dtnrouting.CommentsTA.append("\nCopies Generated by "+dtnrouting.Sources.get(k).name+" : "+dtnrouting.arePacketsDelivered.get(k).packetLoad);}}
             
             //Number of packet copies generated when Spray and WaitB is selected
             if(dtnrouting.protocolName.equals("Spray&WaitN"))
             {
              rand=new Random();

               for(int k=0;k<dtnrouting.Sources.size();k++)
               {

               dtnrouting.arePacketsDelivered.get(k).packetLoad=rand.nextInt( dtnrouting.allNodes.size())+1; //random function set the value of r between 1 and 10
               dtnrouting.Sources.get(k).packetCopies.put(dtnrouting.arePacketsDelivered.get(k).packetName,dtnrouting.arePacketsDelivered.get(k).packetLoad );
               dtnrouting.CommentsTA.append("\nCopies Generated by "+dtnrouting.Sources.get(k).name+" : "+dtnrouting.arePacketsDelivered.get(k).packetLoad);}}
          
            // SET THE SIMULATION DELAY
            dtnrouting.delay=0;
            dtnrouting.isRun=true;

    }}
 
 // PERFORMANCE TABLE AND CHART
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
