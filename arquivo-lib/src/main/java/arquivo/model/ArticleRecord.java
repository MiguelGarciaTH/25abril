package arquivo.model;

public record ArticleRecord(int articleId, int entityId, int siteId, String siteName, int score, String url, String title, String text) {
}
