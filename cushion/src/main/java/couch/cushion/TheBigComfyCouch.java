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
        final Connection connection = new Connection(player);
        
        // Setting up main window
        primaryStage.setTitle("The Big Comfy Couch");
        
        Files.createDirectories(LIBRARY);

        // Creating Home scene
        // setting event handler for importing a file
        StartupScene startup = new StartupScene();
        HomeScene home = new HomeScene(player);
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
                home.addMessage(username, message);
            }
        });

        home.setOnHumbleTest(e -> {
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
            }
        });

        startup.setOnConenctPressed(e -> {
            String ip = startup.getIPAddress();
            username = startup.getUsername();
            if(!username.equals("") && !ip.equals("")) {
                //TODO connection stuff
                home.addUserToList(username);
                primaryStage.setScene(home);
            }
        });

        startup.setOnHostPressed(e -> {
            username = startup.getUsername();
            if(!username.equals("")) {
                home.addUserToList(username);
                primaryStage.setScene(home);
            }
            //TODO other host stuff?
        });
        
        // Displaying the window
        primaryStage.setScene(startup);
        primaryStage.setOnCloseRequest(e -> {
            connection.terminate();
        });
        primaryStage.show();
        
    }
}
