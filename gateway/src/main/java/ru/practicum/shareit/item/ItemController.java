package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable("itemId") @Positive long itemId,
                                          @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size,
                                                 @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        return itemClient.getAllByUserId(from, size, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllByText(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size,
                                               @RequestParam(name = "text") String text) {
        return itemClient.getAllByText(from, size, text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        return itemClient.create(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") @Positive long itemId,
                                             @RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") @Positive long itemId,
                                         @RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        return itemClient.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable("itemId") @Positive long itemId,
                           @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        itemClient.deleteById(itemId, userId);
    }

    @DeleteMapping
    public void deleteAll() {
        itemClient.deleteAll();
    }
}