package org.example.cache;

import org.example.model.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionCache extends LfuCache<Session> {
    
    public SessionCache() {
        super(100);
    }
}