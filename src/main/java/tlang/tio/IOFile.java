package tlang.tio;

import tlang.core.String;
import tlang.core.Void;
import tlang.core.func.FuncRet;

import java.io.*;

public class IOFile {

    public static FuncRet<Void> write(String path, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()))) {
            writer.write(content.toString());
        } catch (IOException e) {
//            return FuncRet.error(e);
        }
        return FuncRet.VOID;
    }

    public static FuncRet<String> read(String path) {
        var content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            java.lang.String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return FuncRet.of(new String(content.toString()));
        } catch (IOException e) {
//            return FuncRet.error(e);
            return null;
        }
    }
}
