package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Notification.NotificationMessage;
import com.project.societyManagement.entity.Notification;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.NotificationType;
import com.project.societyManagement.queryBuilder.notification.NotificationFilter;
import com.project.societyManagement.queryBuilder.notification.NotificationQueryBuilder;
import com.project.societyManagement.repository.NotificationRepo;
import com.project.societyManagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private NotificationRepo notificationRepo;
    @Autowired
    private NotificationQueryBuilder notificationQueryBuilder;
        public void notifySociety(Long societyId , String title , String message){
            Notification notification = Notification.builder().title(title).read(false).type(NotificationType.SOCIETY).societyId(societyId).message(message).isActive(true).build();
            notificationRepo.save(notification);
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setTitle(title);
        notificationMessage.setMessage(message);
        messagingTemplate.convertAndSend(
                "/topic/society/" + societyId,
                notificationMessage
        );
    }

    public void notifySociety(Long societyId , String title , String message,String url){
        Notification notification = Notification.builder().title(title).read(false).type(NotificationType.SOCIETY).societyId(societyId).message(message).url(url).isActive(true).build();

        notificationRepo.save(notification);
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setTitle(title);
        notificationMessage.setMessage(message);
        notificationMessage.setUrl(url);
        messagingTemplate.convertAndSend(
                "/topic/society/" + societyId,
                notificationMessage
        );
    }

    public void notifyUser(Long userId,String title,String message){

        Notification notification = Notification.builder().title(title).read(false).type(NotificationType.USER).message(message).userId(userId).isActive(true).build();
        notificationRepo.save(notification);
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setTitle(title);
        notificationMessage.setMessage(message);
        messagingTemplate.convertAndSend(
                "/topic/user/"+userId,
                notificationMessage
        );
    }

    public void notifyUser(Long userId,String title,String message,String url){
        Notification notification = Notification.builder().title(title).read(false).type(NotificationType.USER).message(message).userId(userId).isActive(true).url(url).build();
        notificationRepo.save(notification);
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setTitle(title);
        notificationMessage.setMessage(message);
        notificationMessage.setUrl(url);
        messagingTemplate.convertAndSend(
                "/topic/user/"+userId,
                notificationMessage
        );
    }

    public List<Notification> getUserNotification(){
        NotificationFilter notificationFilter = new NotificationFilter();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notificationFilter.setUserId(user.getId());
        return notificationQueryBuilder.search(notificationFilter);
    }


    public List<Notification> getSocietyNotification(){
        NotificationFilter notificationFilter = new NotificationFilter();
        notificationFilter.setSocietyId(TenantContextHolder.getCurrentTenant());
        return notificationQueryBuilder.search(notificationFilter);
    }

    @Override
    public void markAsRead(Long id) {
        NotificationFilter notificationFilter = new NotificationFilter();
        notificationFilter.setId(id);
        notificationFilter.setRead(false);
        Notification notification = notificationQueryBuilder.findById(notificationFilter);
        notification.setRead(true);
        notificationRepo.save(notification);
    }


}
