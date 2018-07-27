package view.menu.items;

import javafx.scene.control.CustomMenuItem;

public class ModeItem extends CustomMenuItem {

    ModeBox box;

    public ModeItem(ModeBox box){
        super(box);
        this.box = box;
        this.setHideOnClick(false);

        ///if(isMale == true) getStyleClass().add("blue");
        //else getStyleClass().add("pink");

        getStyleClass().add("menu-item-top");

        //System.out.println(getStyleClass());
    }

    public ModeBox getBox() {
        return box;
    }

    /*public void setGender(boolean isMale){
        getStyleClass().clear();

        //getStyleClass().add("custom-menu-item");
        getStyleClass().add("menu-item");
        if(isMale == true) getStyleClass().add("blue");
        else getStyleClass().add("pink");

        System.out.println("Change: " + getStyleClass());
    }*/
}
