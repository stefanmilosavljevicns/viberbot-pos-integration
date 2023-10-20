package com.payten.viberutil.keyboard;

/**
 * @author n.zvyagintsev
 *
 * Types of button actions
 */
public enum BtnActionType {
    NONE     ("none"),
    REPLY    ("reply"),
    OPEN_URL ("open-url");
    BtnActionType(String action) {
        this.action = action;
    }
    private final String action;

    public String actionName() {
        return action;
    }
}
