import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class DownloadFile extends Thread {
    private ServerSocket socket;
    private String filepath;

    public DownloadFile(int port, String filepath) {
        try {
            this.socket = new ServerSocket(port);
            this.filepath = filepath;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        if (this.socket.isBound()) {
            Socket client = null;
            try {
                client = this.socket.accept();
                if (client.isConnected()) {
                    InputStream in = null;
                    OutputStream out = null;
                    DataInputStream inInfo;
                    try {
                        File f = new File(filepath);
                        f.getParentFile().mkdirs();
                        f.createNewFile();
                        in = new BufferedInputStream(client.getInputStream());
                        out = new FileOutputStream(f);
                        int read = 0;
                        byte[] buffer = new byte[1024];
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                client.close();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}