// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package server;

import common.ChatIF;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
    ChatIF serverUI;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port, ChatIF serverUI)
    {
        super(port);
        this.serverUI = serverUI;
    }

    /**
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client)
    {
        String message = msg.toString();

        if (message.charAt(0) == '#')
        {
            serverUI.display("[I] Message received: " + message + " from " + client);

            String[] tokens = message.split(" ");
            try
            {
                switch (tokens[0])
                {
                    case "#login":
                        if (client.getInfo("loginID") == null)
                        {
                            serverUI.display("[I] A new client is attempting to connect to the server");
                            client.setInfo("loginID", tokens[1]);
                            serverUI.display("[I] " + tokens[1] + " has logged on");
                        }
                        else
                        {
                            try
                            {
                                client.sendToClient(
                                        "[E] The #login command should only be allowed as the first command");
                                client.close();
                            }
                            catch (IOException ignored) {}
                        }
                        break;
                    case "#logoff":
                        String s = "[I] " + client.getInfo("loginID") + " has disconnected";
                        serverUI.display(s);
                        sendToAllClients(s);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
            catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e)
            {
                serverUI.display("[W] Unknown command or syntax error from client " + client.toString());
            }
        }
        else
        {
            String loginID = client.getInfo("loginID").toString();
            serverUI.display("[I] Message received: " + message + " from " + loginID);
            this.sendToAllClients(loginID + "> " + msg);
        }
    }

    /**
     * This method handles any messages received from the server UI.
     *
     * @param message The message received from the server UI.
     */
    public void handleMessageFromServerUI(String message)
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
                    case "#stop":
                        if (isListening())
                            stopListening();
                        else
                            serverUI.display("[W] Already stopped");
                        break;
                    case "#close":
                        close();
                        break;
                    case "#setport":
                        if (!isListening())
                        {
                            setPort(Integer.parseInt(tokens[1]));
                            serverUI.display("[I] Port set to " + getPort());
                        }
                        else
                            serverUI.display("[W] Please stop the server first");
                        break;
                    case "#start":
                        if (!isListening())
                            listen();
                        else
                            serverUI.display("[W] Already listening");
                        break;
                    case "#getport":
                        serverUI.display("[I] Port: " + getPort());
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
            catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e)
            {
                serverUI.display("[W] Unknown command or syntax error");
            }
            catch (IOException ignored)
            {
            }
        }
        else
        {
            String s = "SERVER MESSAGE> " + message;
            serverUI.display(s);
            sendToAllClients(s);
        }
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted()
    {
        serverUI.display("[I] Server listening for connections on port " + getPort());
    }

    @Override
    protected void clientConnected(ConnectionToClient client)
    {
        serverUI.display("[I] Client " + client.toString() + " connected");
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client)
    {
        serverUI.display("[I] Client " + client.getInfo("loginID").toString() + " disconnected");
    }

    public void quit()
    {
        try
        {
            close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.exit(0);
    }

    //Class methods ***************************************************

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped()
    {
        serverUI.display("[I] Server has stopped listening for connections.");
    }
}
// End of EchoServer class
