public class Brain {

    private boolean exit = false;
    private String input;
    private DigestedInput answer;

    public Brain() {

    }

    private void processInput () {
        NLP nlp = new NLP();
        exit = nlp.isInputExit(input);
        answer = nlp.process(input);
        //TODO: Tendremos que hacer "exit = false" si vemos que el usuario se despide de nosotros
    }

    private boolean isUserActive() {
        return !exit;
    }

    private String computeResponse() {
        // De momento printamos lo que nos viene del NLP tal cual, sin nada mas
        return answer.getAction() + " - " + answer.getObject();
    }

    public static void main(String[] args) {
        NLP nlp = NLP.getInstance();
        nlp.process("How do you describe ironman?");

        UserInteraction ui = new UserInteraction();
        Brain brain = new Brain();
        do {
            brain.input = ui.getInput();
            brain.processInput();
            ui.print(brain.computeResponse());
        } while (brain.isUserActive());
    }
}
