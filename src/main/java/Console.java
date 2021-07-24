public class Console {
    /**
     * Prints a message on the console which contains a linebreak sequence.
     * @param msg The message to print to the console.
     */
    public static void print(String... msg) {
        for (String message : msg) {
            System.out.println(message);
        }
        System.out.println("-----");
    }

    /**
     * Prints a message on the console which can contain a linebreak sequence.
     * @param linebreak Boolean value on whether a linebreak should be printed.
     * @param msg The message to print to the console.
     */
    public static void print(Boolean linebreak, String... msg) {
        for (String message : msg) {
            System.out.println(message);
        }
        if (linebreak) {
            System.out.println("-----");
        }
    }
}
