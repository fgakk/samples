package com.fgakk.samples.hazelcast.jet;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.InstanceConfig;
import com.hazelcast.jet.config.JetConfig;

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
 *               -----------
 *              |  html-parser |
 *               -----------
 *                    |
 *                 (element)
 *                    V
 *               -----------
 *              |   price extractor |
 *               -----------
 *                    |
 *                 (game)
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

    private JetInstance jet;

    public static void main(String[] args){
        new GamePriceExtractor().go();
    }
    private void go() {
        try {
            setup();
        } finally {
            jet.shutdown();
        }
    }

    private void setup() {
        // Initialize jet instance
        JetConfig jetConfig = new JetConfig();
        jetConfig.setInstanceConfig(new InstanceConfig().setCooperativeThreadCount(
                Math.max(1, Runtime.getRuntime().availableProcessors() / 2)));
        // Creating first instance
        jet = Jet.newJetInstance(jetConfig);
        // Creating second instance
        Jet.newJetInstance(jetConfig);
    }
}
