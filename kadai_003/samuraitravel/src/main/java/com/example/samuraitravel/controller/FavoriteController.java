package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;

@Controller
@RequestMapping("/favorite")
public class FavoriteController {
	
    private final FavoriteService favoriteService;
	
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }
	
//	お気に入り一覧listにアクセスされたときに表示する
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetailsImpl userDetails,
                       @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                       Model model) {
        Integer userId = userDetails.getUser().getId();
        Page<Favorite> favoritePage = favoriteService.getFavoritesByUser(userId, pageable);
        model.addAttribute("favoritePage", favoritePage);
        return "favorite/list";
    }
	
    // お気に入り追加
    @PostMapping("/add")
    public String addFavorite(@RequestParam("houseId") Integer houseId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               RedirectAttributes redirectAttributes) {
        favoriteService.addFavorite(userDetails.getUser().getId(), houseId);
        return "redirect:/houses/" + houseId;
    }
    
    // お気に入り解除
    @PostMapping("/remove")
    public String removeFavorite(@RequestParam("houseId") Integer houseId,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails,
                                  RedirectAttributes redirectAttributes) {
        favoriteService.removeFavorite(userDetails.getUser().getId(), houseId);
        return "redirect:/houses/" + houseId;
    }
    
    

	
	
	

}
