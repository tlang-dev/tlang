package tlang.tio;

import dev.tlang.tlang.ast.common.value.TLangString;
import dev.tlang.tlang.interpreter.Value;
import tlang.core.func.FuncRet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Terminal {

    private static BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public static FuncRet println(Value<?> value) {
        System.out.println(value.toString());
        return FuncRet.VOID;
    }

    public static FuncRet print(Value<?> value) {
        System.out.print(value.toString());
        return FuncRet.VOID;
    }

    public static FuncRet readln() {
        try {
            return FuncRet.of(READER.readLine());
        } catch (IOException e) {
            return FuncRet.error(e);
        }
    }

    public static FuncRet read() {
        try {
            return FuncRet.of(READER.read());
        } catch (IOException e) {
            return FuncRet.error(e);
        }
    }

    public static FuncRet exec(TLangString command) {
        try {
            Process process = Runtime.getRuntime().exec(command.toString());
            process.waitFor();
            return FuncRet.of(process.exitValue());
        } catch (IOException | InterruptedException e) {
            return FuncRet.error(e);
        }
    }
}
