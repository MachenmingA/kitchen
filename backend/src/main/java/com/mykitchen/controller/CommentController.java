package com.mykitchen.controller;

import com.mykitchen.entity.Comment;
import com.mykitchen.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/recipe/{recipeId}")
    public Result<List<Comment>> getComments(@PathVariable Long recipeId) {
        return Result.success(commentService.getCommentsByRecipeId(recipeId));
    }

    @GetMapping("/recent")
    public Result<List<Comment>> getRecentComments(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(commentService.getRecentComments(limit));
    }

    @PostMapping
    public Result<Comment> addComment(@RequestBody Comment comment) {
        return Result.success(commentService.addComment(comment));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        commentService.deleteComment(id, userId);
        return Result.success(null);
    }
}
