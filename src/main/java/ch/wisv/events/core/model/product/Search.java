package ch.wisv.events.core.model.product;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Search Object.
 * Object for search API.
 */
@Data
@JsonAutoDetect
public class Search {

    /**
     * Query used for searching.
     */
    public String query;

    /**
     * Collection of suggested items.
     */
    public Collection<SearchItem> suggestions;

    /**
     * Default Constructor.
     *
     * @param query Search query
     */
    public Search(String query) {
        this.query = query;
        this.suggestions = new ArrayList<>();
    }

    /**
     * Add suggestion item to Search.
     *
     * @param title Title of the Product
     * @param key   Key of the Product
     */
    public void addItem(String title, Integer key) {
        SearchItem temp = new SearchItem(title, key);
        this.suggestions.add(temp);
    }
}