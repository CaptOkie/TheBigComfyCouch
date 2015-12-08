package couch.cushion;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import couch.cushion.ui.HomeScene;
import couch.cushion.video.VideoConverter;
import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        ImageView view = new ImageView();
        HomeScene home = new HomeScene(view);
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
//                    VideoConverter videoPlayer = new VideoConverter("MP4");
                    new Thread(() -> {
                        try {
                            VideoConverter.playVideo(file.getPath(), view);
                        }
                        catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }).start();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        
        // Displaying the window
        primaryStage.setScene(home);
        primaryStage.show();
    }
}
