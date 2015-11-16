package couch.cushion.ui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class HomeScene extends Scene {

    public HomeScene() {
        this(new GridPane());
    }
    
    private HomeScene(GridPane grid) {
        super(grid);
        
        Text pathMessage = new Text();

        TextField pathField = new TextField();
        pathField.setPromptText("File Path");
        
        Button search = new Button("Search");
        search.setOnAction(e -> {
            File file;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Browse");
            file = fileChooser.showOpenDialog(getWindow());
            if (file != null) {
                pathField.setText(file.getPath());
            }
        });


        Button open = new Button("Open");
        open.setOnAction(e -> {
            pathMessage.setFill(Color.FIREBRICK);
            pathMessage.setText("Invalid file.");
        });
        open.setDefaultButton(true);
        
        /**
         * Setting up grid to align elements
         */
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        /** 
         * Adding elements to the grid
         */
        grid.add(pathField, 0, 0);
        grid.add(search, 1, 0);
        grid.add(pathMessage, 0, 2);
        grid.add(open, 1, 1);
    }
}
