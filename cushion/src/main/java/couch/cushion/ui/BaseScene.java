package couch.cushion.ui;

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
    
    protected final VBox    vbox;
    protected final MenuBar menuBar;
    protected final Menu    fileMenu;

    /**
     * Default Constructor for BaseScene
     */
    protected BaseScene() {
        this(new VBox());
    }
    
    /**
     * Initializes default settings for the scene.
     * @param vbox Takes a vbox that will hold the menu bar along with othe scene nodes.
     */
    private BaseScene(VBox vbox) {
        super(vbox, 300, 300);
        KeyCodeCombination ctrlQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        
        this.vbox = vbox;

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
        
        vbox.getChildren().add(menuBar);
    }
}
