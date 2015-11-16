package couch.cushion;

import couch.cushion.ui.HomeScene;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Hello world!
 */
public class TheBigComfyCouch extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /**
         * Setting up main window
         */
        primaryStage.setTitle("");

        /**
         * Creation of the first scene/adding it to the window
         */
        primaryStage.setScene(new HomeScene());
        primaryStage.show();
    }
}
