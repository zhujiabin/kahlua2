package se.krka.kahlua.j2se.interpreter;

import se.krka.kahlua.converter.KahluaEnumConverter;
import se.krka.kahlua.converter.KahluaTableConverter;
import se.krka.kahlua.converter.LuaConverterManager;
import se.krka.kahlua.converter.LuaNumberConverter;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class InteractiveShell {
    public static void main(final String[] args) {
        JFrame frame = new JFrame("Kahlua interpreter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final Platform platform = new J2SEPlatform();
        KahluaTable env = platform.newEnvironment();

        LuaConverterManager manager = new LuaConverterManager();
        LuaNumberConverter.install(manager);
        KahluaEnumConverter.install(manager);
        new KahluaTableConverter(platform).install(manager);
        LuaJavaClassExposer exposer = new LuaJavaClassExposer(manager, platform, env);

        exposer.exposeGlobalFunctions(exposer);
        KahluaTable staticBase = platform.newTable();
        env.rawset("Java", staticBase);
        exposer.exposeLikeJavaRecursively(Object.class, staticBase);

        exposer.exposeGlobalFunctions(new Sleeper());

        InteractiveShell shell = new InteractiveShell(frame, platform, env);
    }

    public InteractiveShell(JFrame frame, Platform platform, KahluaTable env) {
        JPanel interpreter1 = new Interpreter(platform, env, frame);
        JPanel interpreter2 = new Interpreter(platform, env, frame);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFocusable(false);
        tabs.add("First", interpreter1);
        tabs.add("Second", interpreter2);
        frame.getContentPane().add(tabs);
        frame.pack();
        frame.setVisible(true);
    }

}