import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Created by mrb5960 on 12/3/16.
 */
public class Local implements Runnable{
    int local_port, root_port, tldcom_port, tldorg_port, authoritative_port;
    public Local(int port, int root_port, int tldcom_port, int tldorg_port, int authoritative_port){
        this.local_port = port;
        this.root_port = root_port;
        this.tldcom_port = tldcom_port;
        this.tldorg_port = tldorg_port;
        this.authoritative_port = authoritative_port;
    }

    @Override
    public void run() {
        System.out.println("########## Local server started ###########");
        String url = "", domain, line, IP_address = "";

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(local_port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // to handle incoming and outgoing packets separately
        DatagramPacket inpacket, outpacket;
        InetAddress host_address, server_address;
        int host_port;
        FileInputStream fin;
        BufferedReader br;
        HashMap<String, String> records = new HashMap<>();
        boolean found;

        try {
            fin = new FileInputStream("Local.txt");
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
                found = false;
                byte[] inbuffer = new byte[1000];
                inpacket = new DatagramPacket(inbuffer, inbuffer.length);
                socket.receive(inpacket);
                // getting address of the client from the incoming packet
                host_address = inpacket.getAddress();
                // getting the port number of the client where the packets are to be delivered
                host_port = inpacket.getPort();
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
                    outpacket = new DatagramPacket(outbuffer1, outbuffer1.length, host_address, host_port);
                    socket.send(outpacket);
                }
                else{
                    System.out.println("IP address not found...sending request to root name server");



                                    // send request to root name server //



                    DatagramSocket rootsocket = new DatagramSocket();
                    InetAddress root_address = InetAddress.getByName("localhost");
                    byte[] outbuffer = url.getBytes();
                    outpacket = new DatagramPacket(outbuffer,outbuffer.length, root_address, root_port);
                    rootsocket.send(outpacket);

                    // get response from the root name server
                    inbuffer = new byte[1000];
                    inpacket = new DatagramPacket(inbuffer, inbuffer.length);
                    rootsocket.receive(inpacket);

                    // check the response
                    String rootresponse = new String(inpacket.getData()).trim();
                    System.out.println("Response from root name server : " + rootresponse);
                    if(rootresponse.equals("False")){




                                // send request to top level domain server //




                        // resolve domain to send to root name server
                        domain = resolveDomain(url);
                        System.out.println(domain);
                        if(domain.equals("com")){ // send request to TLD_com server
                            System.out.println("Sending request to ." + domain + " top level domain server");

                            DatagramSocket tldcom_socket = new DatagramSocket();
                            InetAddress tldcom_address = InetAddress.getByName("localhost");
                            outbuffer = new byte[1000];
                            outbuffer = url.getBytes();
                            outpacket = new DatagramPacket(outbuffer,outbuffer.length, tldcom_address, tldcom_port);
                            tldcom_socket.send(outpacket);

                            // get response from the tld .com name server
                            inbuffer = new byte[1000];
                            inpacket = new DatagramPacket(inbuffer, inbuffer.length);
                            tldcom_socket.receive(inpacket);

                            // check the response
                            String tldcom_response = new String(inpacket.getData()).trim();
                            System.out.println("Response from .com tld server : " + tldcom_response);

                            if(tldcom_response.equals("False")){
                                // send request to authoritative server



                                System.out.println("Sending request to " + url + " authoritative server");

                                DatagramSocket auth_socket = new DatagramSocket();
                                InetAddress auth_address = InetAddress.getByName("localhost");
                                outbuffer = new byte[1000];
                                outbuffer = url.getBytes();
                                outpacket = new DatagramPacket(outbuffer,outbuffer.length, auth_address, authoritative_port);
                                auth_socket.send(outpacket);

                                // get response from authoritative server
                                inbuffer = new byte[1000];
                                inpacket = new DatagramPacket(inbuffer, inbuffer.length);
                                auth_socket.receive(inpacket);

                                // check the response
                                String auth_response = new String(inpacket.getData()).trim();
                                System.out.println("Response from TLD_org server : " + auth_response);


                                // send IP address to the host
                                byte[] outbuffer2 = new byte[1000];
                                outbuffer2 = auth_response.getBytes();
                                outpacket = new DatagramPacket(outbuffer2, outbuffer2.length, host_address, host_port);
                                socket.send(outpacket);



                            }
                            else{
                                // send IP address to the host
                                byte[] outbuffer2 = new byte[1000];
                                outbuffer2 = tldcom_response.getBytes();
                                outpacket = new DatagramPacket(outbuffer2, outbuffer2.length, host_address, host_port);
                                socket.send(outpacket);
                            }
                        }



                        else if(domain.equals("org")){ // send request to TLD_org server
                            System.out.println("Sending request to ." + domain + " top level domain server");

                            DatagramSocket tldorg_socket = new DatagramSocket();
                            InetAddress tldorg_address = InetAddress.getByName("localhost");
                            outbuffer = new byte[1000];
                            outbuffer = url.getBytes();
                            outpacket = new DatagramPacket(outbuffer,outbuffer.length, tldorg_address, tldorg_port);
                            tldorg_socket.send(outpacket);

                            // get response from the tld .org name server
                            inbuffer = new byte[1000];
                            inpacket = new DatagramPacket(inbuffer, inbuffer.length);
                            tldorg_socket.receive(inpacket);

                            // check the response
                            String tldorg_response = new String(inpacket.getData()).trim();
                            System.out.println("Response from TLD_org server : " + tldorg_response);

                            if(tldorg_response.equals("False")){
                                // send request to authoritative server




                                System.out.println("Sending request to " + url + " authoritative server");

                                DatagramSocket auth_socket = new DatagramSocket();
                                InetAddress auth_address = InetAddress.getByName("localhost");
                                outbuffer = new byte[1000];
                                outbuffer = url.getBytes();
                                outpacket = new DatagramPacket(outbuffer,outbuffer.length, auth_address, authoritative_port);
                                auth_socket.send(outpacket);

                                // get response from authoritative server
                                inbuffer = new byte[1000];
                                inpacket = new DatagramPacket(inbuffer, inbuffer.length);
                                auth_socket.receive(inpacket);

                                // check the response
                                String auth_response = new String(inpacket.getData()).trim();
                                System.out.println("Response from TLD_org server : " + auth_response);


                                // send IP address to the host
                                byte[] outbuffer2 = new byte[1000];
                                outbuffer2 = auth_response.getBytes();
                                outpacket = new DatagramPacket(outbuffer2, outbuffer2.length, host_address, host_port);
                                socket.send(outpacket);



                            }
                            else{
                                // send IP address to the host
                                byte[] outbuffer2 = new byte[1000];
                                outbuffer2 = tldorg_response.getBytes();
                                outpacket = new DatagramPacket(outbuffer2, outbuffer2.length, host_address, host_port);
                                socket.send(outpacket);
                            }
                        }





                    }
                    else{ // send IP address to host
                        byte[] outbuffer2 = new byte[1000];
                        outbuffer2 = rootresponse.getBytes();
                        outpacket = new DatagramPacket(outbuffer2, outbuffer2.length, host_address, host_port);
                        socket.send(outpacket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String resolveDomain(String url){
        int count = 0, index = 0;
        String domain;
        for(int i = url.length() - 1; i > 0; i--) {
            index = i;
            if (url.charAt(i) == '.'){
                index = i;
                break;
            }
        }
        domain = url.substring(index+1, url.length());
        return domain;
    }
}
