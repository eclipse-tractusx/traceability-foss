package notification.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchableNotificationsRequestTest {
    @Test
    void givenNoSizeGiven_whenConstructingRecord_thenHasDefaultSizeValue() {
        // given
        SearchableNotificationsRequest request = new SearchableNotificationsRequest("", null, null, null);

        // then
        assertThat(request.size()).isEqualTo(200);
    }
}
