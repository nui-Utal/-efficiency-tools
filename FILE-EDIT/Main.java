import java.io.*;
import java.util.Scanner;

public class Main {
    static Scanner keyboard  = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String filePath = "";
        String[] editWord = {};
        String[] infoWord = {};
        System.out.println("- 保存并退出，= 不需要编辑的部分");
        System.out.println("请输入需要定位到的id（不需要请输入负数或0）");

        stringBuffer(filePath, keyboard.nextInt(), editWord, infoWord);
    }

    /**
     * 适用中小文件的方式：
     * 思路：通过使用bufferedReader读取并在读取内容中添加，借助stringBuilder，
     *      相比使用RandomAccessFile拥有更便捷的查找定位方式；
     *      但约等于复制整个文件，直接写会会导致覆盖原本未处理的内容，
     *      将后面部分全部读取进缓存后一起写入原文件。
     * @param filePath 文件路径
     * @param id 当前处理的json文件，通过id快速到达未处理的部分
     * @param editWord 需要插入数据的相关项
     */
    static void stringBuffer(String filePath, int id, String[] editWord, String[] infoWords) {
        String targetContent = "facebook";
        try {
            // 读取文件
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder fileContent = new StringBuilder();
            String line;
            String content;
            String end;
            int curId = 0;

            // 逐行读取文件内容
            while ((line = reader.readLine()) != null) {
                end = "";
                if (line.contains("\"id\":")) {
                    curId = Integer.parseInt(line.substring(line.length() - 2, line.length() - 1));
                    if (!(curId < id)) {
                        System.out.println(line);
                    }
                }
                if (curId < id) {
                    fileContent.append(line).append(System.lineSeparator());
                    continue;
                }
                // 在找到目标内容后，接受输入并添加文本
                if (isContains(line,editWord)) {
                    System.out.println(line);
                    String input = keyboard.nextLine();
                    if (input != null && input.equals("-")) {
                        do {
                            // 先保证当前行被读取
                            fileContent.append(line).append(System.lineSeparator());
                        } while ((line = reader.readLine()) != null);
                        break;
                    }
                    if (input != null && input.equals("=")) {
                        fileContent.append(line).append(System.lineSeparator());
                        continue;
                    }
                    if (line.endsWith("[")) {
                        content = line + System.lineSeparator() + input;
                    } else {
                        content = line.substring(0, line.length() - 2);
                        if (line.endsWith("],")) {
                            end =  "],";
                        }
                        if (line.endsWith("\",")) {
                             end = "\",";
                        }
                    }
                    fileContent.append(content).append(end).append(System.lineSeparator());
                } else {
                    fileContent.append(line).append(System.lineSeparator());
                }

                if (isContains(line, infoWords)) {
                    System.out.println(line);
                }
            }

            // 关闭读取器
            reader.close();

            // 使用BufferedWriter写入文件
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(fileContent.toString());
            // 关闭写入
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean isContains(String line, String[] keywords) {
        for (String keyword : keywords) {
            if (line.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

}
