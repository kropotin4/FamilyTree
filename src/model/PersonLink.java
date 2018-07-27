package model;

public class PersonLink {
    public Person source;
    public Person target;

    public PersonLink(Person source, Person target){
        this.source = source;
        this.target = target;
    }
}
