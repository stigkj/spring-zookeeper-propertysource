package org.springframework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TestComponent {
    @Autowired
    Environment env;

    @Value("${test}")
    String getValueUsingValueAnnotationOnVariable;

    public String getValueFromEnvironment() {
        return env.getProperty("test");
    }
}
