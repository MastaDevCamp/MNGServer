# MNG(Meet&Greet) Resource Server

## 0. 프로젝트 목적
#### 실제 패치 업로드 서비스 만들기

![그림1](..\mng-patch\docs\docs_image\p2.png)



## 1. What is MNG RESOURCE SERVER

### MNG의 리소스 파일 관리 API 구축 및 버전 관리 시스템 담당
상세한 기능은 다음과 같습니다.
- 관리자 페이지에서 새 리소스 버전 업데이트
  - 관리자 클라이언트 요청 버전 정확성 체크
  - http mulipart formdata로 resource server에 파일 전송
  - 파일 전처리 : 버전 정보 생성, 압축 및 해싱 정보로 CRC 체크 정보 저장
  - SFTP를 이용해 resource storage에 정확한 파일 저장
- 클라이언트에서 새버전 패치 요청시 변경 파일만 선택적으로 업데이트
  - 클라이언트 요청 버전 정확성 체크
  - 버전관리 json파일끼리 관계를 파악하여 변경 정보 정리
- 현재까지의 버전 리스트 조회



## 2. API 
프로젝트 실행후 http://localhost:8082/swagger-ui.html에서 API 상세 목록과 API Call 테스트가 가능합니다.

API 관련 자세한 정보는 아래 링크에서 확인하실 수 있습니다.
| Plugin                 | LINK | 요약                      |
| ---------------------- | ---- | ------------------------- |
| [AdminController](../mng-patch/docs/AdminController.md) | [link](../mng-patch/src/main/java/com/masta/patch/controller/AdminController.java) | 관리자 관련 api |
| [UserController](../mng-patch/docs/UserController.md) | [link](../mng-patch/src/main/java/com/masta/patch/controller/UserController.java) | 유저 패치 관련 api  |



## 3. 사용 기술 및 툴 

- spring boot
- java 8
- swagger
- nginx



## 4. 리소스 스토리지 파일 디렉토리

```
#ftp directory
 └─ masta
    ├─ file
    |  ├─ backup
    |  |  └─ [full files]
    |  ├─ history
    |  |  ├─ 0.0.1
    |  |  |  └─ [patch files]
    |  |  ├─ 0.0.2
    |  |  |  └─ [patch files]
    |  |  └─ 0.0.3
    |  |  |  └─ [patch files]
    |  └─ release
    |     └─ [full files]
    └─ log
       ├─ full
       |  └─ [json files : FULL_VER_0.0.1.json]
	   └─ patch
          └─ [json files : PATCH_VER_0.0.1.json]
```

- file : 실제 파일 저장
  - backup : 버전 업데이트시 이전 버전의 full files 임시 저장
  - history : 각 버전 폴더 별 변경 사항이 있는 file 저장
  - release :  최신 버전의 모든 file 저장

- log : 파일의 버전관리를 위한 json파일 저장
  - full : 모든 디렉토리 파일을 json 파일로 저장
  - patch : 변경 된 파일의 정보를 json 파일로 저장