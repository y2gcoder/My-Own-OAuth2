package com.y2gcoder.auth.oauth.infra;

public class NotFoundRedirectUriException extends RuntimeException {

    //TODO 메시지 변경 필요
    public NotFoundRedirectUriException() {
        super("NOT Found Redirect URI");
    }
}
