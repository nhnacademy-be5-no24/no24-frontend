# No24 Bookstore

## 실행

### Package
`mvn package -f pom.xml`

### Execute
`java -Dserver.port=${port} -jar -Dspring.profiles.active=${active_file} -Deureka.instance.instance-id=${instance_id} ~/target/shop.jar > log  2>&1 &`

## Service Introduction
클라우드 서비스를 적용한 Spring boot 웹 도서 쇼핑몰 서비스

## System Architecture
![image](https://github.com/nhnacademy-be5-no24/no24-frontend/assets/95386182/adaac288-a4e4-4a8e-99ad-0d3d1dc43442)

> 1. 클라이언트의 요청은 NginX를 통해서 들어오고, 로드밸런서에서 Round Robin 방식으로 2개의 Front Server에 순서대로 보낸다.
> 2. Front Server
     >   -	Front Server는 SSL을 적용하여 https 환경으로 구성하였으며, NginX 로그를 Filebeat가 수집하여 Logstash에 전송한다.
>   -	Client의 session id를 key로 사용하여 redis server를 session으로 사용한다. 이는 장바구니, 임시 주문정보, JWT token 저장에 사용된다.
>   -	Client의 검색 요청은 Search Engine (이하 ELK)으로 전달되어 요청에 대한 값으로 응답을 처리한다.
>   -	필요한 요청을 API Gateway를 통해 처리하고, Gateway는 해당 요청에 대해 처리되어야 하는 서비스 API로 요청을 보낸다.
> 3. Services
     >   -	Gateway, shop, Auth server는 모두 eureka server에 등록되어, Gateway에 도착한 요청을 적절히 라우팅 처리한다.
>   -	Shop Server는 두 개의 서버 (포트)로 구성되어 있으며, 이에 대한 요청은 Gateway에서 Round Robin 방식으로 로드밸런싱하여 요청한다.
>   -	Shop, Auth, Delivery, Batch, RabbitMQ는 MySQL (Test의 경우, H2 Database)과 통신하여 데이터 수집 및 저장, 변경, 삭제한다.
>   -	Shop Server에서 '쿠폰 지급' 기능에서는 요청 과부하를 Queue를 활용하여 막기 위해 Docker 환경 위의 RabbitMQ 서버에 전달하여 요청한다.
>   -	Auth Server에서는 JWT를 활용하여 인증 요청이 오는 경우, 토큰 인증, 토큰 재발급을 시행한다.
>   -	Shop Server에서 리뷰, 도서 저장 등 이미지 저장 요청이 오는 경우 NHN Cloud Object Storage에 저장한다.


## CI/CD
![image](https://github.com/nhnacademy-be5-no24/no24-frontend/assets/95386182/b46bf35f-ff4a-486b-b870-d52d47c4beae)

> Github을 통해 코드베이스를 관리하며 기본적으로 Github Action과 Jenkins를 통한 CI를 진행하여 빌드하고 테스트가 진행되어 성공 시 통합과정이 이뤄진다.
> - Shop Server와 Front Server는 Github Action을 통해 배포하며, 그 외 서버는 모두 Jenkins를 통해 배포한다.
> - Test 분석 로그 및 빌드 로그가 많은 경우, Jenkins를 통해 배포할 때 Cloud instance의 메모리 및 CPU 할당량을 초과하는 경우가 발생하여 Github action으로 배포 환경을 변경하였음.
> - 통합과정 이후, sonarQube를 통해 정적 코드 분석을 진행하며, 이에 대한 Bug, Test Coverage, Branch Coverage를 저장한다.
> - 통합과정 이후, 이상이 없는 경우 NHN Cloud의 instance에 각각 배포한다.


## Project Management

### WBS
- 구글 Sheet 및 github roadmap 활용하여 간트차트를 만들고, 해당 일자의 마일스톤을 정리 및 수행함.
  ![image](https://github.com/nhnacademy-be5-no24/no24-frontend/assets/95386182/37f36db2-c92d-49e6-bfd2-c728a922d842)

### Kanban Board
- GitHub project 칸반보드 활용
  ![image](https://github.com/nhnacademy-be5-no24/no24-frontend/assets/95386182/b5ed71bd-d1a7-4259-8c01-fc1f5fd02d48)

### Test Coverage (Target: Line coverage 80%)
- Line coverage: 84.8% (2024\. 05\. 12\. 기준)
  ![image](https://github.com/nhnacademy-be5-no24/no24-frontend/assets/95386182/8242fd8b-3945-4ab3-8710-8b3646f38642)


## 주요 기능

### 인프라
- 담당자: 강병구, 박병휘, 진지원
- Elastic Search
- Redis Session
  - Redis 환경 설정

### 검색
- 담당자: 강병구
- 텍스트 검색
    - Full-Text Search
    - 형태소 분석기 다양화

### 인증
- 담당자: 강병구, 박병휘
- 로그인
    - 기능 구현
    - Oauth 2.0 적용 (Payco)
- 로그아웃 - 기능구현 및 쿠키 등 삭제
- 인가 - 권한별 접근 제한 (AOP)

### 도서
 - 담당자: 박병휘, 이재원
 - 도서 조회, 등록, 수정, 삭제 기능 구현
   - 전체 조회, 카테고리 조회, 등록 (알라딘 API 사용), 책 상태 변경, 책 세부내용 수정 기능

### 회원
 - 담당자: 김병주, 박병휘, 진지원
 - 로그인, 로그아웃 기능 구현
 - 회원가입 기능 구현
    - 회원 주소 등록 (kakao 주소 API)

### 장바구니
 - 담당자: 박병휘, 진지원
 - 장바구니 조회, 등록, 수정, 삭제 기능 구현 (Redis)

### 주문
 - 담당자: 박동희, 박병휘, 진지원
 - 주문 등록, 수정, 삭제 기능 구현 (Redis)
 - 주문 조회 (DB)
 - 주문서 작성

### 주소
- 담당자: 진지원
- 주소 조회, 등록, 수정, 삭제 기능 구현
  - 수정 시, 회원 주소가 10개 이상인 경우, 추가 못하도록 설정
  - 주소 등록 (kakao 주소 API)
- 기본 주소 설정 기능 구현

### 결제
 - 담당자: 박동희, 박병휘
 - 결제 조회, 등록 기능 구현
 - Toss Payments 기능 구현

### 포인트
 - 담당자: 강병구, 박병휘
 - 포인트 정책 조회, 등록, 수정, 삭제 기능 구현
 - 포인트 적립 및 사용 기능 구현

### 포장지
- 담당자: 박동희, 박병휘
- 포장지 조회, 등록, 수정, 삭제 기능 구현

### 쿠폰
 - 담당자: 강병구, 박병휘
 - 쿠폰 조회, 등록 기능 구현
 - 쿠폰 정책 조회, 수정 기능 구현
 - 쿠폰 발급 (Spring Batch, RabbitMQ)

### 카테고리
- 담당자: 강병구
- 카테고리 조회 기능 구현

### 파일
 - 담당자: 박병휘
 - Object Storage, Local Storage 저장, 호출

### 등급
 - 담당자: 박병휘
 - 등급 조회 기능 구현
 - 등급 정책 조회, 수정 기능 구현

## Stack

![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)<br/> <img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&amp;logo=java&amp;logoColor=white"> <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&amp;logo=Spring&amp;logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&amp;logo=springboot&amp;logoColor=white"> <img src="https://img.shields.io/badge/springbatch-6DB33F?style=for-the-badge&amp;logo=Spring&amp;logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&amp;logo=docker&amp;logoColor=white"><br/>
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&amp;logo=mysql&amp;logoColor=white"> <img src="https://img.shields.io/badge/hibernate-59666C?style=for-the-badge&amp;logo=hibernate&amp;logoColor=white"> <img src="https://img.shields.io/badge/Jpa-FF0000?style=for-the-badge&amp;logo=Jpa&amp;logoColor=white"> <img src="https://img.shields.io/badge/Querydsl-0769AD?style=for-the-badge&amp;logo=Querydsl&amp;logoColor=white"> <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&amp;logo=redis&amp;logoColor=white"><br/>
<img src="https://img.shields.io/badge/rabbitMq-FF6600?style=for-the-badge&amp;logo=rabbitMq&amp;logoColor=white">
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)<img src="https://img.shields.io/badge/SonarLint-CB2029?style=for-the-badge&amp;logo=SonarLint&amp;logoColor=white"> <img src="https://img.shields.io/badge/SonarQube-4E9BCD?style=for-the-badge&amp;logo=SonarQube&amp;logoColor=white"><br/>
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white) ![Jenkins](https://img.shields.io/badge/jenkins-%232C5263.svg?style=for-the-badge&logo=jenkins&logoColor=white) ![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white) <img src="https://img.shields.io/badge/linux-FCC624?style=for-the-badge&amp;logo=linux&amp;logoColor=black"> ![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white) <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&amp;logo=github&amp;logoColor=white">
<br/>
<img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&amp;logo=elasticsearch&amp;logoColor=white">
<img src="https://img.shields.io/badge/logstash-005571?style=for-the-badge&amp;logo=logstash&amp;logoColor=white">
<img src="https://img.shields.io/badge/kibana-005571?style=for-the-badge&amp;logo=kibana&amp;logoColor=white">



## 팀원

<a href="https://github.com/nhnacademy-be5-no24/contributors/no24-frontend">
  <img src="https://contrib.rocks/image?repo=nhnacademy-be5-no24/no24-frontend"/>
</a>
