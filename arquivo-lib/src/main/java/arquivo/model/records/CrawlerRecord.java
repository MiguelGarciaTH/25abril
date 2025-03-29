package arquivo.model.records;

public record CrawlerRecord(int searchEntityId, int siteId, String title, String url, String textUrl, String imageUrl) {
}
