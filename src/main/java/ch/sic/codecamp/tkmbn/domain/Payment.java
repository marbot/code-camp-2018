package ch.sic.codecamp.tkmbn.domain;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Payment {

  private final Long id;

  private final String uuid;

  private final String iid;

  private final String bic;

  private final BigDecimal amount;

  private final ZonedDateTime submissionStamp;

  private final PaymentState paymentState;

  private final String message;

  private final String encryptedMessage;

  public Payment(Long id, String uuid, String iid, String bic, BigDecimal amount, ZonedDateTime submissionStamp, PaymentState paymentState, String message, String encryptedMessage) {
    this.id = id;
    this.uuid = uuid;
    this.iid = iid;
    this.bic = bic;
    this.amount = amount;
    this.submissionStamp = submissionStamp;
    this.paymentState = paymentState;
    this.message = message;
    this.encryptedMessage = encryptedMessage;
  }

  public Long getId() {
    return this.id;
  }

  public String getUuid() {
    return this.uuid;
  }

  public String getIid() {
    return this.iid;
  }

  public String getBic() {
    return this.bic;
  }

  public BigDecimal getAmount() {
    return this.amount;
  }

  public ZonedDateTime getSubmissionStamp() {
    return this.submissionStamp;
  }

  public PaymentState getPaymentState() {
    return this.paymentState;
  }

  public String getMessage() {
    return this.message;
  }

  public String getEncryptedMessage() {
    return this.encryptedMessage;
  }
}
