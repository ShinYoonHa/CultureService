package com.culture.CultureService.controller;

import com.culture.CultureService.dto.PostFormDto;
import com.culture.CultureService.entity.Post;
import com.culture.CultureService.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PostController {
    private  final PostService postService;

    @GetMapping("/posts")
    public String listPosts(Model model){
        System.out.println("리스트 가져오는중");
        model.addAttribute("posts", postService.getPost());
        return "post_list";
    }
     @GetMapping("/posts/new")
    public String newPostForm(Model model){
         System.out.println("새로운 글 표시중");
        model.addAttribute("postFormDto", new PostFormDto());
        return "post_form";
     }

     @PostMapping("/posts/new")
    public String savePost(@ModelAttribute PostFormDto postFormDto){
         System.out.println("새로운 글 데이터 받음" + postFormDto);
        postService.savePost(postFormDto);
         System.out.println("redirecting,,,");
        return "redirect:/posts";
     }

     @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model){
         System.out.println("글, id 가져오는중" + id);
        Post post = postService.getPostById(id);
         System.out.println("가져온 글" + post);
        model.addAttribute("post",post);
        return "post_detail";
     }
}