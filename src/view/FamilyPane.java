package view;

import io.FamilyReader;
import io.FamilyWriter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.FamilyTree;
import model.Person;
import model.PersonLink;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FamilyPane extends BorderPane {

    ///region Поля
    @FXML private VBox add_pane;
    @FXML private Pane base_pane;
    @FXML private AnchorPane root_pane;

    @FXML private ScrollPane scroll_pane;

    @FXML TreeView tree_view;

    private PersonTreeView personTreeView;

    @FXML private MenuItem save;
    @FXML private MenuItem load;
    @FXML private MenuItem close;
    @FXML private MenuItem clear;

    @FXML private Text per_count;
    private int perCount = 0;

    @FXML Button reg_button;

    DragIcon mDragOverIconMale;
    DragIcon mDragOverIconFemale;
    DragIcon mDragOver;
    boolean isMaleOver;

    private ArrayList<FamilyNode> nodeList = new ArrayList<>(16);
    private ArrayList<NodeLink> linkList = new ArrayList<>(32);

    private EventHandler<DragEvent> mIconDragOverRoot = null;
    private EventHandler<DragEvent> mIconDragDropped = null;
    private EventHandler<DragEvent> mIconDragOverBasePane = null;

    private FileChooser fileChooser = new FileChooser();
    private FamilyReader familyReader = new FamilyReader();
    private FamilyWriter familyWriter = new FamilyWriter();

    private FamilyTree tree;
    private FamilyPane self;

    private final int REG_WIGHT = 54;
    private final int REG_HIGHT = 100;
    private final int REG_CENTER = 2;
    private final int REG_RIGHT = 1;
    private final int REG_LEFT = 0;

    private int CUR_GEN;
    ///endregion

    public FamilyPane(){
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/FamilyPane.fxml")
        );

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        self = this;

        try {
            fxmlLoader.load();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        System.out.println("FamilyPane -> initialize");
        tree = new FamilyTree();

        per_count.setText("Членов семьи: 0");

        ///region Top Menu
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Family Tree file", "*.tree"));

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showSaveDialog(getScene().windowProperty().get());

                if(file != null){
                    try {
                        familyWriter.saveToFile(tree, file);
                        System.out.println("Save complete!");
                    }
                    catch (IOException e){
                        System.out.println(e.getMessage());
                    }

                }
            }
        });
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(getScene().windowProperty().get());

                if(file != null){
                    try {
                        System.out.println("Load begin...");
                        //familyReader.loadFromFile(file);
                        loadNewFamilyTree(familyReader.loadFromFile(file));
                        System.out.println("Load complete!");
                    }
                    catch (IOException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clear();
            }
        });
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });
        ///endregion

        ///region mDragOver
        mDragOverIconMale = new DragIcon();
        mDragOverIconFemale = new DragIcon();

        mDragOverIconMale.setVisible(false);
        //mDragOverIconMale.setOpacity(0.65);
        mDragOverIconMale.setType(true);
        base_pane.getChildren().add(mDragOverIconMale);

        mDragOverIconFemale.setVisible(false);
        //mDragOverIconFemale.setOpacity(0.65);
        mDragOverIconFemale.setType(false);
        base_pane.getChildren().add(mDragOverIconFemale);
        ///endregion

        ///region DragIcon
        DragIcon icnMale = new DragIcon(true);
        DragIcon icnFemale = new DragIcon(false);

        addDragDetection(icnMale);
        addDragDetection(icnFemale);

        add_pane.getChildren().add(icnMale);
        add_pane.getChildren().add(icnFemale);
        ///endregion

        reg_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                regulation();
            }
        });

        scroll_pane.setHvalue(scroll_pane.getHmax() / 2);
        scroll_pane.setVvalue(scroll_pane.getVmax());

        personTreeView = new PersonTreeView(tree_view);

        buildDragHandlers();
        System.out.println(add_pane.getChildren());
        System.out.println(this.getChildren());
    }

    private void addDragDetection(DragIcon dragIcon) {
        System.out.println("FamilyPane->addDragDetection");
        dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {
                System.out.println("FamilyPane->addDragDetection->handle");
                // set drag event handlers on their respective objects
                root_pane.setOnDragOver(mIconDragOverRoot);
                base_pane.setOnDragOver(mIconDragOverBasePane);
                add_pane.setOnDragOver(mIconDragOverBasePane);
                //base_pane.setOnDragDropped(mIconDragDropped);
                root_pane.setOnDragDropped(mIconDragDropped);

                DragIcon icn = (DragIcon) event.getSource();

                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", icn.getType());
                content.put(DragContainer.AddNode, container);

                if(icn.getType()){
                    mDragOver = mDragOverIconMale;
                    isMaleOver = true;
                }
                else{
                    mDragOver = mDragOverIconFemale;
                    isMaleOver = false;
                }

                mDragOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                mDragOver.setVisible(true);
                mDragOver.setMouseTransparent(true);


                mDragOver.startDragAndDrop(TransferMode.ANY).setContent(content);

                event.consume();
            }
        });
    }

    private void buildDragHandlers() {

        //drag over transition to move widget form left pane to right pane
        mIconDragOverRoot = new EventHandler <DragEvent>() {

            @Override
            public void handle(DragEvent event) {

                Point2D p = base_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

                //turn on transfer mode and track in the right-pane's context
                //if (and only if) the mouse cursor falls within the right pane's bounds.
                if (!base_pane.boundsInLocalProperty().get().contains(p)) {
                    event.acceptTransferModes(TransferMode.ANY);

                    mDragOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                }

                event.consume();
            }
        };

        mIconDragOverBasePane = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);

                mDragOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                event.consume();
            }
        };

        mIconDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                System.out.println("FamilyPane->mIconDragDropped->handle");
                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

                container.addData("scene_coords",
                        new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                content.put(DragContainer.AddNode, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };

        this.setOnDragDone (new EventHandler <DragEvent> (){

            @Override
            public void handle (DragEvent event) {
                System.out.println("FamilyPane->this.setOnDragDone->handle");
                base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverBasePane);
                base_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
                root_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);

                mDragOverIconMale.setVisible(false);
                mDragOverIconFemale.setVisible(false);

                ///region AddNode операция
                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

                if (container != null) {
                    if (container.getValue("scene_coords") != null) {

                        FamilyNode node = addFamilyNode();

                        node.person.setGender((boolean)container.getValue("type"));
                        node.checkType();

                        Point2D cursorPoint = container.getValue("scene_coords");
                        node.relocateToPoint(
                                new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                        );
                    }
                }
                ///endregion

                ///region DragDone операция
                container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.DragNode);

                if (container != null) {
                    if (container.getValue("type") != null)
                        System.out.println ("Moved node " + container.getValue("type"));
                }
                ///endregion

                ///region AddLink операция
                container = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

                if (container != null) {
                    boolean toParent;

                    String sourceId;
                    if(container.getValue("sourceToParent") == null){
                        sourceId = container.getValue("sourceToChild");
                        toParent = false;
                        System.out.println("Link: to Child");
                    }
                    else{
                        sourceId = container.getValue("sourceToParent");
                        toParent = true;
                        System.out.println("Link: to Parent");
                    }

                    String targetId = container.getValue("target");

                    if (sourceId != null && targetId != null) {

                        FamilyNode source = null;
                        FamilyNode target = null;

                        for (Node n: base_pane.getChildren()) {

                            if (n.getId().equals(sourceId)) {
                                source = (FamilyNode) n;
                            }
                            else if (n.getId().equals(targetId)) {
                                target = (FamilyNode) n;
                            }
                        }

                        if (source != null && target != null) {
                            ///Проверка повтoрной связи
                            for(Person person : source.person.getParents()){
                                if(person == target.person){
                                    System.out.println("Повторная связь");
                                    return;
                                }
                            }
                            for(Person person : source.person.getChildren()){
                                if(person == target.person){
                                    System.out.println("Повторная связь");
                                    return;
                                }
                            }
                            ///
                            PersonLink personLink;
                            if(toParent == true){
                                personLink = tree.createLink(source.person, target.person);
                            }
                            else{
                                personLink = tree.createLink(target.person, source.person);
                            }
                            NodeLink link = new NodeLink(personLink, NodeLink.LINE);

                            base_pane.getChildren().add(0,link);

                            link.bindEndsFromReader(source, target, toParent);
                            linkList.add(link);

                            System.out.println(getLinkList());
                            //System.out.println(getNodeList());

                            if(toParent == true){
                                source.person.addParent(target.person);
                                target.person.addChild(source.person);
                            }
                            else{
                                target.person.addParent(source.person);
                                source.person.addChild(target.person);
                            }

                            tree.checkGenerations();
                            updatePersonTreeView();
                        }
                    }
                }
                ///endregion

                event.consume();
            }
        });
    }

    ArrayList<FamilyNode> getNodeList(){
        return nodeList;
    }
    ArrayList<NodeLink> getLinkList(){
        return linkList;
    }

    private void addPerCount(){
        perCount++;
        per_count.setText("Членов семьи: " + perCount);
    }
    private void reducePerCount(){
        perCount--;
        per_count.setText("Членов семьи: " + perCount);
    }

    public void clear(){
        while(nodeList.isEmpty() == false){
            for(FamilyNode node : nodeList){
                deleteFamilyNode(node);
                break;
            }
        }

        ///nodeList.clear();
        System.out.println("NodeList" + nodeList);
    }

    private void regulation(){
        int Y = 800;
        int X = (int) base_pane.getWidth() / 2;

        System.out.println("Regulation");
        for(FamilyNode node : nodeList) node.visited = false;

            for(int g = 0; g < nodeList.size(); ++g) {
                if(nodeList.get(g).person.getGeneration() == tree.getMinGeneraton() && nodeList.get(g).visited == false){
                    regulationBranch(nodeList.get(g), 0, X, Y, REG_CENTER);
                    Y -= 100 * (considerGenLenght(nodeList.get(g).person, 0) + 1);

                    if(Y <= 0) break;

                }
            }

            checkOutOfPane();
    }
    private void regulationBranch(FamilyNode node, int gen, int x, int y, int position){
        ///region Sibs
        TreeSet<Person> set = tree.getPersonSibs(node.person);
        set.add(node.person);

        if(set.size() > 1 && position != REG_CENTER){
            if(position == REG_LEFT){
                x -= (set.size() - 1) * REG_WIGHT;
            }
            else{
                x += (set.size() - 1) * REG_WIGHT;
            }

        }
        ///endregion

        //Для 1 и 2 родителей!!!
        if(node.person.getParents().size() == 1){
            regulationSisbs(node, x, y);
            regulationBranch(findFamilyNode(node.person.getParents().get(0)), gen + 1, x, y - REG_HIGHT, REG_CENTER);
        }
        else if(node.person.getParents().size() == 2){
            ArrayList<Person> parents = new ArrayList<>(2);

            if(node.person.getParents().get(0).getX() <= node.person.getParents().get(1).getX()){
                parents.add(node.person.getParents().get(0));
                parents.add(node.person.getParents().get(1));
            }
            else{
                parents.add(node.person.getParents().get(1));
                parents.add(node.person.getParents().get(0));
            }

            int newX = regulationSisbs(node, x, y, parents, position);
            if(position == REG_LEFT) x = newX;
            else if(position == REG_RIGHT) x = newX;

            if(node.person.getParents().get(0).getX() <= node.person.getParents().get(1).getX()){
                regulationBranch(findFamilyNode(node.person.getParents().get(0)), gen + 1, x - (int)Math.pow(2, considerGenLenght(node.person, 0) - 1) * REG_WIGHT, y - REG_HIGHT, REG_LEFT);
                regulationBranch(findFamilyNode(node.person.getParents().get(1)), gen + 1, x + (int)Math.pow(2, considerGenLenght(node.person, 0) - 1) * REG_WIGHT, y - REG_HIGHT, REG_RIGHT);
            }
            else{
                regulationBranch(findFamilyNode(node.person.getParents().get(1)), gen + 1, x - (int)Math.pow(2, considerGenLenght(node.person, 0) - 1) * REG_WIGHT, y - REG_HIGHT, REG_LEFT);
                regulationBranch(findFamilyNode(node.person.getParents().get(0)), gen + 1, x + (int)Math.pow(2, considerGenLenght(node.person, 0) - 1) * REG_WIGHT, y - REG_HIGHT, REG_RIGHT);
            }
        }
        else{
            regulationSisbs(node, x, y);
        }

        //node.relocate(x, y);
        //node.setX(x);
        //node.setY(y);
    }
    private void regulationSisbs(FamilyNode node, int x, int y){
        TreeSet<Person> set = tree.getPersonSibs(node.person);
        set.add(node.person);

        if(set.size() % 2 == 1){
            int i = 0, curX = REG_WIGHT;
            FamilyNode tempNode;

            for(Person person : set){
                if(i == 0){
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x, y);
                    tempNode.setX(x);
                    tempNode.setY(y);
                }
                else if(i % 2 == 1){
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x + curX, y);
                    tempNode.setX(x + curX);
                    tempNode.setY(y);
                }
                else{
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x - curX, y);
                    tempNode.setX(x - curX);
                    tempNode.setY(y);
                    curX += REG_WIGHT;
                }
                tempNode.visited = true;
                ++i;
            }
        }
        else{
            int i = 0, curX = REG_WIGHT / 2;
            FamilyNode tempNode;
            for(Person person : set){
                if(i % 2 == 0){
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x + curX, y);
                    tempNode.setX(x + curX);
                    tempNode.setY(y);
                }
                else{
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x - curX, y);
                    tempNode.setX(x - curX);
                    tempNode.setY(y);
                    curX += REG_WIGHT;
                }

                tempNode.visited = true;
                ++i;
            }
        }
    }
    private int regulationSisbs(FamilyNode node, int x, int y, ArrayList<Person> parents, int position){
        ///Parents - слева направо (как в окне)

        ArrayList<Person> leftGroup = new ArrayList<>();
        ArrayList<Person> centerGroup = new ArrayList<>();
        ArrayList<Person> rightGroup = new ArrayList<>();

        int coordDif, leftDif = 0, rightDif = 0;
        int maxGen = 0;
        boolean leftHasParent = false;
        boolean rightHasParent = false;

        for(Person person : parents.get(0).getChildren()){
            if(considerKinshipDegree(node.person, person) == 1){
                leftGroup.add(person);
                if(person.getParents().size() > 1) leftHasParent = true;
            }
        }

        TreeSet<Person> set = tree.getPersonSibs(node.person);
        set.add(node.person);
        for(Person person : set){
            if(considerKinshipDegree(node.person, person) == 2){
                centerGroup.add(person);
            }
        }

        for(Person person : parents.get(1).getChildren()){
            if(considerKinshipDegree(node.person, person) == 1){
                rightGroup.add(person);
                if(person.getParents().size() > 1) rightHasParent = true;
            }
        }

        if(leftHasParent == true) leftDif = REG_WIGHT;
        if(rightHasParent == true) rightDif = REG_WIGHT;

        if(position == REG_LEFT) x -= leftDif + rightDif;
        else if(position == REG_RIGHT) x += leftDif + rightDif;

        ///region center group
        if(centerGroup.size() % 2 == 1){
            int i = 0, curX = REG_WIGHT;
            FamilyNode tempNode;
            coordDif = ((centerGroup.size() - 1) / 2) * REG_WIGHT + REG_WIGHT / 2;

            for(Person person : centerGroup){
                if(i == 0){
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x, y);
                    tempNode.setX(x);
                    tempNode.setY(y);

                    maxGen = considerGenLenght(person, 0);
                }
                else if(i % 2 == 1){
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x + curX, y);
                    tempNode.setX(x + curX);
                    tempNode.setY(y);
                }
                else{
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x - curX, y);
                    tempNode.setX(x - curX);
                    tempNode.setY(y);
                    curX += REG_WIGHT;
                }
                tempNode.visited = true;
                ++i;
            }
        }
        else{
            int i = 0, curX = REG_WIGHT / 2;
            FamilyNode tempNode;
            coordDif = (centerGroup.size() / 2) * REG_WIGHT;

            for(Person person : centerGroup){
                if(i == 0){
                    maxGen = considerGenLenght(person, 0);
                }

                if(i % 2 == 0){
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x + curX, y);
                    tempNode.setX(x + curX);
                    tempNode.setY(y);
                }
                else{
                    tempNode = findFamilyNode(person);
                    tempNode.relocate(x - curX, y);
                    tempNode.setX(x - curX);
                    tempNode.setY(y);
                    curX += REG_WIGHT;
                }

                tempNode.visited = true;
                ++i;
            }
        }
        ///endregion

        int count = 1, branchDif = 0, curBranchDif = 0, count2 = 0, parentDif = 0;
        for(Person person : leftGroup){
            FamilyNode tempNode = findFamilyNode(person);


            if(person.getParents().size() > 1){
                count2++;
                if(count2 > 1) curBranchDif = branchDif;

                if(parents.contains(person.getParents().get(0)) == false){
                    regulationBranch(findFamilyNode(person.getParents().get(0)), 0, x - coordDif - REG_WIGHT * count - curBranchDif, y - REG_HIGHT * (maxGen + 1), REG_CENTER);
                    branchDif += (int)Math.pow(2, considerGenLenght(person.getParents().get(0), 0)) * REG_WIGHT;
                    parentDif = findFamilyNode(person.getParents().get(0)).getX();
                }
                else{
                    regulationBranch(findFamilyNode(person.getParents().get(1)), 0, x - coordDif - REG_WIGHT * count - curBranchDif, y - REG_HIGHT * (maxGen + 1), REG_CENTER);
                    branchDif += (int)Math.pow(2, considerGenLenght(person.getParents().get(1), 0)) * REG_WIGHT;
                    parentDif = findFamilyNode(person.getParents().get(1)).getX();
                }
            }
            else if(person.getParents().size() == 1){
                parentDif = findFamilyNode(person.getParents().get(0)).getX();
            }

            tempNode.relocate(x -coordDif - REG_WIGHT * count - curBranchDif, y);
            tempNode.setX(x -coordDif - REG_WIGHT * count - curBranchDif);
            tempNode.setY(y);

            count++;
        }

        count = 1;
        branchDif = 0;
        curBranchDif = 0;
        count2 = 0;
        parentDif = 0;
        for(Person person : rightGroup){
            FamilyNode tempNode = findFamilyNode(person);

            if(person.getParents().size() > 1){
                count2++;
                if(count2 > 1) curBranchDif = branchDif;

                if(parents.contains(person.getParents().get(1)) == false){
                    regulationBranch(findFamilyNode(person.getParents().get(1)), 0, x + coordDif + REG_WIGHT * count + curBranchDif, y - REG_HIGHT * (maxGen + 1), REG_CENTER);
                    branchDif += (int)Math.pow(2, considerGenLenght(person.getParents().get(1), 0)) * REG_WIGHT;
                    parentDif = findFamilyNode(person.getParents().get(1)).getX();
                }
                else{
                    regulationBranch(findFamilyNode(person.getParents().get(0)), 0, x + coordDif + REG_WIGHT * count + curBranchDif, y - REG_HIGHT * (maxGen + 1), REG_CENTER);
                    branchDif += (int)Math.pow(2, considerGenLenght(person.getParents().get(0), 0)) * REG_WIGHT;
                    parentDif = findFamilyNode(person.getParents().get(0)).getX();
                }
            }
            else if(person.getParents().size() == 1){
                parentDif = findFamilyNode(person.getParents().get(0)).getX();
            }

            System.out.println("REG -> parentDif: " + parentDif);

            tempNode.relocate(x + coordDif + REG_WIGHT * count + curBranchDif, y); ///x + coordDif -> parentDif
            tempNode.setX(x + coordDif + REG_WIGHT * count + curBranchDif);
            tempNode.setY(y);

            count++;
        }

        return x;
    }

    private void checkOutOfPane(){
        for(FamilyNode node : nodeList){
            if(node.getX() > base_pane.getWidth() - REG_WIGHT){
                node.relocate(base_pane.getWidth() - REG_WIGHT, node.getY()); ///x + coordDif -> parentDif
                node.setX((int)base_pane.getWidth() - REG_WIGHT);
            }
        }
    }
    private void moveBranch(Person person, int value, int step){
        FamilyNode tempNode;
        tempNode = findFamilyNode(person);
        tempNode.relocate(tempNode.getX() + value, tempNode.getY());
        tempNode.setX(tempNode.getX() + value);
        tempNode.setY(tempNode.getY());

        for(Person per : tree.getPersonSibs(person)){
            tempNode = findFamilyNode(per);
            tempNode.relocate(tempNode.getX() + value, tempNode.getY());
            tempNode.setX(tempNode.getX() + value);
            tempNode.setY(tempNode.getY());
        }

    }
    private int considerGenLenght(Person person, int len){
        int max = len;
        int cur;

        for(Person per : person.getParents()){
            cur = considerGenLenght(per, len + 1);
            if(cur > max) max = cur;
        }

        return max;
    }
    private int considerKinshipDegree(Person person1, Person person2){
        int count = 0;
        for(Person parent : person1.getParents()){
            for(Person child : parent.getChildren()){
                if(child == person2) count++;
            }
        }

        return count;
    }

    public FamilyTree getTree(){
        return tree;
    }

    private FamilyNode addFamilyNode(){
        FamilyNode node = new FamilyNode(self);
        tree.addPerson(node.person);
        base_pane.getChildren().add(node);
        addPerCount();

        nodeList.add(node);

        tree.checkGenerations();
        updatePersonTreeView();
        return node;
    }
    public void deleteFamilyNode(FamilyNode node){
        node.menu.hide();

        tree.deletePerson(node.person);


        for (ListIterator iterId = node.getNodeLinksIds().listIterator();
             iterId.hasNext();) {

            String id = (String) iterId.next();

            for (ListIterator  iterNode = base_pane.getChildren().listIterator();
                 iterNode.hasNext();) {

                Node node1 = (Node) iterNode.next();

                if (node1.getId() == null)
                    continue;

                if (node1.getId().equals(id)) {
                    iterNode.remove();
                    linkList.remove(node1);
                }
            }

            iterId.remove();
        }

        base_pane.getChildren().remove(node);
        nodeList.remove(node);
        reducePerCount();

        tree.checkGenerations();
        updatePersonTreeView();

        System.out.println("NodeList: " + nodeList);
        System.out.println("Link list: " + linkList);
    }

    private void loadNewFamilyTree(FamilyTree tree){

        clear();
        this.tree = tree;


        for(Person person : tree.getPersons()){
            FamilyNode node = new FamilyNode(this, person);
            nodeList.add(node);

            base_pane.getChildren().add(node);
            addPerCount();
            node.relocate(person.getX(), person.getY());
            node.checkType();
            node.checkName();
            node.checkMenu();
        }

        for(PersonLink link : tree.getLinks()){

            NodeLink nodeLink = new NodeLink(link, NodeLink.LINE);

            linkList.add(nodeLink);

            base_pane.getChildren().add(0, nodeLink);

            nodeLink.bindEndsFromReader(findFamilyNode(link.source), findFamilyNode(link.target), true);
        }

        tree.checkGenerations();
        updatePersonTreeView();
    }

    private FamilyNode findFamilyNode(Person person){
        for(FamilyNode node : nodeList){
            if(node.person == person){
                return node;
            }
        }

        throw new RuntimeException("FamilyPane -> findFamilyNode -> no such Person");
    }
    public void updatePersonTreeView(){
        personTreeView.updatePersonTreeView(tree);
    }
}
