//PACKAGE NAME
package DTNRouting;

//IMPORT PACKAGES
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JFrame;


//------------------------------------------------------------------------------
//START OF CLASS packet
public class Packet implements ActionListener
{
//Instance Variables
    public Node destNode=new Node(); //destination node of the packet
    public Node sourceNode=new Node(); //source node of the packet
    public int packetTTL,maxTTL;
    public int packetSize,packetLoad=1,packetBandwidth=0,packetLatency=0,packetDelivered=0;
    public static int packetID;
    public boolean ispacketDelivered=false,isTTLExpired=false,isLargeSize=false;
    public String packetName,name;
    public JFrame jf=new JFrame("Create packet");
    public int num_packets=0;
    public Label lpacketNumber=new Label("No. of packets");
    public TextField tpacketNumber=new TextField("");
    public Label lpacketTTL=new Label("TTL Value");
    public TextField tpacketTTL=new TextField("");
    public Label lpacketSize=new Label("Size (MB)");
    public TextField tpacketSize=new TextField("");
    public Button ok=new Button("OK");
    public Button close=new Button("Close");
    public String endNodesRegion="Same";
    public Color packet_color; // so to differentiate between packets
    Random rand=new Random();
    dtnrouting dtn=new dtnrouting();


//******************************************************************************

public Packet()
{    }

//******************************************************************************

public void refreshPacketSettings()
{
    ispacketDelivered=false;
    packetLoad=1;
    packetTTL=this.maxTTL;
    packetLatency=this.packetBandwidth=packetDelivered=0;
    packetDelivered=0;
    isLargeSize=false;
    isTTLExpired=false;

}

@Override
public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
	
}

//******************************************************************************

}//END OF packet CLASS
