import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Math.*;

public class Test {
    public static void main(String[] args) throws IOException {
        String str = "";

        Function<Integer, Integer> f = a -> {


            int i = 0;
            String str2 = str.toUpperCase();
            return i;



        };

        int arr = Stream.of(1, 2, 3, 4, 5, 6, 7, 8)
                .reduce(1, (a, b) -> a * b);

        System.out.println(arr);

//        Reader r = new BufferedReader(new FileReader(new File("img.bmp")));
//
//        BufferedReader br = (BufferedReader) r;
//        br.lines();
//
//        List<List<List<Integer>>> lll = new ArrayList<>();
//        lll.stream().flatMap(List::stream).flatMap(List::stream);

        File fl = new File("img.bmp");

        System.out.println(fl.getAbsolutePath());
        System.out.println(fl.getCanonicalPath());
        System.out.println(fl.isHidden());
        System.out.println(fl.isDirectory());
        System.out.println(fl.isAbsolute());
        System.out.println(fl.isFile());
    }
}






























