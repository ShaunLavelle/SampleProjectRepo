package uk.co.sample.project;

class Person {
	
	String firstName;
	String lastName;
	int age;
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Person(String name, int age) {
		this.firstName = name;
		this.age = age;
	}
	
}
