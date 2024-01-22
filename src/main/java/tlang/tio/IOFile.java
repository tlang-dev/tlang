package tlang.tio;

import tlang.core.func.FuncRet;

import java.io.*;

public class IOFile {

    public static FuncRet write(String path, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content);
        } catch (IOException e) {
            return FuncRet.error(e);
        }
        return FuncRet.VOID;
    }

    public static FuncRet read(String path) {
        var content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return FuncRet.of(content.toString());
        } catch (IOException e) {
            return FuncRet.error(e);
        }
    }
}
