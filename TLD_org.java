import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by mrb5960 on 12/6/16.
 */
public class TLD_org implements Runnable {
    int port;
    public TLD_org(int port){
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("########## .org TLD server started ###########");
        String url = "", domain, line, IP_address = "";

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // to handle incoming and outgoing packets separately
        DatagramPacket inpacket, outpacket;
        InetAddress local_address;
        int local_port;
        FileInputStream fin;
        BufferedReader br;
        HashMap<String, String> records = new HashMap<>();
        boolean found;

        try {
            fin = new FileInputStream("Tld_org.txt");
            br = new BufferedReader(new InputStreamReader(fin));
            String[] temp;
            while((line = br.readLine()) != null){
                temp = line.split(",");
                records.put(temp[0], temp[1]);
            }
            for(String str : records.keySet()){
                System.out.println("Key: " + str + " Value: " + records.get(str));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(true) {
            try {
                byte[] inbuffer = null;
                //byte[] outbuffer = null;
                found = false;
                inbuffer = new byte[1000];
                inpacket = new DatagramPacket(inbuffer, inbuffer.length);
                socket.receive(inpacket);
                // getting address of the client from the incoming packet
                local_address = inpacket.getAddress();
                // getting the port number of the client where the packets are to be delivered
                local_port = inpacket.getPort();
                url = new String(inpacket.getData()).trim();
                System.out.println("Url is : " + url);
                // check if local server has the url entry in the db
                for(String str : records.keySet()){
                    if (str.equals(url)){
                        IP_address = records.get(str);
                        System.out.println("Key: " + str + " Value: " + records.get(str));
                        found = true;
                        break;
                    }
                }

                // if found, send back the IP address to the host
                if(found){
                    byte[] outbuffer1 = new byte[1000];
                    outbuffer1 = IP_address.getBytes();
                    outpacket = new DatagramPacket(outbuffer1, outbuffer1.length, local_address, local_port);
                    socket.send(outpacket);
                }
                else{
                    System.out.println("IP address not found");
                    byte[] outbuffer2 = new byte[1000];
                    outbuffer2 = new String("False").getBytes();
                    outpacket = new DatagramPacket(outbuffer2, outbuffer2.length, local_address, local_port);
                    socket.send(outpacket);
                }
                // resolve domain to send to root name server
                //domain = resolveDomain(url);

                //System.out.println(domain);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
