package arquivo.rest.controller;


import arquivo.model.Site;
import arquivo.repository.SiteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/site")
public class SiteController {

    private final SiteRepository siteRepository;

    SiteController(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @GetMapping("/{siteId}")
    Site getSite(@PathVariable int siteId) {
        return siteRepository.findById(siteId).orElse(null);
    }

    @GetMapping
    List<Site> getSiteList() {
        return siteRepository.findAllByOrderByIdDesc();
    }
}
