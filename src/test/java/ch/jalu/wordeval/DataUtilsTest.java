package ch.jalu.wordeval;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.gson.Gson;

public class DataUtilsTest {
  
  @Test
  public void shouldInitializeWithDefaultValues() {
    DataUtils dataUtils = new DataUtils();
    
    assertThat(dataUtils.getGson(), instanceOf(Gson.class));
    assertThat(dataUtils.getRoot(), equalTo(""));
  }
  
  @Test
  public void shouldAddSlashToRootWhenMissing() {
    DataUtils dataUtils = new DataUtils();
    dataUtils.setRoot("");
    assertThat(dataUtils.getRoot(), equalTo(""));
    
    dataUtils.setRoot("root");
    assertThat(dataUtils.getRoot(), equalTo("root" + File.separator));
    
    dataUtils.setRoot("root/");
    assertThat(dataUtils.getRoot(), equalTo("root/"));
  }
  
  @Test
  public void shouldUsePrettyPrintWhenDefined() {
    DataUtils dataUtils = new DataUtils(true);
    String jsonTest = dataUtils.toJson(Arrays.asList("test", "test2"));
    assertThat(StringUtils.countMatches(jsonTest, '\n'), equalTo(3));
  }

}
