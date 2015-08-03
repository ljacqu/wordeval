package main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class VowelService {

	private VowelService() { }
	
	public static List<Character> getStandardVowels() {
		Character[] charArray = {'a', 'e', 'i', 'o', 'u', 'y'};
		return new ArrayList<Character>(Arrays.asList(charArray));
	}
	
	public static List<Character> getExtendedVowels() {
		List<Character> list = getStandardVowels();
		list.add('y');
		List<Character> variations = new ArrayList<>();
		for (char c : list) {
			variations.addAll(variationsFor(c));
		}
		list.addAll(variations);
		return list;
	}
	
	public static List<Character> variationsFor(Character c) {
		Character[] a = {'â', 'ä', 'à', 'á', 'ã', 'å'};
		Character[] e = {'ê', 'ë', 'è', 'é'};
		Character[] i = {'î', 'ï', 'ì', 'í'};
		Character[] o = {'ô', 'ö', 'ò', 'ó', 'õ'};
		Character[] u = {'û', 'ü', 'ù', 'ú'};
		Character[] y = {'ÿ'};
		
		Map<Character, Character[]> variations = new HashMap<>();
		variations.put('a', a);
		variations.put('e', e);
		variations.put('i', i);
		variations.put('o', o);
		variations.put('u', u);
		variations.put('y', y);
		
		if (variations.get(c) != null) {
			return new ArrayList<Character>(Arrays.asList(variations.get(c)));
		}
		return new ArrayList<Character>();
	}
	
}
