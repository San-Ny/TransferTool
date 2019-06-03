package sftpsender;

import com.jcraft.jsch.*;
import pojos.MyProgressMonitor;
import pojos.SSH2User;
import utils.ConsolePrinterUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Vector;

public class SFTPlive extends Thread {

    private String user, port, host;
    boolean debugging;
    Properties properties;

    public SFTPlive(Properties properties){
        debugging = properties.getProperty("Debugging").equals("ON");
        user = properties.getProperty("user");
        port = properties.getProperty("port");
        host = properties.getProperty("host");
        this.properties = properties;
    }

    @Override
    public void run(){
        try{
            Session session = SSH2User.sshUser(user, port, host, debugging, properties);
            Channel channel;
            ChannelSftp sftp;

            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp)channel;

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintStream out = System.out;

            String[] line;
            while(true){
                out.print("sftp>");

                line = in.readLine().split(" ");

                if(line[0].equals("quit") || line[0].equals("exit")){
                    sftp.quit();
                    break;
                }
                else if(line[0].equals("rekey")) session.rekey();
                else if(line[0].equals("ls")){
                    String path = ".";
                    if(line.length == 2) path = line[1];
                    try{
                        Vector vector = sftp.ls(path);
                        if(vector != null){
                            for(int c = 0; c < vector.size(); c++){
                                Object obj = vector.elementAt(c);
                                if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) out.println(((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getLongname());
                            }
                        }
                    }
                    catch(SftpException e){
                        System.out.println(e.toString());
                    }
                }
                else if(line[0].equals("lls")){
                    String path=".";
                    if(line.length == 2) path = line[1];
                    try{
                        java.io.File file = new java.io.File(path);
                        if(!file.exists()) out.println("No such file or directory");
                        else if(file.isDirectory()){
                            String[] list=file.list();
                            assert list != null;
                            for (String s : list) out.println(s);
                            continue;
                        }
                        out.println(path);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else if((line[0].equals("cd") || line[0].equals("lcd")) && line.length == 2){
                    try{
                        if (line[0].equals("cd")) sftp.cd(line[1]);
                        else  sftp.lcd(line[1]);
                    }
                    catch(SftpException e){
                        System.out.println(e.toString());
                    }
                }
                else if((line[0].equals("rm") || line[0].equals("rmdir") || line[0].equals("mkdir")) && line.length == 2){
                    try{
                        if(line[0].equals("rm")) sftp.rm(line[1]);
                        else if(line[0].equals("rmdir")) sftp.rmdir(line[1]);
                        else sftp.mkdir(line[1]);
                    }
                    catch(SftpException e){
                        System.out.println(e.toString());
                    }
                }
                else if((line[0].equals("get") || line[0].equals("put")) && (line.length == 2 || line.length == 3)){
                    String p2 = ".";
                    if(line.length == 3) p2 = line[2];
                    try{
                        MyProgressMonitor monitor = new MyProgressMonitor();
                        if(line[0].equals("get")){
                            int mode = ChannelSftp.OVERWRITE;
                            sftp.get(line[1], p2, monitor, mode);
                        }
                        else{
                            int mode = ChannelSftp.OVERWRITE;
                            sftp.put(line[1], p2, monitor, mode);
                        }
                    }
                    catch(SftpException e){
//                        System.out.println(e.toString());
                    }
                }
                else if((line[0].equals("chgrp") || line[0].equals("chown") || line[0].equals("chmod")) && line.length == 3){
                    int foo = 0;
                    if(line[0].equals("chmod")){
                        byte[] bar=(line[1]).getBytes();
                        int k;
                        for (byte b : bar) {
                            k = b;
                            if (k < '0' || k > '7') {
                                foo = -1;
                                break;
                            }
                            foo <<= 3;
                            foo |= (k - '0');
                        }
                        if(foo == -1) continue;
                    }
                    else{
                        try{
                            foo = Integer.parseInt(line[2]);
                        }
                        catch(Exception e){continue;}
                    }
                    try{
                        switch (line[0]) {
                            case "chgrp":
                                sftp.chgrp(foo, line[2]);
                                break;
                            case "chown":
                                sftp.chown(foo, line[2]);
                                break;
                            case "chmod":
                                sftp.chmod(foo, line[2]);
                                break;
                        }
                    }
                    catch(SftpException e){
                        if (debugging) e.printStackTrace();
                        else System.err.println("Error");
                    }
                }
                else if(line[0].equals("pwd") || line[0].equals("lpwd")){
                    String str = (line[0].equals("pwd")?"Remote":"Local");
                    str+=" working directory: ";
                    if(line[0].equals("pwd")) str += sftp.pwd();
                    else str += sftp.lpwd();
                    out.println(str);
                }
                else if((line[0].equals("ln") || line[0].equals("symlink") || line[0].equals("rename") || line[0].equals("hardlink")) && line.length == 3){
                    try{
                        if(line[0].equals("hardlink")) sftp.hardlink(line[1], line[2]);
                        else if(line[0].equals("rename")) sftp.rename(line[1], line[2]);
                        else sftp.symlink(line[1], line[2]);
                    }
                    catch(SftpException e){
                        System.out.println(e.toString());
                    }
                }
                else if(line[0].equals("df") && line.length <= 2){
                    String p1 = line.length == 1 ? "." : line[1];
                    SftpStatVFS stat = sftp.statVFS(p1);
                    ConsolePrinterUtil.println("\tSize:\t\t\t\t\t\t" + stat.getSize() + "\n\tUsed:\t\t\t\t\t\t"+stat.getUsed() + "\n\tAvailable (none root):\t\t" + stat.getAvailForNonRoot() + "\n\tAvailable (root):\t\t\t"+stat.getAvail() + "\n\tCapacity:\t\t\t\t\t"+stat.getCapacity() + "%");
                }
                else if(line[0].equals("version")) out.println(sftp.version());
            }

            session.disconnect();


        } catch (Exception e){
            if (debugging)e.printStackTrace();
            else System.err.println("Fatal Error");
            System.exit(-1);
        }
    }
}
