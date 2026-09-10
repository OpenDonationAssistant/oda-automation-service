package io.github.opendonationassistant.wordblacklist;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class WordFilterRepository {

  private WordFilterDataRepository dataRepository;

  @Inject
  public WordFilterRepository(WordFilterDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  public WordFilter getByRecipientId(String recipientId) {
    final List<String> recipientWords = dataRepository
      .getByRecipientId(recipientId)
      .stream()
      .flatMap(data -> data.words().stream())
      .toList();
    return new WordFilter(dataRepository, recipientId, recipientWords);
  }

  public List<WordFilterData> getWordsByRecipientId(String recipientId) {
    return dataRepository.getByRecipientId(recipientId);
  }
}
