package arquivo.crawler;

import java.time.LocalDateTime;

public record CrawlerRecord(int searchEntityId, int siteId, String arquivoDigest, String linkToMetadata) {
}
