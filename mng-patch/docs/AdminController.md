# AdminController

## **관리자를 위한 api 입니다.**

오직 관리자만이 접근 가능하며, version 목록조회 및 관리 기능을 하고 있습니다.



> **주요 기능**
>
> - 새로운 버전으로 업데이트 할 파일 등록
> - 등록된 버전 리스트 조회
> - 각 버전의 등록된 파일 및 폴더 리스트 조회
> - 각 버전의 변경사항 리스트 조회



## API LISTS

####  - [POST /admin/newVersion](http://localhost:8082/swagger-ui.html#!/admin-controller/uploadNewVersionUsingPOST)

- 새로운 버전으로 업데이트 할 파일 등록

  1. 관리자 클라이언트 요청 버전 정확성 체크

  2. multipart Formdata로 resource server에 파일 전송

  3. 파일 전처리
  4. SFTP를 이용해 리소스 스토리지에 파일 저장

#### - [GET /admin/all](http://localhost:8082/swagger-ui.html#!/admin-controller/viewAllVersionUsingGET)

- 등록된 버전 리스트 조회

#### - [GET /admin/getFull](http://localhost:8082/swagger-ui.html#!/admin-controller/viewFullVersionUsingGET)

- 각 버전의 등록된 파일 및 폴더 리스트 조회

#### - [GET /admin/getPatch](http://localhost:8082/swagger-ui.html#!/admin-controller/viewPatchVersionUsingGET)

- 각 버전의 변경사항 리스트 조회