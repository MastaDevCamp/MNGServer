package com.masta.cms.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultRes<T> {

    //Response StatusCode
    private int statusCode;

    //Response Message
    private String responseMessage;

    //Response TestData
    private T responseData;

    /**
     *
     * @param statusCode
     * @param responseMessage
     */
    public DefaultRes(final HttpStatus statusCode, final String responseMessage) {
        this.statusCode = statusCode.value();
        this.responseMessage = responseMessage;
        this.responseData = null;
    }

    /**
     *
     * @param statusCode
     * @param responseMessage
     * @param <T>               에베베베베베베
     * @return                  에베ㅔ베베베
     */
    public static<T> DefaultRes<T> res(final int statusCode, final String responseMessage) {
        return res(statusCode, responseMessage, null);
    }

    public static<T> DefaultRes<T> res(final int statusCode, final String responseMessage, final T t) {
        return DefaultRes.<T>builder()
                .responseData(t)
                .statusCode(statusCode)
                .responseMessage(responseMessage)
                .build();
    }

    public static final DefaultRes FAIL_DEFAULT_RES
            = DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR);
}
