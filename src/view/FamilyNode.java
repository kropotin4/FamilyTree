package view;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Person;
import view.menu.PersonMenu;

import java.io.IOException;
import java.util.ArrayList;

public class FamilyNode extends AnchorPane {

    ///region Поля
    @FXML private VBox vbox_pane;
    @FXML private Text text;

    @FXML private AnchorPane top_link;
    @FXML private AnchorPane bottom_link;

    private EventHandler <MouseEvent> mLinkToParentHandleDragDetected;
    private EventHandler <MouseEvent> mLinkToChildHandleDragDetected;
    private EventHandler <DragEvent> mLinkHandleDragDropped;
    private EventHandler <DragEvent> mContextLinkDragOver;
    private EventHandler <DragEvent> mContextLinkDragDropped;

    private EventHandler<DragEvent> mContextDragOver;
    private EventHandler <DragEvent> mContextDragDropped;

    private Point2D mDragOffset = new Point2D (0.0, 0.0);

    boolean visited = false;
    private int x;
    private int y;

    private NodeLink mDragLink = null;
    private Pane base_pane = null;

    private final ArrayList<String> mLinkIds = new ArrayList<>  ();

    public Person person;
    public PersonMenu menu;

    private final FamilyNode self;
    private FamilyPane pane;
    ///endregion

    /////////////////////////////

    public FamilyNode(FamilyPane pane){
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/FamilyNode.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        self = this;
        this.pane = pane;
        person = new Person();

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public FamilyNode(FamilyPane pane, Person person){
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/FamilyNode.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        self = this;
        this.pane = pane;
        this.person = person;

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        System.out.println("FamilyNode -> create");

        buildNodeDragHandlers();
        buildLinkDragHandlers();

        menu = new PersonMenu(this);

        vbox_pane.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                menu.show(self,event.getScreenX(), event.getScreenY());
                System.out.println("Generation: " + person.getGeneration());
            }
        });

        mDragLink = new NodeLink();
        mDragLink.setVisible(false);

        parentProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            base_pane = (Pane) getParent();
        });

        setId(person.getId());
    }

    void relocateToPoint (Point2D p) {
        Point2D localCoords = getParent().sceneToLocal(p);

        x = (int) (localCoords.getX() - mDragOffset.getX());
        y = (int) (localCoords.getY() - mDragOffset.getY());

        person.setX(x);
        person.setY(y);

        relocate (x, y);
    }

    private void buildNodeDragHandlers() {

        //Перемещение
        mContextDragOver = new EventHandler <DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);
                relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));

                event.consume();
            }
        };

        //Конец перемещения
        mContextDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                event.setDropCompleted(true);

                event.consume();
            }
        };

        //Обнаружение перетаскивания FamilyNode
        vbox_pane.setOnDragDetected (new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                menu.hide();

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver (mContextDragOver);
                getParent().setOnDragDropped (mContextDragDropped);

                //begin drag ops
                mDragOffset = new Point2D(event.getX(), event.getY());

                relocateToPoint(
                        new Point2D(event.getSceneX(), event.getSceneY())
                );

                ///Работа с буфером drag операций -> обязательно
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", person.getGender());
                content.put(DragContainer.DragNode, container);

                ///Начало перемещения
                startDragAndDrop (TransferMode.ANY).setContent(content);
                event.consume();
            }

        });
    }
    private void buildLinkDragHandlers(){

        mLinkToParentHandleDragDetected = new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                menu.hide();

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver(mContextLinkDragOver);
                getParent().setOnDragDropped(mContextLinkDragDropped);

                //Set up user-draggable link
                base_pane.getChildren().add(0,mDragLink);

                mDragLink.setVisible(false);

                Point2D p = new Point2D(
                        getLayoutX() + (getWidth() / 2.0),
                        getLayoutY() + (getHeight() / 2.0) - 35.0
                );

                mDragLink.setStart(p);

                //Drag content code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer ();

                AnchorPane link_handle = (AnchorPane) event.getSource();
                FamilyNode parent = (FamilyNode) link_handle.getParent().getParent();

                container.addData("sourceToParent", getId());

                content.put(DragContainer.AddLink, container);

                parent.startDragAndDrop (TransferMode.ANY).setContent(content);

                event.consume();
            }
        };
        mLinkToChildHandleDragDetected = new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                menu.hide();

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver(mContextLinkDragOver);
                getParent().setOnDragDropped(mContextLinkDragDropped);

                //Set up user-draggable link
                base_pane.getChildren().add(0,mDragLink);

                mDragLink.setVisible(false);

                Point2D p = new Point2D(
                        getLayoutX() + (getWidth() / 2.0),
                        getLayoutY() + (getHeight() / 2.0 + 5.0)
                );

                mDragLink.setStart(p);

                //Drag content code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer ();

                AnchorPane link_handle = (AnchorPane) event.getSource();
                FamilyNode parent = (FamilyNode) link_handle.getParent().getParent();

                container.addData("sourceToChild", getId());

                content.put(DragContainer.AddLink, container);

                parent.startDragAndDrop (TransferMode.ANY).setContent(content);

                event.consume();
            }
        };

        mLinkHandleDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //This isn't the drag event we're looking for.
                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

                if (container == null)
                    return;

                mDragLink.setVisible(false);
                base_pane.getChildren().remove(0);

                //AnchorPane link_handle = (AnchorPane) event.getSource();
                //FamilyNode parent = (FamilyNode) link_handle.getParent().getParent();

                ClipboardContent content = new ClipboardContent();

                container.addData("target", getId());

                content.put(DragContainer.AddLink, container);

                event.getDragboard().setContent(content);

                event.setDropCompleted(true);

                event.consume();
            }
        };

        mContextLinkDragOver = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);

                //Relocate user-draggable link
                if (!mDragLink.isVisible())
                    mDragLink.setVisible(true);

                mDragLink.setEnd(new Point2D(event.getX(), event.getY()));

                event.consume();
            }
        };

        mContextLinkDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                mDragLink.setVisible(false);
                base_pane.getChildren().remove(0);

                event.setDropCompleted(true);
                event.consume();
            }

        };

        top_link.setOnDragDetected(mLinkToParentHandleDragDetected);
        bottom_link.setOnDragDetected(mLinkToChildHandleDragDetected);

        vbox_pane.setOnDragDropped(mLinkHandleDragDropped);
    }

    public void checkType(){
        int age = person.getAge();
        vbox_pane.getStyleClass().clear();
        vbox_pane.getStyleClass().add("familynode");
        if(person.getGender() == true){
            if(age <= 3){
                vbox_pane.getStyleClass().add("icon-son");
            }
            else if(age <= 18){
                vbox_pane.getStyleClass().add("icon-brother");
            }
            else if (age <= 30){
                vbox_pane.getStyleClass().add("icon-father");
            }
            else if (age <= 50){
                vbox_pane.getStyleClass().add("icon-uncle");
            }
            else{
                vbox_pane.getStyleClass().add("icon-grandfather");
            }
        }
        else{
            if(age <= 3){
                vbox_pane.getStyleClass().add("icon-daughter");
            }
            else if(age <= 18){
                vbox_pane.getStyleClass().add("icon-sister");
            }
            else if (age <= 30){
                vbox_pane.getStyleClass().add("icon-mather");
            }
            else if (age <= 50){
                vbox_pane.getStyleClass().add("icon-aunt");
            }
            else{
                vbox_pane.getStyleClass().add("icon-grandmother");
            }
        }

        pane.updatePersonTreeView();
    }
    public void checkName(){
        StringBuilder strb = new StringBuilder(20);

        strb.append(person.getName());

        strb.append(" " + person.getSurname());

        text.setText(strb.toString());

        pane.updatePersonTreeView();

    }
    public void checkMenu(){
        if(person.getName().isEmpty() == false) menu.setName(person.getName());
        if(person.getSurname().isEmpty() == false) menu.setSurname(person.getSurname());
        if(person.getPatronymic().isEmpty() == false) menu.setPatronymic(person.getPatronymic());
        if(person.getAge() != -1) menu.setAge(person.getAge());

        pane.updatePersonTreeView();
    }

    void registerLink(String linkId) {
        mLinkIds.add(linkId);
    }
    ArrayList<String> getNodeLinksIds(){
        return mLinkIds;
    }

    public void setGender(boolean isMale){
        person.setGender(isMale);
        checkType();
    }

    int getX(){
        return x;
    }
    int getY() {
        return y;
    }

    void setX(int x) {
        this.x = x;
    }
    void setY(int y) {
        this.y = y;
    }

    public FamilyPane getFamilyPane(){
        return pane;
    }

    public void delete(){
        System.out.println("Node delete");
        pane.deleteFamilyNode(this);
        System.out.println("NodeList" + pane.getNodeList());
    }
}
