package ch.sic.codecamp.tkmbn.writer;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ch.sic.codecamp.tkmbn.domain.Payment;

@Repository
public class PaymentDao {

  private static final String SQL_INSERT_PAYMENT = "insert into t_payment ("
      + "  ID,\n"
      + "  UUID,\n"
      + "  IID,\n"
      + "  BIC,\n"
      + "  AMOUNT,\n"
      + "  SUBMISSION_STAMP,\n"
      + "  PAYMENT_STATE,\n"
      + "  MESSAGE,\n"
      + "  ENCRYPTED_MESSAGE)"
      + "  VALUES ("
      + "  :id,"
      + "  :uuid,"
      + "  :iid,"
      + "  :bic,"
      + "  :amount,"
      + "  :submissionStamp,"
      + "  :paymentState,"
      + "  :message,"
      + "  :encryptedMessage)";

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Transactional
  public void insert(Collection<Payment> payments) {
    SqlParameterSource[] parameterSources = new SqlParameterSource[payments.size()];
    int i = 0;
    for (Payment payment : payments) {
      MapSqlParameterSource p = new MapSqlParameterSource();
      p.addValue("id", payment.getId());
      p.addValue("uuid", payment.getUuid());
      p.addValue("iid", payment.getIid());
      p.addValue("bic", payment.getBic());
      p.addValue("amount", payment.getAmount());
      p.addValue("submissionStamp", payment.getSubmissionStamp().toOffsetDateTime().toString());
      p.addValue("paymentState", payment.getPaymentState().toString());
      p.addValue("message", payment.getMessage());
      p.addValue("encryptedMessage", payment.getEncryptedMessage());
      parameterSources[i] = p;
    }

    this.namedParameterJdbcTemplate.batchUpdate(SQL_INSERT_PAYMENT, parameterSources);

  }
}
