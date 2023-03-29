package in.gov.abdm.FcmNotification.Notification.service;

import in.gov.abdm.FcmNotification.Notification.dto.PushNotificationRequestDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {

    private final Logger logger = LogManager.getLogger(PushNotificationService.class);

    private final FCMService fcmService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }


    public void sendPushNotificationToToken(PushNotificationRequestDTO request) throws ExecutionException, InterruptedException {
            fcmService.sendMessageToToken(request);     

    }
}
   