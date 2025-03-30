package org.example.cache;

import org.example.model.Advice;
import org.springframework.stereotype.Component;

@Component
public class AdviceCache extends LfuCache<Advice> {
    
    public AdviceCache() {
        super(100);
    }
}