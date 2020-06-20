
//******************************************************************************
//PACKAGE NAME

    package DTNRouting;

//******************************************************************************
//IMPORT CLASSES

    import java.awt.image.BufferedImage;
    import java.awt.*;
    import java.applet.*;
    import java.awt.Panel;
    import java.io.IOException;
    import java.util.*;
    import javax.swing.*;
    import javax.swing.border.Border;
    import javax.swing.border.LineBorder;
    import AdapterPackage.*;
    import RoutingProtocols.*;
    import java.util.logging.Level;
    import java.util.logging.Logger;

//******************************************************************************
//MAIN APPLET CLASS

public class dtnrouting extends Applet implements Runnable
{
	private static final long serialVersionUID = 1L;
	// VARIABLES USED THROUGHOUT THE SIMULATION
    public int i, radio;
    public static long simulationTime = 0;
    //  source and destination indices declare static and other parameters are initially 0
    public static int dataset_simulation_index=0, s_index=0, d_index=0, latency=0, bandwidth=0, load=0, DR=0, NoDuplicate, delay=0, appletWidth, appletHeight;
    // dimensions of applet parameters
    public static int width, height, x_start, y_start, first_regular_node_index=0;
    
    // PERFORMANCE METTRICS
    // After multiple simulation averaging the results of the three metrics
    public static int latency_avg=0, load_avg=0, bandwidth_avg=0, packetCounter=0, DR_avg=0, nodecount=0;
   
    // Variables related to movement speeds
    public static boolean random_movement=false, x_reached=false, y_reached=false;
    public static boolean isdelivered=false, SIMULATION_ENDED=false, isRun=false;
    public static String  movementtype="Random", protocolName="";
    public static int     nodeNumber=-1, THIS_SIMULATION=4, TOTAL_SIMULATION_RUNS=4;
	public static int     n1_neighborhood[][], n2_neighborhood[][]; //when there is a contact between two nodes
	public static double  linkCapacities[][]; // if contact present then link capacity in bandwidth
    //RoutingProtocol.metrixArray(this.Sim); // USED TO ASSING ARRAYS OF ROUTTING PROTOCOL WHEN SIM IS HIGH
    
    //******************************************************************************
    //DIFFERENT OBJECTS
    public static RoutingProtocol ob;  //create object of routing protocol
    public static NodeMovement nodemovement;
    public static double[][] p;    //predictability value
    Random rand=new Random();
    Graphics graphics;
    private Rectangle rect=null;
    PlayField playField=new PlayField();
    UpdateInformation updateInformation=new UpdateInformation();

//******************************************************************************

//Set layout for panel
    BorderLayout bl =new BorderLayout(10,10);    //Create object of layout
    //TOP AND LEFT PANEL
    Panel p1=new Panel();
    Panel p2=new Panel();
    static String s;
    BufferedImage  bf = new BufferedImage(800,600, BufferedImage.TYPE_INT_RGB);

// MENU BARS
    JMenuBar jmb=new JMenuBar(); // Menu bar containing menus and menu items

//Menus and menu items in menu bar jmb
    JButton nodeMenu=new JButton("Node");
    JButton packetMenu=new JButton("Packet");
    JMenu   routingMenu=new JMenu("Routing Protocol");

//  Contact oblivious dtn routing protocols
    JMenuItem DDRP=new JMenuItem("Direct Delivery");
    JMenuItem FC=new JMenuItem("First Contact");
    JMenuItem ERP=new JMenuItem("Epidemic");
    JMenuItem SnWB=new JMenuItem("Spray&Wait Binary");
    JMenuItem SnWN=new JMenuItem("Spray&Wait Normal");
    JMenu contactOblivious=new JMenu("Contact Oblivious");

// History-base routing protocols
    JMenuItem PRoPHET=new JMenuItem("PRoPHET");
    JMenuItem NECTAR=new JMenuItem("NECTAR");
    JMenuItem MPRoPHET=new JMenuItem("MPRoPHET");
    JMenuItem CAoICD=new JMenuItem("CAoICD");
    JMenuItem Fresh=new JMenuItem("FRESH");
    JMenu historyBased=new JMenu("History Based");

//  Routing protocols based on social relationships
    JMenuItem SimBet=new JMenuItem("SimBet");
    JMenuItem BubbleRap=new JMenuItem("BubbleRap");
    JMenuItem CHRP=new JMenuItem("CHRP");
    JMenu socialRShip=new JMenu("Social Relation");

//Node Movement Models
    JMenu nm_model=new JMenu("Movement Model");
    public static JMenuItem nm_random=new JMenuItem("Random");
    public static JMenuItem nm_prandom=new JMenuItem("Pseudorandom");
    public static JMenuItem nm_ds=new JMenuItem("Dataset");
    JMenuItem nm_crossroads=new JMenuItem("Cross Roads");

// Result MenuItem
    JMenu viewResults=new JMenu("View Resluts");
    JMenuItem performance=new JMenuItem("Performance Table");
    JMenuItem chart=new JMenuItem("Bar Chart");

//******************************************************************************
//Image Icons and RestButtons
    ImageIcon refreshIcon=new ImageIcon("refresh.png");
    ImageIcon clearIcon=new ImageIcon("clear.png");
    ImageIcon runIcon=new ImageIcon("run.png");
    ImageIcon map;
    Border refreshBorder = new LineBorder(Color.lightGray, 1);
    JButton refresh=new JButton(refreshIcon);
    Border clearBorder = new LineBorder(Color.lightGray, 1);
    JButton clear=new JButton(clearIcon);
    Border runBorder = new LineBorder(Color.lightGray, 1);
    JButton run=new JButton(runIcon);

//******************************************************************************

//Array Lists for storing different values
    public static ArrayList<Node>   allNodes=new ArrayList<Node>();              // contains all nodes i.e source + destination + relay
    public static ArrayList<Packet> arePacketsDelivered=new ArrayList<Packet>();
    public static ArrayList<Node>   Sources=new ArrayList<Node>();				 // contains only source nodes
    public static ArrayList<Node>   Destinations=new ArrayList<Node>();			 // contains only destination nodes

//******************************************************************************

//LABELS FOR COMPONENTS
    Label hd=new Label("SIMULATION OF AD HOC NEWORK" , Label.CENTER);
    Label rhist=new Label("ROUTING HISTORY" ,Label.CENTER);//create label on p2
    Label comments=new Label("Packet Transition Process" ,Label.LEFT);//create label on p2
    Label situation=new Label("Contact Opportunities" ,Label.LEFT);//create label on p2
    /*Label NoSimulations=new Label("Simulation(s)", Label.LEFT); //it sets number of simulations*/    
    Label rDetail=new Label("End Node Details", Label.CENTER);

//******************************************************************************
// TEXT AREAS USED IN SECOND PANEL
    public static TextArea CommentsTA=new TextArea(" ");						//create Textarea on p2
    public static TextArea currentSituatonTA=new TextArea(" ");					//create Textarea on p2
    public static TextArea tDetail=new TextArea("Src    Dst    Packet");   //create Textarea on p2

//******************************************************************************

//Called when an Applet starts execution

//******************************************************************************

@Override
public void init()
{
        setLayout(bl);      //set border layout
        setParameters();    //set parameters for GUI
        addComponents_Panel1();
        addComponents_Panel2();
        
}

//******************************************************************************

public void addComponents_Panel1()
{
        p1.setLayout(new GridLayout(2,1));

        //set color and font for heading on p1
        hd.setForeground(Color.black);
        hd.setFont(new Font("San Serif", Font.BOLD, 16));

        //Add menus in node Menu
        nodeMenu.setSize(10, 10);
        nodeMenu.setBorderPainted(false);
        nodeMenu.setContentAreaFilled(false);
        nodeMenu.setOpaque(false);
        nodeMenu.setFont(new Font("Dialog",Font.PLAIN,10));
        nodeMenu.addActionListener(new MyActionAdapter(this)); // when clicked on Node Button, it opens

        //Add packet menu  
        packetMenu.setSize(10, 10);
        packetMenu.setBorderPainted(false);
        packetMenu.setContentAreaFilled(false);
        packetMenu.setOpaque(false);
        packetMenu.setFont(new Font("Dialog",Font.PLAIN,10));
        packetMenu.addActionListener(new MyActionAdapter(this));
                    
        // Add contact oblivious routing protocols in contactOblivous menu item
        DDRP.setFont(new Font("Dialog",Font.PLAIN,10));
        FC.setFont(new Font("Dialog",Font.PLAIN,10));
        ERP.setFont(new Font("Dialog",Font.PLAIN,10));
        SnWB.setFont(new Font("Dialog",Font.PLAIN,10));
        SnWN.setFont(new Font("Dialog",Font.PLAIN,10));
        contactOblivious.add(DDRP);
        contactOblivious.add(FC);
        contactOblivious.add(ERP);
        contactOblivious.add(SnWB);
        contactOblivious.add(SnWN);

        //Add history based dtn routing protocols in historyBased menu item
        PRoPHET.setFont(new Font("Dialog",Font.PLAIN,10));
        MPRoPHET.setFont(new Font("Dialog",Font.PLAIN,10));
        CAoICD.setFont(new Font("Dialog",Font.PLAIN,10));
        Fresh.setFont(new Font("Dialog",Font.PLAIN,10));
        historyBased.add(PRoPHET);
        historyBased.add(MPRoPHET);
        historyBased.add(CAoICD);
        historyBased.add(Fresh);

        //Add social relationship based dtn routing protocols in socialRShip menu item
        SimBet.setFont(new Font("Dialog",Font.PLAIN,10));
        BubbleRap.setFont(new Font("Dialog",Font.PLAIN,10));
        socialRShip.add(SimBet);
        socialRShip.add(BubbleRap);

        //Setting Font Size and Adding contactOblivious, historyBased, socialRShip menu items to routing menu
        routingMenu.setFont(new Font("Dialog",Font.PLAIN,10));
        contactOblivious.setFont(new Font("Dialog",Font.PLAIN,10));
        historyBased.setFont(new Font("Dialog",Font.PLAIN,10));
        socialRShip.setFont(new Font("Dialog",Font.PLAIN,10));
        routingMenu.add(contactOblivious);
        routingMenu.add(historyBased);
        routingMenu.add(socialRShip);

        //Action Listener of contact oblivious routing protocols
        DDRP.addActionListener(new MyActionAdapter(this));
        FC.addActionListener(new MyActionAdapter(this));
        ERP.addActionListener(new MyActionAdapter(this));
        SnWB.addActionListener(new MyActionAdapter(this));
        SnWN.addActionListener(new MyActionAdapter(this));
        
        //Action Listener of contact Based routing protocols
        PRoPHET.addActionListener(new MyActionAdapter(this));
        MPRoPHET.addActionListener(new MyActionAdapter(this));
        CAoICD.addActionListener(new MyActionAdapter(this));
        Fresh.addActionListener(new MyActionAdapter(this));
        
        //Action Listener of social relationship based routing protocols
        SimBet.addActionListener(new MyActionAdapter(this));
        BubbleRap.addActionListener(new MyActionAdapter(this));
        //CHRP.addActionListener(new MyActionAdapter(this));

        //Add different movement models in nm_model menu
        nm_model.setFont(new Font("Dialog",Font.PLAIN,10));
        nm_random.setFont(new Font("Dialog",Font.PLAIN,10));
        nm_model.add(nm_random);
        nm_random.addActionListener(new MyActionAdapter(this));
        nm_prandom.setFont(new Font("Dialog",Font.PLAIN,10));
        nm_model.add(nm_prandom);
        nm_prandom.addActionListener(new MyActionAdapter(this));
        nm_ds.setFont(new Font("Dialog",Font.PLAIN,10));
        nm_model.add(nm_ds);
        nm_ds.addActionListener(new MyActionAdapter(this));

        //Add Results Menu and its items
        viewResults.setFont(new Font("Dialog",Font.PLAIN,10));
        performance.setFont(new Font("Dialog",Font.PLAIN,10));
        viewResults.add(performance);
        performance.addActionListener(new MyActionAdapter(this));
        chart.setFont(new Font("Dialog",Font.PLAIN,10));
        viewResults.add(chart);
        chart.addActionListener(new MyActionAdapter(this));
      
        //setting border and name of "run, refresh, clear" button
        run.setBorder(runBorder);
        run.setActionCommand("Run");
        refresh.setBorder(refreshBorder);
        refresh.setActionCommand("Refresh");
        clear.setBorder(clearBorder);
        clear.setActionCommand("Clear");
        //Register reset  button to the listener
        run.addActionListener(new MyActionAdapter(this));
        refresh.addActionListener(new MyActionAdapter(this));
        clear.addActionListener(new MyActionAdapter(this));
        
        //Adding Menus
        jmb.add(routingMenu);
        jmb.add(nm_model); // Adding movement model menu in menu bar
        jmb.add(nodeMenu);
        jmb.add(packetMenu);    
        jmb.add(viewResults);
        jmb.add(run);
        jmb.add(refresh);
        jmb.add(clear);
        
        p1.add(hd);
        p1.add(jmb);
        p1.setBackground(new Color(0xb0c4de));  //set the background color of p1
        p1.setPreferredSize(new Dimension(appletWidth,40));
        y_start=p1.getHeight()+50;
        height=appletHeight-y_start-130;//70;
        add(p1, BorderLayout.PAGE_START);       
}

//******************************************************************************

public void addComponents_Panel2()
{
        //set layout and dimension of p2
        p2.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
        p2.setFont(new Font("San Serif", Font.BOLD,9));

        //set dimension for comments text area and add on p2
        rhist.setFont(new Font("San Serif", Font.BOLD,11));
  //      rhist.setPreferredSize(new Dimension(140,30));//Set dimension for routing history label
        CommentsTA.setPreferredSize(new Dimension(140,150));
        currentSituatonTA.setPreferredSize(new Dimension(140,150));
        tDetail.setPreferredSize(new Dimension(140,150));

        // Add components to panel p2
        p2.add(rhist);
        p2.add(comments); //add comments text area on p2
        p2.add(CommentsTA);
        p2.add(situation); //add current situation text area on p2
        p2.add(currentSituatonTA);
        p2.add(rDetail);
        p2.add(tDetail);
        
        p2.setPreferredSize(new Dimension(140, appletHeight));
        //Setting parameters for graphics
        x_start=p2.getWidth()+150;
        width=appletWidth-x_start-10;
        //setting color of panels
        p2.setBackground(new Color(0xb0c4de));  //set the background color of p2
        // add the panels on different region
        add(p2, BorderLayout.WEST);
}

//******************************************************************************

public void setParameters()
{
        setBackground(Color.white);  //set the rectangle color
        //Create an object of NodeMovement Class
        nodemovement = new NodeMovement();
        //Reset dimensions of width and height
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        appletHeight=(int)dim.getHeight();
        appletWidth=(int)dim.getWidth();
        this.setSize(new Dimension(appletWidth,appletHeight));
}

//******************************************************************************

//start a thread by adding its run method
@Override
public void start ()
{
         Thread th = new Thread (this);
         th.start();
}

//******************************************************************************

//define the code that constitutes the new thread
public void run()
{
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        while (true) {
        if(isRun==true)
            try { nextPositionForMovement(); } 
            catch (IOException ex) { Logger.getLogger(dtnrouting.class.getName()).log(Level.SEVERE, null, ex); }
            repaint();

                        try
                        { Thread.sleep(0,500); }
                        catch (InterruptedException ex) { }
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        }
}

//******************************************************************************

//Selects a ROUTING protocol to execute
public void ExecuteProtocol()
{
		    isdelivered=false;
		//    dtnrouting.CommentsTA.insert(protocolName,0);
		    if(protocolName.equals("Direct Delivery"))      ob = new DirectDelivery();		
		    else if (protocolName.equals("First Contact"))  ob = new FirstContact();		  
		    else if(protocolName.equals("Epidemic"))        ob = new Epidemic();
		    else if(protocolName.equals("Spray&WaitB"))     ob = new SprayAndWaitB();	      
		    else if(protocolName.equals("Spray&WaitN"))     ob = new SprayAndWaitN();	   
		    else if(protocolName.equals("PRoPHET"))         ob = new PRoPHET();	  
		    else if(protocolName.equals("CAoICD"))          ob = new CAoICD();	
		    else if(protocolName.equals("FRESH"))           ob = new FRESH();	   
		    else if(protocolName.equals("BubbleRap"))       ob = new BubbleRap();		 
	   //   else if(protocolName.equals("SimBet"))          ob = new SimBet(); 	
		    
  }

//******************************************************************************
//Calls paint()
@Override
public void update(Graphics g){
            paint(g);
}

//******************************************************************************

@Override
public void paint(Graphics g){
 //resizes play fiEld dimensions accordingly and calls animation() function
        if(!getBounds().equals(rect)){
                rect = this.getBounds();
                bf   = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        try{
        animation(bf.getGraphics());
        g.drawImage(bf,0,0,null);
        }catch(Exception ex){ }
} 

//******************************************************************************
//Draws graphics in play field
public void animation(Graphics g)
{
       Graphics2D g2 = (Graphics2D)g;
       g2.setStroke(new BasicStroke(3));
       g.setColor(Color.WHITE);
       g.fillRect(0, 0, this.getWidth(), this.getHeight());
       g.setColor(Color.RED);
       g2.drawRect(x_start, y_start, width, height);
       //for drawing nodes and the packets that they hold
       playField.drawNodesPackets(g);

       //Until destination does not get packet the transfer of message carries on
       if(SIMULATION_ENDED==true)
         updateInformation.simulationSettings(this);
     
       if(isRun) 
       {
       // Update the TTL field of all packets along with latency of the packet
            UpdateTTLandLatency();
            playField.FindNeighborhoods();
            //Stores the time units elapsed in the simulation environment
            simulationTime+=1;
       }
}

//******************************************************************************

//Update TTL and packet Latency
public void UpdateTTLandLatency()
{
	   
        delay=delay+1;
        for(int h=0;h < arePacketsDelivered.size();h++)
        {
            Packet packetObj=arePacketsDelivered.get(h);
            if(packetObj.packetTTL>0) {
                packetObj.packetTTL-=1;}
            if(packetObj.ispacketDelivered==false)
                packetObj.packetLatency=delay;
        }
        
}

//******************************************************************************

public void nextPositionForMovement() throws IOException
{
	//NODE MOVEMENT
		if(movementtype.equals("Random"))
	    for(int i=0; i< allNodes.size();i++)
	    allNodes.get(i).node_nm.RandomMovement(allNodes.get(i));
	
	    else if(movementtype.equals("Pseudorandom"))
	    	 for(int i=0; i< allNodes.size();i++)
	    		    allNodes.get(i).node_nm.Follow_PseudoRandomPath(allNodes.get(i));
	   
	    else if(movementtype.equals("Dataset"))
	    	 for(int i=0; i< allNodes.size();i++)
	    		    allNodes.get(i).node_nm.Follow_DatasetPath(allNodes.get(i));
	
}
//******************************************************************************
}//END OF CLASS

