package client;

import controller.ScreensFramework;
import controller.ScreensController;
import controller.IScreensController;
import service.Server;
import com.jfoenix.controls.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import service.ServerConnection;

public class HangmanClientController implements Initializable, IScreensController {     //l controller gestisce l'interfaccia utente e la comunicazione con il server attraverso un oggetto di tipo ServerConnection
    ScreensController myController;
    private static Server srv = null;
    private static boolean ongoingGame = false;
    ServerConnection srvConn;                               

    private TreeView<String> srvTree;

 
    private Label word;

    private Label selecredSrv;


    private Label remainingAttempts;

 
    private Label score;

   
    private Label info;

    private Label gameStatus;

 
    private JFXTextField wholeWord;

   
    private ImageView hang;

    //FXML
    private ImageView hangItems;

    private TreeItem<String> root = null;

    
    public void initialize(URL location, ResourceBundle resources) {            // viene chiamato quando la scena associata al controller viene caricata. In questo metodo vengono impostate le azioni da eseguire in risposta a eventi dell'interfaccia utente

        this.root = new TreeItem<>("Impiccato Servers");
        this.root.setExpanded(true);
        this.srvTree.setRoot(root);
        this.srvTree.setShowRoot(false);

        srvTree.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
           
            public void handle(javafx.scene.input.MouseEvent event) {           //viene chiamato in risposta a eventi specifici, come il doppio clic su un elemento del TreeView. In questo caso, viene verificato se l'elemento selezionato è un nodo foglia (cioè un server) e, in tal caso, viene impostato l'oggetto srv come il server selezionato
                if (!ongoingGame) {
                    if (event.getClickCount() == 2) { //deseleziona il server con doppio clic
                        TreeItem<String> selectedSrv = srvTree.getSelectionModel().getSelectedItem();
                        if (selectedSrv != null) {
                            if (selectedSrv.isLeaf()) { // ottiene il nome del genitore e lo confronta con il nome del server
                                if (selectedSrv.getParent().getValue().equals(srv.getSrvName())) {
                                    srv = null;
                                    unselectAllNodes();
                                }
                            } else // ottiene il nome dell'elemento e lo confronta con il nome del server
                            {
                                if (selectedSrv.getValue().equals(srv.getSrvName())) {
                                    srv = null;
                                    unselectAllNodes();
                                }
                            }
                        }

                    
                            }
                        }
                    }
                }
            }
        });

        loadServers();
    }


   
    void playGameAction(ActionEvent event) {            //evento del gioco,gioco iniziato
        if (!ongoingGame) { // questo è un nuovo gioco, il server deve essere selezionato!
            if (srv == null) { // il server non è selezionato!
                showAlert(Alert.AlertType.ERROR, "Allerta sistema", "errore nuovo gioco", "Seleziona un server per giocare a Hangman!");
                return;
            }
            initializeGameVariables(true);
            ConnectService cs = new ConnectService();           //il codice crea un nuovo ConnectServiceoggetto e chiama il suo start metodo. La ConnectService classe è responsabile della gestione di una connessione a un server
            cs.start();
            ongoingGame = true;
        } else {            //in caso di errore
            try {
                initializeGameVariables(false);
                srvConn.writeToServer("nuovo_gioco");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    
    
        }
    }

  
    void sendLetter(ActionEvent event) {            //evento mandare lettera,con bottone
        if (ongoingGame) {                          //gioco in corso
            try {
                if (Integer.parseInt(remainingAttempts.getText()) > 0 && word.getText().contains("-")) {
                    String msg = ((JFXButton) event.getSource()).getText();
                    if (msg != null && !msg.isEmpty()) {
                        srvConn.writeToServer(msg);
                        System.out.println(msg);
                    }
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Informazioni di sistema", "Nuova partita richiesta", "Non hai ancora tentativi o hai vinto la partita! Gioca a un nuovo gioco! Buona fortuna :)");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

   
    void sendWholeWordAction(ActionEvent event) {       //inviare la parola intera
        if (ongoingGame) {
            try {
                if (Integer.parseInt(remainingAttempts.getText()) > 0 && word.getText().contains("-")) {
                    String msg = wholeWord.getText();
                    if (msg != null && !msg.isEmpty()) {
                        srvConn.writeToServer(msg);
                        System.out.println(msg);
                    }
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Informazioni di sistema", "Nuova partita richiesta", "Non hai ancora tentativi o hai vinto la partita! Gioca a un nuovo gioco!\nBuona fortuna :)");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
      
    }

   //
    void addSrvAction(ActionEvent event) {      //se in gioco aggiungi nuovo server ti ferma subito 
        if (ongoingGame) {
            showAlert(Alert.AlertType.WARNING, "Avviso di sistema", "Aggiungi nuovo server", "Non puoi aggiungere un nuovo server mentre giochi! Prima termina il gioco");
            return;
        
        this.myController.setScreen(ScreensFramework.insertingServerScreenID);
    }

    private void loadServers() {            //mettere upload server
        File f = new File("servers.xml");       //crea file xml
        if (f.exists() && !f.isDirectory()) {
            try {
                srvTree.setShowRoot(true);

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();       //Questo codice creerebbe un DocumentBuilder oggetto usando a DocumentBuilderFactory, quindi lo userebbe per analizzare un file XML e creare un Documento ggetto che rappresenta la struttura del documento XML.
                Document doc = docBuilder.parse("servers.xml");
                NodeList serversList = doc.getElementsByTagName("server");

                for (int i = 0; i < serversList.getLength(); i++) {             // legge i dati dei server da un documento XML e crea oggetti Server per ogni elemento server presente nel documento. Gli oggetti Server vengono quindi aggiunti a una vista ad albero per essere visualizzati all'utente.
                    Element srvEl = (Element) serversList.item(i);
                    Server server = new Server(srvEl.getElementsByTagName("srvName").item(0).getTextContent(), srvEl.getElementsByTagName("srvIP").item(0).getTextContent(), srvEl.getElementsByTagName("port").item(0).getTextContent());
                    addToTreeView(server);
                }
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
           
        }

        srv = null;


  

    private void unselectAllNodes() {           // Il metodo prima imposta il modello di selezione della visualizzazione ad albero in modo che non selezioni nulla, quindi imposta il testo di un'etichetta in modo che dica "nessun server selezionato"
        srvTree.getSelectionModel().select(null);
        selecredSrv.setText("nessun server selezionato");
    }


    private class ConnectService extends Service<String> {      //imposta un gestore per l'evento di completamento del lavoro del ConnectService, che visualizza un messaggio all'utente quando la connessione con il server è stata stabilita.

        private ConnectService() {          
            setOnSucceeded((WorkerStateEvent event) -> {
                String result = getValue();
                if (result.equals("fine")) {
                    info.setText("Connessione stabilita con: " + srv.getSrvName() + " [" + srv.getSrvIP() + ":" + srv.getPort() + "]");
                    showAlert(Alert.AlertType.INFORMATION, "Informazioni di sistema", "Connessione stabilita", "Connessione a: " + srv.getSrvName() + " è stato stabilito con successo");
                    srvConn.writeToServer("nuovo_gioco");
                    ReceiveService rs = new ReceiveService();
                    rs.start();
                } else {
                    info.setText("Impossibile connettersi a: " + srv.getSrvName());
                    srvConn = null;
                    srv = null;
                    ongoingGame = false;
                    initializeGameVariables(true);
                    unselectAllNodes();
                    showAlert(Alert.AlertType.ERROR, "Avviso di sistema", "Errore di connessione", "Impossibile connettersi al server.L'errore è: " + result);
                }
            });
       

    
         
  


    }
}
