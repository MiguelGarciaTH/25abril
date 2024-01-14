package arquivo.rest.controller;

import arquivo.model.SearchEntity;
import arquivo.repository.SearchEntityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entity")
public class SearchEntityController {

    private final SearchEntityRepository searchEntityRepository;

    SearchEntityController(SearchEntityRepository searchEntityRepository) {
        this.searchEntityRepository = searchEntityRepository;
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

    @GetMapping
    List<SearchEntity> getSearchEntityList() {
        return searchEntityRepository.findAllByOrderByIdDesc();
    }
}
