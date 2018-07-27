package view;

import java.io.IOException;
import java.util.ListIterator;
import java.util.UUID;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import model.PersonLink;
import view.menu.LinkMenu;

public class NodeLink extends AnchorPane {

    @FXML CubicCurve node_link;
    @FXML Line node_link2;

    public final boolean type;

    private PersonLink personLink = null;

    private LinkMenu linkMenu;

    private FamilyNode source;
    private FamilyNode target;
    private boolean toParent;

    private final DoubleProperty mControlOffsetX = new SimpleDoubleProperty();
    private final DoubleProperty mControlOffsetY = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX2 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY2 = new SimpleDoubleProperty();

    private NodeLink self = this;

    //////////////////////

    public static final boolean LINE = true;
    public static final boolean CURVE = false;

    ///////////////////////


    public NodeLink() {
        type = CURVE;

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/NodeLink.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setId(UUID.randomUUID().toString());
    }
    public NodeLink(PersonLink link, boolean type) {

        FXMLLoader fxmlLoader;

        if(type == LINE){
            fxmlLoader = new FXMLLoader(
                    getClass().getResource("/NodeLink2.fxml")
            );
            this.type = type;
        }
        else {
            fxmlLoader = new FXMLLoader(
                    getClass().getResource("/NodeLink.fxml")
            );
            this.type = type;
        }

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        personLink = link;

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setId(UUID.randomUUID().toString());
    }

    @FXML
    private void initialize() {
        linkMenu = new LinkMenu(self);

        if(type == CURVE) {
            this.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(node_link.contains(event.getSceneX(), event.getSceneY()) == false){
                        linkMenu.hide();
                    }
                }
            });

            node_link.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent event) {
                    linkMenu.getLinkItem().setChild(source.person.getName(), source.person.getSurname());
                    linkMenu.getLinkItem().setParent(target.person.getName(), target.person.getSurname());
                    linkMenu.show(self, event.getScreenX(), event.getScreenY());
                }
            });

        ///region
            mControlOffsetX.set(100.0);
            mControlOffsetY.set(50.0);

            mControlDirectionX1.bind(new When(
                    node_link.startXProperty().greaterThan(node_link.endXProperty()))
                    .then(-1.0).otherwise(1.0));

            mControlDirectionX2.bind(new When(
                    node_link.startXProperty().greaterThan(node_link.endXProperty()))
                    .then(1.0).otherwise(-1.0));


            node_link.controlX1Property().bind(
                    Bindings.add(
                            node_link.startXProperty(),
                            mControlOffsetX.multiply(mControlDirectionX1)
                    )
            );

            node_link.controlX2Property().bind(
                    Bindings.add(
                            node_link.endXProperty(),
                            mControlOffsetX.multiply(mControlDirectionX2)
                    )
            );

            node_link.controlY1Property().bind(
                    Bindings.add(
                            node_link.startYProperty(),
                            mControlOffsetY.multiply(mControlDirectionY1)
                    )
            );

            node_link.controlY2Property().bind(
                    Bindings.add(
                            node_link.endYProperty(),
                            mControlOffsetY.multiply(mControlDirectionY2)
                    )
            );
            ///endregion
        }
        else{
            this.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(node_link2.contains(event.getSceneX(), event.getSceneY()) == false){
                        linkMenu.hide();
                    }
                }
            });

            node_link2.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent event) {
                    linkMenu.getLinkItem().setChild(source.person.getName(), source.person.getSurname());
                    linkMenu.getLinkItem().setParent(target.person.getName(), target.person.getSurname());
                    linkMenu.show(self, event.getScreenX(), event.getScreenY());
                }
            });
        }

    }

    public void setStart(Point2D startPoint) {
        if(type == CURVE) {
            node_link.setStartX(startPoint.getX());
            node_link.setStartY(startPoint.getY());
        }
        else{
            node_link2.setStartX(startPoint.getX());
            node_link2.setStartY(startPoint.getY());
        }
    }
    public void setEnd(Point2D endPoint) {
        if(type == CURVE) {
            node_link.setEndX(endPoint.getX());
            node_link.setEndY(endPoint.getY());
        }
        else{
            node_link2.setEndX(endPoint.getX());
            node_link2.setEndY(endPoint.getY());
        }
    }

    public void bindEnds (FamilyNode source, FamilyNode target, boolean toParent) {
        node_link.startXProperty().bind(
                Bindings.add(source.layoutXProperty(), (source.getWidth() / 2.0)));

        node_link.endXProperty().bind(
                Bindings.add(target.layoutXProperty(), (target.getWidth() / 2.0)));

        if(toParent == true){
            node_link.startYProperty().bind(
                    Bindings.add(source.layoutYProperty(), (source.getHeight() / 2.0) - 35.0));

            node_link.endYProperty().bind(
                    Bindings.add(target.layoutYProperty(), (target.getHeight() / 2.0) + 5.0));
        }
        else {
            node_link.startYProperty().bind(
                    Bindings.add(source.layoutYProperty(), (source.getHeight() / 2.0) + 5.0));

            node_link.endYProperty().bind(
                    Bindings.add(target.layoutYProperty(), (target.getHeight() / 2.0) - 35.0));
        }

        this.source = source;
        this.target = target;
        this.toParent = toParent;

        source.registerLink (getId());
        target.registerLink (getId());
    }
    public void bindEndsFromReader (FamilyNode source, FamilyNode target, boolean toParent) {
        if(type == CURVE) {
            node_link.startXProperty().bind(
                    Bindings.add(source.layoutXProperty(), (source.getPrefWidth() / 2.0)));

            node_link.endXProperty().bind(
                    Bindings.add(target.layoutXProperty(), (target.getPrefWidth() / 2.0)));

            if (toParent == true) {
                node_link.startYProperty().bind(
                        Bindings.add(source.layoutYProperty(), (source.getPrefHeight() / 2.0) - 35.0));

                node_link.endYProperty().bind(
                        Bindings.add(target.layoutYProperty(), (target.getPrefHeight() / 2.0) + 5.0));
            } else {
                node_link.startYProperty().bind(
                        Bindings.add(source.layoutYProperty(), (source.getPrefHeight() / 2.0) + 5.0));

                node_link.endYProperty().bind(
                        Bindings.add(target.layoutYProperty(), (target.getPrefHeight() / 2.0) - 35.0));
            }
        }
        else{
            node_link2.startXProperty().bind(
                    Bindings.add(source.layoutXProperty(), (source.getPrefWidth() / 2.0)));

            node_link2.endXProperty().bind(
                    Bindings.add(target.layoutXProperty(), (target.getPrefWidth() / 2.0)));

            if (toParent == true) {
                node_link2.startYProperty().bind(
                        Bindings.add(source.layoutYProperty(), (source.getPrefHeight() / 2.0) - 35.0));

                node_link2.endYProperty().bind(
                        Bindings.add(target.layoutYProperty(), (target.getPrefHeight() / 2.0) + 5.0));
            } else {
                node_link2.startYProperty().bind(
                        Bindings.add(source.layoutYProperty(), (source.getPrefHeight() / 2.0) + 5.0));

                node_link2.endYProperty().bind(
                        Bindings.add(target.layoutYProperty(), (target.getPrefHeight() / 2.0) - 35.0));
            }
        }

        this.source = source;
        this.target = target;
        this.toParent = toParent;

        source.registerLink (getId());
        target.registerLink (getId());

        linkMenu = new LinkMenu(this);
        this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {

            }
        });
    }

    public FamilyNode getSource(){
        return source;
    }
    public FamilyNode getTarget(){
        return target;
    }

    public void delete(){
        ///Список id в source
        for(int i = 0; i < source.getNodeLinksIds().size(); ++i){
            if(source.getNodeLinksIds().get(i) == getId()){
                source.getNodeLinksIds().remove(i);
                break;
            }
        }
        source.person.deleteChild(target.person);
        source.person.deleteParent(target.person);
        ///Список id в target
        for(int i = 0; i < target.getNodeLinksIds().size(); ++i){
            if(target.getNodeLinksIds().get(i) == getId()){
                target.getNodeLinksIds().remove(i);
                break;
            }
        }
        target.person.deleteChild(source.person);
        target.person.deleteParent(source.person);
        ///Из base_pane
        Pane base_pane = (Pane) getParent();
        for (ListIterator iterNode = base_pane.getChildren().listIterator();
             iterNode.hasNext();) {

            Node node = (Node) iterNode.next();

            if (node.getId() == null)
                continue;

            if (node.getId().equals(getId())) {
                iterNode.remove();
                break;
            }
        }
        //Из tree
        source.getFamilyPane().getTree().getLinks().remove(personLink);
        //Из FamilyPane
        source.getFamilyPane().getLinkList().remove(this);
    }
}
