import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.FamilyPane;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        try {
            FamilyPane root = new FamilyPane();
            Scene scene = new Scene(root, Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

            //primaryStage.resizableProperty().set(false);
            primaryStage.setMaxHeight(625);
            primaryStage.setMaxWidth(1180);
            primaryStage.setMinHeight(520);
            primaryStage.setMinWidth(910);

            //primaryStage.initStyle(StageStyle.TRANSPARENT);

            primaryStage.setTitle("Семейное древо");
            primaryStage.getIcons().add(new Image("file:res/images/tree.png"));
            primaryStage.getIcons().add(new Image("file:res/images/tree(1).png"));
            primaryStage.getIcons().add(new Image("file:res/images/tree(2).png"));
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
