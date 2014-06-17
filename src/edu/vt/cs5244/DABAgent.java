package edu.vt.cs5244;

/**
 * This interface is implemented by clients to allow remote
 * access to a DABEngine instance on a server via the DDP specification.
 * All methods of all implementations of this interface (including those inherited
 * from DABEngine) are required to throw a DABClientException immediately upon 
 * receiving any unexpected response from the server.
 */
public interface DABAgent extends DABEngine {
    
    /**
     * Connect this client to the specified server.
     *
     * This method only returns (without an exception) once a TCP connection
     * has been successfully established with a DDP server.
     * If a connection has already been established, this method will immediately
     * return false, without taking any further action.  No requests are sent by
     * this method, but the welcome message from the server is read and validated.
     * If the welcome message is not valid, this method closes the connection 
     * immediately and throws a DABClientException.
     *
     * @param server Host name (or IP address) of the server
     * @return true if the connection was made successfully;
     *   false otherwise (if this client was already connected to a DDP server)
     * @throws DABClientException
     *   if any problems are encountered attempting to connecting to the server,
     *   or if the server does not respond as expected.
     *
     */
    public boolean connect(String server);
    
    /**
     * Disconnect this client from the server.
     *
     * If the client is not already connected, this method takes no action.
     * Otherwise, this method sends a command request to the server
     * to gracefully end the session, and waits for the expected response.
     * However, regardless of the response, this method always closes the TCP connection.
     * Implementations must ensure that this method succeeds without throwing any exceptions.
     *
     */
    public void disconnect();
}