package in.gov.abdm.uhi.hspa.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import in.gov.abdm.uhi.hspa.utils.ConstantsUtils;
import lombok.Data;

@Entity
@Table(schema = ConstantsUtils.HSPA_SCHEMA_NAME, name = "Payments")
@Data
public class PaymentsModel {
	
    @Id
    @Column(name = "transaction_id")
    private String transactionId;
    private String method;
    private String currency;
    @Column( name = "transaction_time_stamp")
    private String transactionTimestamp;
    @Column(name = "consultation_charge")
    private String consultationCharge;
    @Column(name = "phr_handling_fees")
    private String phrHandlingFees;
    private String sgst;
    private String cgst;
    @Column(name = "transaction_state")
    private String transactionState;

    @Column(name = "healthIdNumber")
    private String UserAbhaId;

}
