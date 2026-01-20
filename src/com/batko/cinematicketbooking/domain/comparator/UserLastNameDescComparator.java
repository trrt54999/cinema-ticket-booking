package com.batko.cinematicketbooking.domain.comparator;

import com.batko.cinematicketbooking.domain.impl.User;
import java.util.Comparator;

public class UserLastNameDescComparator implements Comparator<User> {

  @Override
  public int compare(User o1, User o2) {
    return o2.getLastName().compareTo(o1.getLastName());
  }
}