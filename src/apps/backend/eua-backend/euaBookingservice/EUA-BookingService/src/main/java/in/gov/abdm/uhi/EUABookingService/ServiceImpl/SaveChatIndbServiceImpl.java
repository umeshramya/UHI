package in.gov.abdm.uhi.EUABookingService.serviceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import in.gov.abdm.uhi.EUABookingService.constants.ConstantsUtils;
import in.gov.abdm.uhi.EUABookingService.dto.ErrorResponseDTO;
import in.gov.abdm.uhi.EUABookingService.dto.MessagesDTO;
import in.gov.abdm.uhi.EUABookingService.dto.RequestSharedKeyDTO;
import in.gov.abdm.uhi.EUABookingService.dto.RequestTokenDTO;
import in.gov.abdm.uhi.EUABookingService.entity.ChatUser;
import in.gov.abdm.uhi.EUABookingService.entity.Messages;
import in.gov.abdm.uhi.EUABookingService.entity.SharedKey;
import in.gov.abdm.uhi.EUABookingService.entity.UserToken;
import in.gov.abdm.uhi.EUABookingService.notification.PushNotificationRequest;
import in.gov.abdm.uhi.EUABookingService.notification.PushNotificationResponse;
import in.gov.abdm.uhi.EUABookingService.notification.PushNotificationService;
import in.gov.abdm.uhi.EUABookingService.repository.ChatUserReposotory;
import in.gov.abdm.uhi.EUABookingService.repository.MessagesRepository;
import in.gov.abdm.uhi.EUABookingService.repository.SharedKeyRepository;
import in.gov.abdm.uhi.EUABookingService.repository.UserTokenRepository;
import in.gov.abdm.uhi.EUABookingService.service.ChatDataDbService;
import in.gov.abdm.uhi.common.dto.Ack;
import in.gov.abdm.uhi.common.dto.Error;
import in.gov.abdm.uhi.common.dto.MessageAck;
import in.gov.abdm.uhi.common.dto.Person;
import in.gov.abdm.uhi.common.dto.Request;
import in.gov.abdm.uhi.common.dto.Response;
import reactor.core.publisher.Mono;

@Repository
public class SaveChatIndbServiceImpl implements ChatDataDbService {
	Logger LOGGER = LogManager.getLogger(SaveChatIndbServiceImpl.class);

	@Autowired
	WebClient webclient;

	@Autowired
	MessagesRepository messagesRepo;
	
	@Autowired
	ChatUserReposotory chatUserRepo;

	@Autowired
	SharedKeyRepository sharedKeyRepo;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	UserTokenRepository userTokenRepo;
	
	@Autowired
	 private PushNotificationService pushNotificationService;
	    
	    public SaveChatIndbServiceImpl(PushNotificationService pushNotificationService) {
	        this.pushNotificationService = pushNotificationService;
	    }
	
	public Messages saveChatDataInDb(Request request) {
		   Messages messagesSaved = null;
		   try {
		      messagesSaved = saveMessage(request);
		      saveSenderAndReceiver(request);
		   } catch (NullPointerException e) {
		      LOGGER.error(request.getContext().getMessageId() + "  Null pointer Exception  " + e);

		   } catch (Exception e) {
		      LOGGER.error(request.getContext().getMessageId() + "  Something went wrong  " + e);

		   }

		   return messagesSaved;
		}

	private void saveSenderAndReceiver(Request request) throws Exception {
		   ChatUser sender = getSenderOrReceiver(request.getMessage().getIntent().getChat().getSender().getPerson());
		   ChatUser receiver = getSenderOrReceiver(request.getMessage().getIntent().getChat().getReceiver().getPerson());
		   List<ChatUser> user=new ArrayList<>();
		   user.add(receiver);
		   user.add(sender);
		   List<ChatUser> saveAll = chatUserRepo.saveAll(user);
		   if(saveAll.isEmpty())
		      throw new Exception("Error occurred while saving data");
		}

		private ChatUser getSenderOrReceiver(Person request) {
		   ChatUser sender=new ChatUser();
		   sender.setUserId(request.getCred());
		   sender.setUserName(request.getName());
		   sender.setImage(request.getImage());
		   return sender;
		}

		private Messages saveMessage(Request request) {
		   Messages m = new Messages();
		   String contentType=request.getMessage().getIntent().getChat().getContent().getContent_type();
		   m.setContentId(request.getMessage().getIntent().getChat().getContent().getContent_id());
		   
		   if(contentType.equalsIgnoreCase(ConstantsUtils.TEXT))
		   m.setContentValue(request.getMessage().getIntent().getChat().getContent().getContent_value());
		   
		   m.setContentType(contentType);
		   m.setContentUrl(request.getMessage().getIntent().getChat().getContent().getContent_url());
		   m.setReceiver(request.getMessage().getIntent().getChat().getReceiver().getPerson().getCred());
		   m.setSender(request.getMessage().getIntent().getChat().getSender().getPerson().getCred());
		   m.setTime(getLocalDateTimeFromString(request));
		   m.setConsumerUrl(request.getContext().getConsumerUri());
		   m.setProviderUrl(request.getContext().getProviderUri());
		   return messagesRepo.save(m);
		}
	
	private LocalDateTime getLocalDateTimeFromString(Request request) {
		DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		String time = request.getMessage().getIntent().getChat().getTime().getTimestamp();
		LocalDateTime dateParsed = LocalDateTime.parse(time, ofPattern);
		return dateParsed;
	}

	@Override
	public List<Messages> getMessageDetails(Integer pageNumber, Integer pageSize) {
		Pageable p = PageRequest.of(pageNumber, pageSize, Sort.by("time").ascending());
		Page<Messages> findAll2 = messagesRepo.findAll(p);
		List<Messages> findAll = findAll2.getContent();
		return findAll;
	}

	@Override
	public List<Messages> getMessagesBetweenTwo(String sender, String receiver, Integer pageNumber, Integer pageSize) {

		Pageable p = PageRequest.of(pageNumber, pageSize, Sort.by("time").ascending());
		Page<Messages> findBySenderAndReceiver = messagesRepo.findBySenderAndReceiver(sender, receiver, p);
		List<Messages> SenderAndReceiver = findBySenderAndReceiver.getContent();
		Page<Messages> findBySenderAndReceiver1 = messagesRepo.findBySenderAndReceiver(receiver, sender, p);
		List<Messages> SenderAndReceiver1 = findBySenderAndReceiver1.getContent();
		List<Messages> combination = new ArrayList<Messages>();
		combination.addAll(SenderAndReceiver);
		combination.addAll(SenderAndReceiver1);
		return combination;
	}

	@Override
	public String concatReceiverSender(String receiver, String sender) {
		return receiver + "|" + sender;
	}

	public ResponseEntity<Response> sendErrorIfProviderUriAndDataIsNull(Request request,
			Messages saveDataInDb) {
		if (applyDataValidation(request, saveDataInDb)) {
			return ResponseEntity.status(HttpStatus.OK).body(createAcknowledgementTO());
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createNacknowledgementTO(null));
		}
	}

	public ResponseEntity<Mono<Response>> checkIfDataIsNullAndCallHspa(Request request,
			Messages saveDataInDb) {
		if (applyDataValidation(request, saveDataInDb)) {
			request.getContext().setAction(ConstantsUtils.ON_MESSAGE);
			Mono<Response> onErrorResume = webClientCall(request);
			onErrorResume.subscribe(e->LOGGER.info("INSIDE SUBSCRIBE "));
			
			return ResponseEntity.status(HttpStatus.OK).body(Mono.just(createAcknowledgementTO()));
		} else {
			Response createNacknowledgementTO = createNacknowledgementTO(null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Mono.just(createNacknowledgementTO));
		}
	}

	public Boolean applyDataValidation(Request request, Messages saveDataInDb) {
		if (saveDataInDb != null ) {			
			return true;

		} else {
			return false;
		}
	}

	public Mono<Response> webClientCall(Request request) {
		Mono<Response> onErrorResume = webclient.post()
				.uri(request.getContext().getProviderUri() + "/" + ConstantsUtils.ON_MESSAGE)
				.body(BodyInserters.fromValue(request)).retrieve().bodyToMono(Response.class)
				.onErrorResume(error -> {
					LOGGER.error("Unable to call hspa :error::onErrorResume::" + error);
					return Mono.empty();
				});
		return onErrorResume;
	
	}

	public Response createAcknowledgementTO() {
		Ack ack = new Ack("ACK");
		MessageAck ackMessage = new MessageAck(ack);
		return new Response(ackMessage, null);
	}

	public Response createNacknowledgementTO(String error) {
		Error err= new Error();
		err.setMessage(error);	
		Ack ack = new Ack("NACK");
		MessageAck ackMessage = new MessageAck(ack);
		return new Response(ackMessage, err);
	}
	
	public List<MessagesDTO> getErrorMessage(String message) {
	    MessagesDTO messagesDTO = new MessagesDTO();	    
	    ErrorResponseDTO errorResponse =  new ErrorResponseDTO();
	    errorResponse.setErrorString(message);
	    errorResponse.setCode("500");
	    errorResponse.setPath("getMessagesBetweenTwo");
	    messagesDTO.setError(errorResponse);
	    List<MessagesDTO> messagesDTOS = new ArrayList<>();
	    messagesDTOS.add(messagesDTO);
	    return messagesDTOS;
	}
	
	public List<MessagesDTO> convertToMessageDto(List<Messages> getMessageDetails) {	  
	    return modelMapper.map(getMessageDetails, new TypeToken<List<MessagesDTO>>() {}.getType());
	}

	@Override
	public List<ChatUser> getUserdetails(String userId) {
		List<ChatUser> findByUserId = chatUserRepo.findByUserId(userId);
		return findByUserId;
	}

	@Override
	public List<ChatUser> getAllUsers() {
		List<ChatUser> findAllUsers= chatUserRepo.findAll();
		return findAllUsers;
	}

	@Override
	public UserToken saveUserToken(RequestTokenDTO requesttoken) {
		
		UserToken ut=new UserToken();
		String userid=requesttoken.getUserName()+"|"+requesttoken.getDeviceId();
		ut.setUserId(userid);
		ut.setUserName(requesttoken.getUserName());
		ut.setToken(requesttoken.getToken());
		ut.setDeviceId(requesttoken.getDeviceId());
		UserToken save = userTokenRepo.save(ut);		
		return save;
	}

	@Override
	public List<UserToken> getAllUserToken() {
		return userTokenRepo.findAll();
		
	}

	@Override
	public List<UserToken> getUserTokenByName(String userName) {
		return  userTokenRepo.findByUserName(userName);
		
	}

	@Override
	public void sendNotificationToreceiver(Request request) throws InterruptedException, ExecutionException {		
		String receiver=request.getMessage().getIntent().getChat().getReceiver().getPerson().getCred();
		String sender =request.getMessage().getIntent().getChat().getSender().getPerson().getCred();
		String contentType=request.getMessage().getIntent().getChat().getContent().getContent_type();
		List<UserToken> userTokenByName = getUserTokenByName(receiver);
		String userName=receiver+"|"+sender;
		List<SharedKey> lsk=getKeyDetails(userName);
		String sharedKey="";
		if(!lsk.isEmpty())
		{
			sharedKey=lsk.get(0).getPublicKey();
		}
	for(UserToken token:userTokenByName){		
		if(token.getToken()!=null)
		{			
			if(isValidFCMToken(token.getToken()))
				{				
					PushNotificationRequest pushnot=new PushNotificationRequest();
					pushnot.setTitle(request.getMessage().getIntent().getChat().getSender().getPerson().getName());
					if(contentType.equalsIgnoreCase(ConstantsUtils.MEDIA))
					{
						pushnot.setMessage(request.getMessage().getIntent().getChat().getContent().getContent_url());
					}else {
						pushnot.setMessage(request.getMessage().getIntent().getChat().getContent().getContent_value());	
					}
					pushnot.setSenderAbhaAddress(sender);
					pushnot.setGender(request.getMessage().getIntent().getChat().getSender().getPerson().getGender());
					pushnot.setReceiverAbhaAddress(receiver);
					pushnot.setProviderUri(request.getContext().getProviderUri());
					pushnot.setType(ConstantsUtils.CHAT);
					pushnot.setToken(token.getToken());			
					pushnot.setSharedKey(sharedKey);
					pushnot.setContentType(contentType);
					pushNotificationService.sendPushNotificationToToken(pushnot);
					pushnot=null;
				}
		}
		}	
	}
	public Boolean isValidFCMToken(String fcmToken) {
        Message message = Message.builder().setToken(fcmToken).build();
        try {
            FirebaseMessaging.getInstance().send(message);
            return true;
        } catch (FirebaseMessagingException fme) {
        	LOGGER.error("Firebase token verification exception");
            return false;
        }
    }
	
	@Transactional
	@Override
	public ResponseEntity<PushNotificationResponse> deleteToken(RequestTokenDTO tokenDTO) {
		 
		   Integer userTokenModel = userTokenRepo.deleteByUserId(tokenDTO.getUserName()+"|"+ tokenDTO.getDeviceId());
		   if (null != userTokenModel) {
			   return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "token deleted"), HttpStatus.OK);
		   }
		   throw new RuntimeException("Error logging out. Either Token not found or some error occurred");
	}

	@Override
	public SharedKey saveSharedKey(RequestSharedKeyDTO request) {
		if(request.getPublicKey()!=null)
		{
		SharedKey sk=new SharedKey();
		sk.setUserName(request.getUserName());
		sk.setPublicKey(request.getPublicKey());		
		sk.setPrivateKey(request.getPrivateKey());
		
			List<SharedKey> skl=getKeyDetails(request.getUserName());
			if(skl.isEmpty())
			{
				return sharedKeyRepo.save(sk);
			}
			return skl.get(0);
		}
		else
			return null;
	}

	@Override
	public List<SharedKey> getKeyDetails(String userName) {
		return sharedKeyRepo.findByUserName(userName);
	}



}