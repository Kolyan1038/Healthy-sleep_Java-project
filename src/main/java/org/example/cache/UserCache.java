package org.example.cache;

import org.example.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserCache extends LFUCache<User> {
    public UserCache() {
        super(100);
    }
}
