package service;
public class Server {           //Questa è una classe che rappresenta un server. Ha tre campi: srvName, srvIP, e port. 
    private String srvName;
    private String srvIP;               //Questi campi contengono rispettivamente il nome del server, l'indirizzo IP del server e il numero di porta su cui il server è in ascolto. La classe ha metodi getter e setter per ciascuno di questi campi.
    private String port;

    public String getSrvName() {
        return srvName;
    }

    public void setSrvName(String srvName) {
        this.srvName = srvName;
    }

    public String getSrvIP() {
        return srvIP;
    }

    public void setSrvIP(String srvIP) {
        this.srvIP = srvIP;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Server() {
    }

    public Server(String srvName, String srvIP, String port) {          //Dispone inoltre di un costruttore predefinito e di un costruttore che accetta tre argomenti: srvName, srvIPe port, utilizzati per inizializzare i campi.
        this.srvName = srvName;
        this.srvIP = srvIP;
        this.port = port;
    }
}
