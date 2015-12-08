package couch.cushion.ui;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

/**
 * BaseScene Base scene for scenes to inherit from. Has default settings.
 * @author Juhandré Knoetze
 *
 */
public abstract class BaseScene extends Scene {
    
    protected final VBox    top;
    protected final VBox    right;
    protected final BorderPane    center;
    protected final MenuBar menuBar;
    protected final Menu    fileMenu;
    protected final BorderPane layout;

    /**
     * Default Constructor for BaseScene
     */
    protected BaseScene() {
        this(new BorderPane());
    }
    
    /**
     * Initializes default settings for the scene.
     * @param layout Takes a BorderPane that will layout our scene.
     */
    private BaseScene(BorderPane layout) {
        super(layout, 1280, 720);
        KeyCodeCombination ctrlQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        this.layout = layout;
        top = new VBox();
        right = new VBox();
        center = new BorderPane();

        layout.setTop(top);
        layout.setRight(right);
        layout.setCenter(center);
        // Creating menu bar with file menu. Has 'Ctrl+Q' for quitting.
        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        MenuItem quit = new MenuItem("Quit");
        quit.setAccelerator(ctrlQ);
        quit.setOnAction(e -> {
            System.exit(0);
        });
        fileMenu.getItems().add(quit);
        menuBar.getMenus().add(fileMenu);

        top.getChildren().add(menuBar);
    }
}
