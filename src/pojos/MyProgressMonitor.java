package pojos;

import com.jcraft.jsch.SftpProgressMonitor;

import javax.swing.*;

/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class MyProgressMonitor implements SftpProgressMonitor {
    ProgressMonitor monitor;
    long count=0;
    long max=0;
    public void init(int op, String src, String dest, long max){
        this.max=max;
        monitor=new ProgressMonitor(null,
                ((op== SftpProgressMonitor.PUT)?
                        "put" : "get")+": "+src,
                "",  0, (int)max);
        count=0;
        percent=-1;
        monitor.setProgress((int)this.count);
        monitor.setMillisToDecideToPopup(1000);
    }
    private long percent=-1;
    public boolean count(long count){
        this.count+=count;

        if(percent>=this.count*100/max){ return true; }
        percent=this.count*100/max;

        monitor.setNote("Completed "+this.count+"("+percent+"%) out of "+max+".");
        monitor.setProgress((int)this.count);

        return !(monitor.isCanceled());
    }
    public void end(){
        monitor.close();
    }
}
