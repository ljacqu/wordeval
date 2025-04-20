package ch.jalu.wordeval;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Test for {@link DataUtils}.
 */
class DataUtilsTest {
  
  @Test
  void shouldInitializeWithDefaultValues() {
    // given / when
    DataUtils dataUtils = new DataUtils();

    // then
    assertThat(dataUtils.getGson(), instanceOf(Gson.class));
    assertThat(dataUtils.isJsonPrettyPrint(), equalTo(false));
  }

  @Test
  void shouldUsePrettyPrintWhenDefined() {
    // given
    DataUtils dataUtils = new DataUtils();
    dataUtils.setJsonPrettyPrint(true);

    // when
    String jsonTest = dataUtils.toJson(Arrays.asList("test", "test2"));

    // then
    assertThat(StringUtils.countMatches(jsonTest, '\n'), equalTo(3));
  }
}
