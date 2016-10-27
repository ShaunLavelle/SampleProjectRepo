package uk.co.sample.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "APP_USER")
public class User extends GenericDomain {

	private String firstName;
	private String lastName;
	private Long age;

	@Column(name = "FIRST_NAME", nullable = false)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "LAST_NAME")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "AGE")
	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}
}