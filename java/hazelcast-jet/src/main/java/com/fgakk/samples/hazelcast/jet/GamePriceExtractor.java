package com.fgakk.samples.hazelcast.jet;

import com.fgakk.samples.hazelcast.jet.data.Game;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.*;
import com.hazelcast.jet.config.InstanceConfig;
import com.hazelcast.jet.config.JetConfig;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Analyzes an html document extracted from steam web site and
 * tries to create a structured game data with name and price information.
 * <p>
 * For the purpose of simplicity this sample does not cover complex use cases.
 * Following DAG will be implemented and used:
 * <pre>
 *                --------
 *               | source |
 *                --------
 *              /                |
 *             (line)         (line)
 *                |              |
 *                V              V
 *           ------------       --------
 *          price extractor      name extractor
 *           -----------        --------
 *                    |           |
 *                 (key, price)  (key, name)
 *                          V
 *                      ------
 *                      groupbykey
 *                       ------
 *                          | sink |
 * </pre>
 * <p>
 * The DAG works like this:
 * <li>
 * Source consists of html document content as stream.
 * </li>
 * <li>
 * html-parser will parse the text and extract html element
 * </li>
 * <li>
 * price extractor will try to find an information about price. If there is
 * one it will try to find the name of game and to construct an serializable {@code Game}
 * object.
 * </li>
 * <li>
 * At sink the data will be written to a json file.
 * </li>
 * Created by fgakk on 07/02/17.
 */
public class GamePriceExtractor {

    private static final String GAMES_DOCUMENT = "gamesDocument";
    private static final String PRICES = "prices";

    private static final Pattern pattern = Pattern.compile("\\s+<div class=\"discount_final_price\".*");
    private static final Pattern namePattern = Pattern.compile(".*tab_item_name.*");

    private static final long[] initial = {0L, 0L};

    private JetInstance jet;

    public static void main(String[] args) {
        try {
            new GamePriceExtractor().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void go() throws Exception {
        try {
            setup();
            long start = System.nanoTime();
            jet.newJob(buildDAG()).execute().get();
            System.out.print("done in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + " milliseconds.");
            printResult();
        } finally {
            Jet.shutdownAll();
        }
    }

    private void printResult() {
        final int limit = 100;
        final List<Map.Entry> prices = jet.getList(PRICES);
        System.out.format(" Number of entries are %d %n", prices.size());
        System.out.format(" Top %d entries are:%n", limit);

        System.out.println("/-------+---------\\");
        System.out.println("| Prices  as Html Element  |");
        System.out.println("|-------+---------|");
        prices.forEach(e -> System.out.format("|Key %d | %6s |%n", e.getKey(), e.getValue()));
        System.out.println("\\-------+---------/");
    }

    private void setup() throws IOException {
        // Initialize jet instance
        JetConfig jetConfig = new JetConfig();
        jetConfig.setInstanceConfig(new InstanceConfig().setCooperativeThreadCount(
                Math.max(1, Runtime.getRuntime().availableProcessors() / 2)));
        // Creating first instance
        jet = Jet.newJetInstance(jetConfig);
        // Creating second instance
        Jet.newJetInstance(jetConfig);
        // Getting map for feeding source vertex
        final IList<String> htmlLines = jet.getList(GAMES_DOCUMENT);
        // Feed the elements list
        getHtmlDoc().forEach(htmlLines::add);
    }

    private static DAG buildDAG() {
        DAG dag = new DAG();


        Distributed.Function<String, Map.Entry<Long, String>> itemToNameMap = (str) ->
            extractInfo(GamePriceExtractor::nameMatches, str, 0);

        Distributed.Function<String, Map.Entry<Long, String>> itemToPriceMap = (str) ->
            extractInfo(GamePriceExtractor::priceMatches, str, 1);

        Vertex source = dag.newVertex("source", Processors.readList(GAMES_DOCUMENT)).localParallelism(1);

        Vertex priceParser = dag.newVertex("priceExtractor", Processors.map(itemToPriceMap));

        Vertex nameParser = dag.newVertex("nameExtractor", Processors.map(itemToNameMap));

        Vertex groupByKey = dag.newVertex("groupByKey", Processors.groupAndAccumulate(
                Map.Entry<Long, String>::getKey, () -> "", (value, extractor) -> value.concat(extractor.getValue())));
        Vertex sink = dag.newVertex("sink", Processors.writeList(PRICES));

        // Setting edges
        dag.edge(Edge.between(source, priceParser.localParallelism(1)))
                .edge(Edge.from(source, 1).to(nameParser.localParallelism(1)))
                .edge(Edge.from(nameParser).to(groupByKey.localParallelism(1)))
                .edge(Edge.from(priceParser).to(groupByKey.localParallelism(1), 1))
                .edge(Edge.from(groupByKey).to(sink));
        return dag;
    }

    private static boolean priceMatches(String str) {
        return pattern.matcher(str).matches();
    }

    private static boolean nameMatches(String str) {
        return namePattern.matcher(str).matches();
    }

    private static Stream<String> getHtmlDoc() throws IOException {
        final ClassLoader cl = GamePriceExtractor.class.getClassLoader();
        final BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("steam.html")));
        return br.lines().onClose(() -> close(br));
    }

    private static Map.Entry extractInfo(Function<String, Boolean> checker, String str, int counter){
        if (checker.apply(str)) {
            int idx = str.indexOf("</");
            String extractedPrice = idx > -1 ? str.substring(str.indexOf('>') + 1, idx) : str.substring(str.indexOf('>') + 1);
            return new AbstractMap.SimpleImmutableEntry<>(++initial[counter], extractedPrice);
        } else {
            return null;
        }
    }

    private static void close(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
