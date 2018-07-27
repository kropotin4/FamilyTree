package test;

import model.FamilyTree;
import model.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class FamilyTreeTest {

    @Test
    public void addPerson() {
        Person person = new Person();
        FamilyTree familyTree = new FamilyTree();

        familyTree.addPerson(person);

        assertEquals(1, familyTree.getPersonCount());
    }

    @Test
    public void getPerson() {
        Person person = new Person();
        FamilyTree familyTree = new FamilyTree();

        familyTree.addPerson(person);

        //assertEquals(person, familyTree.getPerson(person.getId()));
    }

    @Test
    public void deletePerson() {
        Person person = new Person();
        FamilyTree familyTree = new FamilyTree();

        familyTree.addPerson(person);
        familyTree.deletePerson(person);

        assertEquals(0, familyTree.getPersonCount());
    }
}