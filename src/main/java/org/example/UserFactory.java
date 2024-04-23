package org.example;

import java.util.List;
import java.util.SortedSet;

public class UserFactory {
    public static User factory (AccountType accountType) {
        if(accountType.equals(AccountType.Regular))
            return new Regular();
        else if(accountType.equals(AccountType.Contributor))
            return new Contributor();
        else if(accountType.equals(AccountType.Admin))
            return new Admin();
        return null;

    }
}
