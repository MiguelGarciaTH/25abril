package arquivo.services;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ContextualTextScoreService {

    private static final Logger LOG = LoggerFactory.getLogger(ContextualTextScoreService.class);

    private static final Map<String, Integer> keywords = Map.ofEntries(
            Map.entry("revolução cravos", 20),
            Map.entry("estado novo", 20),
            Map.entry("1974", 4),
            Map.entry("pide", 10),
            Map.entry("clandestinidade", 5),
            Map.entry("25 abril", 20),
            Map.entry("revolução abril", 20),
            Map.entry("guerra colonial", 10),
            Map.entry("ditadura", 5),
            Map.entry("salazarismo", 20),
            Map.entry("salazarista", 20),
            Map.entry("fascismo", 5)
    );

    public ContextualTextScoreService() {

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
