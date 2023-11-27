package com.wanted.bobo.expense.dto.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {

    private String title;
    private String description;

    public MessageBuilder(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Map<String, Object> build() {
        Map<String, Object> message = new HashMap<>();
        message.put("embeds", Collections.singletonList(createEmbed()));
        return message;
    }

    private Map<String, Object> createEmbed() {
        Map<String, Object> embed = new HashMap<>();
        embed.put("title", title);
        embed.put("description", description);
        embed.put("color", 0x87CEEB);
        return embed;
    }

}
