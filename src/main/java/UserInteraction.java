import java.util.Locale;
import java.util.Scanner;

public class UserInteraction {

    private final Scanner sc;

    private final String[] emogyList = {
            ":)" ,
            ":(" ,
            ":D" ,
            ":P" ,
            ":S",
            "¬¬",
            "o.O",
            "C.c"
    };

    UserInteraction(){
       sc = new Scanner(System.in);
    }

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
