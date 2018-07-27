package view.menu.items;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ModeBox extends HBox {

    @FXML ImageView delete_icon;
    @FXML ImageView gender_icon;

    public ModeBox(){
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/ModeBox.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public ImageView getDeleteIcon() {
        return delete_icon;
    }

    public ImageView getGenderIcon() {
        return gender_icon;
    }
}
