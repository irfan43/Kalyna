# Chat Client

# Client Mode

- To start the application as a Chat client, one can run the following code

```bash
java -jar Kalyna.jar login -u <USER_NAME> -i <SERVER_IP/DOMAIN_NAME>
```

- If the server is running on port other than the default $5555$ then that has to be mentioned while login as well

# Client Classes

- The Chat Client has many Java Classes that handle how the chat client interacts with other clients and the server
- Once the user starts the Client using the above command it calls these classes

### Chat Cipher Class

- This Class handles the RSA key pair
- It is responsible for generating the key pair
- Saving it into the file
- Opening such key pair files
- It also generates Signing objects to sign data with out RSA private key

### Packet Handler

- This Class Handles the Incoming and outgoing Packets
- When the Packet Reader class receives packets from the server
    - It send it to the Packet Handler
    - Packet Handler Checks the type of the packet
    - After which stores it in corresponding list
- The Packet Handler also handles
    - Generating Packets
    - Sending Packets to the server (via the Client Network Class)

### Client Network

- This handles the communication with the server and client
- This also has a implementation of the Java Runnable (Java multi-threading Framework)
- Allowing a thread to be listening to for new packet from the server

### Chat Console

- This acts as a sort of front end for the chat application
- The class use the Raw Console Input library to use the Raw Console commands
- This allows us to print incoming message while the client is typing there message

### Chat Connector

- This Class acts as a back end to the Chat Console
- It handles the Diffie Hellman
- It handles creating encrypting messages and signing them
- It also handles the Decryption of incoming Packets

### Chat Packet

- Decodes the Binary Data of the Packets into the Sender Public Key, Type, message
- Encodes Sender Public Key, Type, message into binary data.

---