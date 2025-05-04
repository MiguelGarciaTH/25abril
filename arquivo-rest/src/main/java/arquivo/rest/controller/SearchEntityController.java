package arquivo.rest.controller;

import arquivo.model.SearchEntity;
import arquivo.repository.SearchEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/entity")
public class SearchEntityController {

    private final SearchEntityRepository searchEntityRepository;


    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String normalizeSearchTerm(String input) {
        if (input == null) return "";

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = DIACRITICS.matcher(normalized).replaceAll("");

        return withoutAccents.toLowerCase().trim();
    }

    SearchEntityController(SearchEntityRepository searchEntityRepository) {
        this.searchEntityRepository = searchEntityRepository;
    }

    @GetMapping
    public Page<SearchEntity> getSearchEntityBySearchTerm(@RequestParam(required = false, value = "search_term") String searchTerm,
                                                          @RequestParam(required = false, value = "type") String type,
                                                          Pageable pageable) {

        boolean typeIsSet = false;
        if (type != null && !type.isEmpty()) {
            type = type.toUpperCase();
            typeIsSet = true;
        }
        boolean searchTermIsSet = false;
        String normalized;
        String query = "";
        if (searchTerm != null && !searchTerm.isEmpty()) {
            normalized = normalizeSearchTerm(searchTerm);
            query = Arrays.stream(normalized.split("\\s+"))
                    .map(word -> word + ":*")
                    .collect(Collectors.joining(" & "));
            searchTermIsSet = true;
        }

        // If searchTerm is empty or null, set it to an empty string for wildcard search
        if (typeIsSet && searchTermIsSet) {
            return searchEntityRepository.findBySearchTermAndByType(query, type, pageable);
        }
        if (!typeIsSet && searchTermIsSet) {
            return searchEntityRepository.findBySearchTerm(query, pageable);
        }
        if (typeIsSet) {
            return searchEntityRepository.findAllByType(type, pageable);

        }
        return searchEntityRepository.findAll(pageable);
    }

    @GetMapping("/stats")
    public List<SearchEntityRepository.SearchEntityCounter> getSearchEntityCounts() {
        return searchEntityRepository.getSearchEntityCounts();
    }

    @GetMapping("/type/stats")
    public List<SearchEntityRepository.SearchEntityTypeCounter> getSearchEntityTypeCounts() {
        return searchEntityRepository.getSearchEntityTypeCounts();
    }

    @GetMapping("/{entityId}")
    SearchEntity getSearchEntity(@PathVariable int entityId) {
        return searchEntityRepository.findById(entityId).orElse(null);
    }

    @GetMapping("/types")
    List<String> getSearchEntityTypes() {
        return searchEntityRepository.getSearchEntityTypes();
    }
}
