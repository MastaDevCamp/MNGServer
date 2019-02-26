# UserController

## **유저 패치를 위한 api 입니다.**

유저 클라이언트에서 resource storage에 있는 파일로 패치 업데이트를 하고 싶을 때 사용합니다.



> **주요 기능**
>
> - 게임 유저 클라이언트에 새로운 버전으로 리소스 패치
> - 최신 버전 조회



## API LISTS

#### - [POST /updateResource/Merge](http://localhost:8082/swagger-ui.html#!/user-controller/updateClientResourceUsingPOST)

- 게임 유저 클라이언트에 새로운 버전으로 리소스 패치

  1. 관리자 클라이언트 요청 버전 정확성 체크

  2. 현재 버전과, 최신 버전 사이 patch_ver.json 파일 읽기

  3. patch_ver.json 파일 조합해서 현재 버전과 최신 버전 사이 업데이트 리스트 조합

     - ex ) client version : 0.0.1 / latest version : 0.0.5

     - patch_ver.json 0.0.2 0.0.3 0.0.4 0.0.5 의 내용 판단 및 조합

       

#### - [GET /updateResource/lastVersion](http://localhost:8082/swagger-ui.html#!/user-controller/checkLastVersionUsingGET)

- 최신 버전 조회 