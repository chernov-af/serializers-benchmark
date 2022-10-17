
# Запуск benchmark-ов посредством java -jar benchmarks.jar

При сборке проекта собирается uberjar с названием **`benchmarks.jar`**, который содержит как код классов данного
проекта, так и код всех его зависимостей.
Таким образом, этот uberjar можно запустить на любой машине простой командой:
```
java -jar target/benchmarks.jar
```
**`benchmarks.jar`** может принимать ключи командной строки для управления ключевыми параметрами запуска JMH-benchmarks:

1. Вывод помощи по поддерживаемым ключам:
   ```
   java -jar target/benchmarks.jar --help
   ```
   Примерный вывод:
   ```   
   BenchmarksRunner accepts the following arguments (values for example only):
      jmh.benchmarks.regexp=Benchmarks(.*)
      jmh.need.fork=true
      jmh.thread.count=3
      jmh.warmup.time.seconds=10
      jmh.measurement.time.seconds=5
      jmh.should.fail.on.error=false
   ```
2. Запуск benchmark-ов в fork-нутой JVM (рекомендуется разработчиками JMH, поэтому данное значение используется по умолчанию):
   ```
   java -jar target/benchmarks.jar jmh.need.fork=true
   ```
3. Запуск benchmark-ов в той же JVM, в которой запущен `benchmarks.jar` (удобно для отладки benchmark-ов, запуская из IDE):
   ```
   java -jar target/benchmarks.jar jmh.need.fork=false
   ```
4. Запуск только тех benchmark-ов, которые удовлетворяют регулярному выражению:
   ```
   java -jar target/benchmarks.jar jmh.benchmarks.regexp=Benchmarks\(.*\)
   ```
5. Запуск becnchmark-ов, используя указанную длительность прогрева и измерения:
   ```
   java -jar target/benchmarks.jar jmh.warmup.time.seconds=10
                                   jmh.measurement.time.seconds=5
   ```
5. Запуск becnchmark-ов, используя несколько параллельных потоков в процессе прогрева и изменения:
   ```
   java -jar target/benchmarks.jar jmh.thread.count=3
   ```

# Запуск benchmark-ов посредством exec-maven-plugin-а

Помимо запуска через `benchmarks.jar`, JMH-benchmarks можно запустить, используя `exec-maven-plugin`.

При этом будет использоваться тот же класс `CliBenchmarksRunner`, поэтому можно использовать те же ключи при запуске (но с префиксом `-D`).<br/>
Например:  

```
mvn clean install exec:java -Djmh.need.fork=true

mvn clean install exec:java -Djmh.need.fork=false

mvn clean install exec:java -Djmh.benchmarks.regexp=Benchmarks

mvn clean install exec:java -Djmh.warmup.time.seconds=10
                            -Djmh.measurement.time.seconds=5

mvn clean install exec:java -Djmh.thread.count=3
```

Этот вариант запуска особенно удобен из IDE (например, IntelliJ IDEA) с ключом `-Djmh.need.fork=false`.
В этом случае, хоть результат и будет менее показательным, зато можно отладить benchmark-и.