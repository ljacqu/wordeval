package ch.jalu.wordeval;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
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
    DataUtils dataUtils = new DataUtils();
    
    assertThat(dataUtils.getGson(), instanceOf(Gson.class));
    assertThat(dataUtils.getRoot(), equalTo(""));
  }
  
  @Test
  void shouldAddSlashToRootWhenMissing() {
    DataUtils dataUtils = new DataUtils();
    dataUtils.setRoot("");
    assertThat(dataUtils.getRoot(), equalTo(""));
    
    dataUtils.setRoot("root");
    assertThat(dataUtils.getRoot(), equalTo("root" + File.separator));
    
    dataUtils.setRoot("root/");
    assertThat(dataUtils.getRoot(), equalTo("root/"));
  }
  
  @Test
  void shouldUsePrettyPrintWhenDefined() {
    DataUtils dataUtils = new DataUtils(true);
    String jsonTest = dataUtils.toJson(Arrays.asList("test", "test2"));
    assertThat(StringUtils.countMatches(jsonTest, '\n'), equalTo(3));
  }

}
