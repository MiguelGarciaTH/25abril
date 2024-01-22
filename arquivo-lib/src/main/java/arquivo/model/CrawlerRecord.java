package arquivo.model;

public record CrawlerRecord(int searchEntityId, int siteId, String digest,
                            String title,
                            String url,
                            String metaDataUrl,
                            String textUrl,
                            String noFrameUrl) {
}
