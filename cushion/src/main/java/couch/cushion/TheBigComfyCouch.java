package couch.cushion;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import couch.cushion.actor.Connection;
import couch.cushion.media.VideoConverter;
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
        
        final VideoPlayer player = new VideoPlayer();
        HomeScene home = new HomeScene(player);
        final Connection connection = new Connection(player, home);
        
        // Setting up main window
        primaryStage.setTitle("The Big Comfy Couch");
        
        Files.createDirectories(LIBRARY);

        //setting a default username
        username = "DefaultUser";
        // Creating Home scene
        // setting event handler for importing a file
        StartupScene startup = new StartupScene();
//        home.setOnImport(e -> {
//            FileChooser fileChooser = new FileChooser();
//            File file = fileChooser.showOpenDialog(home.getWindow());
//            if(file != null) {
//                try {
//                    Path src = LIBRARY.resolve(file.getAbsolutePath());
//                    Path dst = LIBRARY.resolve(com.google.common.io.Files.getNameWithoutExtension(src.getFileName().toString()) + ".mp4");
////                    Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
//                    
//                    VideoConverter converter = new VideoConverter("MP4");
//                    converter.convert(src.toUri().toURL(), dst.toUri().toURL());
//                }
//                catch (Exception e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//            }
//        });

        home.setOnSendPressed(e -> {
            String message = home.getMessage();
            if(!message.equals("")){
                connection.sendMessage(message);
            }
        });

        home.setOnLoadVideo(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File file = fileChooser.showOpenDialog(home.getWindow());
            if (file != null) {
//                new Thread(() -> {
//                    try {
//                        VideoConverter.playVideo(file.toString(), player);
//                    }
//                    catch (Exception e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    }
//                }).start();
                connection.decode(file.toString());
                connection.play();
                home.setPlaying(true);
            }
        });
        
        home.setOnPausePlayPressed(e -> {
        	// TODO toggle pause play
            home.togglePlaying();
        });

        startup.setOnConnectPressed(e -> {
            String ip = startup.getIPAddress();
            username = startup.getUsername();
            if(!username.equals("") && !username.equals("Username") && !ip.equals("") && !ip.equals("IP Address")) {
                //TODO connection stuff
                home.addUserToList(username);
                primaryStage.setScene(home);
            }
        });

        startup.setOnHostPressed(e -> {
            username = startup.getUsername();
            if(!username.equals("")&& !username.equals("Username")) {
                home.addUserToList(username);
                primaryStage.setScene(home);
            }
            //TODO other host stuff?
        });
        
        // Displaying the window
//        primaryStage.setScene(startup);
        primaryStage.setScene(home);
        primaryStage.setOnCloseRequest(e -> {
            connection.terminate();
        });
        primaryStage.show();
        
    }
}
