import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class UserInteraction implements KeyListener {

    private final Brain brain;
    private long lastInteraction;
    private String userName;
    private Queue<String> messagesToProcess;

    private final String[] emogyList = {
            ":)" ,
            ":(" ,
            ":D" ,
            ":P" ,
            ":S",
            "¬¬",
            "o.O",
            "XD",
            "UwU",
            "C.c"
    };

    UserInteraction( Brain brain){
        this.brain = brain;
        userName = "User";
        messagesToProcess = new PriorityBlockingQueue<>();
    }

    // Nota: la func no retorna fins que l'usauri no fa return.
    public String getInput(){
        String line = messagesToProcess.poll();
        return filterLine(line).toLowerCase(Locale.ROOT);
    }

    public boolean hasInput(){
        return !messagesToProcess.isEmpty();
    }

    private String filterLine(String line) {

        //Eliminem caràcters que no siguin ascii
        line = line.replaceAll("[^\\p{ASCII}]", "");
        line = line.trim().replaceAll("( )+"," ");
        line = line.replaceAll("(\n)+","\n");
        return line;
    }

    public void print(String message){
        String line = filterLine(message);
        System.out.println(line);
    }

    public long timeSinceLastInteraction(){
        return System.currentTimeMillis() - lastInteraction;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.lastInteraction = System.currentTimeMillis();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                String message = brain.getWindow().getMessage();
                messagesToProcess.add(message);
                brain.getWindow().addToChat(userName, message);
                break;
            default:
        }
    }
}
