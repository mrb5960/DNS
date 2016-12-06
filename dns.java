/**
 * Created by mrb5960 on 12/3/16.
 */
public class dns {

    public static void main(String args[]){

        if(args[0].equals("local")){
            int local_port = Integer.parseInt(args[1]); // local server port
            int root_port = Integer.parseInt(args[2]); // root name server port
            int tldcom_port = Integer.parseInt(args[3]); // top .com level domain server port
            int tldorg_port = Integer.parseInt(args[4]); // top .org level domain server port
            int authoritative_port = Integer.parseInt(args[5]); // authoritative server port
            new Thread(new Local(local_port,root_port,tldcom_port,tldorg_port,authoritative_port)).start(); // start local server
        }
        else if(args[0].equals("host")){
            int local_port = Integer.parseInt(args[1]); // local server port to connect to
            new Thread(new Host(local_port)).start(); // start the host
        }
        else if(args[0].equals("root")){
            int root_port = Integer.parseInt(args[1]); // root name server port to connect to
            new Thread(new Root(root_port)).start(); // start root name server
        }
        else if(args[0].equals("tld.com")){
            int tldcom_port = Integer.parseInt(args[1]); // tld .com server port to connect to
            new Thread(new TLD_com(tldcom_port)).start(); // start .com tld server
        }
        else if(args[0].equals("authoritative")){
            int auth_port = Integer.parseInt(args[1]); // tld .com server port to connect to
            new Thread(new Authoritative(auth_port)).start(); // start .com tld server
        }
    }
}
