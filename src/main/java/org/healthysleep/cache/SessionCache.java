package org.healthysleep.cache;

import org.healthysleep.model.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionCache extends LfuCache<Session> {
    
    public SessionCache() {
        super(100);
    }
}