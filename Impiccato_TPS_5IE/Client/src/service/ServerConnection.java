package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class ServerConnection {             //Questa è una classe che rappresenta una connessione a un server utilizzando un socket
    
    private PrintWriter out = null;
    private Socket clientSocket = null;

    public Socket getClientSocket() {       
        //è un metodo che restituisce l'oggetto socket client in una classe che rappresenta una connessione client-server.
        return clientSocket;
    }

    public ServerConnection() {

    }

    public String connect(String host, String portStr) {        //metodo accetta un nome host e un numero di porta come argomenti e tenta di stabilire una connessione socket all'host e alla porta specificati. Se la connessione ha esito positivo, crea un PrintWriteroggetto utilizzando il flusso di output del socket e lo memorizza nel outcampo. Se la connessione non va a buon fine, restituisce una stringa che descrive l'errore
        try {                                                   // l'analisi del numero di porta ha esito positivo, crea un nuovo Socketoggetto e tenta di connettersi all'host e alla porta specificati utilizzando il connectmetodo della Socketclasse. Se la connessione ha esito positivo, crea un PrintWriteroggetto utilizzando il flusso di output del socket e lo memorizza nel outcampo. Se la connessione fallisce, rileva UnknownHostExceptionse l'host è sconosciuto o non disponibile o IOExceptionse non è in grado di ottenere l'I/O per la connessione. In entrambi i casi, imposta il clientSocketcampo su nulle restituisce un messaggio di errore.
            int port;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ex) {
                return "Impossibile connettersi!\nLa porta non è un valore numerico!";
            }
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(host, port), 1000);
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            return "fine";
        } catch (UnknownHostException e) {
            clientSocket = null;
            return "Host: " + host + " è sconosciuto o non disponibile nella rete.";
        } catch (IOException e) {
            clientSocket = null;
            return "Impossibile ottenere l'I/O per la connessione a: " + host + ".";
        }
    }

    public void readFromServer(Label word, Label info, Label remainingAttempts, Label score, Label gameStatus, ImageView hang, ImageView hangItems) throws IOException {        //prende dal server gli attributi nelle parentesi
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
   
            }
           
            Platform.runLater(new Runnable() {      //è un metodo nel framework dell'applicazione JavaFX utilizzato per eseguire un'attività eseguibile sul thread dell'applicazione JavaFX. Questo metodo è utile per aggiornare l'interfaccia utente di un'applicazione JavaFX
                @Override
                public void run() {
                    word.setText(msg[1].contains("-") ? msg[1].replaceAll(".(?!$)", "$0 ") : msg[1]);
                    remainingAttempts.setText(msg[2]);
                    score.setText(msg[3]);
                    switch (msg[0]) {
                        case "INIZIO":
                            info.setText("Il nuovo gioco è stato avviato con successo :)");
                            break;
                        case "VINCITA":
                            info.setText("Buon lavoro! hai vinto!!");
                            gameStatus.setText("Bravo! HAI VINTO!");
                            break;
                        case "SBAGLIATA":
                            info.setText("Scusa, la tua ipotesi era sbagliata!");
                            setImages(hang, hangItems, Integer.parseInt(msg[2]));
                            break;
                        case "CORRETTO":
                            info.setText("ottima ipotesi! Continua ad andare avanti!");
                            break;
                        case "GRANDE VINCITA":
                            info.setText("Oh! Ottima supposizione! Sapevi l'intera parola :)");
                            gameStatus.setText("Bravo! HAI VINTO");
                            break;
                        case "PERDITA":
                            setImages(hang, hangItems, Integer.parseInt(msg[2]));
                            info.setText("Scusa! Gioco finito :( Per favore riprova");
                            gameStatus.setText("Game Over!");
                            break;
                        default:
                            break;
                    }
                }

                private void setImages(ImageView hang, ImageView hangItems, int remainingAttempts) {        //setta le immagini del impiccato
                    switch (remainingAttempts) {
                        case 6:
                            hang.setImage(new Image(getClass().getResourceAsStream("/hangs/hang1.png")));
                            hangItems.setImage(new Image(getClass().getResourceAsStream("/item/item2.png")));
                            break;
                        case 5:
                            hang.setImage(new Image(getClass().getResourceAsStream("/hangs/hang2.png")));
                            hangItems.setImage(new Image(getClass().getResourceAsStream("/item/item3.png")));
                            break;
                        case 4:
                            hang.setImage(new Image(getClass().getResourceAsStream("/hangs/hang3.png")));
                            hangItems.setImage(new Image(getClass().getResourceAsStream("/item/item4.png")));
                            break;
                        case 3:
                            hang.setImage(new Image(getClass().getResourceAsStream("/hangs/hang4.png")));
                            hangItems.setImage(new Image(getClass().getResourceAsStream("/item/item5.png")));
                            break;
                        case 2:
                            hang.setImage(new Image(getClass().getResourceAsStream("/hangs/hang5.png")));
                            hangItems.setImage(new Image(getClass().getResourceAsStream("/item/item6.png")));
                            break;
                        case 1:
                            hang.setImage(new Image(getClass().getResourceAsStream("/hangs/hang6.png")));
                            hangItems.setImage(new Image(getClass().getResourceAsStream("/item/item7.png")));
                            break;
                        case 0:
                            hang.setImage(new Image(getClass().getResourceAsStream("/hangs/hang7.png")));
                            hangItems.setImage(new Image(getClass().getResourceAsStream("/item/item8.png")));
                            break;
                    }
                }
            });
        }
        clientSocket.close();
    }

        public void writeToServer(String msg) {         //Il metodo prende una stringa chiamata messaggio come argomento. Converte il messaggio in minuscolo utilizzando il toLowerCase metodo e quindi lo invia al server utilizzando il printl nmetodo outdell'oggetto, che è probabilmente un PrintWriter oggetto utilizzato per scrivere sul socket. Il flus hmetodo viene quindi chiamato per garantire che il messaggio venga inviato immediatamente
        out.println(msg.toLowerCase());
        out.flush();
    }
}
