package view.menu.items;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class LinkBox extends VBox {

    @FXML private Text parent;
    @FXML private Text child;
    @FXML private ImageView delete;

    public LinkBox(){
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/LinkBox.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    Text getTextParent(){
        return parent;
    }
    Text getTextChild(){
        return child;
    }
    public ImageView getDelete(){
        return delete;
    }

}
