# 직접 구현하는 Spring OAuth2 프로젝트

## 개발 의도

- 회사에서 맡은 마이크로서비스 프로젝트 중 SSO 기능의 통합인증 서비스를 개선해야 함
- 회사의 기존 프로젝트는 Spring Security + Spring OAuth2 Client + JWT 조합
- 회사의 프로젝트는 마이크로서비스로 나눠진 백엔드 프로젝트인데 위의 라이브러리 조합을 사용하니 비효율적인 면이 많음
- OAuth2 라이브러리만 분리하고, 해당 부분을 직접 구현해보면 좋을 것 같음 
- 이 부분을 검증해보기 위한 간단한 프로젝트

## 요구사항

- 인증(회원가입 & 로그인)
  - ID/PW
  - OAuth2(Google, Naver, Facebook, ...)
  - 동일한 이메일을 기준으로 다른 방식으로 회원가입 || 로그인할 수 없다.
  - 로그인했을 때 토큰(Access Token, Refresh Token)을 발급한다. 
    - Access Token: 인증이 필요한 API 접근 시 필요
    - Refresh Token: Access Token을 재발급한다.
  - 로그아웃할 수 있다.
- 회원
  - 주요 속성: 이메일, 비밀번호, 이름
  - 탈퇴한 회원을 다시 복구할 수 있다.
  - 내 정보를 확인할 수 있다.

## 개발 조건
- Spring Security 사용
- 토큰 인증 방식
  - Access Token: JWT
  - Refresh Token: DB 저장할 거라 아무거나
- spring-boot-starter-oauth2-client 사용하지 않음.
  - 여기서 구현해볼 흐름
    ![OAuth2-TO-BE drawio](https://user-images.githubusercontent.com/62074748/233544640-8a4ff6f1-9bc3-46fb-a2ef-bf8b9f974a57.png)
- DDD를 따라 최대한 구현해볼 것.
- 테스트용 DB와 로컬 개발 환경용 DB를 분리 
- Docker Compose를 사용해서 로컬 개발 환경 구축

## 개발 환경 구축 가이드(TODO)