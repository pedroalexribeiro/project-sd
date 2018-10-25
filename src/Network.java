import java.net.InetAddress;

class Network {
    private InetAddress IP;

    public Network() {
        try {
            IP = InetAddress.getLocalHost();
        } catch (java.net.UnknownHostException uhe) {
        }
    }

    public String RIP() {
        return IP.getHostAddress();
    }
}