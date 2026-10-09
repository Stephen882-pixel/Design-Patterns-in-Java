package com.example.PrototypeDesignPattern;

import java.util.HashMap;
import java.util.Map;

// A "template shelf": build each template once, then hand out clones of it
public class BotRegistry {
    private final Map<String, GameBotCharacters> templates = new HashMap<>();

    public void addTemplate(String key, GameBotCharacters template) {
        templates.put(key, template);
    }

    public GameBotCharacters get(String key) {
        GameBotCharacters template = templates.get(key);
        if (template == null) {
            throw new IllegalArgumentException("No template called " + key);
        }
        return template.customizeClone();
    }
}
