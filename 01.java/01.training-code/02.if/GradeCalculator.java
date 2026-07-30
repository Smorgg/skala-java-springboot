import java.util.Scanner;

public class GradeCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 점수 입력 받기
        System.out.print("점수를 입력하세요 (0~100): ");
        int score = scanner.nextInt();

        String grade= "";

        //----- 여기에 로직을 작성하세요. ----
        // 학점 계산


        
        System.out.println("학점: " + grade);

        scanner.close();
    }
}

