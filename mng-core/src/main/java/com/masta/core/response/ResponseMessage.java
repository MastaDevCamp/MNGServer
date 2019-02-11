package com.masta.core.response;

public class ResponseMessage {
    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
    public static final String DB_ERROR = "데이터베이스 에러";
  
	// version 관리
	public static final String READ_JSON_FILE = "JSON file 조회 성공";
	public static final String NOT_READ_JSON_FILE = "JSON file 조회 실패";
	public static final String NOT_ZIP_FILE = "version 업로드시에 zip파일만 가능";
	public static final String SUCCESS_TO_NEW_VERSION = "새로운 버전 등록 성공";



	//user 관리
	public static final String EXIST_USER="이미 가입된 회원";
	public static final String INVALID_ID_OR_PW ="잘못된 아이디 혹은 비밀번호";

	//jwt 관리
	public static final String INVALID_TOKEN="유효하지 않은 JWT 토큰";
	public static final String INVALID_JWT_FORM="유효하지 않은 JWT 형식";

}