import java.util.Arrays;

public class Geet{
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println(Arrays.toString(args));
        ArgumentParser parser = new ArgumentParser(args);
        parser.getCommand();
    }
}