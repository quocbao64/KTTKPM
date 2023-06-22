package com.example.chatapp.Entity;

import java.io.Serial;
import java.io.Serializable;

public record Connection(boolean isConnecting, String name) implements Serializable {
    @Serial
    private static final long serialVersionUID = -6893634578516949025L;

}
