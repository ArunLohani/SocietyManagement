package com.project.societyManagement.service;

import com.project.societyManagement.dto.Event.EventCreationRequest;
import com.project.societyManagement.dto.Event.EventResponse;
import com.project.societyManagement.entity.Event;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.event.EventFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;

    public interface EventService {
        public EventResponse updateEvent(Long eventId,EventCreationRequest eventRequest);
    public EventResponse createEvent(EventCreationRequest eventRequest);
    public void deleteEvent(Long eventId);
    public Event getEventById(Long eventId);
    public Page<Event> getEventsBySociety(Long tenantId , Pageable pageable);
        public Page<Event> getEventsByMySociety(Pageable pageable);
    public Page<Event> searchEventsPaginated(EventFilter eventFilter,Pageable pageable);
    public String addParticipant(Long eventId , Long userId);
    public String takeParticipation(Long eventId);
    public String removeParticipants(Long eventId , Long userId);
    public String removeParticipation(Long eventId);
    public List<User> getAllParticipantsForEvent(Long eventId);
    public Set<Event> getAllEventsForUser(Long userId);
    public Boolean isUserParticipant(Long eventId , Long userId);
}
