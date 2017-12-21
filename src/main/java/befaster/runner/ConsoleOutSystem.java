package befaster.runner;

import tdl.client.runner.ConsoleOut;

public class ConsoleOutSystem implements ConsoleOut {
    @Override
    public void println(String s) {
        System.out.println(s);
    }

    @Override
    public void printf(String s, String... strings) {
        System.out.printf(s, (Object[]) strings);
    }
}
