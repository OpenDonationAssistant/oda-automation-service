package io.github.opendonationassistant.wordblacklist;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class WordFilter {

  public static final String regex =
    "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
  public static final Pattern pattern = Pattern.compile(regex);

  private final WordFilterDataRepository dataRepository;
  private final String recipientId;
  private final List<String> banWords;

  public WordFilter(
    WordFilterDataRepository dataRepository,
    String recipientId,
    List<String> banWords
  ) {
    this.dataRepository = dataRepository;
    this.recipientId = recipientId;
    this.banWords = banWords;
  }

  public WordFilter replaceWords(List<String> words) {
    return new WordFilter(dataRepository, recipientId, words);
  }

  public WordFilterData save() {
    return dataRepository
      .getByRecipientId(recipientId)
      .stream()
      .findFirst()
      .map(existing ->
        dataRepository.update(
          new WordFilterData(existing.id(), recipientId, banWords)
        )
      )
      .orElseGet(() ->
        dataRepository.save(
          new WordFilterData(UUID.randomUUID().toString(), recipientId, banWords)
        )
      );
  }

  public String filter(String text) {
    for (String word : banWords) {
      text = text.replaceAll(word, "***");
    }
    text = pattern.matcher(text).replaceAll(" (ссылка удалена) ");
    text = text.replaceAll("https://", "").replaceAll("http://", "");
    return text;
  }
}
