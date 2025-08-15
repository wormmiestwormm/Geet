import java.io.IOException;
import java.util.Arrays;

public class Geet{
    public static void main(String[] args) throws IOException {
        ArgumentParser parser = new ArgumentParser(args);
        parser.getCommand();
    }
}