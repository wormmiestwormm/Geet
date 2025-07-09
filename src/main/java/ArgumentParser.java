public class ArgumentParser {
    private String[] args;
    public ArgumentParser(String[] args) {
        this.args = args;
    }

    public void getCommand(){
        if (args.length < 1) {
            System.out.println("Error: Command Not Found");
        }
        else {
            if (args[0].contains("-init")) {
                System.out.println("Initialized geet repository in [path]");
            }
            else if (args[0].contains("-add")){
                System.out.println("Added [path] to commit");
            }
            else if (args[0].contains("-commit")){
                System.out.println("Commited");
            }
            else if (args[0].contains("-log")){
                System.out.println("Log");
            }
            else {
                System.out.println("Error: Command Not Found");
            }
        }
    }
}
