package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.AnyTokenCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.CharSequenceConditions;
import ch.jalu.wordeval.dictionary.hunspell.condition.RegexCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.SingleCharCondition;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedRule;
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
    Map<String, AffixRule> rulesByName = convertRules(parsedAffixes.getRules());

    HunspellAffixes affixesDefinition = new HunspellAffixes();
    affixesDefinition.setFlagType(flagType);
    affixesDefinition.setAffixRulesByName(rulesByName);
    return affixesDefinition;
  }

  private Map<String, AffixRule> convertRules(List<ParsedRule> rules) {
    return rules.stream()
        .map(this::convertRule)
        .collect(Collectors.toMap(AffixRule::getFlag, Function.identity()));
  }

  private AffixRule convertRule(ParsedRule rule) {
    AffixRule affixRule = new AffixRule();
    affixRule.setType(rule.type);
    affixRule.setFlag(rule.flag);
    affixRule.setCrossProduct(rule.crossProduct);

    for (ParsedRule.RuleEntry ruleEntry : rule.rules) {
      AffixCondition condition = convertCondition(ruleEntry.condition, rule.type);
      AffixRule.AffixRuleEntry affixRuleEntry = rule.type == AffixType.PFX
          ? new AffixRule.PrefixRuleEntry(condition, ruleEntry.strip, ruleEntry.append)
          : new AffixRule.SuffixRuleEntry(condition, ruleEntry.strip, ruleEntry.append);
      affixRule.getRules().add(affixRuleEntry);
    }
    return affixRule;
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
