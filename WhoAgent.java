import UWAgent.*;
import java.io.*;
import java.util.Vector;
import java.util.Date;
import java.net.InetAddress;

public class WhoAgent extends UWAgent implements Serializable {
    public boolean print = false;
    public int cssNumber = 0;
    public int commandNumber = 0;
    public Vector<String> commandList = new Vector<String>();
    public Vector<String> cssList =  new Vector<String>();;
    public String originalIP;
    public long start = 0;
    public long end =0;
    public Vector<Vector<String>> allMessage = new Vector<Vector<String>>();

    public WhoAgent(){}

    public WhoAgent(String args[])
    {
        System.out.println("start");
        try {
            if (args.length < 2) 
                throw new Exception();
        } catch (Exception e) {
            System.err.println("usage: java Client serverIp port");
            System.exit(-1);
        }
        if (args[0].equals("P")) {
            
            print = true;

        }

        try {
            cssNumber = Integer.parseInt(args[1]);
            System.out.println("cssNumber is:" + cssNumber);
            if (cssNumber <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("should at least one cssmpi");
            System.exit(-1);
        }
        cssList = new Vector<String>(); // Store the cssmpis
        for (int i = 2; i < 2 + cssNumber; i++) {

            cssList.add(args[i]);
        }

        commandNumber = Integer.parseInt(args[2 + cssNumber]);
    
        try {
            
            System.out.println("command Number: " + commandNumber);
            if (commandNumber <= 0) {

                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("at least one command");
            System.exit(-1);
        }

        commandList.clear(); // Store the Commands     
        for (int i = 3+cssNumber; i < 3+cssNumber+commandNumber; i++) {
            commandList.add(args[i]);
        }
        // commandList.add("who");
        // commandList.add("ls");
        
    }

    public void init()
    {
        // list all the information 
        for (int i = 1; i < cssNumber; i++) {
            System.out.print("Server" + i + " : " + cssList.get(i - 1) + "|");
        }
        for (int i = 1; i < commandNumber; i++) {
            System.out.print("command" + i + " : " + commandList.get(i - 1) + "|");
        }
        System.out.println("\n======================");

        // restore the original IP address
        try {

            InetAddress local = InetAddress.getLocalHost();
            originalIP = (local.getHostAddress()).trim();
        } catch (Exception e) {}
        start = new Date().getTime();

        // pass the itear in Agent list 
        String args[] = new String[1];
        args[0] = String.valueOf(0);
        hop(cssList.get(0),"passing",args);
    }

    public void passing(String args[])
    {
        int order = Integer.parseInt(args[0]);
        if(order<cssNumber)
        {
            args[0] = String.valueOf(order+1);
            String currentAnget ="===========================\n"
                                 + cssList.get(order);
            Vector<String> temp = new Vector<String>();
            temp.add(currentAnget);
            allMessage.add(temp);
            for (String command : commandList) {
                String temp3 = "===============================\n"+"command: " + command;
                Vector<String> temp2 = new Vector<String>();
                temp2.add(temp3);
                allMessage.add(temp2);
                temp2 = execute(command);
                allMessage.add(temp2);
            }
            hop(cssList.get(order),"passing",args);
        }else
        {
            hop(originalIP,"printing");
        }
    }

    public void printing()
    {
        end = new Date().getTime();

        // if Print == ture, print all lines
        if(print){
            printVecotr(allMessage);
        }

        // if print == false, print the line of number.
        if(!print)
        {
            int loc = 0;
            for(Vector<String> message: allMessage)
            {
                loc += message.size();
            }
            System.out.println("Line of the Strings :" + loc);
        }
        System.out.println("Excutation Time: " + (end - start));
    }

    public void printVecotr(Vector<Vector<String>> allMessageX)
    {
        for(Vector<String> message : allMessageX){
            for(String obj:message)
            {
                System.out.println(obj);
            }
        }   
    }

    // powered By professor. 
    public Vector<String> execute(String command) {
        Vector<String> output = new Vector<String>();
        String line;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            InputStream input = process.getInputStream();
            BufferedReader bufferedInput = new BufferedReader(new InputStreamReader(input));
            while ((line = bufferedInput.readLine()) != null) {
                output.addElement(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return output;
        }
        return output;
    }
}
