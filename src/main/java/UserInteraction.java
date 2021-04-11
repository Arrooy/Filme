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
    private long lastZoombido;

    UserInteraction(Brain brain){
        this.brain = brain;
        userName = "User";
        messagesToProcess = new PriorityBlockingQueue<>();
        lastInteraction = System.currentTimeMillis();
        lastZoombido = System.currentTimeMillis();
    }

    // Nota: la func no retorna fins que l'usauri no fa return.
    public String getInput(){
        String line = messagesToProcess.poll();
        return filterLine(line).toLowerCase(Locale.ROOT);
    }

    //private boolean shown = false;
    public boolean hasInput(){
        /*if (System.currentTimeMillis() - lastInteraction > 1000 && !shown) {
            System.out.println("1s");
            shown = true;
        } else {
            shown = false;
        }
        if (messagesToProcess.size() != 0) System.out.println(messagesToProcess.size());
        */return !messagesToProcess.isEmpty();
    }

    private String filterLine(String line) {
        //Eliminem caràcters que no siguin ascii
        line = line.replaceAll("[^\\p{ASCII}]", "");
        line = line.trim().replaceAll("( )+"," ");
        line = line.replaceAll("(\n)+","\n");
        return line;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void print(String message){
        String line = filterLine(message);

        brain.getWindow().addToChat(userName, line);
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

        // Comeback message
        if (timeSinceLastInteraction() > Brain.APPEAL_TIME || timeSinceLastZoombido() > Brain.VIBRATE_SCREEN_TIME){
            brain.getWindow().addToChat("Filme",Behaviour.USER_TYPES_AFTER_BEING_AWAY.getRandom());
        }

        // Restart message timers
        this.lastInteraction = System.currentTimeMillis();
        this.lastZoombido = System.currentTimeMillis();

        // Process enter.
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                String message = brain.getWindow().getMessage();
                messagesToProcess.add(message);
                print(message);
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
            default:
        }
    }

    public long timeSinceLastInteraction(){
        return System.currentTimeMillis() - lastInteraction;
    }

    public long timeSinceLastZoombido() {
        return System.currentTimeMillis() - lastZoombido;
    }

    public void interacted() {
        lastInteraction = System.currentTimeMillis();
    }
    public void interactedZoombido() {
        lastZoombido = System.currentTimeMillis();
    }
}
