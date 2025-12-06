package com.smalldogg.hospitalsearch.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchViewController {

    @GetMapping("/search")
    public String searchPage() {
        return "search-view";
    }
}
