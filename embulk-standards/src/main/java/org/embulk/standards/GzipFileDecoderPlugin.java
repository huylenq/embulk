package org.embulk.standards;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.embulk.config.ConfigInject;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.config.TaskSource;
import org.embulk.spi.BufferAllocator;
import org.embulk.spi.DecoderPlugin;
import org.embulk.spi.Exec;
import org.embulk.spi.FileInput;
import org.embulk.spi.util.FileInputInputStream;
import org.embulk.spi.util.InputStreamFileInput;
import org.embulk.spi.util.InputStreamFileInput.Provider;
import org.slf4j.Logger;

public class GzipFileDecoderPlugin
        implements DecoderPlugin
{
    private static final Logger log = Exec.getLogger(GzipFileDecoderPlugin.class);
    public interface PluginTask
            extends Task
    {
        @ConfigInject
        BufferAllocator getBufferAllocator();
    }

    @Override
    public void transaction(ConfigSource config, DecoderPlugin.Control control)
    {
        log.info(">> transaction()");
        PluginTask task = config.loadConfig(PluginTask.class);
        control.run(task.dump());
        log.info("<< transaction()");
    }

    @Override
    public FileInput open(TaskSource taskSource, FileInput fileInput)
    {
        log.info(">> open()");
        PluginTask task = taskSource.loadTask(PluginTask.class);
        final FileInputInputStream files = new FileInputInputStream(fileInput);
      InputStreamFileInput ret = new InputStreamFileInput(
          task.getBufferAllocator(),
          new Provider() {
            public InputStream openNext() throws IOException {
              if (!files.nextFile()) {
                return null;
              }
              return new GZIPInputStream(files, 8 * 1024);
            }

            public void close() throws IOException {
              files.close();
            }
          });
      log.info("<< open()");
      return ret;
    }
}
