package couch.cushion.ui;

import java.awt.Toolkit;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;

/**
 * BaseScene Base scene for scenes to inherit from. Has default settings.
 * @author Juhandré Knoetze
 *
 */
public abstract class BaseScene extends Scene {
    
//    protected final VBox    top;
//    protected final VBox    right;
//    protected final BorderPane    center;
    protected final VBox vbox;
    protected final MenuBar menuBar;
    protected final Menu    fileMenu;
//    protected final BorderPane layout;

    /**
     * Default Constructor for BaseScene
     */
    protected BaseScene() {
        this(new VBox());
    }
    
    /**
     * Initializes default settings for the scene.
     * @param layout Takes a BorderPane that will layout our scene.
     */
    private BaseScene(VBox vbox) {
        super(vbox, Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8,
              Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8);
        this.vbox = vbox;
//        super(layout, Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8,
//        		Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8);
//        this.layout = layout;
//        top = new VBox();
//        right = new VBox();
//        center = new BorderPane();
//
//        layout.setTop(top);
//        layout.setRight(right);
//        layout.setCenter(center);
//        center.setMinWidth(getWidth() * 0.7);
//        center.setPrefWidth(getWidth() * 0.7);
//        right.setMinWidth(getWidth() * 0.3);
//        right.setPrefWidth(getWidth() * 0.3);
        // Creating menu bar with file menu. Has 'Ctrl+Q' for quitting.
        KeyCodeCombination ctrlQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        MenuItem quit = new MenuItem("Quit");
        quit.setAccelerator(ctrlQ);
        quit.setOnAction(e -> {
            System.exit(0);
        });
        fileMenu.getItems().add(quit);
        menuBar.getMenus().add(fileMenu);
        vbox.getChildren().add(menuBar);

//        top.getChildren().add(menuBar);
    }
}
