import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Locale;
import java.util.Scanner;

public class UserInteraction {

    private final Scanner sc;
    private final NonblockingBufferedReader nbbr;
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

    UserInteraction(){
        sc = new Scanner(System.in);
        nbbr = new NonblockingBufferedReader(new BufferedReader(new InputStreamReader(System.in)));
    }

    public String getInputNonBlocking(){
        try {
            return nbbr.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Nota: la func no retorna fins que l'usauri no fa return.
    public String getInput(){
        String line = sc.nextLine();
        System.out.println();
        return filterLine(line);
    }

    private String filterLine(String line) {
        //Eliminem caràcters que no siguin ascii
        line = line.replaceAll("[^\\p{ASCII}]", "");
        line = line.toLowerCase(Locale.ROOT);
        return line;
    }

    public void print(String message){
        String line = filterLine(message);
        System.out.println(line);
    }
}
