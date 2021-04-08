public class Brain {

    public Brain() {

    }

    private void processInput(String input) {

    }

    private boolean isUserActive() {
        return true;
    }

    private String computeResponse() {
        return "";
    }

    public static void main(String[] args) {
        UserInteraction ui = new UserInteraction();
        Brain brain = new Brain();
        do{
            String input = ui.getInput();
            brain.processInput(input);
            ui.ask(brain.computeResponse());
        }while (brain.isUserActive());
    }
}
