import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class HangmanServerHandler implements Runnable { 
            //gestione del gioco con tentativi = 7,
    private final static short _TatalAttempts = 7;

    private Socket clientSocket;
    private ArrayList<String> words;

    private String currentWord;     //la parola in corso
    private String attempedGuess;       //ha tentato di indovinare

    private short remainingAttempts;        //tentativi rimanenti
    private short score;            //punteggio

    HangmanServerHandler(ArrayList<String> words, Socket clientSocket) {    // La parola argomento è un elenco di parole che possono essere utilizzate nel gioco e l' clientSocket argomento rappresenta la connessione al client. Il punteggio variabile è  utilizzata per tenere traccia del punteggio del giocatore nel gioco
        this.clientSocket = clientSocket;
        this.words = words;
        this.score = 0;
    }

  
    public void run() {     //metodo viene eseguito in un thread separato per gestire la comunicazione con un client che si è connesso al server tramite un socket.
        BufferedReader br = null;
        PrintWriter pw = null;      //metodo viene eseguito in un thread separato per gestire la comunicazione con un client che si è connesso al server tramite un socket.

        try {
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String line;
            String msg;

            while ((line = br.readLine()) != null) {            //Quindi entra in un ciclo infinito che legge una riga di input dal client utilizzando il readLinemetodo di BufferedReader. Se la riga non è null, il codice controlla se la riga è uguale alla stringa "nuovo_gioco". Se lo è, il server avvierà una nuova partita con il client. Se la linea è null, il loop terminerà, terminando la comunicazione con il client.
                if (line.trim().equals("nuovo_gioco")) {
                    currentWord = getRandomWord();
                    remainingAttempts = _TatalAttempts;
                    attempedGuess = "";
                    msg = String.format("inizio;%s;%s;%s", getWordWithDashes(), remainingAttempts, score);
                    pw.println(msg);
                    pw.flush();
                } else if (line.trim().equals(currentWord)) {
                    score++;
                    msg = String.format("vinto;%s;%s;%s", currentWord, remainingAttempts, score);
                    pw.println(msg);
                    pw.flush();
                } else if (line.trim().length() == 1) {

                    String oldWordWithDashes = getWordWithDashes();     //Il metodo funziona chiamando se stesso in modo ricorsivo, sostituendo ogni volta il carattere nell'indice corrente con un trattino e incrementando l'indice di 1. La ricorsione continua finché l'indice charIndexnon è maggiore o uguale alla lunghezza della parola (stringa), a quel punto il metodo restituisce la modifica della parola come stringa
                    attempedGuess += line;
                    String newWordWithDashes = getWordWithDashes();

                    if (newWordWithDashes.equals(currentWord)) {
                        score++;
                        msg = String.format("GRANDE_VINCITA;%s;%s;%s", currentWord, remainingAttempts, score);
                        pw.println(msg);
                        pw.flush();
                    } else if (oldWordWithDashes.equals(newWordWithDashes)) {
                        if (--remainingAttempts == 0) {
                            score--;
                            msg = String.format("SCONFITTA;%s;%s;%s", currentWord, remainingAttempts, score);
                            pw.println(msg);
                            pw.flush();
                        } else {
                            msg = String.format("sbagliato;%s;%s;%s", newWordWithDashes, remainingAttempts, score);
                            pw.println(msg);
                            pw.flush();     //metodo viene quindi chiamato PrintWriterper garantire che tutti i dati memorizzati nel buffer vengano scritti nella destinazione.
                        }
                    } else {
                        msg = String.format("corretto;%s;%s;%s", newWordWithDashes, remainingAttempts, score);
                        pw.println(msg);
                        pw.flush();
                    }
                } else if (--remainingAttempts == 0) {
                    score--;
                    String message = String.format("SCONFITTA;%s;%s;%s", currentWord, remainingAttempts, score);
                    pw.println(message);
                    pw.flush();
                } else {
                    String message = String.format("sbagliato;%s;%s;%s", getWordWithDashes(), remainingAttempts, score);
                    pw.println(message);
                    pw.flush();
                }
            }
            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    private String getRandomWord() {       //metodo random per prendere la parola da indovinare
        int randIndex = (int) (Math.random() * words.size());
        String word = words.get(randIndex);
        System.out.println("=== Parola === " + word);
        return word;
    }

    private String getWordWithDashes() {            //   sto metodo sembra restituire una versione della parola,(stringa) con alcuni caratteri sostituiti da trattini ('-'). Se la attempedGuesss tringa non è vuota, il metodo utilizza un'espressione regolare per sostituire i caratteri della parola inclusi lettere della parola trattini.
        if (attempedGuess.length() > 0) {
            return currentWord.replaceAll("[^" + attempedGuess + "]", "-");
        } else {
            return getAllWordToDashes(new StringBuilder(currentWord), 0);
        }
    }

    private String getAllWordToDashes(StringBuilder word, int charIndex) {      //l metodo funziona chiamando se stesso in modo ricorsivo, sostituendo ogni volta il carattere nell'indice corrente con un trattino e incrementando l'indice di 1. La ricorsione continua finché l'indice charIndexnon è maggiore o uguale alla lunghezza della parola tringa, a quel punto il metodo restituisce la modifica della parola come stringa.
        if (charIndex >= word.length()) {
            return word.toString();
        } else {
            word.setCharAt(charIndex, '-');
            return getAllWordToDashes(word, ++charIndex);
        }
    }
}
