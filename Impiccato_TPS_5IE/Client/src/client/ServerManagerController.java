package client;

import controller.ScreensFramework;
import controller.ScreensController;
import controller.IScreensController;
import com.jfoenix.controls.*;
import com.jfoenix.validation.*;

import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class ServerManagerController implements Initializable, IScreensController {
    ScreensController myController;

     //FXML
    private JFXButton cancel;

      //FXML
    private JFXTextField srvIP;

       //FXML
    private JFXTextField port;

       //FXML
    private JFXButton saveSrv;

        //FXML
    private JFXTextField srvName;

    RequiredFieldValidator required;
    NumberValidator onlyNumber;


    public void initialize(URL url, ResourceBundle rb) {
        required = new RequiredFieldValidator();
        onlyNumber = new NumberValidator();

        required.setMessage("L'input deve essere fornito!");
        required.setIcon(new Icon(AwesomeIcon.WARNING, "1em", ";", "errore"));

        onlyNumber.setMessage("L'input deve contenere solo numeri");
        onlyNumber.setIcon(new Icon(AwesomeIcon.WARNING, "1em", ";", "errore"));

        srvIP.getValidators().add(required);
        port.getValidators().add(onlyNumber);

        port.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                port.validate();
            }
        });

        srvIP.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                srvIP.validate();
            }
        });

    }

    
    public void setScreenParent(ScreensController screenParent) {
        this.myController = screenParent;
    }

    @FXML
    private void saveSrvAction(ActionEvent event) {
        if (srvIP.textProperty().getValue().length() > 0) {
            if (port.textProperty().getValue().length() <= 0 || !port.textProperty().getValue().matches("[0-9]+")) {
                showAlert(Alert.AlertType.ERROR, "allerta sistema", "Aggiungi errore server!", "La porta del server deve essere fornita.La porta del server deve essere numerica.");
                return;
            }
            String result = addServer(srvName.textProperty().getValue(), srvIP.textProperty().getValue(), port.textProperty().getValue());
            if (result.equals("fine")) {
                showAlert(Alert.AlertType.INFORMATION, "Avviso di sistema", "Aggiungi informazioni sul server", "Il server è stato aggiunto correttamente");
                srvName.textProperty().setValue("");
                srvIP.textProperty().setValue("");
                port.textProperty().setValue("");
                this.myController.unloadScreen(ScreensFramework.hangmanClientID);
                this.myController.loadScreen(ScreensFramework.hangmanClientID, ScreensFramework.hangmanClientFile);
                this.myController.setScreen(ScreensFramework.hangmanClientID);
            } else {
                showAlert(Alert.AlertType.ERROR, "Avviso di sistema", "Aggiungi errore server!", "Errore: " + result);
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Avviso di sistema", "Aggiungi errore server!", "È necessario fornire l'indirizzo del server.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void cancelAction(ActionEvent event) {
        this.myController.setScreen(ScreensFramework.hangmanClientID);
    }

    private String addServer(String srvName, String srvIP, String port) {
        try {
            //Sarà cambiato in seguito in SAX [Simple API XML] perché è leggero!
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = null;
            Element rootElement = null;
            File f = new File("servers.xml");
            if (f.exists() && !f.isDirectory()) {
                doc = docBuilder.parse("servers.xml");
                rootElement = doc.getDocumentElement();
            } else {
                doc = docBuilder.newDocument();
                rootElement = doc.createElement("servers");
                doc.appendChild(rootElement);
            }

            Element server = doc.createElement("server");
            rootElement.appendChild(server);

            Element serverName = doc.createElement("srvName");
            serverName.appendChild(doc.createTextNode(srvName));
            server.appendChild(serverName);

            Element serverIP = doc.createElement("srvIP");
            serverIP.appendChild(doc.createTextNode(srvIP));
            server.appendChild(serverIP);

            Element serverPort = doc.createElement("port");
            serverPort.appendChild(doc.createTextNode(port));
            server.appendChild(serverPort);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("servers.xml"));

            transformer.transform(source, result);
            return "fine";

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            return pce.getMessage();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            return tfe.getMessage();
        } catch (SAXException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

}

