# PushMind 백엔드 개발 계획 (v3)

> 명언 기반 동기부여 웹 서비스의 백엔드 개발 우선순위 및 작업 목록
> **피드백 반영:** API 경로 분리, 카카오 OAuth 도입, 알고리즘 개발 집중, TDD 적용

## 개발 원칙

- **테스트 주도 개발 (TDD):** 모든 API 엔드포인트는 실패하는 테스트 케이스를 먼저 작성하고, 이를 통과시키는 코드를 구현하는 방식으로 개발합니다.
- **API 경로 분리:** 사용자 API (`/api/v1`)와 관리자 API (`/api/admin`)를 명확히 구분합니다.
- **핵심 기능 집중:** 불필요한 테이블과 컬럼은 최소화하고, 명언 추천 알고리즘 고도화에 집중합니다.

## Phase 1: 프로젝트 기본 설정 및 모델링

- [ ] **DB 스키마 정의 및 Entity 클래스 생성**
    - `quotes` 테이블: `Quote.kt` (id, content, speaker, score, createdAt)
    - `feedback` 테이블: `Feedback.kt` (id, quote, userToken, liked, createdAt)
    - `users` 테이블: `User.kt` (id, kakaoId, nickname, role, createdAt) - **관리자 인증용**
    - JPA Auditing을 활용하여 `createdAt` 자동 생성 기능 추가

- [ ] **데이터베이스 연결 설정**
    - `application.yml`에 MySQL 및 H2 (local) 프로필 분리
    - Spring Boot의 `spring.profiles.active`를 사용하여 환경별 설정 로드

- [ ] **Spring Data JPA Repository 생성**
    - `QuoteRepository`, `FeedbackRepository`, `UserRepository` 인터페이스 생성

- [ ] **CORS(Cross-Origin Resource Sharing) 설정**
    - React 클라이언트(Vercel) 요청을 허용하도록 `WebMvcConfigurer` 설정

## Phase 2: 사용자 API 구현 (`/api/v1`) - TDD 적용

- [ ] **[F-01 & F-03] 명언 조회 API**
    - **Endpoint:** `GET /api/v1/quotes/random`
    - **Request:** (Header) `X-User-Token`: `String` (UUID)
    - **Response:** `QuoteResponse` (id, content, speaker)
    - **TDD 사이클:**
        1. `random` API 호출 시 200 OK와 `QuoteResponse`를 반환하는 테스트 작성
        2. `userToken` 기반으로 최근 본 명언을 제외하는지 검증하는 테스트 작성
        3. 알고리즘 적용 후, 스코어 높은 명언이 더 자주 노출되는지 확률적으로 검증하는 테스트 추가

- [ ] **[F-02] 피드백 처리 API**
    - **Endpoint:** `POST /api/v1/feedback`
    - **Request:** `FeedbackRequest` (quoteId, liked) + (Header) `X-User-Token`
    - **Response:** 성공/실패 메시지
    - **TDD 사이클:**
        1. 첫 '좋아요' 요청 시 201 Created와 함께 `score`가 1 증가하는지 테스트
        2. 동일 사용자가 다시 '좋아요' 요청 시 409 Conflict를 반환하는 테스트
        3. '좋아요' 취소 시 `score`가 1 감소하는지 테스트

- [ ] **[F-05] 인기 명언 순위 API**
    - **Endpoint:** `GET /api/v1/quotes/top`
    - **Request:** (Query) `limit`: `Int` (기본값 10)
    - **Response:** `List<QuoteResponse>`
    - **TDD 사이클:**
        1. `score` 순으로 명언이 올바르게 정렬되어 반환되는지 테스트
        2. `limit` 파라미터가 정확히 적용되는지 테스트

## Phase 3: 관리자 API 및 인증 구현 (`/api/admin`) - TDD 적용

- [ ] **카카오 OAuth 2.0 설정**
    - `build.gradle.kts`에 `spring-boot-starter-oauth2-client` 의존성 추가
    - `application.yml`에 카카오 클라이언트 ID, Secret 등 설정 추가
    - Spring Security 설정 (`SecurityConfig.kt`) 추가

- [ ] **[F-04] 명언 관리 API**
    - **Endpoint:**
        - `POST /api/admin/quotes`
        - `PUT /api/admin/quotes/{id}`
        - `DELETE /api/admin/quotes/{id}`
    - **보안:** `@AuthenticationPrincipal`을 통해 인증된 관리자(`Role.ADMIN`)만 접근 가능하도록 설정
    - **TDD 사이클:**
        1. 인증 없이 API 호출 시 401 Unauthorized 반환 테스트
        2. 일반 사용자 권한으로 호출 시 403 Forbidden 반환 테스트
        3. 관리자 권한으로 명언 등록/수정/삭제가 성공하는지 테스트

- [ ] **로그인/로그아웃 및 사용자 정보 조회**
    - **Endpoint:**
        - `GET /login/oauth2/code/kakao` (Redirect URI)
        - `GET /api/admin/me` (인증된 사용자 정보 반환)

## Phase 4: 알고리즘 고도화 및 테스트

- [ ] **명언 추천 알고리즘 구현 및 개선**
    - `score` 기반 가중치 랜덤 추출 알고리즘 구체화 (`score^1.5 * random()`)
    - 다양한 알고리즘(e.g., Epsilon-Greedy)을 테스트하고 비교할 수 있는 구조 설계
    - (선택) 점수 감소(decay) 로직 구현 (`@Scheduled` 활용)

- [ ] **통합 테스트 강화**
    - MockMvc를 사용하여 API 엔드포인트별 테스트 코드 작성
    - `@WithMockUser`를 활용하여 인증/인가 테스트 케이스 작성

## Phase 5: 문서화 및 배포 준비

- [ ] **API 문서화**
    - SpringDoc (Swagger UI)을 이용한 API 명세 자동화
- [ ] **배포 준비**
    - `Dockerfile` 작성
    - EC2, Render 등 배포 환경에 맞는 `application-prod.yml` 설정
