NAME: Giovanni Hernandez
ASSIGNMENT: Project2_EXTRACREDIT
COURSE: CS 4345, Operating Systems, Spring 2023

PREFACE: 

To begin, this package contains a program which utilizes connection-oriented (TCP) sockets to implement communication 
between multiple users. There are two files: "project2_server.java" and "project2_client.java". As the name suggests, 
project2_server.java contains the source code for the 'server' side of the communcation. Conversely, project2_client.java 
contains the code for the 'client' side of the communcation.


COMPILATION INSTRUCTIONS (PLEASE READ):

To compile, you will first need to compile the server code using the command 'javac project2_server.java'. Then, you can run
the server code using the command 'java Server'. Next, compile the client side by using the command 'javac project2_client.java'.
Then, run the client code using command 'java Client'. To create multiple users, you only need to compile once so long as you 
run the code in the same directory as the class file generated from compilation. Afterwards, you can open multiple terminals, 
navigate to the appropriate directory, and create additional users using the same command asrunning the client code 'java Client'. 
IMPORTANT: You must compile and run the server side code before attempting to run the client side code. Failure to do so will 
create errors with the client side code. 
EXAMPLE: If we want to create communication between three clients, we do the following:
1. Open four terminals: one for the server and three for the clients, one for each.
2. 'javac project2_server.java' to compile
3. 'javac Server' to start the server. NOTE: Server will continue running until CTRL+C is entered.
4. 'javac project2_client.java' to compile client side code
5. 'java Client' for each client, once per remaining terminal.


OVERVIEW: 

This section provides a brief overview of the source code and what happens. Further information can be found within the comments
of the source code. 

    Client side:
    Client code has two classes: GUI and Client. The GUI class contains code to create the login page and the message chat page.
    The Client class contains the code for handling sending messages to the server and receiving messages from the server. Once 
    the client side code is ran, an initial GUi appears, prompting the user for a username. Enter a desired username and once
    logged in, the GUI updates to display the program (NOTE: The code does not check for unique usernames. Subsequently, I would
    strongly advise on ensuring unique usernames are entered). Then, once client is connected and username is entered, a new thread 
    called ReceiveThread is created to handle receiving messages from the server. The main GUI contains a primary text area displaying 
    the global chat from all users, previous and current. Below this is an area to enter messages to be sent to the other clients. 
    Currently, there is no feature allowing personal messages between clients. Only global chat is allowed. To leave the chat and 
    close the program, simply press the red X on the corner of the window or type 'exit' into the textbox to exit the program. 
    NOTE: The GUI is NOT responsive, that is, the components of the GUI (buttons, textboxes) are in fixed positions and do not adjust
    based on the size of the window. Therefore, I strongly suggest NOT resizing the window once GUI is created.

    Server side:  
    Once server side code is ran, the server starts and continues indefinitely. Server will print to terminal every message 
    received from any client. Occasionally, the server will encounter an EOFException thrown from DataInputStreams that are 
    closed. This is normal and does not crash nor harm the server. The server maintains a list of all clients. The clients are 
    added to this list upon their connection to the server. However, due to the nature of TCP within Java, the server can only
    detect if a client has left until after the server attempts to send that client a message and an EOFException is thrown. 
    After catching this exception, socket on server side is closed and client is removed from list of active clients. Print 
    statements are used to identify when this situation is ocurring. These are available only in the server terminal. Once the 
    server reaches zero clients after having multiple clients connected and multiple messages sent, I advise to close the server
    and run again to erase all messages from the data. This ensure there is no confusion on old and new sessions.


CONCLUSION:

This code has been tested for handling multiple clients as well as being able to handle multiple disconnections and reconnections.
This code also has been for messages being and delivered properly without any issues. Following compilation instructions annotated
above will ensure you receive the same experience as I can.