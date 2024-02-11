package arquivo.services;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextualTextScoreService {

    private static final Logger LOG = LoggerFactory.getLogger(ContextualTextScoreService.class);

    private static final Map<String, Integer> keywords = Map.ofEntries(
            Map.entry("revolução cravos", 10),
            Map.entry("estado novo", 10),
            Map.entry("25 abril", 10),
            Map.entry("revolução abril", 10),
            Map.entry("guerra colonial", 10),
            Map.entry("salazarismo", 10),
            Map.entry("salazarista", 10),
            Map.entry("clandestinidade", 5),
            Map.entry("ditadura", 5),
            Map.entry("1974", 5),
            Map.entry("pide", 5),
            Map.entry("fascismo", 5)
    );

    public ContextualTextScoreService() {

    }

    public Score score(String text, List<String> entityNames) {
        int score = 0;
        final Map<String, Integer> individualScore = new HashMap<>();
        for (var keyword : keywords.entrySet()) {
            int counter = StringUtils.countMatches(text, keyword.getKey());
            int keywordScore = keyword.getValue() * counter;
            individualScore.put(keyword.getKey(), keywordScore);
            score += keywordScore;
            LOG.debug("Keyword={} Count={} Score={} Final score={}", keyword.getKey(), counter, keyword.getValue(), keywordScore);
        }
        if (entityNames != null) {
            for (String name : entityNames) {
                int counter = StringUtils.countMatches(text, name);
                int nameScore = 100 * counter;
                individualScore.put(name, nameScore);
                score += nameScore;
                LOG.debug("Keyword={} Count={} Score={} Final score={}", name, counter, 100, nameScore);

            }
        }
        return new Score(score, individualScore);
    }

    public record Score(int total, Map<String, Integer> individualScore) {

    }
}
