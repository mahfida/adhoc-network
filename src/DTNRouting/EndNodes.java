//PACKAGE NAME
package DTNRouting;

//IMPORT PCKAGES
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import java.util.Random;

//******************************************************************************

//START OF CLASS ENDNODES
public class EndNodes implements ItemListener, ActionListener
{
    //Instance variables
    String s_nodename="",d_nodename="";
    Random rand=new Random();
    JFrame jf=new JFrame("End Nodes");

    //Labels
    Label lsource=new Label("Source Node");
    Label ldest=new Label("Dest. Node");

    //Choices
    Choice csource=new Choice();
    Choice cdest=new Choice();
    Choice cnType=new Choice();

    //Buttons
    Button ok=new Button("OK");
    Button close=new Button("Close");
    Button clear=new Button("Clear");

    //Node Objects
    Node sourceNode=new Node();
    Node destNode=new Node();
//*****************************************************************************

//CONSTRUCTOR OF END NODES
public EndNodes()
{
   jf.setLayout(new GridLayout(4,2,2,2));
   int m=dtnrouting.nodeArray.size();

   //Adding options for source and destination nodes
   for(int i=0;i<m;i++)
    {
        Node node=new Node();
        node=dtnrouting.nodeArray.get(i);
        if(node.name.substring(0,1).equals("R"))
        {
            csource.add(node.name); // adding all the regular nodes in source choice box
            cdest.add(node.name);
        }
    }

    csource.addItemListener(this);
    cdest.addItemListener(this);

    //Adding controls to the frames
    jf.add(lsource);            jf.add(csource);
    jf.add(ldest);              jf.add(cdest);
    jf.add(ok);                 jf.add(close);      jf.add(clear);

    //Adding action listeners for buttons
    ok.addActionListener(this); close.addActionListener(this);  clear.addActionListener(this);

    //Showing frame of End Nodes
    jf.setSize(new Dimension(200,200));
    jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    jf.setResizable(false);
    jf.setVisible(true);
}

//*****************************************************************************

public void itemStateChanged(ItemEvent e)
{
    Object o=e.getSource();
    //Remove the selected source node from destination choice list
    if(o.equals(csource))
    {
       cdest.remove(csource.getSelectedItem());
    }
}

//*****************************************************************************

public void actionPerformed(ActionEvent e)
{
    String action;
    action=e.getActionCommand();

// Clear the text area of number of nodes
    if(action.equals("Close"))
         jf.dispose();

//Take source and destination node
    else if(action.equals("OK"))
    {
         s_nodename=csource.getSelectedItem();
         d_nodename=cdest.getSelectedItem();
         int m=dtnrouting.nodeArray.size();
    
         for(int i=0;i<m;i++)
         {
            Node node=new Node();
            node=dtnrouting.nodeArray.get(i);
            if(node.name.equals(s_nodename))
            {
                sourceNode = node;
                dtnrouting.Sources.add(node);
            }
            else if(node.name.equals(d_nodename))
            {
                destNode = node;
                dtnrouting.Destinations.add(node);
            }
         }

         dtnrouting.tDetail.append("\n"+sourceNode.name+"\t"+destNode.name);
         ResetPerimeters();
         //Enable Create Bundle Menu
         dtnrouting.createBundle.setEnabled(true);
    }

//Take source and destination node
    else if(action.equals("Clear"))
       ResetPerimeters();
}

//*****************************************************************************

//RESET PERIMETERS OF END NODES TO INITIAL VALUES
public void ResetPerimeters()
{
    // Refresh the values
    cdest.removeAll();
    int m=dtnrouting.nodeArray.size();
    for(int i=0;i<m;i++)
    {
        Node node=new Node();
        node=dtnrouting.nodeArray.get(i);
        if(node.name.substring(0,1).equals("R"))
           cdest.add(node.name); // adding all the regular nodes in destination choice box
    }
    csource.select(0);
    cdest.select(0);
}

//*****************************************************************************

//REMOVE VALUES IN CURRENT SOURCE AND DESTINATION INSTANCE VARIABLES
public void ClearValues()
{
    //clearing the source and destination values
    dtnrouting.s_index=0;
    dtnrouting.d_index=0;
    ResetPerimeters();
}
//*****************************************************************************

} //END OF ENDNODES CLASS
