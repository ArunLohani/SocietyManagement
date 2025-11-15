package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Event.EventCreationRequest;
import com.project.societyManagement.dto.Event.EventResponse;
import com.project.societyManagement.entity.Event;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.event.EventFilter;
import com.project.societyManagement.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@RequestBody EventCreationRequest eventRequest) {
        EventResponse event = eventService.createEvent(eventRequest);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Event created successfully", event),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Event>> getEventById(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event fetched successfully", event));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<Page<Event>>> getEventsForTenant(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "6") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Event> events = eventService.getEventsBySociety(tenantId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Events fetched successfully", events));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<Event>>> searchEvents(
            @RequestBody EventFilter eventFilter,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "6") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Event> events = eventService.searchEventsPaginated(eventFilter, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Events search successful", events));
    }

    @PostMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<ApiResponse<String>> addParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        String message = eventService.addParticipant(eventId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, message, message));
    }

    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<ApiResponse<String>> removeParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        String message = eventService.removeParticipants(eventId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, message, message));
    }

    @PostMapping("/{eventId}/participate")
    public ResponseEntity<ApiResponse<String>> takeParticipation(@PathVariable Long eventId) {
        String message = eventService.takeParticipation(eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, message, message));
    }

    @DeleteMapping("/{eventId}/participate")
    public ResponseEntity<ApiResponse<String>> removeParticipation(@PathVariable Long eventId) {
        String message = eventService.removeParticipation(eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, message, message));
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<ApiResponse<List<User>>> getAllParticipants(@PathVariable Long eventId) {
        List<User> users = eventService.getAllParticipantsForEvent(eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Participants fetched successfully", users));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Set<Event>>> getAllEventsForUser(@PathVariable Long userId) {
        Set<Event> events = eventService.getAllEventsForUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User events fetched successfully", events));
    }

    @GetMapping("/user/{userId}/{eventId}/participation")
    public ResponseEntity<ApiResponse<Boolean>> isUserParticipant(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        Boolean isParticipant = eventService.isUserParticipant(eventId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User participation status fetched", isParticipant));
    }
}
