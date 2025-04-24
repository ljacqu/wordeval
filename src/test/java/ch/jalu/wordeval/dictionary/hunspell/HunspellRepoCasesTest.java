package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.config.BaseConfiguration;
import ch.jalu.wordeval.dictionary.hunspell.parser.AffixesParser;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringJUnitConfig(classes = BaseConfiguration.class)
public class HunspellRepoCasesTest {

  @Autowired
  private HunspellUnmuncherService hunspellUnmuncherService;

  @Autowired
  private AffixesParser affixesParser;

  @Autowired
  private ParserToModelConverter parserToModelConverter;

  @ParameterizedTest(name = "{0}")
  @MethodSource("getTestCases")
  void test(String name, Path affFile) throws IOException {
    // given
    Path dicFile = affFile.getParent().resolve(affFile.getFileName().toString().replace(".aff", ".dic"));
    Path goodFile = affFile.getParent().resolve(affFile.getFileName().toString().replace(".aff", ".good"));
    assumeTrue(Files.exists(goodFile), "No .good file");

    Charset charset = determineCharset(affFile);

    HunspellAffixes affixDefinitions;
    try (Stream<String> affLines = Files.lines(affFile, charset)) {
      ParsedAffixes parsedAffixes = affixesParser.parseAffFile(affLines);
      affixDefinitions = parserToModelConverter.convert(parsedAffixes);
    }

    // when
    Set<String> result;
    try (Stream<String> dicLines = Files.lines(dicFile, charset)) {
      result = hunspellUnmuncherService.unmunch(dicLines.skip(1), affixDefinitions)
          .collect(Collectors.toCollection(TreeSet::new));
    }

    // then
    Set<String> expected = new TreeSet<>(Files.readAllLines(goodFile, charset));
    assertThat(result, equalTo(expected));
  }

  static Charset determineCharset(Path affFile) throws IOException {
    String filename = affFile.getFileName().toString();
    if (filename.startsWith("german") || filename.equals("condition.aff") || filename.startsWith("checksharps")) {
      return StandardCharsets.ISO_8859_1;
    }

    return Files.readAllLines(affFile).stream().anyMatch(l -> l.startsWith("SET UTF-8"))
        ? StandardCharsets.UTF_8
        : StandardCharsets.ISO_8859_1;
  }

  static Stream<Arguments> getTestCases() throws IOException {
    Path folder = Paths.get("./hunspell-test/");

    return Files.list(folder)
        .filter(f -> Files.isRegularFile(f))
        .filter(f -> f.getFileName().toString().endsWith(".aff"))
        .map(f -> Arguments.of(f.getFileName().toString(), f));
  }
}
