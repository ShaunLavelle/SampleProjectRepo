package uk.co.sample.project;

public class Student {

	String name;
	String location;
	
	public Student(Person person) {
		this.name = person.firstName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
