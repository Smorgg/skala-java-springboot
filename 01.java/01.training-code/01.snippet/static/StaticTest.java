public class StaticTest {
    static class Example {
        public static final int INITIAL_COUNT = 10;    //Metaspace 의 클래스 메타데이터에 상수 풀에 존재
        private static int count = 0; // Metaspace 클래스 메타데이터에는 Heap 내 java.lang.Class의 Index 정보
        private int instanceId;

        // ------ 여기에 로직을 작성하세요. ----
    }

    public static void main(String[] args) {
        // ----- 여기에 로직을 작성하세요. ----
    }
    
}
