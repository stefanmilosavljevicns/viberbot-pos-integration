package com.payten.viberutil.keyboard;

import com.payten.viberutil.outgoing.MessageType;

public class RichMedia extends ButtonContainer {

    public RichMedia() {
        this.type = MessageType.CAROUSEL.getKeyName();
    }

    public RichMedia(String altText) {
        this.type = MessageType.CAROUSEL.getKeyName();
        this.altText = altText;
    }
}
