// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import common.ChatIF;
import ocsf.client.AbstractClient;

import java.io.IOException;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient
        extends AbstractClient
{
    //Instance variables **********************************************

    /**
     * The interface type variable.  It allows the implementation of
     * the display method in the client.
     */
    ChatIF clientUI;

    /**
     * Client login ID
     */
    String loginID;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param loginID  Client login ID
     * @param host     The server to connect to.
     * @param port     The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String loginID, String host, int port, ChatIF clientUI)
    {
        super(host, port); // Call the superclass constructor
        this.loginID  = loginID;
        this.clientUI = clientUI;

        try
        {
            login();
        }
        catch (IOException e)
        {
            clientUI.display("[E] Cannot open connection. Awaiting command");
        }
    }


    //Instance methods ************************************************

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg)
    {
        clientUI.display(msg.toString());
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message)
    {
        try
        {
            if (message.charAt(0) == '#')
            {
                String[] tokens = message.split(" ");
                try
                {
                    switch (tokens[0])
                    {
                        case "#quit":
                            quit();
                            break;
                        case "#logoff":
                            if (isConnected())
                                logoff();
                            else
                                clientUI.display("[W] Not connected");
                            break;
                        case "#sethost":
                            if (!isConnected())
                            {
                                setHost(tokens[1]);
                                clientUI.display("[I] Host set to " + getHost());
                            }
                            else
                                clientUI.display("[W] Please disconnect first");
                            break;
                        case "#setport":
                            if (!isConnected())
                            {
                                setPort(Integer.parseInt(tokens[1]));
                                clientUI.display("[I] Port set to " + getPort());
                            }
                            else
                                clientUI.display("[W] Please disconnect first");
                            break;
                        case "#login":
                            if (!isConnected())
                            {
                                try
                                {
                                    login();
                                }
                                catch (IOException e)
                                {
                                    clientUI.display("[E] Cannot open connection. Awaiting command");
                                }
                            }
                            else
                                clientUI.display("[W] Already connected");
                            break;
                        case "#gethost":
                            clientUI.display("[I] Host: " + getHost());
                            break;
                        case "#getport":
                            clientUI.display("[I] Port: " + getPort());
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
                catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e)
                {
                    clientUI.display("[W] Unknown command or syntax error");
                }
            }
            else
            {
                sendToServer(message);
            }
        }
        catch (IOException e)
        {
            clientUI.display("[E] Could not send message to server.");
        }
        catch (StringIndexOutOfBoundsException ignored)
        {}
    }

    /**
     * Getter of loginID
     *
     * @return String LoginID of the client
     */
    public String getLoginID()
    {
        return loginID;
    }

    public void login() throws IOException
    {
        openConnection();
        sendToServer("#login " + loginID);
        clientUI.display("[I] " + loginID + " has logged on");
    }

    public void logoff() throws IOException
    {
        sendToServer("#logoff");
        closeConnection();
    }

    /**
     * This method terminates the client.
     */
    public void quit()
    {
        try
        {
            logoff();
        }
        catch (IOException ignored) {}

        clientUI.display("[I] Quitting...");
        System.exit(0);
    }

    @Override
    protected void connectionClosed()
    {
        clientUI.display("[I] Connection closed");
    }

    @Override
    protected void connectionException(Exception exception)
    {
        clientUI.display("[W] The server has stopped listening for connections");
        clientUI.display("[W] SERVER SHUTTING DOWN! DISCONNECTING!");
        clientUI.display("[W] Abnormal termination of connection");
    }
}

// End of ChatClient class
