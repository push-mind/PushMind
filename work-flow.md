# PushMind TDD 개발 워크플로우

> `PLAN.md`를 기반으로, 테스트 주도 개발(TDD) 사이클을 적용한 구체적인 개발 작업 흐름입니다.

## Phase 1: 프로젝트 기반 설정 및 모델링

### Task 1.1: 데이터베이스 및 JPA 설정
1.  **Entity 클래스 작성:**
    - `User.kt`, `Quote.kt`, `Feedback.kt` 파일을 생성합니다.
    - 각 클래스에 `PLAN.md`에 명시된 필드(id, content 등)와 JPA 어노테이션(`@Entity`, `@Id`, `@GeneratedValue` 등)을 추가합니다.
    - `BaseTimeEntity.kt` 클래스를 생성하고 `@MappedSuperclass`, `@EntityListeners(AuditingEntityListener::class)`를 설정하여 `createdAt` 필드를 관리합니다.
    - 각 Entity가 `BaseTimeEntity`를 상속하도록 수정합니다.
2.  **Repository 인터페이스 생성:**
    - `UserRepository`, `QuoteRepository`, `FeedbackRepository` 인터페이스를 생성하고 `JpaRepository`를 상속받도록 합니다.
3.  **`application.yml` 설정:**
    - `spring.profiles.active`를 `local`로 설정합니다.
    - `local` 프로필에는 H2 인메모리 DB 설정을 추가합니다.
    - `prod` 프로필에는 MySQL DB 연결 정보를 추가합니다.
    - `spring.jpa.hibernate.ddl-auto` 옵션을 `create` 또는 `validate`로 설정하여 개발 효율을 높입니다.

### Task 1.2: 기본 환경 설정
1.  **CORS 전역 설정:**
    - `WebConfig.kt` 파일을 생성하고 `WebMvcConfigurer`를 구현합니다.
    - `addCorsMappings` 메소드를 오버라이드하여 프론트엔드 개발 서버(`http://localhost:3000`)와 Vercel 배포 도메인からの 요청을 허용하도록 설정합니다.
2.  **빌드 및 실행 테스트:**
    - `./gradlew build` 명령어를 실행하여 프로젝트가 오류 없이 빌드되는지 확인합니다.
    - 애플리케이션을 실행하고 H2 콘솔에 접속하여 테이블이 정상적으로 생성되었는지 검증합니다.

---

## Phase 2: 사용자 API 구현 (TDD)

> **TDD 사이클:** `실패하는 테스트 작성` -> `테스트를 통과하는 최소한의 코드 작성` -> `리팩토링`

### Task 2.1: [F-01, F-03] 명언 조회 API (`GET /api/v1/quotes/random`)
1.  **Test:** `QuoteApiTest.kt`에 `@SpringBootTest`와 `@AutoConfigureMockMvc`를 설정합니다.
2.  **Test:** `X-User-Identifier` 헤더와 함께 API를 호출했을 때, 200 OK와 `QuoteResponse` DTO 형식의 JSON이 반환되는지 검증하는 **실패하는 테스트**를 작성합니다.
3.  **Code:** `QuoteController`와 `QuoteService`를 생성하고, 임의의 명언을 반환하는 로직을 **최소한으로 구현**하여 테스트를 통과시킵니다.
4.  **Test:** 특정 사용자가 이전에 봤던 명언이 다시 추천되지 않는지 검증하는 **실패하는 테스트**를 추가합니다. (e.g., 피드백을 남긴 명언은 제외)
5.  **Code:** `FeedbackRepository`를 사용하여 사용자가 피드백을 남긴 명언 ID 목록을 조회하고, 이를 제외한 명언을 추천하도록 로직을 수정합니다.
6.  **Refactor:** 코드의 중복을 제거하고, 가독성을 높이는 등 클린 코드를 적용합니다.

### Task 2.2: [F-02] 피드백 처리 API (`POST /api/v1/feedback`)
1.  **Test:** `FeedbackApiTest.kt`를 생성합니다.
2.  **Test:** '좋아요'(`liked: true`) 요청 시, 201 Created를 반환하고 해당 명언의 `score`가 1 증가하는지 검증하는 **실패하는 테스트**를 작성합니다.
3.  **Code:** `FeedbackService`에서 `Feedback`을 저장하고 `Quote`의 `score`를 업데이트하는 로직을 구현하여 테스트를 통과시킵니다.
4.  **Test:** 동일한 사용자가 동일한 명언에 다시 '좋아요'를 요청하면 409 Conflict를 반환하는 **실패하는 테스트**를 작성합니다.
5.  **Code:** `FeedbackRepository`에서 `userIdentifier`와 `quoteId`로 기존 피드백이 있는지 확인하는 로직을 추가합니다.
6.  **Refactor:** `FeedbackService`의 로직을 명확하고 간결하게 개선합니다.

### Task 2.3: [F-05] 인기 명언 순위 API (`GET /api/v1/quotes/top`)
1.  **Test:** `QuoteApiTest.kt`에 `score`가 높은 순으로 명언 목록이 정렬되어 반환되는지 검증하는 **실패하는 테스트**를 작성합니다.
2.  **Code:** `QuoteRepository`에 `findTopNByOrderByScoreDesc`와 같은 Query Method를 추가하고, `QuoteService`에서 이를 호출하도록 구현합니다.
3.  **Test:** `limit` 쿼리 파라미터 값에 따라 반환되는 명언의 개수가 제어되는지 검증하는 테스트를 추가합니다.
4.  **Code:** Controller 메소드에 `@RequestParam`을 추가하고, 서비스 로직에 반영하여 테스트를 통과시킵니다.
5.  **Refactor:** 관련 코드를 정리합니다.

---

## Phase 3: 관리자 API 및 인증 구현 (TDD)

### Task 3.1: 카카오 OAuth 2.0 및 Spring Security 설정
1.  **Dependency 추가:** `build.gradle.kts`에 `spring-boot-starter-oauth2-client` 의존성을 추가합니다.
2.  **`application.yml` 설정:** 카카오에서 발급받은 `client-id`, `client-secret` 등을 `prod` 프로필에 추가합니다.
3.  **`SecurityConfig.kt` 작성:**
    - `@EnableWebSecurity` 어노테이션을 추가합니다.
    - `SecurityFilterChain` Bean을 등록하여 `/api/admin/**` 경로는 인증된 사용자만 접근 가능하도록 설정합니다.
    - 그 외 경로들(`/`, `/api/v1/**` 등)은 모두 허용(`permitAll`)합니다.
    - `oauth2Login()` 설정을 추가합니다.
4.  **Custom OAuth2 User Service 작성:**
    - `DefaultOAuth2UserService`를 상속받아 `CustomOAuth2UserService`를 구현합니다.
    - 카카오로부터 받은 사용자 정보(kakaoId, nickname)를 기반으로 `UserRepository`에서 사용자를 조회하거나 새로 생성하여 저장하는 로직을 구현합니다.
    - 관리자(Admin) 계정은 DB에 미리 `role='ADMIN'`으로 저장해두고, 로그인 시 해당 권한을 부여하도록 합니다.

### Task 3.2: [F-04] 명언 관리 API (TDD)
1.  **Test:** `AdminQuoteApiTest.kt`를 생성하고, `@WithMockUser`를 사용하여 인증 상태를 모의 실험합니다.
2.  **Test:** 인증 없이 `/api/admin/quotes` 호출 시 401 Unauthorized가 반환되는지 테스트합니다.
3.  **Test:** `USER` 권한으로 API 호출 시 403 Forbidden이 반환되는지 테스트합니다. (`@WithMockUser(roles = "USER")`)
4.  **Test:** `ADMIN` 권한으로 명언 등록(`POST`), 수정(`PUT`), 삭제(`DELETE`) API 호출이 성공하는지 **실패하는 테스트**를 각각 작성합니다. (`@WithMockUser(roles = "ADMIN")`)
5.  **Code:** `AdminQuoteController`를 생성하고, `@AuthenticationPrincipal`을 통해 로그인된 사용자 정보를 받아 권한을 확인하는 로직을 추가합니다. 각 CRUD 기능을 구현하여 모든 테스트를 통과시킵니다.
6.  **Refactor:** Controller와 Service의 역할을 명확히 분리하고 코드를 개선합니다.

---

## Phase 4: 알고리즘 고도화 및 통합 테스트

1.  **알고리즘 구현:** `QuoteService`의 명언 추천 로직에 `score` 기반 가중치 랜덤 추출 알고리즘을 적용합니다.
2.  **확률 테스트:** 추천 API를 여러 번 호출했을 때, `score`가 높은 명언이 더 높은 빈도로 등장하는지 검증하는 **통계적/확률적 테스트**를 작성합니다.
3.  **통합 테스트 강화:** `MockMvc`를 사용하여 주요 API들의 시나리오 테스트를 작성합니다. (e.g., 명언 조회 -> 좋아요 -> 인기 순위 확인)

---

## Phase 5: 문서화 및 배포 준비

1.  **API 문서화:**
    - `springdoc-openapi-starter-webmvc-ui` 의존성을 추가합니다.
    - 각 Controller와 DTO에 `@Operation`, `@Parameter`, `@Schema` 등의 어노테이션을 추가하여 API 명세를 상세히 기술합니다.
    - 애플리케이션 실행 후 `/swagger-ui.html`에 접속하여 문서가 잘 생성되었는지 확인합니다.
2.  **배포 준비:**
    - 프로젝트 루트에 `Dockerfile`을 작성합니다.
    - `application-prod.yml`에 실제 운영 환경에 맞는 설정을 최종 확인합니다.
    - Docker 이미지를 빌드하고 로컬에서 컨테이너를 실행하여 최종 검증을 수행합니다.
