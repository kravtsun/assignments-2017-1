package ru.spbau.mit;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SecondPartTasks {
    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        Function<String, Stream<String>> linesGetter = s -> {
            Path p = Paths.get(s);
            try {
                return Files.lines(p);
            } catch (IOException e) {
//                    e.printStackTrace();
                return Stream.empty();
            }
        };
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
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
        final int POINTS_LIMIT = 10000000;
        final Predicate<Point> isHit = p -> p.sqr() <= 1.0;
        Point seed = new Point();

        UnaryOperator<Point> pointGenerator = new UnaryOperator<Point>() {
            @Override
            public Point apply(Point point) {
                return new Point();
            }
        };

        double hitNumber = (double)Stream.iterate(seed, pointGenerator).limit(POINTS_LIMIT).filter(isHit).count();
        return hitNumber / POINTS_LIMIT;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        Function<Map.Entry<String, List<String>>, Pair<String, Integer>> mapper = stringListEntry -> {
            String name = stringListEntry.getKey();
            Integer length = stringListEntry.getValue().stream()
                    .mapToInt(String::length)
                    .reduce(0, (l, r) -> l + r);
            return new Pair(name, length);
        };

        Comparator<Pair<String, Integer>> bestAuthorComparator = new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return o1.second.compareTo(o2.second);
            }
        };

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
        Supplier<Integer> supplier = () -> (0);
        BiConsumer<Integer, Map.Entry<String, Integer>> accumulator = (i, e) -> i += e.getValue();
        BinaryOperator<Integer> combiner = (a, b) -> a + b;

        Collector<Map.Entry<String, Integer>, ?, Integer> downstream = Collector.of(supplier, accumulator, combiner);
        return orders.stream()
//                .reduce(Stream::concat)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(classifier, Collectors.summingInt(Map.Entry::getValue)));
    }

    private static class Point {
        private static Random POINTS_RANDOMIZER = new Random();
        private final double x;
        private final double y;

        public double sqr() {
            return x * x + y * y;
        }

        Point() {
            x = POINTS_RANDOMIZER.nextDouble();
            y = POINTS_RANDOMIZER.nextDouble();
        }
    }

    private static class Pair<F, S> {
        Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
        F first;
        S second;
    }
}
