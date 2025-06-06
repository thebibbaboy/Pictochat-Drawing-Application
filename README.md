# Pictochat-Drawing-Application
A server-based Java program that simulates the popular Nintendo DS Minigame, Pictochat

DEMO VIDEO: https://youtu.be/_CY6Noy6oCs

Hello! Thank you for using the Java Pictochat Simulator! =)

This is a program that acts as a basic virtual drawing platform
for two users to connect onto. It builds on the Nintendo DS
game Pictochat, a program that acts very similarly. 

FEATURES:
As of now, this program includes:

-Functionality for two players to connect and draw together
on a shared canvas that updates continuously.
-Stroke size, brush color, and erase options are all included 


HOW TO TEST:

1. Import the JAR file into BlueJ, then open the project.


2. Right click on the "Server" class, then click "new Server(int port)"


3. In the "new Server" text box that you are prompted with, type in a number. This represents the port.
 
     -This number will be used to conenct clients to the server. Type in your number and click "Ok".
     -You should see a window that opens, prompting you with "Server started on port (your port number)"

4. Launch a new BlueJ application. 


5. Right click on the Client class, then click "new Client(String address)"


6. You will be prompted with a "String Address" and "int port" text box.

     -Type "localhost" (with quotations) in the string address
     -Type your earlier port number into the int port text box

7. Click "Ok". A window will now open up that you could draw freely on. 


8. Repeat steps 4-7 if you would like to draw with a second friend! The canvas gets updated 
when either client draws from their local system. 
 
