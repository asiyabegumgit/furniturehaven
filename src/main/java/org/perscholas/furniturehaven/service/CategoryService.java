package org.perscholas.furniturehaven.service;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.Category;
import org.perscholas.furniturehaven.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        log.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        log.info("Retrieved {} categories", categories.size());
        return categories;
    }

    public Optional<Category> findById(Long id) {
        log.info("Fetching category with ID {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            log.info("Category found: {}", category.get());
        } else {
            log.warn("Category with ID {} not found", id);
        }
        return category;
    }

    public Category save(Category category) {
        log.info("Saving category: {}", category);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category saved with ID {}", savedCategory.getCategoryId());
        return savedCategory;
    }

    public void deleteById(Long id) {
        log.info("Deleting category with ID {}", id);
        categoryRepository.deleteById(id);
        log.info("Category with ID {} deleted", id);
    }
}