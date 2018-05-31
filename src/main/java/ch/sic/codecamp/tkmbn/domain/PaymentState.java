package ch.sic.codecamp.tkmbn.domain;

public enum PaymentState {
  VALIDATED,
  ACKNOWLEDGED_INBOUND,
  READY_FOR_DELIVERY,
  TO_BE_SETTLED,
  SETTLED,
  CANCELED,
  DELIVERING,
  DELIVERED,
  ACKNOWLEDGED

}
