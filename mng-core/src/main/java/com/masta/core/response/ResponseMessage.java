package com.masta.core.response;

public class ResponseMessage {
	public static final String READ_USER = "회원 정보 조회 성공";
	public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
	public static final String DB_ERROR = "데이터베이스 에러";


	// version 관리
	public static final String  READ_JSON_FILE = "JSON file 조회 성공";
	public static final String NOT_READ_JSON_FILE = "JSON file 조회 실패";

	//user 관리
	public static final String EXIST_USER="이미 가입된 회원";
	public static final String INVALID_ID_OR_PW ="잘못된 아이디 혹은 비밀번호";
}