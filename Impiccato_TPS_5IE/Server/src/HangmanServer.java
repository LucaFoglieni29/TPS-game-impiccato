import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HangmanServer {        //classe, che contiene un metodo principale che è il punto di ingresso per l'applicazione server.
    private static ArrayList<String> words = null;
    private static boolean listening = true;
    private static ServerSocket serverSocket = null;        //Il readWordsmetodo legge un elenco di parole da un file e le memorizza wordsnell'elenco. Questo elenco verrà utilizzato per giocare al gioco dell'impiccato con i clienti. Il listenmetodo crea un ServerSocketoggetto e ascolta le connessioni client in entrata sulla porta specificata
    
    
    public static void main(String[] args) {
        readWords();
        listen(args);
    }

    private static void listen(String[] args) {         //oggetto e ascolta le connessioni client in entrata sulla porta specificata. Quando viene stabilita una connessione, crea un nuovo HangmanServerHandleroggetto e gli passa l'elenco 
        try {                                           //di parole e il socket del client, e lo invia a ExecutorServiceun'esecuzione in concomitanza con altre richieste del client. La HangmanServerHandlerclasse è responsabile della gestione della comunicazione con il cliente e del gioco dell'impiccato.
            serverSocket = new ServerSocket(setPort(args));
            System.out.printf("attesa : [%s]%n", availableThreads);
            System.out.printf("In attesa di connessioni dalla porta [%s]...%n%n", serverSocket.getLocalPort());
            while (listening) {     //variabile è un flag che controlla il ciclo che ascolta le connessioni client. Finché lo è true, il server continuerà ad accettare nuove connessioni. Se è impostato su false, il ciclo terminerà e il server smetterà di ascoltare nuove connessioni.
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new HangmanServerHandler(words, clientSocket));
                
                System.out.printf("Client: %s [%s] è connesso%n", clientSocket.getLocalAddress().getHostName(), clientSocket.getLocalAddress().getHostAddress());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static int setPort(String[] args) {         //setta la porta 5151
        int port = 5151;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                port = 5151;
            }
        }
        return port;
    }

    private static void readWords() {       //legge un elenco di parole da un file chiamato words.txte le memorizza in un file ArrayListchiamato words.
        words = new ArrayList<String>();    //Prima crea un vuoto ArrayListchiamato words. Quindi, crea un BufferedReader oggetto chiamato bReader che viene utilizzato per leggere un file chiamato words.txt.
        String line;
        try {
            BufferedReader bReader = new BufferedReader(new FileReader("Server/words.txt"));

            while ((line = bReader.readLine()) != null) {
                words.add(line.toLowerCase());
            }

            bReader.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void stopServer() {         
        //Questo codice definisce un metodo chiamato stopServerche viene utilizzato per arrestare il server. Il metodo imposta la listening variabile su false per indicare che il server non è più in attesa di connessioni in entrata.
        
        listening = false;
        executorService.shutdownNow();
        try {
         
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Errore nel server,chiusura in corso");
            e.printStackTrace();
        }
        System.exit(0);
    }
}
