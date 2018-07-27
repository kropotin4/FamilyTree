package io;

import model.FamilyTree;
import model.Person;
import model.PersonLink;
import view.FamilyNode;
import view.FamilyPane;
import view.NodeLink;

import java.io.*;
import java.util.zip.Deflater;

public class FamilyWriter {

    public FamilyWriter(){

    }

    public void saveToFile(FamilyTree tree, File file) throws IOException {
        FileWriter fileWriter = null;

        fileWriter = new FileWriter(file);

        String res = familyToString(tree);
        System.out.println(res);

        fileWriter.write(res);
        fileWriter.close();

    }

    private String familyToString(FamilyTree tree){
        StringBuilder strb = new StringBuilder(128);

        strb.append("FamilyTree:" + tree.getPersonCount() + "|");
        for(Person person : tree.getPersons()){
            ///region Person
            if(person.getName().isEmpty() == false) strb.append(person.getName());
            strb.append("|");
            if(person.getSurname().isEmpty() == false) strb.append(person.getSurname());
            strb.append("|");
            if(person.getPatronymic().isEmpty() == false) strb.append(person.getPatronymic());
            strb.append("|");
            strb.append(person.getAge());
            strb.append("|");
            if(person.getGender() == true) strb.append("1|");
            else strb.append("0|");
            ///endregion
            strb.append(person.getX() + "|" + person.getY() + "|"); //layout property ??? --- Было
        }

        strb.append("Links:" + tree.getLinkCount() + "|");
        for(PersonLink link : tree.getLinks()){
            strb.append("1|"); //В программе должно быть всегда true - проверить

            int i = 0;
            for(Person person : tree.getPersons()){
                if(person == link.source){
                    strb.append(i + "|");
                }
                i++;
            }
            i = 0;
            for(Person person : tree.getPersons()){
                if(person == link.target){
                    strb.append(i + "|");
                }
                i++;
            }
        }
        strb.append("|");
        return strb.toString();
    }
}
