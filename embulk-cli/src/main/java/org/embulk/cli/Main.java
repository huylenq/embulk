package org.embulk.cli;

import java.util.ArrayList;
import java.util.Collections;
import org.embulk.EmbulkVersion;
import org.embulk.lens.Repl;

public class Main
{
    public static void main(final String[] args)
    {
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

        new Repl().start();

        final EmbulkRun run = new EmbulkRun(EmbulkVersion.VERSION);
        final int error = run.run(Collections.unmodifiableList(embulkArgs), Collections.unmodifiableList(jrubyOptions));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(error);
    }
}
