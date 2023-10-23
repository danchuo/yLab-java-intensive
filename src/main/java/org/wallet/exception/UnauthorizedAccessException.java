package org.wallet.exception;

import java.io.Serial;

public class UnauthorizedAccessException extends IllegalArgumentException {

  @Serial private static final long serialVersionUID = -6452914812304209181L;

    public UnauthorizedAccessException(){
        super("No player is currently authenticated.");
    }
}
