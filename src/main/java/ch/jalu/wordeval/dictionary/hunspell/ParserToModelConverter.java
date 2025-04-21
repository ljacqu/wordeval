package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.AnyTokenCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.CharSequenceConditions;
import ch.jalu.wordeval.dictionary.hunspell.condition.RegexCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.SingleCharCondition;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixClass;
import org.jheaps.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

/**
 * Converter from {@link ParsedAffixes} to {@link HunspellAffixes}.
 */
@Component
public class ParserToModelConverter {

  public HunspellAffixes convert(ParsedAffixes parsedAffixes) {
    AffixFlagType flagType = firstNonNull(parsedAffixes.getFlagType(), AffixFlagType.SINGLE);
    Map<String, AffixClass> rulesByName = convertAffixClasses(parsedAffixes.getAffixClasses());

    HunspellAffixes affixesDefinition = new HunspellAffixes();
    affixesDefinition.setFlagType(flagType);
    affixesDefinition.setAffixClassesByFlag(rulesByName);
    return affixesDefinition;
  }

  private Map<String, AffixClass> convertAffixClasses(List<ParsedAffixClass> classes) {
    return classes.stream()
        .map(this::convertAffixClass)
        .collect(Collectors.toMap(AffixClass::getFlag, Function.identity()));
  }

  private AffixClass convertAffixClass(ParsedAffixClass parsedClass) {
    AffixClass affixClass = new AffixClass();
    affixClass.setType(parsedClass.type);
    affixClass.setFlag(parsedClass.flag);
    affixClass.setCrossProduct(parsedClass.crossProduct);

    Function<ParsedAffixClass.Rule, AffixClass.AffixRule> ruleConverterFn = parsedClass.type == AffixType.PFX
        ? this::convertPrefixRule
        : this::convertSuffixRule;
    List<AffixClass.AffixRule> convertedRules = parsedClass.rules.stream()
        .map(ruleConverterFn)
        .toList();
    affixClass.getRules().addAll(convertedRules);
    return affixClass;
  }

  private AffixClass.PrefixRule convertPrefixRule(ParsedAffixClass.Rule parsedRule) {
    AffixCondition condition = convertCondition(parsedRule.condition(), AffixType.PFX);
    return new AffixClass.PrefixRule(parsedRule.strip(), parsedRule.affix(), condition);
  }

  private AffixClass.SuffixRule convertSuffixRule(ParsedAffixClass.Rule parsedRule) {
    AffixCondition condition = convertCondition(parsedRule.condition(), AffixType.SFX);
    return new AffixClass.SuffixRule(parsedRule.strip(), parsedRule.affix(), condition);
  }

  @VisibleForTesting
  AffixCondition convertCondition(String pattern, AffixType type) {
    if (".".equals(pattern)) {
      return AnyTokenCondition.INSTANCE;
    }
    AffixCondition condition;
    if (pattern.charAt(0) == '[') {
      condition = SingleCharCondition.createConditionIfApplicable(pattern, type);
    } else {
      condition = CharSequenceConditions.createConditionIfApplicable(pattern, type);
    }

    return condition == null
        ? new RegexCondition(pattern, type)
        : condition;
  }
}
