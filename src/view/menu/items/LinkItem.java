package view.menu.items;

import javafx.scene.control.CustomMenuItem;

public class LinkItem extends CustomMenuItem {

    private LinkBox box;

    public LinkItem(LinkBox box){
        super(box);
        this.box = box;
        this.setHideOnClick(false);

        this.getStyleClass().clear();

    }

    public void setParent(String name, String surname){
        StringBuilder strb = new StringBuilder(20);

        if(name.isEmpty() == false) strb.append(" " + name);
        if(surname.isEmpty() == false) strb.append(" " + surname);

        box.getTextParent().setText(strb.toString());
    }
    public void setChild(String name, String surname){
        StringBuilder strb = new StringBuilder(20);

        if(name.isEmpty() == false) strb.append(" " + name + " ");
        if(surname.isEmpty() == false) strb.append(" " + surname);

        box.getTextChild().setText(strb.toString());
    }

    public LinkBox getBox(){
        return box;
    }
}
