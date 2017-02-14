package com.fgakk.samples.hazelcast.jet;

import com.fgakk.samples.hazelcast.jet.data.Game;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.*;
import com.hazelcast.jet.config.InstanceConfig;
import com.hazelcast.jet.config.JetConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *                     --------
 *                    | source |
 *                       --------
 *                 /             \
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
 *                      groupByKey
 *                       ------
 *                        v
 *                        ------
 *                        filter
 *                        ------
 *                          | sink |
 * </pre>
 * <p>
 * The DAG works like this:
 * <li>
 * Source consists of html document content as stream.
 * </li>
 * <li>
 * price extractor will extract lines with price information
 * </li>
 * <li>
 * name extractor will extract the name information from the line.
 * </li>
 * <li>
 * groupByKey vertex combines the map entries with the same key and produce a game object with price and game fields
 * </li>
 * <li>
 * <li>
 * filter will exclude game data without name.
 * </li>
 * At sink the data will be written to in-memory list.
 * </li>
 * Created by fgakk on 07/02/17.
 */
public class GamePriceExtractor {

    private static final String GAMES_DOCUMENT = "gamesDocument";
    private static final String PRICES = "prices";

    private static final Pattern pattern = Pattern.compile("\\s+<div class=\"discount_final_price\".*");
    private static final Pattern namePattern = Pattern.compile(".*tab_item_name.*");

    private static final long[] initial = {0L, 0L};

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePriceExtractor.class);

    private JetInstance jet;

    public static void main(String[] args) {
        try {
            new GamePriceExtractor().go();
        } catch (Exception e) {
            LOGGER.error("Problem occurred while running GamePriceExtractor", e);
        }
    }

    private void go() throws Exception {
        try {
            setup();
            long start = System.nanoTime();
            jet.newJob(buildDAG()).execute().get();
            LOGGER.info("done in {} milliseconds.", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            logResult();
        } finally {
            Jet.shutdownAll();
        }
    }

    private void logResult() {
        final int limit = 100;
        final List<Map.Entry> prices = jet.getList(PRICES);
        LOGGER.info(" Number of entries are {}", prices.size());
        LOGGER.info(" Top {} entries are: ", limit);
    
        LOGGER.info("| Prices  as Html Element  |");
        
        prices.forEach(e -> LOGGER.info("|Key {} | {} |", e.getKey(), e.getValue()));
        
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
                Map.Entry<Long, String>::getKey, Game::new, (output, entry) ->
                {
                    //TODO find a better selection for money with a more complex regex pattern
                    if (entry.getValue().contains("€") || entry.getValue().contains("Free")) {
                        output.setPrice(entry.getValue());
                    } else {
                        output.setName(entry.getValue());
                    }
                    return output;
                }));

        Vertex filter = dag.newVertex("filter", Processors.filter(GamePriceExtractor::hasName));

        Vertex sink = dag.newVertex("sink", Processors.writeList(PRICES));

        // Setting edges
        dag.edge(Edge.between(source, priceParser.localParallelism(1)))
                .edge(Edge.from(source, 1).to(nameParser.localParallelism(1)))
                .edge(Edge.from(nameParser).to(groupByKey.localParallelism(1)))
                .edge(Edge.from(priceParser).to(groupByKey.localParallelism(1), 1))
                .edge(Edge.from(groupByKey).to(filter))
                .edge(Edge.from(filter).to(sink));
        return dag;
    }


    private static boolean priceMatches(String str) {
        return pattern.matcher(str).matches();
    }


    private static boolean nameMatches(String str) {
        return namePattern.matcher(str).matches();
    }

    private static boolean hasName(Map.Entry<Long, Game> item) {
        return item.getValue().getName() != null;
    }

    private static Stream<String> getHtmlDoc() throws IOException {
        final ClassLoader cl = GamePriceExtractor.class.getClassLoader();
        final BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("steam.html")));
        return br.lines().onClose(() -> close(br));
    }

    private static Map.Entry extractInfo(Function<String, Boolean> checker, String str, int counter) {
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
