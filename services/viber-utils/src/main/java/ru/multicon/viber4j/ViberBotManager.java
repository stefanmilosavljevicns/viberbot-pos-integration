package com.payten.viberutil;

import java.util.HashMap;
import java.util.Map;

public class ViberBotManager {

    private static final Map<String, ViberBot> bots = new HashMap<>();

    public static ViberBot viberBot(String authToken) {
        if (bots.containsKey(authToken))
            return bots.get(authToken);
        else {
            ViberBot bot = new ViberBotImpl(authToken);
            bots.put(authToken, bot);
            return bot;
        }
    }

    public static ViberBot viberBot(String authToken, String senderName, String senderAvatar) {
        if (bots.containsKey(authToken))
            return bots.get(authToken);
        else {
            ViberBot bot = new ViberBotImpl(authToken, new SenderInfo(senderName, senderAvatar));
            bots.put(authToken, bot);
            return bot;
        }
    }
}
