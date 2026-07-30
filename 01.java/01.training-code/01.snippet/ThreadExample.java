public class ThreadExample {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("1초 후에 실행되는 작업");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t.start();
        System.out.println("메인 스레드 종료");
    }
}

 