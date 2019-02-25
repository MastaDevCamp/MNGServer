# AdminController

## **관리자를 위한 api 입니다.**

오직 관리자만이 접근 가능하며, user 전체 목록 조회 및 관리 기능을 담당하고 있습니다.



> **주요 기능**
>- 유저 전체 목록 조회
>- 유저 1명 상세 정보 조회
>- User 권한 변경



## API LISTS

####  [- GET](http://localhost:8180/swagger-ui.html#!/admin-controller/allUserListUsingGET) [/admin/users](http://localhost:8080/swagger-ui.html#!/admin-controller/allUserListUsingGET)

- 유저 전체 목록을 조회하는 API

### [- GET](http://localhost:8180/swagger-ui.html#!/admin-controller/userListUsingGET) [/admin/users/{id}](http://localhost:8080/swagger-ui.html#!/admin-controller/userListUsingGET)

- 유저 1명 상세 정보를 조회하는 API

### [- PUT](http://localhost:8180/swagger-ui.html#!/admin-controller/updateUserUsingPUT) [/admin/{id}](http://localhost:8080/swagger-ui.html#!/admin-controller/updateUserUsingPUT)

- USER 권한 계정을 ADMIN 권한 계정으로 전환하는 API