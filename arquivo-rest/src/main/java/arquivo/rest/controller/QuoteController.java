package arquivo.rest.controller;

import arquivo.model.Quote;
import arquivo.repository.QuoteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/quotes")
public class QuoteController {

    private final QuoteRepository quoteRepository;
    private final Random random = new Random();

    QuoteController(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @GetMapping
    Quote getQuote() {
        List<Quote> quotes = quoteRepository.findAll();
        int randomIndex = random.nextInt(quotes.size());
        return quotes.get(randomIndex);
    }
}
