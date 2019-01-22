package com.masta.core.response;

public class StatusCode {
    public static final int OK = 200; // 서버가 요청을 제대로 처리했다는 뜻이다. 이는 주로 서버가 요청한 페이지를 제공했다는 의미로 쓰인다.
    public static final int CREATE = 201; // 성공적으로 요청되었으며 서버가 새 리소스를 작성했다.
    public static final int NO_CONTENT = 204; // 서버가 요청을 성공적으로 처리했지만 콘텐츠를 제공하지 않는다.
    public static final int BAD_REQUEST = 400; // 서버가 요청의 구문을 인식하지 못했다.
    public static final int UNAUTHORIZED = 401; // 이 요청은 인증이 필요하다. 서버는 로그인이 필요한 페이지에 대해 이 요청을 제공할 수 있다. (인증 안됨에 가깝다.)
    public static final int FORBIDDEN = 403; // 서버가 요청을 거부하고 있다. 예를 들자면, 사용자가 리소스에 대한 필요 권한을 갖고 있지 않다. (401은 인증 실패, 403은 인가 실패라고 볼 수 있음)
    public static final int NOT_FOUND = 404; // 서버가 요청한 페이지(Resource)를 찾을 수 없다. 예를 들어 서버에 존재하지 않는 페이지에 대한 요청이 있을 경우 서버는 이 코드를 제공한다.
    public static final int CONFLICT = 409; // 리소스의 현재 상태와 충돌해서 요청을 처리할 수 없으므로 클라이언트가 요청을 다시 클라이언트가 이 충돌을 수정해서 요청을 다시 보낼 경우
    public static final int INTERNAL_SERVER_ERROR = 500; // 서버에 오류가 발생하여 요청을 수행할 수 없다.
    public static final int SERVICE_UNAVAILABLE = 503; // 서버가 오버로드되었거나 유지관리를 위해 다운되었기 때문에 현재 서버를 사용할 수 없다.
    public static final int DB_ERROR = 600; // database error
}