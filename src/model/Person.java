package model;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

public class Person implements Comparable{

    private String name = "";
    private String surname = "";
    private String patronymic = "";
    private int age = -1;
    private boolean isMale;

    private int x;
    private int y;

    public boolean visited = false;

    private int generation = 0;

    private String id = UUID.randomUUID().toString();

    ArrayList<Person> parents; ///Проверить необходимость всего этого
    ArrayList<Person> children;

    public Person(){
        parents = new ArrayList<>(2);
        children = new ArrayList<>();

        System.out.println("Person id: " + id);
    }

    public void addParent(Person person){
        parents.add(person);
        //person.increaseGeneration();
    }
    public void addChild(Person person){
        children.add(person);
    }

    public ArrayList<Person> getParents(){
        return parents;
    }
    public ArrayList<Person> getChildren(){
        return children;
    }

    public void deleteParent(Person person){
        parents.remove(person);
    }
    public void deleteChild(Person person){
        children.remove(person);
    }

    public void setName(String name){
        this.name = name;
    }
    public void setSurname(String surname){
        this.surname = surname;
    }
    public void setPatronymic(String patronymic){
        this.patronymic = patronymic;
    }

    public void setAge (int age){
        if(age < 0) throw new RuntimeException("Uncorrect age: " + age);
        this.age = age;
    }
    public void setGender(boolean isMale){
        this.isMale = isMale;
    }

    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public String getPatronymic() {
        return patronymic;
    }

    public int getAge (){
        return age;
    }
    public boolean getGender(){
        return isMale;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    public String getId(){
        return id;
    }

    void setGeneration(int value){
        generation = value;
    }
    public int getGeneration(){
        return generation;
    }


    public String toString(){
        StringBuilder strb = new StringBuilder(20);

        strb.append(name + " ");
        strb.append(surname);

        if(name.isEmpty() && surname.isEmpty()){
            if(isMale == true){
                strb.append("Неизвестный");
            }
            else{
                strb.append("Неизвестная");
            }
        }

        return strb.toString();
    }

    @Override
    public int compareTo(Object o) {
        Person person = (Person) o;

        return id.compareTo(person.getId());
    }
}
