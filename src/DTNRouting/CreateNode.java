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


import java.util.ArrayList;
import java.util.Random;


//*****************************************************************************
// START OF CREATENODE CLASS
public class CreateNode extends dtnrouting  implements ItemListener, ActionListener, TextListener
{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Instance variables
    public Random randObj=new Random();
    public JFrame jf=new JFrame("Create Node");
    public Label nodeType=new Label("User Nodes");
    public Label nodeS=new Label("Number of Nodes");
    public Label nodesubcategory=new Label("Sub category");
    public Choice subcategory=new Choice();
    public Label sourceNodes= new Label("Source Nodes");
    
    public TextField source_node= new TextField("0");
    public Label desregular_node= new Label("Dest. Nodes");
    public TextField dest_node= new TextField("0");
    public TextField regular_node=new TextField("0");
    
    public Label lspeed=new Label("Speed(m/s)");
    public Choice cspeed=new Choice();
    public Label radiorange=new Label("Radio Range(m)");
    public Choice cradiorange=new Choice();
    public Label queuesize=new Label("QueueSize(MB)");
    public Choice cqueuesize=new Choice();
    public Button Add=new Button("Add");
    public Button Close=new Button("Close");
    public Choice cnType=new Choice();
    
//In order to pass values to the object of CreateNode`
    public int numberofnodes, speedofnode, radiorangeofnode;

//other variables
    public String nameofnode;

//******************************************************************************

//CONSTRUCTOR
public CreateNode()
{}

public void GenerateFrame() {
   jf.setLayout(new GridLayout(10,2,5,5));

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
    subcategory.add("Driving"); 
    subcategory.add("Cycling"); 
    subcategory.add("Walking/Running");
    subcategory.add("Static");
    
         
    //Components in Frame window
   
    dest_node.setEnabled(false);
    jf.add(sourceNodes); 		jf.add(source_node); //source nodes
    jf.add(nodeType);           jf.add(cnType); //end nodes
    jf.add(nodesubcategory);    jf.add(subcategory);
    jf.add(nodeS);              jf.add(regular_node);
    jf.add(queuesize);          jf.add(cqueuesize);
    jf.add(lspeed);             jf.add(cspeed);
    jf.add(radiorange);         jf.add(cradiorange);
    jf.add(desregular_node); 	jf.add(dest_node); //source nodes
    jf.add(Add);                jf.add(Close);

//registering events
    subcategory.addItemListener(this);
    source_node.addTextListener((TextListener) this);
    dest_node.addTextListener((TextListener) this);
    cnType.addItemListener(this);
    Add.addActionListener(this);
    Close.addActionListener(this);
    regular_node.addTextListener((TextListener) this);

//Frame metrics
    jf.setSize(new Dimension(300,300));
    jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    jf.setVisible(true);
    jf.setResizable(false);

}

//******************************************************************************

public void itemStateChanged(ItemEvent e)
{
        //A regular node can be an end device, it can be either held by a
        //a pedestrian or installed by a car or tram
        if(cnType.getSelectedItem().equals("Regular"))
        {
            nodeS.setEnabled(true);                regular_node.setEnabled(true);
            dest_node.setEnabled(true);
            nodesubcategory.setEnabled(true);      subcategory.setEnabled(true);
        }
        
       //Movement speeds
       if(subcategory.getSelectedItem().equals("Walking/Running"))
       {
    	    cspeed.removeAll();
            for(int j=1;j <= 5;j=j+1)
                cspeed.add(j+"");
       }else if(subcategory.getSelectedItem().equals("Cycling"))
       {
    	    cspeed.removeAll();
            for(int j=10;j <= 25;j=j+2)
                cspeed.add(j+"");
       }else if(subcategory.getSelectedItem().equals("Driving"))
       {
    	    cspeed.removeAll();
            for(int j=40;j <= 80;j=j+10)
                cspeed.add(j+"");
       }else if(subcategory.getSelectedItem().equals("Static")){
    	    cspeed.removeAll();
        	cspeed.add(0+"");
       }

}

//******************************************************************************

public void actionPerformed(ActionEvent e)
{

 String action;
 action=e.getActionCommand();

 if(action.equals("Close"))  jf.dispose();

 //Set metrics of new node
 else if (action.equals("Add"))
 {
     // THE SOURCE NODES
    	 	 if(source_node.getText().equals("0"))
             JOptionPane.showMessageDialog(jf,"Put number of nodes");
    	 	 else
    	 	 {
		              dest_node.setEnabled(true);
		              for (int l=0;l<Integer.parseInt(source_node.getText());l++)
		              {
		              Node node=new Node();
		              Node.ID_INCREMENTER+=1;
		              node.ID=Node.ID_INCREMENTER;
		              node.name="S"+node.ID;
		              node.speed=0;
		              dtnrouting.Sources.add(node);
		              node.setRadioRange(5);
		              node.wholeQueueSize=node.queueSizeLeft=10;
		              dtnrouting.allNodes.add(node);
		              }
              }
    	 	 
           
    	   // THE USER NODES (REGULAR)
    	   if(regular_node.getText().equals("0"))
               JOptionPane.showMessageDialog(jf,"Put number of nodes");
           else
           {

                for (int l=0;l<Integer.parseInt(regular_node.getText());l++)
                {
                Node node=new Node();
                Node.ID_INCREMENTER+=1;
	            node.ID=Node.ID_INCREMENTER;
		        node.name="R"+ node.ID;
                node.speed=Integer.parseInt(cspeed.getSelectedItem());
                nameofnode=cnType.getSelectedItem();
                node.setRadioRange(Integer.parseInt(cradiorange.getSelectedItem()));
                node.wholeQueueSize=node.queueSizeLeft=Integer.parseInt(cqueuesize.getSelectedItem());
                dtnrouting.allNodes.add(node);
                }          
		                 
		    }
              
    	   // THE DESTINATION NODES: Choose randomly from regular user nodes
    	   if(dest_node.getText().equals("0"))
             JOptionPane.showMessageDialog(jf,"Put number of nodes");
    	   else
    		 {
    			 
    		  // Randomly choose the destination nodes
    			 int total_size = dtnrouting.allNodes.size();
    			 int s=Integer.parseInt(dest_node.getText());
    			 if(s > total_size)
    				s = total_size;
    			 ArrayList<Integer> num = new ArrayList<Integer>();
    			 
    			 for (int i=0; i<s; i++ ) {
	    				int rand_number = rand.nextInt(total_size);
	    				while(num.contains(rand_number)==true) 
	    					  rand_number = rand.nextInt(total_size); 
    			         num.add(rand_number);
    			         Node node=dtnrouting.allNodes.get(rand_number);
    			         if(node.name.substring(0,1).equals("R"))
    			        	 node.name = "D"+node.name.substring(1,2); //Rename it.
    			         else continue;
    			         dtnrouting.Destinations.add(node);
    			 	}
    	      }
        }	   
    	  
        else if (action.equals("Clear"))  
        	Resetmetrics();


}

//******************************************************************************

//RESET metrics OF CREATE NODE CLASS
public void Resetmetrics() // reset metrics of a node
{
     regular_node.setText("0");             cnType.select(0);
     cradiorange.select(0);                 cspeed.select(0);
     cqueuesize.select(0);					source_node.setText("0");
     dest_node.setText("0");
     nodeS.setEnabled(true);                regular_node.setEnabled(true);
    
             
}

//******************************************************************************

 public void textValueChanged(TextEvent e)
{
   if(e.getSource()==regular_node)
     {
       try{
         Integer.parseInt(regular_node.getText());
         }catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(jf, "Put only integer values");
                 regular_node.setText("0");     }
     }
}
//******************************************************************************

} //END OF CREATENODE CLASS