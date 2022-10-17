package ru.chernovaf.serializers.benchmark;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Запускатель JMH-бенчмарков, вызываемый:
 * <ul>
 *     <li>либо в составе <b>{@code benchmarks.jar}</b> как Main-Class (в той же JVM, в которой запущен jar):
 *     <pre>{@code
 *     java -jar target/benchmarks.jar[ jmh.benchmarks.regexp=Benchmarks
 *                                      jmh.need.fork=false
 *                                      jmh.warmup.time.seconds=10
 *                                      jmh.measurement.time.seconds=5]
 *     }</pre>
 *     </li>
 *     <li>либо exec-maven-plugin'ом (в той же JVM, в которой работает Maven):
 *     <pre>{@code
 *     mvn clean install exec:java[ -Djmh.benchmarks.regexp=Benchmarks
 *                                  -Djmh.need.fork=false
 *                                  -Djmh.warmup.time.seconds=10
 *                                  -Djmh.measurement.time.seconds=5]
 *     }</pre>
 *     </li>
 * </ul>
 */
public class CliBenchmarksRunner {

    private static final String JMH_BENCHMARKS_REGEXP_ARG        = "jmh.benchmarks.regexp";
    private static final String JMH_NEED_FORK_ARG                = "jmh.need.fork";
    private static final String JMH_THREAD_COUNT_ARG             = "jmh.thread.count";
    private static final String JMH_WARMUP_TIME_SECONDS_ARG      = "jmh.warmup.time.seconds";
    private static final String JMH_MEASUREMENT_TIME_SECONDS_ARG = "jmh.measurement.time.seconds";
    private static final String IDE_PROJECT_DIR_ARG              = "ide.project.dir";
    private static final String JMH_SHOULD_FAIL_ON_ERROR         = "jmh.should.fail.on.error";
    private static final String HELP_MESSAGE =
            String.format( "BenchmarksRunner accepts the following arguments (values for example only):%n" +
                           "   %s=Benchmarks(.*)%n" +
                           "   %s=true%n" +
                           "   %s=3%n" +
                           "   %s=10%n" +
                           "   %s=5%n" +
                           "   %s=false",
                           JMH_BENCHMARKS_REGEXP_ARG, JMH_NEED_FORK_ARG, JMH_THREAD_COUNT_ARG,
                           JMH_WARMUP_TIME_SECONDS_ARG, JMH_MEASUREMENT_TIME_SECONDS_ARG,
                           JMH_SHOULD_FAIL_ON_ERROR);

    private static final boolean NEED_FORK_DEFAULT = true;
    private static final int THREAD_COUNT_DEFAULT = 1;
    private static final long WARMUP_TIME_SECONDS_DEFAULT = 1L;
    private static final long MEASUREMENT_TIME_SECONDS_DEFAULT = 1L;
    private static final boolean SHOULD_FAIL_ON_ERROR_DEFAULT = true;

    private static class Params {
        public String benchmarksRegexp;
        public boolean needFork = NEED_FORK_DEFAULT;
        public String classpathToFork;
        public int threadCount = THREAD_COUNT_DEFAULT;
        public long warmupTimeSeconds = WARMUP_TIME_SECONDS_DEFAULT;
        public long measurementTimeSeconds = MEASUREMENT_TIME_SECONDS_DEFAULT;
        public boolean shouldFailOnError = SHOULD_FAIL_ON_ERROR_DEFAULT;
    }

    private static class HelpException extends RuntimeException {

        public HelpException( String message ) {
            super( message );
        }
        public HelpException( String message, Throwable cause ) {
            super( message, cause );
        }
    }

    public static void main( String... args ) throws RunnerException {
        try {
            helpIfNeed( args );

            Params params = new Params();
            params.benchmarksRegexp = getStringArgFrom( args, JMH_BENCHMARKS_REGEXP_ARG, null );
            params.needFork = getBooleanArgFrom( args, JMH_NEED_FORK_ARG, NEED_FORK_DEFAULT );
            params.threadCount = ( int )getLongArgFrom(args, JMH_THREAD_COUNT_ARG, THREAD_COUNT_DEFAULT);
            params.warmupTimeSeconds = getLongArgFrom(args, JMH_WARMUP_TIME_SECONDS_ARG, WARMUP_TIME_SECONDS_DEFAULT);
            params.measurementTimeSeconds = getLongArgFrom(args, JMH_MEASUREMENT_TIME_SECONDS_ARG,
                                                           MEASUREMENT_TIME_SECONDS_DEFAULT );
            String ideProjectDir = getStringArgFrom(args, IDE_PROJECT_DIR_ARG, null);
            params.shouldFailOnError = getBooleanArgFrom(args, JMH_SHOULD_FAIL_ON_ERROR, SHOULD_FAIL_ON_ERROR_DEFAULT);

            if (params.needFork)
                if (ideProjectDir != null)
                    params.classpathToFork = ideProjectDir + File.separator + "target" + File.separator + "benchmarks.jar";
                else
                    params.classpathToFork = classpathFromClassLoader();

            runBenchmarks( params );
        } catch ( HelpException e ) {
            System.out.println( e.getMessage() );
        }
    }

    private static void helpIfNeed( String[] args ) {
        if ( args.length > 0 &&
             ( args[0].equals( "--help" ) ||
               args[0].equals( "-help" )  ||
               args[0].equals( "-?" )     ||
               args[0].equals( "/?" )     ||
               args[0].equals( "?" ) ) )
            throw new HelpException( HELP_MESSAGE );
    }

    private static String getStringArgFrom( String[] args, String argName, String defaultValue ) {
        for ( String arg : args ) {
            if ( !arg.contains( "=" ) )
                throw new HelpException( HELP_MESSAGE );
            String[] nameAndValue = arg.split( "=" );
            if ( nameAndValue[0].equals( argName ) )
                return ( nameAndValue.length == 1 ? defaultValue : nameAndValue[1] );
        }

        return defaultValue;
    }

    private static boolean getBooleanArgFrom( String[] args, String argName, boolean defaultValue ) {
        String stringValue = getStringArgFrom( args, argName, null );
        if ( stringValue == null )
            return defaultValue;
        else {
            if ( !stringValue.equalsIgnoreCase( "true" ) &&
                 !stringValue.equalsIgnoreCase( "false" ) )
                throw new HelpException( HELP_MESSAGE );
            else
                return Boolean.parseBoolean( stringValue );
        }
    }

    private static long getLongArgFrom( String[] args, String argName, long defaultValue ) throws NumberFormatException {
        String stringValue = getStringArgFrom( args, argName, null );
        if ( stringValue == null )
            return defaultValue;
        else
            try {
                return Long.parseLong( stringValue );
            } catch ( NumberFormatException e ) {
                throw new HelpException( HELP_MESSAGE, e );
            }
    }

    private static String classpathFromClassLoader() {
        ClassLoader currentClassLoader = CliBenchmarksRunner.class.getClassLoader();
        if ( currentClassLoader instanceof URLClassLoader ) {
            URL[] urls = ((URLClassLoader)currentClassLoader).getURLs();
            StringBuilder classpath = new StringBuilder();
            for (int i = 0; i < urls.length; ++i) {
                classpath.append(urls[i].getPath());
                if (i < urls.length - 1)
                    classpath.append(";");
            }
            return classpath.toString();
        } else {
            return System.getProperty("java.class.path");
        }
    }

    /**
     * Метод для запуска benchmark-ов
     *
     * @return коллекция с результатами выполнения benchmark-ов
     */
    public static Collection<RunResult> runBenchmarks( Params params ) throws RunnerException {
        if (params.benchmarksRegexp == null)
            throw new IllegalArgumentException("Не задано регулярное выражение для запуска benchmark-ов");
        ChainedOptionsBuilder optionsBuilder =
                new OptionsBuilder()
                        .include(params.benchmarksRegexp)
                        .threads(params.threadCount)
                        .warmupTime( TimeValue.seconds( params.warmupTimeSeconds ) )
                        .warmupIterations(1)
                        .measurementTime(TimeValue.seconds(params.measurementTimeSeconds))
                        .measurementIterations(1)
                        .timeUnit( TimeUnit.MICROSECONDS )
                        .mode( Mode.AverageTime )
                        .shouldFailOnError(params.shouldFailOnError)
                        .forks(params.needFork ? 1 : 0)
                        .verbosity( VerboseMode.NORMAL );
        if (params.needFork) {
            assert params.classpathToFork != null;
            // Пробросим в fork-нутую JVM classpath
            System.setProperty("java.class.path", params.classpathToFork);
            optionsBuilder.jvmArgs("-classpath", params.classpathToFork);
        }
        Options options = optionsBuilder.build();
        return new Runner( options).run();
    }
}
