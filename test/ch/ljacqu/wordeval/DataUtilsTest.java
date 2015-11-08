package ch.ljacqu.wordeval;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.gson.Gson;

import static org.hamcrest.Matchers.*;

public class DataUtilsTest {
  
  @Test
  public void shouldInitializeWithDefaultValues() {
    DataUtils dataUtils = new DataUtils();
    
    assertThat(dataUtils.getGson(), instanceOf(Gson.class));
    assertThat(dataUtils.getRoot(), equalTo(""));
  }
  
  @Test
  public void shouldAddSlashToRootWhenMissing() {
    DataUtils dataUtils = new DataUtils("", true);
    assertThat(dataUtils.getRoot(), equalTo(""));
    
    dataUtils = new DataUtils("root");
    assertThat(dataUtils.getRoot(), equalTo("root" + File.separator));
    
    dataUtils = new DataUtils("root/", true);
    assertThat(dataUtils.getRoot(), equalTo("root/"));
  }
  
  @Test
  public void shouldUsePrettyPrintWhenDefined() {
    DataUtils dataUtils = new DataUtils(true);
    String jsonTest = dataUtils.toJson(Arrays.asList("test", "test2"));
    assertThat(StringUtils.countMatches(jsonTest, '\n'), equalTo(3));
  }

}
