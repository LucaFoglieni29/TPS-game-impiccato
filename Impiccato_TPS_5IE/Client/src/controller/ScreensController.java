package controller;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;


public class ScreensController extends StackPane {

    private HashMap<String, Node> screens = new HashMap<String, Node>();
    private Stage primaryStage;

    public ScreensController() {
        super();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //Aggiungi schermo alla raccolta
    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    //restituisce un nodo con un nome appropriato
    public Node getScreen(String name) {
        return screens.get(name);
    }

    //caricare il file fxml, aggiungere lo schermo alle raccolte di schermi e
    //finalmente inietta lo screenPane nel controller
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            IScreensController myScreenController = ((IScreensController) myLoader.getController());
            myScreenController.setScreenParent(this);
            addScreen(name, loadScreen);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean setScreen(final String name) {
        if (screens.get(name) != null) { // caricatore dello schermo
            final DoubleProperty opacity = opacityProperty();

            if (!getChildren().isEmpty()) { // se c'è più di uno schermo

                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(1000), new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                getChildren().remove(0); // rimuovere la schermata visualizzata
                                getChildren().add(0, screens.get(name)); //aggiungi lo schermo
                                fitNodeInParent(name);
                                setNodeTitle(name);

                                Timeline faceIn = new Timeline(
                                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                        new KeyFrame(new Duration(800), new KeyValue(opacity, 1.0)));
                                faceIn.play();
                            }

                        }, new KeyValue(opacity, 0.0)));
                fade.play();
            } else {
                
                getChildren().add(screens.get(name)); //se non è stato visualizzato nessun altro, allora mostra
                fitNodeInParent(name);
                setNodeTitle(name);
              
            }
            return true;
        } else {
            System.out.println("Lo schermo non è stato caricato!");
            return false;
        }
    }

    public boolean unloadScreen(String name) {
        if (screens.remove(name) == null) {
            System.out.println("Lo schermo non esisteva");
            return false;
        } else {
            return true;
        }
    }

    private void setNodeTitle(String name) {
        switch (name.split(";")[0]) {
            case "main":
                primaryStage.setTitle("Hydra [Applicazione client dell'impiccato]");
                break;
            case "serverManager":
                primaryStage.setTitle("Hydra ServerManager");
                break;
            default:
                primaryStage.setTitle("");
                break;
        }
    }

    private void fitNodeInParent(String name) {
        try {
            String[] nameArr = name.split(";");
            double w = Double.parseDouble(nameArr[1]);
            double h = Double.parseDouble(nameArr[2]);

            primaryStage.setMinWidth(w);
            primaryStage.setMinHeight(h);

            primaryStage.setWidth(w);
            primaryStage.setHeight(h);
            primaryStage.centerOnScreen();

            // (adattare il nodo allo stadio primario)
            // Nota: cambierò nella prossima versione il nodo alla regione nella definizione di HashMap
            //come Region è una sottoclasse di Node
            Region reg = (Region) screens.get(name);
            reg.prefWidthProperty().bind(primaryStage.widthProperty());
            reg.prefHeightProperty().bind(primaryStage.heightProperty());

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }
}
