package view.menu;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import view.FamilyNode;
import view.menu.items.ModeBox;
import view.menu.items.ModeItem;
import view.menu.items.TextBox;
import view.menu.items.TextItem;

public class PersonMenu extends ContextMenu {

    FamilyNode node;
    PersonMenu self;

    private TextItem name;
    private TextItem surname;
    private TextItem patronymic;
    private TextItem age;
    private ModeItem mode;


    public PersonMenu(FamilyNode node) {
        this.node = node;
        self = this;

        mode = new ModeItem(new ModeBox());
        name = new TextItem(new TextBox(), node.person.getGender());
        surname = new TextItem(new TextBox(), node.person.getGender());
        patronymic = new TextItem(new TextBox(), node.person.getGender());
        age = new TextItem(new TextBox(), node.person.getGender());

        age.getStyleClass().add("menu-item-bottom");

        buildHandlers();

        name.setTextFlow("Имя:");
        surname.setTextFlow("Фамилия:");
        patronymic.setTextFlow("Отчество:");
        age.setTextFlow("Возраст:");

        this.getStyleClass().add("menu-icon");

        this.getItems().addAll(mode, name, surname, patronymic, age);
    }

   /* public void setGender(boolean isMale){
        name.setGender(isMale);
        surname.setGender(isMale);
        patronymic.setGender(isMale);
        age.setGender(isMale);
        mode.setGender(isMale);
    }*/

    public void setName(String name) {
        this.name.setTextField(name);
    }
    public void setSurname(String surname){
        this.surname.setTextField(surname);
    }
    public void setPatronymic(String patronymic){
        this.patronymic.setTextField(patronymic);
    }
    public void setAge(int age){
        this.age.setTextField(String.valueOf(age));
    }

    private void buildHandlers(){
        final boolean[] isFirst = {false};
        mode.getBox().getDeleteIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Delete person");
                if(isFirst[0] == false) node.delete();
                isFirst[0] = true;
            }
        });
        mode.getBox().getGenderIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Change person mode");
                node.setGender(!node.person.getGender());
                //setGender(person.getGender());
            }
        });

        name.getBox().getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.matches("[А-Яа-я]*")){
                node.person.setName(newValue);
                node.checkName();
                //oldName = oldValue;
            }
            else name.getBox().getTextField().setText(oldValue);
        });

        surname.getBox().getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.matches("[А-Яа-я]*")){
                node.person.setSurname(newValue);
                node.checkName();
                //oldSurame = oldValue;
            }
            else surname.getBox().getTextField().setText(oldValue);
        });

        patronymic.getBox().getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.matches("[А-Яа-я]*")){
                node.person.setPatronymic(newValue);
                //node.checkName();
                //oldPatronymic = oldValue;
            }
            else patronymic.getBox().getTextField().setText(oldValue);
        });

        age.getBox().getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.matches("[0-9]*")){
                if(newValue.isEmpty() == false){
                    node.person.setAge(Integer.parseInt(newValue));
                }
                //if(oldValue.isEmpty() == false){
                 //   oldAge = Integer.parseInt(oldValue);
                //}
                node.checkType();
            }
            else {
                if(oldValue.isEmpty() == false) {
                    age.getBox().getTextField().setText(oldValue);
                }
            }
        });
    }
}
