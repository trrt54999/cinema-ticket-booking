package com.batko.cinematicketbooking.domain.impl;

import com.batko.cinematicketbooking.domain.BaseEntity;
import com.batko.cinematicketbooking.domain.enums.UserRole;
import com.batko.cinematicketbooking.domain.enums.ValidationError;
import com.batko.cinematicketbooking.domain.exception.EntityValidationException;

public class User extends BaseEntity implements Comparable<User> {

  private static final String FIRST_NAME = "firstName";
  private static final String LAST_NAME = "lastName";
  private static final String EMAIL = "email";
  private static final String AGE = "age";
  private static final String ROLE = "role";
  private static final String PASSWORD_HASH = "passwordHash";

  private String firstName;
  private String lastName;
  private String email;
  private String passwordHash;
  private int age;
  private UserRole role;

  private User() {
    super();
  }

  public User(String firstName, String lastName, String email, String passwordHash, int age,
      UserRole role) {
    this();
    setFirstName(firstName);
    setLastName(lastName);
    setEmail(email);
    setPasswordHash(passwordHash);
    setAge(age);
    setRole(role);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    clearError(FIRST_NAME);
    if (firstName == null || firstName.trim().isBlank()) {
      addError(FIRST_NAME, ValidationError.FIRST_NAME_EMPTY.getMessage());
    } else if (firstName.length() < 6 || firstName.length() > 35) {
      addError(FIRST_NAME, ValidationError.FIRST_NAME_LENGTH.getMessage());
    }
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    clearError(LAST_NAME);
    if (lastName == null || lastName.trim().isBlank()) {
      addError(LAST_NAME, ValidationError.LAST_NAME_EMPTY.getMessage());
    } else if (lastName.length() < 6 || lastName.length() > 40) {
      addError(LAST_NAME, ValidationError.LAST_NAME_LENGTH.getMessage());
    }
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    clearError(EMAIL);
    if (email == null || email.trim().isBlank()) {
      addError(EMAIL, ValidationError.EMAIL_EMPTY.getMessage());
    } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
      addError(EMAIL, ValidationError.EMAIL_INVALID.getMessage());
    }
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    clearError(PASSWORD_HASH);
    if (passwordHash == null || passwordHash.trim().isBlank()) {
      addError(PASSWORD_HASH, ValidationError.PASSWORD_EMPTY.getMessage());
    }
    this.passwordHash = passwordHash;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    clearError(AGE);
    if (age < 1 || age > 110) {
      addError(AGE, ValidationError.AGE_INVALID.getMessage());
    }
    this.age = age;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    clearError(ROLE);
    if (role == null) {
      addError(ROLE, ValidationError.ROLE_EMPTY.getMessage());
    }
    this.role = role;
  }

  @Override
  public int compareTo(User o) {
    if (this.lastName == null) {
      return -1;
    }
    return this.lastName.compareTo(o.lastName);
  }

  @Override
  public String toString() {
    return String.format("%s %s %s %d %s", firstName, lastName, email, age, role);
  }
}
