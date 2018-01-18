package org.embulk.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

import clojure.lang.Compiler;
import clojure.lang.RT;
import clojure.lang.Symbol;
import org.embulk.EmbulkVersion;
import org.embulk.spik.Repl;
import clojure.lang.PersistentList;

public class Main
{
    public static void main(final String[] args) throws IOException, InterruptedException {
        final ArrayList<String> jrubyOptions = new ArrayList<String>();

        int i;
        for (i = 0; i < args.length; ++i) {
            if (args[i].startsWith("-R")) {
                jrubyOptions.add(args[i].substring(2));
            } else {
                break;
            }
        }

        final ArrayList<String> embulkArgs = new ArrayList<String>();
        for (; i < args.length; ++i) {
            embulkArgs.add(args[i]);
        }

        Repl.start();

//        System.out.print("Hit any key to continue!");
//        System.in.read();
//        System.out.println("Resume execution!");

        for (int j = 0; j < embulkArgs.size(); j++) {
            if (embulkArgs.get(j).equals("-clj")) {
                RT.var("clojure.core", "require").invoke(Symbol.intern("embulk.spik.core"));
                RT.var("clojure.core", "require").invoke(Symbol.intern("embulk.spik.restful"));
                Compiler.loadFile(embulkArgs.get(j + 1));
            }
        }

        while (true) {
            // I encountered issue with class loaders
            // So this work around here to early loading Clojure's classes
            final EmbulkRun run = new EmbulkRun(EmbulkVersion.VERSION);
            final int error = run.run(
                Collections.unmodifiableList(embulkArgs),
                Collections.unmodifiableList(jrubyOptions));
            System.out.println("Hit Enter to rerun!");
            System.in.read();
        }
    }

}