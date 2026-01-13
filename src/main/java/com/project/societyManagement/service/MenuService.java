package com.project.societyManagement.service;

import com.project.societyManagement.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MenuService {

    public Menu findMenuById(Long id);

    public Page<Menu> searchMenuPaginated(Pageable pageable);
    public List<Menu> searchMenu();
    public Menu findMenuByName(String menuName);
    public Menu createMenu(Menu menu);
}
