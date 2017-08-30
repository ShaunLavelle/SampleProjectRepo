package uk.co.sample.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "APP_USER")
public class User {

	private Long mPk;
	private String firstName;
	private String lastName;
	private Long age;
	private String username;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PK", nullable = false)
	public Long getPk() {
		return mPk;
	}

	public void setPk(Long aPk) {
		this.mPk = aPk;
	}
	
	@Column(name = "USERNAME", nullable = false)
	public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

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