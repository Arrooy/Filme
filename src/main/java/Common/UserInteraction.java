package Common;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class UserInteraction implements KeyListener {

    private final Brain brain;
    private long lastInteraction;
    private long timeToLeaveAloneToRead;
    private String userName;

    private final Queue<String> messagesToProcess;

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
    private boolean missingUser;

    UserInteraction(Brain brain){
        this.brain = brain;
        userName = "User";
        messagesToProcess = new PriorityBlockingQueue<>();
        lastInteraction = System.currentTimeMillis();
        lastZoombido = System.currentTimeMillis();
        timeToLeaveAloneToRead = 0;
        missingUser = false;
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

        line = line.replaceAll("[ñÑ]","n");
        line = line.replaceAll("[çÇ]","c");

        //Eliminem simbols ascii que no son d'interes.
        line = line.replaceAll("[^a-zA-Z\\d\\s:,.]","");

        // Eliminem espais i salts de linia multiples.
        line = line.trim().replaceAll("( )+"," ");
        line = line.replaceAll("(\n)+","\n");
        return line;
    }

    public void setUserName(String userName) {
        this.userName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

        // Comeback message
        if (timeSinceLastInteraction() > Brain.APPEAL_TIME || missingUser){
            brain.getWindow().addToChat("Filme", Behaviour.USER_TYPES_AFTER_BEING_AWAY.getRandom());
            missingUser = false;
        }

        // Restart message timers
        this.lastInteraction = System.currentTimeMillis();
        this.lastZoombido = System.currentTimeMillis();

        // Process enter.
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                String message = brain.getWindow().getMessage();
                messagesToProcess.add(message);
                brain.getWindow().addToChat(userName, message);
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
            default:
        }
    }
    public void updateTimeToRead(String textToRead){
        if(textToRead == null || textToRead.isEmpty())return;

        String[] words = textToRead.split("\\s+");
        timeToLeaveAloneToRead = words.length * 250L;
    }

    public long currentTime (){
        return System.currentTimeMillis() - timeToLeaveAloneToRead;
    }

    public long timeSinceLastInteraction(){
        return currentTime() - lastInteraction;
    }

    public void interacted() {
        lastInteraction = System.currentTimeMillis();
        timeToLeaveAloneToRead = 0;
        missingUser = true;
    }
}
