package com.project.societyManagement.controller;


import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> searchUser(
                                        @RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "6") Integer limit)
    {
        Pageable pageable = PageRequest.of(page,limit);
        Page<Menu> userPage = menuService.searchUser(pageable);
        return ResponseEntity.ok(userPage);
    }



}
