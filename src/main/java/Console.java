public class Console {
    public static void print(String... msg) {
        for (String message : msg) {
            System.out.println(message);
        }
        System.out.println("-----");
    }

    public static void print(Boolean linebreak, String... msg) {
        for (String message : msg) {
            System.out.println(message);
        }
        if (linebreak) {
            System.out.println("-----");
        }
    }
}
