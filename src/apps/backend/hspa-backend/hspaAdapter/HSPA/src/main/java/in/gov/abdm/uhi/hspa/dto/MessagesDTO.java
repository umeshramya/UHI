package in.gov.abdm.uhi.hspa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessagesDTO extends ServiceResponseDTO {
    private String contentId;
    private String sender;
    private String receiver;
    private String consumerUrl;
    private String providerUrl;
    private String contentValue;
    private String contentType;
    private String contentUrl;
    private String time;
    private String userName;
    private String image;
}
