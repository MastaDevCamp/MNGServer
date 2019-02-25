# MNGServer

## Settings

### Init Setting

- Settings > Build, Execution, Deployment > Compiler > Annotation Processors > 'Enable annotation processing' check

### Directory tree

> ─ root  
> &nbsp; &nbsp; &nbsp; └ core  
> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├ auth-server  
> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; ├ cms-server  
> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; └ patch-server  

### Dependencies

- root
  - spring boot 2.1.2 RELEASE
  - lombok
  - swagger
- core
  - spring-boot-starter-web
  - mysql-connector-java
  - mybatis-spring-boot-starter
- [MemberShip Server](mng-auth)
- [CMS Server](mng-cms)
- [Patch Server](mng-patch)



## Branch

### Naming

- #### devlop

- #### feature/{server_name}/{feature_name}



### Concept

##### master (실제 사용 X)

> 제품으로 출시될 수 있는 브랜치
> 배포(Release) 이력을 관리하기 위해 사용. 즉, 배포 가능한 상태만을 관리한다.

##### dev

> 기능 개발을 위한 브랜치들을 병합하기 위해 사용. 즉, 모든 기능이 추가되고 버그가 수정되어
> 배포 가능한 안정적인 상태라면 develop 브랜치를 ‘master’ 브랜치에 병합(merge)한다.
> 평소에는 이 브랜치를 기반으로 개발을 진행한다.

##### feature

> feature 브랜치는 새로운 기능 개발 및 버그 수정이 필요할 때마다 ‘develop’ 브랜치로부터 분기한다.
> 개발이 완료되면 ‘develop’ 브랜치로 병합(merge)하여 다른 사람들과 공유한다.

출처 : https://gmlwjd9405.github.io/2018/05/11/types-of-git-branch.htmlhttps://gmlwjd9405.github.io/2018/05/11/types-of-git-branch.html
