package couch.cushion;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Hello world!
 */
public class TheBigComfyCouch extends Application {

    public static void main(final String[] args) throws InterruptedException, IOException {
         launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("What up homies!");
        primaryStage.show();
    }
}
