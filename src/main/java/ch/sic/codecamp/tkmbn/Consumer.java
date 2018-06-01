package ch.sic.codecamp.tkmbn;

import java.util.Collection;
import ch.sic.codecamp.tkmbn.domain.Payment;

public interface Consumer {

  void consume(Collection<Payment> payments);
}
