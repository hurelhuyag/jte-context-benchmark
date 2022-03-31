package io.github.hurelhuyag.jtecontext;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import io.github.hurelhuyag.jteutils.MessageBundle;
import io.github.hurelhuyag.jteutils.TemplateUtils;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BenchmarkMessageFormatting {

    @Test
    public void runBenchmarks() throws Exception {
        Options options = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.Throughput)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(3)
                .threads(4)
                .measurementIterations(6)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(options).run();
    }

    private static final TemplateOutput nullTemplateOutput = new TemplateOutput() {
        @Override
        public Writer getWriter() {
            return new Writer() {
                @Override
                public void write(char[] cbuf, int off, int len) throws IOException {

                }

                @Override
                public void flush() throws IOException {

                }

                @Override
                public void close() throws IOException {

                }
            };
        }

        @Override
        public void writeContent(String value) {

        }
    };

    private static final TemplateEngine templateEngine;
    private static final MessageBundle messageBundle;
    private static final Locale locale = Locale.ENGLISH;

    static  {
        templateEngine = TemplateEngine.createPrecompiled(ContentType.Html);
        try {
            messageBundle = new MessageBundle(BenchmarkMessageFormatting.class.getClassLoader(), "io/github/hurelhuyag/jtecontext/messages");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void ourMessageFormat() throws Exception {
        var mf = new TemplateUtils(ZoneId.systemDefault(), locale, messageBundle);

        var params = new HashMap<String, Object>();
        params.put("mf", mf);
        params.put("p1", nextParam());
        params.put("p2", nextParam());
        params.put("p3", nextParam());
        templateEngine.render("template.jte", params, nullTemplateOutput);
    }

    private static MessageSource messageSource;

    static {
        var ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:io/github/hurelhuyag/jtecontext/messages");
        ms.setDefaultEncoding("UTF-8");
        ms.setCacheSeconds(60*60*6);
        ms.setUseCodeAsDefaultMessage(false);
        messageSource = ms;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void springMessageFormat() throws Exception {
        var params = new HashMap<String, Object>();
        params.put("ms", messageSource);
        params.put("p1", nextParam());
        params.put("p2", nextParam());
        params.put("p3", nextParam());
        templateEngine.render("template_with_spring.jte", params, nullTemplateOutput);
    }

    private static AtomicInteger inc = new AtomicInteger(0);

    private Object nextParam() {
        return System.currentTimeMillis();
    }

}
