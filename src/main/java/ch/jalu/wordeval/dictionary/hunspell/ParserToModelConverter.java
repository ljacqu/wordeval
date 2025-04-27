package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.AnyTokenCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.CharSequenceConditions;
import ch.jalu.wordeval.dictionary.hunspell.condition.RegexCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.SingleCharCondition;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixClass;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.jheaps.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converter from {@link ParsedAffixes} to {@link HunspellAffixes}.
 */
@Component
public class ParserToModelConverter {

  public HunspellAffixes convert(ParsedAffixes parsedAffixes) {
    HunspellAffixes affixesDefinition = new HunspellAffixes();
    affixesDefinition.setFlagType(parsedAffixes.getFlagType());
    affixesDefinition.setNeedAffixFlag(parsedAffixes.getNeedAffixFlag());
    affixesDefinition.setForbiddenWordClass(parsedAffixes.getForbiddenWordClass());
    affixesDefinition.setAffixRulesByFlag(convertAffixClasses(parsedAffixes.getAffixClasses()));
    return affixesDefinition;
  }

  private ListMultimap<String, AffixRule> convertAffixClasses(List<ParsedAffixClass> classes) {
    ListMultimap<String, AffixRule> rulesByFlag = ArrayListMultimap.create();
    for (ParsedAffixClass parsedClass : classes) {
      List<AffixRule> rules = parsedClass.rules.stream()
          .map(rule -> convertRule(parsedClass, rule))
          .toList();
      rulesByFlag.putAll(parsedClass.flag, rules);
    }
    return rulesByFlag;
  }

  private AffixRule convertRule(ParsedAffixClass parsedClass, ParsedAffixClass.Rule parsedRule) {
    AffixCondition condition = convertCondition(parsedRule.condition(), parsedClass.type);
    if (parsedClass.type == AffixType.PFX) {
      return new AffixRule.PrefixRule(parsedRule.strip(), parsedRule.affix(), parsedRule.continuationClasses(),
          condition, parsedClass.crossProduct);
    } else {
      return new AffixRule.SuffixRule(parsedRule.strip(), parsedRule.affix(), parsedRule.continuationClasses(),
          condition, parsedClass.crossProduct);
    }
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
