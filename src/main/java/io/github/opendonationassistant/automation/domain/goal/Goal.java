package io.github.opendonationassistant.automation.domain.goal;

import io.github.opendonationassistant.commons.Amount;

public class Goal {

  private String goalId;
  private String widgetId;
  private String recipientId;
  private String fullDescription;
  private String briefDescription;
  private Amount requiredAmount;
  private Amount accumulatedAmount;
  private Boolean isDefault;

  public Goal(
    String goalId,
    String widgetId,
    String recipientId,
    String fullDescription,
    String briefDescription,
    Amount requiredAmount,
    Amount accumulatedAmount,
    Boolean isDefault
  ) {
    this.goalId = goalId;
    this.widgetId = widgetId;
    this.recipientId = recipientId;
    this.fullDescription = fullDescription;
    this.briefDescription = briefDescription;
    this.requiredAmount = requiredAmount;
    this.accumulatedAmount = accumulatedAmount;
    this.isDefault = isDefault;
  }

  public String getGoalId() {
    return goalId;
  }

  public String getWidgetId() {
    return widgetId;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public String getFullDescription() {
    return fullDescription;
  }

  public String getBriefDescription() {
    return briefDescription;
  }

  public Amount getRequiredAmount() {
    return requiredAmount;
  }

  public Amount getAccumulatedAmount() {
    return accumulatedAmount;
  }

  public Boolean getIsDefault() {
    return isDefault;
  }

  public void setGoalId(String goalId) {
    this.goalId = goalId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
  }

  public void setBriefDescription(String briefDescription) {
    this.briefDescription = briefDescription;
  }

  public void setRequiredAmount(Amount requiredAmount) {
    this.requiredAmount = requiredAmount;
  }

  public void setAccumulatedAmount(Amount accumulatedAmount) {
    this.accumulatedAmount = accumulatedAmount;
  }

  public void setIsDefault(Boolean isDefault) {
    this.isDefault = isDefault;
  }
}
