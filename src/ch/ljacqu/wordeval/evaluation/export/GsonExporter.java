package ch.ljacqu.wordeval.evaluation.export;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import lombok.Getter;

class GsonExporter {
  
  @Getter
  private static final Gson gson = initGson();

  private static Gson initGson() {
    GsonBuilder builder = new GsonBuilder();
    final TreeElementSerializer serializer = new TreeElementSerializer();

    for (Class<?> clazz : TreeElement.class.getDeclaredClasses()) {
      if (TreeElement.class.isAssignableFrom(clazz)) {
        builder.registerTypeAdapter(clazz, serializer);
      }
    }
    return builder.create();
  }

  public static class TreeElementSerializer implements JsonSerializer<TreeElement> {
    @Override
    public JsonElement serialize(TreeElement src, Type typeOfSrc, JsonSerializationContext context) {
      return gson.toJsonTree(src.getValue());
    }
  }

}