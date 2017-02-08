package com.fgakk.samples.hazelcast.jet;

import com.hazelcast.core.IList;
import com.hazelcast.jet.*;
import com.hazelcast.jet.config.InstanceConfig;
import com.hazelcast.jet.config.JetConfig;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
 *                    |
 *                   (line)
 *                    |
 *                    V
 *               -----------
 *              |   price extractor |
 *               -----------
 *                    |
 *                 (price)
 *                    V
 *                 ------
 *                | sink |
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

    private JetInstance jet;

    private static final Pattern pattern = Pattern.compile(".*price.*€.*");
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
        final List<String> prices = jet.getList(PRICES);
        System.out.format(" Number of entries are %d %n", prices.size());
        System.out.format(" Top %d entries are:%n", limit);

        System.out.println("/-------+---------\\");
        System.out.println("| Prices  as Html Element  |");
        System.out.println("|-------+---------|");
        prices.forEach(e -> System.out.format("|%6s |%n", e));
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

        Vertex source = dag.newVertex("source", Processors.readList(GAMES_DOCUMENT)).localParallelism(1);

        Vertex htmlParser = dag.newVertex("priceExtractor", Processors.filter(GamePriceExtractor::matches));

        Vertex sink = dag.newVertex("sink", Processors.writeList(PRICES));

        // Setting edges
        dag.edge(Edge.between(source, htmlParser))
                .edge(Edge.between(htmlParser, sink));
        return dag;
    }

    private static boolean matches(String str){
        return pattern.matcher(str).matches();
    }

    private static Stream<String> getHtmlDoc() throws IOException {
        final ClassLoader cl = GamePriceExtractor.class.getClassLoader();
        final BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("steam.html")));
        return br.lines().onClose(() -> close(br));
    }

    private static void close(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
