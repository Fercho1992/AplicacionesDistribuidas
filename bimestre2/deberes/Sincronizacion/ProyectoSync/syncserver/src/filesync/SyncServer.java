package filesync;

/**
 *
 * @author edwinsp
 * @date 8th April 2013
 */
public class SyncServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Usage: java -jar syncserver.jar");
            System.exit(-1);
        }
        String filename = args[0];

        SyncServerThread c = new SyncServerThread();
        c.start();
    }
}
