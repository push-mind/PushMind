# PushMind 백엔드 개발 계획 (v2)

> 명언 기반 동기부여 웹 서비스의 백엔드 개발 우선순위 및 작업 목록
> **피드백 반영:** API 경로 분리, 카카오 OAuth 도입, 알고리즘 개발 집중

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

## Phase 2: 사용자 API 구현 (`/api/v1`)

- [ ] **[F-01 & F-03] 명언 조회 API**
    - **Endpoint:** `GET /api/v1/quotes/random`
    - **Request:** (Header) `X-User-Token`: `String` (UUID)
    - **Response:** `QuoteResponse` (id, content, speaker)
    - **로직:**
        1. `userToken`을 기준으로 최근 조회한 5개 명언 ID를 `feedback` 테이블에서 조회
        2. 해당 명언들을 제외하고, `score` 기반 가중치 랜덤 알고리즘을 통해 1개 추천
        3. 조회 기록을 `feedback` 테이블에 저장 (liked=false)

- [ ] **[F-02] 피드백 처리 API**
    - **Endpoint:** `POST /api/v1/feedback`
    - **Request:** `FeedbackRequest` (quoteId, liked) + (Header) `X-User-Token`
    - **Response:** 성공/실패 메시지
    - **로직:**
        1. `userToken`과 `quoteId`로 기존 `feedback` 기록을 찾고, `liked` 상태 업데이트
        2. `liked`가 `true`로 변경되면 `quotes` 테이블의 `score` +1, `false`로 변경되면 -1
        3. 중복 피드백 방지 로직 (이미 동일한 상태로 평가된 경우 409 Conflict 반환)

- [ ] **[F-05] 인기 명언 순위 API**
    - **Endpoint:** `GET /api/v1/quotes/top`
    - **Request:** (Query) `limit`: `Int` (기본값 10)
    - **Response:** `List<QuoteResponse>`

- [ ] **DTO(Data Transfer Object) 생성**
    - `QuoteResponse.kt`, `FeedbackRequest.kt`

## Phase 3: 관리자 API 및 인증 구현 (`/api/admin`)

- [ ] **카카오 OAuth 2.0 설정**
    - `build.gradle.kts`에 `spring-boot-starter-oauth2-client` 의존성 추가
    - `application.yml`에 카카오 클라이언트 ID, Secret 등 설정 추가
    - Spring Security 설정 (`SecurityConfig.kt`) 추가

- [ ] **[F-04] 명언 관리 API**
    - **Endpoint:**
        - `POST /api/admin/quotes`
        - `PUT /api/admin/quotes/{id}`
        - `DELETE /api/admin/quotes/{id}`
    - **Request:** `QuoteAdminRequest` (content, speaker)
    - **보안:** `@AuthenticationPrincipal`을 통해 인증된 관리자(`Role.ADMIN`)만 접근 가능하도록 설정

- [ ] **로그인/로그아웃 및 사용자 정보 조회**
    - **Endpoint:**
        - `GET /login/oauth2/code/kakao` (Redirect URI)
        - `GET /api/admin/me` (인증된 사용자 정보 반환)
        - `POST /api/admin/logout`

## Phase 4: 알고리즘 고도화 및 테스트

- [ ] **명언 추천 알고리즘 구현 및 개선**
    - `score` 기반 가중치 랜덤 추출 알고리즘 구체화 (`score^1.5 * random()`)
    - 다양한 알고리즘(e.g., Epsilon-Greedy)을 테스트하고 비교할 수 있는 구조 설계
    - (선택) 점수 감소(decay) 로직 구현 (`@Scheduled` 활용)

- [ ] **단위/통합 테스트 작성**
    - MockMvc를 사용하여 API 엔드포인트별 테스트 코드 작성
    - `@WithMockUser`를 활용하여 인증/인가 테스트 케이스 작성

## Phase 5: 문서화 및 배포 준비

- [ ] **API 문서화**
    - SpringDoc (Swagger UI)을 이용한 API 명세 자동화
- [ ] **배포 준비**
    - `Dockerfile` 작성
    - EC2, Render 등 배포 환경에 맞는 `application-prod.yml` 설정
