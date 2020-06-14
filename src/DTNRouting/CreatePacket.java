//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

//------------------------------------------------------------------------------
//START OF CLASS packet
public class CreatePacket implements ActionListener
{
  public JFrame jf=new JFrame("Create packet");
  public Label lpacketNumber=new Label("No. of packets");
  public TextField tpacketNumber=new TextField("3");
  public Label lpacketTTL=new Label("TTL Value");
  public TextField tpacketTTL=new TextField("1000");
  public Label lpacketSize=new Label("Size (MB)");
  public TextField tpacketSize=new TextField("1");
  public Button ok=new Button("Add");
  public Button close=new Button("Close");
  Random rand=new Random();

//******************************************************************************

public CreatePacket(){}

//******************************************************************************
//packet FRAME GUI
public void GenerateFrame()
{
	  jf.setLayout(new GridLayout(7,2,5,5));
	  jf.add(lpacketNumber);  jf.add(tpacketNumber);
	  jf.add(lpacketTTL);     jf.add(tpacketTTL);
	  jf.add(lpacketSize);    jf.add(tpacketSize);
	  jf.add(ok);             jf.add(close);
	  ok.addActionListener(this);
	  close.addActionListener(this);
	  jf.setSize(new Dimension(200,200));
	  jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	  jf.setResizable(false);
	  jf.setVisible(true);
}

//******************************************************************************

public void actionPerformed(ActionEvent e)
{
 String value=e.getActionCommand();
 String action;
 action=e.getActionCommand();

 if(action.equals("Close"))				jf.dispose();
 else if(value.equals("Add"))			CreateMessageAtSource();
		
 
}    

//******************************************************************************
public void CreateMessageAtSource() {
        for(int d=0; d < dtnrouting.Destinations.size(); d ++) {
        	
        	//Destination chooses its source randomly
        	int source_id = rand.nextInt(dtnrouting.Sources.size());
        	
        	for(int j=0; j< Integer.parseInt(tpacketNumber.getText()); j++) {//number of packets that each source will transmit..
           	   Packet p =new Packet();
           	   Packet.packetID=Packet.packetID+1;
           	     // increment the id of packet and then add it in tpacketNumber
           	    p.packetName="p"+Packet.packetID; //packet Name for reference in code
           	    p.name="p"+(j+1);
           	    p.packet_color=new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
           	    p.packetTTL= p.maxTTL=Integer.parseInt(tpacketTTL.getText());
           	    p.packetSize=Integer.parseInt(tpacketSize.getText());
           	    dtnrouting.arePacketsDelivered.add(p);
        	
              if(dtnrouting.Sources.get(source_id).queueSizeLeft > p.packetSize)
               {    
            	    dtnrouting.Sources.get(source_id).queueSizeLeft-=p.packetSize; //update queue space after putting packet in it
                    dtnrouting.Sources.get(source_id).packetIDHash.add(p.packetName); //Store ID of packet in the source as Hash value
                    dtnrouting.Sources.get(source_id).packetTimeSlots.put(p.packetName,0);
                    dtnrouting.Sources.get(source_id).packetCopies.put(p.packetName,1);
                    dtnrouting.Sources.get(source_id).DestNPacket.put(p,dtnrouting.Destinations.get(d));
                    dtnrouting.tDetail.append("\n "+dtnrouting.Sources.get(source_id).ID+"--"+dtnrouting.Destinations.get(d).ID+" ("+p.name+")");
                 }
              
              else    //If queue of the packet has not enough space to store the new packet then
              dtnrouting.CommentsTA.append("\nSource "+ dtnrouting.Sources.get(source_id).ID+" has not enough space to occupy "+p.name);  
           }  //all packets of the destination assigned to the source of dest.     
	    }     
}

//******************************************************************************
}//END OF packet CLASS
