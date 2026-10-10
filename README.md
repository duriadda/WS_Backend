# Spring Boot 실습 과제 — 회원·게시글 REST API

회원(User)과 게시글(Post)을 관리하는 간단한 REST API 서버입니다.
데이터는 DB 없이 메모리에 저장하므로 서버를 재시작하면 초기화됩니다.

## 개발 환경

| 항목 | 버전 |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.7 |
| Gradle | 8.14.3 |
| 라이브러리 | Spring Web, Lombok |

## 실행 방법

```bash
./gradlew bootRun
```

서버는 `http://localhost:8080`에서 실행됩니다.

점검 모드(503 응답)로 실행하려면 다음 명령을 사용합니다.

```bash
./gradlew bootRun --args='--app.maintenance=true'
```

## 과제 요구사항 대응

| 요구사항 | 구현 내용 |
|---|---|
| A. HTTP 메소드별 API 2개씩 | User 4개 + Post 4개, 총 8개 |
| B. 미들웨어 | `LoggingInterceptor`(요청 로그), `MaintenanceFilter`(점검 모드) |
| C. 응답 코드 다양성 | 2xx: 200, 201 / 4xx: 400, 403, 404, 409 / 5xx: 500, 503 |
| D. 응답 포맷 통일 | 모든 응답을 `ApiResponse`로 감싸 `status`·`data`·`message` 형식으로 반환 |

## API 목록

| # | 메소드 | 경로 | 설명 | 성공 | 주요 실패 |
|---|---|---|---|---|---|
| 1 | POST | `/api/users` | 회원 생성 | 201 | 400 입력 오류, 409 이메일 중복 |
| 2 | POST | `/api/posts` | 게시글 작성 | 201 | 400 입력 오류, 404 작성자 없음 |
| 3 | GET | `/api/users/{id}` | 회원 조회 | 200 | 404 회원 없음 |
| 4 | GET | `/api/posts?page=0&size=10&authorId=1` | 게시글 목록 조회 | 200 | 400 잘못된 페이지 값 |
| 5 | PUT | `/api/users/{id}` | 회원 정보 수정 | 200 | 400, 404, 409 |
| 6 | PUT | `/api/posts/{id}` | 게시글 수정 | 200 | 400, 403 작성자 아님, 404 |
| 7 | DELETE | `/api/users/{id}` | 회원 삭제 (작성한 글도 삭제) | 200 | 404 |
| 8 | DELETE | `/api/posts/{id}` | 게시글 삭제 | 200 | 403, 404 |

- 게시글 수정·삭제는 `X-User-Id` 헤더로 요청자를 식별하며, 작성자 본인만 할 수 있습니다.
- 삭제 성공은 표준 응답 포맷을 유지하기 위해 204 대신 200과 메시지를 반환합니다.
- `GET /api/test/error`는 500 응답을 시연하기 위한 보조 엔드포인트입니다. (8개 API와 별도)

## 응답 형식

성공

```json
{
  "status": "success",
  "data": { "id": 1, "name": "홍길동", "email": "hong@test.com" }
}
```

실패

```json
{
  "status": "error",
  "message": "id=99 사용자를 찾을 수 없습니다."
}
```

## 응답 코드가 발생하는 상황

| 코드 | 상황 | 확인 방법 |
|---|---|---|
| 200 OK | 조회·수정·삭제 성공 | `GET /api/users/1` |
| 201 Created | 회원·게시글 생성 성공 | `POST /api/users` |
| 400 Bad Request | 필수값 누락, 형식 오류, 잘못된 JSON, 헤더 누락 | `POST /api/users` 에 `{"name":""}` |
| 403 Forbidden | 다른 사람의 게시글 수정·삭제 | `PUT /api/posts/1` 에 `X-User-Id: 2` |
| 404 Not Found | 없는 회원·게시글·경로 | `GET /api/users/99` |
| 409 Conflict | 이미 사용 중인 이메일 | 같은 이메일로 `POST /api/users` 두 번 |
| 500 Internal Server Error | 예상하지 못한 서버 오류 | `GET /api/test/error` |
| 503 Service Unavailable | 점검 모드로 실행 중 | 점검 모드 실행 후 아무 `/api` 요청 |

## 미들웨어

**LoggingInterceptor** — `/api/**` 요청이 들어올 때와 끝날 때 로그를 출력합니다.

```
[REQ] POST /api/users
[RES] POST /api/users -> 201 (22ms)
```

**MaintenanceFilter** — `app.maintenance=true`이면 컨트롤러로 넘기지 않고 바로 503을 응답합니다.

```
[MAINTENANCE] GET /api/users/1 -> 503
```

## 요청 예시 (curl)

```bash
# 회원 생성
curl -X POST localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"홍길동","email":"hong@test.com"}'

# 게시글 작성
curl -X POST localhost:8080/api/posts \
  -H 'Content-Type: application/json' \
  -d '{"authorId":1,"title":"첫 글","content":"내용"}'

# 게시글 목록 조회
curl 'localhost:8080/api/posts?page=0&size=5'

# 게시글 수정 (작성자 본인)
curl -X PUT localhost:8080/api/posts/1 \
  -H 'Content-Type: application/json' -H 'X-User-Id: 1' \
  -d '{"title":"수정한 제목","content":"수정한 내용"}'

# 게시글 삭제
curl -X DELETE localhost:8080/api/posts/1 -H 'X-User-Id: 1'
```

## 프로젝트 구조

```
src/main/java/com/example/assignment
├── controller   UserController, PostController, TestController
├── service      UserService, PostService (비즈니스 로직·검증)
├── repository   UserRepository, PostRepository (메모리 저장소)
├── domain       User, Post
├── dto          요청 DTO, PageResponse
├── common       ApiResponse, ApiException, GlobalExceptionHandler
└── middleware   LoggingInterceptor, MaintenanceFilter, WebConfig
```

## AI 사용
이번 과제는 딱히 어디까지 사용해도 된다 안된다는 공지가 없어서 일단 사용 후 적어두겠습니다.
전체적인 구조를 짜고 안쪽 세부적인 구현 코드는 AI를 사용했습니다.
REAMDME의 경우도 전체적인 틀을 제가 짜고 나머지 내용 수정과 채우기는 AI를 사용했습니다.
중간에 버전이 안 맞는 경우가 있어 오류 수정을 위해 AI를 사용했습니다.
스크린샷 중에서, POSTMAN 이외에 웹 화면을 띄우는 과정을 AI의 도움을 받았습니다.