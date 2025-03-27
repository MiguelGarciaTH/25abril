package arquivo.crawler;

import arquivo.model.SearchEntity;
import arquivo.repository.SearchEntityRepository;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
@ConditionalOnProperty(name = "25-abril.open-ia.bio-crawler.enable", havingValue = "true")
public class OpenIABioCrawler {

    private final String API_KEY;

    private static final Logger LOG = LoggerFactory.getLogger(OpenIABioCrawler.class);

    private final SearchEntityRepository searchEntityRepository;
    private final OkHttpClient client;

    @Autowired
    public OpenIABioCrawler(Environment environment, SearchEntityRepository searchEntityRepository) {
        this.searchEntityRepository = searchEntityRepository;
        this.client = new OkHttpClient();
        this.API_KEY = environment.getProperty("open-ai.api-key");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void bios() {
        final List<SearchEntity> entities = searchEntityRepository.findAll();
        for (SearchEntity entity : entities) {
            if (entity.getBiography() == null) {
                final String bio = getBio(entity.getName());
                entity.setBiography(bio);
                searchEntityRepository.save(entity);
            }
        }
    }

    private String getBio(String name) {
        // Create JSON payload for a chat-based request
        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo"); // Free-tier GPT-3.5 model
        json.put("max_tokens", 100);
        json.put("temperature", 0.7); // Optional: Adjust temperature for randomness in output

        // Messages for the chat-based model
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", "Write a short biography of " + name + ". Write the text in Portuguese from Portugal " +
                        "(PT-pt) and don't forget that " + name + " was some how related with '25 de Abril' or 'Estado Novo', " +
                        "therefore, if the bio does not refer these events, probably your are fetching the wrong name. " +
                        "And after te name put birth and death date between (), just the years. Keep the short bio under 100 words please."));
        json.put("messages", messages);

        // Create RequestBody with the JSON content
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

        // Build request
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")  // Endpoint for GPT-3.5 (chat-based)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)  // Add your API key
                .addHeader("Content-Type", "application/json")
                .build();

        // Retry logic for 429 (Rate Limiting)
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        final String responseBody = response.body().string();
                        final JSONObject jsonResponse = new JSONObject(responseBody);
                        LOG.info("Bio fetched successfully for {}", name);
                        return jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                    }
                } else if (response.code() == 429) {
                    // If 429 is returned, sleep for some time before retrying
                    retryCount++;
                    LOG.warn("Rate limit exceeded, retrying... Attempt {}", retryCount);
                    TimeUnit.SECONDS.sleep(5); // Wait 5 seconds before retrying
                } else {
                    LOG.error("Request failed: {}", response);
                    return null;
                }
            } catch (IOException | InterruptedException e) {
                LOG.error("Request failed: ", e.getCause());
                return null;
            }
        }
        LOG.error("The retry attempts exceeded for {}", name);
        return null;
    }
}
