package skipper_serveris;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FailuGavimoServeris {
    private static int port = 15123; // portas ryšiui tap kompiuterių
    private static int maxFailoDydis = 104857600; // Maksimaslus failo dydis baitais (104857600 = 100MB)
    private static int perskaitytiBaitai;
    private static int nuskaitytuBaituSk;
    private static Date Laikas;
    private static SimpleDateFormat laikoFormatas;
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static DataInputStream zinute;
    private static String failasAtnaujinimui;
    private static InputStream is;
    private static String gautas_failas;
    private static FileOutputStream fos;
    private static BufferedOutputStream bos;
//    private static 
//    private static 
//    private static 
            

            
    private static byte[] gaunamasFailas;
    public static void receiveDatabase() throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("-----------------------------");
                Laikas = new Date();
                laikoFormatas = new SimpleDateFormat("yyyy.MM.dd'_'HH:mm:ss");
                System.out.println("Prisijungimo laikas: "+laikoFormatas.format(Laikas));
                System.out.println("Prisijungimas iš:    "+socket.getRemoteSocketAddress());
                /*          zinutes gavimas           */
                zinute = new DataInputStream(socket.getInputStream());
                byte uzklausa = zinute.readByte();
                failasAtnaujinimui = zinute.readUTF();
                System.out.println(uzklausa+" - Gauntas prašymas antnaujinti "+failasAtnaujinimui+".db faila");
                /*          failo gavimas           */
                gaunamasFailas = new byte[maxFailoDydis];
                is = socket.getInputStream();
                perskaitytiBaitai = is.read(gaunamasFailas, 0, gaunamasFailas.length);
                nuskaitytuBaituSk = perskaitytiBaitai;
                do {
                    perskaitytiBaitai = is.read(gaunamasFailas, nuskaitytuBaituSk, (gaunamasFailas.length - nuskaitytuBaituSk));
                    if (perskaitytiBaitai >= 0) {
                        nuskaitytuBaituSk += perskaitytiBaitai;
                    }
                } while (perskaitytiBaitai > -1);
                /*          failo irasymas          */
                gautas_failas = "C:\\Users\\Algirdas\\Desktop\\Skipper\\"+failasAtnaujinimui+".db";// db vieta serveryje
                fos = new FileOutputStream(gautas_failas);
                bos = new BufferedOutputStream(fos);
                bos.write(gaunamasFailas, 0, nuskaitytuBaituSk);
                bos.flush();
                bos.close();

                System.out.println("Failas atnaujintas: "+failasAtnaujinimui+".db (dydis: "+(nuskaitytuBaituSk/1048576)+"."+((nuskaitytuBaituSk%1048576)/1024)+" MB)");
                System.out.println("-----------------------------\n");
                socket.close();
            } catch (IOException e) {
                System.out.println("Prisijungimas nepavyko.");
                System.out.println("Duomenys nebuvo gauti");
                System.out.println(e);
                System.out.println("-----------------------------\n");
            }
        }
    }
    public static void main(String[] args) throws IOException {
        receiveDatabase();
    }
}
