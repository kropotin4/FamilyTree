package view.menu.items;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class TextBox extends HBox {

    @FXML TextFlow text_flow;
    @FXML TextField text_field;

    public TextBox(){
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/TextBox.fxml")
         );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    void setTextField(String text) {
        this.text_field.setText(text);
    }
    void setTextFlow(String text) {
        this.text_flow.getChildren().clear();
        this.text_flow.getChildren().add(new Text(text));
    }

    public TextField getTextField(){
        return text_field;
    }

    /*
    public String getTextField(){
        return text_field.getText();
    }
    public String getTextFlow(){
        Text txt = (Text) text_flow.getChildren().get(0);
        return txt.getText();
    }*/
}
