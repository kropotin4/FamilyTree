package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

public class FamilyTree {

    private ArrayList<Person> family = new ArrayList<>();
    private ArrayList<PersonLink> links = new ArrayList<>();

    private int maxGeneraton;
    private int minGeneraton;

    public FamilyTree(){

    }

    public void addPerson(Person person){
        family.add(person);
        checkGenerations();
    }
    public PersonLink createLink(Person source, Person target){
        PersonLink personLink = new PersonLink(source, target);
        links.add(personLink);

        checkGenerations();

        return personLink;
    }
    public void addLink(PersonLink link){
        links.add(link);
        checkGenerations();
    }

    public TreeSet<Person> getPersonSibs(Person person){
        int count = 0;
        TreeSet<Person> set = new TreeSet<>();

        for(Person parent : person.getParents()){
            for(Person child : parent.getChildren()){
                set.add(child);
            }
        }

        set.remove(person);

        return set;
    }

    public ArrayList<Person> getPersons(){
        return family;
    }
    public ArrayList<PersonLink> getLinks(){
        return links;
    }

    public void deletePerson(Person person){
        for(PersonLink link : links){
            if(link.source == person){
                link.target.deleteChild(person);
            }
            if(link.target == person){
                link.source.deleteParent(person);
            }

            //link.source.deleteParent(person);
            //link.target.deleteParent(person);
            //link.source.deleteChild(person);
            //link.target.deleteChild(person);
        }

        while(links.size() > 0) {
            boolean complete = false;
            for (PersonLink pLink : links) {
                if (pLink.target == person || pLink.source == person) {
                    links.remove(pLink);
                    complete = true;
                    break;
                }
            }
            if(complete == false) break;
        }

        family.remove(person);
        System.out.println("Family tree (Person) -> " + family.toString());

        checkGenerations();
    }
    public void deleteLink(PersonLink link){
        links.remove(link);
        System.out.println("Family tree (Links) -> " + links.toString());

        checkGenerations();
    }

    public int getPersonCount(){
        return family.size();
    }
    public int getLinkCount(){
        return links.size();
    }

    public void checkGenerations(){
        for(Person person : family){
            person.visited = false;
        }

        maxGeneraton = Integer.MIN_VALUE;
        minGeneraton = Integer.MAX_VALUE;

        for(Person person : family){
            if(person.visited == false)
            changeGen(person, 0);
        }
    }
    private void changeGen(Person person, int gen){
        person.setGeneration(gen);
        person.visited = true;

        if(gen > maxGeneraton) maxGeneraton = gen;
        if(gen < minGeneraton) minGeneraton = gen;

        for(Person pre : person.parents){
            if(pre.visited == false)
            changeGen(pre, gen + 1);
        }

        for(Person sub : person.children){
            if(sub.visited == false)
                changeGen(sub, gen - 1);
        }

    }

    public int getMaxGeneraton() {
        return maxGeneraton;
    }
    public int getMinGeneraton(){
        return minGeneraton;
    }
}
