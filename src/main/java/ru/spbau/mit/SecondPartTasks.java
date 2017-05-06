package ru.spbau.mit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.function.Predicate.*;

public final class SecondPartTasks {
    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        Function<String, Stream<String>> linesGetter = s -> {
            Path p = Paths.get(s);
            try {
                return Files.lines(p);
            } catch (IOException e) {
                return Stream.empty();
            }
        };
        return paths.stream()
                .flatMap(linesGetter)
                .filter(s -> s.contains(sequence))
                .collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать,
    // какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        final int pointsLimit = 10000000;
        final java.util.function.Predicate<Point> isHit = p -> p.sqr() <= 1.0;
        Point seed = new Point();

        UnaryOperator<Point> pointGenerator = point -> new Point();

        final double hitNumber = (double) Stream.iterate(seed, pointGenerator)
                .limit(pointsLimit)
                .filter(isHit)
                .count();
        return hitNumber / pointsLimit;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        Function<Map.Entry<String, List<String>>, Pair<String, Integer>> mapper = stringListEntry -> {
            String name = stringListEntry.getKey();
            Integer length = stringListEntry.getValue().stream()
                    .mapToInt(String::length)
                    .sum();
            return new Pair<>(name, length);
        };

        Comparator<Pair<String, Integer>> bestAuthorComparator = Comparator.comparing(o -> o.second);

        return compositions.entrySet()
                .stream()
                .map(mapper)
                .max(bestAuthorComparator)
                .orElseThrow(UnsupportedOperationException::new).first;
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        Function<Map.Entry<String, Integer>, String> classifier = e -> e.getKey();
        return orders.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(classifier, Collectors.summingInt(Map.Entry::getValue)));
    }

    private static class Point {
        private static Random pointsRandomizer = new Random();
        private final double x;
        private final double y;

        Point() {
            x = pointsRandomizer.nextDouble();
            y = pointsRandomizer.nextDouble();
        }

        public double sqr() {
            return x * x + y * y;
        }
    }

    private static class Pair<F, S> {
        private F first;
        private S second;

        Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
}
