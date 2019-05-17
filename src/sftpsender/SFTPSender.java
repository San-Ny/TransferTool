package sftpsender;

import com.jcraft.jsch.*;
import pojos.MyProgressMonitor;
import pojos.SSH2User;
import utils.ArgumentReaderUtil;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

/**
 * TransferTool
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class SFTPSender extends Thread {

    private Properties properties;

    public SFTPSender(Properties properties){
        this.properties = properties;
    }

    @Override
    public void run(){

        //checking required arguments
        String[] requiredProperties = {"user", "port", "host"};

        if (!ArgumentReaderUtil.isValid(properties, requiredProperties)){
            System.err.println("Missing required arguments");
            System.exit(0);
        }

        //assignation
        String user, port, host;
        boolean debugging = properties.getProperty("Debugging").equals("ON");
        user = properties.getProperty("user");
        port = properties.getProperty("port");
        host = properties.getProperty("host");


        try{
            JSch jsch=new JSch();
            Session session = jsch.getSession(user, host, Integer.parseInt(port));
            Properties strict = new Properties();
            if (properties.getProperty("StrictHostKeyChecking").equals("no")) strict.put("StrictHostKeyChecking", "no");
            else if (properties.getProperty("StrictHostKeyChecking").equals("yes")) strict.put("StrictHostKeyChecking", "yes");
            else if (debugging) System.out.println("StrictHostKeyChecking disabled");
            session.setConfig(strict);
            UserInfo ui = new SSH2User(debugging);
            session.setUserInfo(ui);
            Channel channel = null;
            ChannelSftp sftp;

            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp)channel;

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintStream out = System.out;

            int i;
            String[] line;
            int level = 0;

            while(true){
                out.print("sftp>");

                line = in.readLine().split(" ");

                if(line[0].equals("quit") || line[0].equals("exit")){
                    sftp.quit();
                    break;
                }
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
                    String p1 = line[1];
                    String p2=".";
                    if(line.length == 3) p2 = line[2];
                    try{
                        MyProgressMonitor monitor = new MyProgressMonitor();
                        if(line[0].equals("get")){
                            int mode = ChannelSftp.OVERWRITE;
                            sftp.get(p1, p2, monitor, mode);
                        }
                        else{
                            int mode = ChannelSftp.OVERWRITE;
                            sftp.put(p1, p2, monitor, mode);
                        }
                    }
                    catch(SftpException e){
                        System.out.println(e.toString());
                    }
                }

            }

            session.disconnect();


        }catch (JSchException | IOException e){
            if (debugging)e.printStackTrace();
            else System.err.println("Fatal Error");
            System.exit(-1);
        }
    }
}
