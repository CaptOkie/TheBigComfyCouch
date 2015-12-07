package couch.cushion;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import couch.cushion.ui.HomeScene;
import couch.cushion.video.VideoConverter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Hello world!
 */
public class TheBigComfyCouch extends Application {
    
    private static final Path LIBRARY = Paths.get(System.getProperty("user.home"), "couch-library") ;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setting up main window
        primaryStage.setTitle("");
        
        Files.createDirectories(LIBRARY);

        // Creating Home scene
        // setting event handler for importing a file
        HomeScene home = new HomeScene();
        home.setOnImport(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(home.getWindow());
            if(file != null) {
                try {
                    Path src = LIBRARY.resolve(file.getAbsolutePath());
                    Path dst = LIBRARY.resolve(com.google.common.io.Files.getNameWithoutExtension(src.getFileName().toString()) + ".mp4");
//                    Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
                    
                    VideoConverter converter = new VideoConverter("MP4");
                    converter.convert(src.toUri().toURL(), dst.toUri().toURL());
                }
                catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        home.setOnHumbleTest(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(home.getWindow());
            if (file != null) {
                try {
                    VideoConverter videoPlayer = new VideoConverter("MP4");

                    videoPlayer.playVideo(file.getPath());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        
//        VBox box = new VBox();
//        MediaPlayer player = new MediaPlayer(new Media(Paths.get("/home/henri/couch-library/test.mp4").toUri().toURL().toString()));
//        player.setAutoPlay(true);
//        MediaView view = new MediaView(player);
//        box.getChildren().add(view);
        
        // Displaying the window
        primaryStage.setScene(home);
//        primaryStage.setScene(new Scene(box, 640, 480));
        primaryStage.show();
    }
}
