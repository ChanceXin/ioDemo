import java.io.IOException;

public class testsys {
    public static void main(String[] args) {
        try {
            while (true){
                System.out.println(System.in.read());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
