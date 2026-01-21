package com.batko.cinematicketbooking.domain.comparator;

import com.batko.cinematicketbooking.domain.model.User;
import java.util.Comparator;

public class UserFullNameComparator implements Comparator<User> {

  @Override
  public int compare(User o1, User o2) {
    int res = o1.getFirstName().compareTo(o2.getFirstName());
    if (res != 0) {
      return res;
    }
    return o1.getLastName().compareTo(o2.getLastName());
  }
}
