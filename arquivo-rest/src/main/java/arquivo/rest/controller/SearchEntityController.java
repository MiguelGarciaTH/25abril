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
    public Page<SearchEntity> getSearchEntityBySearchTerm(@RequestParam(required = false, value = "search_term") String searchTerm,
                                                          @RequestParam(required = false, value = "type") String type,
                                                          Pageable pageable) {
        // If searchTerm is empty or null, set it to an empty string for wildcard search
        if ((searchTerm == null || searchTerm.isEmpty()) && (type == null || type.isEmpty()) ) {
            return searchEntityRepository.findAll(pageable);
        } else if((searchTerm == null || searchTerm.isEmpty()) && (type != null || !type.isEmpty())){
            return searchEntityRepository.findAllByType(type, pageable);
        } else {
            // If there is a search term, prepare it for the full-text search (replace spaces with ' & ' for PostgreSQL)
            return searchEntityRepository.findBySearchTermAbdType(searchTerm.replaceAll(" ", " & "), type.toUpperCase(), pageable);
        }
    }

    @GetMapping("/stats")
    public List<SearchEntityRepository.SearchEntityCounter> getSearchEntityCounts(){
        return searchEntityRepository.getSearchEntityCounts();
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
