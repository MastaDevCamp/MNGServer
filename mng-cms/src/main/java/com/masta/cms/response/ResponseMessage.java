package com.masta.cms.response;

public class ResponseMessage {
    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String READ_EMPTY_USER = "회원 조회 정보가 없습니다.";
    public static final String NOT_FOUND_USER = "회원을 찾을 수 없습니다.";
    public static final String CREATED_USER = "회원가입 성공";
    public static final String INVALID_INFO = "잘못된 입력 정보 입니다.";
    public static final String UPDATE_USER = "회원 정보 수정 성공";
    public static final String DELETE_USER = "회원 탈퇴 성공";

    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGIN_FAIL = "로그인 실패";

    public static final String CURRENT_USER = "접근한 유저와 토큰의 유저가 같다.";
    public static final String NOT_CURRENT_USER = "접근한 유저와 토큰의 유저가 다르다.";

    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";

    public static final String DB_ERROR = "데이터베이스 에러";
    public static final String INVALID_CREATED_USER = "회원 가입 정보가 잘못되었습니다.";
    public static final String ALREADY_USER ="이미 존재하는 회원 정보 입니다.";
}
