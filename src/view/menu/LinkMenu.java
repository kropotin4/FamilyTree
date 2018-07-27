package view.menu;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import view.NodeLink;
import view.menu.items.LinkBox;
import view.menu.items.LinkItem;

public class LinkMenu extends ContextMenu {

    NodeLink nodeLink;

    private LinkItem linkItem = new LinkItem(new LinkBox());

    public LinkMenu(NodeLink nodeLink){
        this.nodeLink = nodeLink;

        this.getItems().add(linkItem);

        //this.getStyleClass().clear();

        linkItem.getBox().getDelete().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                hide();
                nodeLink.delete();

                nodeLink.getSource().getFamilyPane().getTree().checkGenerations();
                nodeLink.getSource().getFamilyPane().updatePersonTreeView();
            }
        });
    }

    public LinkItem getLinkItem(){
        return linkItem;
    }
}
