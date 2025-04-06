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
                final String bio = getBio(entity.getName(), entity.getAliases());
                entity.setBiography(bio);
                searchEntityRepository.save(entity);
            }
        }
    }

    private String getBio(String name, String aliases) {
        // Create JSON payload for a chat-based request
        JSONObject json = new JSONObject();
        json.put("model", "gpt-4-turbo"); // Free-tier GPT-3.5 model
        json.put("max_tokens", 200);
        json.put("temperature", 0.0); // Optional: Adjust temperature for randomness in output

        String aliasesString = aliases != null ? " (também conhecido por: " + aliases + " )" : "";

        // Messages for the chat-based model
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", "És um historiador do Estado Novo e do 25 de Abril. " +
                        "Dá-me uma biografia curta (50 a 100 palavras) em português de Portugal sobre " + name + aliasesString + " ." +
                        "Considera o contexto do Estado Novo e do 25 de Abril. " +
                        "A biografia deve começar com o nome  seguido do ano de nascimento e de morte (se aplicável, pode não ser uma pessoa), " +
                        "e deve resumir o papel ou relevância histórica da pessoa nesse contexto político. " +
                        "Usa apenas informações verificadas de fontes fiáveis (como arquivos históricos, fontes académicas ou imprensa de referência) e evita especulação. " +
                        "Garante que os dados são factualmente corretos (datas, posições, papéis políticos). Se não souberes, não inventes, diz só 'Não disponível'."));
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
