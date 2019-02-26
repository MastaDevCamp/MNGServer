# [MNG(Meet&Greet) Member Server](../../mng-auth/readme.md)

## 0. 프로젝트 목적
목적...? 
> 실제 서비스를 고려하며 만들고자 하였다.를 잘 풀어 내자!  
> 또 무엇이 있을까? 벽에 부딪치자...?!

## 1. What is MNG MEMBER SERVER
### MNG의 전반적인 회원관리 API 서비스 구축 및 인증 시스템 담당
상세한 기능은 다음과 같습니다.
- 페이스북, 구글 계정으로 로그인이 가능한 소셜 로그인 (카카오톡은 추후 제공 예정)
- 아이디, 비밀번호를 발급하여 로그인이 가능한 계정 회원가입 및 로그인
- 간단한 클릭으로 로그인이 가능한 게스트 로그인
- 로그아웃
- 로그인 시 인증 토큰 발급
- 인증 및 인가

## 2. API 
프로젝트 실행후 http://localhost:8180/swagger-ui.html에서 API 상세 목록과 API Call 테스트가 가능합니다.

API 관련 자세한 정보는 아래 링크에서 확인하실 수 있습니다.

| Plugin                 | LINK | 요약                      |
| ---------------------- | ---- | ------------------------- |
| [AdminController](../mng-auth/docs/AdminController.md)        | [link](../mng-auth/src/main/java/com/masta/auth/membership/controller/AdminController.java) | 관리자 관련 api            |
| [GuestController](../mng-auth/docs/GuestController.md)        | [link](../mng-auth/src/main/java/com/masta/auth/membership/controller/GuestController.java) | 게스트 유저 관련 api       |
| [SocialController](../mng-auth/docs/SocialController.md)      | [link](../mng-auth/src/main/java/com/masta/auth/membership/controller/SocialController.java) | 소셜 로그인 유저 관련 api  |
| [UserController](../mng-auth/docs/UserController.md)          | [link](../mng-auth/src/main/java/com/masta/auth/membership/controller/UserController.java) | 전체 user 관런 api        |

## 3. 사용 기술 및 툴 
- spring boot
- spring data jpa
- spring security
- java 8
- swagger
