package test;

import io.FamilyReader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.FamilyTree;
import view.FamilyPane;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.*;

public class FamilyReaderTest {

    @org.junit.Test
    public void loadFromFile() {

        FamilyReader familyReader = new FamilyReader();

        File f1 = new File("C:\\Desktop\\Java\\IDE\\Family Tree\\src\\test\\ReadersExamples\\1.tree");
        File f2 = new File("C:\\Desktop\\Java\\IDE\\Family Tree\\src\\test\\ReadersExamples\\2.tree");
        File f3 = new File("C:\\Desktop\\Java\\IDE\\Family Tree\\src\\test\\ReadersExamples\\3.tree");

        if(f1 != null){
            FamilyTree familyTree = null;
            try {
                familyTree = familyReader.loadFromFile(f1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assertEquals(familyTree.getPersonCount(), 2);
            assertEquals(familyTree.getLinkCount(), 1);
        }

        if(f2 != null){
            FamilyTree familyTree = null;
            try {
                familyTree = familyReader.loadFromFile(f2);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assertEquals(familyTree.getPersonCount(), 3);
            assertEquals(familyTree.getLinkCount(), 2);
        }

        if(f3 != null){
            FamilyTree familyTree = null;
            try {
                familyTree = familyReader.loadFromFile(f3);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assertEquals(familyTree.getPersonCount(), 4);
            assertEquals(familyTree.getLinkCount(), 3);
        }
    }
}