/**
 * 사용자 관련 HTTP 요청을 처리하는 컨트롤러
 *
 * @Controller 어노테이션 → HttpWebServer가 이 클래스를 라우터에 등록
 * @GetMapping  어노테이션 → GET  요청 경로와 메서드 연결
 * @PostMapping 어노테이션 → POST 요청 경로와 메서드 연결
 *
 * [규칙]
 * - 모든 메서드의 반환 타입은 String (JSON 문자열)
 * - POST 메서드는 첫 번째 파라미터로 String body를 받음
 * - GET 메서드는 파라미터 없음
 */
@Controller
public class UserController {

    // GET / → 서버 상태 응답
    @GetMapping("/")
    public String home() {
        return "{\"message\":\"HttpWebServer 에 오신 것을 환영합니다\",\"status\":\"running\"}";
    }

    // GET /users → 사용자 목록 응답
    @GetMapping("/users")
    public String getUsers() {
        return "{\"users\":[" +
               "{\"id\":1,\"name\":\"Alice\",\"role\":\"admin\"}," +
               "{\"id\":2,\"name\":\"Bob\",\"role\":\"user\"}," +
               "{\"id\":3,\"name\":\"Charlie\",\"role\":\"user\"}" +
               "]}";
    }

    // GET /users/1 → 단일 사용자 응답 (경로 예시)
    @GetMapping("/users/1")
    public String getUser() {
        return "{\"id\":1,\"name\":\"Alice\",\"role\":\"admin\",\"email\":\"alice@example.com\"}";
    }

    // POST /users → 사용자 생성, body는 JSON 문자열로 전달
    @PostMapping("/users")
    public String createUser(String body) {
        // body 예시: {"name":"Dave","role":"user"}
        return "{\"result\":\"created\",\"data\":" + body + "}";
    }

    // POST /users/login → 로그인 처리
    @PostMapping("/users/login")
    public String login(String body) {
        // body 예시: {"username":"Alice","password":"1234"}
        return "{\"result\":\"login success\",\"token\":\"abc123xyz\",\"request\":" + body + "}";
    }
}
