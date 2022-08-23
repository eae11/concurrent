import java.io.*;
import java.util.ArrayList;
import java.util.List;

//从很多文件中筛选指定文件名的文件
public class test02 {
    //所有文件
    static List<File> files = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        String fileName = "C:\\Users\\17477\\Desktop\\aaa.txt";
        String f2 = "C:\\Users\\17477\\Desktop\\笔记2\\springboot.assets";
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        //要获取的文件名
        List<String> list = new ArrayList<>();
        String tempString = null;
        try {
            while ((tempString = reader.readLine()) != null) {
                list.add(tempString);
            }
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
        //System.out.println(list);
        //System.out.println(list.size());
        //文件夹
        File folder = new File("C:\\Users\\17477\\Desktop\\新建文件夹 (2)");
        listFilesForFolder(folder);
        for (File file : files) {
            for (String s : list) {
                String name = file.getName();
                if (name.equals(s)) {
                    byte[] bytes = fileByte(file);
                    try {
                        //输出到另一个文件夹里    f2文件夹 s文件名
                        FileOutputStream fos = new FileOutputStream(f2 + "\\" + s);
                        fos.write(bytes);
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static void listFilesForFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                listFilesForFolder(file);
            } else {
                files.add(file);
                System.out.println(file.getName());
            }
        }
    }

    //
    public static byte[] fileByte(File filePath) {
        byte[] buffer = null;

        try {
            FileInputStream fis = new FileInputStream(filePath);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {

        }
        return buffer;
    }

}
