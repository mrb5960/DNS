import java.io.*;
import java.net.*;

/**
 * Created by mrb5960 on 12/3/16.
 */
public class Host implements Runnable{
    int local_port; // server port number to connect to
    public Host(int local_port){
        this.local_port = local_port;
    }

    @Override
    public void run() {
        System.out.println("########## Host started ##########");
        byte[] inbuffer;
        byte[] outbuffer;
        DatagramPacket inpacket, outpacket;
        DataOutputStream dout;
        DataInputStream din;
        DatagramSocket socket;
        InetAddress server_address;
        String reply = "";

        try {
            socket = new DatagramSocket();
            server_address = InetAddress.getByName("localhost");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String url = "", domain;

            while (true) {
                outbuffer = new byte[1000];
                System.out.println("Enter url: ");
                url = br.readLine();
                outbuffer = url.getBytes();
                outpacket = new DatagramPacket(outbuffer, outbuffer.length, server_address, local_port);
                socket.send(outpacket);

                inbuffer = new byte[1000];
                inpacket = new DatagramPacket(inbuffer, inbuffer.length);
                socket.receive(inpacket);
                reply = new String(inpacket.getData()).trim();
                if(reply.equals("False")){
                    System.out.println("IP address not found at local name server");

                }
                else
                    System.out.println("The IP address is : " + reply);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
