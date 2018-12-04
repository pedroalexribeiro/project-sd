import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

class Network {
    public static String getIP() {

        try(final DatagramSocket socket = new DatagramSocket()){
            int randomNum = ThreadLocalRandom.current().nextInt(1500, 2000 + 1);
            socket.connect(InetAddress.getByName("8.8.8.8"), randomNum);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }
}