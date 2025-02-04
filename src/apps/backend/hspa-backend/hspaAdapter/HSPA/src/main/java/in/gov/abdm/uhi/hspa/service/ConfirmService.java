package in.gov.abdm.uhi.hspa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.uhi.common.dto.Error;
import in.gov.abdm.uhi.common.dto.*;
import in.gov.abdm.uhi.hspa.dto.RequestSharedKeyDTO;
import in.gov.abdm.uhi.hspa.exceptions.UserException;
import in.gov.abdm.uhi.hspa.models.IntermediatePatientAppointmentModel;
import in.gov.abdm.uhi.hspa.models.SharedKeyModel;
import in.gov.abdm.uhi.hspa.models.opemMRSModels.appointment;
import in.gov.abdm.uhi.hspa.service.ServiceInterface.IService;
import in.gov.abdm.uhi.hspa.utils.ConstantsUtils;
import in.gov.abdm.uhi.hspa.utils.IntermediateBuilderUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ConfirmService implements IService {

    private static final String API_RESOURCE_PATIENT = "patient";
    private static final String API_RESOURCE_APPOINTMENT = "appointmentscheduling/appointment";
    private static final String API_RESOURCE_APPOINTMENT_TIMESLOT = "appointmentscheduling/timeslot";
    private static final String API_RESOURCE_APPOINTMENT_TYPE = "appointmentscheduling/appointmenttype?v=custom:uuid,name&q=";
    private static final Logger LOGGER = LogManager.getLogger(ConfirmService.class);
    @Value("${spring.openmrs_baselink}")
    String OPENMRS_BASE_LINK;
    @Value("${spring.openmrs_api}")
    String OPENMRS_API;
    @Value("${spring.openmrs_username}")
    String OPENMRS_USERNAME;
    @Value("${spring.openmrs_password}")
    String OPENMRS_PASSWORD;
    @Value("${spring.gateway_uri}")
    String GATEWAY_URI;
    @Value("${spring.provider_uri}")
    String PROVIDER_URI;
    @Autowired
    WebClient webClient;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    CacheManager cacheManager;
  
    @Autowired
    SaveChatService chatIndb;
    
    @Autowired
    PaymentService paymentService;


    private static Response generateAck(ObjectMapper mapper) {

        String jsonString;
        MessageAck msz = new MessageAck();
        Response res = new Response();
        Ack ack = new Ack();
        ack.setStatus("ACK");
        msz.setAck(ack);
        Error err = new Error();
        res.setError(err);
        res.setMessage(msz);
        return res;
    }

    private static Response generateNack(ObjectMapper mapper, Exception js) {

        MessageAck msz = new MessageAck();
        Response res = new Response();
        Ack ack = new Ack();
        ack.setStatus("NACK");
        msz.setAck(ack);
        Error err = new Error();
        err.setMessage(js.getMessage());
        err.setType("Search");
        res.setError(err);
        res.setMessage(msz);
        return res;
    }

    @Override
    public Mono<Response> processor(String request) {
        new Request();
        Request objRequest;
        Response ack = generateAck(mapper);

        LOGGER.info("Processing::Confirm::Request::" + request);

        try {
            objRequest = new ObjectMapper().readValue(request, Request.class);
            String typeFulfillment = objRequest.getMessage().getOrder().getFulfillment().getType();
            if(typeFulfillment.equalsIgnoreCase("Teleconsultation") || typeFulfillment.equalsIgnoreCase("PhysicalConsultation")) {
                run(objRequest, request);
            } else {
               return Mono.just(new Response());
            }

        } catch (Exception ex) {
            LOGGER.error("Confirm Service process::error::onErrorResume::" + ex);
            ack = generateNack(mapper, ex);

        }

        return Mono.just(ack);
    }

    @Override
    public Mono<String> run(Request request, String s) {

        String paymentStatus = request.getMessage().getOrder().getPayment().getStatus();
        Mono<String> response = Mono.empty();

        if (paymentStatus.equals("PAID")) {
            getPatientDetails(request).zipWith(getAllAppointmentTypes(request))
                    .flatMap(result -> getPatinetandAppointment(result, request))
                    .flatMap(this::createAppointment)
                    .flatMap(result -> callOnConfirm(result, request))
                    .flatMap(this::logResponse)
                    .subscribe();
        }
        else {

            LOGGER.info("Processing::Search::Run::Not Paid!" + request);
            callOnConfirm("", request);
        }



        return response;
    }

    private Mono<String> getPatientDetails(Request request) {

        String abha = request.getMessage().getOrder().getCustomer().getCred();
        String searchEndPoint = OPENMRS_BASE_LINK + OPENMRS_API + API_RESOURCE_PATIENT;
        String searchPatient = "?v=custom:uuid&q=" + abha;

        return webClient.get()
                .uri(searchEndPoint + searchPatient)
                .exchangeToMono(clientResponse -> {
                    return clientResponse.bodyToMono(String.class);
                });
    }

    public Mono<String> getAllAppointmentTypes(Request request) {

        String serviceType = request.getMessage().getOrder().getFulfillment().getType();
        String searchEndPoint = OPENMRS_BASE_LINK + OPENMRS_API + API_RESOURCE_APPOINTMENT_TYPE + serviceType;

        return webClient.get()
                .uri(searchEndPoint)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class));
    }

    private Mono<List<IntermediatePatientAppointmentModel>> getPatinetandAppointment(Tuple2 result, Request request) {

        List<IntermediatePatientAppointmentModel> patientModel = new ArrayList<IntermediatePatientAppointmentModel>();

        try {

            patientModel = IntermediateBuilderUtils.BuildIntermediatePatientAppoitmentObj(result.getT2().toString(), result.getT1().toString(), request.getMessage().getOrder());

        } catch (Exception ex) {
            LOGGER.error("Select service Get Provider Id::error::onErrorResume::" + ex);
        }
        System.out.println("getPatinetandAppointment###############"+patientModel);
        return Mono.just(patientModel);
    }

    private Boolean checkIfSlotAvaiable(Request request)
    {
        AtomicReference<Boolean> isBooked = new AtomicReference<>(true);
        Cache cache = cacheManager.getCache("slotCache");
        Mono<String> existingAppointment = checkValidAppointment(request);
        existingAppointment.flatMap(result -> {
            if (result.contains("\"countOfAppointments\": 1"))
            {
                isBooked.set(false);
            }
            else if (cache.get(request.getMessage().getOrder().getFulfillment().getId()) != null)
            {
                isBooked.set(false);
            }

            return Mono.empty();
        })
                .subscribe();
       return isBooked.get();
    }
    private Mono<String> checkValidAppointment(Request request) {

        String appointmentSlot = request.getMessage().getOrder().getFulfillment().getId();
        String searchEndPoint = OPENMRS_BASE_LINK + OPENMRS_API + API_RESOURCE_APPOINTMENT_TIMESLOT;
        String searchAppointment = "/" + appointmentSlot;

        return webClient.get()
                .uri(searchEndPoint + searchAppointment)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class));
    }

    private Mono<String> createAppointment(List<IntermediatePatientAppointmentModel> collection) {

        if(collection.size() > 0) {
            appointment appointment = IntermediateBuilderUtils.BuildAppointmentModel(collection.get(0));

            String searchEndPoint = OPENMRS_BASE_LINK + OPENMRS_API + API_RESOURCE_APPOINTMENT;
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            return webClient.post()
                    .uri(searchEndPoint)
                    .body(BodyInserters.fromValue(appointment))
                    .exchangeToMono(clientResponse -> {
                        return clientResponse.bodyToMono(String.class);
                    }).log();
        }
        return Mono.just("");
    }

    private Mono<String> callOnConfirm(String result, Request request) {
    	 System.out.println("@@@@@@@@@@@@@@@@@@@@"+result);
    	 String uuid="";
        request.getContext().setAction("on_confirm");
        if(!checkIfSlotAvaiable(request))
        {
            request.getMessage().getOrder().setState("FAILED");
        }
        else if (result.contains("uuid")) {
            request.getMessage().getOrder().setState("CONFIRMED");
            uuid=IntermediateBuilderUtils.getUUID(result);
        } else {
            request.getMessage().getOrder().setState("FAILED");
        }

        if(request.getContext().getConsumerId().equalsIgnoreCase("eua-nha"))
            request.getContext().setProviderUri(PROVIDER_URI);
        else
            request.getContext().setProviderUri("http://121.242.73.124:8084/api/v1");
        
        System.out.println("!!!!!!!!!!!!!!!!!!!!"+uuid);
        System.out.println("############################"+request);
        System.out.println("@@@@@@@@@@@@@@@@@@@@"+result);
        
        

       String patient= request.getMessage().getOrder().getCustomer().getCred();
       String doctor  =request.getMessage().getOrder().getFulfillment().getAgent().getId();
       
       Map<String, String> fulfillmentTagsMap=request.getMessage().getOrder().getFulfillment().getTags();
            
       String key = fulfillmentTagsMap.get("@abdm/gov.in/patient_key");
       
       if(key!=null)
       {
    	   RequestSharedKeyDTO rpk=new RequestSharedKeyDTO();
    	   rpk.setUserName(patient);
    	   rpk.setPublicKey(key);
    	   chatIndb.saveSharedKey(rpk);
       }
       
       List<SharedKeyModel> doctorKey=chatIndb.getSharedKeyDetails(doctor);
       
       
       if(!doctorKey.isEmpty())
       {
    	   SharedKeyModel keydetails= doctorKey.get(0);
    	   request.getMessage().getOrder().getFulfillment().getTags().put("@abdm/gov.in/doctors_key",keydetails.getPublicKey());
       }
       if (result.contains("uuid")) 
       {
       try {
    			paymentService.saveDataInDb(uuid,request,ConstantsUtils.ON_CONFIRM);
    			
    			LOGGER.info("Request sent to on_conform" + request);
    	        WebClient on_webclient = WebClient.create();
    	       
    	        return on_webclient.post()
    	                .uri(request.getContext().getConsumerUri() + "/on_confirm")
    	                .body(BodyInserters.fromValue(request))
    	                .retrieve()
    	                .bodyToMono(String.class)
    	                .retry(3)
    	                .onErrorResume(error -> {
    	                    LOGGER.error("confirm Service call on confirm::" + error);
    	                    return Mono.empty(); //TODO:Add appropriate response
    	                });
    		} catch (JsonProcessingException | UserException e) {
    			// TODO Auto-generated catch block
    			  LOGGER.error(e.getMessage());
    		}
      
       }
       return Mono.empty();	
       
       
    }

     public Mono<String> logResponse(String result) {

        LOGGER.info("OnConfirm::Log::Response::" + result);

        return Mono.just(result);
    }

}

