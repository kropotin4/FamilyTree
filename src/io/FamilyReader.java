package io;

import model.FamilyTree;
import model.Person;
import model.PersonLink;
import view.FamilyNode;
import view.FamilyPane;
import view.NodeLink;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class FamilyReader {



    private ArrayList<Person> personList = new ArrayList<>(16);

    /////////

    public FamilyReader(){

    }

    public FamilyTree loadFromFile(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        char[] buf = new char[2048];

        personList.clear();

        fileReader.read(buf, 0, buf.length);
        return familyFromString(String.valueOf(buf));
    }

    private FamilyTree familyFromString(String str){

        if(str.substring(0, 11).equals("FamilyTree:") == false){
            throw new RuntimeException("FamilyReader -> load exception (1)");
        }

        FamilyTree tree = new FamilyTree();
        int nodeCount = 0, linkCount = 0;
        int i = 11, pre;

        ///region Person
        while(str.charAt(++i) != '|');
        nodeCount = Integer.parseInt(str.substring(11, i));

        for(int g = 0; g < nodeCount; ++g){
            Person person = new Person();
            tree.addPerson(person); //Может TreeMap -> ArrayList ???
            personList.add(person);

            int x = 0, y = 0, age = -1;
            ///region Person data
            pre = i;
            while(str.charAt(++i) != '|');
            if(i - pre > 1) person.setName(str.substring(pre + 1, i));

            pre = i;
            while(str.charAt(++i) != '|');
            if(i - pre > 1) person.setSurname(str.substring(pre + 1, i));

            pre = i;
            while(str.charAt(++i) != '|');
            if(i - pre > 1) person.setPatronymic(str.substring(pre + 1, i));

            pre = i;
            while(str.charAt(++i) != '|');
            if(i - pre > 1) age = Integer.parseInt(str.substring(pre + 1, i));
            if(age != -1) person.setAge(age);

            pre = i;
            while(str.charAt(++i) != '|');
            if(str.charAt(i - 1) == '1') person.setGender(true);
            else person.setGender(false);

            pre = i;
            while(str.charAt(++i) != '|');
            if(i - pre > 1) x = Integer.parseInt(str.substring(pre + 1, i));

            pre = i;
            while(str.charAt(++i) != '|');
            if(i - pre > 1) y = Integer.parseInt(str.substring(pre + 1, i));

            person.setX(x);
            person.setY(y);
            ///endregion
        }
        ///endregion

        if(str.substring(++i, i + 6).equals("Links:") == false){
            throw new RuntimeException("FamilyReader -> load exception (2)");
        }

        ///region PersonLink
        i += 6;
        pre = i;
        while(str.charAt(++i) != '|');
        linkCount = Integer.parseInt(str.substring(pre, i));

        for(int g = 0; g < linkCount; ++g){

            int source, target;
            Person sourcePerson, targetPerson;
            boolean toParent;

            pre = i;
            while(str.charAt(++i) != '|');
            if(str.charAt(i - 1) == '1') toParent = true;
            else toParent = false;

            pre = i;
            while(str.charAt(++i) != '|');
            if(i - pre > 1) source = Integer.parseInt(str.substring(pre + 1, i));
            else throw new RuntimeException("FamilyReader -> NodeLink -> source");
            sourcePerson = personList.get(source);

            pre = i;
            while(str.charAt(++i) != '|');
            if(i - pre > 1) target = Integer.parseInt(str.substring(pre + 1, i));
            else throw new RuntimeException("FamilyReader -> NodeLink -> target");
            targetPerson = personList.get(target);

            PersonLink link;
            if(toParent == true){
                link = new PersonLink(sourcePerson, targetPerson);
                sourcePerson.addParent(targetPerson);
                targetPerson.addChild(sourcePerson);
            }
            else{
                link = new PersonLink(targetPerson, sourcePerson);
                sourcePerson.addChild(targetPerson);
                targetPerson.addParent(sourcePerson);
            }

            tree.addLink(link);
        }
        ///endregion

        return tree;
    }
}
