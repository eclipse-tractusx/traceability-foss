package assets.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchableAssetsRequestTest {
    @Test
    void givenNoSizeGiven_whenConstructingRecord_thenHasDefaultSizeValue() {
        // given
        SearchableAssetsRequest request = new SearchableAssetsRequest("", null, null, null, null);

        // then
        assertThat(request.size()).isEqualTo(200);
    }
}
