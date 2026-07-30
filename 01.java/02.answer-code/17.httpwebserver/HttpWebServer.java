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

        // @Controller 어노테이션이 없으면 등록 거부
        if (!clazz.isAnnotationPresent(Controller.class)) {
            throw new IllegalArgumentException("@Controller 어노테이션이 없습니다: " + clazz.getName());
        }

        System.out.println("[컨트롤러 등록] " + clazz.getSimpleName());

        // 클래스의 모든 메서드를 순회하며 어노테이션 확인
        for (Method method : clazz.getDeclaredMethods()) {

            if (method.isAnnotationPresent(GetMapping.class)) {
                String path = method.getAnnotation(GetMapping.class).value();
                routes.put("GET " + path, new RouteEntry(controller, method));
                System.out.println("  GET  " + path + " → " + method.getName() + "()");
            }

            if (method.isAnnotationPresent(PostMapping.class)) {
                String path = method.getAnnotation(PostMapping.class).value();
                routes.put("POST " + path, new RouteEntry(controller, method));
                System.out.println("  POST " + path + " → " + method.getName() + "(String body)");
            }
        }
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
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
            );
            PrintWriter writer = new PrintWriter(
                clientSocket.getOutputStream(), true
            )
        ) {
            // ── Step 1: 요청 라인 읽기 ───────────────────────────────────────
            // "GET /users HTTP/1.1"
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;
            System.out.println("\n[요청] " + requestLine);

            // ── Step 2: 헤더 읽기 (빈 줄까지) + Content-Length 추출 ──────────
            int contentLength = 0;
            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                if (headerLine.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                }
            }

            // ── Step 3: POST Body 읽기 ───────────────────────────────────────
            String body = "";
            if (contentLength > 0) {
                char[] buf = new char[contentLength];
                reader.read(buf, 0, contentLength);
                body = new String(buf);
                System.out.println("[바디] " + body);
            }

            // ── Step 4: 메서드와 경로 파싱 ──────────────────────────────────
            // "GET /users HTTP/1.1" → httpMethod="GET", path="/users"
            String[] parts   = requestLine.split(" ");
            String httpMethod = parts[0];  // GET 또는 POST
            String path       = parts[1];  // /users

            // ── Step 5: 라우팅 - Map에서 메서드 검색 ───────────────────────
            String routeKey  = httpMethod + " " + path;  // "GET /users"
            RouteEntry entry = routes.get(routeKey);

            if (entry == null) {
                // 등록된 라우트 없음 → 404
                sendResponse(writer, 404, "Not Found",
                    "{\"error\":\"없는 경로입니다\",\"path\":\"" + path + "\"}");
                return;
            }

            // ── Step 6: 리플렉션으로 @Controller 메서드 호출 ────────────────
            //   GET 메서드: 파라미터 없음 → invoke(instance)
            //   POST 메서드: body를 String으로 전달 → invoke(instance, body)
            try {
                Object result;
                if ("POST".equals(httpMethod) && entry.method().getParameterCount() > 0) {
                    result = entry.method().invoke(entry.instance(), body);
                } else {
                    result = entry.method().invoke(entry.instance());
                }

                String responseBody = result != null ? result.toString() : "{}";
                System.out.println("[응답] " + responseBody);
                sendResponse(writer, 200, "OK", responseBody);

            } catch (Exception e) {
                sendResponse(writer, 500, "Internal Server Error",
                    "{\"error\":\"" + e.getCause().getMessage() + "\"}");
            }

        } catch (IOException e) {
            System.err.println("[오류] " + e.getMessage());
        }
    }

    // ── HTTP 응답 전송: 상태라인 + 헤더 + 빈줄 + Body ────────────────────────
    private void sendResponse(PrintWriter writer, int statusCode,
                              String statusMessage, String body) {
        writer.print("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n");
        writer.print("Content-Type: application/json; charset=UTF-8\r\n");
        writer.print("Content-Length: " + body.getBytes().length + "\r\n");
        writer.print("Connection: close\r\n");
        writer.print("\r\n");   // 헤더 끝 빈 줄 (필수)
        writer.print(body);
        writer.flush();
    }

    // ── 진입점: 컨트롤러 등록 후 서버 시작 ─────────────────────────────────
    public static void main(String[] args) throws IOException {
        HttpWebServer server = new HttpWebServer(8080);

        // Spring의 @ComponentScan 역할: 컨트롤러를 수동으로 등록
        server.registerController(new UserController());

        server.start();
    }
}
