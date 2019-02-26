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

     - 버전 정보 생성, 압축 및 해싱 정보로 CRC체크 정보 저장

  4. SFTP를 이용해 리소스 스토리지에 파일 저장

     - version up중 파일 업로딩 오류를 대비한 backup 폴더에 리소스 저장

     - roolback을 대비한 history 버전 파일 저장

     - 최신 버전 파일들 realese에 저장

     - full_ver.json 파일 저장 : 최신버전 파일 혹은 폴더 리스트 정보

     - patch_ver.json 파일 저장 : 업데이트된 파일 혹은 폴더 리스트 정보

       

#### - [GET /admin/all](http://localhost:8082/swagger-ui.html#!/admin-controller/viewAllVersionUsingGET)

- 등록된 버전 리스트 조회

#### - [GET /admin/getFull](http://localhost:8082/swagger-ui.html#!/admin-controller/viewFullVersionUsingGET)

- 각 버전의 등록된 파일 및 폴더 리스트 조회

#### - [GET /admin/getPatch](http://localhost:8082/swagger-ui.html#!/admin-controller/viewPatchVersionUsingGET)

- 각 버전의 변경사항 리스트 조회