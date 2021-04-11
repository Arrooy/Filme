import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.Locale;

public class Finestra extends JFrame {

    private JTextField jtfmessage;
    private JTextPane jepchat;

    private Style style;
    private Style style2;

    private final static int zumbidoSpeed = 10;

    public Finestra(){
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setTitle("Filme");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.black);
        jepchat = new JTextPane();
        jepchat.setFont(new Font("Arial", Font.PLAIN, 16));
        jepchat.setFocusable(false);
        jepchat.setEditable(false);

        jepchat.setBackground(Color.BLACK);
        jepchat.setForeground(Color.GREEN);
        jepchat.setAutoscrolls(true);
        jepchat.setPreferredSize(new Dimension(600,600));
        jepchat.setBorder(BorderFactory.createEmptyBorder());

        StyledDocument doc = jepchat.getStyledDocument();

        JScrollPane editorScrollPane = new JScrollPane(jepchat);
        editorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        editorScrollPane.setBorder(null);
        getContentPane().add(editorScrollPane,BorderLayout.CENTER);

        style = doc.addStyle("BOT",null);
        StyleConstants.setForeground(style, Color.GREEN);

        style2 = doc.addStyle("HUMAN",null);
        StyleConstants.setForeground(style, Color.WHITE);

        jtfmessage = new JTextField();

//        JLabel title = new JLabel("Filme");
//        title.setFont(new Font("Arial", Font.PLAIN, 23));
//        title.setHorizontalAlignment(JLabel.CENTER);
//        title.setVerticalAlignment(JLabel.CENTER);
//        title.setForeground(Color.WHITE);
//        getContentPane().add(title,BorderLayout.NORTH);

        getContentPane().add(jtfmessage,BorderLayout.SOUTH);

        pack();
    }

    public void disableTextbox(){
        this.jtfmessage.setEnabled(false);
    }

    public String getMessage(){
        String text = jtfmessage.getText() ;
        clearTextBox();
        return text;
    }

    public void clearTextBox(){
        jtfmessage.setText("");
    }

    public void addToChat(String name, String newText){

        StyledDocument doc = jepchat.getStyledDocument();
        String text = name + ": " + newText + "\n";
        if(name.toLowerCase(Locale.ROOT).contains("filme")){
            try { doc.insertString(doc.getLength(),text, style); }
            catch (BadLocationException e){}
        }else{
            try { doc.insertString(doc.getLength(),  text, style2); }
            catch (BadLocationException e){}
        }
    }

    public void attach(UserInteraction ui){
        jtfmessage.addKeyListener(ui);
    }

    public boolean showPanel(String title,String message) {
        return  JOptionPane.showConfirmDialog(getContentPane(),
                message, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public void requestWrite(){
        jtfmessage.requestFocus();
    }

    public void zumbido(){
        Point initPos = new Point(getLocation().x,getLocation().y);
        long actualTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - actualTime <= 2000) {
            try {
                setLocation((int) (getLocation().x + Math.round((float) Math.random() * zumbidoSpeed - (float)zumbidoSpeed/2.0)), getLocation().y + Math.round((float) Math.random() * zumbidoSpeed - (float)zumbidoSpeed/2));
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setLocation(initPos);
    }
}
