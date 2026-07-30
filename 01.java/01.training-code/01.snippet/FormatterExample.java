import java.text.DecimalFormat;
import java.util.Scanner;

public class FormatterExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Formatter 선택 =====");
            System.out.println("1) string-format");
            System.out.println("2) message-format");
            System.out.println("3) decimal-format");
            System.out.println("4) exit");
            System.out.print("선택: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    stringFormat();
                    break;
                case "2":
                    messageFormat();
                    break;
                case "3":
                    decimalFormat();
                    break;
                case "4":
                    System.out.println("종료합니다.");
                    scanner.close();
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 1~4 중에서 선택하세요.");
            }
        }
    }


    //----- 여기에 로직을 작성하세요. ----
    private static void stringFormat() {}

    private static void messageFormat() {}

    private static void decimalFormat() {}

}