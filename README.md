# 📑 Today's Work (Back-end)
이 프로젝트는 **Java Servlet과 Oracle DB**를 기반으로 구축된 **오늘의 할 일을 체크**하는 할 일 관리 API 서버입니다. **RESTful** 설계 원칙을 준수하며, **리액트** 기반 프론트엔드와 **비동기 통신**을 통해 **실시간 데이터 처리**를 수행합니다.

## 📖 Background
**Today's Work** 프로젝트는 컴퓨터를 사용하다 보면 메모장이나 스티커 메모, 혹은 오프라인 메모지, 수첩 등에 정리하던 오늘의 할 일을 조금 더 편하게 작업하기 위해 만든 **개인 프로젝트**입니다. 스트레스가 많이 쌓일 수 밖에 없는 현대에서 스트레스를 성취감을 통해 작게나마 완화하기 위해 작업하였습니다. **하루 일과 시작 전**, 오늘의 할 일 리스트를 작성하고, 하나씩 체크해가며 지워가면서 작은 성취감을 쌓아 자존감을 올려 현대사회에 이바지 되었으면 하는 마음입니다. 

## 🔗 관련 링크
**Front-end Repository:** (https://github.com/Somallcool/todo-frontend.git)

## 🛠 핵심 기술 스택
- **Language & Framework:** Java 11, Java Servlet (Tomcat 9.0)

- **Database:** Oracle Database (Express Edition)

- **Connection Pool:** HikariCP (데이터베이스 연결 최적화)

- **Library:** Lombok, Jackson (JSON 데이터 바인딩), JSR310 (LocalDate 처리)

## 🏗 프로젝트 아키텍처 및 구현 포인트
#### 1. 효율적인 데이터 액세스 계층 (DAO)
- **ConnectionUtil:** HikariCP를 적용하여 데이터베이스 커넥션을 효율적으로 관리합니다.

- **동적 SQL:** sortType(최신순, 마감순, 우선순위순)과 keyword(검색어) 파라미터에 따라 PreparedStatement를 동적으로 생성하여 처리합니다.

- **Batch Delete:** SQL의 IN 연산자를 활용하여 선택된 여러 항목을 한 번에 삭제하는 기능을 구현했습니다.

#### 2. 비즈니스 로직 및 유효성 검사 (Service)
- **Singleton Pattern:** Enum을 사용하여 TodoService를 싱글톤으로 구현, 객체 생성 비용을 줄이고 일관된 상태 관리를 보장합니다.

- **Server-side Validation:** 등록 시 제목 누락 여부, 100자 이내 제한, 과거 날짜 마감 설정 방지 등 데이터 무결성을 위한 검증 로직을 포함합니다.

#### 3. 보안 및 인프라 설정 (Filter)
- **CORSFilter:** 리액트 개발 서버(Port 5173)와의 원활한 통신을 위해 CORS 헤더를 직접 설정하였습니다.

- **Encoding:** 모든 요청과 응답에 UTF-8 인코딩을 강제하여 한글 데이터 깨짐 문제를 해결했습니다.

## 💻 주요 API 명세
- **GET /api/todos:** 목록 조회 (정렬 및 검색 지원)

- **POST /api/todos:** 신규 등록

- **POST /api/todos?mode=modify:** 상세 내용 수정

- **POST /api/todos?mode=delete&tno=1:** 단일 항목 삭제

- **POST /api/todos?mode=updateFinished:** 완료 상태 토글

## 💡 학습 및 성장 포인트
- **CORS 이슈 해결:** 라이브러리 도움 없이 필터를 통해 직접 Access-Control-Allow-Origin 헤더를 조작하며 웹 보안 정책을 이해했습니다.

- **RESTful 고민:** 단순 페이지 이동이 아닌, JSON 데이터를 주고받는 API 서버로서의 Servlet 역할을 깊이 있게 학습했습니다.

- **데이터베이스 설계:** 오라클 시퀀스와 테이블 설계를 통해 효율적인 CRUD 연동을 경험했습니다.
