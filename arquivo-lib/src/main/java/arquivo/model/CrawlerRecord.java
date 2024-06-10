package arquivo.model;

public record CrawlerRecord(int searchEntityId, int siteId,
                            String title,
                            String url,
                            String originalUrl,
                            String metaDataUrl,
                            String textUrl,
                            String noFrameUrl) {
}
