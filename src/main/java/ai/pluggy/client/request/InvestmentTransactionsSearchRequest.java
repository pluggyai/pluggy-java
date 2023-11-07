package ai.pluggy.client.request;

import static ai.pluggy.utils.Asserts.assertNotNull;

import java.util.HashMap;

public class InvestmentTransactionsSearchRequest extends HashMap<String, Object> {

    /**
     * @param page Integer - page number to fetch, starting at page=1.
     * @return this instance, useful to continue adding params
     */
    public InvestmentTransactionsSearchRequest page(Integer page) {
        assertNotNull(page, "page");
        put("page", page);
        return this;
    }

    /**
     * @param pageSize Integer - page size value, indicates max items to fetch per
     *                 page.
     * @return this instance, useful to continue adding params
     */
    public InvestmentTransactionsSearchRequest pageSize(Integer pageSize) {
        assertNotNull(pageSize, "pageSize");
        put("pageSize", pageSize);
        return this;
    }

    public Integer getPage() {
        if (!containsKey("page")) {
            return null;
        }
        return (Integer) get("page");
    }

    public Integer getPageSize() {
        if (!containsKey("pageSize")) {
            return null;
        }
        return (Integer) get("pageSize");
    }
}
