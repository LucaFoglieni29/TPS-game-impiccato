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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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

                    } else if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                        if (srv == null) {
                            TreeItem<String> selectedSrv = srvTree.getSelectionModel().getSelectedItem();
                            if (selectedSrv != null) {
                                if (selectedSrv.getParent() != null) { // non elemento radice -> nodo 0
                                    if (selectedSrv.getChildren() != null && selectedSrv.getChildren().size() > 0) { // nome server -> nodo 1
                                        srv = new Server(selectedSrv.getValue(), selectedSrv.getChildren().get(0).getValue(), selectedSrv.getChildren().get(1).getValue());
                                        selecredSrv.setText(srv.getSrvName() + " Selezionato");
                                        System.out.printf("%s [%s: %s]%n", srv.getSrvName(), srv.getSrvIP(), srv.getPort());
                                    } else { // uno dei campi non è inserito
                                        TreeItem<String> parent = selectedSrv.getParent();
                                        srv = new Server(parent.getValue(), parent.getChildren().get(0).getValue(), parent.getChildren().get(1).getValue());
                                        selecredSrv.setText(srv.getSrvName() + " Selected");
                                        System.out.printf("%s [%s: %s]%n", srv.getSrvName(), srv.getSrvIP(), srv.getPort());
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

    public void setScreenParent(ScreensController screenParent) {
        this.myController = screenParent;
    }

   
    void playGameAction(ActionEvent event) {
        if (!ongoingGame) { // questo è un nuovo gioco, il server deve essere selezionato!
            if (srv == null) { // il server non è selezionato!
                showAlert(Alert.AlertType.ERROR, "Allerta sistema", "errore nuovo gioco", "Seleziona un server per giocare a Hangman!");
                return;
            }
            initializeGameVariables(true);
            ConnectService cs = new ConnectService();
            cs.start();
            ongoingGame = true;
        } else {
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

  
    void sendLetter(ActionEvent event) {
        if (ongoingGame) {
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

   
    void sendWholeWordAction(ActionEvent event) {
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
        wholeWord.setText("");
    }

   //
    void addSrvAction(ActionEvent event) {
        if (ongoingGame) {
            showAlert(Alert.AlertType.WARNING, "Avviso di sistema", "Aggiungi nuovo server", "Non puoi aggiungere un nuovo server mentre giochi! Prima termina il gioco");
            return;
        }
        System.out.println("client.HangmanClientController.addSrvAction()");
        this.myController.setScreen(ScreensFramework.insertingServerScreenID);
    }

    private void loadServers() {
        File f = new File("servers.xml");
        if (f.exists() && !f.isDirectory()) {
            try {
                srvTree.setShowRoot(true);

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse("servers.xml");
                NodeList serversList = doc.getElementsByTagName("server");

                for (int i = 0; i < serversList.getLength(); i++) {
                    Element srvEl = (Element) serversList.item(i);
                    Server server = new Server(srvEl.getElementsByTagName("srvName").item(0).getTextContent(), srvEl.getElementsByTagName("srvIP").item(0).getTextContent(), srvEl.getElementsByTagName("port").item(0).getTextContent());
                    addToTreeView(server);
                }
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        srv = null;
    }

    private void addToTreeView(Server server) {
        TreeItem<String> parent;
        if (server.getSrvName() == null || server.getSrvName().isEmpty()) {
            parent = makeBranch("Untitled Server", root, true);
        } else {
            parent = makeBranch("Server: " + server.getSrvName(), root, true);
        }

        makeBranch("IP: " + server.getSrvIP(), parent, true);
        makeBranch("Port: " + server.getPort(), parent, true);
    }

    private TreeItem<String> makeBranch(String title, TreeItem<String> parent, boolean setExpand) {
        TreeItem<String> item = new TreeItem<>(title);
        item.setExpanded(setExpand);
        parent.getChildren().add(item);
        return item;
    }

    private void unselectAllNodes() {
        srvTree.getSelectionModel().select(null);
        selecredSrv.setText("nessun server selezionato");
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void initializeGameVariables(boolean initScore) {
        word.setText("-");
        remainingAttempts.setText("0");
        if (initScore) {
            score.setText("0");
        }
        info.setText("");
        gameStatus.setText("");

        hang.setImage(new Image(getClass().getResourceAsStream("/hangs/hang.png")));
        hangItems.setImage(new Image(getClass().getResourceAsStream("/item/item1.png")));

    }

    private class ConnectService extends Service<String> {

        private ConnectService() {
            setOnSucceeded((WorkerStateEvent event) -> {
                String result = getValue();
                if (result.equals("done")) {
                    info.setText("Connessione stabilita con: " + srv.getSrvName() + " [" + srv.getSrvIP() + ":" + srv.getPort() + "]");
                    showAlert(Alert.AlertType.INFORMATION, "Informazioni di sistema", "Connessione stabilita", "Connessione a: " + srv.getSrvName() + " è stato stabilito con successo");
                    srvConn.writeToServer("new_game");
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

     
        protected Task<Void> createTask() {
            return new Task() {
                
                protected Object call() throws IOException {
                    srvConn.readFromServer(word, info, remainingAttempts, score, gameStatus, hang, hangItems);
                    return null;
                }
            };
        }

    }
}
