package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Event.EventCreationRequest;
import com.project.societyManagement.dto.Event.EventResponse;
import com.project.societyManagement.entity.Event;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.event.EventFilter;
import com.project.societyManagement.queryBuilder.event.EventQueryBuilder;
import com.project.societyManagement.repository.EventRepo;
import com.project.societyManagement.service.EventService;
import com.project.societyManagement.service.NotificationService;
import com.project.societyManagement.service.TenantService;
import com.project.societyManagement.service.UserService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;
    private final EventQueryBuilder eventQueryBuilder;
    private final ModelMapper modelMapper;
    private final TenantService tenantService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ValidationUtil validationUtil;

    public EventResponse createEvent(EventCreationRequest eventRequest){
        validationUtil.validate(eventRequest);
        Event event = modelMapper.map(eventRequest, Event.class);
        Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
        event.setTenant(tenant);
        User user = userService.findUserById(eventRequest.getOrganizedBy());
        event.setOrganizedBy(user);

        event = eventRepo.save(event);
        notificationService.notifySociety(
                event.getTenant().getId(),
                "New Event Announced",
                "A new event has been scheduled: " + event.getName()
                        + ". Please check the event details for more information.","/menu/events/"+event.getId()
        );
        return modelMapper.map(eventQueryBuilder,EventResponse.class);
    }

    public EventResponse updateEvent(Long eventId,EventCreationRequest eventRequest){
        validationUtil.validate(eventRequest);
        Event event = getEventById(eventId);
        event.setName(eventRequest.getName());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setStatus(eventRequest.getStatus());
        event.setStartDateTime(eventRequest.getStartDateTime());
        event.setEndDateTime(eventRequest.getEndDateTime());
        event.setMaxParticipants(eventRequest.getMaxParticipants());
        event.setRegistrationRequired(eventRequest.getRegistrationRequired());
        event = eventRepo.save(event);
        return modelMapper.map(eventQueryBuilder,EventResponse.class);
    }

    public void deleteEvent(Long eventId){
        EventFilter eventFilter = new EventFilter();
        eventFilter.setId(eventId);
        Event event = eventQueryBuilder.findById(eventFilter);
        event.setIsActive(false);
        eventRepo.save(event);
    }

    public Event getEventById(Long eventId){
        EventFilter eventFilter = new EventFilter();
        eventFilter.setId(eventId);
        Event event = eventQueryBuilder.findById(eventFilter);
        return event;
    }

    public Page<Event> getEventsBySociety(Long tenantId , Pageable pageable){
        EventFilter eventFilter = new EventFilter();
        eventFilter.setTenantId(tenantId);
        Page<Event> events = eventQueryBuilder.searchPaginated(eventFilter,pageable);
        return events;
    }

    @Override
    public Page<Event> getEventsByMySociety(Pageable pageable) {
        EventFilter eventFilter = new EventFilter();
        eventFilter.setTenantId(TenantContextHolder.getCurrentTenant());
        Page<Event> events = eventQueryBuilder.searchPaginated(eventFilter,pageable);
        return events;
    }

    public Page<Event> searchEventsPaginated(EventFilter eventFilter,Pageable pageable){
        Page<Event> events = eventQueryBuilder.searchPaginated(eventFilter,pageable);
        return events;
    }
    public String addParticipant(Long eventId , Long userId) {
        Event event = getEventById(eventId);
        User user = userService.findUserById(userId);
        try{
            if (event.getMaxParticipants() > event.getParticipants().size()){
                event.getParticipants().add(user);
                event = eventRepo.save(event);
                return "User have participated in the event.";
            }

            throw new Exception("No of Participants have exceeded the limit");
        } catch (Exception e) {
            return "No of Participants have exceeded the limit";
        }

    }

    public String takeParticipation(Long eventId){
        Event event = getEventById(eventId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       try{
           if (event.getMaxParticipants() > event.getParticipants().size()){
               event.getParticipants().add(user);
               event = eventRepo.save(event);
                return "You have participated in the event.";
           }

           throw new Exception("Number of Participants have exceeded the limit");
       } catch (Exception e) {
           return "No of Participants have exceeded the limit";
       }

    }
    public String removeParticipants(Long eventId , Long userId) {
        Event event = getEventById(eventId);
        User user = userService.findUserById(userId);
   try{
       if (event.getParticipants().contains(user)){
           List<User> participants = event.getParticipants();
           participants.remove(user);
           event.setParticipants(participants);
           event = eventRepo.save(event);
           return "You have been removed from the event.";
       }

       throw new Exception("You are not participating in the event.");
        } catch (Exception e) {

       return "You are not participating in the event.";
        }

    }

    public String removeParticipation(Long eventId)  {
        Event event = getEventById(eventId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.findUserById(user.getId());
        try{

            if (event.getParticipants().contains(user)){
                List<User> participants = event.getParticipants();
                participants.remove(user);
                event.setParticipants(participants);
                event = eventRepo.save(event);
                return "You have been removed from the event.";
            }

            throw new Exception("You are not participating in the event.");
        } catch (Exception e) {

            return "You are not participating in the event.";
        }
    }


    public List<User> getAllParticipantsForEvent(Long eventId){
        Event event = getEventById(eventId);
        return event.getParticipants();
    }

    public Set<Event> getAllEventsForUser(Long userId){
        User user = userService.findUserById(userId);
        return user.getEventsParticipated();
    }

    public Boolean isUserParticipant(Long eventId , Long userId){
        Event event = getEventById(eventId);
        User user = userService.findUserById(userId);
        return  event.getParticipants().contains(user);
    }


}
