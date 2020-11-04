import common.ChatIF;
import server.EchoServer;

import java.io.IOException;
import java.util.Scanner;

/**
 * ServerConsole.java - The CLI of a chat server
 *
 * @author Xiaoxuan Wang 300133594
 */
public class ServerConsole implements ChatIF
{
    // Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    // Instance variables **********************************************

    /**
     * The instance of the server.
     */
    EchoServer server;

    /**
     * Scanner to read from the console
     */
    Scanner fromConsole;


    // Constructors ****************************************************

    /**
     * Constructs an instance of the ServerConsole UI.
     *
     * @param port The port to listen on.
     */
    public ServerConsole(int port)
    {
        server = new EchoServer(port, this);
        try
        {
            server.listen();
        }
        catch (IOException e)
        {
            System.err.println("[E] Could not listen for clients!");
            e.printStackTrace();
        }

        // Create scanner object to read from console
        fromConsole = new Scanner(System.in);
    }


    // Instance methods ************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     *
     * @param args [0] The host to connect to.
     */
    public static void main(String[] args)
    {
        int port; // Port to listen on

        try
        {
            port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException e)
        {
            port = DEFAULT_PORT; //Set port to 5555
        }

        ServerConsole serverConsole = new ServerConsole(port);
        serverConsole.accept();
    }

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the server's message handler.
     */
    public void accept()
    {
        try
        {
            String message;

            while (true)
            {
                message = fromConsole.nextLine();
                server.handleMessageFromServerUI(message);
            }
        }
        catch (Exception e)
        {
            System.err.println("[E] Unexpected error while reading from console!");
        }
    }

    // Class methods ***************************************************

    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message)
    {
        System.out.println(message);
    }
}

// End of ConsoleChat class
