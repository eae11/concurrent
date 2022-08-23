import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {


    public static void main(String[] args) {
        String file_name = "C:\\Users\\17477\\Desktop\\笔记2\\springboot.md";
        readFileByLines(file_name);
    }

    public static void readFileByLines(String fileName) {
        BufferedReader reader = null;

        try {
            System.out.println("以行为单位读取txt");
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String tempString = null;
            int line = 1;
            Pattern p = Pattern.compile("springboot.assets/\\d*\\.\\w*");
            //一次读一行，直到读入null为结束
            while ((tempString = reader.readLine()) != null) {
                Matcher matcher = p.matcher(tempString);
                while (matcher.find()) {
                    System.out.println(matcher.group());
                }

            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
