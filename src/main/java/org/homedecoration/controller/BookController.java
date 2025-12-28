package org.homedecoration.controller;

import org.homedecoration.entity.Book;
import org.homedecoration.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping("/findAll")
    public List<Book> findAll() {
        return bookService.findAll();
    }
}
