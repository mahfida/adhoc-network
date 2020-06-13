    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AdapterPackage;
import DTNRouting.*;
import javax.swing.event.MenuEvent;

/**
 *
 * @author fwu
 */
public class MyMenuAdapter extends MenuAdapter {
dtnrouting dtn=new dtnrouting();
public MyMenuAdapter (dtnrouting dtn)
    {
    this.dtn=dtn;
    }
    @Override
 public void menuSelected(MenuEvent e) {
        Setting setting=new Setting();
                setting.createSettingFrame();
       
        
    }

}
