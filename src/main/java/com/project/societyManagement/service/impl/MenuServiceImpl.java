package com.project.societyManagement.service.impl;


import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.queryBuilder.menu.MenuFilter;
import com.project.societyManagement.queryBuilder.menu.MenuQueryBuilder;
import com.project.societyManagement.repository.MenuRepo;
import com.project.societyManagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuQueryBuilder menuQueryBuilder;
    @Autowired
    private MenuRepo menuRepo;

    public Menu findMenuById(Long id){
        MenuFilter menuFilter = new MenuFilter();
        menuFilter.setId(id);
        Menu menu = menuQueryBuilder.findById(menuFilter);
        return menu;
    }

    @Override
    public Page<Menu> searchMenu(Pageable pageable) {
        MenuFilter menuFilter = new MenuFilter();
        return menuQueryBuilder.searchPaginated(menuFilter,pageable);
    }



    @Override
    public Menu createMenu(Menu menu){
        return menuRepo.save(menu);
    }

}
