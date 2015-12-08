package couch.cushion.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class HomeScene extends BaseScene {

    private final MenuItem imprt;
    private final MenuItem testImprt;
//    private final 
    
    public HomeScene() {
        imprt = new MenuItem("Import");
        fileMenu.getItems().add(imprt);
        testImprt = new MenuItem("Humble Test");
        fileMenu.getItems().add(testImprt);
        
    }
    
    public void setOnImport(EventHandler<ActionEvent> value) {
        imprt.setOnAction(value);
    }

    public void setOnHumbleTest(EventHandler<ActionEvent> value) {
        testImprt.setOnAction(value);
    }
}