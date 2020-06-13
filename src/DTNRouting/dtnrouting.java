
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
    import javax.swing.JMenu.*;
    import AdapterPackage.*;
    import RoutingProtocols.*;
    import MovementPattern.*;
    import java.io.FileNotFoundException;
    import java.util.logging.Level;
    import java.util.logging.Logger;

//******************************************************************************
//MAIN APPLET CLASS

public class dtnrouting extends Applet implements Runnable
{
    // VARIABLES USED THROUGHOUT THE SIMULATION
    public int i,radio;
    public static long simulationTime=0;
    //  source and destination indices declare static and other parameters are initially 0
    public static int  s_index=0, d_index=0,latency=0,bandwidth=0, load=0, DR=0, NoDuplicate,Nectar=0,delay=0,appletWidth,appletHeight;
    // After multiple simulation averaging the results of the three metrics
    public static int latency_avg=0,load_avg=0, bandwidth_avg=0,bundleCounter=0,DR_avg=0;
    // dimensions of applet parameters
    public static int width,height,x_start,y_start, contactDuration[][];
    // other variables
    public static boolean random_movement=false,x_reached=false,y_reached=false,isdelivered=false,isIntersect=false,simEnded=false,isRun=false;
    public static String movementtype=" ", protocolName="", FerryMov="";
    public static int nodeNumber=-1;

//******************************************************************************
//DIFFERENT OBJECTS
    public static RoutingProtocol ob;  //create object of routing protocol
    public static NodeMovement nodemovement;
    public static MapBasedMovement mbm;
    public static SingleFerryMovement singleFerryMovement;
    public static double[][] p;    //predictability value
    Random rand=new Random();
    Graphics graphics;
    private Rectangle rect=null;
    public static Node source=new Node();
    public static Node destination=new Node();
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
    JMenu nodeMenu=new JMenu("Node");
    public static JMenuItem createNode=new JMenuItem("Create Node");
    public static JMenuItem dsNode=new JMenuItem("Dataset Node");
    public static JMenuItem endNodes=new JMenuItem("End Nodes");
    JMenu bundleMenu=new JMenu("Bundle");
    public static JMenuItem createBundle=new JMenuItem("Create Bundle");
    //JMenuItem deleteBundle=new JMenuItem("Delete Bundle");
    JMenu routingMenu=new JMenu("Routing Protocol");

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
    JMenu historyBased=new JMenu("History Based");
    JMenuItem Fresh=new JMenuItem("FRESH");

//  Routing protocols based on social relationships
    JMenuItem SimBet=new JMenuItem("SimBet");
    JMenuItem BubbleRap=new JMenuItem("BubbleRap");
    JMenuItem CHRP=new JMenuItem("CHRP");
    JMenu socialRShip=new JMenu("Social Relation");

// Device Based routing protocols
    JMenu deviceBased=new JMenu("Relay Device");
    JMenuItem fixedRelay=new JMenuItem("Fixed Relay(s)");
    JMenuItem  ferry=new JMenuItem("Ferr(y/ies)");

//Node Movement Models
    JMenu nm_model=new JMenu("Movement Model");
    public static JMenuItem nm_random=new JMenuItem("Random");
    public static JMenuItem nm_prandom=new JMenuItem("Pseudorandom");
    public static JMenuItem nm_ds=new JMenuItem("Dataset");
    public static JMenuItem nm_mapBased=new JMenuItem("MapBased");
    JMenuItem nm_crossroads=new JMenuItem("Cross Roads");

// Settings MenuItem
    static JButton settings=new JButton("Settings");
    JMenu viewResults=new JMenu("View Resluts");
    JMenuItem performance=new JMenuItem("Performance Table");
    JMenuItem chart=new JMenuItem("Bar Chart");


// Graph Selection Scheme
    JMenu snGraph=new JMenu("SN Graph");
    JMenuItem karate=new JMenuItem("Karate Club");
    JMenuItem karate2=new JMenuItem("Ede Betweenness");
    JMenuItem karate3=new JMenuItem("Agglomerative");
   // JMenuItem chart=new JMenuItem("Bar Chart");

//******************************************************************************

// Community Detection Schemes IconButton
    ImageIcon com=new ImageIcon("community.png");
    Border comBorder = new LineBorder(Color.lightGray, 1);
    JButton community=new JButton(com);

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
    public static ArrayList<Node> allNodes=new ArrayList<Node>();
    public static ArrayList<Node> nodeArray=new ArrayList<Node>();
    public static ArrayList<Node> FixedRelayArray=new ArrayList<Node>();
    public static ArrayList<Node> ferryArray=new ArrayList<Node>();
    public static ArrayList<Bundle> areBundlesDelivered=new ArrayList<Bundle>();
    public static ArrayList<Node> Sources=new ArrayList<Node>();
    public static ArrayList<Node> Destinations=new ArrayList<Node>();
    public static ArrayList<Regions> RegionArray=new ArrayList<Regions>();
    public static ArrayList<Regions> RegionwithNodes=new ArrayList<Regions>();

//******************************************************************************

//LABELS FOR COMPONENTS
    Label hd=new Label("DTN-RSIM: SIMULATION OF DTN ROUTING PROTOCOLS" , Label.CENTER);
    Label rhist=new Label("ROUTING HISTORY" ,Label.CENTER);//creat label on p2
    Label comments=new Label("Bundle Transition Process" ,Label.LEFT);//creat label on p2
    Label situation=new Label("Contact Opportunities" ,Label.LEFT);//creat label on p2
    Label NoSimulations=new Label("Simulation(s)", Label.LEFT); //it sets number of simulations
    Label rDetail=new Label("End Node Details", Label.CENTER);

//******************************************************************************
// TEXT AREAS USED IN SECOND PANEL

    public static TextArea CommentsTA=new TextArea(" ");//Create Textarea on p2
    public static TextArea currentSituatonTA=new TextArea(" ");//creat Textarea  on p2
    public static TextArea tDetail=new TextArea("Source    Dest.    Bundle");

//******************************************************************************

//Called when an Applet starts execution

//******************************************************************************

@Override
public void init()

{
       
        setLayout(bl);      //set border layout
        setParameters();    //set parameters for GUI
        addComponents2Panel1();
        addComponents2Panel2();
        mbm=new MapBasedMovement();
        
       
}

//******************************************************************************

public void addComponents2Panel1()
{
        p1.setLayout(new GridLayout(2,1));

        //set color and font for heading on p1
        hd.setForeground(Color.black);
        hd.setFont(new Font("San Serif", Font.BOLD, 16));

        //Adding settings Button with no border
        settings.setSize(10, 10);
        settings.setBorderPainted(false);
        settings.setContentAreaFilled(false);
        settings.setOpaque(false);

        //Add menus in node Menu
        nodeMenu.add(createNode);
        nodeMenu.add(dsNode);
        nodeMenu.setFont(new Font("Dialog",Font.PLAIN,10));
        createNode.setFont(new Font("Dialog",Font.PLAIN,10));
        createNode.addActionListener(new MyActionAdapter(this));
        dsNode.setFont(new Font("Dialog",Font.PLAIN,10));
        dsNode.setEnabled(false);
        dsNode.addActionListener(new MyActionAdapter(this));
        endNodes.setFont(new Font("Dialog",Font.PLAIN,10));
        nodeMenu.add(endNodes);
        endNodes.addActionListener(new MyActionAdapter(this));
        endNodes.setEnabled(false);
        //Add bundle menu
        bundleMenu.add(createBundle);
        bundleMenu.setFont(new Font("Dialog",Font.PLAIN,10));
        createBundle.addActionListener(new MyActionAdapter(this));
        createBundle.setFont(new Font("Dialog",Font.PLAIN,10));
        createBundle.setEnabled(false);
        // Add contact oblivious routing protocols in contactOblivous menu item
        DDRP.setFont(new Font("Dialog",Font.PLAIN,10));
        contactOblivious.add(DDRP);
        FC.setFont(new Font("Dialog",Font.PLAIN,10));
        contactOblivious.add(FC);
        ERP.setFont(new Font("Dialog",Font.PLAIN,10));
        contactOblivious.add(ERP);
        SnWB.setFont(new Font("Dialog",Font.PLAIN,10));
        contactOblivious.add(SnWB);
        SnWB.setFont(new Font("Dialog",Font.PLAIN,10));
        SnWN.setFont(new Font("Dialog",Font.PLAIN,10));
        contactOblivious.add(SnWN);
        contactOblivious.setFont(new Font("Dialog",Font.PLAIN,10));
        //Add contactOblivious menu items to routing menu
        routingMenu.add(contactOblivious);
        routingMenu.setFont(new Font("Dialog",Font.PLAIN,10));

        //Action Listener of contact oblivious routing protocols
        DDRP.addActionListener(new MyActionAdapter(this));
        FC.addActionListener(new MyActionAdapter(this));
        ERP.addActionListener(new MyActionAdapter(this));
        SnWB.addActionListener(new MyActionAdapter(this));
        SnWN.addActionListener(new MyActionAdapter(this));

        //Add history based dtn routing protocols in historyBased menuitem
        historyBased.setFont(new Font("Dialog",Font.PLAIN,10));
        historyBased.add(PRoPHET);
        PRoPHET.setFont(new Font("Dialog",Font.PLAIN,10));
        historyBased.add(NECTAR);
        NECTAR.setFont(new Font("Dialog",Font.PLAIN,10));
        historyBased.add(MPRoPHET);
        MPRoPHET.setFont(new Font("Dialog",Font.PLAIN,10));
        historyBased.add(CAoICD);
        CAoICD.setFont(new Font("Dialog",Font.PLAIN,10));
        historyBased.add(Fresh);
        Fresh.setFont(new Font("Dialog",Font.PLAIN,10));
        //Add contactOblivious menu items to routing menu
        routingMenu.add(historyBased);

        //Add social relationship based dtn routing protocols in socialRShip menuitem
        socialRShip.setFont(new Font("Dialog",Font.PLAIN,10));
        socialRShip.add(SimBet);
        SimBet.setFont(new Font("Dialog",Font.PLAIN,10));
        socialRShip.add(BubbleRap);
        BubbleRap.setFont(new Font("Dialog",Font.PLAIN,10));
        socialRShip.add(CHRP);
        CHRP.setFont(new Font("Dialog",Font.PLAIN,10));
        //Add socialRShip menu items to routing menu
        routingMenu.add(socialRShip);

        //Action Listener of social rship based routing protocols
        SimBet.addActionListener(new MyActionAdapter(this));
        BubbleRap.addActionListener(new MyActionAdapter(this));
        CHRP.addActionListener(new MyActionAdapter(this));



        //Add device based routing protocols
        deviceBased.setFont(new Font("Dialog",Font.PLAIN,10));
        fixedRelay.setFont(new Font("Dialog",Font.PLAIN,10));
        ferry.setFont(new Font("Dialog",Font.PLAIN,10));
        deviceBased.add(fixedRelay);
        deviceBased.add(ferry);
        fixedRelay.addActionListener(new MyActionAdapter(this));
        ferry.addActionListener(new MyActionAdapter(this));

        //Action Listener of contact Based routing protocols
        PRoPHET.addActionListener(new MyActionAdapter(this));
        NECTAR.addActionListener(new MyActionAdapter(this));
        MPRoPHET.addActionListener(new MyActionAdapter(this));
        CAoICD.addActionListener(new MyActionAdapter(this));
        Fresh.addActionListener(new MyActionAdapter(this));

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
        nm_mapBased.setFont(new Font("Dialog",Font.PLAIN,10));
        nm_model.add(nm_mapBased);
        nm_mapBased.setEnabled(false);
        nm_mapBased.addActionListener(new MyActionAdapter(this));

        //Add social network graphs
        snGraph.setFont(new Font("Dialog",Font.PLAIN,10));
        karate.setFont(new Font("Dialog",Font.PLAIN,10));
        karate2.setFont(new Font("Dialog",Font.PLAIN,10));
        karate3.setFont(new Font("Dialog",Font.PLAIN,10));
        snGraph.add(karate);
        snGraph.add(karate2);
        snGraph.add(karate3);

        //Add Results Menu and its items
        viewResults.setFont(new Font("Dialog",Font.PLAIN,10));
        performance.setFont(new Font("Dialog",Font.PLAIN,10));
        viewResults.add(performance);
        performance.addActionListener(new MyActionAdapter(this));
        chart.setFont(new Font("Dialog",Font.PLAIN,10));
        viewResults.add(chart);
        chart.addActionListener(new MyActionAdapter(this));
        settings.setFont(new Font("Dialog",Font.PLAIN,10));
        settings.addActionListener(new MyActionAdapter(this));

        //Add Community Detection Button
        community.setBorder(comBorder);
        community.setActionCommand("Community");

        //setting border and name of run button
        run.setBorder(runBorder);
        run.setActionCommand("Run");

        //setting border and name of refresh button
        refresh.setBorder(refreshBorder);
        refresh.setActionCommand("Refresh");

        //Adding Menus
        jmb.add(settings);
        jmb.add(routingMenu);
        jmb.add(deviceBased);
        jmb.add(nodeMenu);
        jmb.add(bundleMenu);
        jmb.add(nm_model); // Adding movement model menu in menu bar
        jmb.add(snGraph); // Social Network Graph
        jmb.add(viewResults);
        jmb.add(community); // Adding Community Detection Schemes
        jmb.add(run);
        jmb.add(refresh);
        jmb.add(clear);
        

        p1.add(hd);
        p1.add(jmb);
        
        //Setting border and name of clear button
        clear.setBorder(clearBorder);
        clear.setActionCommand("Clear");
   
        //Register reset  button to the listener
        community.addActionListener(new MyActionAdapter(this));
        run.addActionListener(new MyActionAdapter(this));
        refresh.addActionListener(new MyActionAdapter(this));
        clear.addActionListener(new MyActionAdapter(this));
        p1.setBackground(new Color(0xb0c4de));  //set the background color of p1
        p1.setPreferredSize(new Dimension(appletWidth,40));
        y_start=p1.getHeight()+50;
        height=appletHeight-y_start-70;
        add(p1, BorderLayout.PAGE_START);
       
}

//******************************************************************************

public void addComponents2Panel2()
{
        //set layout and dimension of p2
        p2.setLayout(new FlowLayout(FlowLayout.CENTER,2,2));
        p2.setFont(new Font("San Serif", Font.BOLD,9));

        //set dimension for comments text area and add on p2
        rhist.setFont(new Font("San Serif", Font.BOLD,11));
        rhist.setPreferredSize(new Dimension(140,30));//Set dimension for routing history label
        CommentsTA.setPreferredSize(new Dimension(140,120));
        currentSituatonTA.setPreferredSize(new Dimension(140,100));
        tDetail.setPreferredSize(new Dimension(140,100));

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
        setBackground(Color.white);  //set the rectangel color
        //Create an object of NodeMovement Class
        nodemovement=new NodeMovement();
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
        while (true)
        {
        if(isRun==true)

            try {
                
                nextPositionForMovement();
                } catch (IOException ex) {
                Logger.getLogger(dtnrouting.class.getName()).log(Level.SEVERE, null, ex);
                }
                repaint();

                        try
                        {

                        Thread.sleep(0,500);
                        }
                        catch (InterruptedException ex)
                        {

                        }
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        }
}

//******************************************************************************

//Selects a protocol to execute
public void ExecuteProtocol()
{
    isdelivered=false;
    Nectar=0;
    dtnrouting.CommentsTA.insert(protocolName,0);
    if(protocolName.equals("Direct Delivery"))
        ob=new DirectDelivery();

    else if (protocolName.equals("First Contact"))
        ob=new FirstContact();
  
    else if(protocolName.equals("Epidemic"))
        ob=new Epidemic();

    else if(protocolName.equals("Spray&WaitB"))
        ob=new SprayAndWaitB();
      
    else if(protocolName.equals("Spray&WaitN"))
        ob=new SprayAndWaitN();
   
    else if(protocolName.equals("PRoPHET"))
        ob=new PRoPHET();
       
    else if(protocolName.equals("NECTAR"))
        {
            Nectar=1;
            ob=new NECTAR();
        }
  
     else if(protocolName.equals("CAoICD"))
            ob=new CAoICD();

    else if(protocolName.equals("FRESH"))
            ob=new FRESH();
    else if(protocolName.equals("SimBet"));
          //ob=new SimBet();
    else if(protocolName.equals("BubbleRap"))
            ob=new BubbleRap();

    else if(protocolName.equals("CHRP"))
            ob=new CHRP();
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
 //resizes play feild dimensions accordingly and calls animation() function
        if(!getBounds().equals(rect)){
                rect=this.getBounds();

                bf = new BufferedImage( this.getWidth(),this.getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        try{
        animation(bf.getGraphics());
        g.drawImage(bf,0,0,null);
        }catch(Exception ex){

        }
} 

//******************************************************************************
//Draws graphics in playfield
public void animation(Graphics g)
{
       Graphics2D g2 = (Graphics2D)g;
       g2.setStroke(new BasicStroke(3));
       g.setColor(Color.WHITE);
       g.fillRect(0, 0, this.getWidth(), this.getHeight());
       g.setColor(Color.LIGHT_GRAY);

       //Rectangle enclosing the whole playfield
       g2.drawRect(x_start, y_start, width, height);

       //if movement is real
       if((Setting.enviroment.equals("Real Life"))&& (Setting.realType.equals("Map")))
       {
             String mapCity="..\\Maps\\"+Setting.mapCity+".jpg";
             map= new ImageIcon(mapCity); 
             g.drawImage(map.getImage(),x_start, y_start, null);
       }

        //Rectangles showing boundries for the regions
       if(!RegionArray.isEmpty())
       playField.drawRegions(g);
 
        //for drawing nodes and the bundles that they hold
       playField.drawNodesBundles(g);

       //Until destination does not get bundle the transfer of message carries on
       if(simEnded==true)
         updateInformation.simulationSettings(this);
     
       if(isRun)
       {
       // Update the TTL field of all bundles along with latency of the bundle
            UpdateTTLandLatency();
            playField.transferBundle();
            //Stores the time units elapsed in the simulation environment
            simulationTime+=1;
       }
}

//******************************************************************************

//Update TTL and bundle Latency
public void UpdateTTLandLatency()
{
        delay=delay+1;
        for(int h=0;h<areBundlesDelivered.size();h++)
        {
            Bundle bundleObj=areBundlesDelivered.get(h);
            if(bundleObj.bundleTTL>0)
                bundleObj.bundleTTL-=1;
            if(bundleObj.isBundleDelivered==false)
                bundleObj.bundleLatency=delay;
        }
        
}

//******************************************************************************

public void nextPositionForMovement() throws IOException
{
//NODE MOVEMENT
    
     if(movementtype.equals("Random"))
           nodemovement.RandomMovement();

     else if(movementtype.equals("Pseudorandom"))
           nodemovement.PseudoRandomMovement();

     else if(movementtype.equals("Dataset"))
     {
         nodemovement.DatasetMovement();
         nodemovement.newPositions();
     }
   
     else if(movementtype.equals("MapBased"))
     {
            try
            {
                mbm.mapMovement();
            }
            catch (FileNotFoundException ex) {
                Logger.getLogger(dtnrouting.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IOException ex) {
                Logger.getLogger(dtnrouting.class.getName()).log(Level.SEVERE, null, ex);
            }
     }

     else if(movementtype.equals(" ")); //If no movement is specified then nodes will not move
//************************************************************************
 //FERRY MOVEMENT

     //Single ferry with random path
     if(DeviceBasedSettings.ferryType.equals("Single-Ferry"))
      {
         singleFerryMovement=new SingleFerryMovement();

         //If the regions are both static and mobile
         if(DeviceBasedSettings.fDeploymentType.equals("Mobile") )
            singleFerryMovement.randomPath(ferryArray.get(0));

         //if each region is static then ferry will go to each node
         if(DeviceBasedSettings.fDeploymentType.equals("Static") )
            singleFerryMovement.fixedPathwithinRegions(ferryArray.get(0));
      }

     //Pigeon-based ferries
     if(DeviceBasedSettings.ferryType.equals("Pigeon"))
      {
         PigeonMovement pm=new PigeonMovement();
         pm.movementPath();
      }
}
//******************************************************************************
} //END OF dtnrouting CLASS

