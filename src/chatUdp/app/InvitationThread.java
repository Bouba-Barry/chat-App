package chatUdp.app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class InvitationThread extends Thread{
    private DatagramSocket socket;
    public InvitationThread(DatagramSocket socket){this.socket = socket;}

    public void run(){
            while (true) {
                //Arrays.fill(tmp, (byte) 0);
                byte[] tmp = new byte[1024];
                DatagramPacket packet = new DatagramPacket(tmp, tmp.length);
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());

                    if(message.startsWith("Invitation")){
                        String parts [] = message.split(":");

                        System.out.println(parts[1]);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
