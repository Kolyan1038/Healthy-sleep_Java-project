package org.healthysleep.cache;

import org.healthysleep.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserCache extends LfuCache<User> {
    public UserCache() {
        super(100);
    }
}
