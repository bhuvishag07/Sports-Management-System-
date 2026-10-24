package model;

import java.io.Serializable;

/**
 * Abstract class Person demonstrating OOP Inheritance and Polymorphism.
 * Serves as the base class for Member and Coach.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated fields
    private String id;
    private String name;
    private String contact;
    private String email;

    // Default Constructor
    public Person() {
    }

    // Parameterized Constructor
    public Person(String id, String name, String contact, String email) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.email = email;
    }

    // Getters and Setters (Encapsulation)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Abstract methods demonstrating Polymorphism
    public abstract String getRole();

    public abstract String getDetails();

    @Override
    public String toString() {
        return "[" + getRole() + "] " + id + " - " + name + " (" + contact + ")";
    }
}
