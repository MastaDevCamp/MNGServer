# GuestController

## **소셜 로그인, 회원가입 절차를 거치지 않은 게스트 계정을 관리를 위한 API입니다.**    

> **주요 기능**
>
> - 게스트 유저에서 계정 유저로 전환



## API LISTS

#### [- PUT](http://localhost:8080/swagger-ui.html#!/guest-controller/switchUserUsingPUT) [/guest/switch/{usernum}](http://localhost:8080/swagger-ui.html#!/guest-controller/switchUserUsingPUT)

- 게스트 유저에서 계정 유저로 전환
  - 게스트 계정으로 로그인 할 경우 기기 분실, 게임 삭제 등으로 소중한 게임 플레이 내역을 잃어버릴 위험이 있어, 유저 전환 기능 탑재

  