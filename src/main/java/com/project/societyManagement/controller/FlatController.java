package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Flat.FlatCreationRequest;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.queryBuilder.flat.FlatFilter;
import com.project.societyManagement.service.FlatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/flat")
@RequiredArgsConstructor
public class FlatController {

        private final FlatService flatService;

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<Flat>> getFlatById(@PathVariable Long id){
            Flat flat = flatService.getFlatById(id);
            ApiResponse<Flat> response = new ApiResponse<>(true,"Flat has been fetched successfully",flat);
            return ResponseEntity.ok(response);
        }

        @PostMapping("")
        public ResponseEntity<ApiResponse<Flat>> createFlat(@RequestBody FlatCreationRequest flatCreationRequest){
            Flat flat = flatService.createFlat(flatCreationRequest);
            ApiResponse<Flat> response = new ApiResponse<>(true,"Flat has been created successfully",flat);
            return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<Flat>> updateFlat(@PathVariable Long id,@RequestBody FlatCreationRequest flatCreationRequest){
            Flat flat = flatService.updateFlat(id,flatCreationRequest);
            ApiResponse<Flat> response = new ApiResponse<>(true,"Flat has been updated successfully",flat);
            return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Flat>> deleteFlat(@PathVariable Long id){
            Flat flat = flatService.deleteFlat(id);
            ApiResponse<Flat> response = new ApiResponse<>(true,"Flat has been deleted successfully",flat);
            return ResponseEntity.ok(response);
        }

        @PostMapping("/search-list")
        public ResponseEntity<ApiResponse<List<Flat>>> searchFlat(@RequestBody FlatFilter filter){
            List<Flat> flats = flatService.searchFlat(filter);
            System.out.println("MEMBER RECEIVED: " + filter.getMember());
            ApiResponse<List<Flat>> response = new ApiResponse<>(true,"Flats has been fetched successfully",flats);
            return ResponseEntity.ok(response);
        }

        @PostMapping("/search")
        public ResponseEntity<Page<Flat>> searchFlatPaginated(@RequestBody FlatFilter filter,
                                                              @RequestParam(defaultValue = "0") Integer pageNumber,
                                                              @RequestParam(defaultValue = "6") Integer pageSize){
            Pageable pageable = PageRequest.of(pageNumber,pageSize);
            Page<Flat> flats = flatService.searchFlatPaginated(filter,pageable);
             return ResponseEntity.ok(flats);
        }
}
