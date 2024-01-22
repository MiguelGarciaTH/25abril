package arquivo.processor;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ContextualTextScore {

    private static final Logger LOG = LoggerFactory.getLogger(ContextualTextScore.class);

    private static final Map<String, Integer> keywords = Map.ofEntries(
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

    public Score score(String text) {
        int score = 0;
        final Map<String, Integer> individualScore = new HashMap<>();
        final String lowerCase = text.toLowerCase();
        for (var keyword : keywords.entrySet()) {
            int counter = StringUtils.countMatches(lowerCase, keyword.getKey());
            int keywordScore = keyword.getValue() * counter;
            LOG.debug("Keyword={} Count={} Score={} Final score={}", keyword.getKey(), counter, keyword.getValue(), keywordScore);
            individualScore.put(keyword.getKey(), keywordScore);
            score += keywordScore;
        }
        return new Score(score, individualScore);
    }

    public record Score(int total, Map<String, Integer> individualScore){

    }
}
