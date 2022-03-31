package io.github.hurelhuyag.jtecontext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BenchmarkDateTimeFormatterCache {

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

    private static final Appendable nullAppendable = new Appendable() {
        @Override
        public Appendable append(CharSequence csq) throws IOException {
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            return this;
        }

        @Override
        public Appendable append(char c) throws IOException {
            return this;
        }
    };
    private static final Map<String, DateTimeFormatter> cache = new ConcurrentHashMap<>();
    private static final ThreadLocal<Map<String, DateTimeFormatter>> threadLocalCache = ThreadLocal.withInitial(ConcurrentHashMap::new);

    static  {
        cache.put("yyyy-MM-dd HH:mm:ss-" + ZoneId.systemDefault(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));
        cache.put("yyyy-MM-dd-" + ZoneId.systemDefault(), DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()));
        cache.put("HH:mm:ss-" + ZoneId.systemDefault(), DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault()));
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void instantiate() throws Exception {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        Assertions.assertNotNull(formatter);
        formatter.formatTo(LocalDateTime.now(), nullAppendable);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void getFromCache() throws Exception {
        var format = "yyyy-MM-dd HH:mm:ss";
        var formatter = cache.get(format + "-" + ZoneId.systemDefault());
        Assertions.assertNotNull(formatter);
        formatter.formatTo(LocalDateTime.now(), nullAppendable);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void getFromThreadLocalCache() throws Exception {
        threadLocalCache.get().computeIfAbsent("yyyy-MM-dd HH:mm:ss-" + ZoneId.systemDefault(), s -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()));
        var format = "yyyy-MM-dd HH:mm:ss";
        var formatter = threadLocalCache.get().get(format + "-" + ZoneId.systemDefault());
        Assertions.assertNotNull(formatter);
        formatter.formatTo(LocalDateTime.now(), nullAppendable);
        threadLocalCache.get().remove("yyyy-MM-dd HH:mm:ss-" + ZoneId.systemDefault());
    }

}
