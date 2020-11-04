// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import client.ChatClient;
import common.ChatIF;

import java.util.Scanner;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version September 2020
 */
public class ClientConsole implements ChatIF
{
    //Class variables *************************************************

    /**
     * The default port to connect on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Instance variables **********************************************

    /**
     * The instance of the client that created this ConsoleChat.
     */
    ChatClient client;


    /**
     * Scanner to read from the console
     */
    Scanner fromConsole;


    //Constructors ****************************************************

    /**
     * Constructs an instance of the ClientConsole UI.
     *
     * @param loginID Client login ID
     * @param host    The host to connect to.
     * @param port    The port to connect on.
     */
    public ClientConsole(String loginID, String host, int port)
    {
        client = new ChatClient(loginID, host, port, this);

        // Create scanner object to read from console
        fromConsole = new Scanner(System.in);
    }


    //Instance methods ************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     *
     * @param args [0] The host to connect to.
     */
    public static void main(String[] args)
    {
        String loginID;
        String host;
        int    port;

        try
        {
            loginID = args[0];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.println("[E] No login ID specified. Connection aborted");
            return;
        }

        if (loginID.contains(" "))
        {
            System.err.println("[E] Login ID must not have space. Connection aborted");
            return;
        }

        try
        {
            host = args[1];
            port = Integer.parseInt(args[2]);
        }
        catch (ArrayIndexOutOfBoundsException | NumberFormatException e)
        {
            host = "localhost";
            port = DEFAULT_PORT;
        }

        ClientConsole chat = new ClientConsole(loginID, host, port);
        chat.accept();  // Wait for console data
    }

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the client's message handler.
     */
    public void accept()
    {
        try
        {
            String message;

            while (true)
            {
                message = fromConsole.nextLine();
                client.handleMessageFromClientUI(message);
            }
        }
        catch (Exception e)
        {
            System.err.println("[E] Unexpected error while reading from console!");
            e.printStackTrace();
        }
    }

    //Class methods ***************************************************

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

// End of ClientConsole class
