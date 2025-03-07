package arquivo.rest.controller;

import arquivo.model.SearchEntity;
import arquivo.repository.SearchEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entity")
public class SearchEntityController {

    private final SearchEntityRepository searchEntityRepository;

    SearchEntityController(SearchEntityRepository searchEntityRepository) {
        this.searchEntityRepository = searchEntityRepository;
    }

    @GetMapping
    public Page<SearchEntity> getSearchEntityBySearchTerm(@RequestParam(required = false, value = "search_term") String searchTerm, Pageable pageable) {
        // If searchTerm is empty or null, set it to an empty string for wildcard search
        if (searchTerm == null || searchTerm.isEmpty()) {
            return searchEntityRepository.findAll(pageable);
        } else {
            // If there is a search term, prepare it for the full-text search (replace spaces with ' & ' for PostgreSQL)
            return searchEntityRepository.findBySearchTerm(searchTerm.replaceAll(" ", " & "), pageable);
        }
    }

    @GetMapping("/{entityId}")
    SearchEntity getSearchEntity(@PathVariable int entityId) {
        return searchEntityRepository.findById(entityId).orElse(null);
    }

    @GetMapping("/type/{type}")
    List<SearchEntity> getSearchEntityList(@PathVariable String type) {
        return searchEntityRepository.findAllByType(SearchEntity.Type.valueOf(type).name());
    }

    @GetMapping("/types")
    List<String> getSearchEntityTypesList() {
        return searchEntityRepository.findAllTypes();
    }
}
