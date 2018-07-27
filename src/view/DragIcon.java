package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DragIcon extends AnchorPane {

    private boolean isMale;

    public DragIcon(){
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/DragIcon.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public DragIcon(boolean isMale){
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/DragIcon.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setType(isMale);
        this.isMale = isMale;
    }

    @FXML
    private void initialize() {System.out.println("DragIcon -> create");}

    void relocateToPoint (Point2D p) {
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
                (int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
                (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
        );
    }

    public void setType(boolean isMale){
        getStyleClass().clear();
        getStyleClass().add("dragicon");

        if(isMale == true){ getStyleClass().add("icon-unknown-man");}
        else getStyleClass().add("icon-unknown-woman");

        this.isMale = isMale;
    }
    public boolean getType(){
        return isMale;
    }

}
