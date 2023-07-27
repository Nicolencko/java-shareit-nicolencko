package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    @Query("select c from Comment as c where c.item in ?1")
    List<Comment> findByItemIn(List<Item> items);

    List<Comment> findByItem_IdOrderByCreatedDesc(long id);

    List<Comment> findAllByItemIdInOrderByCreatedDesc(List<Long> items);
}
