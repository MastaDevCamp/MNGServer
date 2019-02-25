package com.masta.core.response;

public class ResponseMessage {
    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
    public static final String DB_ERROR = "데이터베이스 에러";


    // version 관리
    public static final String READ_JSON_FILE = "JSON file 조회 성공";
    public static final String NOT_READ_JSON_FILE = "JSON file 조회 실패";
    public static final String SUCCESS_TO_GET_LATEST_VERSION = "최신 버전 전달 성공";
    public static final String NOT_VERSION_FORMAT = "버전 포멧 오류";
    public static final String FILA_TO_GET_VERSION = "최신 버전 조회 실패";
    public static final String NOT_ZIP_FILE = "version 업로드시에 zip파일만 가능";
    public static final String UPLOAD_FIREST_VERSION = "처음 버전 등록 성공";
    public static final String SUCCESS_TO_NEW_VERSION = "새로운 버전 등록 성공";
    public static final String NOT_LATEST_VERSION = "최신 버전이 아님";
    public static final String FAIL_TO_SAVE_JSON_IN_LOCAL = "JSON 로컬에 저장 실패";
    public static final String SUCCESS_TO_SAVE_JSON_IN_LOCAL = "JSON 로컬에 저장 성공";
    public static final String FAIL_TO_UPLOAD_JSON_TO_SFTP = "JSON 파일 SFTP에 업로드 실패";
    public static final String FAIL_TO_UPLOAD_FILES_TO_SFTP = "파일 SFTP에 업로드 실패";
    public static final String SUCCESS_TO_UPLOAD_PATCH = "패치 파일 업로드 성공";
    public static final String FAIL_TO_INSERT_JSON_DB = "Json DB 입력 실패";
    public static final String SUCCESS_TO_INSERT_JSON_DB = "Json DB 입력 성공";

    public static final String UPDATE_NEW_VERSION(String client, String latest) {
        return client + "버전에서 " + latest + " 버전으로 업데이트 리스트 전달";
    }

    public static final String ALREADY_UPDATED_VERSION = "이미 최신버전으로 업데이트 완료";
    public static final String NOT_LAST_VERSION = "최신 버전 등록 실패";
    public static final String ALREADY_REGISTERED_VERSION = "이미 등록된 버전";
    public static final String VERSION_ERROR = "확인되지 않은 버전";
    public static final String READ_VERSION = "버전 조회 성공";
    public static final String EMPTY_VERSION = "버전 리스트가 없음";

    // user 관리
    public static final String EXIST_USER = "이미 가입된 회원";
    public static final String INVALID_USER_DATA = "잘못된 유저 정보";
    public static final String REGIST_USER = "회원가입 성공";

    //social 관리
    public static final String FAILED_GET_APP_TOKEN = "소셜에서 app token 발행시 에러 발생";
    public static final String FAILED_GET_SOCIALUSERINFO = "잘못된 social user access token ";

    // jwt 관리
    public static final String INVALID_TOKEN = "유효하지 않은 JWT 토큰";
    public static final String INVALID_JWT_FORM = "유효하지 않은 JWT 형식";

    // admin 관리
    public static final String CHANGE_USER_ROLE = "유저 권한 변경 성공";
    public static final String ALREADY_GOT_ROLE = "유저 권한 변경 에러";


    public static final String NOT_FOUND_USER = "찾을 수 없는 사용자";

}