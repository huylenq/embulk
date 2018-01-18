package org.embulk.standards;

import java.util.List;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.config.TaskReport;
import org.embulk.config.TaskSource;
import org.embulk.spi.Exec;
import org.embulk.spi.OutputPlugin;
import org.embulk.spi.Page;
import org.embulk.spi.PageReader;
import org.embulk.spi.Schema;
import org.embulk.spi.TransactionalPageOutput;
import org.embulk.spi.util.PagePrinter;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

public class StdoutOutputPlugin
        implements OutputPlugin
{
    private static final Logger log = Exec.getLogger(StdoutOutputPlugin.class);
    public interface PluginTask
            extends Task
    {
        @Config("timezone")
        @ConfigDefault("\"UTC\"")
        public DateTimeZone getTimeZone();
    }

    @Override
    public ConfigDiff transaction(ConfigSource config,
            Schema schema, int taskCount,
            OutputPlugin.Control control)
    {
        log.info(">> transaction()");
        final PluginTask task = config.loadConfig(PluginTask.class);
        ConfigDiff ret = resume(task.dump(), schema, taskCount, control);
        log.info("<< transaction()");
        return ret;
    }

    @Override
    public ConfigDiff resume(TaskSource taskSource,
            Schema schema, int taskCount,
            OutputPlugin.Control control)
    {
        log.info(">> resume()");
        control.run(taskSource);
        log.info("<< resume()");
        return Exec.newConfigDiff();
    }

    public void cleanup(TaskSource taskSource,
            Schema schema, int taskCount,
            List<TaskReport> successTaskReports)
    { }

    @Override
    public TransactionalPageOutput open(TaskSource taskSource, final Schema schema,
            int taskIndex/**/)
    {
//        log.info(">> open");
        final PluginTask task = taskSource.loadTask(PluginTask.class);

        TransactionalPageOutput ret = new TransactionalPageOutput() {
            private final PageReader reader = new PageReader(schema);
            private final PagePrinter printer = new PagePrinter(schema, task.getTimeZone());

            public void add(Page page) {
              log.info(">> TransactionalPageOutput.addPage()");
                reader.setPage(page);
                while (reader.nextRecord()) {
                    System.out.println(printer.printRecord(reader, ","));
                }
                log.info("<< TransactionalPageOutput.addPage()");
            }

            public void finish() {
                System.out.flush();
            }

            public void close() {
                reader.close();
            }

            public void abort() {
            }

            public TaskReport commit() {
                return Exec.newTaskReport();
            }
        };
//        log.info("<< open");
        return ret;
    }
}
