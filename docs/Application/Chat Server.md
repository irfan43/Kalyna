# Chat Server

# Server Mode

- The `**Kalyna.jar`** application is also designed to run in the server mode
- One can intialize the server using the following command

```bash
java -jar Kalyna.jar server -p <PORT_NUMBER>
```

- If the port number is not mentioned, by default the server runs and listens on Port $5555$.

# Server API

- The server for the Chat application is a very simple API
- The server takes API instruction through a TCP port ($5555$ by default)
- There are **4 endpoints** for the **API** a client can give once they connect to the server

### Login

- This asks the client for a **Username** and RSA Public Key
- The server then stores this information in the Client List
- The server also keeps the socket open and stores the socket information in the Client List
- Incoming messages are later forwarded through this socket to the recipient Client

```java
/**
 * Grabs the Username and Public Key from the Socket
 * This is stored in the<code>clientList</code>along with the socket
 *@throwsIOException if an IOException occurs
 */
private void login() throws IOException{
    String username = br.readLine();
    String Base64PublicKey = br.readLine();

    System.out.println(" new User login " + username);
    ChatServer.clientList.LoginClient(Sock,Base64PublicKey,username);

}
```

### Send Packet

- This Sends a packet to any recipient client registered with the Server
- The Client provides
    - The Recipient Public Key
    - The Senders Public Key
    - The Packet Data
- Note the server does not authenticate the signature - if the connected Client holds the Public Key
- This is done on the Client Side
- This data is passed to the Client List API which further sends the packet along if the user is currently connected

### Get Public Key / Username

- This retrieves the Public Key or Username of a given User
- The Client should provide the username or public key to get the other