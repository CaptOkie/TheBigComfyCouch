package couch.cushion;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import couch.cushion.actor.Connection;
import couch.cushion.actor.message.ChangeUsername;
import couch.cushion.actor.message.ChatMessage;
import couch.cushion.actor.message.Connect;
import couch.cushion.ui.HomeScene;
import couch.cushion.ui.StartupScene;
import couch.cushion.ui.VideoPlayer;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TheBigComfyCouch extends Application {
    
    private static final Path LIBRARY = Paths.get(System.getProperty("user.home"), "couch-library") ;
    private String username; //the user name of the user
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
        while (ifs.hasMoreElements()) {
            final NetworkInterface ni = ifs.nextElement();
            if (!ni.isLoopback() && !ni.isVirtual() && ni.isUp()) {
                final Enumeration<InetAddress> inets = ni.getInetAddresses();
                while (inets.hasMoreElements()) {
                    final InetAddress addr = inets.nextElement();
                    if (!addr.isLinkLocalAddress()) {
                        System.setProperty("akka.remote.netty.tcp.hostname", addr.getHostAddress());                
                    }
                    break;
                }
                break;
            }
        }
        final VideoPlayer player = new VideoPlayer();
        HomeScene home = new HomeScene(player);
        final Connection connection = new Connection(player, home);
        
        // Setting up main window
        primaryStage.setTitle("The Big Comfy Couch");
        
        Files.createDirectories(LIBRARY);

        //setting a default username
        username = "The Big Cheese";
        // Creating Home scene
        // setting event handler for importing a file
        StartupScene startup = new StartupScene();

        home.setOnSendPressed(e -> {
            String message = home.getMessage();
            if(!message.equals("")){
                connection.sendMessage(new ChatMessage(username, message));
            }
        });

        home.setOnLoadVideo(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File file = fileChooser.showOpenDialog(home.getWindow());
            if (file != null) {
                connection.decode(file.toString());
                home.setPlaying(false);
            }
        });
        
        home.setOnPausePlayPressed(e -> {

            if (home.togglePlaying()) {
                connection.play();
            }
            else {
                connection.pause();
            }
        });

        startup.setOnConnectPressed(e -> {
            String ip = startup.getIPAddress();
            username = startup.getUsername();
            if(!username.trim().equals("") && !ip.equals("")) {

                home.addUserToList(username);
                primaryStage.setScene(home);
                connection.changeUsername(new ChangeUsername(null, username));
                connection.connect(new Connect(ip));
            }
        });

        startup.setOnHostPressed(e -> {
            username = startup.getUsername();
            if(!username.trim().equals("")) {
                connection.changeUsername(new ChangeUsername(null, username));
                home.addUserToList(username);
                primaryStage.setScene(home);
            }
        });
        
        // Displaying the window
        primaryStage.setScene(startup);
//        primaryStage.setScene(home);
        primaryStage.setOnCloseRequest(e -> {
            connection.terminate();
        });
        primaryStage.show();
        
    }
}
