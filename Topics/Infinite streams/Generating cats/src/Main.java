import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

class GenerateCats {

    public static List<Cat> generateNCats(int n) {
        Stream<Cat> catStream = Stream.generate(Cat::new).limit(n);
        return catStream.toList();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<Cat> cats = generateNCats(scanner.nextInt());

        System.out.println(cats.size());
    }
}

class Cat {
    String name;
    int age;
}