import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 커스텀 어노테이션 기반 HTTP 웹 서버
 *
 * [동작 원리]
 *
 *  1. registerController(@Controller 클래스)
 *     └─ 리플렉션으로 메서드를 스캔
 *        ├─ @GetMapping("/path")  → GET  /path 라우트 등록
 *        └─ @PostMapping("/path") → POST /path 라우트 등록
 *
 *  2. 클라이언트 요청 수신
 *     └─ HTTP 요청 파싱: 메서드, 경로, Body 추출
 *
 *  3. 라우팅
 *     └─ "GET /users" 형태의 키로 Map에서 메서드 검색
 *        ├─ 발견 → 리플렉션으로 메서드 호출 → String 결과 반환
 *        └─ 미발견 → 404 응답
 *
 *  4. HTTP 응답 전송
 *     └─ 상태라인 + 헤더 + 빈줄 + JSON Body
 */
public class HttpWebServer {

    private final int port;

    // 라우팅 테이블: "GET /users" → {컨트롤러 인스턴스, 메서드}
    private final Map<String, RouteEntry> routes = new HashMap<>();

    // 라우트 정보를 담는 레코드 (인스턴스 + 메서드 쌍)
    record RouteEntry(Object instance, Method method) {}

    public HttpWebServer(int port) {
        this.port = port;
    }

    // ── @Controller 클래스를 받아서 라우팅 테이블에 등록 ────────────────────
    public void registerController(Object controller) {
        Class<?> clazz = controller.getClass();

        //--- 여기에 @GetMapping과 @PostMapping 어노테이션을 스캔하고 라우팅 테이블에 등록하는 코드를 작성하세요. ---


    }

    // ── 서버 시작: 클라이언트 연결 대기 ─────────────────────────────────────
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("\nHttpWebServer started on port " + port);
            System.out.println("등록된 라우트 수: " + routes.size());
            System.out.println("=".repeat(40));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);  // 단순화: 동기 처리
            }
        }
    }

    // ── HTTP 요청 1건 처리 ───────────────────────────────────────────────────
    private void handleRequest(Socket clientSocket) {
        //--- 여기에 클라이언트 요청을 읽고, 라우팅 테이블에서 메서드를 찾아 호출하고, HTTP 응답을 작성하는 코드를 작성하세요. ---

    }

    // ── 진입점: 컨트롤러 등록 후 서버 시작 ─────────────────────────────────
    public static void main(String[] args) throws IOException {
        HttpWebServer server = new HttpWebServer(8080);

        // Spring의 @ComponentScan 역할: 컨트롤러를 수동으로 등록
        server.registerController(new UserController());

        server.start();
    }
}
