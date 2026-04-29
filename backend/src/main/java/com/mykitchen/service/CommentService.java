package com.mykitchen.service;

import com.mykitchen.entity.Comment;
import com.mykitchen.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> getCommentsByRecipeId(Long recipeId) {
        return commentMapper.findByRecipeId(recipeId);
    }

    public List<Comment> getRecentComments(int limit) {
        return commentMapper.findRecent((long) limit);
    }

    public Comment addComment(Comment comment) {
        commentMapper.insert(comment);
        return comment;
    }

    public void deleteComment(Long id, Long userId) {
        commentMapper.delete(id, userId);
    }
}
