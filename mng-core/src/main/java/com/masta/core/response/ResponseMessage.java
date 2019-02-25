package com.masta.core.response;

public class ResponseMessage {
    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
    public static final String DB_ERROR = "데이터베이스 에러";


    // version 관리
    public static final String READ_JSON_FILE = "JSON file 조회 성공";
    public static final String NOT_READ_JSON_FILE = "JSON file 조회 실패";
    public static final String LASTEST_VERSION = "최신 버전 조회 성공";
    public static final String NOT_VERSION_FORMAT = "버전 포멧 오류";
    public static final String FILA_TO_GET_VERSION = "최신 버전 조회 실패";
    public static final String NOT_ZIP_FILE = "version 업로드시에 zip파일만 가능";
    public static final String UPLOAD_FIREST_VERSION = "처음 버전 등록 성공";
    public static final String SUCCESS_TO_NEW_VERSION = "새로운 버전 등록 성공";

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
    public static final String Forbidden_User = "유저 권환 없음";

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


    //cms 관리
    public static final String FIND_HISTORY = "히스토리 정보 조회 성공";
    public static final String REGISTER_HISTORY = "히스토리 정보 조회 성공";
    public static final String FIND_MAILBOX = "우편함 조회 성공";
    public static final String GET_REWARD_MAILBOX = "우편함 보상 수령 성공";
    public static final String ALREADY_REWARD = "해당 사용자가 이미 수령한 이벤트";

    public static final String REGISTER_NOTICE = "공지사항 등록 성공";
    public static final String FIND_NOTICE = "공지사항 조회 성공";
    public static final String UPDATE_NOTICE = "공지사항 업데이트 성공";
    public static final String DELETE_NOTICE = "공지사항 삭제 성공";
    public static final String CHECK_INVALID_NOTICE = "유효하지 않은 공지사항 확인";

    public static final String FIND_FAVOR = "호감도 조회 성공";
    public static final String MODIFY_FAVOR = "호감도 수정 성공";

    public static final String FIND_PUZZLES = "퍼즐 조회 성공";
    public static final String REGISTER_PIECES = "퍼즐 조각 등록 성공";
    public static final String MODIFY_PIECES = "퍼즐 조각 수정 성공";
    public static final String DELETE_PIECES = "퍼즐 조각 삭제 성공";


    public static final String FIND_SCENARIO = "시나리오 조회 성공";
    public static final String FIND_SCENARIO_ANSWER = "시나리오별 답변 조회 성공";
    public static final String REGISTER_SCENARIO_ANSWER = "답변 등록 성공";
    public static final String MODIFY_SCENARIO_PROGRESS = "시나리오 진행상황 변경";
    public static final String ALREADY_SCENARIO_START = "이미 진행된 시나리오";

    public static final String FIND_USER_DETAIL = "사용자 게임정보 조회 성공";
    public static final String MODIFY_NICKNAME = "사용자 닉네임 변경 성공";
    public static final String MODIFY_PUSH = "사용자 푸시 여부 변경 성공";
    public static final String MODIFY_MONEY = "사용자 재화 정보 변경 성공";
    public static final String SPEND_HEART = "사용자 하트 1개 소모";
    public static final String MODIFY_HEARTS_CNT = "사용자 하트 개수 변경 성공";
    public static final String MODIFY_USER_DETAIL = "사용자 게임정보 전체 변경";

    public static final String CREATE_NEW_USER = "새 사용자 생성 성공";
}