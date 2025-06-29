package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final UserRepository userRepository;

	
		public ReviewController(ReviewRepository reviewRepository, ReviewService reviewService, UserRepository userRepository) {
			this.reviewRepository = reviewRepository;
			this.reviewService = reviewService;
			this.userRepository = userRepository;
	}
	
//	コントローラからビューにデータを渡す場合はModelクラスを使う
	@GetMapping
	public String list(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
		Page<Review> reviewPage = reviewRepository.findAll(pageable);
		
	    House house = null;
	    if (!reviewPage.isEmpty()) {
	        house = reviewPage.getContent().get(0).getHouse(); //reviews内でHouseを使いたい
	    }
		
		model.addAttribute("reviewPage", reviewPage);
		model.addAttribute("house", house);
		
		return "reviews/list";
	}
	
	@GetMapping("/edit")
//	現在ログイン中のユーザー情報を取得
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
//		現在ログイン中の「最新の」ユーザー情報を取得
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		model.addAttribute("user", user);
		
		return "reviews//edit";
	}
	
    @GetMapping("/{reviewId}/edit")
    public String edit(@PathVariable(name = "houseId") Integer houseId,
            @PathVariable(name = "reviewId") Integer reviewId, Model model) {
        Review review = reviewRepository.getReferenceById(reviewId);
        ReviewEditForm reviewEditForm = new ReviewEditForm(
        		review.getId(), review.getHouse().getId(), review.getUser().getId(), review.getRating(), review.getComment());
        
        model.addAttribute("reviewEditForm", reviewEditForm);
        
        return "reviews/edit";
    }
    
	
    @PostMapping("/create")
    public String create(@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm, BindingResult bindingResult,
    						@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {        
        if (bindingResult.hasErrors()) {
            return "reviews/new";
        }
        
        reviewRegisterForm.setUserId(userDetailsImpl.getUser().getId());

//        エラーがない場合はメソッドを実行して登録（引数はフォームクラスのインスタンス）
        reviewService.create(reviewRegisterForm);
        
//        リダイレクト先にデータを渡すメソッド
//        redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");    
        
        return "redirect:/reviews/new";
    } 
    
    @PostMapping("/houses/{houseId}/reviews/{reviewId}/edit")
    public String editReview(@PathVariable Integer houseId, @PathVariable Integer reviewId,
                               @ModelAttribute @Validated ReviewEditForm form, BindingResult bindingResult, Model model,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        if (bindingResult.hasErrors()) {
            return "reviews/edit";
        }

        // 更新処理
        reviewService.edit(form, userDetails.getUser().getId());

        return "redirect:/reviews/edit";
    }
    
    
    @PostMapping("{houseId}/reviews/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, Integer houseId, RedirectAttributes redirectAttributes) {        
        reviewRepository.deleteById(id);
        
        return "redirect:/houses/" + houseId;
    }

	
}
