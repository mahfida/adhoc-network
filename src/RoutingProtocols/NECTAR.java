//******************************************************************************
//PACKAGE

package RoutingProtocols;

//******************************************************************************
//IMPORT FILES

import DTNRouting.*;
import MovementPattern.NodeMovement;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

//******************************************************************************
//NECTAR-A FREQUENCT OF ENCOUNTER BASED ROUTING PROTOCOL

public class NECTAR extends RoutingProtocol implements ActionListener
{
    //Instance Variables
    dtnrouting dtn=new dtnrouting();
    int size;
    double rand;    //creat random number for random movement
    boolean warmFlag=false;
    static int minEpidemicValue,maxEpidemicValue,curTS, currentTime,lemda;
    static double gamma,weight;
    static double[][] CC;
    static double[][] NIndex;
    static double[][] NIndexDash;
    static double[][] updateTS;
    static int Hops[][];
    JFrame jf=new  JFrame("Set NECTAR Perimeters");
    Label lminEV=new Label("Min. Epidemic Value");
    Label lmaxEV=new Label("Max. Epidemic Value");
    Label lgamma=new Label("Aging Constant (0-1)");
    Label lweight=new Label("Weight(0-1)");
    Label lThresholdValue=new Label("Threshold Value");
    TextField tminEV=new TextField("1");
    TextField tmaxEV=new TextField("2");
    TextField tgamma=new TextField("1");
    TextField tweight=new TextField("0.5");
    TextField tThresholdValue=new TextField("2");
    Button ok=new Button("OK");
    Button close=new Button("Close");

//******************************************************************************
//CONSTRUCTOR

public   NECTAR()
{
    jf.setLayout(new GridLayout(6,2,5,5));
    jf.add(lminEV);             jf.add(tminEV);
    jf.add(lmaxEV);             jf.add(tmaxEV);
    jf.add(lgamma);             jf.add(tgamma);
    jf.add(lweight);            jf.add(tweight);
    jf.add(lThresholdValue);    jf.add(tThresholdValue);
    jf.add(ok);                 jf.add(close);
    ok.addActionListener((ActionListener) this);
    close.addActionListener((ActionListener) this);
    jf.setSize(new Dimension(300,300));
    jf.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    jf.setVisible(true);

}

//******************************************************************************
//CONSTRUTOR OVERLOADED

public NECTAR(String str){}

//******************************************************************************

@Override
public void setPerimeters()
{
    size=dtnrouting.allNodes.size();
    NIndex=new double[size][size];
    CC=new double[size][size];
    NIndexDash=new double[size][size];
    updateTS=new double[size][size];
    Hops=new int[size][size];
    for(int m=0;m<size;m++)
       for(int n=0;n<size;n++)
       {
        NIndex[m][n]=0;
        CC[m][n]=0;
        NIndexDash[m][n]=0; // it is NI'
        updateTS[m][n]=0;
       }
    dtnrouting.CommentsTA.append("NECTAR\nWARMUP PERIOD");
}

//******************************************************************************
//CALCULATE DURATION OF A CONTACT

@Override
public void ContactCounter(int x,int y)  //x and y are sender and reciever nodes
{
    CC[y][x]=CC[x][y]=CC[x][y]+1; //contact duration increases incremently
    NIndex[y][x]=NIndex[x][y]=NIndex[x][y]+1; //linear increase in Neghborhood index for the duration of contact
    updateTS[y][x]=updateTS[x][y]=currentTime;
    Hops[y][x]=Hops[x][y]=0;
    for(int m=0;m<(size-1);m++)
        for(int n=m+1;n<size;n++)
            if((n!=x && m!=y)||(n!=y && m!=x))
            {
                if(Hops[m][n]<(dtnrouting.allNodes.size()-2))
                     Hops[n][m]=Hops[m][n]=Hops[m][n]+1; // increases hops as time passes and they are not in contact
            }


}

//******************************************************************************
//INCREASE PROBABILITY OF ENCOUNTERING DESTINATION BY A MUTUAL FRIEND

@Override
public void Transitivity(int x, int y)
{
    for(int m=0;m<size;m++)
     {
      curTS=currentTime;
          if((m!=x) && (m!=y))
          {
             if(NIndex[x][m]>NIndex[y][m])
             {
                      NIndexDash[y][m]=(CC[x][m])/((Hops[x][m]+1)*(Math.pow((curTS-updateTS[x][m]+1),gamma)));
                  if (NIndex[y][m]==0)
                      NIndex[m][y]=NIndex[y][m]=NIndexDash[y][m]; // if node m is not known to y
                  else
                     NIndex[m][y]=NIndex[y][m]=((NIndex[y][m]*weight)+NIndexDash[y][m]/(weight+1));//if node m is known to y  
             }
               else if(NIndex[y][m]>NIndex[x][m])
             {
                   NIndexDash[x][m]=(CC[y][m])/((Hops[y][m]+1)*(curTS-updateTS[y][m]+1));
                    if (NIndex[x][m]==0)
                      NIndex[m][x]=NIndex[x][m]=NIndexDash[x][m]; // if node m is not known to y
                  else
                      //if node m is known to y
                      NIndex[m][x]=NIndex[x][m]=((NIndex[x][m]*weight)+NIndexDash[x][m]/(weight+1));
             }
          }
      }
}//End of Transitivity()

//******************************************************************************

public void Deliver(Node nx,Node ny)    //x and y are intermediet sender and reciever
{
    //stores the time units passed away
    currentTime+=1;
    Transitivity(nx.nodeID-1,ny.nodeID-1);    //if nx and ny comes in contact indirectly, increase there mutual NI
    //Bidirectional connectivity
    DeliverMessage(nx, ny);
    DeliverMessage(ny, nx);
}

//******************************************************************************
//DELIVER MESSAGE

public void DeliverMessage(Node nx, Node ny)
{
if(dtnrouting.isRun==true ){
if(NodeMovement.warmupPeriod==size) //Warming Period finished
  {

      if(!warmFlag)
      {
        for(int h=0;h<dtnrouting.areBundlesDelivered.size();h++)
        {
            Bundle bundleObj=dtnrouting.areBundlesDelivered.get(h);
            bundleObj.bundleTTL=bundleObj.maxTTL;
            bundleObj.bundleLatency=0;
        }
          dtnrouting.CommentsTA.append(" FINISHED ");
          dtnrouting.delay=0;
      }
        warmFlag=true;
        //if nx has bundle and ny has to recieve it
        if(!nx.DestNBundle.isEmpty())
        {
        
         //Update the time spent by bundles within a node nx
       nx. updateBundleTimestamp(nx);
        //Transfer the bundels
       if(nx.queueSizeLeft==0)
       bundleDiscardPolicy(nx);
        for (Iterator<Map.Entry<Bundle,Node>> i = nx.DestNBundle.entrySet().iterator(); i.hasNext(); )
        {
           Map.Entry<Bundle,Node> entry = i.next();
           Bundle bundleObj = entry.getKey();
           Node destNode = entry.getValue();
        
        //If destiantion has not enough size to recieve bundle
        //OR if its TTL is expired, , it bundle cannot be sent

        if(checkTTLandSize(nx,ny,destNode,bundleObj)==true);

        //If destiantion has enough size to recieve bundle
        //and if its TTL is not expired, , it bundle can be sent
        // if contact duration is enough to transfer the message
        else
        if(bundleObj.bundleSize<=dtnrouting.contactDuration[nx.nodeID-1][ny.nodeID-1]){
             
        //If encountered Node has not yet recieved bundle, bundle is yet not delivered,in ny's buffer enough space is free to occupy the bundle
        if((ny.bundleIDHash.contains(bundleObj.bundleName)==false)&&(ny.queueSizeLeft>bundleObj.bundleSize)&&(bundleObj.isBundleDelivered==false))
        {

            if(ny==destNode)  // if ny is destination, give bundle
            {
                    bundleObj.bundleBandwidth+=1; //Since bundle is transfered
                    ny.DestNBundle.put(bundleObj,null);
                    ny.bundleIDHash.add(bundleObj.bundleName);
                    ny.queueSizeLeft-=bundleObj.bundleSize;
                    ny.bundleTimeSlots.put(bundleObj.bundleName, 0);
                    ny.bundleCopies.put(bundleObj.bundleName, 1);
                    bundleObj.isBundleDelivered=true;
                    //update nx
                    nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                    nx.bundleIDHash.remove(bundleObj.bundleName);
                    nx.bundleTimeSlots.remove(bundleObj.bundleName);
                    nx.bundleCopies.remove(bundleObj.bundleName);
                    i.remove();
                   dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
            }
                        
            else if((NIndex[ny.nodeID-1][destNode.nodeID-1]>NIndex[nx.nodeID-1][destNode.nodeID-1]))
            {
                     bundleObj.bundleBandwidth+=1; //Since bundle is transfered
                     ny.DestNBundle.put(bundleObj,destNode);
                     ny.bundleIDHash.add(bundleObj.bundleName);
                     ny.bundleTimeSlots.put(bundleObj.bundleName, 0);
                     ny.bundleCopies.put(bundleObj.bundleName,1);
                     ny.queueSizeLeft-=bundleObj.bundleSize;
                     //update nx
                     nx.queueSizeLeft+=bundleObj.bundleSize; // the whole space
                  //   nx.bundleIDHash.remove(bundleObj.bundleName);
                     nx.bundleTimeSlots.remove(bundleObj.bundleName);
                     nx.bundleCopies.remove(bundleObj.bundleName);
                     i.remove();
                     dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
             }

             
          //Solve the Problem of TTL min and TTL max in Necatar And ProPhet            *
          else if(
          //if maxTTL-cTTL<minEV
            ( ((bundleObj.maxTTL-bundleObj.bundleTTL))<minEpidemicValue) ||
          //OR minEV<maxTTL-cTTL<maxTTL && lemda>node's used space+bundle_size
            (
                (minEpidemicValue<=(bundleObj.maxTTL-bundleObj.bundleTTL))&&
                ((bundleObj.maxTTL-bundleObj.bundleTTL)<maxEpidemicValue)&&
                (lemda>((ny.wholeQueueSize-ny.queueSizeLeft)+bundleObj.bundleSize))
             )
             )
            {
                    bundleObj.bundleBandwidth+=1; //Since bundle is transfered
                    bundleObj.bundleLoad+=1;
                    ny.DestNBundle.put(bundleObj,destNode);
                    ny.bundleTimeSlots.put(bundleObj.bundleName, 0);
                    ny.bundleCopies.put(bundleObj.bundleName,1);
                    ny.bundleIDHash.add(bundleObj.bundleName);

                    ny.queueSizeLeft-=bundleObj.bundleSize;
                    nx.bundleCopies.put(bundleObj.bundleName, nx.bundleCopies.get(bundleObj.bundleName)+1);
                    dtnrouting.CommentsTA.append("\n"+nx.name+" ---> "+ny.name+":"+bundleObj.bundleName);
            }

            }}
       }
    }
  }

    //Check whether forwading of bundles have ended
     checkForwardingEnds();
   }
}//end of Deliver()

//******************************************************************************

public void actionPerformed(ActionEvent e)
{
      String bname=e.getActionCommand();
      if(bname.equals("OK"))
      {
          minEpidemicValue=Integer.parseInt(tminEV.getText());
          maxEpidemicValue=Integer.parseInt(tmaxEV.getText());
          gamma=Float.parseFloat(tgamma.getText());
          weight=Float.parseFloat(tweight.getText());
          lemda=Integer.parseInt(tThresholdValue.getText());
          if(!(gamma>0 && gamma<=1))
              JOptionPane.showMessageDialog(jf, "Put float value between 0 and 1 as aging constant");
          if(!(weight>0 && weight<=1))
              JOptionPane.showMessageDialog(jf, "Put float value between 0 and 1 as weight");
          dtnrouting.Nectar=1;
       }
      if(bname.equals("Close"))
         jf.dispose();
}

//******************************************************************************
//DISCARD AN OLD BUNDLE WHEN BUFFER IS FULL

public void bundleDiscardPolicy(Node nx)
{
    HashMap<String,Integer> bundleTTS=nx.bundleTimeSlots;
    HashMap<String,Integer> bundleCopies=nx.bundleCopies;
    HashMap <Bundle,Integer> bundleAge=new HashMap();
    Bundle maxAgedBundle=null;
    //Adding time slots spent by a bundle in a node to bundleAge
    for (Iterator<Map.Entry<String,Integer>> i = bundleTTS.entrySet().iterator(); i.hasNext(); )
    {
       Map.Entry<String,Integer> entry = i.next();
       String bundleName = entry.getKey();
       int TS = entry.getValue();

       for(int j=0;j<dtnrouting.areBundlesDelivered.size();j++)
        {
          Bundle bObj=dtnrouting.areBundlesDelivered.get(j) ;
          if(bundleName.equals(bObj.bundleName))
              bundleAge.put(bObj, TS);
        }
    }
       //Adding number of copies generated by this node to bundleAge
   for (Iterator<Map.Entry<String,Integer>> i = bundleCopies.entrySet().iterator(); i.hasNext(); )
    {
       Map.Entry<String,Integer> entry = i.next();
       String bundleName = entry.getKey();
       int Copies = entry.getValue();

       int maxAge=0;
       for(int j=0;j<dtnrouting.areBundlesDelivered.size();j++)
        {
          Bundle bObj=dtnrouting.areBundlesDelivered.get(j) ;
          if(bundleName.equals(bObj.bundleName))
          {
              bundleAge.put(bObj, bundleAge.get(bObj) + Copies);
              if(maxAge<bundleAge.get(bObj))
              {

                  maxAge=bundleAge.get(bObj);
                  maxAgedBundle = bObj;
              }
          }
        }
   }
  //Deleting Node have Maximum age
  nx.DestNBundle.remove(maxAgedBundle);
}
//******************************************************************************
}//end of nectar class





