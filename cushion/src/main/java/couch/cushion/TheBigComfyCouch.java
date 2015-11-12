package couch.cushion;

import couch.cushion.ui.ImportMediaScene;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
        KeyCodeCombination ctrlQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);

        /**
         * Setting up main window
         * Adding handler for ctrl+Q to for quit shorcut
         */
        primaryStage.setTitle("");
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (ctrlQ.match(e)) {
                primaryStage.close();
            }
        });

        /**
         * Creation of the first scene/adding it to the window
         */
        primaryStage.setScene(new ImportMediaScene());
        primaryStage.show();
    }
}
