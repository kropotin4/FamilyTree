package view;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.FamilyTree;
import model.Person;

import java.util.ArrayList;

public class PersonTreeView {

    TreeView<Person> treeView;

    public PersonTreeView(TreeView treeView){
        this.treeView = treeView;

        treeView.getStyleClass().clear();
    }

    public void updatePersonTreeView(FamilyTree tree){
        TreeItem<Person> rootItem = new TreeItem<>();
        rootItem.setExpanded(true);

        for(Person person : tree.getPersons()){
            TreeItem<Person> treeItem = new TreeItem<Person>(person);

            rootItem.getChildren().add(treeItem);

            for(Person personSub : person.getChildren()){
                TreeItem<Person> itemSub = new TreeItem<Person>(personSub);
                itemSub.setGraphic(new ImageView("file:res/images/if_Son(S).png"));
                treeItem.getChildren().add(itemSub);
            }

            for(Person personPre : person.getParents()){
                TreeItem<Person> itemPre = new TreeItem<Person>(personPre);
                itemPre.setGraphic(new ImageView("file:res/images/if_Father(S).png"));
                treeItem.getChildren().add(itemPre);
            }
        }

        treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getTarget() instanceof Text){
                    Text text = (Text)event.getTarget();

                    System.out.println("TreeView -> Mouse Clicked: " + treeView.getSelectionModel().getSelectedItem().getValue());
                }

            }
        });

        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);

        //treeView.getStyleClass().clear();

        //treeView.getStyleClass().add("-fx-background-radius: 10");
        //treeView.getStyleClass().add("-fx-margin: 1em 0em 1em 0em");
    }
}
