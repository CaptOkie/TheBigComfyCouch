package couch.cushion.ui;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;

public abstract class BaseScene extends Scene {
    
    protected BaseScene() {
        this(new VBox());
    }
    
    protected final VBox    vbox;
    protected final MenuBar menuBar;
    protected final Menu    file;
    
    private BaseScene(VBox vbox) {
        super(vbox, 300, 300);
        
        this.vbox = vbox;
        
        this.menuBar = new MenuBar();
        this.file = new Menu("File");
        this.menuBar.getMenus().add(this.file);
        
        vbox.getChildren().add(this.menuBar);
    }
}
