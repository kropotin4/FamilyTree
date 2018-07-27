package view.menu.items;

import javafx.scene.control.CustomMenuItem;

public class TextItem extends CustomMenuItem {

    TextBox box;

    public TextItem(TextBox box) {
        super (box);
        this.box = box;
        this.setHideOnClick(false);

        //System.out.println(getStyleClass());
    }

    public void setTextField(String text) {
        box.setTextField(text);
    }
    public void setTextFlow(String text) {
        box.setTextFlow(text);
    }

    public TextBox getBox() {
        return box;
    }

    /*public void setGender(boolean isMale){
        getStyleClass().clear();
       // getStyleClass().add("menu-icon");
        //getStyleClass().add("custom-menu-item");
        getStyleClass().add("menu-item");
        if(isMale == true) getStyleClass().add("blue");
        else getStyleClass().add("pink");

        System.out.println("Change: " + getStyleClass());
    }*/
}
