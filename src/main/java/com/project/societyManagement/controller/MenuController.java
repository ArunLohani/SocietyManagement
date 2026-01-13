package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/{id}")
    public Menu getMenuById(@PathVariable Long id){
        return menuService.findMenuById(id);
    }

    @GetMapping("")
    public ResponseEntity<Page<Menu>> getAllMenuPaginated(
                                        @RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "6") Integer limit)
    {
        Pageable pageable = PageRequest.of(page,limit);
        Page<Menu> userPage = menuService.searchMenuPaginated(pageable);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Menu>> getAllMenu()
    {
        List<Menu> menu = menuService.searchMenu();
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "Menus fetched successfully", menu);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Menu>> createMenu(@RequestBody Menu menu){
        Menu createdMenu = menuService.createMenu(menu);
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User fetched successfully", createdMenu);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

}
