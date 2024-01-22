package arquivo.processor;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ContextualTextScore {

    private static final Logger LOG = LoggerFactory.getLogger(ContextualTextScore.class);

    private final Map<String, Integer> keywords = Map.ofEntries(
            Map.entry("revolução dos cravos", 10),
            Map.entry("estado novo", 10),
            Map.entry("1974", 10),
            Map.entry("pide", 10),
            Map.entry("clandestinidade", 5),
            Map.entry("25 de abril", 10),
            Map.entry("revolução de abril", 10),
            Map.entry("guerra colonial", 10),
            Map.entry("ditadura", 5),
            Map.entry("facismo", 5)
    );


    public ContextualTextScore() {

    }

    public int score(String text) {
        int score = 0;
        final String lowerCase = text.toLowerCase();
        for (String keyword : keywords.keySet()) {
            int counter = StringUtils.countMatches(lowerCase, keyword);
            int keywordScore = keywords.get(keyword) * counter;
            LOG.debug("Keyword={} Count={} Score={} Final score={}", keyword, counter, keywords.get(keyword), keywordScore);
            score += keywordScore;

        }
        return score;
    }
}
